package com.healthcare;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.layout.*;
        import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DoctorScheduleView {

    private final com.healthcare.service.ScheduleService scheduleService =
            new com.healthcare.service.ScheduleService();

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("Doctor Schedule");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1E3A5F"));

        Label subtitle = new Label(
                "Manage your available days and consultation timings."
        );
        subtitle.setTextFill(Color.web("#607D8B"));

        // =========================
        // DAY
        // =========================

        Label dayLabel = new Label("Select Day");
        dayLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        ComboBox<String> dayBox = new ComboBox<>();

        dayBox.getItems().addAll(
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        );

        dayBox.setPromptText("Choose a day");
        dayBox.setMaxWidth(Double.MAX_VALUE);
        dayBox.setPrefHeight(40);

        // =========================
        // START TIME
        // =========================

        Label startLabel = new Label("Start Time");
        startLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        ComboBox<String> startTimeBox = new ComboBox<>();

        startTimeBox.getItems().addAll(
                "09:00 AM",
                "10:00 AM",
                "11:00 AM",
                "12:00 PM",
                "02:00 PM",
                "03:00 PM",
                "04:00 PM"
        );

        startTimeBox.setPromptText("Select start time");
        startTimeBox.setMaxWidth(Double.MAX_VALUE);
        startTimeBox.setPrefHeight(40);

        // =========================
        // END TIME
        // =========================

        Label endLabel = new Label("End Time");
        endLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        ComboBox<String> endTimeBox = new ComboBox<>();

        endTimeBox.getItems().addAll(
                "10:00 AM",
                "11:00 AM",
                "12:00 PM",
                "01:00 PM",
                "03:00 PM",
                "04:00 PM",
                "05:00 PM"
        );

        endTimeBox.setPromptText("Select end time");
        endTimeBox.setMaxWidth(Double.MAX_VALUE);
        endTimeBox.setPrefHeight(40);

        // =========================
        // STATUS
        // =========================

        Label statusLabel = new Label("Schedule Status");
        statusLabel.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        CheckBox availableBox = new CheckBox(
                "Available for appointments"
        );

        availableBox.setSelected(true);

        // =========================
        // SAVE BUTTON
        // =========================

        Button saveButton = new Button("SAVE SCHEDULE");

        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setPrefHeight(45);

        saveButton.setStyle(
                "-fx-background-color: #1976D2;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-cursor: hand;"
        );

        // =========================
        // MESSAGE
        // =========================

        Label message = new Label();

        message.setFont(
                Font.font("Arial", FontWeight.BOLD, 13)
        );

        // =========================
        // CURRENT WEEKLY SCHEDULE (from the database)
        // =========================

        Label weekTitle = new Label("Your current weekly schedule");
        weekTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label weekText = new Label(weeklyText());
        weekText.setTextFill(Color.web("#607D8B"));
        weekText.setWrapText(true);

        // picking a day shows what is already saved for it
        dayBox.valueProperty().addListener((obs, oldDay, newDay) -> {

            availableBox.setSelected(false);
            startTimeBox.setValue(null);
            endTimeBox.setValue(null);

            if (newDay == null) {
                return;
            }

            try {
                for (com.healthcare.dao.ScheduleDAO.Slot slot :
                        scheduleService.weeklySchedule(com.healthcare.model.UserSession.getUserId())) {

                    if (slot.day.equals(newDay)) {
                        String from = com.healthcare.service.ScheduleService.format(slot.start);
                        String to = com.healthcare.service.ScheduleService.format(slot.end);

                        availableBox.setSelected(true);
                        startTimeBox.setValue(startTimeBox.getItems().contains(from) ? from : null);
                        endTimeBox.setValue(endTimeBox.getItems().contains(to) ? to : null);
                        break;
                    }
                }
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        });

        // =========================
        // SAVE ACTION
        // =========================

        saveButton.setOnAction(event -> {

            String day = dayBox.getValue();
            String startTime = startTimeBox.getValue();
            String endTime = endTimeBox.getValue();
            boolean available = availableBox.isSelected();

            if (day == null
                    || (available && (startTime == null || endTime == null))) {

                message.setText(
                        "Please complete all schedule details."
                );

                message.setTextFill(Color.RED);
                return;
            }

            try {
                String problem = scheduleService.save(
                        com.healthcare.model.UserSession.getUserId(),
                        day, startTime, endTime, available);

                if (problem != null) {
                    message.setText(problem);
                    message.setTextFill(Color.RED);
                    return;
                }

                message.setText(available
                        ? "Schedule saved successfully!"
                        : day + " is now marked as not available.");
                message.setTextFill(Color.GREEN);

                weekText.setText(weeklyText());

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                message.setText("Database error: " + e.getMessage());
                message.setTextFill(Color.RED);
            }
        });

        // =========================
        // CALENDAR VIEW OF APPOINTMENTS
        // =========================

        Label calendarTitle = new Label("Appointment calendar");
        calendarTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        java.util.Map<java.time.LocalDate, java.util.List<
                com.healthcare.model.PatientAppointment>> byDay = new java.util.HashMap<>();
        try {
            for (com.healthcare.model.PatientAppointment a :
                    new com.healthcare.dao.DoctorAppointmentDAO().getForDoctor(
                            com.healthcare.model.UserSession.getUserId())) {
                if (!"Cancelled".equals(a.getStatus())) {
                    byDay.computeIfAbsent(a.getDate(), d -> new java.util.ArrayList<>()).add(a);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        Label dayAppointments = new Label("Pick a date to see its appointments.");
        dayAppointments.setWrapText(true);
        dayAppointments.setTextFill(Color.web("#607D8B"));

        DatePicker calendar = new DatePicker();
        calendar.setPromptText("Choose a date");
        calendar.setMaxWidth(Double.MAX_VALUE);

        // days that have appointments are highlighted
        calendar.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && byDay.containsKey(item)) {
                    setStyle("-fx-background-color: #BBDEFB; -fx-font-weight: bold;");
                }
            }
        });

        calendar.setOnAction(event -> {
            java.time.LocalDate day = calendar.getValue();
            java.util.List<com.healthcare.model.PatientAppointment> list =
                    day == null ? null : byDay.get(day);

            if (list == null || list.isEmpty()) {
                dayAppointments.setText("No appointments on this day.");
                return;
            }

            list.sort(java.util.Comparator.comparing(
                    com.healthcare.model.PatientAppointment::getTime));

            StringBuilder sb = new StringBuilder();
            for (com.healthcare.model.PatientAppointment a : list) {
                sb.append(a.getTimeDisplay()).append("  -  ")
                        .append(a.getPatientName()).append("  (")
                        .append(a.getStatus()).append(")");
                if (!a.getReason().isEmpty()) {
                    sb.append("\n      ").append(a.getReason());
                }
                sb.append("\n");
            }
            dayAppointments.setText(sb.toString().trim());
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
        // FORM
        // =========================

        VBox form = new VBox(10);

        form.getChildren().addAll(
                dayLabel,
                dayBox,

                startLabel,
                startTimeBox,

                endLabel,
                endTimeBox,

                statusLabel,
                availableBox,

                saveButton,
                message,
                weekTitle,
                weekText,
                calendarTitle,
                calendar,
                dayAppointments,
                backButton
        );

        // =========================
        // CARD
        // =========================

        VBox card = new VBox(20);

        card.setPadding(new Insets(30));

        card.setMaxWidth(500);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 12px;"
        );

        card.getChildren().addAll(
                title,
                subtitle,
                form
        );

        // =========================
        // ROOT
        // =========================

        StackPane root = new StackPane();

        root.setPadding(new Insets(30));

        root.setStyle(
                "-fx-background-color: #F7FAFC;"
        );

        ScrollPane scroll = new ScrollPane(card);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        root.getChildren().add(scroll);

        // =========================
        // SCENE
        // =========================

        Scene scene =
                new Scene(root, 800, 700);

        stage.setTitle(
                "Doctor Schedule - Online Healthcare"
        );

        stage.setScene(scene);
        stage.show();
    }

    /** One line per working day, e.g. "Monday: 09:00 AM - 05:00 PM". */
    private String weeklyText() {
        try {
            StringBuilder sb = new StringBuilder();

            for (com.healthcare.dao.ScheduleDAO.Slot slot :
                    scheduleService.weeklySchedule(com.healthcare.model.UserSession.getUserId())) {

                sb.append(slot.day).append(":  ")
                        .append(com.healthcare.service.ScheduleService.format(slot.start))
                        .append(" - ")
                        .append(com.healthcare.service.ScheduleService.format(slot.end))
                        .append("\n");
            }

            return sb.length() == 0
                    ? "No working hours saved yet. Patients cannot book you until you add some."
                    : sb.toString().trim();

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return "Could not load the schedule: " + e.getMessage();
        }
    }
}
