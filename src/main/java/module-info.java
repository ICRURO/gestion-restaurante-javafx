module com.equipok {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.equipok to javafx.fxml;
    exports com.equipok;
}
//NO BORRAR!