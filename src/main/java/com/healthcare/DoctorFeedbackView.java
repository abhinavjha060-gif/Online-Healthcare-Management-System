package com.healthcare;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DoctorFeedbackView {

    public void show(Stage stage) {

        // =========================
        // TITLE
        // =========================

        Label title = new Label("Patient Feedback");

        title.setFont(
                Font.font("Arial", FontWeight.BOLD, 26)
        );

        title.setTextFill(
                Color.web("#1E3A5F")
        );

        Label subtitle = new Label(
                "View feedback and ratings provided by your patients."
        );

        subtitle.setTextFill(
                Color.web("#607D8B")
        );

        // =========================
        // FEEDBACK TABLE
        // =========================

        TableView<Feedback> table =
                new TableView<>();

        TableColumn<Feedback, String> patientColumn =
                new TableColumn<>("Patient");

        patientColumn.setCellValueFactory(
                data -> data.getValue().patientProperty()
        );

        TableColumn<Feedback, String> ratingColumn =
                new TableColumn<>("Rating");

        ratingColumn.setCellValueFactory(
                data -> data.getValue().ratingProperty()
        );

        TableColumn<Feedback, String> feedbackColumn =
                new TableColumn<>("Feedback");

        feedbackColumn.setCellValueFactory(
                data -> data.getValue().feedbackProperty()
        );

        table.getColumns().addAll(
                patientColumn,
                ratingColumn,
                feedbackColumn
        );

        // =========================
        // LOAD FROM DATABASE
        // =========================

        int doctorId = com.healthcare.model.UserSession.getUserId();

        Label summaryLabel = new Label();
        summaryLabel.setTextFill(Color.web("#1E3A5F"));
        summaryLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        try {
            com.healthcare.dao.FeedbackDAO dao =
                    new com.healthcare.dao.FeedbackDAO();

            for (com.healthcare.dao.FeedbackDAO.FeedbackItem item :
                    dao.getForDoctor(doctorId)) {

                table.getItems().add(
                        new Feedback(
                                item.patientName,
                                item.rating + " / 5",
                                item.comments
                        )
                );
            }

            double[] summary = dao.getSummary(doctorId);

            if (summary[0] == 0) {
                summaryLabel.setText("No feedback received yet.");
            } else {
                summaryLabel.setText(
                        String.format(
                                "Average rating: %.2f / 5   (%d reviews)",
                                summary[1],
                                (int) summary[0]
                        )
                );
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            summaryLabel.setText("Could not load feedback: " + e.getMessage());
            summaryLabel.setTextFill(Color.RED);
        }

        table.setPrefHeight(350);

        // =========================
        // VIEW FEEDBACK
        // =========================

        Label selectedFeedback =
                new Label("Select a feedback to view details.");

        selectedFeedback.setWrapText(true);

        selectedFeedback.setTextFill(
                Color.web("#607D8B")
        );

        Button viewButton =
                new Button("View Feedback");

        viewButton.setPrefHeight(40);

        viewButton.setStyle(
                "-fx-background-color: #1976D2;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // shows the selected feedback in its own dialog (a small label under the table was
        // easy to miss, so the button looked as if it did nothing)
        Runnable showSelected = () -> {

            Feedback selected =
                    table.getSelectionModel()
                            .getSelectedItem();

            if (selected == null) {

                selectedFeedback.setText("Please select a feedback first.");
                selectedFeedback.setTextFill(Color.RED);

                Alert warning = new Alert(Alert.AlertType.INFORMATION);
                warning.setTitle("View Feedback");
                warning.setHeaderText(null);
                warning.setContentText("Please select a feedback from the table first.");
                warning.showAndWait();
                return;
            }

            String details =
                    "Patient: " + selected.getPatient()
                            + "\nRating: " + selected.getRating()
                            + "\nFeedback: " + (selected.getFeedback().isEmpty()
                            ? "(no comment written)" : selected.getFeedback());

            selectedFeedback.setText(details);
            selectedFeedback.setTextFill(Color.web("#1E3A5F"));

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Patient Feedback");
            dialog.setHeaderText("Feedback from " + selected.getPatient());
            dialog.setContentText(details);
            dialog.getDialogPane().setMinWidth(420);
            dialog.showAndWait();
        };

        viewButton.setOnAction(event -> showSelected.run());

        // double-click a row = same as the button
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                showSelected.run();
            }
        });

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No feedback received yet."));

        // =========================
        // BACK BUTTON
        // =========================

        Button backButton =
                new Button("← Back to Dashboard");

        backButton.setOnAction(event -> {

            DoctorDashboard dashboard =
                    new DoctorDashboard();

            dashboard.show(stage);
        });

        // =========================
        // BUTTON AREA
        // =========================

        HBox buttons =
                new HBox(15);

        buttons.setAlignment(
                Pos.CENTER_LEFT
        );

        buttons.getChildren().addAll(
                viewButton,
                backButton
        );

        // =========================
        // CARD
        // =========================

        VBox card =
                new VBox(20);

        card.setPadding(
                new Insets(30)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 12px;"
        );

        card.getChildren().addAll(
                title,
                subtitle,
                summaryLabel,
                table,
                selectedFeedback,
                buttons
        );

        // =========================
        // ROOT
        // =========================

        StackPane root =
                new StackPane();

        root.setPadding(
                new Insets(30)
        );

        root.setStyle(
                "-fx-background-color: #F7FAFC;"
        );

        root.getChildren().add(card);

        // =========================
        // SCENE
        // =========================

        Navigator.show(stage, root, "Patient Feedback - Online Healthcare");
    }

    // =====================================================
    // FEEDBACK MODEL
    // =====================================================

    public static class Feedback {

        private final javafx.beans.property.SimpleStringProperty patient;
        private final javafx.beans.property.SimpleStringProperty rating;
        private final javafx.beans.property.SimpleStringProperty feedback;

        public Feedback(
                String patient,
                String rating,
                String feedback
        ) {

            this.patient =
                    new javafx.beans.property.SimpleStringProperty(
                            patient
                    );

            this.rating =
                    new javafx.beans.property.SimpleStringProperty(
                            rating
                    );

            this.feedback =
                    new javafx.beans.property.SimpleStringProperty(
                            feedback
                    );
        }

        public javafx.beans.property.StringProperty patientProperty() {
            return patient;
        }

        public javafx.beans.property.StringProperty ratingProperty() {
            return rating;
        }

        public javafx.beans.property.StringProperty feedbackProperty() {
            return feedback;
        }

        public String getPatient() {
            return patient.get();
        }

        public String getRating() {
            return rating.get();
        }

        public String getFeedback() {
            return feedback.get();
        }
    }
}