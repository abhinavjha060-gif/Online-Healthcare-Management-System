package com.healthcare;

import com.healthcare.dao.DoctorAppointmentDAO;
import com.healthcare.dao.FeedbackDAO;
import com.healthcare.model.PatientAppointment;
import com.healthcare.model.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Doctor "Reports" screen: totals, a month-by-month table and a CSV export, all built from the doctor's own appointments and feedback.
 */
public class DoctorReportsView {

    private static final String NAVY = "#12355B";
    private static final String BLUE = "#2684D9";
    private static final String MUTED = "#64748B";

    /** One row of the month table. */
    public static class MonthRow {
        final String month;
        int total;
        int completed;
        int cancelled;
        int active;

        MonthRow(String month) {
            this.month = month;
        }
    }

    public void show(Stage stage) {

        int doctorId = UserSession.getUserId();

        // ---------------- load data ----------------
        List<PatientAppointment> all = new ArrayList<>();
        double[] rating = {0, 0};
        String loadError = null;

        try {
            all = new DoctorAppointmentDAO().getForDoctor(doctorId);
            rating = new FeedbackDAO().getSummary(doctorId);
        } catch (SQLException e) {
            e.printStackTrace();
            loadError = "Could not load the report: " + e.getMessage();
        }

        final List<PatientAppointment> appointments = all;

        int completed = 0;
        int cancelled = 0;
        int active = 0;                       // Pending + Confirmed
        Set<Integer> patients = new HashSet<>();
        Map<YearMonth, MonthRow> months = new TreeMap<>(java.util.Comparator.reverseOrder());
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

        for (PatientAppointment a : appointments) {

            MonthRow row = months.computeIfAbsent(
                    YearMonth.from(a.getDate()), k -> new MonthRow(k.format(monthFormat)));
            row.total++;

            switch (a.getStatus()) {
                case "Completed" -> { completed++; row.completed++; patients.add(a.getPatientId()); }
                case "Cancelled" -> { cancelled++; row.cancelled++; }
                default          -> { active++; row.active++; patients.add(a.getPatientId()); }
            }
        }

        // ---------------- heading ----------------
        Label title = new Label("Reports");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#1E3A5F"));

        Label subtitle = new Label("A summary of your appointments, patients and ratings.");
        subtitle.setTextFill(Color.web(MUTED));

        // ---------------- summary cards ----------------
        HBox cards = new HBox(15);
        cards.getChildren().addAll(
                card("Total appointments", String.valueOf(appointments.size())),
                card("Completed", String.valueOf(completed)),
                card("Upcoming (pending / confirmed)", String.valueOf(active)),
                card("Cancelled", String.valueOf(cancelled)),
                card("Different patients", String.valueOf(patients.size())),
                card("Average rating",
                        rating[0] == 0 ? "-" : String.format("%.2f / 5", rating[1])
                                + "   (" + (int) rating[0] + ")")
        );
        for (javafx.scene.Node n : cards.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }

        // ---------------- month table ----------------
        TableView<MonthRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No appointments yet."));
        table.setPrefHeight(300);

        table.getColumns().add(column("Month", r -> r.month));
        table.getColumns().add(column("Total", r -> String.valueOf(r.total)));
        table.getColumns().add(column("Completed", r -> String.valueOf(r.completed)));
        table.getColumns().add(column("Upcoming", r -> String.valueOf(r.active)));
        table.getColumns().add(column("Cancelled", r -> String.valueOf(r.cancelled)));
        table.getItems().addAll(months.values());

        Label tableTitle = new Label("Appointments by month");
        tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web("#1E3A5F"));

        Label message = new Label(loadError == null ? "" : loadError);
        message.setTextFill(Color.web("#C62828"));

        // ---------------- buttons ----------------
        Button exportButton = new Button("Export appointments (CSV)");
        exportButton.setPrefHeight(40);
        exportButton.setDisable(appointments.isEmpty());
        exportButton.setStyle(
                "-fx-background-color: " + BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;");

        exportButton.setOnAction(event -> {

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save appointments report");
            chooser.setInitialFileName("my-appointments-report.csv");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV file", "*.csv"));

            File file = chooser.showSaveDialog(stage);
            if (file == null) {
                return;
            }

            try {
                Files.writeString(file.toPath(), toCsv(appointments), StandardCharsets.UTF_8);
                message.setTextFill(Color.web("#16A085"));
                message.setText("Saved: " + file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                message.setTextFill(Color.web("#C62828"));
                message.setText("Could not save the file: " + e.getMessage());
            }
        });

        Button backButton = new Button("← Back to Dashboard");
        backButton.setPrefHeight(40);
        backButton.setOnAction(event -> new DoctorDashboard().show(stage));

        HBox buttons = new HBox(15, exportButton, backButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // ---------------- page ----------------
        VBox page = new VBox(20);
        page.setPadding(new Insets(30));
        page.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 12px;");
        page.getChildren().addAll(title, subtitle, cards, tableTitle, table, message, buttons);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #F7FAFC; -fx-background-color: #F7FAFC;");

        StackPane root = new StackPane(scroll);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F7FAFC;");

        Navigator.show(stage, root, "Reports - Online Healthcare");
    }

    // ------------------------------------------------------------------

    private VBox card(String heading, String value) {

        Label h = new Label(heading);
        h.setFont(Font.font("Segoe UI", 11));
        h.setTextFill(Color.web(MUTED));
        h.setWrapText(true);

        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        v.setTextFill(Color.web(NAVY));

        VBox box = new VBox(6, h, v);
        box.setPadding(new Insets(16));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(
                "-fx-background-color: #F4F8FC;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #DCE5EE;" +
                        "-fx-border-radius: 10;");
        return box;
    }

    private TableColumn<MonthRow, String> column(
            String heading, java.util.function.Function<MonthRow, String> getter) {

        TableColumn<MonthRow, String> col = new TableColumn<>(heading);
        col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        return col;
    }

    private static String toCsv(List<PatientAppointment> list) {

        StringBuilder sb = new StringBuilder("Date,Time,Patient,Reason,Status\r\n");

        for (PatientAppointment a : list) {
            sb.append(csv(a.getDateDisplay())).append(',')
                    .append(csv(a.getTimeDisplay())).append(',')
                    .append(csv(a.getPatientName())).append(',')
                    .append(csv(a.getReason())).append(',')
                    .append(csv(a.getStatus())).append("\r\n");
        }
        return sb.toString();
    }

    /** Quotes a CSV value (and guards against spreadsheet formula injection). */
    private static String csv(String value) {
        String v = value == null ? "" : value;
        if (!v.isEmpty() && "=+-@".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        return "\"" + v.replace("\"", "\"\"") + "\"";
    }
}
