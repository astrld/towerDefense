module com.example.battlefortress {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.battlefortress to javafx.fxml;
    exports com.example.battlefortress;
    exports com.example.battlefortress.towers;
    opens com.example.battlefortress.towers to javafx.fxml;
}