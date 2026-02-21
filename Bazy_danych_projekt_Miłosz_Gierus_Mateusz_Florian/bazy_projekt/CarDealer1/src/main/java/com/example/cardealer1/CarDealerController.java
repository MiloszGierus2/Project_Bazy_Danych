package com.example.cardealer1;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class CarDealerController {
    @FXML
    private Button addCarButton;
    @FXML
    private Button sellCarButton;
    @FXML
    private Button refreshButton;
    @FXML
    private TableView<Car> carTableView;
    @FXML
    private TableColumn<Car, String> makeColumn;
    @FXML
    private TableColumn<Car, String> modelColumn;
    @FXML
    private TableColumn<Car, Integer> yearColumn;
    @FXML
    private TableColumn<Car, Double> priceColumn;
    @FXML
    private TextField makeTextField;
    @FXML
    private TextField modelTextField;
    @FXML
    private TextField yearTextField;
    @FXML
    private TextField priceTextField;

    private void callProcedure() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "{ ? = call get_car_count() }";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.execute();
                int carCount = stmt.getInt(1);

                // Wyświetl wynik w oknie alertu
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Liczba samochodów");
                alert.setHeaderText(null);
                alert.setContentText("Liczba samochodów na aukcji: " + carCount);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void callGetMostExpensiveCarProcedure() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "{ ? = call get_most_expensive_car_price() }";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                // Zarejestruj parametr wynikowy
                stmt.registerOutParameter(1, Types.INTEGER);
                // Wykonaj procedurę
                stmt.execute();

                // Pobierz wynik
                int maxPrice = stmt.getInt(1);

                // Wyświetl wynik w oknie alertu
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Najwyższa cena samochodu");
                alert.setHeaderText(null);
                if (maxPrice > 0) {
                    alert.setContentText("Najwyższa cena samochodu w bazie: " + maxPrice + "zł");
                } else {
                    alert.setContentText("Brak informacji o najwyższej cenie samochodu w bazie.");
                }
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostExpensiveCarButtonClick(ActionEvent event) {
        callGetMostExpensiveCarProcedure();
    }

    @FXML
    private void callProcedureButtonClick(ActionEvent event) {
        callProcedure();
    }


    //dodane
    private int userId; // Identyfikator użytkownika


    public void setUserId(int userId) {
        this.userId = userId;
    }
    //dodane
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/CarDealer";
    private static final String USER = "postgres";
    private static final String PASS = "admin";


    @FXML
    public void initialize() {
        // Ustawienie komórek tabeli
        TableColumn<Car, String> makeColumn = new TableColumn<>("Make");
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        TableColumn<Car, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        TableColumn<Car, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<Car, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        carTableView.getColumns().addAll(makeColumn, modelColumn, yearColumn, priceColumn);

        // Ustawienie słuchacza zdarzeń dla zaznaczenia w tabeli
        carTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Pobranie wybranego samochodu
                Car selectedCar = carTableView.getSelectionModel().getSelectedItem();

                // Wyświetlenie danych wybranego samochodu w polach tekstowych
                makeTextField.setText(selectedCar.getMake());
                modelTextField.setText(selectedCar.getModel());
                yearTextField.setText(String.valueOf(selectedCar.getYear()));
                priceTextField.setText(String.valueOf(selectedCar.getPrice()));
            }
        });

        // Odśwież widok
        refresh();
    }

    @FXML
    private void logoutButtonClick(ActionEvent event) {
        // Wyświetl alert o wylogowywaniu

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Nastąpiło wylogowanie, zaloguj się ponownie");
        alert.showAndWait();
        // Załaduj widok logowania
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Pobierz bieżący stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda obsługująca dodawanie nowego samochodu

    @FXML
    private void addNewCar() {
        // Pobierz dane wprowadzone przez użytkownika
        String make = makeTextField.getText();
        String model = modelTextField.getText();
        String yearText = yearTextField.getText();
        String priceText = priceTextField.getText();

        // Walidacja pól tekstowych
        if (make.isEmpty() || model.isEmpty() || yearText.isEmpty() || priceText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Wszystkie pola muszą być wypełnione.");
            alert.showAndWait();
            return;
        }

        // Walidacja polskich znaków
        if (!make.matches("[\\p{L} ]+") || !model.matches("[\\p{L} ]+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Pola 'Marka' i 'Model' mogą zawierać wyłącznie polskie znaki.");
            alert.showAndWait();
            return;
        }

        int year;
        double price;

        try {
            year = Integer.parseInt(yearText);
            if (year < 1886 || year > 3000) { // Sprawdź czy rok jest realistyczny
                throw new NumberFormatException("Rok poza zakresem, wprowadź realistyczny rok produkcji");
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Rok musi być prawidłową liczbą całkowitą między 1886 a 3000.");
            alert.showAndWait();
            return;
        }

        try {
            price = Double.parseDouble(priceText);
            if (price < 0) { // Sprawdź czy cena nie jest ujemna
                throw new NumberFormatException("Cena nie może być ujemna.");
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd walidacji");
            alert.setHeaderText(null);
            alert.setContentText("Cena musi być prawidłową liczbą.");
            alert.showAndWait();
            return;
        }

        // Połącz z bazą danych
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Utwórz zapytanie SQL sprawdzające wartość dealer_id dla danego użytkownika
            String query = "SELECT dealer_id FROM cardealer WHERE dealer_id = ?";

            // Utwórz obiekt PreparedStatement
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Ustaw parametr zapytania SQL na podstawie wartości userId
                pstmt.setInt(1, userId);

                // Wykonaj zapytanie SQL
                ResultSet rs = pstmt.executeQuery();

                // Sprawdź wartość dealer_id
                if (rs.next()) {
                    int dealerId = rs.getInt("dealer_id");

                    // Sprawdź czy użytkownik ma odpowiednie uprawnienia
                    if (dealerId != 1) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Brak uprawnień");
                        alert.setHeaderText(null);
                        alert.setContentText("Nie masz uprawnień do dodawania samochodów.");
                        alert.showAndWait();
                        return; // Przerwij dodawanie samochodu
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText(null);
                    alert.setContentText("Nie znaleziono użytkownika w bazie danych.");
                    alert.showAndWait();
                    return; // Przerwij dodawanie samochodu
                }
            }

            // Utwórz zapytanie SQL wstawiające nowy samochód do tabeli cars, w tym dealer_id
            String insertSQL = "INSERT INTO cars (make, model, year, price, dealer_id) VALUES (?, ?, ?, ?, ?)";

            // Utwórz obiekt PreparedStatement
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertSQL)) {
                // Ustaw parametry zapytania SQL na podstawie danych wprowadzonych przez użytkownika
                pstmtInsert.setString(1, make);
                pstmtInsert.setString(2, model);
                pstmtInsert.setInt(3, year);
                pstmtInsert.setDouble(4, price);
                pstmtInsert.setInt(5, userId); // Dodaj dealer_id

                // Wykonaj zapytanie SQL
                pstmtInsert.executeUpdate();
            }

            // Jeśli dodanie do bazy danych się powiedzie, dodaj też do tabeli w interfejsie użytkownika
            Car newCar = new Car(make, model, year, price);
            carTableView.getItems().add(newCar);

            // Wyczyść pola tekstowe po dodaniu samochodu
            makeTextField.clear();
            modelTextField.clear();
            yearTextField.clear();
            priceTextField.clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Błąd dodawania samochodu do bazy danych: " + e.getMessage());
            alert.showAndWait();
        }
    }




    @FXML
    public void editCarButtonClick(ActionEvent event) {
        Car selectedCar = carTableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            // Ustawienie starych wartości w polach tekstowych
            makeTextField.setText(selectedCar.getMake());
            modelTextField.setText(selectedCar.getModel());
            yearTextField.setText(String.valueOf(selectedCar.getYear()));
            priceTextField.setText(String.valueOf(selectedCar.getPrice()));
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Edytowanie samochodu");
            alert.setHeaderText(null);
            alert.setContentText("Nie wybrano żadnego samochodu do edycji.");
            alert.showAndWait();
        }
    }

    @FXML
    public void updateCar(ActionEvent event) {
        Car selectedCar = carTableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            String make = makeTextField.getText();
            String model = modelTextField.getText();
            String yearText = yearTextField.getText();
            String priceText = priceTextField.getText();

            // Walidacja pól tekstowych
            if (make.isEmpty() || model.isEmpty() || yearText.isEmpty() || priceText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd walidacji");
                alert.setHeaderText(null);
                alert.setContentText("Wszystkie pola muszą być wypełnione.");
                alert.showAndWait();
                return;
            }

            // Walidacja polskich znaków
            if (!make.matches("[\\p{L} ]+") || !model.matches("[\\p{L} ]+")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd walidacji");
                alert.setHeaderText(null);
                alert.setContentText("Pola 'Marka' i 'Model' mogą zawierać wyłącznie polskie znaki.");
                alert.showAndWait();
                return;
            }

            int year;
            double price;

            try {
                year = Integer.parseInt(yearText);
                if (year < 1886 || year > 3000) { // Sprawdź czy rok jest realistyczny
                    throw new NumberFormatException("Rok poza zakresem, wprowadź realistyczny rok produkcji");
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd walidacji");
                alert.setHeaderText(null);
                alert.setContentText("Rok musi być prawidłową liczbą całkowitą między 1886 a 3000.");
                alert.showAndWait();
                return;
            }

            try {
                price = Double.parseDouble(priceText);
                if (price < 0) { // Sprawdź czy cena nie jest ujemna
                    throw new NumberFormatException("Cena nie może być ujemna.");
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd walidacji");
                alert.setHeaderText(null);
                alert.setContentText("Cena musi być prawidłową liczbą.");
                alert.showAndWait();
                return;
            }

            // Połącz z bazą danych
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String updateSQL = "UPDATE cars SET make = ?, model = ?, year = ?, price = ? WHERE make = ? AND model = ? AND year = ? AND price = ? AND dealer_id = ?";
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSQL)) {
                    pstmtUpdate.setString(1, make);
                    pstmtUpdate.setString(2, model);
                    pstmtUpdate.setInt(3, year);
                    pstmtUpdate.setDouble(4, price);
                    pstmtUpdate.setString(5, selectedCar.getMake());
                    pstmtUpdate.setString(6, selectedCar.getModel());
                    pstmtUpdate.setInt(7, selectedCar.getYear());
                    pstmtUpdate.setDouble(8, selectedCar.getPrice());
                    pstmtUpdate.setInt(9, userId);
                    pstmtUpdate.executeUpdate();
                }

                selectedCar.setMake(make);
                selectedCar.setModel(model);
                selectedCar.setYear(year);
                selectedCar.setPrice(price);
                carTableView.refresh();

                makeTextField.clear();
                modelTextField.clear();
                yearTextField.clear();
                priceTextField.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Edycja samochodu");
                alert.setHeaderText(null);
                alert.setContentText("Samochód został pomyślnie zaktualizowany.");
                alert.showAndWait();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd edycji samochodu");
                alert.setHeaderText(null);
                alert.setContentText("Błąd edycji samochodu: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Edycja samochodu");
            alert.setHeaderText(null);
            alert.setContentText("Nie wybrano żadnego samochodu do edycji.");
            alert.showAndWait();
        }
    }






    // Metoda obsługująca kliknięcie przycisku "Add Car"
    @FXML
    private void addCarButtonClick(ActionEvent event) {
        addNewCar();
    }
    // Metoda obsługująca sprzedaż samochodu
    @FXML
    private void sellCarButtonClick(ActionEvent event) {
        sellCar();
    }
    // Metoda obsługująca odświeżenie widoku
    @FXML
    private void refreshButtonClick(ActionEvent event) {
        refresh();
    }

    // Metoda obsługująca sprzedaż samochodu
    @FXML
    private void sellCar() {
        Car selectedCar = carTableView.getSelectionModel().getSelectedItem();
        if (selectedCar != null) {
            // Połącz z bazą danych
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // Utwórz zapytanie SQL sprawdzające wartość dealer_id dla danego użytkownika
                String query = "SELECT dealer_id FROM cardealer WHERE dealer_id = ?";
                // Utwórz obiekt PreparedStatement
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    // Ustaw parametr zapytania SQL na podstawie wartości userId
                    pstmt.setInt(1, userId);

                    // Wykonaj zapytanie SQL
                    ResultSet rs = pstmt.executeQuery();

                    // Sprawdź wartość dealer_id
                    if (rs.next()) {
                        int dealerId = rs.getInt("dealer_id");

                        // Sprawdź czy użytkownik ma odpowiednie uprawnienia
                        if (dealerId != 1) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Brak uprawnień");
                            alert.setHeaderText(null);
                            alert.setContentText("Nie masz uprawnień do sprzedaży samochodów.");
                            alert.showAndWait();
                            return; // Przerwij sprzedaż samochodu
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd");
                        alert.setHeaderText(null);
                        alert.setContentText("Nie znaleziono użytkownika w bazie danych.");
                        alert.showAndWait();
                        return; // Przerwij sprzedaż samochodu
                    }
                }

                // Utwórz zapytanie SQL usuwające wybrany samochód z tabeli cars
                String deleteSQL = "DELETE FROM cars WHERE make = ? AND model = ? AND year = ? AND price = ? AND dealer_id = ?";
                // Utwórz obiekt PreparedStatement
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                    // Ustaw parametry zapytania SQL na podstawie danych wybranego samochodu
                    pstmt.setString(1, selectedCar.getMake());
                    pstmt.setString(2, selectedCar.getModel());
                    pstmt.setInt(3, selectedCar.getYear());
                    pstmt.setDouble(4, selectedCar.getPrice());
                    pstmt.setInt(5, userId);
                    // Wykonaj zapytanie SQL
                    pstmt.executeUpdate();
                }

                // Usuń samochód z tabeli w interfejsie użytkownika
                carTableView.getItems().remove(selectedCar);

                // Wyświetl komunikat o pomyślnej sprzedaży
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sprzedaż samochodu");
                alert.setHeaderText(null);
                alert.setContentText("Samochód został pomyślnie sprzedany.");
                alert.showAndWait();

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd sprzedaży samochodu");
                alert.setHeaderText(null);
                alert.setContentText("Błąd sprzedaży samochodu: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sprzedaż samochodu");
            alert.setHeaderText(null);
            alert.setContentText("Nie wybrano żadnego samochodu do sprzedaży.");
            alert.showAndWait();
        }
    }

    // Metoda obsługująca odświeżenie widoku
    @FXML
    private void refresh() {
        carTableView.getItems().clear();
        // Połącz z bazą danych
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            // Utwórz zapytanie SQL pobierające wszystkie samochody z tabeli cars
            String selectSQL = "SELECT make, model, year, price FROM cars";
            try (PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
                ResultSet resultSet = pstmt.executeQuery();
                while (resultSet.next()) {
                    // Pobierz dane każdego samochodu
                    String make = resultSet.getString("make");
                    String model = resultSet.getString("model");
                    int year = resultSet.getInt("year");
                    double price = resultSet.getDouble("price");
                    // Dodaj samochód do tabeli w interfejsie użytkownika
                    Car car = new Car(make, model, year, price);
                    carTableView.getItems().add(car);
                }
            }
        } catch (SQLException e) {
            System.out.println("Błąd odświeżania widoku: " + e.getMessage());
        }
    }
}