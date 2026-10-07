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

public class MedicalHistoryView {

    private final String NAVY = "#12355b";
    private final String BLUE = "#2693df";
    private final String BG = "#f4f8fc";
    private final String TEXT = "#163b63";
    private final String MUTED = "#64748b";

    public void show(Stage stage) {

        // the patient's medical records come from the database
        java.util.List<MedicalRecord> allRecords = loadRecords();

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(255);

        sidebar.setPadding(
                new Insets(25, 15, 20, 15)
        );

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );

        // =========================================================
        // LOGO
        // =========================================================

        HBox logo = new HBox(12);

        logo.setAlignment(
                Pos.CENTER_LEFT
        );

        logo.setPadding(
                new Insets(0, 10, 20, 10)
        );

        StackPane logoIcon = new StackPane();

        logoIcon.setPrefSize(48, 48);
        logoIcon.setMinSize(48, 48);
        logoIcon.setMaxSize(48, 48);

        logoIcon.setStyle(
                "-fx-background-color: #2d9cdb;" +
                        "-fx-background-radius: 12;"
        );

        Label plus = new Label("+");

        plus.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        31
                )
        );

        plus.setTextFill(Color.WHITE);

        logoIcon.getChildren().add(plus);

        VBox logoText = new VBox(0);

        Label medicare = new Label("MediCare");

        medicare.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        23
                )
        );

        medicare.setTextFill(Color.WHITE);

        Label tagline =
                new Label("Your Health, Our Priority");

        tagline.setFont(
                Font.font(
                        "Segoe UI",
                        10
                )
        );

        tagline.setTextFill(
                Color.web("#b9d2e8")
        );

        logoText.getChildren().addAll(
                medicare,
                tagline
        );

        logo.getChildren().addAll(
                logoIcon,
                logoText
        );

        // =========================================================
        // PROFILE
        // =========================================================

        HBox profile = new HBox(10);

        profile.setAlignment(
                Pos.CENTER_LEFT
        );

        profile.setPadding(
                new Insets(12)
        );

        profile.setStyle(
                "-fx-background-color: #1d4b79;" +
                        "-fx-background-radius: 12;"
        );

        Circle profileCircle =
                new Circle(
                        23,
                        Color.web("#dceeff")
                );

        Label initials = new Label(UserSession.getInitials());

        initials.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        initials.setTextFill(
                Color.web("#1976d2")
        );

        StackPane profileIcon =
                new StackPane(
                        profileCircle,
                        initials
                );

        VBox profileInfo =
                new VBox(2);

        Label patientName =
                new Label(UserSession.getFullName());

        patientName.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        patientName.setTextFill(
                Color.WHITE
        );

        Label patientRole =
                new Label("Patient  •  Online");

        patientRole.setFont(
                Font.font(
                        "Segoe UI",
                        10
                )
        );

        patientRole.setTextFill(
                Color.web("#c7dced")
        );

        profileInfo.getChildren().addAll(
                patientName,
                patientRole
        );

        profile.getChildren().addAll(
                profileIcon,
                profileInfo
        );

        // =========================================================
        // NAVIGATION
        // =========================================================

        Button dashboardBtn =
                navButton(
                        "⌂",
                        "Dashboard",
                        false
                );

        Button bookBtn =
                navButton(
                        "▣",
                        "Book Appointment",
                        false
                );

        Button appointmentsBtn =
                navButton(
                        "▤",
                        "My Appointments",
                        false
                );

        Button historyBtn =
                navButton(
                        "♥",
                        "Medical History",
                        true
                );

        Button profileBtn =
                navButton(
                        "♙",
                        "My Profile",
                        false
                );

        Region sidebarSpacer =
                new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        Button logoutBtn =
                navButton(
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

        HBox topBar =
                new HBox();

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(
                        15,
                        25,
                        15,
                        25
                )
        );

        topBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5eaf0;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        VBox pageHeading =
                new VBox(2);

        Label pageTitle =
                new Label("Medical History");

        pageTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        22
                )
        );

        pageTitle.setTextFill(
                Color.web(TEXT)
        );

        Label pageSubtitle =
                new Label(
                        "Review your previous medical records and treatments"
                );

        pageSubtitle.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        pageSubtitle.setTextFill(
                Color.web(MUTED)
        );

        pageHeading.getChildren().addAll(
                pageTitle,
                pageSubtitle
        );

        Region topSpacer =
                new Region();

        HBox.setHgrow(
                topSpacer,
                Priority.ALWAYS
        );

        Label notification =
                new Label("🔔");

        notification.setFont(
                Font.font(
                        "Segoe UI",
                        17
                )
        );

        Label date =
                new Label("26 September 2026");

        date.setFont(
                Font.font(
                        "Segoe UI",
                        12
                )
        );

        date.setTextFill(
                Color.web(MUTED)
        );

        topBar.getChildren().addAll(
                pageHeading,
                topSpacer,
                notification,
                createWidth(20),
                date
        );

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(25)
        );

        content.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        // =========================================================
        // PATIENT SUMMARY
        // =========================================================

        HBox patientCard =
                new HBox();

        patientCard.setAlignment(
                Pos.CENTER_LEFT
        );

        patientCard.setPadding(
                new Insets(20, 24, 20, 24)
        );

        patientCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #dce8f1;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(30,70,100,0.07), 12, 0.1, 0, 3" +
                        ");"
        );

        StackPane patientAvatar =
                new StackPane();

        Circle avatarCircle =
                new Circle(
                        34,
                        Color.web("#e3f3ff")
                );

        Label avatarText =
                new Label(UserSession.getInitials());

        avatarText.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        15
                )
        );

        avatarText.setTextFill(
                Color.web(BLUE)
        );

        patientAvatar.getChildren().addAll(
                avatarCircle,
                avatarText
        );

        VBox patientDetails =
                new VBox(4);

        Label patientTitle =
                new Label(UserSession.getFullName());

        patientTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        18
                )
        );

        patientTitle.setTextFill(
                Color.web(TEXT)
        );

        Label patientId =
                new Label(
                        "Patient ID: P1001"
                );

        patientId.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        patientId.setTextFill(
                Color.web(MUTED)
        );

        Label recordText =
                new Label(
                        "Personal medical records"
                );

        recordText.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        recordText.setTextFill(
                Color.web("#7890a5")
        );

        patientDetails.getChildren().addAll(
                patientTitle,
                patientId,
                recordText
        );

        Region patientSpacer =
                new Region();

        HBox.setHgrow(
                patientSpacer,
                Priority.ALWAYS
        );

        VBox recordStat =
                new VBox(2);

        recordStat.setAlignment(
                Pos.CENTER
        );

        Label recordNumber =
                new Label(String.valueOf(allRecords.size()));

        recordNumber.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        28
                )
        );

        recordNumber.setTextFill(
                Color.web(BLUE)
        );

        Label recordLabel =
                new Label("Medical Records");

        recordLabel.setFont(
                Font.font(
                        "Segoe UI",
                        10
                )
        );

        recordLabel.setTextFill(
                Color.web(MUTED)
        );

        recordStat.getChildren().addAll(
                recordNumber,
                recordLabel
        );

        patientCard.getChildren().addAll(
                patientAvatar,
                createWidth(15),
                patientDetails,
                patientSpacer,
                recordStat
        );

        // =========================================================
        // INFORMATION CARDS
        // =========================================================

        HBox stats =
                new HBox(15);

        stats.getChildren().addAll(
                statCard(
                        "3",
                        "Total Records",
                        "All medical visits"
                ),
                statCard(
                        "3",
                        "Doctors",
                        "Healthcare providers"
                ),
                statCard(
                        "2026",
                        "Latest Year",
                        "Most recent records"
                )
        );

        // =========================================================
        // SEARCH BAR
        // =========================================================

        HBox searchBar =
                new HBox(12);

        searchBar.setAlignment(
                Pos.CENTER_LEFT
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search diagnosis, doctor or treatment..."
        );

        searchField.setPrefHeight(42);
        searchField.setPrefWidth(430);

        searchField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dce5ef;" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-padding: 0 14;" +
                        "-fx-font-size: 13;"
        );

        Region searchSpacer =
                new Region();

        HBox.setHgrow(
                searchSpacer,
                Priority.ALWAYS
        );

        Label privacy =
                new Label(
                        "🔒 Your medical records are private"
                );

        privacy.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        privacy.setTextFill(
                Color.web("#6d8296")
        );

        searchBar.getChildren().addAll(
                searchField,
                searchSpacer,
                privacy
        );

        // =========================================================
        // MEDICAL RECORD CARD
        // =========================================================

        VBox recordCard =
                new VBox(15);

        recordCard.setPadding(
                new Insets(20)
        );

        recordCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #e1e8ef;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(30,70,100,0.07), 12, 0.1, 0, 3" +
                        ");"
        );

        HBox recordHeader =
                new HBox();

        VBox recordHeaderText =
                new VBox(3);

        Label historyTitle =
                new Label("Medical Records");

        historyTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        18
                )
        );

        historyTitle.setTextFill(
                Color.web(TEXT)
        );

        Label historySubtitle =
                new Label(
                        "Previous consultations, diagnoses and treatments"
                );

        historySubtitle.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        historySubtitle.setTextFill(
                Color.web(MUTED)
        );

        recordHeaderText.getChildren().addAll(
                historyTitle,
                historySubtitle
        );

        Region headerSpacer =
                new Region();

        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );

        Label recordCount =
                new Label(allRecords.size() + " Records");

        recordCount.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        recordCount.setTextFill(
                Color.web(BLUE)
        );

        recordHeader.getChildren().addAll(
                recordHeaderText,
                headerSpacer,
                recordCount
        );

        // =========================================================
        // TABLE
        // =========================================================

        TableView<MedicalRecord> table =
                new TableView<>();

        table.setPrefHeight(300);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e5ebf1;" +
                        "-fx-border-radius: 8;"
        );

        // DATE
        TableColumn<MedicalRecord, String>
                dateColumn =
                new TableColumn<>("Date");

        dateColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .dateProperty()
        );

        // DOCTOR
        TableColumn<MedicalRecord, String>
                doctorColumn =
                new TableColumn<>("Doctor");

        doctorColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .doctorProperty()
        );

        // DIAGNOSIS
        TableColumn<MedicalRecord, String>
                diagnosisColumn =
                new TableColumn<>("Diagnosis");

        diagnosisColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .diagnosisProperty()
        );

        // TREATMENT
        TableColumn<MedicalRecord, String>
                treatmentColumn =
                new TableColumn<>("Treatment");

        treatmentColumn.setCellValueFactory(
                data ->
                        data.getValue()
                                .treatmentProperty()
        );

        // ACTION
        TableColumn<MedicalRecord, Void>
                actionColumn =
                new TableColumn<>("Action");

        actionColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button viewButton =
                            new Button("View Details");

                    {

                        viewButton.setPrefHeight(29);

                        viewButton.setStyle(
                                "-fx-background-color: #eef7ff;" +
                                        "-fx-text-fill: #1976d2;" +
                                        "-fx-background-radius: 6;" +
                                        "-fx-font-size: 10;" +
                                        "-fx-font-weight: bold;"
                        );

                        viewButton.setOnAction(event -> {

                            MedicalRecord record =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            showDetails(record);
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

                        if (empty) {

                            setGraphic(null);

                        } else {

                            setGraphic(
                                    viewButton
                            );
                        }
                    }
                }
        );

        table.getColumns().addAll(
                dateColumn,
                doctorColumn,
                diagnosisColumn,
                treatmentColumn,
                actionColumn
        );

        table.getItems().addAll(allRecords);

        // =========================================================
        // SEARCH FUNCTIONALITY
        // =========================================================

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    String search =
                            newValue
                                    .trim()
                                    .toLowerCase();

                    table.getItems().setAll(allRecords);

                    if (!search.isEmpty()) {

                        table.getItems().removeIf(
                                record ->
                                        !record
                                                .doctorProperty()
                                                .get()
                                                .toLowerCase()
                                                .contains(search)
                                                &&
                                                !record
                                                        .diagnosisProperty()
                                                        .get()
                                                        .toLowerCase()
                                                        .contains(search)
                                                &&
                                                !record
                                                        .treatmentProperty()
                                                        .get()
                                                        .toLowerCase()
                                                        .contains(search)
                        );
                    }
                }
        );

        recordCard.getChildren().addAll(
                recordHeader,
                table
        );

        // =========================================================
        // BOTTOM INFORMATION
        // =========================================================

        HBox infoBox =
                new HBox();

        infoBox.setAlignment(
                Pos.CENTER_LEFT
        );

        infoBox.setPadding(
                new Insets(15, 18, 15, 18)
        );

        infoBox.setStyle(
                "-fx-background-color: #edf7ff;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #d5eafa;" +
                        "-fx-border-radius: 12;"
        );

        Label infoIcon =
                new Label("ℹ");

        infoIcon.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        16
                )
        );

        infoIcon.setTextFill(
                Color.web(BLUE)
        );

        Label infoText =
                new Label(
                        "Medical records are maintained by authorized healthcare professionals. " +
                                "Contact your doctor if you need any record updated."
                );

        infoText.setFont(
                Font.font(
                        "Segoe UI",
                        11
                )
        );

        infoText.setTextFill(
                Color.web("#4d6d89")
        );

        infoBox.getChildren().addAll(
                infoIcon,
                createWidth(10),
                infoText
        );

        // =========================================================
        // ADD CONTENT
        // =========================================================

        content.getChildren().addAll(
                patientCard,
                stats,
                searchBar,
                recordCard,
                infoBox
        );

        // =========================================================
        // NAVIGATION
        // =========================================================

        dashboardBtn.setOnAction(event -> {

            PatientDashboard dashboard =
                    new PatientDashboard();

            dashboard.show(stage);
        });

        bookBtn.setOnAction(event -> {

            BookAppointmentView bookView =
                    new BookAppointmentView();

            bookView.show(stage);
        });

        appointmentsBtn.setOnAction(event -> {

            MyAppointmentsView appointmentView =
                    new MyAppointmentsView();

            appointmentView.show(stage);
        });

        profileBtn.setOnAction(event -> {

            ProfileView profileView =
                    new ProfileView();

            profileView.show(stage);
        });

        logoutBtn.setOnAction(event -> {

            LoginView loginView =
                    new LoginView();

            loginView.show(stage);
        });

        // =========================================================
        // SCROLL PANE
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

        // =========================================================
        // SCENE
        // =========================================================

        Scene scene =
                new Scene(
                        root,
                        1400,
                        820
                );

        stage.setTitle(
                "MediCare - Medical History"
        );

        stage.setScene(scene);

        stage.setMinWidth(1200);
        stage.setMinHeight(750);

        stage.show();
    }

    // =============================================================
    // STAT CARD
    // =============================================================

    private HBox statCard(
            String number,
            String title,
            String subtitle
    ) {

        HBox card =
                new HBox(12);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(16)
        );

        card.setPrefHeight(80);

        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 13;" +
                        "-fx-border-color: #e1e8ef;" +
                        "-fx-border-radius: 13;"
        );

        StackPane icon =
                new StackPane();

        Circle circle =
                new Circle(
                        22,
                        Color.web("#e8f5ff")
                );

        Label iconText =
                new Label("✓");

        iconText.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        14
                )
        );

        iconText.setTextFill(
                Color.web(BLUE)
        );

        icon.getChildren().addAll(
                circle,
                iconText
        );

        VBox text =
                new VBox(2);

        Label numberLabel =
                new Label(number);

        numberLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        19
                )
        );

        numberLabel.setTextFill(
                Color.web(TEXT)
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        titleLabel.setTextFill(
                Color.web(TEXT)
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        9
                )
        );

        subtitleLabel.setTextFill(
                Color.web(MUTED)
        );

        text.getChildren().addAll(
                numberLabel,
                titleLabel,
                subtitleLabel
        );

        card.getChildren().addAll(
                icon,
                text
        );

        return card;
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
    // LOAD FROM DATABASE
    // =============================================================
    private java.util.List<MedicalRecord> loadRecords() {

        java.util.List<MedicalRecord> list =
                new java.util.ArrayList<>();

        try {
            for (com.healthcare.dao.MedicalRecordDAO.RecordItem item :
                    new com.healthcare.dao.MedicalRecordDAO()
                            .getForPatient(UserSession.getUserId())) {

                list.add(
                        new MedicalRecord(
                                item.date,
                                item.doctorName,
                                item.diagnosis,
                                item.prescription.isEmpty()
                                        ? "-"
                                        : item.prescription
                        )
                );
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // =============================================================
    // VIEW DETAILS
    // =============================================================

    private void showDetails(
            MedicalRecord record
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                "Medical Record Details"
        );

        alert.setHeaderText(
                "Medical Record"
        );

        alert.setContentText(
                "Date: " +
                        record.dateProperty().get() +

                        "\n\nDoctor: " +
                        record.doctorProperty().get() +

                        "\n\nDiagnosis: " +
                        record.diagnosisProperty().get() +

                        "\n\nTreatment: " +
                        record.treatmentProperty().get()
        );

        alert.showAndWait();
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
    // MEDICAL RECORD MODEL
    // =============================================================

    public static class MedicalRecord {

        private final SimpleStringProperty date;
        private final SimpleStringProperty doctor;
        private final SimpleStringProperty diagnosis;
        private final SimpleStringProperty treatment;

        public MedicalRecord(
                String date,
                String doctor,
                String diagnosis,
                String treatment
        ) {

            this.date =
                    new SimpleStringProperty(date);

            this.doctor =
                    new SimpleStringProperty(doctor);

            this.diagnosis =
                    new SimpleStringProperty(diagnosis);

            this.treatment =
                    new SimpleStringProperty(treatment);
        }

        public StringProperty dateProperty() {
            return date;
        }

        public StringProperty doctorProperty() {
            return doctor;
        }

        public StringProperty diagnosisProperty() {
            return diagnosis;
        }

        public StringProperty treatmentProperty() {
            return treatment;
        }
    }
}