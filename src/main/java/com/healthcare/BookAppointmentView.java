package com.healthcare;

import com.healthcare.model.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;

public class BookAppointmentView {

    private final String NAVY = "#12355b";
    private final String BLUE = "#2693df";
    private final String BG = "#f4f8fc";
    private final String TEXT = "#163b63";
    private final String MUTED = "#64748b";

    public void show(Stage stage) {

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(255);
        sidebar.setPadding(new Insets(25, 15, 20, 15));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        // Logo
        HBox logo = new HBox(12);
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.setPadding(new Insets(0, 10, 20, 10));

        StackPane logoIcon = new StackPane();
        logoIcon.setPrefSize(48, 48);
        logoIcon.setMinSize(48, 48);
        logoIcon.setMaxSize(48, 48);
        logoIcon.setStyle(
                "-fx-background-color: #2d9cdb;" +
                        "-fx-background-radius: 12;"
        );

        Label plus = new Label("+");
        plus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 31));
        plus.setTextFill(Color.WHITE);

        logoIcon.getChildren().add(plus);

        VBox logoText = new VBox(0);

        Label medicare = new Label("MediCare");
        medicare.setFont(Font.font("Segoe UI", FontWeight.BOLD, 23));
        medicare.setTextFill(Color.WHITE);

        Label tagline = new Label("Your Health, Our Priority");
        tagline.setFont(Font.font("Segoe UI", 10));
        tagline.setTextFill(Color.web("#b9d2e8"));

        logoText.getChildren().addAll(medicare, tagline);
        logo.getChildren().addAll(logoIcon, logoText);

        // Profile
        HBox profile = new HBox(10);
        profile.setAlignment(Pos.CENTER_LEFT);
        profile.setPadding(new Insets(12));
        profile.setStyle(
                "-fx-background-color: #1d4b79;" +
                        "-fx-background-radius: 12;"
        );

        Circle profileCircle = new Circle(23, Color.web("#dceeff"));

        Label profileInitials = new Label(UserSession.getInitials());
        profileInitials.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        profileInitials.setTextFill(Color.web("#1976d2"));

        StackPane profileIcon = new StackPane(
                profileCircle,
                profileInitials
        );

        VBox profileText = new VBox(2);

        Label name = new Label(UserSession.getFullName());
        name.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        name.setTextFill(Color.WHITE);

        Label role = new Label("Patient  •  Online");
        role.setFont(Font.font("Segoe UI", 10));
        role.setTextFill(Color.web("#c7dced"));

        profileText.getChildren().addAll(name, role);

        profile.getChildren().addAll(
                profileIcon,
                profileText
        );

        // Navigation
        Button dashboardBtn = navButton("⌂", "Dashboard", false);
        Button bookBtn = navButton("▣", "Book Appointment", true);
        Button appointmentsBtn = navButton("▤", "My Appointments", false);
        Button historyBtn = navButton("♥", "Medical History", false);
        Button profileBtn = navButton("♙", "My Profile", false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = navButton("↪", "Logout", false);

        sidebar.getChildren().addAll(
                logo,
                profile,
                createHeight(15),
                dashboardBtn,
                bookBtn,
                appointmentsBtn,
                historyBtn,
                profileBtn,
                spacer,
                logoutBtn
        );

        // =========================================================
        // TOP BAR
        // =========================================================

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5eaf0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label pageTitle = new Label("Book Appointment");
        pageTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                22
        ));
        pageTitle.setTextFill(Color.web(TEXT));

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label date = new Label("26 September 2026");
        date.setFont(Font.font("Segoe UI", 12));
        date.setTextFill(Color.web(MUTED));

        Label notification = new Label("🔔");
        notification.setFont(Font.font(17));

        topBar.getChildren().addAll(
                pageTitle,
                topSpacer,
                notification,
                createWidth(18),
                date
        );

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = new VBox(18);
        content.setPadding(new Insets(25));
        content.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        // Page heading
        VBox heading = new VBox(4);

        Label title = new Label("Find a Doctor & Book Your Visit");
        title.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                25
        ));
        title.setTextFill(Color.web(TEXT));

        Label subtitle = new Label(
                "Choose a doctor, select your preferred date and time, and confirm your appointment."
        );
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web(MUTED));

        heading.getChildren().addAll(
                title,
                subtitle
        );

        // =========================================================
        // MAIN TWO-COLUMN AREA
        // =========================================================

        HBox mainArea = new HBox(18);

        // =========================================================
        // LEFT - FORM
        // =========================================================

        VBox formCard = new VBox(17);
        formCard.setPadding(new Insets(25));
        formCard.setStyle(cardStyle());
        HBox.setHgrow(formCard, Priority.ALWAYS);

        Label formTitle = new Label("Appointment Details");
        formTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                18
        ));
        formTitle.setTextFill(Color.web(TEXT));

        // Doctor
        Label doctorLabel = fieldLabel("Select Doctor");

        ComboBox<String> doctorBox = new ComboBox<>();

        // doctors come from the database (doctors JOIN users)
        java.util.Map<String, Integer> doctorIds = new java.util.LinkedHashMap<>();
        try {
            for (com.healthcare.model.Doctor d :
                    new com.healthcare.dao.DoctorDAO().getAllDoctors()) {
                doctorIds.put(d.getLabel(), d.getDoctorId());
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        doctorBox.getItems().addAll(doctorIds.keySet());

        doctorBox.setPromptText("Choose a doctor");
        doctorBox.setMaxWidth(Double.MAX_VALUE);
        doctorBox.setPrefHeight(45);
        doctorBox.setStyle(fieldStyle());

        // Doctor speciality info
        Label specialityInfo = new Label(
                "Select a doctor to see available appointment slots."
        );
        specialityInfo.setFont(Font.font("Segoe UI", 11));
        specialityInfo.setTextFill(Color.web(MUTED));

        // Date
        Label dateLabel = fieldLabel("Preferred Date");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPrefHeight(45);
        datePicker.setStyle(fieldStyle());

        // Time
        Label timeLabel = fieldLabel("Preferred Time");

        ComboBox<String> timeBox = new ComboBox<>();
        timeBox.setPromptText("Choose an available time");
        timeBox.setMaxWidth(Double.MAX_VALUE);
        timeBox.setPrefHeight(45);
        timeBox.setStyle(fieldStyle());

        // Reason
        Label reasonLabel = fieldLabel("Reason for Visit");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText(
                "Briefly describe your symptoms or reason for consultation..."
        );
        reasonArea.setPrefRowCount(4);
        reasonArea.setWrapText(true);
        reasonArea.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #d7e1eb;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10;" +
                        "-fx-font-size: 13;"
        );

        // Notes
        Label note = new Label(
                "🔒 Your appointment information is securely handled."
        );
        note.setFont(Font.font("Segoe UI", 11));
        note.setTextFill(Color.web("#5f7d99"));

        // Buttons
        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button backButton = new Button("← Back to Dashboard");
        backButton.setPrefHeight(43);
        backButton.setStyle(
                "-fx-background-color: #edf3f8;" +
                        "-fx-text-fill: #42617e;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 18;"
        );

        Button bookButton = new Button("Confirm Appointment  →");
        bookButton.setPrefHeight(43);
        bookButton.setStyle(
                "-fx-background-color: #2693df;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 20;"
        );

        buttons.getChildren().addAll(
                backButton,
                bookButton
        );

        formCard.getChildren().addAll(
                formTitle,
                doctorLabel,
                doctorBox,
                specialityInfo,
                dateLabel,
                datePicker,
                timeLabel,
                timeBox,
                reasonLabel,
                reasonArea,
                note,
                buttons
        );

        // =========================================================
        // RIGHT - APPOINTMENT SUMMARY
        // =========================================================

        VBox summaryCard = new VBox(15);
        summaryCard.setPadding(new Insets(22));
        summaryCard.setPrefWidth(320);
        summaryCard.setMaxWidth(320);
        summaryCard.setStyle(cardStyle());

        Label summaryTitle = new Label("Appointment Summary");
        summaryTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                18
        ));
        summaryTitle.setTextFill(Color.web(TEXT));

        // Doctor icon
        StackPane doctorIcon = new StackPane();

        Circle doctorCircle = new Circle(38);
        doctorCircle.setFill(Color.web("#e3f3ff"));

        Label initials = new Label("DS");
        initials.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                15
        ));
        initials.setTextFill(Color.web(BLUE));

        doctorIcon.getChildren().addAll(
                doctorCircle,
                initials
        );

        Label selectedDoctor = new Label("No doctor selected");
        selectedDoctor.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                15
        ));
        selectedDoctor.setTextFill(Color.web(TEXT));

        Label selectedSpeciality = new Label(
                "Choose a doctor"
        );
        selectedSpeciality.setFont(Font.font("Segoe UI", 12));
        selectedSpeciality.setTextFill(Color.web(MUTED));

        VBox doctorSummary = new VBox(3);
        doctorSummary.getChildren().addAll(
                selectedDoctor,
                selectedSpeciality
        );

        HBox doctorSummaryRow = new HBox(12);
        doctorSummaryRow.setAlignment(Pos.CENTER_LEFT);

        doctorSummaryRow.getChildren().addAll(
                doctorIcon,
                doctorSummary
        );

        Separator separator1 = new Separator();

        Label summaryDate = new Label("Date");
        summaryDate.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                12
        ));
        summaryDate.setTextFill(Color.web(MUTED));

        Label selectedDate = new Label("Not selected");
        selectedDate.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                14
        ));
        selectedDate.setTextFill(Color.web(TEXT));

        Label summaryTime = new Label("Time");
        summaryTime.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                12
        ));
        summaryTime.setTextFill(Color.web(MUTED));

        Label selectedTime = new Label("Not selected");
        selectedTime.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                14
        ));
        selectedTime.setTextFill(Color.web(TEXT));

        VBox dateSummary = new VBox(3);
        dateSummary.getChildren().addAll(
                summaryDate,
                selectedDate
        );

        VBox timeSummary = new VBox(3);
        timeSummary.getChildren().addAll(
                summaryTime,
                selectedTime
        );

        HBox dateTime = new HBox(35);
        dateTime.getChildren().addAll(
                dateSummary,
                timeSummary
        );

        Separator separator2 = new Separator();

        Label summaryNote = new Label(
                "Please review your appointment details before confirming."
        );
        summaryNote.setWrapText(true);
        summaryNote.setFont(Font.font("Segoe UI", 11));
        summaryNote.setTextFill(Color.web(MUTED));

        Label consultation = new Label(
                "Consultation Duration: 30 minutes"
        );
        consultation.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                11
        ));
        consultation.setTextFill(Color.web(BLUE));

        summaryCard.getChildren().addAll(
                summaryTitle,
                doctorSummaryRow,
                separator1,
                dateTime,
                separator2,
                summaryNote,
                consultation
        );

        mainArea.getChildren().addAll(
                formCard,
                summaryCard
        );

        content.getChildren().addAll(
                heading,
                mainArea
        );

        // =========================================================
        // LIVE SUMMARY
        // =========================================================

        // show today's date in the summary from the start
        selectedDate.setText(datePicker.getValue().toString());

        // past days cannot be chosen
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });

        // time slots = doctor's working hours cut by the admin's slot duration,
        // minus slots already booked (and minus past times for today)
        Runnable loadSlots = () -> {
            timeBox.getItems().clear();
            timeBox.setValue(null);
            selectedTime.setText("Not selected");

            String chosenDoctor = doctorBox.getValue();
            LocalDate day = datePicker.getValue();
            if (chosenDoctor == null || day == null) {
                return;
            }

            try {
                int minutes = 30;
                try {
                    minutes = Integer.parseInt(
                            com.healthcare.service.SettingsService.getSlotDuration()
                                    .replaceAll("\\D", ""));
                } catch (NumberFormatException ignored) {
                    // keep 30 minutes
                }
                if (minutes <= 0) {
                    minutes = 30;
                }

                java.time.format.DateTimeFormatter fmt =
                        java.time.format.DateTimeFormatter.ofPattern(
                                "hh:mm a", java.util.Locale.ENGLISH);

                for (java.time.LocalTime t :
                        new com.healthcare.dao.AppointmentDAO().getAvailableSlots(
                                doctorIds.get(chosenDoctor), day, minutes)) {
                    timeBox.getItems().add(t.format(fmt));
                }

                timeBox.setPromptText(timeBox.getItems().isEmpty()
                        ? "No free slots on this day"
                        : "Choose an available time");

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                showError("Database error: " + e.getMessage());
            }
        };

        doctorBox.setOnAction(event -> {

            String selected = doctorBox.getValue();

            if (selected != null) {

                selectedDoctor.setText(selected);

                String[] parts = selected.split(" - ", 2);
                selectedSpeciality.setText(parts.length > 1 ? parts[1] : "");

                StringBuilder docInitials = new StringBuilder();
                for (String w : parts[0].split("\\s+")) {
                    if (!w.isEmpty() && !w.equalsIgnoreCase("Dr.")
                            && docInitials.length() < 2) {
                        docInitials.append(Character.toUpperCase(w.charAt(0)));
                    }
                }
                initials.setText(docInitials.toString());
            }

            loadSlots.run();
        });

        datePicker.setOnAction(event -> {

            LocalDate dateValue = datePicker.getValue();

            if (dateValue != null) {
                selectedDate.setText(
                        dateValue.toString()
                );
            }

            loadSlots.run();
        });

        timeBox.setOnAction(event -> {

            String time = timeBox.getValue();

            if (time != null) {
                selectedTime.setText(time);
            }
        });

        // =========================================================
        // BOOK APPOINTMENT
        // =========================================================

        bookButton.setOnAction(event -> {

            if (doctorBox.getValue() == null) {

                showError(
                        "Please select a doctor first."
                );

                doctorBox.requestFocus();
                return;
            }

            if (datePicker.getValue() == null) {

                showError(
                        "Please select an appointment date."
                );

                datePicker.requestFocus();
                return;
            }

            if (timeBox.getValue() == null) {

                showError(
                        "Please select an available time."
                );

                timeBox.requestFocus();
                return;
            }

            if (reasonArea.getText().trim().isEmpty()) {

                showError(
                        "Please enter the reason for your visit."
                );

                reasonArea.requestFocus();
                return;
            }

            if (!com.healthcare.service.SettingsService.isBookingEnabled()) {

                showError(
                        "Online booking is currently turned off by the administrator."
                );
                return;
            }

            // ----------------------------------------------------
            // SAVE TO DATABASE
            // ----------------------------------------------------
            try {
                int doctorId = doctorIds.get(doctorBox.getValue());
                LocalDate bookingDate = datePicker.getValue();
                java.time.LocalTime bookingTime = java.time.LocalTime.parse(
                        timeBox.getValue(),
                        java.time.format.DateTimeFormatter.ofPattern(
                                "hh:mm a", java.util.Locale.ENGLISH));

                if (bookingDate.isBefore(LocalDate.now())) {
                    showError("You cannot book an appointment in the past.");
                    return;
                }

                com.healthcare.dao.AppointmentDAO appointmentDAO =
                        new com.healthcare.dao.AppointmentDAO();

                // is the doctor working at that day and time?
                String notAvailable =
                        appointmentDAO.checkAvailability(doctorId, bookingDate, bookingTime);

                if (notAvailable != null) {
                    showError(notAvailable);
                    return;
                }

                String result = appointmentDAO.book(
                        UserSession.getUserId(),
                        doctorId,
                        bookingDate,
                        bookingTime,
                        reasonArea.getText().trim());

                if ("SLOT ALREADY BOOKED".equals(result)) {
                    showError("This slot is already booked. Please choose another time.");
                    return;
                }

                if (!"BOOKED".equals(result)) {
                    showError("Booking failed. Please try again.");
                    return;
                }

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                showError("Database error: " + e.getMessage());
                return;
            }

            Alert success = new Alert(
                    Alert.AlertType.INFORMATION
            );

            success.setTitle("Appointment Confirmed");
            success.setHeaderText(
                    "Appointment booked successfully! ✓"
            );

            success.setContentText(
                    "Doctor: " + doctorBox.getValue() +
                            "\nDate: " + datePicker.getValue() +
                            "\nTime: " + timeBox.getValue() +
                            "\n\nYour appointment has been added."
            );

            success.showAndWait();

            // Return to patient dashboard
            PatientDashboard dashboard =
                    new PatientDashboard();

            dashboard.show(stage);
        });

        // =========================================================
        // NAVIGATION
        // =========================================================

        dashboardBtn.setOnAction(event -> {

            PatientDashboard dashboard =
                    new PatientDashboard();

            dashboard.show(stage);
        });

        backButton.setOnAction(event -> {

            PatientDashboard dashboard =
                    new PatientDashboard();

            dashboard.show(stage);
        });

        appointmentsBtn.setOnAction(event -> {

            MyAppointmentsView view =
                    new MyAppointmentsView();

            view.show(stage);
        });

        historyBtn.setOnAction(event -> {

            MedicalHistoryView view =
                    new MedicalHistoryView();

            view.show(stage);
        });

        profileBtn.setOnAction(event -> {

            ProfileView view =
                    new ProfileView();

            view.show(stage);
        });

        logoutBtn.setOnAction(event -> {

            LoginView loginView =
                    new LoginView();

            loginView.show(stage);
        });

        // =========================================================
        // ROOT + SCROLL
        // =========================================================

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: " + BG + ";" +
                        "-fx-border-color: transparent;"
        );

        BorderPane root = new BorderPane();

        root.setLeft(sidebar);
        root.setTop(topBar);
        root.setCenter(scrollPane);

        Scene scene = new Scene(
                root,
                1400,
                820
        );

        stage.setTitle(
                "MediCare - Book Appointment"
        );

        stage.setScene(scene);

        stage.setMinWidth(1200);
        stage.setMinHeight(750);

        stage.show();
    }

    // =============================================================
    // SIDEBAR BUTTON
    // =============================================================

    private Button navButton(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(
                "Segoe UI",
                16
        ));

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font(
                "Segoe UI",
                active
                        ? FontWeight.BOLD
                        : FontWeight.NORMAL,
                13
        ));

        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER_LEFT);

        box.getChildren().addAll(
                iconLabel,
                textLabel
        );

        Button button = new Button();
        button.setGraphic(box);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);
        button.setAlignment(Pos.CENTER_LEFT);

        if (active) {

            button.setStyle(
                    "-fx-background-color: #269cdd;" +
                            "-fx-background-radius: 9;" +
                            "-fx-padding: 0 14;"
            );

            iconLabel.setTextFill(Color.WHITE);
            textLabel.setTextFill(Color.WHITE);

        } else {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-padding: 0 14;"
            );

            iconLabel.setTextFill(
                    Color.web("#d7e6f5")
            );

            textLabel.setTextFill(
                    Color.web("#d7e6f5")
            );
        }

        return button;
    }

    // =============================================================
    // LABEL
    // =============================================================

    private Label fieldLabel(String text) {

        Label label = new Label(text);

        label.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));

        label.setTextFill(Color.web(TEXT));

        return label;
    }

    // =============================================================
    // FIELD STYLE
    // =============================================================

    private String fieldStyle() {

        return
                "-fx-background-color: white;" +
                        "-fx-border-color: #d7e1eb;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13;";
    }

    // =============================================================
    // CARD STYLE
    // =============================================================

    private String cardStyle() {

        return
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #e1e8ef;" +
                        "-fx-border-radius: 14;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(30,70,100,0.08), 12, 0.1, 0, 3" +
                        ");";
    }

    // =============================================================
    // ERROR
    // =============================================================

    private void showError(String message) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Appointment Error");
        alert.setHeaderText(
                "Unable to book appointment"
        );

        alert.setContentText(message);

        alert.showAndWait();
    }

    private Region createHeight(double height) {

        Region region = new Region();

        region.setPrefHeight(height);

        return region;
    }

    private Region createWidth(double width) {

        Region region = new Region();

        region.setPrefWidth(width);

        return region;
    }
}