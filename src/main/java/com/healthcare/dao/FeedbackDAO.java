package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO for the `feedback` table. */
public class FeedbackDAO {

    /** One row of the doctor's feedback table. */
    public static class FeedbackItem {
        public final String patientName;
        public final int rating;
        public final String comments;

        public FeedbackItem(String patientName, int rating, String comments) {
            this.patientName = patientName;
            this.rating = rating;
            this.comments = comments == null ? "" : comments;
        }
    }

    /** Result of trying to save a feedback. */
    public enum Result { SAVED, ALREADY_GIVEN, NOT_ALLOWED }

    /**
     *Saves feedback for a COMPLETED appointment of this patient. The doctor is taken from the appointment itself (INSERT ... SELECT), so a patient cannot rate a different doctor or someone else's visit.
     */
    public Result submit(int appointmentId, int patientId, int rating, String comments)
            throws SQLException {

        String sql = "INSERT INTO feedback (appointment_id, patient_id, doctor_id, rating, comments) "
                + "SELECT appointment_id, patient_id, doctor_id, ?, ? "
                + "FROM appointments "
                + "WHERE appointment_id = ? AND patient_id = ? AND status = 'Completed'";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setString(2, (comments == null || comments.isEmpty()) ? null : comments);
            ps.setInt(3, appointmentId);
            ps.setInt(4, patientId);

            return ps.executeUpdate() == 1 ? Result.SAVED : Result.NOT_ALLOWED;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {          // UNIQUE(appointment_id)
                return Result.ALREADY_GIVEN;
            }
            throw e;
        }
    }

    /** All feedback received by one doctor, newest first. */
    public List<FeedbackItem> getForDoctor(int doctorId) throws SQLException {

        String sql = "SELECT u.full_name, f.rating, f.comments "
                + "FROM feedback f "
                + "JOIN users u ON u.user_id = f.patient_id "
                + "WHERE f.doctor_id = ? "
                + "ORDER BY f.created_at DESC, f.feedback_id DESC";

        List<FeedbackItem> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new FeedbackItem(
                            rs.getString("full_name"),
                            rs.getInt("rating"),
                            rs.getString("comments")));
                }
            }
        }
        return list;
    }

    /** @return {number of reviews, average rating (0 if none)} using COUNT and AVG */
    public double[] getSummary(int doctorId) throws SQLException {

        String sql = "SELECT COUNT(*), COALESCE(AVG(rating), 0) "
                + "FROM feedback WHERE doctor_id = ?";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new double[]{rs.getInt(1), rs.getDouble(2)};
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
