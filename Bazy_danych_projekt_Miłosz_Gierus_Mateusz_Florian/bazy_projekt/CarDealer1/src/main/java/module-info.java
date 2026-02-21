module com.example.cardealer1 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.cardealer1 to javafx.fxml;
    exports com.example.cardealer1;
}