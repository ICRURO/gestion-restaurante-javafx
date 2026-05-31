package com.equipok;

import com.equipok.DAO.EmpleadoDAOImpl;
import com.equipok.DAO.IEmpleadoDAO;
import com.equipok.model.Empleado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class PersonalController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbEstado; 
    
    @FXML private TableView<Empleado> tableEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colTelefono; 
    @FXML private TableColumn<Empleado, String> colEstado;   

    private IEmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();
    private Empleado selectedEmployee = null; 

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("turno")); 
        cmbEstado.getItems().addAll("ACTIVE", "INACTIVE");
        cmbEstado.setValue("ACTIVE");

        cargarDatos();

        tableEmpleados.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEmployee = newSelection;
                txtNombre.setText(newSelection.getNombre());
                txtTelefono.setText(newSelection.getTelefono());
                cmbEstado.setValue(newSelection.getTurno()); // Loads current ACTIVE/INACTIVE value
            }
        });
    }

    private void cargarDatos() {
        ObservableList<Empleado> lista = FXCollections.observableArrayList(((EmpleadoDAOImpl) empleadoDAO).listWaitersOnly());
        tableEmpleados.setItems(lista);
    }

    @FXML
    private void handleGuardarEmpleado() {
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String estado = cmbEstado.getValue();

        if (nombre == null || nombre.trim().isEmpty() || telefono == null || telefono.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validation Error", "All fields are required.");
            return;
        }

        if (selectedEmployee == null) {

            Empleado nuevo = new Empleado();
            nuevo.setNombre(nombre);
            nuevo.setPuesto("Mesero");
            nuevo.setTelefono(telefono);
            nuevo.setTurno(estado);

            if (((EmpleadoDAOImpl) empleadoDAO).registerWaiterOnly(nuevo)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Waiter registered successfully.");
                cargarDatos(); 
                limpiarCampos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el registro en la BD.");
            }
        } else {
            selectedEmployee.setNombre(nombre);
            selectedEmployee.setTelefono(telefono);
            selectedEmployee.setTurno(estado);

            if (((EmpleadoDAOImpl) empleadoDAO).updateWaiterData(selectedEmployee)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Waiter updated successfully.");
                cargarDatos();
                limpiarCampos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el registro en la BD.");
            }
        }
    }

    @FXML
    private void handleEliminarEmpleado() {
        Empleado seleccionado = tableEmpleados.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            if (((EmpleadoDAOImpl) empleadoDAO).disableWaiterStatus(seleccionado.getId())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Waiter status updated to INACTIVE.");
                cargarDatos(); 
                limpiarCampos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo deshabilitar en MySQL.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un empleado de la tabla primero.");
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtTelefono.clear();
        cmbEstado.setValue("ACTIVE");
        selectedEmployee = null;
        tableEmpleados.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void handleBorrarWaiterPermanente() {
    Empleado seleccionado = tableEmpleados.getSelectionModel().getSelectedItem();
    
    if (seleccionado != null) {
        if (((EmpleadoDAOImpl) empleadoDAO).deleteWaiterPhysically(seleccionado.getId())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Waiter permanently deleted from database.");
            cargarDatos(); // Refreshes the TableView immediately
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar de MySQL (Check foreign keys).");
        }
        } else {
        mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un empleado de la tabla primero.");
        }
    }
}