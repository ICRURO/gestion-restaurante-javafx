package com.equipok;

import com.equipok.DAO.IReservaDAO;
import com.equipok.DAO.ReservaDAOImpl;
import com.equipok.model.Reserva;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.Optional;

public class GestionReservasController {

    @FXML private TextField txtFecha, txtHora, txtPersonas, txtNombre, txtTelefono;
    @FXML private ComboBox<Integer> cbMesas;
    
    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, String> colHora;
    @FXML private TableColumn<Reserva, Integer> colPersonas;
    @FXML private TableColumn<Reserva, String> colNombre;
    @FXML private TableColumn<Reserva, String> colTelefono;
    @FXML private TableColumn<Reserva, Integer> colMesa;

    private IReservaDAO reservaDAO = new ReservaDAOImpl();

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPersonas.setCellValueFactory(new PropertyValueFactory<>("personas"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoCliente"));
        colMesa.setCellValueFactory(new PropertyValueFactory<>("tableId"));

        actualizarComponentes();

        tablaReservas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtFecha.setText(newSel.getFecha());
                txtHora.setText(newSel.getHora());
                txtPersonas.setText(String.valueOf(newSel.getPersonas()));
                txtNombre.setText(newSel.getNombreCliente());
                txtTelefono.setText(newSel.getTelefonoCliente());
                cbMesas.setValue(newSel.getTableId());
            }
        });
    }

    @FXML
    public void handleModificarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Error", "Selecciona una reserva de la tabla.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Reserva actualizada = new Reserva(
                seleccionada.getIdReserva(),
                txtFecha.getText(), txtHora.getText(),
                Integer.parseInt(txtPersonas.getText()),
                txtNombre.getText(), txtTelefono.getText(),
                seleccionada.getTableId()
            );

            if (reservaDAO.modificarReserva(actualizada)) {
                mostrarAlerta("Éxito", "Reserva modificada correctamente.", Alert.AlertType.INFORMATION);
                actualizarComponentes();
                limpiarCampos();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El número de invitados debe ser numérico.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleCancelarReserva() {
        Reserva seleccionada = tablaReservas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Error", "Selecciona una reserva para cancelar.", Alert.AlertType.ERROR);
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmar Cancelación");
        conf.setHeaderText("¿Dar de baja la reserva de " + seleccionada.getNombreCliente() + "?");
        
        Optional<ButtonType> result = conf.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reservaDAO.cancelarReserva(seleccionada.getIdReserva(), seleccionada.getTableId())) {
                mostrarAlerta("Éxito", "Reserva eliminada. Mesa disponible.", Alert.AlertType.INFORMATION);
                actualizarComponentes();
                limpiarCampos();
            }
        }
    }

    private void actualizarComponentes() {
        tablaReservas.getItems().clear();
        tablaReservas.getItems().addAll(reservaDAO.obtenerTodasLasReservas());
        cbMesas.getItems().clear();
        cbMesas.getItems().addAll(reservaDAO.obtenerMesasDisponibles());
    }

    private void limpiarCampos() {
        txtFecha.clear(); txtHora.clear(); txtPersonas.clear();
        txtNombre.clear(); txtTelefono.clear(); cbMesas.setValue(null);
        tablaReservas.getSelectionModel().clearSelection();
    }

    @FXML
    private void regresarAlFormulario() throws IOException {
        App.setRoot("reserva");
    }

    private void mostrarAlerta(String tit, String msg, Alert.AlertType t) {
        Alert a = new Alert(t); a.setTitle(tit); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}