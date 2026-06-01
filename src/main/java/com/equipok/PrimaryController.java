package com.equipok;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

// --- IMPORTS PARA EL CU-18 ---
import com.equipok.DAO.IInsumoDAO;
import com.equipok.DAO.InsumoDAOImpl;
import com.equipok.model.Insumo;
import java.util.List;

public class PrimaryController {

    @FXML private StackPane mainPane;
    @FXML private Pane buttonPane;
    @FXML private Button btnPedirOrden; 
    @FXML private Button btnAgregarProducto;
    @FXML private Button btnPagarCuenta;   
    @FXML private Button btnReporteVentas; 
    @FXML private Button btnReservar;       
    @FXML private Button btnGestionMesas;   
    @FXML private Button btnGestionPersonal; 
    @FXML private Button btnCocina;
    @FXML private Button btnIngreso; // <-- Botón de inventario/insumos

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
    private void abrirInsumos() throws IOException {
        App.setRoot("insumos"); 
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

    @FXML
    private void switchToKitchen() throws IOException {
        App.setRoot("kitchen");
    }

    @FXML
    private void switchToPersonal() throws IOException {
        App.setRoot("personal");
    }

    @FXML
    private void switchToBilling() throws IOException {
        App.setRoot("Billing");
    }

    @FXML
private void switchToCorteCaja() throws IOException {
    App.setRoot("CorteCaja"); // Navegación estándar de la app
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

private void verificarAlertasStock() {
    IInsumoDAO insumoDAO = new InsumoDAOImpl();
    List<Insumo> insumosBajos = insumoDAO.obtenerInsumosBajoStock();

    if (!insumosBajos.isEmpty()) {
        StringBuilder mensaje = new StringBuilder("Los siguientes insumos están por agotarse:\n\n");
        for (Insumo i : insumosBajos) {
            mensaje.append("• ").append(i.getName())
                   .append(" (Quedan: ").append(i.getQuantity()).append(" ").append(i.getUnit()).append(")\n");
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta de Stock Crítico");
        alert.setHeaderText("¡Atención Gerente! Posible desabasto");
        alert.setContentText(mensaje.toString() + "\n¿Desea ir a la gestión de proveedores para reordenar?");

        ButtonType btnIrAProveedores = new ButtonType("Ir a Proveedores");
        ButtonType btnEntendido = new ButtonType("Entendido", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnIrAProveedores, btnEntendido);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnIrAProveedores) {
                try {
                    // CONEXIÓN DIRECTA A TU CU-19
                    App.setRoot("proveedor"); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

    private void configurarPermisosPorRol() {
        String rol = App.getUserRole();
        if (rol == null) return;
        
        switch (rol) {
            case "GERENTE":
                ocultarNodo(btnCocina);
                verificarAlertasStock(); 
                break;
            case "MESERO_CAJERO":
                ocultarNodo(btnAgregarProducto);
                ocultarNodo(btnReporteVentas);
                ocultarNodo(btnGestionMesas);
                //ocultarNodo(btnGestionPersonal);
                ocultarNodo(btnCocina);
                ocultarNodo(btnIngreso);
                break;
            case "CHEF": 
                ocultarNodo(btnAgregarProducto);
                ocultarNodo(btnPagarCuenta);
                ocultarNodo(btnReporteVentas);
                ocultarNodo(btnReservar);
                ocultarNodo(btnGestionMesas);
                ocultarNodo(btnGestionPersonal);
                ocultarNodo(btnPedirOrden);
                ocultarNodo(btnIngreso);
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