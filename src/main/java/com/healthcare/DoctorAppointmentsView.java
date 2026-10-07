package com.healthcare;

import com.healthcare.dao.DoctorAppointmentDAO;
import com.healthcare.model.PatientAppointment;
import com.healthcare.model.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;

public class DoctorAppointmentsView {

    private final DoctorAppointmentDAO dao = new DoctorAppointmentDAO();

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("My Appointments");

        title.setFont(
                Font.font("Arial", FontWeight.BOLD, 26)
        );

        title.setTextFill(
                Color.web("#1E3A5F")
        );

        Label subtitle = new Label(
                "View and manage your patient appointments."
        );

        subtitle.setTextFill(
                Color.web("#607D8B")
        );

        // =========================
        // TABLE
        // =========================

        TableView<PatientAppointment> table = new TableView<>();

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        TableColumn<PatientAppointment, String> patientColumn =
                new TableColumn<>("Patient");

        patientColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getPatientName()
                )
        );

        TableColumn<PatientAppointment, String> dateColumn =
                new TableColumn<>("Date");

        dateColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getDateDisplay()
                )
        );

        TableColumn<PatientAppointment, String> timeColumn =
                new TableColumn<>("Time");

        timeColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getTimeDisplay()
                )
        );

        TableColumn<PatientAppointment, String> reasonColumn =
                new TableColumn<>("Reason");

        reasonColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getReason()
                )
        );

        TableColumn<PatientAppointment, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getStatus()
                )
        );

        table.getColumns().addAll(
                patientColumn,
                dateColumn,
                timeColumn,
                reasonColumn,
                statusColumn
        );

        table.setPrefHeight(350);

        // =========================
        // BUTTONS + MESSAGE
        // =========================

        Button confirmButton =
                new Button("Confirm Appointment");

        Button completeButton =
                new Button("Complete Visit");

        Button cancelButton =
                new Button("Cancel Appointment");

        Button backButton =
                new Button("← Back to Dashboard");

        Label message = new Label();

        message.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        // =========================
        // LOAD DATA FROM DATABASE
        // =========================

        Runnable reload = () -> {

            try {
                table.setItems(
                        FXCollections.observableArrayList(
                                dao.getForDoctor(UserSession.getUserId())
                        )
                );

            } catch (SQLException e) {
                e.printStackTrace();
                showMessage(message, "Database error: " + e.getMessage(), false);
            }
        };

        reload.run();

        // =========================
        // CONFIRM ACTION
        // =========================

        confirmButton.setOnAction(event -> {

            PatientAppointment selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showMessage(message, "Please select an appointment.", false);
                return;
            }

            try {
                boolean done = dao.confirm(
                        selected.getAppointmentId(),
                        UserSession.getUserId()
                );

                if (done) {
                    showMessage(message,
                            "Appointment confirmed for "
                                    + selected.getPatientName() + ".", true);
                } else {
                    showMessage(message,
                            "Only Pending appointments can be confirmed.", false);
                }

                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showMessage(message, "Database error: " + e.getMessage(), false);
            }
        });

        // =========================
        // COMPLETE ACTION
        // =========================

        completeButton.setOnAction(event -> {

            PatientAppointment selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showMessage(message, "Please select an appointment.", false);
                return;
            }

            if (!selected.getStatus().equals("Confirmed")) {
                showMessage(message,
                        "Confirm the appointment first, then complete it.", false);
                return;
            }

            // small form: diagnosis + prescription
            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setTitle("Complete Visit");
            dialog.setHeaderText(
                    "Medical record for " + selected.getPatientName()
            );

            TextField diagnosisField = new TextField();
            diagnosisField.setPromptText("Diagnosis (required)");

            TextArea prescriptionArea = new TextArea();
            prescriptionArea.setPromptText("Prescription / advice");
            prescriptionArea.setPrefRowCount(4);
            prescriptionArea.setWrapText(true);

            VBox form = new VBox(10);
            form.setPadding(new Insets(10));
            form.getChildren().addAll(
                    new Label("Diagnosis"),
                    diagnosisField,
                    new Label("Prescription"),
                    prescriptionArea
            );

            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL
            );

            dialog.showAndWait().ifPresent(response -> {

                if (response != ButtonType.OK) {
                    return;
                }

                String diagnosis = diagnosisField.getText().trim();

                if (diagnosis.isEmpty()) {
                    showMessage(message, "Diagnosis is required.", false);
                    return;
                }

                try {
                    boolean done = dao.complete(
                            selected.getAppointmentId(),
                            UserSession.getUserId(),
                            selected.getPatientId(),
                            diagnosis,
                            prescriptionArea.getText().trim()
                    );

                    if (done) {
                        showMessage(message,
                                "Visit completed and medical record saved.", true);
                    } else {
                        showMessage(message,
                                "Only Confirmed appointments can be completed.", false);
                    }

                    reload.run();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showMessage(message, "Database error: " + e.getMessage(), false);
                }
            });
        });

        // =========================
        // CANCEL ACTION
        // =========================

        cancelButton.setOnAction(event -> {

            PatientAppointment selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showMessage(message, "Please select an appointment.", false);
                return;
            }

            try {
                boolean done = dao.cancel(
                        selected.getAppointmentId(),
                        UserSession.getUserId()
                );

                if (done) {
                    showMessage(message,
                            "Appointment cancelled.", true);
                } else {
                    showMessage(message,
                            "Only Pending or Confirmed appointments can be cancelled.",
                            false);
                }

                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showMessage(message, "Database error: " + e.getMessage(), false);
            }
        });

        // =========================
        // BACK ACTION
        // =========================

        backButton.setOnAction(event -> {

            DoctorDashboard dashboard =
                    new DoctorDashboard();

            dashboard.show(stage);
        });

        // =========================
        // BUTTON AREA
        // =========================

        HBox buttons = new HBox(15);

        buttons.setAlignment(Pos.CENTER_LEFT);

        buttons.getChildren().addAll(
                confirmButton,
                completeButton,
                cancelButton,
                backButton
        );

        // =========================
        // CARD
        // =========================

        VBox card = new VBox(20);

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
                buttons,
                message
        );

        // =========================
        // ROOT
        // =========================

        StackPane root = new StackPane();

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
                new Scene(root, 1000, 700);

        stage.setTitle(
                "Doctor Appointments - Online Healthcare"
        );

        stage.setScene(scene);

        stage.show();
    }

    // green text for success, red for errors
    private void showMessage(Label label, String text, boolean success) {

        label.setText(text);

        label.setTextFill(
                success ? Color.web("#2E7D32") : Color.RED
        );
    }
}
