package com.example.cardealer1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Register {

    @FXML
    private Button rejstruj;

    @FXML
    void zarejstruj(MouseEvent event) {
        try {
            // Pobierz aktualną scenę
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Załaduj nową scenę
            Parent parent = FXMLLoader.load(getClass().getResource("login.fxml"));
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