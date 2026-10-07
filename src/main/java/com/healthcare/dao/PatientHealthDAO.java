package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO for the patient's own health notes (conditions, allergies, medicines ...) that are edited on the "My Health Profile" screen.
 * They are stored as key/value rows in `patient_health_info`. Like `system_settings`, the table is created automatically when first needed, so an existing database keeps working without running schema.sql again.
 */
public class PatientHealthDAO {

    public static final String CONDITIONS         = "conditions";
    public static final String PREVIOUS_DISEASES  = "previous_diseases";
    public static final String SURGERIES          = "surgeries";
    public static final String FAMILY_HISTORY     = "family_history";
    public static final String MEDICINE_ALLERGIES = "medicine_allergies";
    public static final String FOOD_ALLERGIES     = "food_allergies";
    public static final String OTHER_ALLERGIES    = "other_allergies";
    public static final String MEDICATIONS        = "medications";
    public static final String VACCINATIONS       = "vaccinations";
    public static final String TEST_RESULTS       = "test_results";
    public static final String MEDICAL_NOTES      = "medical_notes";

    /** All saved notes of one patient (key -> text). Missing keys are simply absent. */
    public Map<String, String> load(int patientId) throws SQLException {

        Map<String, String> result = new HashMap<>();

        try (Connection con = open()) {
            ensureTable(con);

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT field_key, field_value FROM patient_health_info "
                            + "WHERE patient_id = ?")) {
                ps.setInt(1, patientId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.put(rs.getString(1), rs.getString(2));
                    }
                }
            }
        }
        return result;
    }

    /** Saves every given key (insert or update) in one transaction. */
    public void save(int patientId, Map<String, String> values) throws SQLException {

        String sql = "INSERT INTO patient_health_info (patient_id, field_key, field_value) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE field_value = VALUES(field_value)";

        try (Connection con = open()) {
            ensureTable(con);
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Map.Entry<String, String> e : values.entrySet()) {
                    ps.setInt(1, patientId);
                    ps.setString(2, e.getKey());
                    ps.setString(3, e.getValue() == null ? "" : e.getValue());
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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS patient_health_info ("
                    + "patient_id INT NOT NULL, "
                    + "field_key VARCHAR(40) NOT NULL, "
                    + "field_value TEXT, "
                    + "PRIMARY KEY (patient_id, field_key), "
                    + "CONSTRAINT fk_health_patient FOREIGN KEY (patient_id) "
                    + "REFERENCES patients(patient_id) ON DELETE CASCADE) ENGINE=InnoDB");
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
