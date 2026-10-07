package com.healthcare;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.layout.*;
        import javafx.stage.Stage;

public class SystemSettingsView {

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("System Settings");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label(
                "Configure basic settings for the healthcare management system."
        );
        subtitle.setStyle("-fx-text-fill: #666666;");

        VBox heading = new VBox(5, title, subtitle);

        // =========================
        // GENERAL SETTINGS
        // =========================

        Label generalTitle = new Label("General Settings");
        generalTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label systemNameLabel = new Label("System Name");

        TextField systemNameField = new TextField(
                com.healthcare.service.SettingsService.get(
                        com.healthcare.dao.SettingsDAO.SYSTEM_NAME)
        );

        Label supportEmailLabel = new Label("Support Email");

        TextField supportEmailField =
                new TextField(
                        com.healthcare.service.SettingsService.get(
                                com.healthcare.dao.SettingsDAO.SUPPORT_EMAIL));

        Label timezoneLabel = new Label("Timezone");

        ComboBox<String> timezoneBox = new ComboBox<>();

        timezoneBox.getItems().addAll(
                "India Standard Time (IST)",
                "UTC",
                "Eastern Time",
                "Pacific Time"
        );

        String savedZone = com.healthcare.service.SettingsService.get(
                com.healthcare.dao.SettingsDAO.TIMEZONE);
        if (!timezoneBox.getItems().contains(savedZone)) {
            timezoneBox.getItems().add(savedZone);
        }
        timezoneBox.setValue(savedZone);

        timezoneBox.setPrefWidth(300);

        GridPane generalGrid = new GridPane();

        generalGrid.setHgap(15);
        generalGrid.setVgap(15);

        generalGrid.add(systemNameLabel, 0, 0);
        generalGrid.add(systemNameField, 1, 0);

        generalGrid.add(supportEmailLabel, 0, 1);
        generalGrid.add(supportEmailField, 1, 1);

        generalGrid.add(timezoneLabel, 0, 2);
        generalGrid.add(timezoneBox, 1, 2);

        // =========================
        // APPOINTMENT SETTINGS
        // =========================

        Label appointmentTitle =
                new Label("Appointment Settings");

        appointmentTitle.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold;"
        );

        CheckBox appointmentBooking =
                new CheckBox(
                        "Allow patients to book appointments"
                );

        appointmentBooking.setSelected(com.healthcare.service.SettingsService
                .isEnabled(com.healthcare.dao.SettingsDAO.BOOKING_ENABLED));

        CheckBox cancellation =
                new CheckBox(
                        "Allow patients to cancel appointments"
                );

        cancellation.setSelected(com.healthcare.service.SettingsService
                .isEnabled(com.healthcare.dao.SettingsDAO.CANCELLATION_ENABLED));

        Label durationLabel =
                new Label("Default Appointment Duration");

        ComboBox<String> durationBox =
                new ComboBox<>();

        durationBox.getItems().addAll(
                "15 minutes",
                "30 minutes",
                "45 minutes",
                "60 minutes"
        );

        String savedDuration = com.healthcare.service.SettingsService.getSlotDuration();
        if (!durationBox.getItems().contains(savedDuration)) {
            durationBox.getItems().add(savedDuration);
        }
        durationBox.setValue(savedDuration);

        VBox appointmentSettings =
                new VBox(
                        12,
                        appointmentBooking,
                        cancellation,
                        durationLabel,
                        durationBox
                );

        // =========================
        // NOTIFICATION SETTINGS
        // =========================

        Label notificationTitle =
                new Label("Notification Settings");

        notificationTitle.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold;"
        );

        CheckBox emailNotification =
                new CheckBox(
                        "Enable email notifications"
                );

        emailNotification.setSelected(com.healthcare.service.SettingsService
                .isEnabled(com.healthcare.dao.SettingsDAO.EMAIL_NOTIFICATIONS));

        CheckBox appointmentNotification =
                new CheckBox(
                        "Send appointment reminders"
                );

        appointmentNotification.setSelected(com.healthcare.service.SettingsService
                .isEnabled(com.healthcare.dao.SettingsDAO.APPOINTMENT_ALERTS));

        VBox notificationSettings =
                new VBox(
                        12,
                        emailNotification,
                        appointmentNotification
                );

        // =========================
        // SAVE BUTTON
        // =========================

        Button saveButton =
                new Button("Save Settings");

        saveButton.setPrefWidth(150);

        saveButton.setOnAction(event -> {

            if (systemNameField.getText().isEmpty()
                    || supportEmailField.getText().isEmpty()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please fill all required fields."
                );

                return;
            }

            if (!supportEmailField
                    .getText()
                    .contains("@")) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please enter a valid support email."
                );

                return;
            }

            java.util.Map<String, String> values = new java.util.LinkedHashMap<>();
            values.put(com.healthcare.dao.SettingsDAO.SYSTEM_NAME,
                    systemNameField.getText().trim());
            values.put(com.healthcare.dao.SettingsDAO.SUPPORT_EMAIL,
                    supportEmailField.getText().trim());
            values.put(com.healthcare.dao.SettingsDAO.TIMEZONE, timezoneBox.getValue());
            values.put(com.healthcare.dao.SettingsDAO.BOOKING_ENABLED,
                    String.valueOf(appointmentBooking.isSelected()));
            values.put(com.healthcare.dao.SettingsDAO.CANCELLATION_ENABLED,
                    String.valueOf(cancellation.isSelected()));
            values.put(com.healthcare.dao.SettingsDAO.SLOT_DURATION, durationBox.getValue());
            values.put(com.healthcare.dao.SettingsDAO.EMAIL_NOTIFICATIONS,
                    String.valueOf(emailNotification.isSelected()));
            values.put(com.healthcare.dao.SettingsDAO.APPOINTMENT_ALERTS,
                    String.valueOf(appointmentNotification.isSelected()));

            try {
                com.healthcare.service.SettingsService.save(values);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "System settings saved successfully!"
                );

            } catch (java.sql.SQLException e) {
                e.printStackTrace();

                showAlert(
                        Alert.AlertType.ERROR,
                        "Could not save settings: " + e.getMessage()
                );
            }
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
        // CONTENT
        // =========================

        VBox content = new VBox(
                20,
                heading,
                generalTitle,
                generalGrid,
                appointmentTitle,
                appointmentSettings,
                notificationTitle,
                notificationSettings,
                saveButton,
                backButton
        );

        content.setPadding(new Insets(30));

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        BorderPane root =
                new BorderPane(scrollPane);

        Scene scene =
                new Scene(root, 1000, 700);

        stage.setTitle(
                "System Settings - Online Healthcare"
        );

        stage.setScene(scene);
        stage.show();
    }

    // =========================
    // ALERT METHOD
    // =========================

    private void showAlert(
            Alert.AlertType type,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle("System Settings");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}