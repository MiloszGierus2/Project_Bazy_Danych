package com.example.cardealer1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/CarDealer";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    @FXML
    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Sprawdź, czy wszystkie pola są uzupełnione
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Wszystkie pola są wymagane.");
            return;
        }

        // Sprawdź, czy adres e-mail jest w prawidłowym formacie
        if (!isValidEmail(email)) {
            showAlert("Nieprawidłowy adres e-mail.");
            return;
        }

        // Sprawdź, czy hasło ma minimum 8 znaków
        if (password.length() < 8) {
            showAlert("Hasło musi mieć co najmniej 8 znaków.");
            return;
        }

        String getMaxIdSQL = "SELECT MAX(dealer_id) FROM CarDealer";
        String insertSQL = "INSERT INTO CarDealer (dealer_id, name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement getMaxIdStmt = conn.prepareStatement(getMaxIdSQL);
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

            // Get the current max ID
            ResultSet rs = getMaxIdStmt.executeQuery();
            int newId = 1; // Default to 1 if no rows are present
            if (rs.next()) {
                newId = rs.getInt(1) + 1; // Increment the max ID by 1
            }

            // Insert the new user with the incremented ID
            insertStmt.setInt(1, newId);
            insertStmt.setString(2, name);
            insertStmt.setString(3, email);
            insertStmt.setString(4, password);
            insertStmt.executeUpdate();

            System.out.println("User registered successfully.");

            // Load the login scene
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load(), 697, 366);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login");
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
    private boolean isValidEmail(String email) {
        // Prosta walidacja adresu e-mail, można użyć bardziej zaawansowanych metod
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
