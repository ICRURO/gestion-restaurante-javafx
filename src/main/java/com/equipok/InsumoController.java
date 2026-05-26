package com.equipok;

import com.equipok.DAO.IInsumoDAO;
import com.equipok.DAO.InsumoDAOImpl;
import com.equipok.model.Insumo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;



import java.util.List;

public class InsumoController {

    @FXML private TableView<Insumo> tablaInsumos;
    @FXML private TableColumn<Insumo, Integer> colId;
    @FXML private TableColumn<Insumo, String> colNombre;
    @FXML private TableColumn<Insumo, Double> colCantidad;
    @FXML private TableColumn<Insumo, String> colUnidad;
    @FXML private TableColumn<Insumo, Double> colStockMinimo;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbUnidad;
    @FXML private TextField txtStockMinimo;

    private IInsumoDAO insumoDAO = new InsumoDAOImpl();
    private ObservableList<Insumo> insumosObservable;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("minStock"));


        cmbUnidad.getItems().addAll("kg", "gramos", "litros", "mililitros", "piezas", "cajas");

        cargarInsumos();
        
        tablaInsumos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getName());
                cmbUnidad.setValue(newSelection.getUnit()); 
                txtStockMinimo.setText(String.valueOf(newSelection.getMinStock()));
                txtCantidad.clear(); 
            }
        });
    }

    private void cargarInsumos() {
        List<Insumo> insumos = insumoDAO.obtenerInsumos();
        insumosObservable = FXCollections.observableArrayList(insumos);
        tablaInsumos.setItems(insumosObservable);
        tablaInsumos.refresh();
    }

    @FXML
    public void handleGuardarOIngresarInsumo() {
        String nombre = txtNombre.getText();
        String cantidadStr = txtCantidad.getText();
        String unidad = cmbUnidad.getValue(); 
        String stockMinStr = txtStockMinimo.getText();

        if (nombre.isEmpty() || cantidadStr.isEmpty() || unidad == null || stockMinStr.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios. Seleccione una unidad de medida.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double cantidadIngresada = Double.parseDouble(cantidadStr);
            double stockMinimo = Double.parseDouble(stockMinStr);

            if (cantidadIngresada <= 0) {
                mostrarAlerta("Error", "La cantidad ingresada debe ser mayor a 0.", Alert.AlertType.WARNING);
                return;
            }

            Insumo insumoSeleccionado = tablaInsumos.getSelectionModel().getSelectedItem();

            if (insumoSeleccionado != null && insumoSeleccionado.getName().equalsIgnoreCase(nombre)) {
                double nuevaCantidad = insumoSeleccionado.getQuantity() + cantidadIngresada;
                insumoSeleccionado.setQuantity(nuevaCantidad);
                insumoSeleccionado.setMinStock(stockMinimo); 
                insumoSeleccionado.setUnit(unidad);

                if (insumoDAO.actualizarInsumo(insumoSeleccionado)) {
                    mostrarAlerta("Éxito", "Se agregó stock al insumo correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "No se pudo actualizar el insumo en la base de datos.", Alert.AlertType.ERROR);
                }
            } else {
                Insumo nuevoInsumo = new Insumo(nombre, cantidadIngresada, unidad, stockMinimo);
                if (insumoDAO.agregarInsumo(nuevoInsumo)) {
                    mostrarAlerta("Éxito", "Nuevo insumo registrado correctamente.", Alert.AlertType.INFORMATION);
                } else {
                    mostrarAlerta("Error", "No se pudo guardar el insumo en la base de datos.", Alert.AlertType.ERROR);
                }
            }
            limpiarCampos();
            cargarInsumos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "La cantidad y stock mínimo deben ser números válidos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleEliminarInsumo() {
        Insumo seleccionado = tablaInsumos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Debe seleccionar un insumo para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        if (insumoDAO.eliminarInsumo(seleccionado.getId())) {
            mostrarAlerta("Éxito", "Insumo eliminado del almacén.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarInsumos();
        }
    }

    @FXML
    private void regresarAlMenu() throws java.io.IOException {
        App.setRoot("primary");
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtCantidad.clear();
        cmbUnidad.getSelectionModel().clearSelection();
        cmbUnidad.setPromptText("Seleccione unidad...");
        txtStockMinimo.clear();
        tablaInsumos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void verificarAlertasStock() {
        IInsumoDAO insumoDAO = new InsumoDAOImpl();
        List<Insumo> insumosBajos = insumoDAO.obtenerInsumosBajoStock();

        // Si la lista no está vacía, significa que hay problemas de stock
        if (!insumosBajos.isEmpty()) {
            StringBuilder mensaje = new StringBuilder("Los siguientes insumos están por agotarse:\n\n");
            
            for (Insumo i : insumosBajos) {
                mensaje.append("• ").append(i.getName())
                       .append(" (Quedan: ").append(i.getQuantity()).append(" ").append(i.getUnit()).append(")\n");
            }

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta de Stock Crítico");
            alert.setHeaderText("¡Atención Gerente! Posible desabasto");
            alert.setContentText(mensaje.toString());
            alert.showAndWait();
        }
    }
}