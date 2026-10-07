package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

/** DAO for the patient's own profile (`users` + `patients` tables). */
public class PatientProfileDAO {

    /** The profile fields that exist in the database. */
    public static class Profile {
        public final int patientId;
        public final String fullName;
        public final String email;
        public final String phone;
        public final LocalDate dateOfBirth;   // may be null
        public final String gender;           // may be null
        public final String bloodGroup;       // may be null
        public final String address;
        public final String emergencyPhone;
        public final LocalDate memberSince;   // may be null

        public Profile(int patientId, String fullName, String email, String phone,
                       LocalDate dateOfBirth, String gender, String bloodGroup,
                       String address, String emergencyPhone, LocalDate memberSince) {
            this.patientId = patientId;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone == null ? "" : phone;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.bloodGroup = bloodGroup;
            this.address = address == null ? "" : address;
            this.emergencyPhone = emergencyPhone == null ? "" : emergencyPhone;
            this.memberSince = memberSince;
        }
    }

    public enum Result { SAVED, EMAIL_EXISTS, NOT_FOUND }

    /** @return the profile, or null if there is no such patient */
    public Profile load(int patientId) throws SQLException {

        String sql = "SELECT u.user_id, u.full_name, u.email, u.phone, u.created_at, "
                + "p.date_of_birth, p.gender, p.blood_group, p.address, p.emergency_phone "
                + "FROM users u LEFT JOIN patients p ON p.patient_id = u.user_id "
                + "WHERE u.user_id = ?";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                java.sql.Date dob = rs.getDate("date_of_birth");
                java.sql.Timestamp created = rs.getTimestamp("created_at");

                return new Profile(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        dob == null ? null : dob.toLocalDate(),
                        rs.getString("gender"),
                        rs.getString("blood_group"),
                        rs.getString("address"),
                        rs.getString("emergency_phone"),
                        created == null ? null : created.toLocalDateTime().toLocalDate());
            }
        }
    }

    /** Updates `users` and `patients` together (one transaction). */
    public Result save(int patientId, String fullName, String email, String phone,
                       LocalDate dob, String gender, String bloodGroup,
                       String address, String emergencyPhone) throws SQLException {

        String userSql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";

        // INSERT ... ON DUPLICATE KEY keeps working for admin-created patients
        // that have no row in `patients` yet.
        String patientSql = "INSERT INTO patients "
                + "(patient_id, date_of_birth, gender, blood_group, address, emergency_phone) "
                + "VALUES (?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE date_of_birth = VALUES(date_of_birth), "
                + "gender = VALUES(gender), blood_group = VALUES(blood_group), "
                + "address = VALUES(address), emergency_phone = VALUES(emergency_phone)";

        try (Connection con = open()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(userSql)) {
                    ps.setString(1, fullName);
                    ps.setString(2, email);
                    ps.setString(3, phone);
                    ps.setInt(4, patientId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(patientSql)) {
                    ps.setInt(1, patientId);

                    if (dob != null) {
                        ps.setDate(2, java.sql.Date.valueOf(dob));
                    } else {
                        ps.setNull(2, Types.DATE);
                    }
                    ps.setString(3, blank(gender));
                    ps.setString(4, blank(bloodGroup));
                    ps.setString(5, blank(address));
                    ps.setString(6, blank(emergencyPhone));
                    ps.executeUpdate();
                }

                con.commit();
                return Result.SAVED;

            } catch (SQLException e) {
                con.rollback();

                if (e.getErrorCode() == 1062) {      // duplicate e-mail
                    return Result.EMAIL_EXISTS;
                }
                throw e;
            }
        }
    }

    /**
     * Visits with the diagnosis the doctor wrote (medical_records LEFT JOIN appointments).
     * Each row = {date dd/MM/yyyy, doctor, reason, diagnosis, prescription}.
     */
    public java.util.List<String[]> getConsultations(int patientId) throws SQLException {

        String sql = "SELECT m.record_date, u.full_name, a.reason, m.diagnosis, m.prescription "
                + "FROM medical_records m "
                + "JOIN users u ON u.user_id = m.doctor_id "
                + "LEFT JOIN appointments a ON a.appointment_id = m.appointment_id "
                + "WHERE m.patient_id = ? "
                + "ORDER BY m.record_date DESC, m.record_id DESC";

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
                .ofPattern("dd/MM/yyyy", java.util.Locale.ENGLISH);

        java.util.List<String[]> rows = new java.util.ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new String[]{
                            rs.getDate("record_date").toLocalDate().format(fmt),
                            rs.getString("full_name"),
                            nz(rs.getString("reason")),
                            nz(rs.getString("diagnosis")),
                            nz(rs.getString("prescription"))});
                }
            }
        }
        return rows;
    }

    private static String nz(String s) {
        return s == null || s.isEmpty() ? "-" : s;
    }

    private static String blank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private Connection open() throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            throw new SQLException("Could not connect to the database.");
        }
        return con;
    }
}
