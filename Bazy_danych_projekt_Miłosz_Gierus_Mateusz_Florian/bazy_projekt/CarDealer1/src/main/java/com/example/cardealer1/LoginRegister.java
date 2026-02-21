package com.example.cardealer1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginRegister {

    @FXML
    private Button rejstracja;

    @FXML
    private Button zaloguj;

    @FXML
    void login(MouseEvent event) {
        try {
            // Pobierz aktualną scenę
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Załaduj nową scenę
            Parent parent = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("Rejestracja");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Zamknij aktualną scenę
            currentStage.close();
        } catch (IOException ex) {
            Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void zarejstruj(MouseEvent event) {
        try {
            // Pobierz aktualną scenę
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Załaduj nową scenę
            Parent parent = FXMLLoader.load(getClass().getResource("register.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("Rejestracja");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Zamknij aktualną scenę
            currentStage.close();
        } catch (IOException ex) {
            Logger.getLogger(HelloApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}