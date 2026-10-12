package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregate queries (COUNT, GROUP BY, AVG) for the Performance Analytics screen. */
public class AnalyticsDAO {

    /** Row of the doctor performance table. */
    public static class DoctorStat {
        public final String doctorName;
        public final String specialization;
        public final int totalAppointments;
        public final int completed;
        public final String avgRating;      // "4.50" or "-" if no feedback yet

        public DoctorStat(String doctorName, String specialization,
                          int totalAppointments, int completed, String avgRating) {
            this.doctorName = doctorName;
            this.specialization = specialization;
            this.totalAppointments = totalAppointments;
            this.completed = completed;
            this.avgRating = avgRating;
        }
    }

    /** @return {total users, total doctors, total patients, total appointments} */
    public int[] getTotals() throws SQLException {

        String sql = "SELECT (SELECT COUNT(*) FROM users), "
                + "(SELECT COUNT(*) FROM doctors), "
                + "(SELECT COUNT(*) FROM patients), "
                + "(SELECT COUNT(*) FROM appointments)";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)};
        }
    }

    /** @return {total users, today's appointments, doctors, patients} for the admin dashboard */
    public int[] getDashboardCounts() throws SQLException {

        String sql = "SELECT (SELECT COUNT(*) FROM users), "
                + "(SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()), "
                + "(SELECT COUNT(*) FROM doctors), "
                + "(SELECT COUNT(*) FROM patients)";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return new int[]{rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)};
        }
    }


    /** Number of appointments per status (GROUP BY). */
    public Map<String, Integer> getStatusCounts() throws SQLException {

        String sql = "SELECT status, COUNT(*) AS total "
                + "FROM appointments GROUP BY status";

        Map<String, Integer> map = new LinkedHashMap<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("total"));
            }
        }
        return map;
    }

    /** Doctor-wise appointments and average rating (from the v_doctor_performance view). */
    public List<DoctorStat> getDoctorStats() throws SQLException {

        String sql = "SELECT doctor_name, specialization, total_appointments, "
                + "completed, avg_rating "
                + "FROM v_doctor_performance "
                + "ORDER BY total_appointments DESC, doctor_name";

        List<DoctorStat> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double rating = rs.getDouble("avg_rating");
                boolean noRating = rs.wasNull();

                list.add(new DoctorStat(
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getInt("total_appointments"),
                        rs.getInt("completed"),
                        noRating ? "-" : String.format("%.2f", rating)));
            }
        }
        return list;
    }

    private Connection open() throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            throw new SQLException("Could not connect to the database.");
        }
        return con;
    }
}
