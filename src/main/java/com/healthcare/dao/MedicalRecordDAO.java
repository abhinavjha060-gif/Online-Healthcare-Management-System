package com.healthcare.dao;

import com.healthcare.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** DAO for the `medical_records` table (patient history and doctor's patient list). */
public class MedicalRecordDAO {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

    /** One medical record. */
    public static class RecordItem {
        public final String date;
        public final String doctorName;
        public final String diagnosis;
        public final String prescription;
        public final String notes;

        public RecordItem(String date, String doctorName, String diagnosis,
                          String prescription, String notes) {
            this.date = date;
            this.doctorName = doctorName;
            this.diagnosis = diagnosis;
            this.prescription = prescription == null ? "" : prescription;
            this.notes = notes == null ? "" : notes;
        }
    }

    /** One row of the doctor's patient list. */
    public static class PatientSummary {
        public final int patientId;
        public final String name;
        public final String age;
        public final String lastDiagnosis;

        public PatientSummary(int patientId, String name, String age, String lastDiagnosis) {
            this.patientId = patientId;
            this.name = name;
            this.age = age;
            this.lastDiagnosis = lastDiagnosis;
        }
    }

    /** One record of a doctor, with its id so it can be edited. */
    public static class EditableRecord {
        public final int recordId;
        public final String date;
        public final String diagnosis;
        public final String prescription;
        public final String notes;

        public EditableRecord(int recordId, String date, String diagnosis,
                              String prescription, String notes) {
            this.recordId = recordId;
            this.date = date;
            this.diagnosis = diagnosis == null ? "" : diagnosis;
            this.prescription = prescription == null ? "" : prescription;
            this.notes = notes == null ? "" : notes;
        }

        @Override
        public String toString() {
            return date + " - " + diagnosis;
        }
    }

    /** Records this doctor wrote for this patient (newest first), for editing. */
    public List<EditableRecord> getEditableRecords(int doctorId, int patientId)
            throws SQLException {

        String sql = "SELECT record_id, record_date, diagnosis, prescription, notes "
                + "FROM medical_records WHERE patient_id = ? AND doctor_id = ? "
                + "ORDER BY record_date DESC, record_id DESC";

        List<EditableRecord> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EditableRecord(
                            rs.getInt("record_id"),
                            rs.getDate("record_date").toLocalDate().format(DATE_FORMAT),
                            rs.getString("diagnosis"),
                            rs.getString("prescription"),
                            rs.getString("notes")));
                }
            }
        }
        return list;
    }

    /** Doctor writes a new record for one of his patients. */
    public void addRecord(int doctorId, int patientId, String diagnosis,
                          String prescription, String notes) throws SQLException {

        String sql = "INSERT INTO medical_records "
                + "(patient_id, doctor_id, diagnosis, prescription, notes, record_date) "
                + "VALUES (?, ?, ?, ?, ?, CURDATE())";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, diagnosis);
            ps.setString(4, prescription);
            ps.setString(5, notes);
            ps.executeUpdate();
        }
    }

    /** Doctor updates one of his own records. */
    public boolean updateRecord(int recordId, int doctorId, String diagnosis,
                                String prescription, String notes) throws SQLException {

        String sql = "UPDATE medical_records SET diagnosis = ?, prescription = ?, notes = ? "
                + "WHERE record_id = ? AND doctor_id = ?";

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, diagnosis);
            ps.setString(2, prescription);
            ps.setString(3, notes);
            ps.setInt(4, recordId);
            ps.setInt(5, doctorId);
            return ps.executeUpdate() == 1;
        }
    }

    /** Medical history of one patient (JOIN with users to get the doctor's name). */
    public List<RecordItem> getForPatient(int patientId) throws SQLException {

        String sql = "SELECT m.record_date, u.full_name, m.diagnosis, m.prescription, m.notes "
                + "FROM medical_records m "
                + "JOIN users u ON u.user_id = m.doctor_id "
                + "WHERE m.patient_id = ? "
                + "ORDER BY m.record_date DESC, m.record_id DESC";

        return queryRecords(sql, patientId, -1);
    }

    /** Records one doctor has written for one patient. */
    public List<RecordItem> getForDoctorAndPatient(int doctorId, int patientId)
            throws SQLException {

        String sql = "SELECT m.record_date, u.full_name, m.diagnosis, m.prescription, m.notes "
                + "FROM medical_records m "
                + "JOIN users u ON u.user_id = m.doctor_id "
                + "WHERE m.patient_id = ? AND m.doctor_id = ? "
                + "ORDER BY m.record_date DESC, m.record_id DESC";

        return queryRecords(sql, patientId, doctorId);
    }

    /**
     * Patients who have an appointment with this doctor, with age and
     * the doctor's latest diagnosis for them (subquery).
     */
    public List<PatientSummary> getPatientsOfDoctor(int doctorId) throws SQLException {

        String sql = "SELECT p.patient_id, u.full_name, "
                + "TIMESTAMPDIFF(YEAR, p.date_of_birth, CURDATE()) AS age, "
                + "(SELECT m.diagnosis FROM medical_records m "
                + "  WHERE m.patient_id = p.patient_id AND m.doctor_id = ? "
                + "  ORDER BY m.record_date DESC, m.record_id DESC LIMIT 1) AS last_diagnosis "
                + "FROM patients p "
                + "JOIN users u ON u.user_id = p.patient_id "
                + "WHERE p.patient_id IN "
                + "  (SELECT patient_id FROM appointments WHERE doctor_id = ?) "
                + "ORDER BY u.full_name";

        List<PatientSummary> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setInt(2, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int age = rs.getInt("age");
                    boolean noAge = rs.wasNull();
                    String diagnosis = rs.getString("last_diagnosis");

                    list.add(new PatientSummary(
                            rs.getInt("patient_id"),
                            rs.getString("full_name"),
                            noAge ? "-" : String.valueOf(age),
                            diagnosis == null ? "-" : diagnosis));
                }
            }
        }
        return list;
    }

    private List<RecordItem> queryRecords(String sql, int patientId, int doctorId)
            throws SQLException {

        List<RecordItem> list = new ArrayList<>();

        try (Connection con = open();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            if (doctorId >= 0) {
                ps.setInt(2, doctorId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new RecordItem(
                            rs.getDate("record_date").toLocalDate().format(DATE_FORMAT),
                            rs.getString("full_name"),
                            rs.getString("diagnosis"),
                            rs.getString("prescription"),
                            rs.getString("notes")));
                }
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
