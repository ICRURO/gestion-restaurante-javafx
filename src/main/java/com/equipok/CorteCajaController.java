package com.equipok;

import com.equipok.DAO.CorteCajaDAOImpl;
import com.equipok.DAO.ICorteCajaDAO;
import com.equipok.model.CorteCaja;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Map;

public class CorteCajaController {

    @FXML private TextField txtFondoApertura;
    @FXML private TextField txtEfectivoReal;
    @FXML private Label lblEfectivoEsperado;
    @FXML private Label lblTarjetaEsperado;
    @FXML private Label lblDiferencia;
    @FXML private TextArea txtObservaciones;

    private ICorteCajaDAO corteDAO = new CorteCajaDAOImpl();
    private double ventasEfectivo = 0.0;
    private double ventasTarjeta = 0.0;

    @FXML
    public void initialize() {
        Map<String, Double> totales = corteDAO.obtenerVentasDelDia();
        ventasEfectivo = totales.get("Efectivo");
        ventasTarjeta = totales.get("Tarjeta de crédito");

        lblEfectivoEsperado.setText(String.format("$%.2f", ventasEfectivo));
        lblTarjetaEsperado.setText(String.format("$%.2f", ventasTarjeta));
        txtFondoApertura.textProperty().addListener((obs, oldVal, newVal) -> ejecutarBalance());
        txtEfectivoReal.textProperty().addListener((obs, oldVal, newVal) -> ejecutarBalance());
    }

    private void ejecutarBalance() {
        try {
            double fondo = txtFondoApertura.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtFondoApertura.getText());
            double real = txtEfectivoReal.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtEfectivoReal.getText());
            
            double esperadoTotal = fondo + ventasEfectivo;
            double diferencia = real - esperadoTotal;

            lblDiferencia.setText(String.format("$%.2f", diferencia));
            if (diferencia < 0) {
                lblDiferencia.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); 
            } else if (diferencia > 0) {
                lblDiferencia.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;"); 
            } else {
                lblDiferencia.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); 
            }
        } catch (NumberFormatException e) {
            lblDiferencia.setText("Formato numérico erróneo");
        }
    }

    @FXML
    private void handleProcesarCorte() throws IOException {
        String fondoStr = txtFondoApertura.getText().trim();
        String realStr = txtEfectivoReal.getText().trim();

        if (fondoStr.isEmpty() || realStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debe introducir el fondo de apertura y el conteo físico.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double fondo = Double.parseDouble(fondoStr);
            double real = Double.parseDouble(realStr);
            double diferencia = real - (fondo + ventasEfectivo);
            if (diferencia != 0 && txtObservaciones.getText().trim().isEmpty()) {
                mostrarAlerta("Justificación requerida", "Existe un descuadre en caja. Escriba los motivos en Observaciones.", Alert.AlertType.WARNING);
                return;
            }

            CorteCaja c = new CorteCaja();
            c.setFondoApertura(fondo);
            c.setVentasEfectivoEsperado(ventasEfectivo);
            c.setVentasTarjetaEsperado(ventasTarjeta);
            c.setEfectivoRealContado(real);
            c.setDiferencia(diferencia);
            c.setUsuarioCajero(App.getUserRole() != null ? App.getUserRole() : "CAJERO_DEFAULT"); 
            c.setObservaciones(txtObservaciones.getText().trim());

            if (corteDAO.registrarCorteCaja(c)) {
                mostrarAlerta("Corte Exitoso", "El turno financiero ha sido cerrado y guardado correctamente.", Alert.AlertType.INFORMATION);
                App.setRoot("primary"); 
                mostrarAlerta("Error", "No se pudo guardar el registro en MySQL.", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Introduzca únicamente valores numéricos decimales.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void regresarAlMenu() throws IOException {
        App.setRoot("primary");
    }

    private void mostrarAlerta(String tit, String msg, Alert.AlertType t) {
        Alert a = new Alert(t); a.setTitle(tit); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}