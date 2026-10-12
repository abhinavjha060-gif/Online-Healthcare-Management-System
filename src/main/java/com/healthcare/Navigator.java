package com.healthcare;

import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class Navigator {

    public static final double DEFAULT_WIDTH = 1400;
    public static final double DEFAULT_HEIGHT = 850;

    public static final double MIN_WIDTH = 1200;
    public static final double MIN_HEIGHT = 750;

    private Navigator() { }

    public static void show(Stage stage, Parent root, String title) {
        show(stage, root, title, new String[0]);
    }

    public static void show(Stage stage, Parent root, String title, String... stylesheets) {

        Scene scene = stage.getScene();

        if (scene == null) {
            // very first screen: create the scene once, sized to fit the user's monitor
            Rectangle2D area = Screen.getPrimary().getVisualBounds();
            double width = Math.min(DEFAULT_WIDTH, area.getWidth());
            double height = Math.min(DEFAULT_HEIGHT, area.getHeight());

            scene = new Scene(root, width, height);
            applyStylesheets(scene, stylesheets);
            stage.setScene(scene);

        } else {
            // later screens: same scene, same window - only the content changes
            applyStylesheets(scene, stylesheets);
            scene.setRoot(root);
        }

        stage.setTitle(title);
        stage.setMinWidth(Math.min(MIN_WIDTH, Screen.getPrimary().getVisualBounds().getWidth()));
        stage.setMinHeight(Math.min(MIN_HEIGHT, Screen.getPrimary().getVisualBounds().getHeight()));

        if (!stage.isShowing()) {
            stage.show();
        }
    }

    private static void applyStylesheets(Scene scene, String... stylesheets) {
        scene.getStylesheets().clear();
        for (String css : stylesheets) {
            if (css != null) {
                scene.getStylesheets().add(css);
            }
        }
    }
}
