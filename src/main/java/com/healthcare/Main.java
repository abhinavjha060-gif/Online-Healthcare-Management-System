package com.healthcare;

import com.healthcare.database.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Tell the user clearly if MySQL is not running / the password is wrong,
        // instead of failing later on the first button click.
        if (!DatabaseConnection.isAvailable()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database not reachable");
            alert.setHeaderText("MediCare could not connect to the database");
            alert.setContentText(
                    "URL: " + DatabaseConnection.getUrl() + "\n"
                            + "Reason: " + DatabaseConnection.getLastError() + "\n\n"
                            + "1. Make sure MySQL is running.\n"
                            + "2. Run database/schema.sql once (creates healthcare_db).\n"
                            + "3. Check user / password in src/main/resources/db.properties.");
            alert.showAndWait();
        }

        LoginView loginView = new LoginView();
        loginView.show(stage);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
