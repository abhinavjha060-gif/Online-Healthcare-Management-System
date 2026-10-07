package com.healthcare.service;

import com.healthcare.dao.SettingsDAO;

import java.sql.SQLException;
import java.util.Map;

/**
 * Business-rule layer for the admin's system settings.
 * Screens ask this class ("is booking allowed?") instead of touching SQL.
 * Values are cached and refreshed after the admin saves.
 */
public final class SettingsService {

    private static final SettingsDAO DAO = new SettingsDAO();
    private static Map<String, String> cache;

    private SettingsService() { }

    private static synchronized Map<String, String> settings() {
        if (cache == null) {
            try {
                cache = DAO.loadAll();
            } catch (SQLException e) {
                // database not reachable: behave with the defaults, do not block the app
                System.err.println("[Settings] using defaults: " + e.getMessage());
                return SettingsDAO.defaults();
            }
        }
        return cache;
    }

    /** Re-reads the table on the next access. */
    public static synchronized void reload() {
        cache = null;
    }

    public static synchronized void save(Map<String, String> values) throws SQLException {
        DAO.saveAll(values);
        cache = null;
    }

    public static String get(String key) {
        return settings().getOrDefault(key, SettingsDAO.defaults().get(key));
    }

    public static boolean isEnabled(String key) {
        return "true".equalsIgnoreCase(get(key));
    }

    public static boolean isBookingEnabled()      { return isEnabled(SettingsDAO.BOOKING_ENABLED); }
    public static boolean isCancellationEnabled() { return isEnabled(SettingsDAO.CANCELLATION_ENABLED); }
    public static String  getSlotDuration()       { return get(SettingsDAO.SLOT_DURATION); }
}
