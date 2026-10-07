package com.healthcare;

import com.healthcare.model.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

public class MyAppointmentsView {

    private final String NAVY = "#12355b";
    private final String BLUE = "#2693df";
    private final String BG = "#f4f8fc";
    private final String TEXT = "#163b63";
    private final String MUTED = "#64748b";

    public void show(Stage stage) {

        // used to refresh the screen after cancelling an appointment
        Runnable[] reload = new Runnable[1];
        Appointment[] upcoming = new Appointment[1];

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

        Label initials = new Label(UserSession.getInitials());
        initials.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        initials.setTextFill(Color.web("#1976d2"));

        StackPane profileIcon = new StackPane(
                profileCircle,
                initials
        );

        VBox profileInfo = new VBox(2);

        Label patientName = new Label(UserSession.getFullName());
        patientName.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                13
        ));
        patientName.setTextFill(Color.WHITE);

        Label patientRole = new Label("Patient  •  Online");
        patientRole.setFont(Font.font("Segoe UI", 10));
        patientRole.setTextFill(Color.web("#c7dced"));

        profileInfo.getChildren().addAll(
                patientName,
                patientRole
        );

        profile.getChildren().addAll(
                profileIcon,
                profileInfo
        );

        // Navigation
        Button dashboardBtn = navButton(
                "⌂",
                "Dashboard",
                false
        );

        Button bookBtn = navButton(
                "▣",
                "Book Appointment",
                false
        );

        Button appointmentsBtn = navButton(
                "▤",
                "My Appointments",
                true
        );

        Button historyBtn = navButton(
                "♥",
                "Medical History",
                false
        );

        Button profileBtn = navButton(
                "♙",
                "My Profile",
                false
        );

        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        Button logoutBtn = navButton(
                "↪",
                "Logout",
                false
        );

        sidebar.getChildren().addAll(
                logo,
                profile,
                createHeight(15),
                dashboardBtn,
                bookBtn,
                appointmentsBtn,
                historyBtn,
                profileBtn,
                sidebarSpacer,
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

        VBox pageHeading = new VBox(2);

        Label pageTitle = new Label("My Appointments");
        pageTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                22
        ));
        pageTitle.setTextFill(Color.web(TEXT));

        Label pageSubtitle = new Label(
                "View and manage your healthcare appointments"
        );
        pageSubtitle.setFont(Font.font("Segoe UI", 11));
        pageSubtitle.setTextFill(Color.web(MUTED));

        pageHeading.getChildren().addAll(
                pageTitle,
                pageSubtitle
        );

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        Label notification = new Label("🔔");
        notification.setFont(Font.font(17));

        Label date = new Label("26 September 2026");
        date.setFont(Font.font("Segoe UI", 12));
        date.setTextFill(Color.web(MUTED));

        topBar.getChildren().addAll(
                pageHeading,
                topSpacer,
                notification,
                createWidth(20),
                date
        );

        // =========================================================
        // CONTENT
        // =========================================================

        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        // =========================================================
        // WELCOME / SUMMARY BANNER
        // =========================================================

        HBox banner = new HBox();
        banner.setPadding(new Insets(22, 25, 22, 25));
        banner.setPrefHeight(130);

        banner.setStyle(
                "-fx-background-color: linear-gradient(to right, #e2f4ff, #f1f9ff);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #d2eaf9;" +
                        "-fx-border-radius: 16;"
        );

        VBox bannerText = new VBox(6);

        Label bannerTitle = new Label(
                "Your Appointments"
        );

        bannerTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                25
        ));

        bannerTitle.setTextFill(Color.web(TEXT));

        Label bannerSubtitle = new Label(
                "Stay organized and never miss your next consultation."
        );

        bannerSubtitle.setFont(Font.font(
                "Segoe UI",
                13
        ));

        bannerSubtitle.setTextFill(Color.web("#4d6d89"));

        bannerText.getChildren().addAll(
                bannerTitle,
                bannerSubtitle
        );

        Region bannerSpacer = new Region();
        HBox.setHgrow(
                bannerSpacer,
                Priority.ALWAYS
        );

        VBox bannerStats = new VBox(3);
        bannerStats.setAlignment(Pos.CENTER);

        Label appointmentCount = new Label("3");
        appointmentCount.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                30
        ));
        appointmentCount.setTextFill(Color.web(BLUE));

        Label appointmentCountText = new Label(
                "Total Appointments"
        );

        appointmentCountText.setFont(Font.font(
                "Segoe UI",
                11
        ));

        appointmentCountText.setTextFill(
                Color.web(MUTED)
        );

        bannerStats.getChildren().addAll(
                appointmentCount,
                appointmentCountText
        );

        banner.getChildren().addAll(
                bannerText,
                bannerSpacer,
                bannerStats
        );

        // =========================================================
        // FILTER / SEARCH
        // =========================================================

        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText(
                "Search doctor or appointment..."
        );
        searchField.setPrefHeight(42);
        searchField.setPrefWidth(350);

        searchField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dce5ef;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-padding: 0 14;" +
                        "-fx-font-size: 13;"
        );

        ComboBox<String> statusFilter = new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Appointments",
                "Confirmed",
                "Pending",
                "Completed",
                "Cancelled"
        );

        statusFilter.setValue(
                "All Appointments"
        );

        statusFilter.setPrefHeight(42);
        statusFilter.setPrefWidth(180);

        statusFilter.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dce5ef;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-font-size: 13;"
        );

        Region filterSpacer = new Region();
        HBox.setHgrow(
                filterSpacer,
                Priority.ALWAYS
        );

        Button refreshButton = new Button("⟳  Refresh");
        refreshButton.setPrefHeight(42);

        refreshButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #42617e;" +
                        "-fx-border-color: #dce5ef;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-font-weight: bold;"
        );

        filterBar.getChildren().addAll(
                searchField,
                statusFilter,
                filterSpacer,
                refreshButton
        );

        // =========================================================
        // UPCOMING APPOINTMENT
        // =========================================================

        VBox upcomingCard = new VBox(15);
        upcomingCard.setPadding(new Insets(20));

        upcomingCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #dce8f1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(30,70,100,0.08), 12, 0.1, 0, 3" +
                        ");"
        );

        HBox upcomingHeader = new HBox();

        VBox upcomingHeading = new VBox(2);

        Label upcomingTitle = new Label(
                "Upcoming Appointment"
        );

        upcomingTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                18
        ));

        upcomingTitle.setTextFill(
                Color.web(TEXT)
        );

        Label upcomingSubtitle = new Label(
                "Your next scheduled consultation"
        );

        upcomingSubtitle.setFont(Font.font(
                "Segoe UI",
                11
        ));

        upcomingSubtitle.setTextFill(
                Color.web(MUTED)
        );

        upcomingHeading.getChildren().addAll(
                upcomingTitle,
                upcomingSubtitle
        );

        Region upcomingSpacer = new Region();
        HBox.setHgrow(
                upcomingSpacer,
                Priority.ALWAYS
        );

        Label confirmedBadge = statusBadge(
                "●  CONFIRMED",
                "#e5f8ef",
                "#087f5b"
        );

        upcomingHeader.getChildren().addAll(
                upcomingHeading,
                upcomingSpacer,
                confirmedBadge
        );

        // Appointment information
        HBox appointmentInfo = new HBox(20);
        appointmentInfo.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane doctorAvatar = new StackPane();

        Circle doctorCircle = new Circle(
                35,
                Color.web("#e3f3ff")
        );

        Label doctorInitials = new Label("DP");
        doctorInitials.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                15
        ));

        doctorInitials.setTextFill(
                Color.web(BLUE)
        );

        doctorAvatar.getChildren().addAll(
                doctorCircle,
                doctorInitials
        );

        VBox doctorDetails = new VBox(4);

        Label doctorName = new Label(
                "Dr. Priya"
        );

        doctorName.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                17
        ));

        doctorName.setTextFill(
                Color.web(TEXT)
        );

        Label specialization = new Label(
                "Cardiologist"
        );

        specialization.setFont(Font.font(
                "Segoe UI",
                12
        ));

        specialization.setTextFill(
                Color.web(MUTED)
        );

        Label rating = new Label(
                "★ 4.9   •   128 Reviews"
        );

        rating.setFont(Font.font(
                "Segoe UI",
                11
        ));

        rating.setTextFill(
                Color.web("#f59e0b")
        );

        doctorDetails.getChildren().addAll(
                doctorName,
                specialization,
                rating
        );

        appointmentInfo.getChildren().addAll(
                doctorAvatar,
                doctorDetails
        );

        Region infoSpacer = new Region();
        HBox.setHgrow(
                infoSpacer,
                Priority.ALWAYS
        );

        VBox dateInfo = infoBlock(
                "DATE",
                "25 September 2026"
        );

        VBox timeInfo = infoBlock(
                "TIME",
                "10:30 AM"
        );

        VBox durationInfo = infoBlock(
                "DURATION",
                "30 Minutes"
        );

        appointmentInfo.getChildren().addAll(
                infoSpacer,
                dateInfo,
                createWidth(20),
                timeInfo,
                createWidth(20),
                durationInfo
        );

        Separator upcomingSeparator =
                new Separator();

        HBox upcomingActions = new HBox(10);

        Button viewDetailsButton =
                new Button("View Details");

        viewDetailsButton.setPrefHeight(40);

        viewDetailsButton.setStyle(
                "-fx-background-color: #e9f5ff;" +
                        "-fx-text-fill: #1976d2;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 18;"
        );

        Button cancelUpcomingButton =
                new Button("Cancel Appointment");

        cancelUpcomingButton.setPrefHeight(40);

        cancelUpcomingButton.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #dc3545;" +
                        "-fx-border-color: #f0b8bd;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 18;"
        );

        upcomingActions.getChildren().addAll(
                viewDetailsButton,
                cancelUpcomingButton
        );

        upcomingCard.getChildren().addAll(
                upcomingHeader,
                appointmentInfo,
                upcomingSeparator,
                upcomingActions
        );

        // =========================================================
        // APPOINTMENT HISTORY
        // =========================================================

        VBox historyCard = new VBox(15);
        historyCard.setPadding(new Insets(20));

        historyCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #e1e8ef;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(30,70,100,0.07), 12, 0.1, 0, 3" +
                        ");"
        );

        HBox historyHeader = new HBox();

        VBox historyTitleBox = new VBox(2);

        Label historyTitle = new Label(
                "Appointment History"
        );

        historyTitle.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                18
        ));

        historyTitle.setTextFill(
                Color.web(TEXT)
        );

        Label historySubtitle = new Label(
                "Your recent consultations"
        );

        historySubtitle.setFont(Font.font(
                "Segoe UI",
                11
        ));

        historySubtitle.setTextFill(
                Color.web(MUTED)
        );

        historyTitleBox.getChildren().addAll(
                historyTitle,
                historySubtitle
        );

        Region historySpacer = new Region();
        HBox.setHgrow(
                historySpacer,
                Priority.ALWAYS
        );

        Label recordsCount = new Label(
                "3 Records"
        );

        recordsCount.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                11
        ));

        recordsCount.setTextFill(
                Color.web(BLUE)
        );

        historyHeader.getChildren().addAll(
                historyTitleBox,
                historySpacer,
                recordsCount
        );

        // =========================================================
        // TABLE
        // =========================================================

        TableView<Appointment> table =
                new TableView<>();

        table.setPrefHeight(240);
        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5ebf1;" +
                        "-fx-border-radius: 8;"
        );

        // Doctor
        TableColumn<Appointment, String>
                doctorColumn =
                new TableColumn<>("Doctor");

        doctorColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .doctorProperty()
        );

        // Date
        TableColumn<Appointment, String>
                dateColumn =
                new TableColumn<>("Date");

        dateColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .dateProperty()
        );

        // Time
        TableColumn<Appointment, String>
                timeColumn =
                new TableColumn<>("Time");

        timeColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .timeProperty()
        );

        // Status
        TableColumn<Appointment, String>
                statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .statusProperty()
        );

        // Custom status rendering
        statusColumn.setCellFactory(column ->
                new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String status,
                            boolean empty
                    ) {

                        super.updateItem(
                                status,
                                empty
                        );

                        if (empty || status == null) {

                            setGraphic(null);
                            setText(null);

                        } else {

                            Label badge;

                            if (status.equalsIgnoreCase(
                                    "Confirmed"
                            )) {

                                badge = statusBadge(
                                        "● Confirmed",
                                        "#e5f8ef",
                                        "#087f5b"
                                );

                            } else if (
                                    status.equalsIgnoreCase(
                                            "Pending"
                                    )
                            ) {

                                badge = statusBadge(
                                        "● Pending",
                                        "#fff3df",
                                        "#b76b00"
                                );

                            } else if (
                                    status.equalsIgnoreCase(
                                            "Completed"
                                    )
                            ) {

                                badge = statusBadge(
                                        "✓ Completed",
                                        "#e9f0ff",
                                        "#4267a8"
                                );

                            } else {

                                badge = statusBadge(
                                        "● Cancelled",
                                        "#ffe8e8",
                                        "#c62828"
                                );
                            }

                            setGraphic(badge);
                            setText(null);
                            setAlignment(Pos.CENTER_LEFT);
                        }
                    }
                }
        );

        // Action column
        TableColumn<Appointment, Void>
                actionColumn =
                new TableColumn<>("Action");

        actionColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button view =
                            new Button("View");

                    private final Button cancel =
                            new Button("Cancel");

                    private final Button rate =
                            new Button("Rate");

                    private final HBox actionBox =
                            new HBox(6);

                    {

                        cancel.setPrefHeight(28);

                        cancel.setStyle(
                                "-fx-background-color: #fff1f2;" +
                                        "-fx-text-fill: #dc3545;" +
                                        "-fx-background-radius: 6;" +
                                        "-fx-font-size: 11;" +
                                        "-fx-font-weight: bold;"
                        );

                        rate.setPrefHeight(28);

                        rate.setStyle(
                                "-fx-background-color: #fff7e6;" +
                                        "-fx-text-fill: #d97706;" +
                                        "-fx-background-radius: 6;" +
                                        "-fx-font-size: 11;" +
                                        "-fx-font-weight: bold;"
                        );

                        rate.setOnAction(event -> {

                            Appointment appointment =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            rateAppointment(
                                    appointment,
                                    reload[0]
                            );
                        });

                        cancel.setOnAction(event -> {

                            Appointment appointment =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            confirmAndCancel(
                                    appointment,
                                    reload[0]
                            );
                        });

                        actionBox.getChildren().addAll(
                                view,
                                cancel,
                                rate
                        );


                        view.setPrefHeight(28);

                        view.setStyle(
                                "-fx-background-color: #eef7ff;" +
                                        "-fx-text-fill: #1976d2;" +
                                        "-fx-background-radius: 6;" +
                                        "-fx-font-size: 11;" +
                                        "-fx-font-weight: bold;"
                        );

                        view.setOnAction(event -> {

                            Appointment appointment =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            showDetails(
                                    appointment
                            );
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty
                                || getIndex() < 0
                                || getIndex() >= getTableView()
                                .getItems().size()) {

                            setGraphic(null);

                        } else {

                            Appointment row =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            String rowStatus =
                                    row.statusProperty().get();

                            boolean canCancel =
                                    rowStatus.equals("Pending")
                                            || rowStatus.equals("Confirmed");

                            cancel.setVisible(canCancel);
                            cancel.setManaged(canCancel);

                            boolean canRate =
                                    rowStatus.equals("Completed")
                                            && row.getRecord() != null
                                            && !row.getRecord().isRated();

                            rate.setVisible(canRate);
                            rate.setManaged(canRate);

                            setGraphic(actionBox);
                        }
                    }
                }
        );

        table.getColumns().addAll(
                doctorColumn,
                dateColumn,
                timeColumn,
                statusColumn,
                actionColumn
        );

        // (table rows are loaded from the database - see reload below)

        historyCard.getChildren().addAll(
                historyHeader,
                table
        );

        // =========================================================
        // CONTENT ADD
        // =========================================================

        content.getChildren().addAll(
                banner,
                filterBar,
                upcomingCard,
                historyCard
        );

        // =========================================================
        // SEARCH
        // =========================================================

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    String search =
                            newValue.trim()
                                    .toLowerCase();

                    table.setItems(
                            javafx.collections.FXCollections
                                    .observableArrayList(
                                            getFilteredAppointments(
                                                    search,
                                                    statusFilter
                                                            .getValue()
                                            )
                                    )
                    );
                }
        );

        // =========================================================
        // STATUS FILTER
        // =========================================================

        statusFilter.setOnAction(event -> {

            String search =
                    searchField.getText()
                            .trim()
                            .toLowerCase();

            table.setItems(
                    javafx.collections.FXCollections
                            .observableArrayList(
                                    getFilteredAppointments(
                                            search,
                                            statusFilter.getValue()
                                    )
                            )
            );
        });

        // =========================================================
        // REFRESH
        // =========================================================

        refreshButton.setOnAction(event -> {

            searchField.clear();

            statusFilter.setValue(
                    "All Appointments"
            );

            reload[0].run();
        });

        // =========================================================
        // UPCOMING DETAILS
        // =========================================================

        viewDetailsButton.setOnAction(event -> {

            if (upcoming[0] != null) {
                showDetails(upcoming[0]);
            }
        });

        // =========================================================
        // CANCEL UPCOMING
        // =========================================================

        cancelUpcomingButton.setOnAction(event -> {

            confirmAndCancel(upcoming[0], reload[0]);
        });

        // =========================================================
        // LOAD EVERYTHING FROM THE DATABASE
        // =========================================================

        reload[0] = () -> {

            java.util.List<Appointment> everyAppointment =
                    loadAppointments();

            table.setItems(
                    javafx.collections.FXCollections
                            .observableArrayList(
                                    getFilteredAppointments(
                                            searchField.getText()
                                                    .trim()
                                                    .toLowerCase(),
                                            statusFilter.getValue()
                                    )
                            )
            );

            appointmentCount.setText(
                    String.valueOf(everyAppointment.size())
            );

            recordsCount.setText(
                    everyAppointment.size() + " Records"
            );

            Appointment nextOne = findUpcoming(everyAppointment);
            upcoming[0] = nextOne;

            if (nextOne == null) {

                doctorInitials.setText("--");
                doctorName.setText("No upcoming appointment");
                specialization.setText(
                        "Use Book Appointment to schedule one"
                );
                rating.setText("");
                ((Label) dateInfo.getChildren().get(1)).setText("-");
                ((Label) timeInfo.getChildren().get(1)).setText("-");
                confirmedBadge.setText("");
                viewDetailsButton.setDisable(true);
                cancelUpcomingButton.setDisable(true);

            } else {

                com.healthcare.model.AppointmentRecord rec =
                        nextOne.getRecord();

                doctorInitials.setText(
                        initialsOf(rec.getDoctorName())
                );
                doctorName.setText(rec.getDoctorName());
                specialization.setText(rec.getSpecialization());
                rating.setText("");
                ((Label) dateInfo.getChildren().get(1))
                        .setText(rec.getLongDate());
                ((Label) timeInfo.getChildren().get(1))
                        .setText(rec.getTimeDisplay());
                confirmedBadge.setText(
                        "●  " + rec.getStatus().toUpperCase()
                );
                viewDetailsButton.setDisable(false);
                cancelUpcomingButton.setDisable(false);
            }
        };

        reload[0].run();

        // =========================================================
        // NAVIGATION
        // =========================================================

        dashboardBtn.setOnAction(event -> {

            PatientDashboard dashboard =
                    new PatientDashboard();

            dashboard.show(stage);
        });

        bookBtn.setOnAction(event -> {

            BookAppointmentView view =
                    new BookAppointmentView();

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
        // SCROLL
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

        // =========================================================
        // ROOT
        // =========================================================

        BorderPane root =
                new BorderPane();

        root.setLeft(sidebar);
        root.setTop(topBar);
        root.setCenter(scrollPane);

        Scene scene =
                new Scene(
                        root,
                        1400,
                        820
                );

        stage.setTitle(
                "MediCare - My Appointments"
        );

        stage.setScene(scene);

        stage.setMinWidth(1200);
        stage.setMinHeight(750);

        stage.show();
    }

    // =============================================================
    // DATABASE HELPERS
    // =============================================================

    private java.util.List<Appointment> loadAppointments() {

        java.util.List<Appointment> list =
                new java.util.ArrayList<>();

        try {
            for (com.healthcare.model.AppointmentRecord record :
                    new com.healthcare.dao.AppointmentDAO()
                            .getPatientAppointments(UserSession.getUserId())) {

                list.add(new Appointment(record));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // nearest Pending/Confirmed appointment from today onwards
    private Appointment findUpcoming(
            java.util.List<Appointment> all
    ) {

        Appointment best = null;
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Appointment a : all) {

            com.healthcare.model.AppointmentRecord r = a.getRecord();

            if (r == null) {
                continue;
            }

            boolean active = r.getStatus().equals("Pending")
                    || r.getStatus().equals("Confirmed");

            if (!active || r.getDate().isBefore(today)) {
                continue;
            }

            if (best == null) {
                best = a;
                continue;
            }

            java.time.LocalDateTime thisOne =
                    java.time.LocalDateTime.of(r.getDate(), r.getTime());

            java.time.LocalDateTime bestOne =
                    java.time.LocalDateTime.of(
                            best.getRecord().getDate(),
                            best.getRecord().getTime());

            if (thisOne.isBefore(bestOne)) {
                best = a;
            }
        }

        return best;
    }

    private String initialsOf(String name) {

        StringBuilder sb = new StringBuilder();

        for (String part : name.trim().split("\\s+")) {

            if (part.equalsIgnoreCase("Dr.")
                    || part.equalsIgnoreCase("Dr")
                    || part.isEmpty()) {
                continue;
            }

            if (sb.length() < 2) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }

        return sb.length() == 0 ? "?" : sb.toString();
    }

    // lets the patient rate a COMPLETED appointment (saved in `feedback`)
    private void rateAppointment(
            Appointment appointment,
            Runnable afterSave
    ) {

        if (appointment == null || appointment.getRecord() == null) {
            return;
        }

        com.healthcare.model.AppointmentRecord rec =
                appointment.getRecord();

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Rate Your Visit");
        dialog.setHeaderText(
                "How was your visit with " + rec.getDoctorName() + "?"
        );

        ComboBox<String> ratingBox = new ComboBox<>();
        ratingBox.getItems().addAll(
                "5 - Excellent",
                "4 - Good",
                "3 - Average",
                "2 - Poor",
                "1 - Very poor"
        );
        ratingBox.setValue("5 - Excellent");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write a short comment (optional)");
        commentArea.setPrefRowCount(3);
        commentArea.setWrapText(true);

        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        form.getChildren().addAll(
                new Label("Rating"),
                ratingBox,
                new Label("Comment"),
                commentArea
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

            int stars = Integer.parseInt(
                    ratingBox.getValue().substring(0, 1)
            );

            String comment = commentArea.getText().trim();

            if (comment.length() > 300) {
                comment = comment.substring(0, 300);
            }

            Alert result;

            try {
                com.healthcare.dao.FeedbackDAO.Result outcome =
                        new com.healthcare.dao.FeedbackDAO().submit(
                                rec.getAppointmentId(),
                                UserSession.getUserId(),
                                stars,
                                comment
                        );

                if (outcome == com.healthcare.dao.FeedbackDAO.Result.SAVED) {

                    result = new Alert(Alert.AlertType.INFORMATION);
                    result.setContentText("Thank you! Your feedback has been saved.");

                } else if (outcome == com.healthcare.dao.FeedbackDAO.Result.ALREADY_GIVEN) {

                    result = new Alert(Alert.AlertType.WARNING);
                    result.setContentText("You have already rated this appointment.");

                } else {

                    result = new Alert(Alert.AlertType.ERROR);
                    result.setContentText("Only completed appointments can be rated.");
                }

            } catch (java.sql.SQLException e) {
                e.printStackTrace();

                result = new Alert(Alert.AlertType.ERROR);
                result.setContentText("Database error: " + e.getMessage());
            }

            result.setTitle("Feedback");
            result.setHeaderText(null);
            result.showAndWait();

            if (afterSave != null) {
                afterSave.run();
            }
        });
    }

    // asks "are you sure?", then updates the database
    private void confirmAndCancel(
            Appointment appointment,
            Runnable afterCancel
    ) {

        if (appointment == null || appointment.getRecord() == null) {
            return;
        }

        if (!com.healthcare.service.SettingsService.isCancellationEnabled()) {

            Alert blocked = new Alert(Alert.AlertType.WARNING);
            blocked.setTitle("Cancellation Disabled");
            blocked.setHeaderText("Cancellation is not available");
            blocked.setContentText(
                    "The administrator has turned off online cancellation.\n"
                            + "Please contact support to cancel this appointment.");
            blocked.showAndWait();
            return;
        }

        com.healthcare.model.AppointmentRecord rec =
                appointment.getRecord();

        Alert confirmation =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmation.setTitle("Cancel Appointment");
        confirmation.setHeaderText("Cancel your appointment?");
        confirmation.setContentText(
                "Your appointment with " + rec.getDoctorName() +
                        "\non " + rec.getLongDate() +
                        " at " + rec.getTimeDisplay() +
                        "\nwill be cancelled."
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response != ButtonType.OK) {
                return;
            }

            try {
                boolean done =
                        new com.healthcare.dao.AppointmentDAO()
                                .cancel(
                                        rec.getAppointmentId(),
                                        UserSession.getUserId()
                                );

                Alert result = new Alert(
                        done ? Alert.AlertType.INFORMATION
                                : Alert.AlertType.ERROR
                );

                result.setTitle(
                        done ? "Appointment Cancelled" : "Cannot Cancel"
                );
                result.setHeaderText(
                        done ? "Appointment cancelled"
                                : "This appointment cannot be cancelled"
                );
                result.setContentText(
                        done ? "Your appointment has been cancelled successfully."
                                : "Only Pending or Confirmed appointments can be cancelled."
                );
                result.showAndWait();

                if (done && afterCancel != null) {
                    afterCancel.run();
                }

            } catch (java.sql.SQLException e) {
                e.printStackTrace();

                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Database Error");
                error.setHeaderText("Could not cancel the appointment");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        });
    }

    // =============================================================
    // FILTER METHOD
    // =============================================================

    private java.util.List<Appointment>
    getFilteredAppointments(
            String search,
            String status
    ) {

        java.util.List<Appointment> all =
                new java.util.ArrayList<>(loadAppointments());

        java.util.List<Appointment> result =
                new java.util.ArrayList<>();

        for (Appointment appointment : all) {

            boolean matchesSearch =
                    search.isEmpty()
                            ||
                            appointment
                                    .doctorProperty()
                                    .get()
                                    .toLowerCase()
                                    .contains(search);

            boolean matchesStatus =
                    status.equals(
                            "All Appointments"
                    )
                            ||
                            appointment
                                    .statusProperty()
                                    .get()
                                    .equalsIgnoreCase(
                                            status
                                    );

            if (matchesSearch &&
                    matchesStatus) {

                result.add(appointment);
            }
        }

        return result;
    }

    // =============================================================
    // DETAILS POPUP
    // =============================================================

    private void showDetails(
            Appointment appointment
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Appointment Details"
        );

        alert.setHeaderText(
                "Appointment Information"
        );

        alert.setContentText(
                "Doctor: " +
                        appointment.doctorProperty().get() +
                        "\n\nDate: " +
                        appointment.dateProperty().get() +
                        "\nTime: " +
                        appointment.timeProperty().get() +
                        "\nStatus: " +
                        appointment.statusProperty().get() +
                        "\n\nConsultation Duration: 30 minutes"
        );

        alert.showAndWait();
    }

    // =============================================================
    // SIDEBAR BUTTON
    // =============================================================

    private Button navButton(
            String icon,
            String text,
            boolean active
    ) {

        Label iconLabel =
                new Label(icon);

        iconLabel.setFont(
                Font.font(
                        "Segoe UI",
                        16
                )
        );

        Label textLabel =
                new Label(text);

        textLabel.setFont(
                Font.font(
                        "Segoe UI",
                        active
                                ? FontWeight.BOLD
                                : FontWeight.NORMAL,
                        13
                )
        );

        HBox box =
                new HBox(12);

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.getChildren().addAll(
                iconLabel,
                textLabel
        );

        Button button =
                new Button();

        button.setGraphic(box);
        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(45);

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        if (active) {

            button.setStyle(
                    "-fx-background-color: #269cdd;" +
                            "-fx-background-radius: 9;" +
                            "-fx-padding: 0 14;"
            );

            iconLabel.setTextFill(
                    Color.WHITE
            );

            textLabel.setTextFill(
                    Color.WHITE
            );

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
    // STATUS BADGE
    // =============================================================

    private Label statusBadge(
            String text,
            String background,
            String foreground
    ) {

        Label badge =
                new Label(text);

        badge.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        10
                )
        );

        badge.setTextFill(
                Color.web(foreground)
        );

        badge.setPadding(
                new Insets(
                        6,
                        10,
                        6,
                        10
                )
        );

        badge.setStyle(
                "-fx-background-color: " +
                        background +
                        ";" +
                        "-fx-background-radius: 20;"
        );

        return badge;
    }

    // =============================================================
    // INFO BLOCK
    // =============================================================

    private VBox infoBlock(
            String heading,
            String value
    ) {

        VBox box =
                new VBox(4);

        Label headingLabel =
                new Label(heading);

        headingLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        9
                )
        );

        headingLabel.setTextFill(
                Color.web("#8a9bad")
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        valueLabel.setTextFill(
                Color.web(TEXT)
        );

        box.getChildren().addAll(
                headingLabel,
                valueLabel
        );

        return box;
    }

    // =============================================================
    // HELPERS
    // =============================================================

    private Region createHeight(
            double height
    ) {

        Region region =
                new Region();

        region.setPrefHeight(height);

        return region;
    }

    private Region createWidth(
            double width
    ) {

        Region region =
                new Region();

        region.setPrefWidth(width);

        return region;
    }

    // =============================================================
    // APPOINTMENT MODEL
    // =============================================================

    public static class Appointment {

        private final SimpleStringProperty doctor;
        private final SimpleStringProperty date;
        private final SimpleStringProperty time;
        private final SimpleStringProperty status;

        public Appointment(
                String doctor,
                String date,
                String time,
                String status
        ) {

            this.doctor =
                    new SimpleStringProperty(
                            doctor
                    );

            this.date =
                    new SimpleStringProperty(
                            date
                    );

            this.time =
                    new SimpleStringProperty(
                            time
                    );

            this.status =
                    new SimpleStringProperty(
                            status
                    );
        }

        private com.healthcare.model.AppointmentRecord record;

        public Appointment(
                com.healthcare.model.AppointmentRecord record
        ) {
            this(
                    record.getDoctorName(),
                    record.getDateDisplay(),
                    record.getTimeDisplay(),
                    record.getStatus()
            );
            this.record = record;
        }

        public com.healthcare.model.AppointmentRecord getRecord() {
            return record;
        }

        public StringProperty doctorProperty() {
            return doctor;
        }

        public StringProperty dateProperty() {
            return date;
        }

        public StringProperty timeProperty() {
            return time;
        }

        public StringProperty statusProperty() {
            return status;
        }
    }
}