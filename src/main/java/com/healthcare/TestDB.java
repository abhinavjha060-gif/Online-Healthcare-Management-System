package com.healthcare;

import com.healthcare.database.DatabaseConnection;
import java.sql.Connection;

/* Run this class to check the database settings without opening the app. */
public class TestDB {
    public static void main(String[] args) {
        System.out.println("Trying: " + DatabaseConnection.getUrl());

        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Database connected successfully!");
        } else {
            System.out.println("Failed to connect to database.");
            System.out.println("Reason: " + DatabaseConnection.getLastError());
        }
    }
}
