package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NotificationDAO {

    /** One notification line. */
    public static class Item {
        public final String key;          // stable id, used to remember "read"
        public final String message;
        public final LocalDateTime when;  // used for sorting (newest first)
        public boolean read;

        Item(String key, String message, LocalDateTime when) {
            this.key = key;
            this.message = message;
            this.when = when;
        }
    }

    private static final DateTimeFormatter DAY =
            DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter CLOCK =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    /** All notifications of the patient, newest first, with the read flag filled in. */
    public List<Item> getForPatient(int patientId) throws SQLException {

        List<Item> list = new ArrayList<>();
        LocalDate today = LocalDate.now();

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }
            ensureTable(con);

            // ---------- appointments ----------
            String sql = "SELECT a.appointment_id, a.appointment_date, a.appointment_time, "
                    + "a.status, a.created_at, u.full_name, "
                    + "(SELECT COUNT(*) FROM feedback f WHERE f.appointment_id = a.appointment_id) AS rated "
                    + "FROM appointments a JOIN users u ON u.user_id = a.doctor_id "
                    + "WHERE a.patient_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, patientId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {

                        int id = rs.getInt("appointment_id");
                        LocalDate date = rs.getDate("appointment_date").toLocalDate();
                        LocalTime time = rs.getTime("appointment_time").toLocalTime();
                        String status = rs.getString("status");
                        String doctor = rs.getString("full_name");
                        LocalDateTime visit = LocalDateTime.of(date, time);
                        String slot = date.format(DAY) + " at " + time.format(CLOCK);

                        switch (status) {

                            case "Pending" -> {
                                if (!date.isBefore(today)) {
                                    list.add(new Item("pending-" + id,
                                            "Your appointment request with " + doctor + " on "
                                                    + slot + " is waiting for confirmation.",
                                            rs.getTimestamp("created_at").toLocalDateTime()));
                                }
                            }
                            case "Confirmed" -> {
                                if (!date.isBefore(today)) {
                                    list.add(new Item("confirmed-" + id,
                                            "Your appointment with " + doctor + " on "
                                                    + slot + " is confirmed.",
                                            rs.getTimestamp("created_at").toLocalDateTime()));
                                }
                            }
                            case "Cancelled" -> {
                                if (!date.isBefore(today.minusDays(14))) {
                                    list.add(new Item("cancelled-" + id,
                                            "Your appointment with " + doctor + " on "
                                                    + slot + " was cancelled.",
                                            visit));
                                }
                            }
                            case "Completed" -> {
                                if (rs.getInt("rated") == 0
                                        && !date.isBefore(today.minusDays(30))) {
                                    list.add(new Item("rate-" + id,
                                            "How was your visit with " + doctor + " on "
                                                    + date.format(DAY)
                                                    + "? You can rate it in My Appointments.",
                                            visit));
                                }
                            }
                            default -> { }
                        }

                        // reminder: today or tomorrow
                        if (("Pending".equals(status) || "Confirmed".equals(status))
                                && (date.equals(today) || date.equals(today.plusDays(1)))) {

                            list.add(new Item("reminder-" + id + "-" + date,
                                    "Reminder: you have an appointment with " + doctor + " "
                                            + (date.equals(today) ? "today" : "tomorrow")
                                            + " at " + time.format(CLOCK) + ".",
                                    LocalDateTime.of(today, LocalTime.MAX)));
                        }
                    }
                }
            }

            // ---------- new medical records ----------
            String recSql = "SELECT r.record_id, r.record_date, r.diagnosis, u.full_name "
                    + "FROM medical_records r JOIN users u ON u.user_id = r.doctor_id "
                    + "WHERE r.patient_id = ? AND r.record_date >= ?";

            try (PreparedStatement ps = con.prepareStatement(recSql)) {
                ps.setInt(1, patientId);
                ps.setDate(2, java.sql.Date.valueOf(today.minusDays(14)));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LocalDate d = rs.getDate("record_date").toLocalDate();
                        list.add(new Item("record-" + rs.getInt("record_id"),
                                rs.getString("full_name") + " added a medical record ("
                                        + rs.getString("diagnosis") + ") on " + d.format(DAY) + ".",
                                d.atStartOfDay()));
                    }
                }
            }

            // ---------- which were already read ----------
            Set<String> read = new HashSet<>();
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT notif_key FROM notification_reads WHERE user_id = ?")) {
                ps.setInt(1, patientId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        read.add(rs.getString(1));
                    }
                }
            }
            for (Item item : list) {
                item.read = read.contains(item.key);
            }
        }

        list.sort(Comparator.comparing((Item i) -> i.when).reversed());
        return list;
    }

    public int countUnread(int patientId) throws SQLException {
        int n = 0;
        for (Item item : getForPatient(patientId)) {
            if (!item.read) {
                n++;
            }
        }
        return n;
    }

    public void markRead(int patientId, List<String> keys) throws SQLException {

        if (keys.isEmpty()) {
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                throw new SQLException("Could not connect to the database.");
            }
            ensureTable(con);

            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT IGNORE INTO notification_reads (user_id, notif_key) VALUES (?, ?)")) {
                for (String key : keys) {
                    ps.setInt(1, patientId);
                    ps.setString(2, key);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    private void ensureTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS notification_reads ("
                    + "user_id INT NOT NULL, "
                    + "notif_key VARCHAR(100) NOT NULL, "
                    + "PRIMARY KEY (user_id, notif_key))");
        }
    }
}
