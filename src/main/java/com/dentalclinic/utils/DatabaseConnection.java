package com.dentalclinic.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;

    // CHANGE THESE VALUES TO MATCH YOUR MYSQL SETUP
    private static final String URL = "jdbc:mysql://localhost:3306/dental_clinic";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Put your MySQL password here

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            LOGGER.info("Database connected successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.severe("Database connection failed: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            LOGGER.severe("Failed to get connection: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed.");
            } catch (SQLException e) {
                LOGGER.severe("Error closing connection: " + e.getMessage());
            }
        }
    }
}