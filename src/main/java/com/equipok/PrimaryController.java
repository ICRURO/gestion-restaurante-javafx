package com.equipok;

import javafx.scene.Node;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

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
    private StackPane mainPane;

    @FXML 
    private Pane buttonPane;
    
    private Node vistaInicio;

    @FXML
    public void initialize() {
        vistaInicio = buttonPane;
    }
    
    @FXML
    private void switchToOrderTaking() throws IOException {
        cargarPantalla("orderTaking");
    }

    @FXML
    private void switchToPayBill() throws IOException {
        cargarPantalla("payBill");
    }
    
    @FXML
    private void abrirReservas() throws IOException {
        cargarPantalla("reserva"); 
    }

    @FXML
    private void switchToSalesReport() throws IOException {
        cargarPantalla("salesReport");
    }
    @FXML
    private void abrirProductos() throws java.io.IOException {
        cargarPantalla("productos"); 
    }

    @FXML
    private void cerrarSesion() throws java.io.IOException {
        App.setRoot("login"); 
    }

    private void cargarPantalla(String Fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(Fxml + ".fxml"));
            Parent nuevaVista = loader.load();
            mainPane.getChildren().clear();
            mainPane.getChildren().add(nuevaVista);
        } catch (IOException e) {
            System.out.println("No se pudo cargar la vista: " + Fxml);
            e.printStackTrace();
        }
    }

    @FXML
    private void regresarAlInicio() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(vistaInicio);
    }
}
