package com.healthcare;

import com.healthcare.dao.UserAdminDAO;
import com.healthcare.model.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class UserManagementView {

    private final UserAdminDAO dao = new UserAdminDAO();

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("User Management");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        Label subtitle = new Label(
                "Manage patients, doctors and administrators."
        );
        subtitle.setStyle("-fx-text-fill: #666666;");

        VBox heading = new VBox(5, title, subtitle);

        // =========================
        // USER TABLE
        // =========================

        TableView<UserAdminDAO.UserRow> table = new TableView<>();

        table.getColumns().add(column("Name", 180, u -> u.name));
        table.getColumns().add(column("Email", 250, u -> u.email));
        table.getColumns().add(column("Role", 110, u -> u.role));
        table.getColumns().add(column("Status", 110, u -> u.status));

        table.setPrefHeight(300);

        // =========================
        // INPUT FIELDS
        // =========================

        Label nameLabel = new Label("Name");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");

        Label emailLabel = new Label("Email");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");

        Label roleLabel = new Label("Role");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll(
                "Patient",
                "Doctor",
                "Admin"
        );
        roleBox.setPromptText("Select role");
        roleBox.setPrefWidth(150);

        Label specialityLabel = new Label("Speciality (doctors)");

        ComboBox<String> specialityBox = new ComboBox<>();
        specialityBox.getItems().addAll(
                "General Physician",
                "Cardiologist",
                "Dermatologist",
                "Pediatrician",
                "Orthopedic",
                "Neurologist"
        );
        specialityBox.setPromptText("Select speciality");
        specialityBox.setPrefWidth(170);
        specialityBox.setDisable(true);

        // speciality is only needed when the role is Doctor
        roleBox.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        specialityBox.setDisable(!"Doctor".equals(newValue))
        );

        VBox nameBox = new VBox(5, nameLabel, nameField);
        VBox emailBox = new VBox(5, emailLabel, emailField);
        VBox roleBoxContainer = new VBox(5, roleLabel, roleBox);
        VBox specialityContainer = new VBox(5, specialityLabel, specialityBox);

        HBox form = new HBox(
                15,
                nameBox,
                emailBox,
                roleBoxContainer,
                specialityContainer
        );

        Label hint = new Label(
                "New users get the default password: "
                        + UserAdminDAO.DEFAULT_PASSWORD
        );
        hint.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        // =========================
        // BUTTONS
        // =========================

        Button addButton = new Button("Add User");
        Button updateButton = new Button("Update User");
        Button statusButton = new Button("Activate / Deactivate");
        Button deleteButton = new Button("Delete User");
        Button clearButton = new Button("Clear");

        addButton.setPrefWidth(110);
        updateButton.setPrefWidth(110);
        statusButton.setPrefWidth(160);
        deleteButton.setPrefWidth(110);
        clearButton.setPrefWidth(90);

        HBox buttons = new HBox(
                10,
                addButton,
                updateButton,
                statusButton,
                deleteButton,
                clearButton
        );

        // =========================
        // LOAD FROM DATABASE
        // =========================

        Runnable reload = () -> {

            try {
                table.setItems(
                        FXCollections.observableArrayList(dao.getAll())
                );

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        };

        reload.run();

        // =========================
        // TABLE SELECTION
        // =========================

        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldUser, selectedUser) -> {

                    if (selectedUser != null) {

                        nameField.setText(selectedUser.name);
                        emailField.setText(selectedUser.email);
                        roleBox.setValue(selectedUser.role);
                    }
                }
        );

        // =========================
        // ADD USER
        // =========================

        addButton.setOnAction(event -> {

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String role = roleBox.getValue();

            if (name.isEmpty() || email.isEmpty() || role == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please fill name, email and role."
                );

                return;
            }

            if (!email.contains("@") || !email.contains(".")) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please enter a valid email address."
                );

                return;
            }

            if (role.equals("Doctor") && specialityBox.getValue() == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select the doctor's speciality."
                );

                return;
            }

            try {
                UserAdminDAO.Result result = dao.addUser(
                        name,
                        email,
                        role,
                        specialityBox.getValue()
                );

                if (result == UserAdminDAO.Result.EMAIL_EXISTS) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "This email is already registered."
                    );

                    return;
                }

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "User added successfully!\nDefault password: "
                                + UserAdminDAO.DEFAULT_PASSWORD
                );

                clearFields(nameField, emailField, roleBox, specialityBox);
                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        });

        // =========================
        // UPDATE USER
        // =========================

        updateButton.setOnAction(event -> {

            UserAdminDAO.UserRow selectedUser =
                    table.getSelectionModel().getSelectedItem();

            if (selectedUser == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select a user first."
                );

                return;
            }

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || email.isEmpty()
                    || !email.contains("@") || !email.contains(".")) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please enter a name and a valid email."
                );

                return;
            }

            if (roleBox.getValue() != null
                    && !roleBox.getValue().equals(selectedUser.role)) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "The role of an existing user cannot be changed."
                );

                roleBox.setValue(selectedUser.role);
                return;
            }

            try {
                UserAdminDAO.Result result =
                        dao.updateUser(selectedUser.id, name, email);

                if (result == UserAdminDAO.Result.EMAIL_EXISTS) {

                    showAlert(
                            Alert.AlertType.WARNING,
                            "This email is already used by another user."
                    );

                    return;
                }

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "User updated successfully!"
                );

                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        });

        // =========================
        // ACTIVATE / DEACTIVATE
        // =========================

        statusButton.setOnAction(event -> {

            UserAdminDAO.UserRow selectedUser =
                    table.getSelectionModel().getSelectedItem();

            if (selectedUser == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select a user first."
                );

                return;
            }

            if (selectedUser.id == UserSession.getUserId()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "You cannot deactivate your own account."
                );

                return;
            }

            String newStatus =
                    selectedUser.status.equals("Active")
                            ? "Inactive"
                            : "Active";

            try {
                dao.setStatus(selectedUser.id, newStatus);

                showAlert(
                        Alert.AlertType.INFORMATION,
                        selectedUser.name + " is now " + newStatus + "."
                );

                reload.run();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(
                        Alert.AlertType.ERROR,
                        "Database error: " + e.getMessage()
                );
            }
        });

        // =========================
        // DELETE USER
        // =========================

        deleteButton.setOnAction(event -> {

            UserAdminDAO.UserRow selectedUser =
                    table.getSelectionModel().getSelectedItem();

            if (selectedUser == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please select a user first."
                );

                return;
            }

            if (selectedUser.id == UserSession.getUserId()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "You cannot delete your own account."
                );

                return;
            }

            Alert confirmation = new Alert(
                    Alert.AlertType.CONFIRMATION
            );

            confirmation.setTitle("Delete User");
            confirmation.setHeaderText(
                    "Delete " + selectedUser.name + "?"
            );
            confirmation.setContentText(
                    "This also deletes their appointments, medical "
                            + "records and feedback.\n"
                            + "To only block the login, use "
                            + "Activate / Deactivate instead."
            );

            confirmation.showAndWait().ifPresent(response -> {

                if (response != ButtonType.OK) {
                    return;
                }

                try {
                    dao.delete(selectedUser.id);

                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "User deleted successfully!"
                    );

                    clearFields(nameField, emailField, roleBox, specialityBox);
                    reload.run();

                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(
                            Alert.AlertType.ERROR,
                            "Database error: " + e.getMessage()
                    );
                }
            });
        });

        // =========================
        // CLEAR
        // =========================

        clearButton.setOnAction(event -> {

            clearFields(nameField, emailField, roleBox, specialityBox);

            table.getSelectionModel().clearSelection();
        });

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
                20,
                heading,
                table,
                form,
                hint,
                buttons,
                backButton
        );

        content.setPadding(new Insets(30));

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        BorderPane root =
                new BorderPane(scrollPane);

        Scene scene =
                new Scene(root, 1000, 700);

        stage.setTitle(
                "User Management - Online Healthcare"
        );

        stage.setScene(scene);
        stage.show();
    }

    // =========================
    // TABLE COLUMN HELPER
    // =========================

    private TableColumn<UserAdminDAO.UserRow, String> column(
            String heading,
            double width,
            java.util.function.Function<UserAdminDAO.UserRow, String> getter
    ) {

        TableColumn<UserAdminDAO.UserRow, String> column =
                new TableColumn<>(heading);

        column.setPrefWidth(width);

        column.setCellValueFactory(
                data -> new SimpleStringProperty(
                        getter.apply(data.getValue())
                )
        );

        return column;
    }

    // =========================
    // CLEAR FIELDS METHOD
    // =========================

    private void clearFields(
            TextField nameField,
            TextField emailField,
            ComboBox<String> roleBox,
            ComboBox<String> specialityBox
    ) {

        nameField.clear();
        emailField.clear();
        roleBox.setValue(null);
        specialityBox.setValue(null);
    }

    // =========================
    // ALERT METHOD
    // =========================

    private void showAlert(
            Alert.AlertType type,
            String message
    ) {

        Alert alert = new Alert(type);

        alert.setTitle("User Management");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
