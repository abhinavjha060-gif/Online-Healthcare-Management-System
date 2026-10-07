package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO for the `system_settings` key/value table (Admin -> System Settings).
 * The table is created automatically the first time it is needed, so an existing team database keeps working without running any extra SQL.
 */
public class SettingsDAO {

    public static final String SYSTEM_NAME          = "system_name";
    public static final String SUPPORT_EMAIL        = "support_email";
    public static final String TIMEZONE             = "timezone";
    public static final String BOOKING_ENABLED      = "booking_enabled";
    public static final String CANCELLATION_ENABLED = "cancellation_enabled";
    public static final String SLOT_DURATION        = "slot_duration";
    public static final String EMAIL_NOTIFICATIONS  = "email_notifications";
    public static final String APPOINTMENT_ALERTS   = "appointment_alerts";

    /** Values used when a key is missing from the table. */
    public static Map<String, String> defaults() {
        Map<String, String> d = new LinkedHashMap<>();
        d.put(SYSTEM_NAME, "Online Healthcare Management System");
        d.put(SUPPORT_EMAIL, "support@healthcare.com");
        d.put(TIMEZONE, "India Standard Time (IST)");
        d.put(BOOKING_ENABLED, "true");
        d.put(CANCELLATION_ENABLED, "true");
        d.put(SLOT_DURATION, "30 minutes");
        d.put(EMAIL_NOTIFICATIONS, "true");
        d.put(APPOINTMENT_ALERTS, "true");
        return d;
    }

    /** All settings (defaults filled in for anything not stored yet). */
    public Map<String, String> loadAll() throws SQLException {

        Map<String, String> result = defaults();

        try (Connection con = open()) {
            ensureTable(con);

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT setting_key, setting_value FROM system_settings");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    result.put(rs.getString(1), rs.getString(2));
                }
            }
        }
        return result;
    }

    /** Saves every given key (insert or update) in one transaction. */
    public void saveAll(Map<String, String> values) throws SQLException {

        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";

        try (Connection con = open()) {
            ensureTable(con);
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Map.Entry<String, String> e : values.entrySet()) {
                    ps.setString(1, e.getKey());
                    ps.setString(2, e.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
                con.commit();

            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private void ensureTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS system_settings ("
                    + "setting_key VARCHAR(60) PRIMARY KEY, "
                    + "setting_value VARCHAR(255) NOT NULL)");
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
