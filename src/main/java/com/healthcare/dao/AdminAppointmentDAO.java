package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;
import com.healthcare.model.AppointmentRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO for the admin's Appointment Management screen. */
public class AdminAppointmentDAO {

    /** Every appointment, with patient and doctor names (JOIN of 3 tables). */
    public List<AppointmentRow> getAll() throws SQLException {

        String sql = "SELECT a.appointment_id, a.doctor_id, up.full_name AS patient_name, "
                + "ud.full_name AS doctor_name, a.appointment_date, "
                + "a.appointment_time, a.status "
                + "FROM appointments a "
                + "JOIN users up ON up.user_id = a.patient_id "
                + "JOIN users ud ON ud.user_id = a.doctor_id "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        List<AppointmentRow> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new AppointmentRow(
                        rs.getInt("appointment_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("status")));
            }
        }
        return list;
    }

    /** One patient, as shown in the "Schedule appointment" drop-down. */
    public static class PatientItem {
        public final int id;
        public final String name;

        public PatientItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /** All active patients (for the admin's "Schedule Appointment" form). */
    public List<PatientItem> getPatients() throws SQLException {

        String sql = "SELECT p.patient_id, u.full_name FROM patients p "
                + "JOIN users u ON u.user_id = p.patient_id "
                + "WHERE u.status = 'Active' ORDER BY u.full_name";

        List<PatientItem> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PatientItem(rs.getInt(1), rs.getString(2)));
            }
        }
        return list;
    }

    /**
     * Admin schedules an appointment for a patient. It uses the same rules as the patient's own booking (doctor working hours + slot not taken).
     * @return null when booked, otherwise a message to show to the admin
     */
    public String schedule(int patientId, int doctorId, java.time.LocalDate date,
                           java.time.LocalTime time, String reason) throws SQLException {

        if (date.isBefore(java.time.LocalDate.now())) {
            return "You cannot schedule an appointment in the past.";
        }

        AppointmentDAO appointments = new AppointmentDAO();

        String notAvailable = appointments.checkAvailability(doctorId, date, time);
        if (notAvailable != null) {
            return notAvailable;
        }

        String result = appointments.book(patientId, doctorId, date, time, reason);

        if ("BOOKED".equals(result)) {
            return null;
        }
        return "SLOT ALREADY BOOKED".equals(result)
                ? "That slot is already booked. Please choose another time."
                : "Could not schedule the appointment. Please try again.";
    }

    /**
     * Moves a Pending/Confirmed appointment to a new date and time (same doctor).
     * @return null when moved, otherwise a message to show to the admin
     */
    public String reschedule(int appointmentId, java.time.LocalDate date,
                             java.time.LocalTime time) throws SQLException {

        if (date.isBefore(java.time.LocalDate.now())) {
            return "You cannot move an appointment to a past date.";
        }

        String findSql = "SELECT doctor_id FROM appointments "
                + "WHERE appointment_id = ? AND status IN ('Pending','Confirmed')";

        String clashSql = "SELECT COUNT(*) FROM appointments "
                + "WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? "
                + "AND status IN ('Pending','Confirmed') AND appointment_id <> ? FOR UPDATE";

        String updateSql = "UPDATE appointments SET appointment_date = ?, appointment_time = ? "
                + "WHERE appointment_id = ?";

        try (Connection con = open()) {

            int doctorId;
            try (PreparedStatement ps = con.prepareStatement(findSql)) {
                ps.setInt(1, appointmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "Only Pending or Confirmed appointments can be rescheduled.";
                    }
                    doctorId = rs.getInt(1);
                }
            }

            String notAvailable =
                    new AppointmentDAO().checkAvailability(doctorId, date, time);
            if (notAvailable != null) {
                return notAvailable;
            }

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(clashSql)) {
                    ps.setInt(1, doctorId);
                    ps.setDate(2, java.sql.Date.valueOf(date));
                    ps.setTime(3, java.sql.Time.valueOf(time));
                    ps.setInt(4, appointmentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            con.rollback();
                            return "That slot is already booked. Please choose another time.";
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setDate(1, java.sql.Date.valueOf(date));
                    ps.setTime(2, java.sql.Time.valueOf(time));
                    ps.setInt(3, appointmentId);
                    ps.executeUpdate();
                }

                con.commit();
                return null;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Pending -> Confirmed. */
    public boolean confirm(int appointmentId) throws SQLException {
        return update("UPDATE appointments SET status = 'Confirmed' "
                + "WHERE appointment_id = ? AND status = 'Pending'", appointmentId);
    }

    /** Pending/Confirmed -> Cancelled. */
    public boolean cancel(int appointmentId) throws SQLException {
        return update("UPDATE appointments SET status = 'Cancelled' "
                        + "WHERE appointment_id = ? AND status IN ('Pending', 'Confirmed')",
                appointmentId);
    }

    /** Removes the appointment row completely. */
    public boolean delete(int appointmentId) throws SQLException {
        return update("DELETE FROM appointments WHERE appointment_id = ?", appointmentId);
    }

    private boolean update(String sql, int appointmentId) throws SQLException {

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);
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
