package com.healthcare;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegistrationView {

    public void show(Stage stage) {

        // ============================================================
        // LEFT SIDE - BRANDING
        // ============================================================

        VBox leftPanel = new VBox();
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(55));
        leftPanel.setPrefWidth(430);
        leftPanel.setStyle("-fx-background-color: #12355B;");

        // Logo
        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        StackPane logoShape = new StackPane();
        logoShape.setStyle(
                "-fx-background-color: #2D9CDB;" +
                        "-fx-background-radius: 10;"
        );
        logoShape.setMinSize(45, 45);
        logoShape.setMaxSize(45, 45);

        Rectangle vertical = new Rectangle(7, 28);
        Rectangle horizontal = new Rectangle(28, 7);

        vertical.setFill(Color.WHITE);
        horizontal.setFill(Color.WHITE);

        logoShape.getChildren().addAll(vertical, horizontal);

        Label logoText = new Label("MediCare");
        logoText.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 27)
        );
        logoText.setTextFill(Color.WHITE);

        logoBox.getChildren().addAll(logoShape, logoText);

        // Heading
        Label heading = new Label(
                "Create Your\nHealthcare Account"
        );
        heading.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 38)
        );
        heading.setTextFill(Color.WHITE);

        // Description
        Label description = new Label(
                "Join MediCare and manage your appointments,\n" +
                        "medical records and healthcare services easily."
        );
        description.setFont(Font.font("Segoe UI", 15));
        description.setTextFill(Color.web("#D7E6F5"));

        // Features
        VBox features = new VBox(18);

        features.getChildren().add(
                createFeature("Easy appointment booking")
        );

        features.getChildren().add(
                createFeature("Secure medical records")
        );

        features.getChildren().add(
                createFeature("Manage your healthcare profile")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label bottomText = new Label(
                "Secure  •  Simple  •  Connected"
        );
        bottomText.setTextFill(Color.web("#9FB8D2"));
        bottomText.setFont(Font.font("Segoe UI", 13));

        leftPanel.getChildren().addAll(
                logoBox,
                createSpacing(65),
                heading,
                createSpacing(20),
                description,
                createSpacing(35),
                features,
                spacer,
                bottomText
        );


        // ============================================================
        // RIGHT SIDE - REGISTRATION FORM
        // ============================================================

        StackPane rightPanel = new StackPane();
        rightPanel.setPadding(new Insets(30));
        rightPanel.setStyle("-fx-background-color: #F4F7FB;");

        VBox registrationCard = new VBox(12);
        registrationCard.setMaxWidth(650);
        registrationCard.setPadding(
                new Insets(30, 40, 30, 40)
        );

        registrationCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 18;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 25, 0.15, 0, 8);"
        );


        // ============================================================
        // TITLE
        // ============================================================

        Label title = new Label("Create Account");
        title.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 30)
        );
        title.setTextFill(Color.web("#163B63"));

        Label subtitle = new Label(
                "Create your patient account to get started"
        );
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#64748B"));


        // ============================================================
        // BASIC INFORMATION
        // ============================================================

        Label basicInfo = createSectionTitle("Basic Information");


        // Name
        Label nameLabel = createFieldLabel("Full Name");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setPrefHeight(45);
        styleField(nameField);


        // Email
        Label emailLabel = createFieldLabel("Email Address");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setPrefHeight(45);
        styleField(emailField);


        // Phone
        Label phoneLabel = createFieldLabel("Phone Number");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter your phone number");
        phoneField.setPrefHeight(45);
        styleField(phoneField);


        // ============================================================
        // TWO COLUMN ROW
        // ============================================================

        VBox dobBox = new VBox(6);

        Label dobLabel = createFieldLabel("Date of Birth");

        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select date");
        dobPicker.setPrefHeight(45);
        dobPicker.setMaxWidth(Double.MAX_VALUE);

        styleDatePicker(dobPicker);

        dobBox.getChildren().addAll(
                dobLabel,
                dobPicker
        );


        VBox genderBox = new VBox(6);

        Label genderLabel = createFieldLabel("Gender");

        ComboBox<String> genderComboBox = new ComboBox<>();

        genderComboBox.getItems().addAll(
                "Male",
                "Female",
                "Other"
        );

        genderComboBox.setPromptText("Select gender");
        genderComboBox.setPrefHeight(45);
        genderComboBox.setMaxWidth(Double.MAX_VALUE);

        styleComboBox(genderComboBox);

        genderBox.getChildren().addAll(
                genderLabel,
                genderComboBox
        );


        HBox personalRow = new HBox(15);

        HBox.setHgrow(dobBox, Priority.ALWAYS);
        HBox.setHgrow(genderBox, Priority.ALWAYS);

        personalRow.getChildren().addAll(
                dobBox,
                genderBox
        );


        // ============================================================
        // BLOOD GROUP
        // ============================================================

        Label bloodLabel = createFieldLabel("Blood Group");

        ComboBox<String> bloodComboBox = new ComboBox<>();

        bloodComboBox.getItems().addAll(
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"
        );

        bloodComboBox.setPromptText("Select blood group");
        bloodComboBox.setPrefHeight(45);
        bloodComboBox.setMaxWidth(Double.MAX_VALUE);

        styleComboBox(bloodComboBox);


        // ============================================================
        // ADDRESS
        // ============================================================

        Label addressLabel = createFieldLabel("Address");

        TextArea addressField = new TextArea();

        addressField.setPromptText(
                "Enter your complete address"
        );

        addressField.setPrefRowCount(2);
        addressField.setWrapText(true);

        addressField.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 14px;"
        );


        // ============================================================
        // EMERGENCY CONTACT
        // ============================================================

        Label emergencyLabel =
                createFieldLabel("Emergency Contact");

        TextField emergencyField = new TextField();

        emergencyField.setPromptText(
                "Emergency contact number"
        );

        emergencyField.setPrefHeight(45);

        styleField(emergencyField);


        // ============================================================
        // SECURITY INFORMATION
        // ============================================================

        Label securityTitle =
                createSectionTitle("Account Security");


        // Password
        Label passwordLabel =
                createFieldLabel("Password");

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Create a password"
        );

        passwordField.setPrefHeight(45);

        styleField(passwordField);


        // Confirm Password
        Label confirmPasswordLabel =
                createFieldLabel("Confirm Password");

        PasswordField confirmPasswordField =
                new PasswordField();

        confirmPasswordField.setPromptText(
                "Confirm your password"
        );

        confirmPasswordField.setPrefHeight(45);

        styleField(confirmPasswordField);


        // ============================================================
        // TERMS
        // ============================================================

        CheckBox termsCheckBox =
                new CheckBox(
                        "I agree to the Terms & Conditions and Privacy Policy"
                );

        termsCheckBox.setStyle(
                "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 13px;"
        );


        // ============================================================
        // CREATE ACCOUNT BUTTON
        // ============================================================

        Button createAccountButton =
                new Button("Create Account");

        createAccountButton.setPrefHeight(48);
        createAccountButton.setMaxWidth(
                Double.MAX_VALUE
        );

        createAccountButton.setStyle(
                "-fx-background-color: #2684D9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );


        // ============================================================
        // BACK TO LOGIN
        // ============================================================

        HBox loginRow = new HBox(5);
        loginRow.setAlignment(Pos.CENTER);

        Label alreadyAccount =
                new Label("Already have an account?");

        alreadyAccount.setStyle(
                "-fx-text-fill: #64748B;" +
                        "-fx-font-size: 13px;"
        );

        Hyperlink loginLink =
                new Hyperlink("Sign In");

        loginLink.setStyle(
                "-fx-text-fill: #1976D2;" +
                        "-fx-font-size: 13px;"
        );

        loginRow.getChildren().addAll(
                alreadyAccount,
                loginLink
        );


        // ============================================================
        // CREATE ACCOUNT ACTION
        // ============================================================

        createAccountButton.setOnAction(event -> {

            String name =
                    nameField.getText().trim();

            String email =
                    emailField.getText().trim();

            String phone =
                    phoneField.getText().trim();

            String password =
                    passwordField.getText();

            String confirmPassword =
                    confirmPasswordField.getText();


            // Name validation
            if (name.isEmpty()) {

                showError(
                        "Please enter your full name."
                );

                nameField.requestFocus();
                return;
            }


            // Email validation
            if (email.isEmpty()) {

                showError(
                        "Please enter your email address."
                );

                emailField.requestFocus();
                return;
            }

            if (!email.contains("@")) {

                showError(
                        "Please enter a valid email address."
                );

                emailField.requestFocus();
                return;
            }


            // Phone validation
            if (phone.isEmpty()) {

                showError(
                        "Please enter your phone number."
                );

                phoneField.requestFocus();
                return;
            }


            if (phone.length() != 10) {

                showError(
                        "Phone number must contain 10 digits."
                );

                phoneField.requestFocus();
                return;
            }


            // Gender
            if (genderComboBox.getValue() == null) {

                showError(
                        "Please select your gender."
                );

                return;
            }


            // Blood Group
            if (bloodComboBox.getValue() == null) {

                showError(
                        "Please select your blood group."
                );

                return;
            }


            // Password
            if (password.isEmpty()) {

                showError(
                        "Please create a password."
                );

                passwordField.requestFocus();
                return;
            }


            if (password.length() < 6) {

                showError(
                        "Password must contain at least 6 characters."
                );

                passwordField.requestFocus();
                return;
            }


            // Confirm Password
            if (!password.equals(confirmPassword)) {

                showError(
                        "Passwords do not match."
                );

                confirmPasswordField.requestFocus();
                return;
            }


            // Terms
            if (!termsCheckBox.isSelected()) {

                showError(
                        "Please accept the Terms & Conditions."
                );

                return;
            }


            // ========================================================
            // SAVE TO DATABASE
            // ========================================================

            String emergency = emergencyField.getText().trim();

            if (emergency.length() > 15) {
                showError("Emergency contact number is too long.");
                emergencyField.requestFocus();
                return;
            }

            try {
                boolean saved =
                        new com.healthcare.dao.UserDAO().registerPatient(
                                name,
                                email,
                                phone,
                                password,
                                dobPicker.getValue(),
                                genderComboBox.getValue(),
                                bloodComboBox.getValue(),
                                addressField.getText().trim(),
                                emergency
                        );

                if (!saved) {
                    showError("This email is already registered. Please sign in.");
                    emailField.requestFocus();
                    return;
                }

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                showError("Database error: " + e.getMessage());
                return;
            }

            // ========================================================
            // SUCCESS
            // ========================================================

            Alert success =
                    new Alert(
                            Alert.AlertType.INFORMATION
                    );

            success.setTitle("Registration Successful");
            success.setHeaderText(
                    "Welcome to MediCare!"
            );

            success.setContentText(
                    "Your patient account has been created successfully.\n\n" +
                            "You can now sign in using your email and password."
            );

            success.showAndWait();


            // Return to Login
            LoginView loginView =
                    new LoginView();

            loginView.show(stage);
        });


        // ============================================================
        // SIGN IN ACTION
        // ============================================================

        loginLink.setOnAction(event -> {

            LoginView loginView =
                    new LoginView();

            loginView.show(stage);
        });


        // ============================================================
        // ADD EVERYTHING TO CARD
        // ============================================================

        registrationCard.getChildren().addAll(

                title,
                subtitle,

                createSpacing(5),

                basicInfo,

                nameLabel,
                nameField,

                emailLabel,
                emailField,

                phoneLabel,
                phoneField,

                personalRow,

                bloodLabel,
                bloodComboBox,

                addressLabel,
                addressField,

                emergencyLabel,
                emergencyField,

                securityTitle,

                passwordLabel,
                passwordField,

                confirmPasswordLabel,
                confirmPasswordField,

                termsCheckBox,

                createSpacing(3),

                createAccountButton,

                createSpacing(2),

                loginRow
        );


        // ============================================================
        // SCROLL PANE
        // ============================================================

        ScrollPane scrollPane =
                new ScrollPane(registrationCard);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );


        rightPanel.getChildren().add(
                scrollPane
        );


        // ============================================================
        // MAIN LAYOUT
        // ============================================================

        HBox mainLayout =
                new HBox(
                        leftPanel,
                        rightPanel
                );

        HBox.setHgrow(
                rightPanel,
                Priority.ALWAYS
        );


        // ============================================================
        // SCENE
        // ============================================================

        Scene scene =
                new Scene(
                        mainLayout,
                        1250,
                        800
                );


        stage.setTitle(
                "MediCare - Create Account"
        );

        stage.setScene(scene);

        stage.setMinWidth(1050);
        stage.setMinHeight(700);

        stage.show();
    }


    // ================================================================
    // FEATURE CREATOR
    // ================================================================

    private HBox createFeature(String text) {

        Label check =
                new Label("✓");

        check.setTextFill(Color.WHITE);
        check.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        StackPane icon =
                new StackPane();

        icon.setStyle(
                "-fx-background-color: #2D9CDB;" +
                        "-fx-background-radius: 50;"
        );

        icon.setMinSize(26, 26);
        icon.setMaxSize(26, 26);

        icon.getChildren().add(check);


        Label label =
                new Label(text);

        label.setTextFill(
                Color.web("#E1EDF8")
        );

        label.setFont(
                Font.font("Segoe UI", 15)
        );


        HBox box =
                new HBox(
                        12,
                        icon,
                        label
                );

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        return box;
    }


    // ================================================================
    // FIELD LABEL
    // ================================================================

    private Label createFieldLabel(String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        label.setTextFill(
                Color.web("#183B60")
        );

        return label;
    }


    // ================================================================
    // SECTION TITLE
    // ================================================================

    private Label createSectionTitle(String text) {

        Label label =
                new Label(text);

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        17
                )
        );

        label.setTextFill(
                Color.web("#12355B")
        );

        return label;
    }


    // ================================================================
    // TEXT FIELD STYLE
    // ================================================================

    private void styleField(TextInputControl field) {

        field.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 12;" +
                        "-fx-font-size: 14px;"
        );
    }


    // ================================================================
    // COMBO BOX STYLE
    // ================================================================

    private void styleComboBox(
            ComboBox<String> comboBox
    ) {

        comboBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 14px;"
        );
    }


    // ================================================================
    // DATE PICKER STYLE
    // ================================================================

    private void styleDatePicker(
            DatePicker datePicker
    ) {

        datePicker.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 14px;"
        );
    }


    // ================================================================
    // SPACING
    // ================================================================

    private Region createSpacing(double height) {

        Region region =
                new Region();

        region.setPrefHeight(height);

        return region;
    }


    // ================================================================
    // ERROR ALERT
    // ================================================================

    private void showError(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Registration Error"
        );

        alert.setHeaderText(
                "Unable to Create Account"
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }
}