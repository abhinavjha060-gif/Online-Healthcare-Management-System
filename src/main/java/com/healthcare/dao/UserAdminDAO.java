package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** DAO for the admin's User Management screen. */
public class UserAdminDAO {

    /** Every new user created by the admin gets this password (SHA-256 hashed in the DB). */
    public static final String DEFAULT_PASSWORD = "password123";

    /** One row of the user table. */
    public static class UserRow {
        public final int id;
        public final String name;
        public final String email;
        public final String role;
        public final String status;

        public UserRow(int id, String name, String email, String role, String status) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.status = status;
        }
    }

    public enum Result { OK, EMAIL_EXISTS, NOT_FOUND }

    public List<UserRow> getAll() throws SQLException {

        String sql = "SELECT user_id, full_name, email, role, status "
                + "FROM users ORDER BY user_id";

        List<UserRow> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new UserRow(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("status")));
            }
        }
        return list;
    }

    /**
     * Adds a user. One row goes into `users`, and (for Doctor / Patient)
     * one row into `doctors` / `patients` - all inside ONE transaction.
     *
     * @param role  "Patient", "Doctor" or "Admin"
     * @param specialization only used for doctors
     */
    public Result addUser(String name, String email, String role, String specialization)
            throws SQLException {

        String userSql = "INSERT INTO users (full_name, email, password_hash, role) "
                + "VALUES (?, ?, SHA2(?, 256), ?)";

        try (Connection con = open()) {

            con.setAutoCommit(false);

            try {
                int userId;

                try (PreparedStatement ps =
                             con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, DEFAULT_PASSWORD);
                    ps.setString(4, role);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        userId = keys.getInt(1);
                    }
                }

                if (role.equals("Doctor")) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO doctors (doctor_id, specialization, license_no) "
                                    + "VALUES (?, ?, ?)")) {
                        ps.setInt(1, userId);
                        ps.setString(2, specialization);
                        ps.setString(3, "LIC-" + userId);
                        ps.executeUpdate();
                    }

                } else if (role.equals("Patient")) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO patients (patient_id) VALUES (?)")) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return Result.OK;

            } catch (SQLException e) {
                con.rollback();

                if (e.getErrorCode() == 1062) {
                    return Result.EMAIL_EXISTS;
                }
                throw e;
            }
        }
    }

    /** Changes name and email (the role of an existing user is not changed). */
    public Result updateUser(int userId, String name, String email) throws SQLException {

        String sql = "UPDATE users SET full_name = ?, email = ? WHERE user_id = ?";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, userId);

            // an unchanged row reports 0 changed rows, so check existence separately
            ps.executeUpdate();
            return Result.OK;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return Result.EMAIL_EXISTS;
            }
            throw e;
        }
    }

    /** Active <-> Inactive. An Inactive user cannot log in. */
    public boolean setStatus(int userId, String status) throws SQLException {

        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Deletes the user. ON DELETE CASCADE also removes the doctor/patient row
     * and their appointments, records and feedback.
     */
    public boolean delete(int userId) throws SQLException {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        }
    }

    private Connection open() throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            throw new SQLException("Could not connect to the database.");
        }
        return con;
    }
}
