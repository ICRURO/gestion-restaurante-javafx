package com.equipok;

import com.equipok.DAO.IReservaDAO;
import com.equipok.DAO.ReservaDAOImpl;
import com.equipok.model.Reserva;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class ReservaController {

    @FXML private TextField txtFecha, txtHora, txtPersonas, txtNombre, txtTelefono;
    @FXML private ComboBox<Integer> cbMesas;

    private IReservaDAO reservaDAO = new ReservaDAOImpl();

    @FXML
    public void initialize() {
        cbMesas.getItems().addAll(reservaDAO.obtenerMesasDisponibles());
    }

    @FXML
    public void handleConfirmarReserva() {
        if(txtNombre.getText().isEmpty() || txtTelefono.getText().isEmpty() || cbMesas.getValue() == null) {
            mostrarAlerta("Error", "Debes llenar todos los campos y seleccionar una mesa.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Reserva nueva = new Reserva(
                txtFecha.getText(), txtHora.getText(), 
                Integer.parseInt(txtPersonas.getText()), 
                txtNombre.getText(), txtTelefono.getText(),
                cbMesas.getValue()
            );

            if(reservaDAO.guardarReserva(nueva)) {
                mostrarAlerta("Éxito", "Reserva guardada correctamente.", Alert.AlertType.INFORMATION);
                limpiarFormulario();
            } else {
                mostrarAlerta("Error", "No se pudo guardar la reserva.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El número de personas debe ser numérico.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void abrirPanelGestion() throws IOException {
        App.setRoot("gestionReservas");
    }

    @FXML
    private void regresarAlMenu() throws IOException {
        App.setRoot("primary");
    }

    private void limpiarFormulario() {
        txtFecha.clear();
        txtHora.clear();
        txtPersonas.clear();
        txtNombre.clear();
        txtTelefono.clear();
        cbMesas.getItems().clear();
        cbMesas.getItems().addAll(reservaDAO.obtenerMesasDisponibles());
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}