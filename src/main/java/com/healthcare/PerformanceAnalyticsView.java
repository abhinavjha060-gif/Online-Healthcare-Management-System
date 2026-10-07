package com.healthcare;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PerformanceAnalyticsView {

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("Performance Analytics");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label(
                "View overall system performance and statistics."
        );
        subtitle.setStyle("-fx-text-fill: #666666;");

        VBox heading = new VBox(5, title, subtitle);

        // =========================
        // STATISTICS
        // =========================

        // numbers come from the database (COUNT queries)
        int[] totals = loadTotals();

        Label totalUsers = createValue(String.valueOf(totals[0]));
        Label totalDoctors = createValue(String.valueOf(totals[1]));
        Label totalPatients = createValue(String.valueOf(totals[2]));
        Label totalAppointments = createValue(String.valueOf(totals[3]));

        VBox usersCard = createCard(
                "Total Users",
                totalUsers
        );

        VBox doctorsCard = createCard(
                "Total Doctors",
                totalDoctors
        );

        VBox patientsCard = createCard(
                "Total Patients",
                totalPatients
        );

        VBox appointmentsCard = createCard(
                "Total Appointments",
                totalAppointments
        );

        HBox cards = new HBox(
                20,
                usersCard,
                doctorsCard,
                patientsCard,
                appointmentsCard
        );

        // =========================
        // APPOINTMENT STATISTICS
        // =========================

        Label appointmentTitle =
                new Label("Appointment Statistics");

        appointmentTitle.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold;"
        );

        // one bar per status, from: SELECT status, COUNT(*) ... GROUP BY status
        java.util.Map<String, Integer> statusCounts = loadStatusCounts();

        VBox appointmentStats = new VBox(10);

        for (String status : new String[]{
                "Pending", "Confirmed", "Completed", "Cancelled"}) {

            int count = statusCounts.getOrDefault(status, 0);

            double ratio = totals[3] == 0
                    ? 0
                    : (double) count / totals[3];

            Label statusLabel = new Label(
                    status + " Appointments - " + count +
                            " (" + Math.round(ratio * 100) + "%)"
            );

            ProgressBar statusBar = new ProgressBar(ratio);
            statusBar.setPrefWidth(500);

            appointmentStats.getChildren().addAll(
                    statusLabel,
                    statusBar
            );
        }

        // bar graph of the same numbers
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        yAxis.setLabel("Appointments");
        yAxis.setMinorTickVisible(false);
        yAxis.setTickUnit(1);

        javafx.scene.chart.BarChart<String, Number> statusChart =
                new javafx.scene.chart.BarChart<>(xAxis, yAxis);
        statusChart.setTitle("Appointments by status");
        statusChart.setLegendVisible(false);
        statusChart.setAnimated(false);
        statusChart.setPrefHeight(280);

        javafx.scene.chart.XYChart.Series<String, Number> statusSeries =
                new javafx.scene.chart.XYChart.Series<>();
        for (String status : new String[]{
                "Pending", "Confirmed", "Completed", "Cancelled"}) {
            statusSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(
                    status, statusCounts.getOrDefault(status, 0)));
        }
        statusChart.getData().add(statusSeries);
        appointmentStats.getChildren().add(statusChart);

        // =========================
        // SYSTEM INFORMATION
        // =========================

        Label systemTitle =
                new Label("System Overview");

        systemTitle.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold;"
        );

        Label systemInfo = new Label(
                "The healthcare management system currently " +
                        "supports patient appointment booking, doctor " +
                        "schedule management, medical records and " +
                        "administrative operations."
        );

        systemInfo.setWrapText(true);
        systemInfo.setStyle("-fx-font-size: 14px;");

        VBox systemBox = new VBox(
                10,
                systemTitle,
                systemInfo
        );

        systemBox.setPadding(new Insets(20));

        // doctor-wise performance (from the v_doctor_performance view)
        Label performanceTitle = new Label("Doctor Performance");
        performanceTitle.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold;"
        );

        TableView<com.healthcare.dao.AnalyticsDAO.DoctorStat> performanceTable =
                new TableView<>();

        performanceTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
        performanceTable.setPrefHeight(170);

        performanceTable.getColumns().add(
                statColumn("Doctor", st -> st.doctorName)
        );
        performanceTable.getColumns().add(
                statColumn("Speciality", st -> st.specialization)
        );
        performanceTable.getColumns().add(
                statColumn("Appointments", st -> String.valueOf(st.totalAppointments))
        );
        performanceTable.getColumns().add(
                statColumn("Completed", st -> String.valueOf(st.completed))
        );
        performanceTable.getColumns().add(
                statColumn("Avg Rating", st -> st.avgRating)
        );

        performanceTable.getItems().addAll(loadDoctorStats());

        systemBox.getChildren().addAll(
                performanceTitle,
                performanceTable
        );

        systemBox.setStyle(
                "-fx-background-color: #F5F5F5;" +
                        "-fx-border-color: #DDDDDD;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

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
                25,
                heading,
                cards,
                appointmentTitle,
                appointmentStats,
                systemBox,
                backButton
        );

        content.setPadding(new Insets(30));

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        BorderPane root =
                new BorderPane(scrollPane);

        Scene scene =
                new Scene(root, 1100, 700);

        stage.setTitle(
                "Performance Analytics - Online Healthcare"
        );

        stage.setScene(scene);
        stage.show();
    }

    // =========================
    // CREATE CARD
    // =========================

    // =========================
    // DATABASE HELPERS
    // =========================

    private int[] loadTotals() {
        try {
            return new com.healthcare.dao.AnalyticsDAO().getTotals();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return new int[]{0, 0, 0, 0};
        }
    }

    private java.util.Map<String, Integer> loadStatusCounts() {
        try {
            return new com.healthcare.dao.AnalyticsDAO().getStatusCounts();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return new java.util.HashMap<>();
        }
    }

    private java.util.List<com.healthcare.dao.AnalyticsDAO.DoctorStat> loadDoctorStats() {
        try {
            return new com.healthcare.dao.AnalyticsDAO().getDoctorStats();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    private TableColumn<com.healthcare.dao.AnalyticsDAO.DoctorStat, String> statColumn(
            String heading,
            java.util.function.Function<com.healthcare.dao.AnalyticsDAO.DoctorStat, String> getter
    ) {
        TableColumn<com.healthcare.dao.AnalyticsDAO.DoctorStat, String> column =
                new TableColumn<>(heading);

        column.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        getter.apply(data.getValue())
                )
        );

        return column;
    }

    private VBox createCard(
            String title,
            Label value
    ) {

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #666666;"
        );

        VBox card =
                new VBox(
                        10,
                        titleLabel,
                        value
                );

        card.setPadding(
                new Insets(20)
        );

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

    // =========================
    // CREATE VALUE LABEL
    // =========================

    private Label createValue(String value) {

        Label label =
                new Label(value);

        label.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1565C0;"
        );

        return label;
    }
}
