package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;
import com.healthcare.model.AppointmentRecord;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** DAO for appointments. */
public class AppointmentDAO {

    /**
     * Checks the doctor's weekly schedule (`doctor_schedules` table).
     * @return null if the doctor works at that day/time, otherwise a message telling the patient when the doctor is available.
     */
    public String checkAvailability(int doctorId, LocalDate date, LocalTime time)
            throws SQLException {

        String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String checkSql = "SELECT COUNT(*) FROM doctor_schedules "
                + "WHERE doctor_id = ? AND day_of_week = ? "
                + "AND ? >= start_time AND ? < end_time";

        String listSql = "SELECT day_of_week, start_time, end_time FROM doctor_schedules "
                + "WHERE doctor_id = ? "
                + "ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday',"
                + "'Thursday','Friday','Saturday','Sunday'), start_time";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setInt(1, doctorId);
                ps.setString(2, day);
                ps.setTime(3, java.sql.Time.valueOf(time));
                ps.setTime(4, java.sql.Time.valueOf(time));

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        return null;          // doctor is available
                    }
                }
            }

            // not available -> build a helpful message
            StringBuilder msg = new StringBuilder(
                    "The doctor is not available on " + day + " at that time.");

            try (PreparedStatement ps = con.prepareStatement(listSql)) {
                ps.setInt(1, doctorId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        msg.append(first ? "\n\nAvailable: " : ", ");
                        first = false;
                        msg.append(rs.getString("day_of_week")).append(" ")
                                .append(rs.getString("start_time"), 0, 5).append("-")
                                .append(rs.getString("end_time"), 0, 5);
                    }
                }
            }
            return msg.toString();
        }
    }

    /**
     * Free time slots of a doctor on one date. Built from the doctor's weekly working hours (`doctor_schedules`) cut into pieces of the admin's "slot duration" setting; slots that are already booked (Pending/Confirmed) or already in the past (for today) are left out.
     */
    public List<LocalTime> getAvailableSlots(int doctorId, LocalDate date, int slotMinutes)
            throws SQLException {

        String day = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        String blocksSql = "SELECT start_time, end_time FROM doctor_schedules "
                + "WHERE doctor_id = ? AND day_of_week = ? ORDER BY start_time";

        String bookedSql = "SELECT appointment_time FROM appointments "
                + "WHERE doctor_id = ? AND appointment_date = ? "
                + "AND status IN ('Pending','Confirmed')";

        List<LocalTime> slots = new ArrayList<>();
        java.util.Set<LocalTime> booked = new java.util.HashSet<>();

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(bookedSql)) {
                ps.setInt(1, doctorId);
                ps.setDate(2, java.sql.Date.valueOf(date));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        booked.add(rs.getTime(1).toLocalTime());
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(blocksSql)) {
                ps.setInt(1, doctorId);
                ps.setString(2, day);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LocalTime start = rs.getTime(1).toLocalTime();
                        LocalTime end = rs.getTime(2).toLocalTime();
                        for (LocalTime t = start;
                             !t.plusMinutes(slotMinutes).isAfter(end);
                             t = t.plusMinutes(slotMinutes)) {
                            slots.add(t);
                            if (t.plusMinutes(slotMinutes).isBefore(t)) {
                                break;      // passed midnight
                            }
                        }
                    }
                }
            }
        }

        boolean today = date.equals(LocalDate.now());
        LocalTime now = LocalTime.now();
        slots.removeIf(t -> booked.contains(t) || (today && !t.isAfter(now)));
        return slots;
    }

    /**
     * Books an appointment by calling the stored procedure `book_appointment` (which uses a transaction so two patients cannot take the same slot).
      @return "BOOKED", "SLOT ALREADY BOOKED" or an error text
     */
    public String book(int patientId, int doctorId, LocalDate date,
                       LocalTime time, String reason) throws SQLException {

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (CallableStatement cs =
                         con.prepareCall("{CALL book_appointment(?, ?, ?, ?, ?, ?)}")) {

                cs.setInt(1, patientId);
                cs.setInt(2, doctorId);
                cs.setDate(3, java.sql.Date.valueOf(date));
                cs.setTime(4, java.sql.Time.valueOf(time));
                cs.setString(5, reason);
                cs.registerOutParameter(6, Types.VARCHAR);

                cs.execute();
                return cs.getString(6);
            }
        }
    }

    /**
     * All appointments of one patient, newest first. Uses a JOIN of appointments + doctors + users to get the doctor's name.
     */
    public List<AppointmentRecord> getPatientAppointments(int patientId) throws SQLException {

        String sql = "SELECT a.appointment_id, u.full_name, d.specialization, "
                + "a.appointment_date, a.appointment_time, a.status, a.reason, "
                + "(f.feedback_id IS NOT NULL) AS rated "
                + "FROM appointments a "
                + "JOIN doctors d ON d.doctor_id = a.doctor_id "
                + "JOIN users u   ON u.user_id   = d.doctor_id "
                + "LEFT JOIN feedback f ON f.appointment_id = a.appointment_id "
                + "WHERE a.patient_id = ? "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC";

        List<AppointmentRecord> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, patientId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new AppointmentRecord(
                                rs.getInt("appointment_id"),
                                rs.getString("full_name"),
                                rs.getString("specialization"),
                                rs.getDate("appointment_date").toLocalDate(),
                                rs.getTime("appointment_time").toLocalTime(),
                                rs.getString("status"),
                                rs.getString("reason"),
                                rs.getBoolean("rated")));
                    }
                }
            }
        }
        return list;
    }

    /**
     * Cancels an appointment (only the patient's own, and only if it is still Pending or Confirmed).
     * @return true if one row was updated
     */
    public boolean cancel(int appointmentId, int patientId) throws SQLException {

        String sql = "UPDATE appointments SET status = 'Cancelled' "
                + "WHERE appointment_id = ? AND patient_id = ? "
                + "AND status IN ('Pending', 'Confirmed')";

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, appointmentId);
                ps.setInt(2, patientId);
                return ps.executeUpdate() == 1;
            }
        }
    }
}
