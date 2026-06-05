module com.example._7 {
    requires javafx.controls;
    requires javafx.fxml;

    // JavaFX 啟動器需能建立 Application
    exports com.example._7.app to javafx.graphics;

    // 允許 FXMLLoader 透過反射注入 controller 與 component
    opens com.example._7.ui.screen to javafx.fxml;
    opens com.example._7.ui.component to javafx.fxml;
    opens com.example._7.app to javafx.graphics;
    exports com.example._7.ui to javafx.graphics;
    opens com.example._7.ui to javafx.graphics;
}