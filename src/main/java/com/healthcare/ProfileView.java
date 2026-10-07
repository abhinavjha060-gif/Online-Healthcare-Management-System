package com.healthcare;

import com.healthcare.model.UserSession;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProfileView {

    private final String DARK_BLUE = "#12355B";
    private final String PRIMARY_BLUE = "#2684D9";
    private final String BACKGROUND = "#F4F8FC";
    private final String TEXT_DARK = "#183B60";
    private final String TEXT_GRAY = "#64748B";
    private final String BORDER = "#DCE5EE";
    private final String LIGHT_BLUE = "#EEF7FF";
    private final String GREEN = "#16A085";
    private final String RED = "#D32F2F";


    public void show(Stage stage) {

        // ---- real data from the database ----
        final com.healthcare.dao.PatientProfileDAO profileDao =
                new com.healthcare.dao.PatientProfileDAO();
        final com.healthcare.dao.PatientProfileDAO.Profile profile = loadProfile(profileDao);
        final String patientCode = String.format("P%04d", profile.patientId);

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox(8);

        sidebar.setPrefWidth(245);

        sidebar.setPadding(
                new Insets(25, 18, 20, 18)
        );

        sidebar.setStyle(
                "-fx-background-color: " + DARK_BLUE + ";"
        );


        // ---------------- LOGO ----------------

        HBox logoBox = new HBox(12);

        logoBox.setAlignment(Pos.CENTER_LEFT);

        logoBox.setPadding(
                new Insets(0, 0, 25, 8)
        );

        Label logoIcon = new Label("✚");

        logoIcon.setPrefSize(42, 42);

        logoIcon.setAlignment(Pos.CENTER);

        logoIcon.setStyle(
                "-fx-background-color: #2D9CDB;" +
                        "-fx-background-radius: 10;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );


        Label logoText = new Label("MediCare");

        logoText.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 21px;" +
                        "-fx-font-weight: bold;"
        );


        logoBox.getChildren().addAll(
                logoIcon,
                logoText
        );


        // ---------------- MINI PROFILE ----------------

        VBox miniProfile = new VBox(3);

        miniProfile.setPadding(
                new Insets(0, 8, 20, 8)
        );


        Label miniName =
                new Label(UserSession.getFullName());

        miniName.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );


        Label miniId =
                new Label("Patient • " + patientCode);

        miniId.setStyle(
                "-fx-text-fill: #AFC7DE;" +
                        "-fx-font-size: 12px;"
        );


        miniProfile.getChildren().addAll(
                miniName,
                miniId
        );


        // ---------------- NAVIGATION ----------------

        Button dashboardButton =
                createNavButton(
                        "⌂",
                        "Dashboard",
                        false
                );


        Button bookButton =
                createNavButton(
                        "＋",
                        "Book Appointment",
                        false
                );


        Button appointmentsButton =
                createNavButton(
                        "▣",
                        "My Appointments",
                        false
                );


        Button historyButton =
                createNavButton(
                        "▤",
                        "Medical History",
                        false
                );


        Button profileButton =
                createNavButton(
                        "●",
                        "My Profile",
                        true
                );


        VBox navigation = new VBox(7);

        navigation.getChildren().addAll(
                dashboardButton,
                bookButton,
                appointmentsButton,
                historyButton,
                profileButton
        );


        Region sidebarSpacer = new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );


        // ---------------- HEALTH CARD ----------------

        VBox healthCard = new VBox(8);

        healthCard.setPadding(
                new Insets(15)
        );

        healthCard.setStyle(
                "-fx-background-color: #1C4A76;" +
                        "-fx-background-radius: 12;"
        );


        Label healthTitle =
                new Label("Your Health Matters 💙");

        healthTitle.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );


        Label healthText =
                new Label(
                        "Keep your health information\n"
                                + "updated for better care."
                );

        healthText.setWrapText(true);

        healthText.setStyle(
                "-fx-text-fill: #C8DCEE;" +
                        "-fx-font-size: 12px;"
        );


        healthCard.getChildren().addAll(
                healthTitle,
                healthText
        );


        // ---------------- LOGOUT ----------------

        Button logoutButton =
                createNavButton(
                        "↪",
                        "Logout",
                        false
                );


        logoutButton.setOnAction(event -> {

            Main main = new Main();

            main.start(stage);
        });


        sidebar.getChildren().addAll(
                logoBox,
                miniProfile,
                navigation,
                sidebarSpacer,
                healthCard,
                logoutButton
        );


        // =========================================================
        // TOP BAR
        // =========================================================

        BorderPane topBar = new BorderPane();

        topBar.setPadding(
                new Insets(18, 28, 18, 28)
        );

        topBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E4EBF2;" +
                        "-fx-border-width: 0 0 1 0;"
        );


        VBox topTitleBox = new VBox(3);


        Label topTitle =
                new Label("My Health Profile");

        topTitle.setStyle(
                "-fx-text-fill: " + TEXT_DARK + ";" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );


        Label topSubtitle =
                new Label(
                        "Manage your personal and health information"
                );

        topSubtitle.setStyle(
                "-fx-text-fill: " + TEXT_GRAY + ";" +
                        "-fx-font-size: 13px;"
        );


        topTitleBox.getChildren().addAll(
                topTitle,
                topSubtitle
        );


        HBox topRight = new HBox(20);

        topRight.setAlignment(
                Pos.CENTER_RIGHT
        );


        Label notification =
                new Label("🔔");

        notification.setStyle(
                "-fx-font-size: 19px;" +
                        "-fx-cursor: hand;"
        );


        Label topPatient =
                new Label(UserSession.getFullName());

        topPatient.setStyle(
                "-fx-text-fill: " + TEXT_DARK + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );


        topRight.getChildren().addAll(
                notification,
                topPatient
        );


        topBar.setLeft(topTitleBox);

        topBar.setRight(topRight);


        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = new VBox(20);

        content.setPadding(
                new Insets(28)
        );

        content.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =========================================================
        // PROFILE HEADER
        // =========================================================

        HBox profileHeader = new HBox(25);

        profileHeader.setAlignment(
                Pos.CENTER_LEFT
        );

        profileHeader.setPadding(
                new Insets(28)
        );

        profileHeader.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 16;"
        );


        StackPane avatar =
                createDefaultAvatar();


        Button changePhoto =
                new Button("📷 Change Photo");


        changePhoto.setStyle(
                "-fx-background-color: " + LIGHT_BLUE + ";" +
                        "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #B9D8F2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;"
        );


        changePhoto.setOnAction(event -> {

            FileChooser chooser =
                    new FileChooser();

            chooser.setTitle(
                    "Select Profile Photo"
            );

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.png",
                            "*.jpg",
                            "*.jpeg"
                    )
            );


            File file =
                    chooser.showOpenDialog(stage);


            if (file != null) {

                Image image =
                        new Image(
                                file.toURI().toString(),
                                124,
                                124,
                                true,
                                true
                        );


                ImageView imageView =
                        new ImageView(image);


                imageView.setFitWidth(124);
                imageView.setFitHeight(124);


                imageView.setClip(
                        new Circle(
                                62,
                                62,
                                62
                        )
                );


                avatar.getChildren().clear();

                avatar.getChildren().add(
                        imageView
                );
            }
        });


        VBox profileInfo =
                new VBox(7);


        Label patientName =
                new Label(UserSession.getFullName());

        patientName.setStyle(
                "-fx-text-fill: " + TEXT_DARK + ";" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;"
        );


        HBox patientMeta =
                new HBox(10);

        patientMeta.setAlignment(
                Pos.CENTER_LEFT
        );


        Label verified =
                new Label("Patient");

        verified.setStyle(
                "-fx-background-color: #E7F8F2;" +
                        "-fx-text-fill: " + GREEN + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 5 10 5 10;"
        );


        Label patientId =
                new Label("Patient ID: " + patientCode);

        patientId.setStyle(
                "-fx-text-fill: " + TEXT_GRAY + ";" +
                        "-fx-font-size: 13px;"
        );


        patientMeta.getChildren().addAll(
                verified,
                patientId
        );


        Label member =
                new Label(profile.memberSince == null ? "Member"
                        : "Member since " + profile.memberSince.getYear());

        member.setStyle(
                "-fx-text-fill: " + TEXT_GRAY + ";" +
                        "-fx-font-size: 12px;"
        );


        profileInfo.getChildren().addAll(
                patientName,
                patientMeta,
                member,
                changePhoto
        );


        profileHeader.getChildren().addAll(
                avatar,
                profileInfo
        );


        // =========================================================
        // PROFILE COMPLETION
        // =========================================================

        VBox completionCard =
                createCard();


        HBox completionHeader =
                new HBox();


        completionHeader.setAlignment(
                Pos.CENTER_LEFT
        );


        Label completionTitle =
                sectionTitle(
                        "Profile Completion"
                );


        Region completionSpacer =
                new Region();

        HBox.setHgrow(
                completionSpacer,
                Priority.ALWAYS
        );


        Label completionPercent =
                new Label(completionPercentText(profile));

        completionPercent.setStyle(
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );


        completionHeader.getChildren().addAll(
                completionTitle,
                completionSpacer,
                completionPercent
        );


        ProgressBar progress =
                new ProgressBar(completionOf(profile));

        progress.setMaxWidth(
                Double.MAX_VALUE
        );

        progress.setPrefHeight(8);

        progress.setStyle(
                "-fx-accent: " + PRIMARY_BLUE + ";"
        );


        Label completionText =
                new Label(
                        "Add remaining health details and documents "
                                + "to complete your profile."
                );

        completionText.setStyle(
                "-fx-text-fill: " + TEXT_GRAY + ";" +
                        "-fx-font-size: 12px;"
        );


        completionCard.getChildren().addAll(
                completionHeader,
                progress,
                completionText
        );


        // =========================================================
        // BASIC INFORMATION
        // =========================================================

        VBox basicCard =
                createCard();


        basicCard.getChildren().add(
                sectionTitle("Basic Information")
        );


        GridPane basicGrid =
                createGrid();


        TextField name =
                createField(profile.fullName);


        TextField age =
                createField(ageText(profile.dateOfBirth));
        age.setEditable(false);          // calculated from the date of birth


        ComboBox<String> gender =
                new ComboBox<>();

        gender.getItems().addAll(
                "Male",
                "Female",
                "Other"
        );

        gender.setValue(gender.getItems().contains(profile.gender) ? profile.gender : null);

        styleControl(gender);


        DatePicker dob =
                new DatePicker(profile.dateOfBirth);
        dob.valueProperty().addListener((obs, oldValue, newValue) ->
                age.setText(ageText(newValue)));

        styleControl(dob);


        TextField phone =
                createField(profile.phone);


        TextField email =
                createField(profile.email);


        TextArea address =
                createArea(profile.address);


        basicGrid.add(
                fieldBox("Full Name", name),
                0,
                0
        );


        basicGrid.add(
                fieldBox("Age", age),
                1,
                0
        );


        basicGrid.add(
                fieldBox("Gender", gender),
                2,
                0
        );


        basicGrid.add(
                fieldBox("Date of Birth", dob),
                0,
                1
        );


        basicGrid.add(
                fieldBox("Phone Number", phone),
                1,
                1
        );


        basicGrid.add(
                fieldBox("Email Address", email),
                2,
                1
        );


        basicGrid.add(
                fieldBox("Address", address),
                0,
                2,
                3,
                1
        );


        basicCard.getChildren().add(
                basicGrid
        );


        // =========================================================
        // EMERGENCY CONTACT + BLOOD GROUP
        // =========================================================

        VBox emergencyCard =
                createCard();


        emergencyCard.getChildren().add(
                sectionTitle(
                        "Emergency Contact & Blood Group"
                )
        );


        GridPane emergencyGrid =
                createGrid();


        TextField emergencyName =
                createField("");


        TextField relationship =
                createField("");


        TextField emergencyPhone =
                createField(profile.emergencyPhone);


        ComboBox<String> bloodGroup =
                new ComboBox<>();


        bloodGroup.getItems().addAll(
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        );


        bloodGroup.setValue(bloodGroup.getItems().contains(profile.bloodGroup) ? profile.bloodGroup : null);

        styleControl(bloodGroup);


        emergencyGrid.add(
                fieldBox(
                        "Emergency Contact Name",
                        emergencyName
                ),
                0,
                0
        );


        emergencyGrid.add(
                fieldBox(
                        "Relationship",
                        relationship
                ),
                1,
                0
        );


        emergencyGrid.add(
                fieldBox(
                        "Emergency Phone",
                        emergencyPhone
                ),
                2,
                0
        );


        emergencyGrid.add(
                fieldBox(
                        "Blood Group",
                        bloodGroup
                ),
                0,
                1
        );


        emergencyCard.getChildren().add(
                emergencyGrid
        );


        // =========================================================
        // MEDICAL HISTORY
        // =========================================================

        VBox medicalHistory =
                createCard();


        medicalHistory.getChildren().add(
                sectionTitle(
                        "Medical History"
                )
        );


        Label medicalHint =
                sectionSubtitle(
                        "Select conditions that apply to you."
                );


        medicalHistory.getChildren().add(
                medicalHint
        );


        FlowPane conditions =
                new FlowPane();

        conditions.setHgap(15);
        conditions.setVgap(12);


        CheckBox diabetes =
                new CheckBox("Diabetes");


        CheckBox bp =
                new CheckBox("High Blood Pressure");


        CheckBox heart =
                new CheckBox("Heart Disease");


        CheckBox thyroid =
                new CheckBox("Thyroid");


        CheckBox asthma =
                new CheckBox("Asthma");


        CheckBox otherCondition =
                new CheckBox("Other");


        conditions.getChildren().addAll(
                diabetes,
                bp,
                heart,
                thyroid,
                asthma,
                otherCondition
        );


        for (CheckBox box : new CheckBox[]{
                diabetes,
                bp,
                heart,
                thyroid,
                asthma,
                otherCondition
        }) {

            box.setStyle(
                    "-fx-text-fill: " + TEXT_DARK + ";" +
                            "-fx-font-size: 13px;"
            );
        }


        TextArea previousDiseases =
                createArea(
                        "Previous diseases / important medical history..."
                );


        TextArea surgeries =
                createArea(
                        "Previous surgeries or operations..."
                );


        TextArea familyHistory =
                createArea(
                        "Family medical history..."
                );


        medicalHistory.getChildren().addAll(
                conditions,
                fieldBox(
                        "Previous Diseases",
                        previousDiseases
                ),
                fieldBox(
                        "Previous Surgeries / Operations",
                        surgeries
                ),
                fieldBox(
                        "Family Medical History",
                        familyHistory
                )
        );


        // =========================================================
        // ALLERGIES
        // =========================================================

        VBox allergyCard =
                createCard();


        allergyCard.getChildren().add(
                sectionTitle(
                        "Allergies"
                )
        );


        TextArea medicineAllergy =
                createArea(
                        "Example: Penicillin, Aspirin..."
                );


        TextArea foodAllergy =
                createArea(
                        "Example: Peanuts, shellfish..."
                );


        TextArea otherAllergy =
                createArea(
                        "Other allergies..."
                );


        allergyCard.getChildren().addAll(
                fieldBox(
                        "Medicine Allergies",
                        medicineAllergy
                ),
                fieldBox(
                        "Food Allergies",
                        foodAllergy
                ),
                fieldBox(
                        "Other Allergies",
                        otherAllergy
                )
        );


        // =========================================================
        // CURRENT MEDICATIONS
        // =========================================================

        VBox medicationCard =
                createCard();


        medicationCard.getChildren().add(
                sectionTitle(
                        "Current Medications"
                )
        );


        TextArea medications =
                createArea(
                        "Medicine Name | Dose | Frequency | Since When\n"
                                + "Example: Paracetamol | 500 mg | Twice daily | 3 days"
                );


        medicationCard.getChildren().add(
                fieldBox(
                        "Current Medicines",
                        medications
                )
        );


        // =========================================================
        // VACCINATION
        // =========================================================

        VBox vaccinationCard =
                createCard();


        vaccinationCard.getChildren().add(
                sectionTitle(
                        "Vaccination Record"
                )
        );


        TextArea vaccination =
                createArea(
                        "Vaccine | Date | Dose\n"
                                + "Example: COVID-19 | 12/04/2026 | Dose 2"
                );


        vaccinationCard.getChildren().add(
                fieldBox(
                        "Vaccination History",
                        vaccination
                )
        );


        // =========================================================
        // RECENT TESTS
        // =========================================================

        VBox testsCard =
                createCard();


        testsCard.getChildren().add(
                sectionTitle(
                        "Recent Test Results"
                )
        );


        TextArea tests =
                createArea(
                        "Test | Date | Result\n"
                                + "Example: Blood Test | 20/09/2026 | Normal"
                );


        testsCard.getChildren().add(
                fieldBox(
                        "Medical Test Results",
                        tests
                )
        );


        // =========================================================
        // DOCTOR VISITS
        // =========================================================

        VBox consultationCard =
                createCard();


        consultationCard.getChildren().add(
                sectionTitle(
                        "Doctor Visits & Consultations"
                )
        );


        TableView<Consultation> consultationTable =
                new TableView<>();


        TableColumn<Consultation, String> dateCol =
                new TableColumn<>("Date");


        TableColumn<Consultation, String> doctorCol =
                new TableColumn<>("Doctor");


        TableColumn<Consultation, String> reasonCol =
                new TableColumn<>("Reason");


        TableColumn<Consultation, String> diagnosisCol =
                new TableColumn<>("Diagnosis");


        TableColumn<Consultation, String> prescriptionCol =
                new TableColumn<>("Prescription");


        dateCol.setCellValueFactory(
                data -> data.getValue().dateProperty()
        );


        doctorCol.setCellValueFactory(
                data -> data.getValue().doctorProperty()
        );


        reasonCol.setCellValueFactory(
                data -> data.getValue().reasonProperty()
        );


        diagnosisCol.setCellValueFactory(
                data -> data.getValue().diagnosisProperty()
        );


        prescriptionCol.setCellValueFactory(
                data -> data.getValue().prescriptionProperty()
        );


        consultationTable.getColumns().addAll(
                dateCol,
                doctorCol,
                reasonCol,
                diagnosisCol,
                prescriptionCol
        );


        consultationTable.setPrefHeight(180);


        try {
            for (String[] row : profileDao.getConsultations(profile.patientId)) {
                consultationTable.getItems().add(
                        new Consultation(row[0], row[1], row[2], row[3], row[4])
                );
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        consultationTable.setPlaceholder(new Label("No consultations yet."));
        consultationCard.getChildren().add(
                consultationTable
        );


        // =========================================================
        // DIAGNOSIS NOTES
        // =========================================================

        VBox diagnosisCard =
                createCard();


        diagnosisCard.getChildren().add(
                sectionTitle(
                        "Diagnosis Notes"
                )
        );


        TextArea diagnosisNotes =
                createArea(
                        "Doctor diagnosis notes and consultation observations..."
                );


        diagnosisCard.getChildren().add(
                fieldBox(
                        "Medical Notes",
                        diagnosisNotes
                )
        );


        // =========================================================
        // CHANGE PASSWORD
        // =========================================================

        VBox passwordCard =
                createCard();

        passwordCard.getChildren().add(
                sectionTitle(
                        "Change Password"
                )
        );

        PasswordField currentPassword = new PasswordField();
        PasswordField newPassword = new PasswordField();
        PasswordField confirmPassword = new PasswordField();

        styleControl(currentPassword);
        styleControl(newPassword);
        styleControl(confirmPassword);

        Label passwordMessage = new Label();

        Button passwordButton =
                new Button("Update Password");

        passwordButton.setPrefHeight(40);
        passwordButton.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;"
        );

        passwordButton.setOnAction(event -> {

            String oldPw = currentPassword.getText();
            String newPw = newPassword.getText();

            if (oldPw.isEmpty() || newPw.isEmpty()) {
                passwordMessage.setText("Please fill in all password fields.");
                passwordMessage.setTextFill(Color.web(RED));
                return;
            }

            if (newPw.length() < 6) {
                passwordMessage.setText("New password must be at least 6 characters.");
                passwordMessage.setTextFill(Color.web(RED));
                return;
            }

            if (!newPw.equals(confirmPassword.getText())) {
                passwordMessage.setText("New password and confirmation do not match.");
                passwordMessage.setTextFill(Color.web(RED));
                return;
            }

            try {
                boolean changed = new com.healthcare.dao.UserDAO()
                        .changePassword(profile.patientId, oldPw, newPw);

                if (changed) {
                    passwordMessage.setText("✓ Password updated successfully.");
                    passwordMessage.setTextFill(Color.web(GREEN));
                    currentPassword.clear();
                    newPassword.clear();
                    confirmPassword.clear();
                } else {
                    passwordMessage.setText("Current password is incorrect.");
                    passwordMessage.setTextFill(Color.web(RED));
                }

            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                passwordMessage.setText("Database error: " + ex.getMessage());
                passwordMessage.setTextFill(Color.web(RED));
            }
        });

        passwordCard.getChildren().addAll(
                fieldBox("Current Password", currentPassword),
                fieldBox("New Password", newPassword),
                fieldBox("Confirm New Password", confirmPassword),
                new HBox(15, passwordButton, passwordMessage)
        );

        // =========================================================
        // SAVE MESSAGE
        // =========================================================

        Label message =
                new Label();


        message.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );


        // =========================================================
        // SAVE BUTTON
        // =========================================================

        Button saveButton =
                new Button("✓ Save Health Profile");


        saveButton.setPrefHeight(46);

        saveButton.setPrefWidth(200);


        saveButton.setStyle(
                "-fx-background-color: " + PRIMARY_BLUE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;"
        );


        // ---- fill the health notes from the database ----
        final com.healthcare.dao.PatientHealthDAO healthDao =
                new com.healthcare.dao.PatientHealthDAO();

        try {
            java.util.Map<String, String> saved = healthDao.load(profile.patientId);

            String savedConditions = saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.CONDITIONS, "");
            diabetes.setSelected(savedConditions.contains("Diabetes"));
            bp.setSelected(savedConditions.contains("High Blood Pressure"));
            heart.setSelected(savedConditions.contains("Heart Disease"));
            thyroid.setSelected(savedConditions.contains("Thyroid"));
            asthma.setSelected(savedConditions.contains("Asthma"));
            otherCondition.setSelected(savedConditions.contains("Other"));

            previousDiseases.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.PREVIOUS_DISEASES, ""));
            surgeries.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.SURGERIES, ""));
            familyHistory.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.FAMILY_HISTORY, ""));
            medicineAllergy.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.MEDICINE_ALLERGIES, ""));
            foodAllergy.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.FOOD_ALLERGIES, ""));
            otherAllergy.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.OTHER_ALLERGIES, ""));
            medications.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.MEDICATIONS, ""));
            vaccination.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.VACCINATIONS, ""));
            tests.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.TEST_RESULTS, ""));
            diagnosisNotes.setText(saved.getOrDefault(
                    com.healthcare.dao.PatientHealthDAO.MEDICAL_NOTES, ""));

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
        }

        saveButton.setOnAction(event -> {

            String patientNameValue =
                    name.getText().trim();


            String emailValue =
                    email.getText().trim();


            String phoneValue =
                    phone.getText().trim();


            if (patientNameValue.isEmpty()
                    || emailValue.isEmpty()
                    || phoneValue.isEmpty()) {

                message.setText(
                        "Please fill all required fields."
                );

                message.setTextFill(
                        Color.web(RED)
                );

            } else if (!emailValue.contains("@")) {

                message.setText(
                        "Please enter a valid email address."
                );

                message.setTextFill(
                        Color.web(RED)
                );

            } else {

                try {
                    com.healthcare.dao.PatientProfileDAO.Result result =
                            profileDao.save(
                                    profile.patientId,
                                    patientNameValue,
                                    emailValue,
                                    phoneValue,
                                    dob.getValue(),
                                    gender.getValue(),
                                    bloodGroup.getValue(),
                                    address.getText(),
                                    emergencyPhone.getText()
                            );

                    // health notes (conditions, allergies, medicines ...)
                    java.util.List<String> picked = new java.util.ArrayList<>();
                    if (diabetes.isSelected()) picked.add("Diabetes");
                    if (bp.isSelected()) picked.add("High Blood Pressure");
                    if (heart.isSelected()) picked.add("Heart Disease");
                    if (thyroid.isSelected()) picked.add("Thyroid");
                    if (asthma.isSelected()) picked.add("Asthma");
                    if (otherCondition.isSelected()) picked.add("Other");

                    java.util.Map<String, String> notes = new java.util.LinkedHashMap<>();
                    notes.put(com.healthcare.dao.PatientHealthDAO.CONDITIONS,
                            String.join(", ", picked));
                    notes.put(com.healthcare.dao.PatientHealthDAO.PREVIOUS_DISEASES,
                            previousDiseases.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.SURGERIES,
                            surgeries.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.FAMILY_HISTORY,
                            familyHistory.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.MEDICINE_ALLERGIES,
                            medicineAllergy.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.FOOD_ALLERGIES,
                            foodAllergy.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.OTHER_ALLERGIES,
                            otherAllergy.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.MEDICATIONS,
                            medications.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.VACCINATIONS,
                            vaccination.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.TEST_RESULTS,
                            tests.getText().trim());
                    notes.put(com.healthcare.dao.PatientHealthDAO.MEDICAL_NOTES,
                            diagnosisNotes.getText().trim());

                    if (result != com.healthcare.dao.PatientProfileDAO.Result.EMAIL_EXISTS) {
                        healthDao.save(profile.patientId, notes);
                    }

                    if (result == com.healthcare.dao.PatientProfileDAO.Result.EMAIL_EXISTS) {

                        message.setText(
                                "This email address is already used by another account."
                        );

                        message.setTextFill(
                                Color.web(RED)
                        );

                    } else {

                        // keep the name shown on every screen up to date
                        com.healthcare.model.User old = UserSession.getCurrentUser();
                        if (old != null) {
                            UserSession.setCurrentUser(new com.healthcare.model.User(
                                    old.getUserId(), patientNameValue, emailValue, old.getRole()));
                        }

                        message.setText(
                                "✓ Profile saved: personal details, contact and health notes."
                        );

                        message.setTextFill(
                                Color.web(GREEN)
                        );
                    }

                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();

                    message.setText(
                            "Database error: " + ex.getMessage()
                    );

                    message.setTextFill(
                            Color.web(RED)
                    );
                }
            }
        });


        HBox bottomActions =
                new HBox(15);

        bottomActions.setAlignment(
                Pos.CENTER_RIGHT
        );


        bottomActions.getChildren().addAll(
                message,
                saveButton
        );


        // =========================================================
        // ADD EVERYTHING
        // =========================================================

        content.getChildren().addAll(

                profileHeader,

                completionCard,

                basicCard,

                emergencyCard,

                medicalHistory,

                allergyCard,


                medicationCard,

                vaccinationCard,

                testsCard,

                consultationCard,

                diagnosisCard,
                passwordCard,





                bottomActions
        );


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
                "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-border-color: transparent;"
        );


        // =========================================================
        // NAVIGATION
        // =========================================================

        dashboardButton.setOnAction(event -> {

            PatientDashboard dashboard =
                    new PatientDashboard();

            dashboard.show(stage);
        });


        bookButton.setOnAction(event -> {

            BookAppointmentView view =
                    new BookAppointmentView();

            view.show(stage);
        });


        appointmentsButton.setOnAction(event -> {

            MyAppointmentsView view =
                    new MyAppointmentsView();

            view.show(stage);
        });


        historyButton.setOnAction(event -> {

            MedicalHistoryView view =
                    new MedicalHistoryView();

            view.show(stage);
        });


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
                "Patient Health Profile - MediCare"
        );


        stage.setScene(scene);

        stage.show();
    }


    // =============================================================
    // CREATE DEFAULT AVATAR
    // =============================================================

    // =============================================================
    // DATA HELPERS
    // =============================================================

    private com.healthcare.dao.PatientProfileDAO.Profile loadProfile(
            com.healthcare.dao.PatientProfileDAO dao
    ) {
        try {
            com.healthcare.dao.PatientProfileDAO.Profile p = dao.load(UserSession.getUserId());
            if (p != null) {
                return p;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database error");
            alert.setHeaderText("Could not load your profile");
            alert.setContentText(e.getMessage());
            alert.show();
        }

        // fall back to an empty profile so the screen can still open
        return new com.healthcare.dao.PatientProfileDAO.Profile(
                UserSession.getUserId(), UserSession.getFullName(),
                UserSession.getCurrentUser() == null ? "" : UserSession.getCurrentUser().getEmail(),
                "", null, null, null, "", "", null);
    }

    private String ageText(java.time.LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return "";
        }
        return String.valueOf(java.time.Period.between(dateOfBirth, java.time.LocalDate.now()).getYears());
    }

    /** Share of the 8 profile fields that are filled in (0.0 - 1.0). */
    private double completionOf(com.healthcare.dao.PatientProfileDAO.Profile p) {
        int filled = 0;
        if (!p.fullName.isEmpty())        filled++;
        if (!p.email.isEmpty())           filled++;
        if (!p.phone.isEmpty())           filled++;
        if (p.dateOfBirth != null)        filled++;
        if (p.gender != null)             filled++;
        if (p.bloodGroup != null)         filled++;
        if (!p.address.isEmpty())         filled++;
        if (!p.emergencyPhone.isEmpty())  filled++;
        return filled / 8.0;
    }

    private String completionPercentText(com.healthcare.dao.PatientProfileDAO.Profile p) {
        return Math.round(completionOf(p) * 100) + "%";
    }

    private StackPane createDefaultAvatar() {

        Circle circle =
                new Circle(
                        62,
                        Color.web("#DCEEFF")
                );


        Label initials =
                new Label(UserSession.getInitials());


        initials.setStyle(
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                        "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;"
        );


        StackPane avatar =
                new StackPane(
                        circle,
                        initials
                );


        return avatar;
    }


    // =============================================================
    // NAV BUTTON
    // =============================================================

    private Button createNavButton(
            String icon,
            String text,
            boolean active
    ) {

        Button button =
                new Button(
                        icon + "   " + text
                );


        button.setMaxWidth(
                Double.MAX_VALUE
        );


        button.setAlignment(
                Pos.CENTER_LEFT
        );


        button.setPrefHeight(44);


        if (active) {

            button.setStyle(
                    "-fx-background-color: #2A5D88;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 9;" +
                            "-fx-padding: 0 14;" +
                            "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #C8D9E8;" +
                            "-fx-font-size: 13px;" +
                            "-fx-background-radius: 9;" +
                            "-fx-padding: 0 14;" +
                            "-fx-cursor: hand;"
            );
        }


        return button;
    }


    // =============================================================
    // CARD
    // =============================================================

    private VBox createCard() {

        VBox card =
                new VBox(15);


        card.setPadding(
                new Insets(22)
        );


        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 14;"
        );


        return card;
    }


    // =============================================================
    // SECTION TITLE
    // =============================================================

    private Label sectionTitle(
            String text
    ) {

        Label label =
                new Label(text);


        label.setStyle(
                "-fx-text-fill: " + TEXT_DARK + ";" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;"
        );


        return label;
    }


    // =============================================================
    // SECTION SUBTITLE
    // =============================================================

    private Label sectionSubtitle(
            String text
    ) {

        Label label =
                new Label(text);


        label.setStyle(
                "-fx-text-fill: " + TEXT_GRAY + ";" +
                        "-fx-font-size: 12px;"
        );


        return label;
    }


    // =============================================================
    // GRID
    // =============================================================

    private GridPane createGrid() {

        GridPane grid =
                new GridPane();


        grid.setHgap(18);

        grid.setVgap(18);


        for (int i = 0; i < 3; i++) {

            ColumnConstraints column =
                    new ColumnConstraints();


            column.setPercentWidth(33.33);


            grid.getColumnConstraints()
                    .add(column);
        }


        return grid;
    }


    // =============================================================
    // TEXT FIELD
    // =============================================================

    private TextField createField(
            String text
    ) {

        TextField field =
                new TextField(text);


        field.setPrefHeight(42);

        field.setMaxWidth(
                Double.MAX_VALUE
        );


        styleControl(field);


        return field;
    }


    // =============================================================
    // TEXT AREA
    // =============================================================

    private TextArea createArea(
            String text
    ) {

        TextArea area =
                new TextArea(text);


        area.setWrapText(true);

        area.setPrefRowCount(3);

        area.setMaxWidth(
                Double.MAX_VALUE
        );


        area.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10;"
        );


        return area;
    }


    // =============================================================
    // COMBO BOX
    // =============================================================

    private ComboBox<String> createCombo(
            String... values
    ) {

        ComboBox<String> combo =
                new ComboBox<>();


        combo.getItems().addAll(values);


        combo.setValue(
                values[0]
        );


        combo.setMaxWidth(
                Double.MAX_VALUE
        );


        combo.setPrefHeight(42);


        styleControl(combo);


        return combo;
    }


    // =============================================================
    // CONTROL STYLE
    // =============================================================

    private void styleControl(
            Control control
    ) {

        control.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 9;" +
                        "-fx-background-radius: 9;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 0 10;"
        );
    }


    // =============================================================
    // FIELD BOX
    // =============================================================

    private VBox fieldBox(
            String labelText,
            Node control
    ) {

        VBox box =
                new VBox(7);


        Label label =
                new Label(labelText);


        label.setStyle(
                "-fx-text-fill: " + TEXT_DARK + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );


        box.getChildren().addAll(
                label,
                control
        );


        return box;
    }


    // =============================================================
    // CONSULTATION MODEL
    // =============================================================

    public static class Consultation {

        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleStringProperty doctor;
        private final javafx.beans.property.SimpleStringProperty reason;
        private final javafx.beans.property.SimpleStringProperty diagnosis;
        private final javafx.beans.property.SimpleStringProperty prescription;


        public Consultation(
                String date,
                String doctor,
                String reason,
                String diagnosis,
                String prescription
        ) {

            this.date =
                    new javafx.beans.property.SimpleStringProperty(date);

            this.doctor =
                    new javafx.beans.property.SimpleStringProperty(doctor);

            this.reason =
                    new javafx.beans.property.SimpleStringProperty(reason);

            this.diagnosis =
                    new javafx.beans.property.SimpleStringProperty(diagnosis);

            this.prescription =
                    new javafx.beans.property.SimpleStringProperty(prescription);
        }


        public javafx.beans.property.StringProperty dateProperty() {
            return date;
        }


        public javafx.beans.property.StringProperty doctorProperty() {
            return doctor;
        }


        public javafx.beans.property.StringProperty reasonProperty() {
            return reason;
        }


        public javafx.beans.property.StringProperty diagnosisProperty() {
            return diagnosis;
        }


        public javafx.beans.property.StringProperty prescriptionProperty() {
            return prescription;
        }
    }
}