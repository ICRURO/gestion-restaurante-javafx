package com.equipok;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    private Button btnPagarCuenta;

    @FXML
    private Button btnPedirOrden;

    @FXML
    private Button btnRegistroVenta;

    @FXML
    private Pane barraSuperior;

    @FXML
    public void initialize() {
        if (barraSuperior != null) {
        barraSuperior.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        barraSuperior.setOnMouseDragged(event -> {
            Stage stage = (Stage) barraSuperior.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    }
    
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
    @FXML
    private void abrirProductos() throws java.io.IOException {
        App.setRoot("productos"); 
    }

    @FXML
    private void closeApp(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimizeApp(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void barraSuperiorPresionada(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void barraSuperiorArrastrada(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    @FXML
    private void cerrarSesion() throws java.io.IOException {
        App.setRoot("login"); 
    }
}
