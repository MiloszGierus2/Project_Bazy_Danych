
package com.example.cardealer1;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Pattern;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button zapomniales;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/CarDealer";
    private static final String USER = "postgres";
    private static final String PASS = "admin";
    @FXML
    private void login() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Walidacja pola email
        if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).find()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Podaj prawidłowy adres email, prawidłowy format to (email@cos.rozszerzenie) musi być @asld.rozszerzenie");
            alert.showAndWait();
            return;
        }

        // Walidacja pola hasło
        if (password.isEmpty() || password.length() < 8) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Nie prawidłowe Hasło, musi zawierać co najmniej 8 znaków.");
            alert.showAndWait();
            return;
        }

        String selectSQL = "SELECT dealer_id, name FROM CarDealer WHERE email = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                // Jeśli znaleziono użytkownika, przejdź do panelu głównego
                int userId = resultSet.getInt("dealer_id");
                String userName = resultSet.getString("name");

                // Wyświetl alert z nazwą zalogowanego użytkownika
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setHeaderText(null);
                alert.setContentText("Zalogowany użytkownik: " + userName);
                alert.showAndWait();

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent root = fxmlLoader.load();

                CarDealerController carDealerController = fxmlLoader.getController();
                carDealerController.setUserId(userId);

                Scene carDealerPanelScene = new Scene(root, 850, 605);
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(carDealerPanelScene);
                stage.setTitle("Car Dealer Panel");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd logowania");
                alert.setHeaderText(null);
                alert.setContentText("Nieprawidłowy email lub hasło.");
                alert.showAndWait();
            }
        } catch (SQLException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd logowania");
            alert.setHeaderText(null);
            alert.setContentText("Błąd logowania: " + e.getMessage());
            alert.showAndWait();
        }
    }




    @FXML
    public void zarejstruj(MouseEvent mouseEvent) {
        try {
            // Załaduj nowy widok rejestracji
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
            Scene registerScene = new Scene(fxmlLoader.load());

            // Pobierz bieżące okno (stage)
            Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

            // Utwórz nowe okno (stage) dla rejestracji
            Stage stage = new Stage();
            stage.setScene(registerScene);
            stage.setTitle("Rejestracja");
            stage.setResizable(false);
            stage.show();

            // Zamknij bieżące okno (stage)
            currentStage.close();
        } catch (IOException e) {
            System.out.println("Błąd ładowania widoku rejestracji: " + e.getMessage());
        }
    }

    @FXML
    public void zapomniales_hasla(MouseEvent event) {
        try {
            // Załaduj nowy widok resetowania hasła
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reset_password.fxml"));
            Scene resetPasswordScene = new Scene(fxmlLoader.load());

            // Pobierz bieżące okno (stage)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Utwórz nowe okno (stage) dla resetowania hasła
            Stage stage = new Stage();
            stage.setScene(resetPasswordScene);
            stage.setTitle("Resetowanie Hasła");
            stage.setResizable(false);
            stage.show();

            // Zamknij bieżące okno (stage)
            currentStage.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Błąd podczas otwierania okna resetowania hasła: " + e.getMessage());
            alert.showAndWait();
        }
    }


}
 