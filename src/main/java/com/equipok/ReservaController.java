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
        // Validar que no haya campos vacíos
        if(txtNombre.getText().isEmpty() || txtTelefono.getText().isEmpty() || cbMesas.getValue() == null) {
            mostrarAlerta("Error", "Debes llenar todos los campos y seleccionar una mesa.");
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
                mostrarAlerta("Éxito", "Reserva guardada correctamente. La mesa ahora está RESERVED.");
                cbMesas.getItems().clear();
                cbMesas.getItems().addAll(reservaDAO.obtenerMesasDisponibles());
            } else {
                mostrarAlerta("Error", "No se pudo guardar la reserva en la base de datos.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El número de personas debe ser un valor numérico.");
        }
    }

    @FXML
    private void regresarAlMenu() throws IOException {
        App.setRoot("primary");
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}