package com.equipok;

import com.equipok.DAO.IProductDAO;
import com.equipok.DAO.ProductDAOImpl;
import com.equipok.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ProductoController {

    @FXML private TableView<Product> tablaProductos;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colNombre;
    @FXML private TableColumn<Product, Double> colPrecio;

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;

    private IProductDAO productDAO = new ProductDAOImpl();
    private ObservableList<Product> productosObservable;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("price"));

        cargarProductos();
    }

    private void cargarProductos() {
        List<Product> productos = productDAO.obtenerProductos();
        productosObservable = FXCollections.observableArrayList(productos);
        tablaProductos.setItems(productosObservable);
    }

    @FXML
    public void handleGuardarProducto() {
        String nombre = txtNombre.getText();
        String precioStr = txtPrecio.getText();

        if (nombre == null || nombre.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
            mostrarAlerta("Error de validación", "Por favor, llene todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);

              if (precio <= 0) {
                mostrarAlerta("Error de validación", "El precio debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }

            Product nuevoProducto = new Product(nombre, precio);
            
            if (productDAO.agregarProducto(nuevoProducto)) {
                mostrarAlerta("Éxito", "Producto guardado correctamente.", Alert.AlertType.INFORMATION);
                txtNombre.clear();
                txtPrecio.clear();
                cargarProductos(); // Refrescar la tabla
            } else {
                mostrarAlerta("Error", "No se pudo guardar el producto en la base de datos.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El precio debe ser un valor numérico válido.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarProducto() {
        Product productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        
        if (productoSeleccionado == null) {
            mostrarAlerta("Atención", "Debe seleccionar un producto de la tabla para eliminarlo.", Alert.AlertType.WARNING);
            return;
        }

        if (productDAO.eliminarProducto(productoSeleccionado.getId())) {
            mostrarAlerta("Éxito", "Producto eliminado correctamente.", Alert.AlertType.INFORMATION);
            cargarProductos(); // Refrescar la tabla
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el producto. Podría estar asociado a una cuenta existente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void regresarAlMenu() throws java.io.IOException {
        App.setRoot("primary");
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}