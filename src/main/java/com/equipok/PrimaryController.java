package com.equipok;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
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
    
    private Node homeMenu;

    @FXML
    public void initialize() {
        homeMenu = buttonPane;
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
}
