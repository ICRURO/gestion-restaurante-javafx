package com.equipok;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrimaryController {

    @FXML
    private Button btnPagarCuenta;

    @FXML
    private Button btnPedirOrden;

    @FXML
    private Button btnRegistroVenta;
    
    @FXML
    private void switchToOrderTaking() throws IOException {
        App.setRoot("orderTaking");
    }

    @FXML
    private void switchToPayBill() throws IOException {
        App.setRoot("payBill");
    }
    
    @FXML
    private void abrirReservas() throws IOException {
        
        App.setRoot("reserva"); 
    }

    @FXML
    private void switchToSalesReport() throws IOException {
        App.setRoot("salesReport");
    }

}
