package com.healthcare;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DoctorPatientRecordsView {

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("Patient Records");

        title.setFont(
                Font.font("Arial", FontWeight.BOLD, 26)
        );

        title.setTextFill(
                Color.web("#1E3A5F")
        );

        Label subtitle = new Label(
                "View patient information and medical records."
        );

        subtitle.setTextFill(
                Color.web("#607D8B")
        );

        // =========================
        // TABLE
        // =========================

        TableView<PatientRecord> table =
                new TableView<>();

        TableColumn<PatientRecord, String> nameColumn =
                new TableColumn<>("Patient Name");

        nameColumn.setCellValueFactory(
                data -> data.getValue().nameProperty()
        );

        TableColumn<PatientRecord, String> idColumn =
                new TableColumn<>("Patient ID");

        idColumn.setCellValueFactory(
                data -> data.getValue().idProperty()
        );

        TableColumn<PatientRecord, String> ageColumn =
                new TableColumn<>("Age");

        ageColumn.setCellValueFactory(
                data -> data.getValue().ageProperty()
        );

        TableColumn<PatientRecord, String> diagnosisColumn =
                new TableColumn<>("Diagnosis");

        diagnosisColumn.setCellValueFactory(
                data -> data.getValue().diagnosisProperty()
        );

        table.getColumns().addAll(
                nameColumn,
                idColumn,
                ageColumn,
                diagnosisColumn
        );

        // =========================
        // LOAD PATIENTS FROM DATABASE
        // =========================

        try {
            for (com.healthcare.dao.MedicalRecordDAO.PatientSummary p :
                    new com.healthcare.dao.MedicalRecordDAO()
                            .getPatientsOfDoctor(
                                    com.healthcare.model.UserSession.getUserId())) {

                table.getItems().add(
                        new PatientRecord(
                                p.name,
                                String.format("P%04d", p.patientId),
                                p.age,
                                p.lastDiagnosis
                        )
                );
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        table.setPrefHeight(350);

        // =========================
        // RECORD DETAILS
        // =========================

        Label detailsLabel =
                new Label("Patient Details");

        detailsLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 16)
        );

        TextArea detailsArea =
                new TextArea();

        detailsArea.setPromptText(
                "Select a patient to view medical details..."
        );

        detailsArea.setPrefRowCount(5);

        detailsArea.setWrapText(true);

        // =========================
        // VIEW BUTTON
        // =========================

        Button viewButton =
                new Button("View Patient Details");

        viewButton.setPrefHeight(40);

        viewButton.setStyle(
                "-fx-background-color: #1976D2;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        viewButton.setOnAction(event -> {

            PatientRecord selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                detailsArea.setText(
                        "Please select a patient first."
                );

            } else {

                detailsArea.setText(buildDetails(selected));
            }
        });

        // =========================
        // ADD / UPDATE RECORD
        // =========================

        Button editButton =
                new Button("Add / Update Record");

        editButton.setPrefHeight(40);

        editButton.setOnAction(event -> {
            PatientRecord selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                detailsArea.setText("Please select a patient first.");
                return;
            }

            if (editRecordDialog(selected)) {
                detailsArea.setText(buildDetails(selected));
            }
        });

        // =========================
        // BACK BUTTON
        // =========================

        Button backButton =
                new Button("← Back to Dashboard");

        backButton.setOnAction(event -> {

            DoctorDashboard dashboard =
                    new DoctorDashboard();

            dashboard.show(stage);
        });

        // =========================
        // BUTTON AREA
        // =========================

        HBox buttons =
                new HBox(15);

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );

        buttons.getChildren().addAll(
                viewButton,
                editButton,
                backButton
        );

        // =========================
        // CARD
        // =========================

        VBox card =
                new VBox(20);

        card.setPadding(
                new Insets(30)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 12px;"
        );

        card.getChildren().addAll(
                title,
                subtitle,
                table,
                detailsLabel,
                detailsArea,
                buttons
        );

        // =========================
        // ROOT
        // =========================

        StackPane root =
                new StackPane();

        root.setPadding(
                new Insets(30)
        );

        root.setStyle(
                "-fx-background-color: #F7FAFC;"
        );

        root.getChildren().add(card);

        // =========================
        // SCENE
        // =========================

        Scene scene =
                new Scene(root, 1000, 750);

        stage.setTitle(
                "Patient Records - Online Healthcare"
        );

        stage.setScene(scene);

        stage.show();
    }

    /**
     * Dialog where the doctor writes a new record or updates one of his own.
     *
     * @return true if something was saved
     */
    private boolean editRecordDialog(PatientRecord patient) {

        com.healthcare.dao.MedicalRecordDAO dao =
                new com.healthcare.dao.MedicalRecordDAO();

        int doctorId = com.healthcare.model.UserSession.getUserId();
        int patientId;

        java.util.List<com.healthcare.dao.MedicalRecordDAO.EditableRecord> existing;

        try {
            patientId = Integer.parseInt(patient.getId().substring(1));
            existing = dao.getEditableRecords(doctorId, patientId);
        } catch (java.sql.SQLException | NumberFormatException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load records: " + e.getMessage()).showAndWait();
            return false;
        }

        final String NEW_RECORD = "+ New record";

        ComboBox<Object> recordBox = new ComboBox<>();
        recordBox.getItems().add(NEW_RECORD);
        recordBox.getItems().addAll(existing);
        recordBox.setValue(NEW_RECORD);

        TextField diagnosisField = new TextField();
        diagnosisField.setPromptText("Diagnosis (required)");

        TextArea prescriptionArea = new TextArea();
        prescriptionArea.setPromptText("Prescription");
        prescriptionArea.setPrefRowCount(3);

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes / medical advice");
        notesArea.setPrefRowCount(3);

        recordBox.setOnAction(e -> {
            Object v = recordBox.getValue();
            if (v instanceof com.healthcare.dao.MedicalRecordDAO.EditableRecord r) {
                diagnosisField.setText(r.diagnosis);
                prescriptionArea.setText(r.prescription);
                notesArea.setText(r.notes);
            } else {
                diagnosisField.clear();
                prescriptionArea.clear();
                notesArea.clear();
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.addRow(0, new Label("Record"), recordBox);
        grid.addRow(1, new Label("Diagnosis"), diagnosisField);
        grid.addRow(2, new Label("Prescription"), prescriptionArea);
        grid.addRow(3, new Label("Notes"), notesArea);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Patient Record");
        dialog.setHeaderText(patient.getName() + " (" + patient.getId() + ")");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return false;
        }

        String diagnosis = diagnosisField.getText().trim();

        if (diagnosis.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Diagnosis is required.").showAndWait();
            return false;
        }

        try {
            Object v = recordBox.getValue();

            if (v instanceof com.healthcare.dao.MedicalRecordDAO.EditableRecord r) {
                dao.updateRecord(r.recordId, doctorId, diagnosis,
                        prescriptionArea.getText().trim(), notesArea.getText().trim());
            } else {
                dao.addRecord(doctorId, patientId, diagnosis,
                        prescriptionArea.getText().trim(), notesArea.getText().trim());
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Medical record saved successfully!").showAndWait();
            return true;

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error: " + e.getMessage()).showAndWait();
            return false;
        }
    }

    // =====================================================
    // PATIENT RECORD MODEL
    // builds the details text from the database
    private String buildDetails(PatientRecord selected) {

        StringBuilder text = new StringBuilder();

        text.append("Patient Name: ").append(selected.getName())
                .append("\nPatient ID: ").append(selected.getId())
                .append("\nAge: ").append(selected.getAge())
                .append("\n\nMedical Records:");

        try {
            int patientId =
                    Integer.parseInt(selected.getId().substring(1));

            java.util.List<com.healthcare.dao.MedicalRecordDAO.RecordItem> records =
                    new com.healthcare.dao.MedicalRecordDAO()
                            .getForDoctorAndPatient(
                                    com.healthcare.model.UserSession.getUserId(),
                                    patientId);

            if (records.isEmpty()) {
                text.append("\nNo medical record written yet.");
            }

            for (com.healthcare.dao.MedicalRecordDAO.RecordItem r : records) {
                text.append("\n\n").append(r.date)
                        .append(" - ").append(r.diagnosis);

                if (!r.prescription.isEmpty()) {
                    text.append("\nPrescription: ").append(r.prescription);
                }

                if (!r.notes.isEmpty()) {
                    text.append("\nNotes: ").append(r.notes);
                }
            }

        } catch (java.sql.SQLException | NumberFormatException e) {
            e.printStackTrace();
            text.append("\nCould not load records: ").append(e.getMessage());
        }

        return text.toString();
    }

    // =====================================================

    public static class PatientRecord {

        private final StringProperty name;
        private final StringProperty id;
        private final StringProperty age;
        private final StringProperty diagnosis;

        public PatientRecord(String name, String id, String age, String diagnosis) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.age = new SimpleStringProperty(age);
            this.diagnosis = new SimpleStringProperty(diagnosis);
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty idProperty() {
            return id;
        }

        public StringProperty ageProperty() {
            return age;
        }

        public StringProperty diagnosisProperty() {
            return diagnosis;
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAge() {
            return age.get();
        }

        public String getDiagnosis() {
            return diagnosis.get();
        }
    }
}