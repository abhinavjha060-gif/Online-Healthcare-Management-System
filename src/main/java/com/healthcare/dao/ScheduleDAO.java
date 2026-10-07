package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** DAO for `doctor_schedules` - the doctor's weekly working hours. */
public class ScheduleDAO {

    /** One working block, e.g. Monday 09:00-17:00. */
    public static class Slot {
        public final String day;
        public final LocalTime start;
        public final LocalTime end;

        public Slot(String day, LocalTime start, LocalTime end) {
            this.day = day;
            this.start = start;
            this.end = end;
        }
    }

    /** The doctor's whole week, in Monday..Sunday order. */
    public List<Slot> getForDoctor(int doctorId) throws SQLException {

        String sql = "SELECT day_of_week, start_time, end_time FROM doctor_schedules "
                + "WHERE doctor_id = ? "
                + "ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday',"
                + "'Thursday','Friday','Saturday','Sunday'), start_time";

        List<Slot> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Slot(
                            rs.getString("day_of_week"),
                            rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time").toLocalTime()));
                }
            }
        }
        return list;
    }

    /**
     * Sets the working hours of ONE day: old rows of that day are removed and the
     * new block is inserted - in a single transaction.
     */
    public void saveDay(int doctorId, String day, LocalTime start, LocalTime end)
            throws SQLException {

        try (Connection con = open()) {
            con.setAutoCommit(false);

            try {
                deleteDay(con, doctorId, day);

                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time) "
                                + "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, doctorId);
                    ps.setString(2, day);
                    ps.setTime(3, java.sql.Time.valueOf(start));
                    ps.setTime(4, java.sql.Time.valueOf(end));
                    ps.executeUpdate();
                }
                con.commit();

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** "Not available" on that day: removes the day's working hours. */
    public void clearDay(int doctorId, String day) throws SQLException {
        try (Connection con = open()) {
            deleteDay(con, doctorId, day);
        }
    }

    private void deleteDay(Connection con, int doctorId, String day) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM doctor_schedules WHERE doctor_id = ? AND day_of_week = ?")) {
            ps.setInt(1, doctorId);
            ps.setString(2, day);
            ps.executeUpdate();
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
