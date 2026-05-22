package com.equipok;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class PrimaryController {

    @FXML
    private StackPane mainPane;

    @FXML 
    private Pane buttonPane;

    @FXML 
    private Button btnPedirOrden; 

    @FXML 
    private Button btnAgregarProducto;

    @FXML 
    private Button btnPagarCuenta;   

    @FXML 
    private Button btnReporteVentas; 

    @FXML 
    private Button btnReservar;       

    @FXML 
    private Button btnGestionMesas;   

    @FXML 
    private Button btnGestionPersonal; 
    
    private Node homeMenu;

    @FXML
    public void initialize() {
        homeMenu = buttonPane;

        configurarPermisosPorRol();
    }
    
    @FXML
    private void switchToOrderTaking() throws IOException {
        cargarPantalla("orderTaking");
    }

    @FXML
    private void switchToPayBill() throws IOException {
        cargarPantalla("Bills");
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
    private void switchToTableManagement() throws IOException {
    cargarPantalla("TableManagement"); 
}

    @FXML
    private void abrirProductos() throws java.io.IOException {
        cargarPantalla("productos"); 
    }

    @FXML
    private void cerrarSesion() throws java.io.IOException {
        App.setRoot("login"); 
    }

    @FXML
    private void switchToWaste() throws IOException {
        cargarPantalla("waste");
    }

    @FXML
    private void openProveedores() throws IOException {
        App.setRoot("proveedor");
    }

    private void cargarPantalla(String Fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(Fxml + ".fxml"));
            Parent newView = loader.load();
            mainPane.getChildren().clear();
            mainPane.getChildren().add(newView);
        } catch (IOException e) {
            showErrorAlert("Error al cargar la vista", Fxml);
            e.printStackTrace();
        }
    }

    @FXML
    private void backToHome() {
        mainPane.getChildren().clear();
        mainPane.getChildren().add(homeMenu);
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void switchToPersonal() throws IOException {
        App.setRoot("personal");
    }

    private void configurarPermisosPorRol() {
        String rol = App.getUserRole();
        if (rol == null) return;
        switch (rol) {
            case "GERENTE":
                break;
            case "MESERO_CAJERO":
                ocultarNodo(btnAgregarProducto);
                ocultarNodo(btnReporteVentas);
                ocultarNodo(btnGestionMesas);
                ocultarNodo(btnGestionPersonal);
                break;
            case "CHEF": //Aun falta implementar las funcionalidades para el rol de chef
                ocultarNodo(btnAgregarProducto);
                ocultarNodo(btnPagarCuenta);
                ocultarNodo(btnReporteVentas);
                ocultarNodo(btnReservar);
                ocultarNodo(btnGestionMesas);
                ocultarNodo(btnGestionPersonal);
                ocultarNodo(btnPedirOrden);
                break;
        }
    }

    private void ocultarNodo(Node nodo) {
        if (nodo != null) {
            nodo.setVisible(false);
            nodo.setManaged(false);
        }
    }
}
