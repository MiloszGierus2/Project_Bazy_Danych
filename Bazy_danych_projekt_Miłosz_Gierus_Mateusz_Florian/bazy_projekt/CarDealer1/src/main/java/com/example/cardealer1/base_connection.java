package com.example.cardealer1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class base_connection {

    private static final String URL = "jdbc:postgresql://localhost:5432/CarDealer";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private static final Logger LOGGER = Logger.getLogger(base_connection.class.getName());

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found.", e);
        }
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = connect()) {
            if (connection != null) {
                LOGGER.log(Level.INFO, "Połączono z bazą danych.");
            } else {
                LOGGER.log(Level.SEVERE, "Failed to make connection!");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection to the PostgreSQL server failed.", e);
        }
    }
}