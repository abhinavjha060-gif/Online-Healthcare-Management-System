package com.healthcare.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens connections to the MySQL database.
 *
 * <p>The settings are read from (highest priority first):
 * <ol>
 *   <li>environment variables  DB_URL, DB_USER, DB_PASSWORD</li>
 *   <li>JVM options            -Ddb.url=... -Ddb.user=... -Ddb.password=...</li>
 *   <li>a file called <code>db.properties</code> next to where the app is started
 *       (project folder) - handy to override without rebuilding</li>
 *   <li><code>src/main/resources/db.properties</code> (on the classpath)</li>
 *   <li>built-in defaults (localhost, healthcare_db, root, empty password)</li>
 * </ol>
 * Because the classpath file is found by every IDE, the app behaves the same in
 * IntelliJ IDEA, VS Code and Eclipse.
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL  = "jdbc:mysql://localhost:3306/healthcare_db";
    private static final String DEFAULT_USER = "root";

    private static final Properties FILE_CONFIG = loadFileConfig();

    private static volatile String lastError = null;

    private DatabaseConnection() { }

    /**
     * @return an open connection, or null if the database cannot be reached
     *         (the reason is available from {@link #getLastError()}).
     */
    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(
                    setting("DB_URL", "db.url", DEFAULT_URL),
                    setting("DB_USER", "db.user", DEFAULT_USER),
                    setting("DB_PASSWORD", "db.password", ""));
            lastError = null;
            return con;
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.err.println("[DB] Could not connect: " + e.getMessage());
            return null;
        }
    }

    /** Quick check used at start-up. */
    public static boolean isAvailable() {
        try (Connection con = getConnection()) {
            return con != null;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Human readable reason of the last failed connection attempt (or null). */
    public static String getLastError() {
        return lastError;
    }

    /** The JDBC URL in use (never contains the password) - for messages. */
    public static String getUrl() {
        return setting("DB_URL", "db.url", DEFAULT_URL);
    }

    // ------------------------------------------------------------------

    private static String setting(String envName, String propName, String fallback) {
        String value = System.getenv(envName);
        if (value == null || value.isEmpty()) {
            value = System.getProperty(propName);
        }
        if (value == null) {
            value = FILE_CONFIG.getProperty(propName);
        }
        return value == null ? fallback : value;
    }

    private static Properties loadFileConfig() {
        Properties props = new Properties();

        // 1) bundled with the app (works in every IDE)
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("[DB] Could not read bundled db.properties: " + e.getMessage());
        }

        // 2) optional override file in the working directory
        File external = new File("db.properties");
        if (external.isFile()) {
            try (InputStream in = new java.io.FileInputStream(external)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("[DB] Could not read db.properties: " + e.getMessage());
            }
        }
        return props;
    }
}
