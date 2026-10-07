package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/** Queries for the patient's home screen (counters + next appointment). */
public class PatientDashboardDAO {

    /** The patient's next Pending/Confirmed appointment, with the doctor's rating. */
    public static class NextAppointment {
        public final int appointmentId;
        public final String doctorName;
        public final String specialization;
        public final LocalDate date;
        public final LocalTime time;
        public final String status;
        public final int reviewCount;
        public final double averageRating;

        public NextAppointment(int appointmentId, String doctorName, String specialization,
                               LocalDate date, LocalTime time, String status,
                               int reviewCount, double averageRating) {
            this.appointmentId = appointmentId;
            this.doctorName = doctorName;
            this.specialization = specialization;
            this.date = date;
            this.time = time;
            this.status = status;
            this.reviewCount = reviewCount;
            this.averageRating = averageRating;
        }
    }

    /** @return {total appointments, appointments booked this month, medical records} */
    public int[] getCounts(int patientId) throws SQLException {

        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM appointments WHERE patient_id = ?), "
                + "(SELECT COUNT(*) FROM appointments WHERE patient_id = ? "
                + "   AND YEAR(appointment_date) = YEAR(CURDATE()) "
                + "   AND MONTH(appointment_date) = MONTH(CURDATE())), "
                + "(SELECT COUNT(*) FROM medical_records WHERE patient_id = ?)";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, patientId);
            ps.setInt(3, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3)};
            }
        }
    }

    /** @return the next upcoming appointment, or null when there is none */
    public NextAppointment getNext(int patientId) throws SQLException {

        String sql = "SELECT a.appointment_id, u.full_name, d.specialization, "
                + "a.appointment_date, a.appointment_time, a.status, "
                + "(SELECT COUNT(*) FROM feedback f WHERE f.doctor_id = d.doctor_id) AS reviews, "
                + "(SELECT COALESCE(AVG(f.rating), 0) FROM feedback f "
                + "   WHERE f.doctor_id = d.doctor_id) AS avg_rating "
                + "FROM appointments a "
                + "JOIN doctors d ON d.doctor_id = a.doctor_id "
                + "JOIN users u ON u.user_id = d.doctor_id "
                + "WHERE a.patient_id = ? AND a.status IN ('Pending','Confirmed') "
                + "AND TIMESTAMP(a.appointment_date, a.appointment_time) >= NOW() "
                + "ORDER BY a.appointment_date, a.appointment_time LIMIT 1";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new NextAppointment(
                        rs.getInt("appointment_id"),
                        rs.getString("full_name"),
                        rs.getString("specialization"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("status"),
                        rs.getInt("reviews"),
                        rs.getDouble("avg_rating"));
            }
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
