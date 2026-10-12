package com.healthcare;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public final class PasswordToggle {

    private static final String EYE =
            "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5z"
                    + "M12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5z"
                    + "m0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";

    private static final String EYE_OFF =
            "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75"
                    + "-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7z"
                    + "M2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84"
                    + "l.42.42L19.73 22 21 20.73 3.27 3 2 4.27z"
                    + "M7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55"
                    + "c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2z"
                    + "m4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z";

    private PasswordToggle() { }

    public static StackPane wrap(PasswordField passwordField) {

        passwordField.setStyle(passwordField.getStyle() + ";-fx-padding: 0 42 0 14;");

        TextField visibleField = new TextField();
        visibleField.getStyleClass().setAll(passwordField.getStyleClass());
        visibleField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleField.promptTextProperty().bind(passwordField.promptTextProperty());
        visibleField.styleProperty().bind(passwordField.styleProperty());
        visibleField.prefHeightProperty().bind(passwordField.prefHeightProperty());
        visibleField.prefWidthProperty().bind(passwordField.prefWidthProperty());
        visibleField.minHeightProperty().bind(passwordField.minHeightProperty());
        visibleField.maxWidthProperty().bind(passwordField.maxWidthProperty());
        visibleField.setVisible(false);
        visibleField.setManaged(false);

        // pressing Enter in the visible field behaves like Enter in the password field
        visibleField.setOnAction(e ->
                Event.fireEvent(passwordField, new ActionEvent(visibleField, passwordField)));

        SVGPath icon = new SVGPath();
        icon.setContent(EYE);
        icon.setFill(Color.web("#64748B"));
        icon.setScaleX(0.8);
        icon.setScaleY(0.8);

        Button eyeButton = new Button();
        eyeButton.setGraphic(icon);
        eyeButton.setFocusTraversable(false);
        eyeButton.setCursor(javafx.scene.Cursor.HAND);
        eyeButton.setTooltip(new Tooltip("Show password"));
        eyeButton.setStyle("-fx-background-color: transparent; -fx-padding: 4 8 4 8;");

        eyeButton.setOnAction(e -> {

            boolean show = !visibleField.isVisible();

            visibleField.setVisible(show);
            visibleField.setManaged(show);
            passwordField.setVisible(!show);
            passwordField.setManaged(!show);

            icon.setContent(show ? EYE_OFF : EYE);
            eyeButton.getTooltip().setText(show ? "Hide password" : "Show password");

            // keep the cursor in whichever field is showing
            (show ? visibleField : passwordField).requestFocus();
            (show ? visibleField : passwordField).positionCaret(
                    (show ? visibleField : passwordField).getText().length());
        });

        StackPane holder = new StackPane(passwordField, visibleField, eyeButton);
        StackPane.setAlignment(eyeButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(eyeButton, new Insets(0, 4, 0, 0));
        holder.setAlignment(Pos.CENTER_LEFT);
        return holder;
    }
}
