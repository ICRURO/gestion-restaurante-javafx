package com.equipok;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrimaryController {

    @FXML
    private Button btnPagarCuenta;

    @FXML
    private void switchToPayBill() throws IOException {
        App.setRoot("payBill");
    }
}
