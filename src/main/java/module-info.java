module com.example._7 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example._7 to javafx.fxml;
    exports com.example._7;
}