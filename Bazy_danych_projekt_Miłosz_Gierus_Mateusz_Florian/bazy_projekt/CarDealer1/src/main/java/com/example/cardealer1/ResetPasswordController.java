package com.example.cardealer1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResetPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button reset;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/CarDealer";
    private static final String USER = "postgres";
    private static final String PASS = "admin";

    @FXML
    private void reset_password(MouseEvent event) {
        String email = emailField.getText();
        String newPassword = passwordField.getText();

        // Walidacja pól
        if (email.isEmpty() || newPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Wszystkie pola muszą być wypełnione.");
            alert.showAndWait();
            return;
        }

        if (newPassword.length() < 8) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Hasło musi zawierać co najmniej 8 znaków.");
            alert.showAndWait();
            return;
        }

        // Połącz z bazą danych i zaktualizuj hasło
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String updateSQL = "UPDATE CarDealer SET password = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, email);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sukces");
                    alert.setHeaderText(null);
                    alert.setContentText("Hasło zostało zresetowane.");
                    alert.showAndWait();

                    // Załaduj nowy widok logowania
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
                    Scene loginScene = new Scene(fxmlLoader.load());

                    // Pobierz bieżące okno (stage)
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    // Utwórz nowe okno (stage) dla logowania
                    Stage stage = new Stage();
                    stage.setScene(loginScene);
                    stage.setTitle("Logowanie");
                    stage.setResizable(false);
                    stage.show();

                    // Zamknij bieżące okno (stage)
                    currentStage.close();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText(null);
                    alert.setContentText("Nie znaleziono użytkownika z podanym adresem email.");
                    alert.showAndWait();
                }
            }
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Błąd podczas resetowania hasła: " + e.getMessage());
            alert.showAndWait();
        }
    }


}
