package com.healthcare;

import com.healthcare.dao.AdminAppointmentDAO;
import com.healthcare.model.AppointmentRow;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AdminAppointmentsView {

    private final AdminAppointmentDAO dao = new AdminAppointmentDAO();

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("Appointment Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label(
                "View and manage all patient appointments."
        );
        subtitle.setStyle("-fx-text-fill: #666666;");

        VBox heading = new VBox(5, title, subtitle);

        // =========================
        // APPOINTMENT TABLE
        // =========================

        TableView<AppointmentRow> table = new TableView<>();

        table.getColumns().add(
                column("Patient", 160, row -> row.getPatientName())
        );
        table.getColumns().add(
                column("Doctor", 160, row -> row.getDoctorName())
        );
        table.getColumns().add(
                column("Date", 120, row -> row.getDateDisplay())
        );
        table.getColumns().add(
                column("Time", 120, row -> row.getTimeDisplay())
        );
        table.getColumns().add(
                column("Status", 140, row -> row.getStatus())
        );

        table.setPrefHeight(350);

        // =========================
        // LOAD FROM DATABASE
        // =========================

        Runnable reload = () -> {

            try {
                table.setItems(
                        FXCollections.observableArrayList(dao.getAll())
                );

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        };

        reload.run();

        // =========================
        // BUTTONS
        // =========================

        Button scheduleButton =
                new Button("Schedule Appointment");

        Button rescheduleButton =
                new Button("Reschedule Appointment");

        Button confirmButton =
                new Button("Confirm Appointment");

        Button cancelButton =
                new Button("Cancel Appointment");

        Button deleteButton =
                new Button("Delete Appointment");

        scheduleButton.setPrefWidth(170);
        rescheduleButton.setPrefWidth(170);
        confirmButton.setPrefWidth(170);
        cancelButton.setPrefWidth(170);
        deleteButton.setPrefWidth(170);

        HBox buttons = new HBox(
                10,
                scheduleButton,
                rescheduleButton,
                confirmButton,
                cancelButton,
                deleteButton
        );

        // =========================
        // SCHEDULE (admin books for a patient)
        // =========================

        scheduleButton.setOnAction(event -> {

            try {
                java.util.List<AdminAppointmentDAO.PatientItem> patients = dao.getPatients();
                java.util.List<com.healthcare.model.Doctor> doctors =
                        new com.healthcare.dao.DoctorDAO().getAllDoctors();

                if (patients.isEmpty() || doctors.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING,
                            "There must be at least one active patient and one active doctor.");
                    return;
                }

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Schedule Appointment");
                dialog.setHeaderText("Book an appointment for a patient");

                ComboBox<String> patientBox = new ComboBox<>();
                java.util.Map<String, Integer> patientIds = new java.util.LinkedHashMap<>();
                for (AdminAppointmentDAO.PatientItem p : patients) {
                    String label = p.name + " (P" + String.format("%04d", p.id) + ")";
                    patientIds.put(label, p.id);
                    patientBox.getItems().add(label);
                }

                ComboBox<String> doctorBox = new ComboBox<>();
                java.util.Map<String, Integer> doctorIds = new java.util.LinkedHashMap<>();
                for (com.healthcare.model.Doctor d : doctors) {
                    doctorIds.put(d.getLabel(), d.getDoctorId());
                    doctorBox.getItems().add(d.getLabel());
                }

                DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
                ComboBox<String> timeBox = new ComboBox<>();
                TextField reasonField = new TextField();
                reasonField.setPromptText("Reason for visit");

                Runnable loadSlots = () -> {
                    timeBox.getItems().clear();
                    if (doctorBox.getValue() == null || datePicker.getValue() == null) {
                        return;
                    }
                    try {
                        for (java.time.LocalTime t :
                                new com.healthcare.dao.AppointmentDAO().getAvailableSlots(
                                        doctorIds.get(doctorBox.getValue()),
                                        datePicker.getValue(), slotMinutes())) {
                            timeBox.getItems().add(formatTime(t));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                };
                doctorBox.setOnAction(e -> loadSlots.run());
                datePicker.setOnAction(e -> loadSlots.run());

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(15));
                grid.addRow(0, new Label("Patient"), patientBox);
                grid.addRow(1, new Label("Doctor"), doctorBox);
                grid.addRow(2, new Label("Date"), datePicker);
                grid.addRow(3, new Label("Time"), timeBox);
                grid.addRow(4, new Label("Reason"), reasonField);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes()
                        .addAll(ButtonType.OK, ButtonType.CANCEL);

                if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }

                if (patientBox.getValue() == null || doctorBox.getValue() == null
                        || datePicker.getValue() == null || timeBox.getValue() == null
                        || reasonField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Please fill in every field.");
                    return;
                }

                String problem = dao.schedule(
                        patientIds.get(patientBox.getValue()),
                        doctorIds.get(doctorBox.getValue()),
                        datePicker.getValue(),
                        parseTime(timeBox.getValue()),
                        reasonField.getText().trim());

                if (problem != null) {
                    showAlert(Alert.AlertType.WARNING, problem);
                } else {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Appointment scheduled successfully!");
                    reload.run();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            }
        });

        // =========================
        // RESCHEDULE
        // =========================

        rescheduleButton.setOnAction(event -> {

            AppointmentRow selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Please select an appointment first.");
                return;
            }

            try {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Reschedule Appointment");
                dialog.setHeaderText(selected.getPatientName() + " with "
                        + selected.getDoctorName() + " (currently "
                        + selected.getDateDisplay() + " " + selected.getTimeDisplay() + ")");

                DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
                ComboBox<String> timeBox = new ComboBox<>();

                Runnable loadSlots = () -> {
                    timeBox.getItems().clear();
                    if (datePicker.getValue() == null) {
                        return;
                    }
                    try {
                        for (java.time.LocalTime t :
                                new com.healthcare.dao.AppointmentDAO().getAvailableSlots(
                                        selected.getDoctorId(),
                                        datePicker.getValue(), slotMinutes())) {
                            timeBox.getItems().add(formatTime(t));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                };
                datePicker.setOnAction(e -> loadSlots.run());
                loadSlots.run();

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(15));
                grid.addRow(0, new Label("New date"), datePicker);
                grid.addRow(1, new Label("New time"), timeBox);

                dialog.getDialogPane().setContent(grid);
                dialog.getDialogPane().getButtonTypes()
                        .addAll(ButtonType.OK, ButtonType.CANCEL);

                if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }

                if (datePicker.getValue() == null || timeBox.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Please choose a new date and time.");
                    return;
                }

                String problem = dao.reschedule(selected.getAppointmentId(),
                        datePicker.getValue(), parseTime(timeBox.getValue()));

                if (problem != null) {
                    showAlert(Alert.AlertType.WARNING, problem);
                } else {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Appointment rescheduled successfully!");
                    reload.run();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
            }
        });

        // =========================
        // CONFIRM
        // =========================

        confirmButton.setOnAction(event -> {

            AppointmentRow selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select an appointment first."
                );

                return;
            }

            try {
                boolean done = dao.confirm(selected.getAppointmentId());

                if (done) {
                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Appointment confirmed successfully!"
                    );
                } else {
                    showAlert(
                            Alert.AlertType.WARNING,
                            "Only Pending appointments can be confirmed."
                    );
                }

                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        });

        // =========================
        // CANCEL
        // =========================

        cancelButton.setOnAction(event -> {

            AppointmentRow selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select an appointment first."
                );

                return;
            }

            try {
                boolean done = dao.cancel(selected.getAppointmentId());

                if (done) {
                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Appointment cancelled successfully!"
                    );
                } else {
                    showAlert(
                            Alert.AlertType.WARNING,
                            "Only Pending or Confirmed appointments can be cancelled."
                    );
                }

                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        });

        // =========================
        // DELETE
        // =========================

        deleteButton.setOnAction(event -> {

            AppointmentRow selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select an appointment first."
                );

                return;
            }

            Alert confirmation =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmation.setTitle("Delete Appointment");
            confirmation.setHeaderText(
                    "Delete selected appointment?"
            );

            confirmation.setContentText(
                    selected.getPatientName()
                            + " - "
                            + selected.getDateDisplay()
            );

            confirmation.showAndWait().ifPresent(response -> {

                if (response != ButtonType.OK) {
                    return;
                }

                try {
                    dao.delete(selected.getAppointmentId());

                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Appointment deleted successfully!"
                    );

                    reload.run();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Database error: " + e.getMessage()
                    );
                }
            });
        });

        // =========================
        // BACK BUTTON
        // =========================

        Button backButton =
                new Button("← Back to Dashboard");

        backButton.setOnAction(event -> {

            AdminDashboard dashboard =
                    new AdminDashboard();

            dashboard.show(stage);
        });

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox(
                20,
                heading,
                table,
                buttons,
                backButton
        );

        content.setPadding(new Insets(30));

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        BorderPane root =
                new BorderPane(scrollPane);

        Scene scene =
                new Scene(root, 1050, 650);

        stage.setTitle(
                "Appointment Management - Online Healthcare"
        );

        stage.setScene(scene);
        stage.show();
    }

    // =========================
    // TIME HELPERS
    // =========================

    private static final java.time.format.DateTimeFormatter TIME_FORMAT =
            java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH);

    private static String formatTime(java.time.LocalTime t) {
        return t.format(TIME_FORMAT);
    }

    private static java.time.LocalTime parseTime(String text) {
        return java.time.LocalTime.parse(text, TIME_FORMAT);
    }

    /** Slot length in minutes from the admin's System Settings (default 30). */
    private static int slotMinutes() {
        try {
            int m = Integer.parseInt(com.healthcare.service.SettingsService
                    .getSlotDuration().replaceAll("\\D", ""));
            return m > 0 ? m : 30;
        } catch (NumberFormatException e) {
            return 30;
        }
    }

    // =========================
    // TABLE COLUMN HELPER
    // =========================

    private TableColumn<AppointmentRow, String> column(
            String heading,
            double width,
            java.util.function.Function<AppointmentRow, String> getter
    ) {

        TableColumn<AppointmentRow, String> column =
                new TableColumn<>(heading);

        column.setPrefWidth(width);

        column.setCellValueFactory(
                data -> new SimpleStringProperty(
                        getter.apply(data.getValue())
                )
        );

        return column;
    }

    // =========================
    // ALERT METHOD
    // =========================

    private void showAlert(
            Alert.AlertType type,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle("Appointment Management");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
