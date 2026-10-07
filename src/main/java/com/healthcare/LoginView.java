package com.healthcare;

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

public class LoginView {

    public void show(Stage stage) {

        // ============================================================
        // LEFT SIDE - HEALTHCARE BRANDING
        // ============================================================

        VBox leftPanel = new VBox();
        leftPanel.setAlignment(Pos.CENTER_LEFT);
        leftPanel.setPadding(new Insets(55));
        leftPanel.setPrefWidth(470);
        leftPanel.getStyleClass().add("login-left");

        // Logo
        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        StackPane logoShape = new StackPane();
        logoShape.getStyleClass().add("logo-shape");

        Rectangle vertical = new Rectangle(7, 28);
        Rectangle horizontal = new Rectangle(28, 7);

        vertical.setFill(Color.WHITE);
        horizontal.setFill(Color.WHITE);

        logoShape.getChildren().addAll(vertical, horizontal);

        Label logoText = new Label("MediCare");
        logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 27));
        logoText.getStyleClass().add("logo-text");

        logoBox.getChildren().addAll(logoShape, logoText);

        // Main heading
        Label heading = new Label("Your Health,\nOur Priority.");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        heading.getStyleClass().add("left-heading");

        // Description
        Label description = new Label(
                "A smarter way to manage appointments,\n" +
                        "medical records and healthcare services."
        );
        description.setFont(Font.font("Segoe UI", 16));
        description.getStyleClass().add("left-description");

        // Features
        VBox features = new VBox(18);

        features.getChildren().add(
                createFeature("Easy appointment management")
        );

        features.getChildren().add(
                createFeature("Secure patient records")
        );

        features.getChildren().add(
                createFeature("Doctor schedule management")
        );

        // Bottom text
        Region leftSpacer = new Region();
        VBox.setVgrow(leftSpacer, Priority.ALWAYS);

        Label bottomText = new Label("Secure  •  Simple  •  Connected");
        bottomText.getStyleClass().add("left-bottom-text");

        leftPanel.getChildren().addAll(
                logoBox,
                new Region() {{
                    setPrefHeight(75);
                }},
                heading,
                new Region() {{
                    setPrefHeight(20);
                }},
                description,
                new Region() {{
                    setPrefHeight(35);
                }},
                features,
                leftSpacer,
                bottomText
        );

        // ============================================================
        // RIGHT SIDE - LOGIN CARD
        // ============================================================

        StackPane rightPanel = new StackPane();
        rightPanel.setPadding(new Insets(40));
        rightPanel.getStyleClass().add("login-right");

        VBox loginCard = new VBox(15);
        loginCard.setMaxWidth(500);
        loginCard.setPrefWidth(500);
        loginCard.setPadding(new Insets(42, 48, 38, 48));
        loginCard.getStyleClass().add("login-card");

        // Welcome
        Label welcome = new Label("Welcome Back");
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        welcome.getStyleClass().add("welcome-title");

        Label subtitle = new Label(
                "Sign in to access your healthcare dashboard"
        );
        subtitle.getStyleClass().add("welcome-subtitle");

        // ============================================================
        // EMAIL
        // ============================================================

        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("field-label");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email address");
        emailField.setPrefHeight(48);
        emailField.getStyleClass().add("login-field");

        // ============================================================
        // PASSWORD
        // ============================================================

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(48);
        passwordField.getStyleClass().add("login-field");

        // ============================================================
        // ROLE
        // ============================================================

        Label roleLabel = new Label("Login As");
        roleLabel.getStyleClass().add("field-label");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(
                "Patient",
                "Doctor",
                "Admin"
        );
        roleComboBox.setValue("Patient");
        roleComboBox.setPrefHeight(48);
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.getStyleClass().add("login-combo");

        // ============================================================
        // REMEMBER + FORGOT PASSWORD
        // ============================================================

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.getStyleClass().add("remember-checkbox");

        Hyperlink forgotPassword = new Hyperlink("Forgot Password?");
        forgotPassword.getStyleClass().add("forgot-link");

        HBox optionsRow = new HBox();
        optionsRow.setAlignment(Pos.CENTER_LEFT);

        Region optionSpacer = new Region();
        HBox.setHgrow(optionSpacer, Priority.ALWAYS);

        optionsRow.getChildren().addAll(
                rememberMe,
                optionSpacer,
                forgotPassword
        );

        // ============================================================
        // LOGIN BUTTON
        // ============================================================

        Button loginButton = new Button("Sign In");
        loginButton.setPrefHeight(50);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.getStyleClass().add("login-button");

        // ============================================================
        // CREATE ACCOUNT
        // ============================================================

        HBox signupRow = new HBox(5);
        signupRow.setAlignment(Pos.CENTER);

        Label accountText = new Label("Don't have an account?");
        accountText.getStyleClass().add("account-text");

        Hyperlink signupLink = new Hyperlink("Create Account");
        signupLink.getStyleClass().add("signup-link");

        signupRow.getChildren().addAll(
                accountText,
                signupLink
        );

        // ============================================================
        // LOGIN ACTION
        // ============================================================

        loginButton.setOnAction(event -> {

            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleComboBox.getValue();

            if (email.isEmpty()) {
                showError("Please enter your email address.");
                emailField.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                showError("Please enter your password.");
                passwordField.requestFocus();
                return;
            }

            if (!email.contains("@")) {
                showError("Please enter a valid email address.");
                emailField.requestFocus();
                return;
            }

            // --------------------------------------------------------
            // DATABASE LOGIN CHECK
            // --------------------------------------------------------

            try {
                com.healthcare.model.User user =
                        new com.healthcare.dao.UserDAO()
                                .login(email, password, role);

                if (user == null) {
                    showError("Invalid email, password or role.");
                    return;
                }

                // remember who is logged in
                com.healthcare.model.UserSession.setCurrentUser(user);

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                showError("Database error: " + e.getMessage());
                return;
            }

            // --------------------------------------------------------
            // ROLE BASED NAVIGATION
            // --------------------------------------------------------

            if (role.equals("Patient")) {

                PatientDashboard dashboard =
                        new PatientDashboard();

                dashboard.show(stage);

            } else if (role.equals("Doctor")) {

                DoctorDashboard dashboard =
                        new DoctorDashboard();

                dashboard.show(stage);

            } else if (role.equals("Admin")) {

                AdminDashboard dashboard =
                        new AdminDashboard();

                dashboard.show(stage);
            }
        });

        // Enter key = Login
        passwordField.setOnAction(event ->
                loginButton.fire()
        );

        // ============================================================
        // FORGOT PASSWORD
        // ============================================================

        forgotPassword.setOnAction(event -> {

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Forgot Password");
            dialog.setHeaderText("Reset your password");

            TextField resetEmail = new TextField(emailField.getText().trim());
            resetEmail.setPromptText("Registered email");

            TextField resetPhone = new TextField();
            resetPhone.setPromptText("Phone number used at registration");

            PasswordField resetNew = new PasswordField();
            resetNew.setPromptText("New password (min 6 characters)");

            PasswordField resetConfirm = new PasswordField();
            resetConfirm.setPromptText("Confirm new password");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(15));
            grid.add(new Label("Email"), 0, 0);
            grid.add(resetEmail, 1, 0);
            grid.add(new Label("Phone"), 0, 1);
            grid.add(resetPhone, 1, 1);
            grid.add(new Label("New password"), 0, 2);
            grid.add(resetNew, 1, 2);
            grid.add(new Label("Confirm"), 0, 3);
            grid.add(resetConfirm, 1, 3);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(choice -> {

                if (choice != ButtonType.OK) {
                    return;
                }

                String mail = resetEmail.getText().trim();
                String phoneNo = resetPhone.getText().trim();
                String pass = resetNew.getText();

                if (mail.isEmpty() || phoneNo.isEmpty() || pass.isEmpty()) {
                    showError("Please fill in email, phone and the new password.");
                    return;
                }

                if (pass.length() < 6) {
                    showError("The new password must have at least 6 characters.");
                    return;
                }

                if (!pass.equals(resetConfirm.getText())) {
                    showError("The two passwords do not match.");
                    return;
                }

                try {
                    boolean changed = new com.healthcare.dao.UserDAO()
                            .resetPassword(mail, phoneNo, pass);

                    if (changed) {
                        Alert ok = new Alert(Alert.AlertType.INFORMATION);
                        ok.setTitle("Password Reset");
                        ok.setHeaderText("Password changed");
                        ok.setContentText("You can now log in with your new password.");
                        ok.showAndWait();
                    } else {
                        showError("No active account matches that email and phone number.");
                    }

                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                    showError("Database error: " + e.getMessage());
                }
            });
        });

        // ============================================================
        // SIGN UP
        // ============================================================

        signupLink.setOnAction(event -> {

            RegistrationView registrationView = new RegistrationView();

            registrationView.show(stage);
        });

        // ============================================================
        // ADD EVERYTHING TO LOGIN CARD
        // ============================================================

        loginCard.getChildren().addAll(
                welcome,
                subtitle,

                createSpacing(15),

                emailLabel,
                emailField,

                passwordLabel,
                passwordField,

                roleLabel,
                roleComboBox,

                optionsRow,

                createSpacing(5),

                loginButton,

                createSpacing(8),

                signupRow
        );

        rightPanel.getChildren().add(loginCard);

        // ============================================================
        // MAIN LAYOUT
        // ============================================================

        HBox mainLayout = new HBox(
                leftPanel,
                rightPanel
        );

        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // ============================================================
        // SCENE
        // ============================================================

        Scene scene = new Scene(
                mainLayout,
                1200,
                720
        );

        // ============================================================
        // LOAD CSS
        // ============================================================

        var css = getClass()
                .getResource("/com/healthcare/styles.css");

        if (css == null) {
            css = getClass()
                    .getResource("/styles.css");
        }

        if (css != null) {
            scene.getStylesheets().add(
                    css.toExternalForm()
            );
        }

        // ============================================================
        // STAGE
        // ============================================================

        stage.setTitle("MediCare - Healthcare Management System");
        stage.setScene(scene);

        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        stage.show();
    }

    // ================================================================
    // FEATURE CREATOR
    // ================================================================

    private HBox createFeature(String text) {

        Circle circle = new Circle(13);
        circle.getStyleClass().add("feature-circle");

        Label check = new Label("✓");
        check.getStyleClass().add("feature-check");

        StackPane icon = new StackPane(
                circle,
                check
        );

        Label label = new Label(text);
        label.getStyleClass().add("feature-text");

        HBox box = new HBox(
                12,
                icon,
                label
        );

        box.setAlignment(Pos.CENTER_LEFT);

        return box;
    }

    // ================================================================
    // SPACING
    // ================================================================

    private Region createSpacing(double height) {

        Region region = new Region();
        region.setPrefHeight(height);

        return region;
    }

    // ================================================================
    // ERROR ALERT
    // ================================================================

    private void showError(String message) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Login Error");
        alert.setHeaderText("Unable to Sign In");
        alert.setContentText(message);

        alert.showAndWait();
    }
}