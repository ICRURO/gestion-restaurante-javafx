package com.equipok;

import com.equipok.DAO.FacturaDAOImpl;
import com.equipok.DAO.IFacturaDAO;
import com.equipok.model.Sales;
import com.equipok.model.Factura;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.UUID;

public class BillingController {

    @FXML 
    private TextField txtTicketId;
    @FXML 
    private Label lblItems;
    @FXML 
    private Label lblSubtotal;
    @FXML 
    private Label lblTotal;
    @FXML 
    private TextField txtRfc;
    @FXML 
    private TextField txtRazonSocial;
    @FXML 
    private TextField txtCodigoPostal;
    @FXML 
    private ComboBox<String> cbRegimenFiscal;
    @FXML 
    private ComboBox<String> cbUsoCfdi;
    @FXML 
    private Button btnGenerarFactura;

    private IFacturaDAO facturaDAO = new FacturaDAOImpl();
    private Sales ventaSeleccionada = null;

    @FXML
    public void initialize() {
        cbRegimenFiscal.getItems().addAll(
            "601 - General de Ley Personas Morales",
            "603 - Personas Morales con Fines no Lucrativos",
            "605 - Sueldos y Salarios e Ingresos Asimilados a Salarios",
            "626 - Régimen Simplificado de Confianza (RESICO)"
        );
        cbUsoCfdi.getItems().addAll(
            "G03 - Gastos en general",
            "D01 - Honorarios médicos, dentales y gastos hospitalarios",
            "S01 - Sin efectos fiscales"
        );
        
        btnGenerarFactura.setDisable(true); 
    }

    @FXML
    private void handleBuscarTicket() {
        String idStr = txtTicketId.getText().trim();
        if (idStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Introduce un número de ticket.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int ticketId = Integer.parseInt(idStr);
            
            if (facturaDAO.existeFactura(ticketId)) {
                mostrarAlerta("Ticket ya facturado", "Este ticket ya cuenta con un CFDI emitido previamente.", Alert.AlertType.WARNING);
                limpiarResumenTicket();
                return;
            }

            ventaSeleccionada = facturaDAO.buscarVentaPorId(ticketId);

            if (ventaSeleccionada != null) {
                lblItems.setText(ventaSeleccionada.getItems());
                lblSubtotal.setText(String.format("$%.2f", ventaSeleccionada.getSubtotal()));
                lblTotal.setText(String.format("$%.2f", ventaSeleccionada.getTotalFinal()));
                btnGenerarFactura.setDisable(false); 
            } else {
                mostrarAlerta("No encontrado", "No se localizó ninguna venta cobrada con ese ID.", Alert.AlertType.ERROR);
                limpiarResumenTicket();
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El ID de ticket debe ser un número entero.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleGenerarFactura() {
        if (ventaSeleccionada == null) return;
        String rfc = txtRfc.getText().trim();
        String razonSocial = txtRazonSocial.getText().trim();
        String cp = txtCodigoPostal.getText().trim();
        String regimen = cbRegimenFiscal.getValue();
        String uso = cbUsoCfdi.getValue();
        if (rfc.isEmpty() || razonSocial.isEmpty() || cp.isEmpty() || regimen == null || uso == null) {
            mostrarAlerta("Datos incompletos", "Por favor llene todos los campos fiscales del cliente.", Alert.AlertType.WARNING);
            return;
        }
        if (rfc.length() < 12 || rfc.length() > 13) {
            mostrarAlerta("RFC Inválido", "El RFC debe tener entre 12 (P. Morales) o 13 (P. Físicas) caracteres.", Alert.AlertType.WARNING);
            return;
        }
        if (cp.length() != 5) {
            mostrarAlerta("Código Postal Inválido", "El Código Postal debe ser de exactamente 5 dígitos.", Alert.AlertType.WARNING);
            return;
        }
        String uuidSimulado = UUID.randomUUID().toString().toUpperCase();

        Factura nuevaFactura = new Factura(ventaSeleccionada.getId(), rfc, razonSocial, regimen, cp, uso, uuidSimulado);

        if (facturaDAO.registrarFactura(nuevaFactura)) {
            try {
                String ruta = "src/main/resources/com/equipok/";
                java.io.File directorio = new java.io.File(ruta);
                if (!directorio.exists()) {
                    directorio.mkdirs();
                }
                String nombreArchivo = ruta + "Factura_Ticket_" + ventaSeleccionada.getId() + ".txt";
                java.io.FileWriter writer = new java.io.FileWriter(nombreArchivo);
                
                writer.write("====================================================\n");
                writer.write("          FACTURA ELECTRÓNICA SIMULADA (CFDI 4.0)   \n");
                writer.write("====================================================\n");
                writer.write("FOLIO FISCAL (UUID): " + uuidSimulado + "\n");
                writer.write("FECHA EMISIÓN: " + java.time.LocalDateTime.now() + "\n\n");
                writer.write("EMISOR:\n");
                writer.write(" RESTAURANTE EQUIPO K\n\n");
                writer.write("RECEPTOR:\n");
                writer.write(" RFC: " + rfc + "\n");
                writer.write(" RAZÓN SOCIAL: " + razonSocial + "\n");
                writer.write(" CÓDIGO POSTAL: " + cp + "\n");
                writer.write(" RÉGIMEN: " + regimen + "\n");
                writer.write(" USO CFDI: " + uso + "\n");
                writer.write("----------------------------------------------------\n");
                writer.write("CONCEPTOS DEL CONSUMO:\n");
                writer.write(" " + ventaSeleccionada.getItems() + "\n");
                writer.write("----------------------------------------------------\n");
                writer.write(" SUBTOTAL: " + String.format("$%.2f", ventaSeleccionada.getSubtotal()) + "\n");
                writer.write(" TOTAL COBRADO: " + String.format("$%.2f", ventaSeleccionada.getTotalFinal()) + "\n");
                writer.write("====================================================\n");
                writer.write("   Este documento es una simulación con fines educativos.  \n");
                writer.write("====================================================\n");
                
                writer.close(); 
                System.out.println("Documento de factura generado en: " + nombreArchivo);
                
            } catch (IOException e) {
                System.err.println("No se pudo generar el archivo físico: " + e.getMessage());
            }

            mostrarAlerta("Factura Generada", "La factura se ha generado y registrado exitosamente.", Alert.AlertType.INFORMATION);
            limpiarCamposCliente();
        } else {
            mostrarAlerta("Error", "No se pudo registrar la factura en la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void regresarAlMenu() throws IOException {
        App.setRoot("primary"); 
    }

    private void limpiarCamposCliente() {
        txtTicketId.clear();
        txtRfc.clear();
        txtRazonSocial.clear();
        txtCodigoPostal.clear();
        cbRegimenFiscal.getSelectionModel().clearSelection();
        cbUsoCfdi.getSelectionModel().clearSelection();
        limpiarResumenTicket();
    }

    private void limpiarResumenTicket() {
        ventaSeleccionada = null;
        lblItems.setText("---");
        lblSubtotal.setText("$0.00");
        lblTotal.setText("$0.00");
        btnGenerarFactura.setDisable(true);
    }

    private void mostrarAlerta(String tit, String msg, Alert.AlertType t) {
        Alert a = new Alert(t); a.setTitle(tit); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}