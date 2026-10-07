package com.healthcare;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminDashboard {

    public void show(Stage stage) {

        // =========================
        // HEADER
        // =========================

        Label title = new Label("Online Healthcare Management System");
        title.setFont(Font.font("Arial", 22));
        title.setTextFill(Color.WHITE);

        Label welcome = new Label("Welcome, Admin");
        welcome.setFont(Font.font("Arial", 16));
        welcome.setTextFill(Color.WHITE);

        Button logoutButton = new Button("Logout");

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(20, title, headerSpacer, welcome, logoutButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #1565C0;");

        // =========================
        // SIDEBAR
        // =========================

        Label menuTitle = new Label("ADMIN MENU");
        menuTitle.setFont(Font.font("Arial", 18));

        Button dashboardButton = new Button("Dashboard");
        Button usersButton = new Button("User Management");
        Button appointmentsButton = new Button("Appointment Management");
        Button settingsButton = new Button("System Settings");
        Button analyticsButton = new Button("Performance Analytics");

        Button[] menuButtons = {
                dashboardButton,
                usersButton,
                appointmentsButton,
                settingsButton,
                analyticsButton
        };

        for (Button button : menuButtons) {
            button.setMaxWidth(Double.MAX_VALUE);
            button.setPrefHeight(45);
        }

        VBox sidebar = new VBox(
                15,
                menuTitle,
                dashboardButton,
                usersButton,
                appointmentsButton,
                settingsButton,
                analyticsButton
        );

        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #E3F2FD;");

        // =========================
        // DASHBOARD TITLE
        // =========================

        Label dashboardTitle = new Label("Admin Dashboard");
        dashboardTitle.setFont(Font.font("Arial", 26));

        Label subtitle = new Label(
                "Manage users, appointments and system operations"
        );
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.GRAY);

        VBox dashboardHeader = new VBox(5, dashboardTitle, subtitle);

        // =========================
        // DASHBOARD CARDS
        // =========================

        // numbers come from the database
        int[] counts = loadCounts();

        VBox usersCard = createCard(
                "Total Users",
                String.valueOf(counts[0])
        );

        VBox appointmentsCard = createCard(
                "Today's Appointments",
                String.valueOf(counts[1])
        );

        VBox doctorsCard = createCard(
                "Registered Doctors",
                String.valueOf(counts[2])
        );

        VBox patientsCard = createCard(
                "Registered Patients",
                String.valueOf(counts[3])
        );

        HBox cards = new HBox(
                20,
                usersCard,
                appointmentsCard,
                doctorsCard,
                patientsCard
        );

        // =========================
        // QUICK INFORMATION
        // =========================

        Label quickTitle = new Label("System Overview");
        quickTitle.setFont(Font.font("Arial", 20));

        Label info = new Label(
                "Use the menu on the left to manage users, appointments, " +
                        "system settings and view performance analytics."
        );

        info.setWrapText(true);
        info.setFont(Font.font("Arial", 14));

        VBox overviewBox = new VBox(10, quickTitle, info);
        overviewBox.setPadding(new Insets(20));
        overviewBox.setStyle(
                "-fx-background-color: #F5F5F5;" +
                        "-fx-border-color: #DDDDDD;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox(
                25,
                dashboardHeader,
                cards,
                overviewBox
        );

        content.setPadding(new Insets(30));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        BorderPane root = new BorderPane();

        root.setTop(header);
        root.setLeft(sidebar);
        root.setCenter(scrollPane);

        // =========================
        // BUTTON ACTIONS
        // =========================

        usersButton.setOnAction(event -> {
            UserManagementView view = new UserManagementView();
            view.show(stage);
        });

        appointmentsButton.setOnAction(event -> {
            AdminAppointmentsView view = new AdminAppointmentsView();
            view.show(stage);
        });

        settingsButton.setOnAction(event -> {
            SystemSettingsView view = new SystemSettingsView();
            view.show(stage);
        });

        analyticsButton.setOnAction(event -> {
            PerformanceAnalyticsView view = new PerformanceAnalyticsView();
            view.show(stage);
        });

        logoutButton.setOnAction(event -> {
            Main main = new Main();
            main.start(stage);
        });

        // =========================
        // SCENE
        // =========================

        Scene scene = new Scene(root, 1200, 700);

        stage.setTitle("Admin Dashboard - Online Healthcare Management System");
        stage.setScene(scene);
        stage.show();
    }

    // =========================
    // CARD CREATION METHOD
    // =========================

    private int[] loadCounts() {
        try {
            return new com.healthcare.dao.AnalyticsDAO().getDashboardCounts();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return new int[]{0, 0, 0, 0};
        }
    }

    private VBox createCard(String title, String value) {

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 14));
        titleLabel.setTextFill(Color.GRAY);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", 28));
        valueLabel.setTextFill(Color.DARKBLUE);

        VBox card = new VBox(10, titleLabel, valueLabel);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setPrefHeight(120);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #DDDDDD;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        return card;
    }
}