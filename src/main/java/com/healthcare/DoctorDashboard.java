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

public class DoctorDashboard {

    // =========================================================
    // COLORS
    // =========================================================

    private final String NAVY = "#12355B";
    private final String BLUE = "#1976D2";
    private final String LIGHT_BLUE = "#EAF4FB";
    private final String BG = "#F4F8FC";
    private final String TEXT = "#183B60";
    private final String MUTED = "#64748B";
    private final String BORDER = "#E2E8F0";
    private final String GREEN = "#16A34A";
    private final String ORANGE = "#F59E0B";
    private final String RED = "#DC2626";


    // =========================================================
    // SHOW DASHBOARD
    // =========================================================

    public void show(Stage stage) {

        // ---- real data from the database ----
        com.healthcare.service.DoctorDashboardService.Summary summary = loadSummary();

        String specialityText =
                (summary.specialization == null || summary.specialization.isEmpty())
                        ? "Doctor" : summary.specialization;

        // =====================================================
        // TOP HEADER
        // =====================================================

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 25, 14, 25));
        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // Logo
        StackPane logoBox = new StackPane();

        Circle logoCircle = new Circle(23);
        logoCircle.setFill(Color.web(BLUE));

        Label logoCross = new Label("+");
        logoCross.setTextFill(Color.WHITE);
        logoCross.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        logoBox.getChildren().addAll(logoCircle, logoCross);

        Label brand = new Label("MediCare");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 21));
        brand.setTextFill(Color.web(NAVY));

        VBox brandBox = new VBox(1);

        Label brandSubtitle = new Label("Doctor Portal");
        brandSubtitle.setFont(Font.font("Segoe UI", 11));
        brandSubtitle.setTextFill(Color.web(MUTED));

        brandBox.getChildren().addAll(brand, brandSubtitle);

        HBox branding = new HBox(10, logoBox, brandBox);
        branding.setAlignment(Pos.CENTER_LEFT);

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search patients, appointments...");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(38);

        searchField.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 14;" +
                        "-fx-font-size: 13px;"
        );

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Notification
        Button notificationButton = new Button("🔔");
        notificationButton.setPrefSize(42, 38);

        notificationButton.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 16px;" +
                        "-fx-cursor: hand;"
        );

        // Doctor profile
        StackPane doctorAvatar = new StackPane();

        Circle avatarCircle = new Circle(20);
        avatarCircle.setFill(Color.web("#DCEEFF"));

        Label avatarText = new Label(UserSession.getInitials());
        avatarText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        avatarText.setTextFill(Color.web(BLUE));

        doctorAvatar.getChildren().addAll(avatarCircle, avatarText);

        VBox doctorInfo = new VBox(1);

        Label doctorName = new Label(UserSession.getFullName());
        doctorName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        doctorName.setTextFill(Color.web(TEXT));

        Label doctorRole = new Label(specialityText);
        doctorRole.setFont(Font.font("Segoe UI", 11));
        doctorRole.setTextFill(Color.web(MUTED));

        doctorInfo.getChildren().addAll(doctorName, doctorRole);

        header.getChildren().addAll(
                branding,
                searchField,
                headerSpacer,
                notificationButton,
                doctorAvatar,
                doctorInfo
        );


        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(245);
        sidebar.setPadding(new Insets(25, 15, 20, 15));

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );

        Label menuLabel = new Label("CLINICAL WORKSPACE");
        menuLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        menuLabel.setTextFill(Color.web("#8FB1D1"));
        menuLabel.setPadding(new Insets(0, 0, 8, 12));

        // Buttons
        Button dashboardButton =
                createSidebarButton("▣   Dashboard", true);

        Button scheduleButton =
                createSidebarButton("◷   My Schedule", false);

        Button appointmentsButton =
                createSidebarButton("▤   Appointments", false);

        Button patientRecordsButton =
                createSidebarButton("♙   Patient Records", false);

        Button feedbackButton =
                createSidebarButton("★   Patient Feedback", false);

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #315675;");

        Label toolsLabel = new Label("DOCTOR TOOLS");
        toolsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        toolsLabel.setTextFill(Color.web("#8FB1D1"));
        toolsLabel.setPadding(new Insets(10, 0, 5, 12));

        Button clinicalNotes =
                createSidebarButton("✎   Clinical Notes", false);

        Button reportsButton =
                createSidebarButton("▧   Reports", false);

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        // Online status
        HBox onlineBox = new HBox(8);
        onlineBox.setAlignment(Pos.CENTER_LEFT);
        onlineBox.setPadding(new Insets(10, 12, 10, 12));

        Circle onlineDot = new Circle(5);
        onlineDot.setFill(Color.web("#22C55E"));

        Label onlineText = new Label(summary.workingToday ? "Available for consultation" : "Off duty today");
        onlineText.setFont(Font.font("Segoe UI", 11));
        onlineText.setTextFill(Color.web("#D9E8F5"));

        onlineBox.getChildren().addAll(onlineDot, onlineText);

        Button logoutButton =
                createSidebarButton("⇥   Logout", false);

        sidebar.getChildren().addAll(
                menuLabel,
                dashboardButton,
                scheduleButton,
                appointmentsButton,
                patientRecordsButton,
                feedbackButton,
                separator,
                toolsLabel,
                clinicalNotes,
                reportsButton,
                sideSpacer,
                onlineBox,
                logoutButton
        );


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox mainContent = new VBox(22);
        mainContent.setPadding(new Insets(28));
        mainContent.setStyle(
                "-fx-background-color: " + BG + ";"
        );


        // =====================================================
        // WELCOME SECTION
        // =====================================================

        HBox welcomeSection = new HBox();

        VBox welcomeText = new VBox(5);

        Label welcomeTitle =
                new Label(greeting() + ", " + UserSession.getFullName() + " 👋");

        welcomeTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 27)
        );

        welcomeTitle.setTextFill(Color.web(TEXT));

        Label welcomeSubtitle =
                new Label(
                        "Here is your clinical overview for today, "
                                + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter
                                .ofPattern("d MMMM yyyy", java.util.Locale.ENGLISH))
                                + "."
                );

        welcomeSubtitle.setFont(Font.font("Segoe UI", 13));
        welcomeSubtitle.setTextFill(Color.web(MUTED));

        welcomeText.getChildren().addAll(
                welcomeTitle,
                welcomeSubtitle
        );

        Region welcomeSpacer = new Region();
        HBox.setHgrow(welcomeSpacer, Priority.ALWAYS);

        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER_RIGHT);

        Label statusTitle =
                new Label("CONSULTATION STATUS");

        statusTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 10)
        );

        statusTitle.setTextFill(Color.web(MUTED));

        Label status =
                new Label(summary.workingToday ? "●  AVAILABLE" : "●  NOT WORKING TODAY");

        status.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        status.setTextFill(Color.web(summary.workingToday ? GREEN : MUTED));

        statusBox.getChildren().addAll(
                statusTitle,
                status
        );

        welcomeSection.getChildren().addAll(
                welcomeText,
                welcomeSpacer,
                statusBox
        );


        // =====================================================
        // STAT CARDS
        // =====================================================

        HBox statsRow = new HBox(15);

        VBox patientsStat = createStatCard(
                "TOTAL PATIENTS",
                String.valueOf(summary.totalPatients),
                "↑ " + summary.newPatientsThisMonth + " new this month",
                "♙",
                BLUE
        );

        VBox appointmentsStat = createStatCard(
                "TODAY'S APPOINTMENTS",
                String.format("%02d", summary.todayTotal),
                summary.todayPending + " pending confirmation",
                "▤",
                "#7C3AED"
        );

        VBox pendingStat = createStatCard(
                "PENDING REQUESTS",
                String.format("%02d", summary.pendingAll),
                summary.pendingAll == 0 ? "All caught up" : "Requires your attention",
                "◷",
                ORANGE
        );

        VBox completedStat = createStatCard(
                "COMPLETED TODAY",
                String.format("%02d", summary.completedToday),
                summary.todayTotal == 0 ? "No appointments today"
                        : (summary.completedToday * 100 / summary.todayTotal)
                        + "% of today's schedule",
                "✓",
                GREEN
        );

        statsRow.getChildren().addAll(
                patientsStat,
                appointmentsStat,
                pendingStat,
                completedStat
        );


        // =====================================================
        // SECTION TITLE
        // =====================================================

        Label scheduleTitle =
                new Label("Today's Clinical Schedule");

        scheduleTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        scheduleTitle.setTextFill(Color.web(TEXT));


        // =====================================================
        // TODAY'S SCHEDULE TABLE
        // =====================================================

        TableView<AppointmentData> scheduleTable =
                new TableView<>();

        scheduleTable.setPrefHeight(280);
        scheduleTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        scheduleTable.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );

        TableColumn<AppointmentData, String> timeCol =
                new TableColumn<>("TIME");

        TableColumn<AppointmentData, String> patientCol =
                new TableColumn<>("PATIENT");

        TableColumn<AppointmentData, String> typeCol =
                new TableColumn<>("CONSULTATION");

        TableColumn<AppointmentData, String> statusCol =
                new TableColumn<>("STATUS");

        TableColumn<AppointmentData, String> actionCol =
                new TableColumn<>("ACTION");

        timeCol.setCellValueFactory(
                data -> data.getValue().time
        );

        patientCol.setCellValueFactory(
                data -> data.getValue().patient
        );

        typeCol.setCellValueFactory(
                data -> data.getValue().type
        );

        statusCol.setCellValueFactory(
                data -> data.getValue().status
        );

        actionCol.setCellValueFactory(
                data -> data.getValue().action
        );

        scheduleTable.getColumns().addAll(
                timeCol,
                patientCol,
                typeCol,
                statusCol,
                actionCol
        );

        for (com.healthcare.model.PatientAppointment a : summary.today) {
            scheduleTable.getItems().add(
                    new AppointmentData(
                            a.getTimeDisplay(),
                            a.getPatientName(),
                            a.getReason().isEmpty() ? "Consultation" : a.getReason(),
                            a.getStatus(),
                            "Pending".equals(a.getStatus()) ? "Review" : "View"
                    )
            );
        }

        scheduleTable.setPlaceholder(new Label("No appointments scheduled for today."));


        // =====================================================
        // LOWER SECTION
        // =====================================================

        HBox lowerSection = new HBox(20);


        // =====================================================
        // PATIENT QUEUE
        // =====================================================

        VBox queueCard =
                createWhiteCard();

        queueCard.setPrefWidth(520);

        Label queueTitle =
                new Label("Patient Queue");

        queueTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 17)
        );

        queueTitle.setTextFill(Color.web(TEXT));

        Label queueSubtitle =
                new Label("Patients waiting for consultation");

        queueSubtitle.setFont(Font.font("Segoe UI", 12));
        queueSubtitle.setTextFill(Color.web(MUTED));

        VBox queueList = new VBox(10);

        int shown = 0;
        for (com.healthcare.model.PatientAppointment a : summary.queue) {
            if (shown == 3) {
                break;
            }
            queueList.getChildren().add(
                    createPatientQueue(
                            a.getPatientName().substring(0, 1).toUpperCase(),
                            a.getPatientName(),
                            a.getReason().isEmpty() ? "Consultation" : a.getReason(),
                            shown == 0 ? "Next Patient" : "At " + a.getTimeDisplay()
                    )
            );
            shown++;
        }

        if (queueList.getChildren().isEmpty()) {
            Label emptyQueue = new Label("No confirmed patients waiting right now.");
            emptyQueue.setFont(Font.font("Segoe UI", 12));
            emptyQueue.setTextFill(Color.web(MUTED));
            queueList.getChildren().add(emptyQueue);
        }

        Button viewQueue =
                new Button("View Full Patient Queue →");

        viewQueue.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + BLUE + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        queueCard.getChildren().addAll(
                queueTitle,
                queueSubtitle,
                queueList,
                viewQueue
        );


        // =====================================================
        // ALERTS CARD
        // =====================================================

        VBox alertsCard =
                createWhiteCard();

        alertsCard.setPrefWidth(420);

        Label alertsTitle =
                new Label("Clinical Alerts");

        alertsTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 17)
        );

        alertsTitle.setTextFill(Color.web(TEXT));

        Label alertsSubtitle =
                new Label("Things that need your attention");

        alertsSubtitle.setFont(Font.font("Segoe UI", 12));
        alertsSubtitle.setTextFill(Color.web(MUTED));

        VBox alertsList = new VBox(12);

        alertsList.getChildren().addAll(
                createAlert(
                        String.valueOf(summary.pendingAll),
                        summary.pendingAll == 0
                                ? "No pending appointment requests"
                                : "Pending appointment requests",
                        "Review and confirm appointments",
                        ORANGE
                ),
                createAlert(
                        String.valueOf(summary.tomorrowCount),
                        "Appointments tomorrow",
                        summary.tomorrowCount == 0
                                ? "Nothing scheduled yet"
                                : "Get ready for tomorrow's consultations",
                        BLUE
                ),
                createAlert(
                        "✓",
                        summary.completedToday + " consultations completed",
                        "Today's completed consultations",
                        GREEN
                )
        );

        alertsCard.getChildren().addAll(
                alertsTitle,
                alertsSubtitle,
                alertsList
        );

        lowerSection.getChildren().addAll(
                queueCard,
                alertsCard
        );


        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        Label quickTitle =
                new Label("Quick Clinical Actions");

        quickTitle.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        quickTitle.setTextFill(Color.web(TEXT));

        HBox quickActions = new HBox(15);

        Button viewAppointments =
                createQuickAction(
                        "▤",
                        "Appointments",
                        "Manage today's visits"
                );

        Button viewPatients =
                createQuickAction(
                        "♙",
                        "Patient Records",
                        "Access medical records"
                );

        Button manageSchedule =
                createQuickAction(
                        "◷",
                        "Manage Schedule",
                        "Update availability"
                );

        Button feedback =
                createQuickAction(
                        "★",
                        "Patient Feedback",
                        "Review patient feedback"
                );

        quickActions.getChildren().addAll(
                viewAppointments,
                viewPatients,
                manageSchedule,
                feedback
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        mainContent.getChildren().addAll(
                welcomeSection,
                statsRow,
                scheduleTitle,
                scheduleTable,
                lowerSection,
                quickTitle,
                quickActions
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(mainContent);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: " + BG + ";" +
                        "-fx-border-color: transparent;"
        );


        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        scheduleButton.setOnAction(event -> {

            DoctorScheduleView scheduleView =
                    new DoctorScheduleView();

            scheduleView.show(stage);
        });


        appointmentsButton.setOnAction(event -> {

            DoctorAppointmentsView appointmentsView =
                    new DoctorAppointmentsView();

            appointmentsView.show(stage);
        });


        patientRecordsButton.setOnAction(event -> {

            DoctorPatientRecordsView recordsView =
                    new DoctorPatientRecordsView();

            recordsView.show(stage);
        });


        feedbackButton.setOnAction(event -> {

            DoctorFeedbackView feedbackView =
                    new DoctorFeedbackView();

            feedbackView.show(stage);
        });


        // Quick action buttons

        viewAppointments.setOnAction(event -> {

            DoctorAppointmentsView appointmentsView =
                    new DoctorAppointmentsView();

            appointmentsView.show(stage);
        });


        viewQueue.setOnAction(event ->
                new DoctorAppointmentsView().show(stage)
        );


        viewPatients.setOnAction(event -> {

            DoctorPatientRecordsView recordsView =
                    new DoctorPatientRecordsView();

            recordsView.show(stage);
        });


        manageSchedule.setOnAction(event -> {

            DoctorScheduleView scheduleView =
                    new DoctorScheduleView();

            scheduleView.show(stage);
        });


        feedback.setOnAction(event -> {

            DoctorFeedbackView feedbackView =
                    new DoctorFeedbackView();

            feedbackView.show(stage);
        });


        // =====================================================
        // NOTIFICATION
        // =====================================================

        notificationButton.setOnAction(event -> {

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Notifications");
            alert.setHeaderText("Doctor Notifications");

            alert.setContentText(
                    "• 3 appointment requests are pending.\n\n" +
                            "• Rahul Sharma's medical record requires an update.\n\n" +
                            "• You have 5 completed consultations today."
            );

            alert.showAndWait();
        });


        // =====================================================
        // LOGOUT
        // =====================================================

        logoutButton.setOnAction(event -> {

            Alert confirm =
                    new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to logout?",
                            ButtonType.YES,
                            ButtonType.NO
                    );

            confirm.setTitle("Logout");
            confirm.setHeaderText("Doctor Logout");

            confirm.showAndWait().ifPresent(response -> {

                if (response == ButtonType.YES) {

                    Main main = new Main();
                    main.start(stage);
                }
            });
        });


        // =====================================================
        // OTHER TOOLS
        // =====================================================

        clinicalNotes.setOnAction(event -> {

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Clinical Notes");
            alert.setHeaderText("Clinical Notes");

            alert.setContentText(
                    "Clinical notes module will be connected " +
                            "to patient records during backend integration."
            );

            alert.showAndWait();
        });


        reportsButton.setOnAction(event -> {

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Reports");
            alert.setHeaderText("Doctor Reports");

            alert.setContentText(
                    "Reports and analytics will be connected " +
                            "to the database during backend integration."
            );

            alert.showAndWait();
        });


        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root =
                new BorderPane();

        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(scrollPane);

        root.setStyle(
                "-fx-background-color: " + BG + ";"
        );


        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(root, 1450, 850);

        stage.setTitle(
                "Doctor Portal - MediCare"
        );

        stage.setScene(scene);
        stage.show();
    }


    // =========================================================
    // SIDEBAR BUTTON
    // =========================================================

    private Button createSidebarButton(
            String text,
            boolean active
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(44);

        button.setAlignment(Pos.CENTER_LEFT);

        button.setPadding(
                new Insets(0, 15, 0, 15)
        );

        if (active) {

            button.setStyle(
                    "-fx-background-color: #1E5A8A;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #D9E8F5;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-size: 13px;" +
                            "-fx-cursor: hand;"
            );
        }

        return button;
    }


    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox createStatCard(
            String title,
            String value,
            String description,
            String icon,
            String iconColor
    ) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setPrefHeight(145);

        HBox top =
                new HBox();

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 11)
        );

        titleLabel.setTextFill(Color.web(MUTED));

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        StackPane iconBox =
                new StackPane();

        Circle circle =
                new Circle(19);

        circle.setFill(
                Color.web(iconColor + "22")
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 15)
        );

        iconLabel.setTextFill(
                Color.web(iconColor)
        );

        iconBox.getChildren().addAll(
                circle,
                iconLabel
        );

        top.getChildren().addAll(
                titleLabel,
                spacer,
                iconBox
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 28)
        );

        valueLabel.setTextFill(Color.web(TEXT));

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setFont(
                Font.font("Segoe UI", 11)
        );

        descriptionLabel.setTextFill(
                Color.web(MUTED)
        );

        card.getChildren().addAll(
                top,
                valueLabel,
                descriptionLabel
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;"
        );

        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }


    // =========================================================
    // WHITE CARD
    // =========================================================

    private VBox createWhiteCard() {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(20)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;"
        );

        return card;
    }


    // =========================================================
    // PATIENT QUEUE
    // =========================================================

    private HBox createPatientQueue(
            String initial,
            String name,
            String consultation,
            String status
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(
                new Insets(10)
        );

        row.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-background-radius: 8;"
        );

        StackPane avatar =
                new StackPane();

        Circle circle =
                new Circle(20);

        circle.setFill(
                Color.web("#DCEEFF")
        );

        Label initialLabel =
                new Label(initial);

        initialLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        initialLabel.setTextFill(
                Color.web(BLUE)
        );

        avatar.getChildren().addAll(
                circle,
                initialLabel
        );

        VBox patientInfo =
                new VBox(2);

        Label nameLabel =
                new Label(name);

        nameLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        nameLabel.setTextFill(
                Color.web(TEXT)
        );

        Label consultationLabel =
                new Label(consultation);

        consultationLabel.setFont(
                Font.font("Segoe UI", 11)
        );

        consultationLabel.setTextFill(
                Color.web(MUTED)
        );

        patientInfo.getChildren().addAll(
                nameLabel,
                consultationLabel
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Label statusLabel =
                new Label(status);

        statusLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        10
                )
        );

        statusLabel.setTextFill(
                Color.web(BLUE)
        );

        row.getChildren().addAll(
                avatar,
                patientInfo,
                spacer,
                statusLabel
        );

        return row;
    }


    // =========================================================
    // ALERT
    // =========================================================

    private HBox createAlert(
            String icon,
            String title,
            String description,
            String color
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(Pos.CENTER_LEFT);

        StackPane iconBox =
                new StackPane();

        Circle circle =
                new Circle(17);

        circle.setFill(
                Color.web(color + "22")
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        iconLabel.setTextFill(
                Color.web(color)
        );

        iconBox.getChildren().addAll(
                circle,
                iconLabel
        );

        VBox textBox =
                new VBox(2);

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        titleLabel.setTextFill(
                Color.web(TEXT)
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setFont(
                Font.font("Segoe UI", 10)
        );

        descriptionLabel.setTextFill(
                Color.web(MUTED)
        );

        textBox.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        row.getChildren().addAll(
                iconBox,
                textBox
        );

        return row;
    }


    // =========================================================
    // QUICK ACTION
    // =========================================================

    private Button createQuickAction(
            String icon,
            String title,
            String description
    ) {

        Button button =
                new Button();

        button.setPrefHeight(82);
        button.setPrefWidth(230);

        VBox content =
                new VBox(4);

        content.setAlignment(
                Pos.CENTER_LEFT
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        20
                )
        );

        iconLabel.setTextFill(
                Color.web(BLUE)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        titleLabel.setTextFill(
                Color.web(TEXT)
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setFont(
                Font.font("Segoe UI", 10)
        );

        descriptionLabel.setTextFill(
                Color.web(MUTED)
        );

        content.getChildren().addAll(
                iconLabel,
                titleLabel,
                descriptionLabel
        );

        button.setGraphic(content);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12;" +
                        "-fx-cursor: hand;"
        );

        return button;
    }


    // =========================================================
    // APPOINTMENT DATA
    // =========================================================

    // =========================================================
    // DATA / HELPERS
    // =========================================================

    /** Loads the numbers shown on this screen. On a database error an empty summary is used. */
    private com.healthcare.service.DoctorDashboardService.Summary loadSummary() {
        try {
            return new com.healthcare.service.DoctorDashboardService()
                    .load(UserSession.getUserId());
        } catch (java.sql.SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error");
            alert.setHeaderText("Could not load your dashboard");
            alert.setContentText(e.getMessage());
            alert.show();

            return new com.healthcare.service.DoctorDashboardService.Summary();
        }
    }

    private String greeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) {
            return "Good Morning";
        }
        return hour < 17 ? "Good Afternoon" : "Good Evening";
    }

    private static class AppointmentData {

        javafx.beans.property.SimpleStringProperty time;
        javafx.beans.property.SimpleStringProperty patient;
        javafx.beans.property.SimpleStringProperty type;
        javafx.beans.property.SimpleStringProperty status;
        javafx.beans.property.SimpleStringProperty action;

        AppointmentData(
                String time,
                String patient,
                String type,
                String status,
                String action
        ) {

            this.time =
                    new javafx.beans.property.SimpleStringProperty(time);

            this.patient =
                    new javafx.beans.property.SimpleStringProperty(patient);

            this.type =
                    new javafx.beans.property.SimpleStringProperty(type);

            this.status =
                    new javafx.beans.property.SimpleStringProperty(status);

            this.action =
                    new javafx.beans.property.SimpleStringProperty(action);
        }
    }
}