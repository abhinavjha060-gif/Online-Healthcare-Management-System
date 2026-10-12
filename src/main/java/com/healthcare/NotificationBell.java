package com.healthcare;

import com.healthcare.dao.NotificationDAO;
import com.healthcare.model.UserSession;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public final class NotificationBell {

    private NotificationBell() { }

    public static Node create() {

        // vector bell: an emoji looks like a blurry picture on Windows
        javafx.scene.shape.SVGPath bellIcon = new javafx.scene.shape.SVGPath();
        bellIcon.setContent("M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4"
                + "c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z");
        bellIcon.setFill(Color.web("#42617e"));

        Button bell = new Button();
        bell.setGraphic(bellIcon);
        bell.setCursor(javafx.scene.Cursor.HAND);
        bell.setTooltip(new Tooltip("Notifications"));
        bell.setStyle("-fx-background-color: transparent; -fx-padding: 4 8 4 8;");

        Label badge = new Label();
        badge.setMinSize(18, 18);
        badge.setAlignment(Pos.CENTER);
        badge.setTextFill(Color.WHITE);
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        badge.setStyle("-fx-background-color: #ef4444; -fx-background-radius: 9; -fx-padding: 0 4 0 4;");
        badge.setMouseTransparent(true);
        badge.setVisible(false);

        StackPane holder = new StackPane(bell, badge);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        StackPane.setMargin(badge, new Insets(0, 0, 0, 0));
        holder.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Runnable refreshBadge = () -> {
            int unread = 0;
            try {
                unread = new NotificationDAO().countUnread(UserSession.getUserId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            badge.setText(unread > 9 ? "9+" : String.valueOf(unread));
            badge.setVisible(unread > 0);
        };

        bell.setOnAction(event -> {
            showList();
            refreshBadge.run();           // the badge reflects what was read in the list
        });

        Platform.runLater(refreshBadge);
        refreshBadge.run();
        return holder;
    }

    // ------------------------------------------------------------------

    private static void showList() {

        int patientId = UserSession.getUserId();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Notifications");
        dialog.setHeaderText("Your notifications");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox list = new VBox(8);
        list.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefSize(520, 340);

        Button markAll = new Button("Mark all as read");

        VBox content = new VBox(10, markAll, scroll);
        dialog.getDialogPane().setContent(content);

        List<NotificationDAO.Item> items = new ArrayList<>();

        Runnable render = new Runnable() {
            @Override
            public void run() {

                list.getChildren().clear();

                try {
                    items.clear();
                    items.addAll(new NotificationDAO().getForPatient(patientId));
                } catch (SQLException e) {
                    e.printStackTrace();
                    list.getChildren().add(new Label("Could not load notifications: " + e.getMessage()));
                    markAll.setDisable(true);
                    return;
                }

                if (items.isEmpty()) {
                    Label none = new Label("You have no notifications.");
                    none.setTextFill(Color.web("#64748B"));
                    list.getChildren().add(none);
                }

                boolean anyUnread = false;

                for (NotificationDAO.Item item : items) {

                    Label text = new Label((item.read ? "" : "●  ") + item.message);
                    text.setWrapText(true);
                    text.setMaxWidth(Double.MAX_VALUE);
                    text.setTextFill(Color.web(item.read ? "#64748B" : "#0F2A47"));
                    text.setFont(Font.font("Segoe UI",
                            item.read ? FontWeight.NORMAL : FontWeight.BOLD, 13));

                    HBox row = new HBox(text);
                    row.setPadding(new Insets(10, 12, 10, 12));
                    HBox.setHgrow(text, Priority.ALWAYS);
                    row.setCursor(item.read ? javafx.scene.Cursor.DEFAULT : javafx.scene.Cursor.HAND);
                    row.setStyle("-fx-background-color: " + (item.read ? "#F8FAFC" : "#EAF3FF")
                            + "; -fx-background-radius: 8;");

                    if (!item.read) {
                        anyUnread = true;
                        row.setOnMouseClicked(e -> {
                            try {
                                new NotificationDAO().markRead(patientId, List.of(item.key));
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            this.run();        // draw again
                        });
                    }
                    list.getChildren().add(row);
                }

                markAll.setDisable(!anyUnread);
            }
        };

        markAll.setOnAction(event -> {
            List<String> keys = new ArrayList<>();
            for (NotificationDAO.Item item : items) {
                if (!item.read) {
                    keys.add(item.key);
                }
            }
            try {
                new NotificationDAO().markRead(patientId, keys);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            render.run();
        });

        render.run();
        dialog.showAndWait();
    }
}
