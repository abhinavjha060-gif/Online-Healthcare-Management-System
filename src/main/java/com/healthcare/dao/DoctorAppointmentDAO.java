package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;
import com.healthcare.model.PatientAppointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO for the doctor's side of appointments. */
public class DoctorAppointmentDAO {

    /** All appointments of one doctor (JOIN with users to get the patient's name). */
    public List<PatientAppointment> getForDoctor(int doctorId) throws SQLException {

        String sql = "SELECT a.appointment_id, a.patient_id, u.full_name, "
                + "a.appointment_date, a.appointment_time, a.reason, a.status "
                + "FROM appointments a "
                + "JOIN users u ON u.user_id = a.patient_id "
                + "WHERE a.doctor_id = ? "
                + "ORDER BY a.appointment_date, a.appointment_time";

        List<PatientAppointment> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, doctorId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new PatientAppointment(
                                rs.getInt("appointment_id"),
                                rs.getInt("patient_id"),
                                rs.getString("full_name"),
                                rs.getDate("appointment_date").toLocalDate(),
                                rs.getTime("appointment_time").toLocalTime(),
                                rs.getString("reason"),
                                rs.getString("status")));
                    }
                }
            }
        }
        return list;
    }

    /** Pending -> Confirmed. Returns true if one row changed. */
    public boolean confirm(int appointmentId, int doctorId) throws SQLException {
        return changeStatus(appointmentId, doctorId, "Confirmed", "'Pending'");
    }

    /** Pending/Confirmed -> Cancelled. Returns true if one row changed. */
    public boolean cancel(int appointmentId, int doctorId) throws SQLException {
        return changeStatus(appointmentId, doctorId, "Cancelled", "'Pending','Confirmed'");
    }

    private boolean changeStatus(int appointmentId, int doctorId,
                                 String newStatus, String allowedFrom) throws SQLException {

        String sql = "UPDATE appointments SET status = ? "
                + "WHERE appointment_id = ? AND doctor_id = ? "
                + "AND status IN (" + allowedFrom + ")";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, appointmentId);
                ps.setInt(3, doctorId);
                return ps.executeUpdate() == 1;
            }
        }
    }

    /**
     * Confirmed -> Completed AND saves a medical record, in ONE transaction. If either step fails, nothing is saved.
     */
    public boolean complete(int appointmentId, int doctorId, int patientId,
                            String diagnosis, String prescription) throws SQLException {

        String updateSql = "UPDATE appointments SET status = 'Completed' "
                + "WHERE appointment_id = ? AND doctor_id = ? "
                + "AND status = 'Confirmed'";

        String insertSql = "INSERT INTO medical_records "
                + "(patient_id, doctor_id, appointment_id, diagnosis, "
                + "prescription, record_date) "
                + "VALUES (?, ?, ?, ?, ?, CURDATE())";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            con.setAutoCommit(false);

            try {
                int changed;

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setInt(1, appointmentId);
                    ps.setInt(2, doctorId);
                    changed = ps.executeUpdate();
                }

                if (changed != 1) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                    ps.setInt(3, appointmentId);
                    ps.setString(4, diagnosis);
                    ps.setString(5, prescription);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}
