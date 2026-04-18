module com.equipok {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.equipok.model to javafx.base;
    opens com.equipok to javafx.fxml;
    exports com.equipok;
}
//NO BORRAR!