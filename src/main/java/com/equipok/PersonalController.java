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
    @FXML private TextField txtPuesto;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cbTurno;
    
    @FXML private TableView<Empleado> tableEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colPuesto;
    @FXML private TableColumn<Empleado, String> colTurno;


    private IEmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();


    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        colTurno.setCellValueFactory(new PropertyValueFactory<>("turno"));
        
        cargarDatos();
    }


    private void cargarDatos() {
        ObservableList<Empleado> lista = FXCollections.observableArrayList(empleadoDAO.listarEmpleados());
        tableEmpleados.setItems(lista);
    }


    @FXML
    private void handleGuardarEmpleado() {
        String nombre = txtNombre.getText();
        String puesto = txtPuesto.getText();
        String telefono = txtTelefono.getText();
        String turno = cbTurno.getValue();


        if (nombre == null || nombre.trim().isEmpty() || puesto == null || puesto.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "El nombre y puesto son obligatorios.");
            return;
        }

        Empleado nuevo = new Empleado(nombre, puesto, telefono, turno);
        if (empleadoDAO.registrarEmpleado(nuevo)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empleado registrado correctamente.");
            cargarDatos(); 
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el registro en la BD.");
        }
    }


    @FXML
    private void handleEliminarEmpleado() {
        Empleado seleccionado = tableEmpleados.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            if (empleadoDAO.eliminarEmpleado(seleccionado.getId())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Empleado eliminado.");
                cargarDatos(); // Recargar tabla
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar de MySQL.");
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
        txtPuesto.clear();
        txtTelefono.clear();
        if (cbTurno.getItems().size() > 0) {
            cbTurno.getSelectionModel().clearSelection();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}