package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;
import com.healthcare.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** DAO for doctors: reads from the `doctors` and `users` tables (a JOIN). */
public class DoctorDAO {

    public List<Doctor> getAllDoctors() throws SQLException {

        String sql = "SELECT d.doctor_id, u.full_name, d.specialization "
                + "FROM doctors d "
                + "JOIN users u ON u.user_id = d.doctor_id "
                + "WHERE u.status = 'Active' "
                + "ORDER BY u.full_name";

        List<Doctor> doctors = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    doctors.add(new Doctor(
                            rs.getInt("doctor_id"),
                            rs.getString("full_name"),
                            rs.getString("specialization")));
                }
            }
        }
        return doctors;
    }

    /** @return the doctor's speciality (empty text if the doctor has no row yet) */
    public String getSpecialization(int doctorId) throws SQLException {

        String sql = "SELECT specialization FROM doctors WHERE doctor_id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getString(1) : "";
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // "Available for consultation" switch (doctor dashboard)
    // The table is created automatically the first time it is needed, so an
    // existing database keeps working without running any extra SQL.
    // ------------------------------------------------------------------

    private void ensureAvailabilityTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS doctor_availability ("
                    + "doctor_id INT PRIMARY KEY, "
                    + "available BOOLEAN NOT NULL DEFAULT TRUE)");
        }
    }

    // @return false only when the doctor has switched "available for consultation" off */
    
    public boolean isAvailable(int doctorId) throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }
            ensureAvailabilityTable(con);

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT available FROM doctor_availability WHERE doctor_id = ?")) {
                ps.setInt(1, doctorId);

                try (ResultSet rs = ps.executeQuery()) {
                    return !rs.next() || rs.getBoolean(1);   // no row = available
                }
            }
        }
    }

    public void setAvailable(int doctorId, boolean available) throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }
            ensureAvailabilityTable(con);

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO doctor_availability (doctor_id, available) VALUES (?, ?) "
                            + "ON DUPLICATE KEY UPDATE available = VALUES(available)")) {
                ps.setInt(1, doctorId);
                ps.setBoolean(2, available);
                ps.executeUpdate();
            }
        }
    }
}
