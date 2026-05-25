package com.equipok;

import com.equipok.DAO.IProveedorDAO;
import com.equipok.DAO.ProveedorDAOImpl;
import com.equipok.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class ProveedorController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmpresa;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cbCategoria;

    @FXML private TableView<Proveedor> tablaProveedores;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colEmpresa;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colCorreo;
    @FXML private TableColumn<Proveedor, String> colCategoria;

    private IProveedorDAO proveedorDAO = new ProveedorDAOImpl();
    private ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    private Proveedor proveedorSeleccionado;

    @FXML
    public void initialize() {
        cbCategoria.setItems(FXCollections.observableArrayList("Alimentos", "Bebidas", "Limpieza", "Insumos Generales", "Otros"));

        txtTelefono.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("\\d*")) {
                return change;
            }
            return null;
        }));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                proveedorSeleccionado = newSelection;
                llenarFormulario(newSelection);
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            listaProveedores.setAll(proveedorDAO.getAllProveedores());
            tablaProveedores.setItems(listaProveedores);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los proveedores: " + e.getMessage());
        }
    }

@FXML
    private void handleGuardar(ActionEvent event) {
        if (txtNombre.getText().isEmpty() || txtEmpresa.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Por favor, llene al menos Nombre, Empresa y Teléfono.");
            return;
        }

        String telefono = txtTelefono.getText();
        if (telefono.length() < 8 || telefono.length() > 15) {
            mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido", "El número de teléfono debe tener entre 8 y 15 dígitos.");
            return;
        }

        String correo = txtCorreo.getText();
        if (!correo.isEmpty() && !correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo inválido", "Por favor, ingrese un correo electrónico válido (ej. correo@empresa.com).");
            return;
        }

        try {
            if (proveedorSeleccionado == null) {
                Proveedor nuevo = new Proveedor(
                        txtNombre.getText(), txtEmpresa.getText(), telefono,
                        correo, cbCategoria.getValue()
                );
                proveedorDAO.addProveedor(nuevo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Proveedor registrado correctamente.");
            } else {
                proveedorSeleccionado.setNombre(txtNombre.getText());
                proveedorSeleccionado.setEmpresa(txtEmpresa.getText());
                proveedorSeleccionado.setTelefono(telefono);
                proveedorSeleccionado.setCorreo(correo);
                proveedorSeleccionado.setCategoria(cbCategoria.getValue());
                
                proveedorDAO.updateProveedor(proveedorSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Proveedor actualizado correctamente.");
            }
            limpiarFormulario();
            cargarDatos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos", "Error al guardar: " + e.getMessage());
        }
    }
    @FXML
    private void handleEliminar(ActionEvent event) {
        // FA-03 Eliminar proveedor
        if (proveedorSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Seleccione un proveedor de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea eliminar este proveedor?", ButtonType.YES, ButtonType.NO);
        confirmacion.showAndWait();

        if (confirmacion.getResult() == ButtonType.YES) {
            try {
                proveedorDAO.deleteProveedor(proveedorSeleccionado.getId());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Proveedor eliminado correctamente.");
                limpiarFormulario();
                cargarDatos();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar proveedor: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    @FXML
    private void handleRegresar(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }

    private void llenarFormulario(Proveedor p) {
        txtNombre.setText(p.getNombre());
        txtEmpresa.setText(p.getEmpresa());
        txtTelefono.setText(p.getTelefono());
        txtCorreo.setText(p.getCorreo());
        cbCategoria.setValue(p.getCategoria());
    }

    private void limpiarFormulario() {
        proveedorSeleccionado = null;
        txtNombre.clear();
        txtEmpresa.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        cbCategoria.setValue(null);
        tablaProveedores.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}