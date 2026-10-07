package com.healthcare;

import com.healthcare.model.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PatientDashboard {

    private final String NAVY = "#12355b";
    private final String BLUE = "#2693df";
    private final String LIGHT_BG = "#f4f8fc";
    private final String TEXT = "#163b63";
    private final String MUTED = "#64748b";

    public void show(Stage stage) {

        // ---- real data from the database ----
        HomeData data = loadHomeData();

        java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter
                .ofPattern("hh:mm a", java.util.Locale.ENGLISH);

        boolean hasNext = data.next != null;
        String nextDoctor = hasNext ? data.next.doctorName : "No upcoming appointment";
        String nextSpeciality = hasNext ? data.next.specialization
                : "Book a visit with one of our doctors";
        String nextRating = !hasNext ? ""
                : (data.next.reviewCount == 0 ? "No reviews yet"
                : String.format("★ %.1f  •  %d review%s", data.next.averageRating,
                data.next.reviewCount, data.next.reviewCount == 1 ? "" : "s"));
        String nextDate = hasNext ? data.next.date.format(java.time.format.DateTimeFormatter
                .ofPattern("d MMMM yyyy", java.util.Locale.ENGLISH)) : "—";
        String nextTime = hasNext ? data.next.time.format(timeFmt) : "—";
        String nextDuration = hasNext
                ? com.healthcare.service.SettingsService.getSlotDuration() : "";
        String nextInitials = hasNext ? initialsOf(data.next.doctorName) : "+";

        // =========================================================
        // LEFT SIDEBAR
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

        // Patient profile
        HBox patientProfile = new HBox(12);
        patientProfile.setAlignment(Pos.CENTER_LEFT);
        patientProfile.setPadding(new Insets(12));
        patientProfile.setStyle(
                "-fx-background-color: #1d4b79;" +
                        "-fx-background-radius: 12;"
        );

        StackPane avatar = new StackPane();

        Circle avatarCircle = new Circle(25);
        avatarCircle.setFill(Color.web("#dceeff"));

        Label avatarText = new Label(UserSession.getInitials());
        avatarText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        avatarText.setTextFill(Color.web("#1976d2"));

        avatar.getChildren().addAll(avatarCircle, avatarText);

        VBox profileInfo = new VBox(3);

        Label patientName = new Label(UserSession.getFullName());
        patientName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        patientName.setTextFill(Color.WHITE);

        HBox onlineRow = new HBox(5);
        onlineRow.setAlignment(Pos.CENTER_LEFT);

        Circle onlineDot = new Circle(4, Color.web("#22c55e"));

        Label patientRole = new Label("Patient  •  Online");
        patientRole.setFont(Font.font("Segoe UI", 11));
        patientRole.setTextFill(Color.web("#c7dced"));

        onlineRow.getChildren().addAll(onlineDot, patientRole);
        profileInfo.getChildren().addAll(patientName, onlineRow);

        patientProfile.getChildren().addAll(avatar, profileInfo);

        // Navigation
        Button dashboardBtn = navButton("⌂", "Dashboard", true);
        Button bookBtn = navButton("▣", "Book Appointment", false);
        Button appointmentBtn = navButton("▤", "My Appointments", false);
        Button historyBtn = navButton("♥", "Medical History", false);
        Button profileBtn = navButton("♙", "My Profile", false);

        // Sidebar promotional card
        VBox promo = new VBox(8);
        promo.setPadding(new Insets(18));
        promo.setStyle(
                "-fx-background-color: #1d4b79;" +
                        "-fx-background-radius: 14;"
        );

        Label promoTitle = new Label("Stay Healthy,\nStay Happy!");
        promoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        promoTitle.setTextFill(Color.WHITE);

        Label promoText = new Label(
                "Book your appointment\nwith trusted doctors."
        );
        promoText.setFont(Font.font("Segoe UI", 11));
        promoText.setTextFill(Color.web("#c7dced"));

        Button bookNow = new Button("Book Now  →");
        bookNow.setStyle(
                "-fx-background-color: #2d9cdb;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 15;"
        );

        promo.getChildren().addAll(
                promoTitle,
                promoText,
                bookNow
        );

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        Button logoutBtn = navButton("↪", "Logout", false);

        sidebar.getChildren().addAll(
                logo,
                patientProfile,
                createHeight(15),
                dashboardBtn,
                bookBtn,
                appointmentBtn,
                historyBtn,
                profileBtn,
                sideSpacer,
                promo,
                createHeight(15),
                logoutBtn
        );

        // =========================================================
        // TOP HEADER
        // =========================================================

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5eaf0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // Search
        TextField search = new TextField();
        search.setPromptText("Search doctors, appointments, or services...");
        search.setPrefWidth(580);
        search.setPrefHeight(40);
        search.setStyle(
                "-fx-background-color: #f5f8fc;" +
                        "-fx-border-color: #dce5ef;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 0 15;" +
                        "-fx-font-size: 13;"
        );

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label notification = new Label("●");
        notification.setFont(Font.font(17));
        notification.setTextFill(Color.web("#ef4444"));

        Label bell = new Label("🔔");
        bell.setFont(Font.font(17));

        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        dateBox.setPadding(new Insets(0, 10, 0, 18));

        Label calendar = new Label("▣");
        calendar.setTextFill(Color.web(BLUE));

        VBox dateInfo = new VBox(2);

        Label day = new Label(java.time.LocalDate.now().getDayOfWeek()
                .getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH));
        day.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        day.setTextFill(Color.web(TEXT));

        Label date = new Label(java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.ENGLISH)));
        date.setFont(Font.font("Segoe UI", 11));
        date.setTextFill(Color.web(MUTED));

        dateInfo.getChildren().addAll(day, date);
        dateBox.getChildren().addAll(calendar, dateInfo);

        Circle headerAvatar = new Circle(19, Color.web("#e5f2ff"));

        Label userIcon = new Label("♙");
        userIcon.setFont(Font.font(18));
        userIcon.setTextFill(Color.web(TEXT));

        StackPane user = new StackPane(
                headerAvatar,
                userIcon
        );

        topBar.getChildren().addAll(
                search,
                topSpacer,
                bell,
                notification,
                dateBox,
                user
        );

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox mainContent = new VBox(18);
        mainContent.setPadding(new Insets(18, 25, 25, 25));
        mainContent.setStyle(
                "-fx-background-color: " + LIGHT_BG + ";"
        );

        // =========================================================
        // WELCOME BANNER
        // =========================================================

        HBox welcomeBanner = new HBox();
        welcomeBanner.setPadding(new Insets(25, 30, 22, 30));
        welcomeBanner.setPrefHeight(165);
        welcomeBanner.setStyle(
                "-fx-background-color: linear-gradient(to right, #dff2ff, #eef8ff);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #d3eafa;" +
                        "-fx-border-radius: 16;"
        );

        VBox welcomeText = new VBox(7);

        Label welcomeTitle = new Label(
                "Welcome back, " + UserSession.getFirstName() + "! 👋"
        );
        welcomeTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 27)
        );
        welcomeTitle.setTextFill(Color.web(TEXT));

        Label welcomeSub = new Label(
                "Take control of your health journey with MediCare."
        );
        welcomeSub.setFont(Font.font("Segoe UI", 15));
        welcomeSub.setTextFill(Color.web("#4b6b8c"));

        HBox miniFeatures = new HBox(12);

        miniFeatures.getChildren().addAll(
                miniFeature("♙", "Trusted Doctors"),
                miniFeature("▣", "Easy Appointments"),
                miniFeature("▤", "Secure Records")
        );

        welcomeText.getChildren().addAll(
                welcomeTitle,
                welcomeSub,
                createHeight(5),
                miniFeatures
        );

        Region bannerSpacer = new Region();
        HBox.setHgrow(bannerSpacer, Priority.ALWAYS);

        VBox bannerVisual = new VBox();
        bannerVisual.setAlignment(Pos.CENTER);

        Label hospital = new Label("🏥");
        hospital.setFont(Font.font(65));

        Label bannerText = new Label("Healthcare\nmade simple");
        bannerText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        bannerText.setTextFill(Color.web("#2878ad"));

        bannerVisual.getChildren().addAll(
                hospital,
                bannerText
        );

        welcomeBanner.getChildren().addAll(
                welcomeText,
                bannerSpacer,
                bannerVisual
        );

        // =========================================================
        // STAT CARDS
        // =========================================================

        HBox stats = new HBox(14);

        VBox stat1 = statCard(
                "▣",
                "Upcoming Appointment",
                hasNext ? data.next.date.format(java.time.format.DateTimeFormatter
                        .ofPattern("dd MMM yyyy", java.util.Locale.ENGLISH)) : "None",
                hasNext ? nextTime + "  •  " + nextDoctor : "Book your first visit",
                "#2693df",
                "#e5f3ff"
        );

        VBox stat2 = statCard(
                "▤",
                "Total Appointments",
                String.valueOf(data.counts[0]),
                "↑ " + data.counts[1] + " this month",
                "#7c4dff",
                "#f0e9ff"
        );

        VBox stat3 = statCard(
                "♥",
                "Medical History",
                data.counts[2] == 0 ? "No records yet"
                        : data.counts[2] + (data.counts[2] == 1 ? " record" : " records"),
                "View your health records",
                "#10b981",
                "#e4faf2"
        );

        VBox stat4 = statCard(
                "✓",
                "Account Status",
                "Active",
                "All features enabled",
                "#f59e0b",
                "#fff4df"
        );

        HBox.setHgrow(stat1, Priority.ALWAYS);
        HBox.setHgrow(stat2, Priority.ALWAYS);
        HBox.setHgrow(stat3, Priority.ALWAYS);
        HBox.setHgrow(stat4, Priority.ALWAYS);

        stats.getChildren().addAll(
                stat1,
                stat2,
                stat3,
                stat4
        );

        // =========================================================
        // TWO COLUMN AREA
        // =========================================================

        HBox columns = new HBox(18);

        VBox leftColumn = new VBox(15);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        VBox rightColumn = new VBox(15);
        rightColumn.setPrefWidth(285);

        // QUICK ACTIONS
        VBox quickTitle = sectionTitle(
                "Quick Actions",
                "Everything you need in one place"
        );

        HBox quickActions = new HBox(12);

        Button action1 = actionCard(
                "▣",
                "Book Appointment",
                "Schedule a visit with a doctor",
                "#e9f6ff",
                BLUE
        );

        Button action2 = actionCard(
                "▤",
                "My Appointments",
                "View and manage appointments",
                "#f1ecff",
                "#7c4dff"
        );

        Button action3 = actionCard(
                "♥",
                "Medical History",
                "View your previous records",
                "#e7faf3",
                "#10b981"
        );

        Button action4 = actionCard(
                "♙",
                "My Profile",
                "Update your personal information",
                "#fff3e5",
                "#f59e0b"
        );

        HBox.setHgrow(action1, Priority.ALWAYS);
        HBox.setHgrow(action2, Priority.ALWAYS);
        HBox.setHgrow(action3, Priority.ALWAYS);
        HBox.setHgrow(action4, Priority.ALWAYS);

        quickActions.getChildren().addAll(
                action1,
                action2,
                action3,
                action4
        );

        // NEXT APPOINTMENT
        VBox nextAppointment = new VBox(12);
        nextAppointment.setPadding(new Insets(18));
        nextAppointment.setStyle(cardStyle());

        HBox nextHeader = new HBox();

        Label nextTitle = new Label("Next Appointment");
        nextTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                18
        ));
        nextTitle.setTextFill(Color.web(TEXT));

        Region nextSpacer = new Region();
        HBox.setHgrow(nextSpacer, Priority.ALWAYS);

        Hyperlink viewAll = new Hyperlink("View All");
        viewAll.setTextFill(Color.web(BLUE));

        nextHeader.getChildren().addAll(
                nextTitle,
                nextSpacer,
                viewAll
        );

        HBox appointmentDetails = new HBox(15);
        appointmentDetails.setAlignment(Pos.CENTER_LEFT);

        StackPane doctorAvatar = new StackPane();

        Circle doctorCircle = new Circle(35);
        doctorCircle.setFill(Color.web("#e1f1ff"));

        Label doctorIcon = new Label(nextInitials);
        doctorIcon.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                14
        ));
        doctorIcon.setTextFill(Color.web(BLUE));

        doctorAvatar.getChildren().addAll(
                doctorCircle,
                doctorIcon
        );

        VBox doctorInfo = new VBox(4);

        Label doctorName = new Label(nextDoctor);
        doctorName.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                16
        ));
        doctorName.setTextFill(Color.web(TEXT));

        Label speciality = new Label(nextSpeciality);
        speciality.setFont(Font.font("Segoe UI", 12));
        speciality.setTextFill(Color.web(MUTED));

        Label rating = new Label(nextRating);
        rating.setFont(Font.font("Segoe UI", 11));
        rating.setTextFill(Color.web("#f59e0b"));

        doctorInfo.getChildren().addAll(
                doctorName,
                speciality,
                rating
        );

        Region appointSpacer = new Region();
        HBox.setHgrow(appointSpacer, Priority.ALWAYS);

        VBox appointTime = new VBox(4);

        Label appointDate = new Label(nextDate);
        appointDate.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        appointDate.setTextFill(Color.web(TEXT));

        Label appointTimeText = new Label(nextTime);
        appointTimeText.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                14
        ));
        appointTimeText.setTextFill(Color.web(BLUE));

        Label duration = new Label(nextDuration);
        duration.setFont(Font.font("Segoe UI", 11));
        duration.setTextFill(Color.web(MUTED));

        appointTime.getChildren().addAll(
                appointDate,
                appointTimeText,
                duration
        );

        appointmentDetails.getChildren().addAll(
                doctorAvatar,
                doctorInfo,
                appointSpacer,
                appointTime
        );

        nextAppointment.getChildren().addAll(
                nextHeader,
                appointmentDetails
        );

        leftColumn.getChildren().addAll(
                quickTitle,
                quickActions,
                nextAppointment
        );

        // =========================================================
        // HEALTH TIPS
        // =========================================================

        VBox healthTips = new VBox(12);
        healthTips.setPadding(new Insets(18));
        healthTips.setStyle(cardStyle());

        HBox tipsHeader = new HBox();

        Label tipsTitle = new Label("💡  Health Tips");
        tipsTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                17
        ));
        tipsTitle.setTextFill(Color.web(TEXT));

        Region tipsSpacer = new Region();
        HBox.setHgrow(tipsSpacer, Priority.ALWAYS);

        Hyperlink tipsAll = new Hyperlink("View All");
        tipsAll.setTextFill(Color.web(BLUE));

        tipsHeader.getChildren().addAll(
                tipsTitle,
                tipsSpacer,
                tipsAll
        );

        VBox tipBox = new VBox(8);
        tipBox.setPadding(new Insets(15));
        tipBox.setStyle(
                "-fx-background-color: #e8f6ff;" +
                        "-fx-background-radius: 12;"
        );

        Label exercise = new Label("🏃");
        exercise.setFont(Font.font(32));

        Label tipHeading = new Label(
                "Stay Active,\nStay Healthy"
        );
        tipHeading.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                15
        ));
        tipHeading.setTextFill(Color.web(TEXT));

        Label tipText = new Label(
                "Regular exercise helps improve\n" +
                        "your physical and mental health."
        );
        tipText.setFont(Font.font("Segoe UI", 11));
        tipText.setTextFill(Color.web(MUTED));

        tipBox.getChildren().addAll(
                exercise,
                tipHeading,
                tipText
        );

        healthTips.getChildren().addAll(
                tipsHeader,
                tipBox
        );

        // =========================================================
        // REMINDERS
        // =========================================================

        VBox reminders = new VBox(10);
        reminders.setPadding(new Insets(18));
        reminders.setStyle(cardStyle());

        HBox reminderHeader = new HBox();

        Label reminderTitle = new Label("🔔  Reminders");
        reminderTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                17
        ));
        reminderTitle.setTextFill(Color.web(TEXT));

        Region reminderSpacer = new Region();
        HBox.setHgrow(reminderSpacer, Priority.ALWAYS);

        Hyperlink reminderAll = new Hyperlink("View All");
        reminderAll.setTextFill(Color.web(BLUE));

        reminderHeader.getChildren().addAll(
                reminderTitle,
                reminderSpacer,
                reminderAll
        );

        reminders.getChildren().add(reminderHeader);
        reminders.getChildren().addAll(buildReminders(data, timeFmt));

        viewAll.setOnAction(event -> new MyAppointmentsView().show(stage));
        reminderAll.setOnAction(event -> new MyAppointmentsView().show(stage));

        // =========================================================
        // HEALTH RECORD CARD
        // =========================================================

        HBox healthRecord = new HBox(12);
        healthRecord.setAlignment(Pos.CENTER_LEFT);
        healthRecord.setPadding(new Insets(15));
        healthRecord.setStyle(
                "-fx-background-color: #e5f9f0;" +
                        "-fx-background-radius: 12;"
        );

        Label shield = new Label("🛡");
        shield.setFont(Font.font(24));

        VBox healthRecordText = new VBox(3);

        Label recordTitle = new Label(
                "Your Health is Important"
        );
        recordTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        recordTitle.setTextFill(Color.web("#087f5b"));

        Label recordText = new Label(
                "Keep your records updated."
        );
        recordText.setFont(Font.font("Segoe UI", 10));
        recordText.setTextFill(Color.web(MUTED));

        healthRecordText.getChildren().addAll(
                recordTitle,
                recordText
        );

        healthRecord.getChildren().addAll(
                shield,
                healthRecordText
        );

        rightColumn.getChildren().addAll(
                healthTips,
                reminders,
                healthRecord
        );

        columns.getChildren().addAll(
                leftColumn,
                rightColumn
        );

        mainContent.getChildren().addAll(
                welcomeBanner,
                stats,
                columns
        );

        // =========================================================
        // NAVIGATION
        // =========================================================

        dashboardBtn.setOnAction(e -> show(stage));

        bookBtn.setOnAction(e -> {
            BookAppointmentView view = new BookAppointmentView();
            view.show(stage);
        });

        appointmentBtn.setOnAction(e -> {
            MyAppointmentsView view = new MyAppointmentsView();
            view.show(stage);
        });

        historyBtn.setOnAction(e -> {
            MedicalHistoryView view = new MedicalHistoryView();
            view.show(stage);
        });

        profileBtn.setOnAction(e -> {
            ProfileView view = new ProfileView();
            view.show(stage);
        });

        bookNow.setOnAction(e -> {
            BookAppointmentView view = new BookAppointmentView();
            view.show(stage);
        });

        action1.setOnAction(e -> {
            BookAppointmentView view = new BookAppointmentView();
            view.show(stage);
        });

        action2.setOnAction(e -> {
            MyAppointmentsView view = new MyAppointmentsView();
            view.show(stage);
        });

        action3.setOnAction(e -> {
            MedicalHistoryView view = new MedicalHistoryView();
            view.show(stage);
        });

        action4.setOnAction(e -> {
            ProfileView view = new ProfileView();
            view.show(stage);
        });

        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView();
            loginView.show(stage);
        });

        // =========================================================
        // MAIN LAYOUT
        // =========================================================

        BorderPane root = new BorderPane();

        root.setLeft(sidebar);
        root.setTop(topBar);

