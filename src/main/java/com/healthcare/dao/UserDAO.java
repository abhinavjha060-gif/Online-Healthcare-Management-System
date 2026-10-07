package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;
import com.healthcare.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * DAO (Data Access Object) for the `users` and `patients` tables.
 */
public class UserDAO {

    /**
     * Checks email + password + role against the database.
     *
     * @return the logged-in User if the login is valid,
     *         or null if email/password/role is wrong or the account is inactive.
     * @throws SQLException if the database cannot be reached
     */
    public User login(String email, String password, String role) throws SQLException {

        String sql = "SELECT user_id, full_name, email, role FROM users "
                + "WHERE email = ? AND password_hash = SHA2(?, 256) "
                + "AND role = ? AND status = 'Active'";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, password);
                ps.setString(3, role);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                                rs.getInt("user_id"),
                                rs.getString("full_name"),
                                rs.getString("email"),
                                rs.getString("role"));
                    }
                }
            }
        }
        return null;
    }

    /**
     * Registers a new patient. Inserts one row in `users` and one row in `patients`
     * inside a single TRANSACTION: either both rows are saved, or neither.
     *
     * @return true if registered, false if the email already exists
     * @throws SQLException for any other database problem
     */
    public boolean registerPatient(String fullName, String email, String phone,
                                   String password, LocalDate dob, String gender,
                                   String bloodGroup, String address,
                                   String emergencyPhone) throws SQLException {

        String userSql = "INSERT INTO users (full_name, email, password_hash, phone, role) "
                + "VALUES (?, ?, SHA2(?, 256), ?, 'Patient')";

        String patientSql = "INSERT INTO patients (patient_id, date_of_birth, gender, "
                + "blood_group, address, emergency_phone) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            con.setAutoCommit(false);   // start transaction

            try {
                int userId;

                try (PreparedStatement ps =
                             con.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setString(3, password);
                    ps.setString(4, phone);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        userId = keys.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(patientSql)) {
                    ps.setInt(1, userId);

                    if (dob != null) {
                        ps.setDate(2, java.sql.Date.valueOf(dob));
                    } else {
                        ps.setNull(2, java.sql.Types.DATE);
                    }

                    ps.setString(3, gender);
                    ps.setString(4, bloodGroup);
                    ps.setString(5, (address == null || address.isEmpty()) ? null : address);
                    ps.setString(6, (emergencyPhone == null || emergencyPhone.isEmpty())
                            ? null : emergencyPhone);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();

                if (e.getErrorCode() == 1062) {   // duplicate entry (email already exists)
                    return false;
                }
                throw e;
            }
        }
    }

    /**
     * Changes the password of a logged-in user after checking the current one.
     *
     * @return true if the password was changed, false if the current password is wrong
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword)
            throws SQLException {

        String sql = "UPDATE users SET password_hash = SHA2(?, 256) "
                + "WHERE user_id = ? AND password_hash = SHA2(?, 256)";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, newPassword);
                ps.setInt(2, userId);
                ps.setString(3, currentPassword);
                return ps.executeUpdate() == 1;
            }
        }
    }

    /**
     * "Forgot password": the account is identified by e-mail AND the phone number
     * given at registration. (A real system would send an e-mail link instead.)
     *
     * @return true if the password was changed
     */
    public boolean resetPassword(String email, String phone, String newPassword)
            throws SQLException {

        String sql = "UPDATE users SET password_hash = SHA2(?, 256) "
                + "WHERE email = ? AND phone = ? AND status = 'Active'";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, newPassword);
                ps.setString(2, email);
                ps.setString(3, phone);
                return ps.executeUpdate() == 1;
            }
        }
    }
}