// Main content scrollable
        ScrollPane scrollPane = new ScrollPane(mainContent);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle(
                "-fx-background-color: #f4f8fc;" +
                        "-fx-border-color: transparent;"
        );

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1400, 820);

        stage.setTitle("MediCare - Patient Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(750);
        stage.show();
    }

    // =============================================================
    // STAT CARD
    // =============================================================

    // =========================================================
    // DATA
    // =========================================================

    /** Everything the home screen needs from the database. */
    private static class HomeData {
        com.healthcare.dao.PatientDashboardDAO.NextAppointment next;
        int[] counts = {0, 0, 0};          // total, this month, medical records
        java.util.List<com.healthcare.model.AppointmentRecord> appointments =
                new java.util.ArrayList<>();
        java.util.List<com.healthcare.dao.MedicalRecordDAO.RecordItem> records =
                new java.util.ArrayList<>();
    }

    private HomeData loadHomeData() {
        HomeData data = new HomeData();
        int patientId = UserSession.getUserId();

        try {
            com.healthcare.dao.PatientDashboardDAO dao =
                    new com.healthcare.dao.PatientDashboardDAO();

            data.next = dao.getNext(patientId);
            data.counts = dao.getCounts(patientId);
            data.appointments =
                    new com.healthcare.dao.AppointmentDAO().getPatientAppointments(patientId);
            data.records =
                    new com.healthcare.dao.MedicalRecordDAO().getForPatient(patientId);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error");
            alert.setHeaderText("Could not load your dashboard");
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return data;
    }

    /** "Dr. Anil Verma" -> "AV" (titles are skipped). */
    private String initialsOf(String fullName) {
        StringBuilder sb = new StringBuilder();
        for (String part : fullName.trim().split("\\s+")) {
            if (part.equalsIgnoreCase("Dr.") || part.equalsIgnoreCase("Dr")) {
                continue;
            }
            if (!part.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }

    /** Reminders built from the patient's real appointments and records. */
    private java.util.List<HBox> buildReminders(HomeData data,
                                                java.time.format.DateTimeFormatter timeFmt) {

        java.util.List<HBox> list = new java.util.ArrayList<>();

        if (data.next != null) {
            list.add(reminder(
                    "▣",
                    "Upcoming Appointment",
                    data.next.doctorName,
                    data.next.date.format(java.time.format.DateTimeFormatter
                            .ofPattern("d MMM", java.util.Locale.ENGLISH))
                            + ", " + data.next.time.format(timeFmt),
                    BLUE
            ));
        }

        if (!data.records.isEmpty()) {
            com.healthcare.dao.MedicalRecordDAO.RecordItem latest = data.records.get(0);
            String medicine = latest.prescription.isEmpty()
                    ? latest.diagnosis : latest.prescription;
            if (medicine.length() > 26) {
                medicine = medicine.substring(0, 25) + "…";
            }
            list.add(reminder(
                    "●",
                    "Latest Prescription",
                    medicine,
                    latest.date,
                    "#ef476f"
            ));
        }

        for (com.healthcare.model.AppointmentRecord a : data.appointments) {
            if ("Completed".equals(a.getStatus()) && !a.isRated()) {
                list.add(reminder(
                        "♥",
                        "Rate Your Visit",
                        a.getDoctorName(),
                        a.getDateDisplay(),
                        "#10b981"
                ));
                break;
            }
        }

        if (list.isEmpty()) {
            Label none = new Label("You're all caught up. No reminders right now.");
            none.setFont(Font.font("Segoe UI", 12));
            none.setTextFill(Color.web(MUTED));
            HBox row = new HBox(none);
            row.setPadding(new Insets(6, 0, 6, 0));
            list.add(row);
        }
        return list;
    }

    private VBox statCard(
            String icon,
            String title,
            String value,
            String description,
            String color,
            String iconBg
    ) {

        VBox card = new VBox(7);
        card.setPadding(new Insets(15));
        card.setPrefHeight(125);

        card.setStyle(cardStyle());

        HBox top = new HBox();

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(19));
        iconLabel.setAlignment(Pos.CENTER);

        StackPane iconBox = new StackPane(iconLabel);
        iconBox.setPrefSize(40, 40);
        iconBox.setMaxSize(40, 40);
        iconBox.setStyle(
                "-fx-background-color: " + iconBg + ";" +
                        "-fx-background-radius: 11;"
        );

        top.getChildren().add(iconBox);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 11));
        titleLabel.setTextFill(Color.web(MUTED));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                19
        ));
        valueLabel.setTextFill(Color.web(color));

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", 10));
        descLabel.setTextFill(Color.web(MUTED));

        card.getChildren().addAll(
                top,
                titleLabel,
                valueLabel,
                descLabel
        );

        return card;
    }

    // =============================================================
    // QUICK ACTION
    // =============================================================

    private Button actionCard(
            String icon,
            String title,
            String description,
            String bg,
            String iconColor
    ) {

        VBox content = new VBox(5);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                22
        ));
        iconLabel.setTextFill(Color.web(iconColor));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        titleLabel.setTextFill(Color.web(TEXT));

        Label descriptionLabel = new Label(description);
        descriptionLabel.setFont(Font.font("Segoe UI", 10));
        descriptionLabel.setTextFill(Color.web(MUTED));
        descriptionLabel.setWrapText(true);

        content.getChildren().addAll(
                iconLabel,
                titleLabel,
                descriptionLabel
        );

        Button button = new Button();
        button.setGraphic(content);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(115);

        button.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-background-radius: 13;" +
                        "-fx-border-color: #dce7f0;" +
                        "-fx-border-radius: 13;" +
                        "-fx-padding: 13;"
        );

        return button;
    }

    // =============================================================
    // SIDEBAR NAVIGATION
    // =============================================================

    private Button navButton(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI", 16));

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font(
                "Segoe UI",
                active ? FontWeight.BOLD : FontWeight.NORMAL,
                13
        ));

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(
                iconLabel,
                textLabel
        );

        Button button = new Button();
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);
        button.setAlignment(Pos.CENTER_LEFT);

        if (active) {
            button.setStyle(
                    "-fx-background-color: #269cdd;" +
                            "-fx-background-radius: 9;" +
                            "-fx-text-fill: white;" +
                            "-fx-padding: 0 14;"
            );
            iconLabel.setTextFill(Color.WHITE);
            textLabel.setTextFill(Color.WHITE);
        } else {
            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #d7e6f5;" +
                            "-fx-padding: 0 14;"
            );
            iconLabel.setTextFill(Color.web("#d7e6f5"));
            textLabel.setTextFill(Color.web("#d7e6f5"));
        }

        return button;
    }

    // =============================================================
    // MINI FEATURE
    // =============================================================

    private HBox miniFeature(
            String icon,
            String text
    ) {

        HBox box = new HBox(7);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(7, 11, 7, 9));
        box.setStyle(
                "-fx-background-color: rgba(255,255,255,0.65);" +
                        "-fx-background-radius: 20;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setTextFill(Color.web(BLUE));

        Label textLabel = new Label(text);
        textLabel.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                10
        ));
        textLabel.setTextFill(Color.web(TEXT));

        box.getChildren().addAll(
                iconLabel,
                textLabel
        );

        return box;
    }

    // =============================================================
    // REMINDER
    // =============================================================

    private HBox reminder(
            String icon,
            String title,
            String subtitle,
            String time,
            String color
    ) {

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(17));
        iconLabel.setTextFill(Color.web(color));

        VBox text = new VBox(3);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                11
        ));
        titleLabel.setTextFill(Color.web(TEXT));

        Label sub = new Label(subtitle);
        sub.setFont(Font.font("Segoe UI", 10));
        sub.setTextFill(Color.web(MUTED));

        text.getChildren().addAll(
                titleLabel,
                sub
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(time);
        timeLabel.setFont(Font.font("Segoe UI", 9));
        timeLabel.setTextFill(Color.web(MUTED));

        row.getChildren().addAll(
                iconLabel,
                text,
                spacer,
                timeLabel
        );

        return row;
    }

    // =============================================================
    // SECTION TITLE
    // =============================================================

    private VBox sectionTitle(
            String title,
            String subtitle
    ) {

        VBox box = new VBox(2);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                19
        ));
        titleLabel.setTextFill(Color.web(TEXT));

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Segoe UI", 11));
        subtitleLabel.setTextFill(Color.web(MUTED));

        box.getChildren().addAll(
                titleLabel,
                subtitleLabel
        );

        return box;
    }

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

    private Region createHeight(double height) {

        Region region = new Region();
        region.setPrefHeight(height);
        return region;
    }
}