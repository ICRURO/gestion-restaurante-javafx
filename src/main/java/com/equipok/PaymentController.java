package com.equipok;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.equipok.model.Bill;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaymentController {

    @FXML 
    private Label lblTotal;
    
    @FXML
    private Label lblChange;

    @FXML 
    private ComboBox<String> cbMethod;

    @FXML
    private TextField txtReceived;

    @FXML 
    private VBox details;

    @FXML
    private Button btnPay;

    @FXML 
    private CheckBox chkAddTip;

    @FXML 
    private TextField txtTip;

    private double totalAmount;
    private boolean paymentConfirmed = false;
    private Bill currentBill;

   @FXML
    private void initialize() {
        txtTip.disableProperty().bind(chkAddTip.selectedProperty().not());
        txtTip.visibleProperty().bind(chkAddTip.selectedProperty());
        chkAddTip.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (!isSelected) {
                txtTip.clear();
                calculateChange(txtReceived.getText()); 
            }
        });
            txtTip.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*(\\.\\d*)?")) {
                txtTip.setText(oldV);
            } else {
                calculateChange(txtReceived.getText()); 
            }
        });
        cbMethod.getItems().addAll("Efectivo", "Tarjeta de crédito");
        cbMethod.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCash = "Efectivo".equals(newVal);
            details.setVisible(isCash);
            details.setManaged(isCash);
        });
        txtReceived.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtReceived.setText(oldValue); 
            } else {
                calculateChange(newValue); 
            }
        });
        txtReceived.disableProperty().bind(cbMethod.getSelectionModel().selectedItemProperty().isNotEqualTo("Efectivo"));
   }

    public void setTotal(double total) {
        this.totalAmount = total;
        lblTotal.setText(String.format("%.2f", total));
    }

    private void calculateChange(String receivedInput) {
        try {
            double subtotal = currentBill.getTotal();
            double tip = 0.0;
            if (chkAddTip.isSelected() && !txtTip.getText().isEmpty()) {
                tip = Double.parseDouble(txtTip.getText());
            }
            double totalWithTip = subtotal + tip;
            lblTotal.setText(String.format("%.2f", totalWithTip));
            if (receivedInput.isEmpty()) {
                lblChange.setText("0.00");
                return;
            }
            double received = Double.parseDouble(receivedInput);
            double change = received - totalWithTip;
            lblChange.setText(String.format("%.2f", Math.max(0, change)));
            btnPay.setDisable(received < totalWithTip);
        } catch (NumberFormatException e) {
            lblChange.setText("0.00");
        }
    }

   @FXML
    private void handleConfirm() {
        if (currentBill == null) {
            return;
        }
        boolean printSuccess = Ticket.print(currentBill, cbMethod.getValue());
        if (printSuccess) {
            double tip = 0;
            if (chkAddTip.isSelected() && !txtTip.getText().isEmpty()) {
                tip = Double.parseDouble(txtTip.getText());
            }
            finalizePayment(currentBill, tip, cbMethod.getValue());
            paymentConfirmed = true;
            closeWindow();
        } else {
            showPrintErrorAlert();
        }
    }

    @FXML
    private void handleCancel() {
        paymentConfirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnPay.getScene().getWindow();
        stage.close();
    }

    public boolean isPaymentConfirmed() { 
        return paymentConfirmed; 
    }

    public void setBill(Bill bill) {
        this.currentBill = bill;
        if (lblTotal != null) {
            lblTotal.setText(String.format("%.2f", bill.getTotal()));
        }
    }

    private void showPrintErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Error de impresión");
        alert.setHeaderText(null);
        alert.setContentText("No es posible generar el ticket");
        ButtonType btnRetry = new ButtonType("Reintentar");
        ButtonType btnCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE); 
        alert.getButtonTypes().setAll(btnRetry, btnCancel);
        alert.showAndWait().ifPresent(response -> {
            if (response == btnRetry) {
                handleConfirm(); 
            }
        });
    }

    public void finalizePayment(Bill bill, double tip, String method) {
    String sqlBill = "UPDATE bills SET status_bill = 'PAID' WHERE id = ?";
    String sqlTable = "UPDATE tables SET status_table = 'AVAILABLE' WHERE table_id = ?";
    String sqlSale = "INSERT INTO sales (bill_id, table_id, subtotal, tip, total, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = ConexionDB.obtenerConexion()) {
        conn.setAutoCommit(false); 
        try (PreparedStatement pstBill = conn.prepareStatement(sqlBill);
             PreparedStatement pstTable = conn.prepareStatement(sqlTable);
             PreparedStatement pstSale = conn.prepareStatement(sqlSale)) {

            pstBill.setInt(1, bill.getId());
            pstBill.executeUpdate();

            pstTable.setInt(1, bill.getTableId());
            pstTable.executeUpdate();

            pstSale.setInt(1, bill.getId());
            pstSale.setInt(2, bill.getTableId());
            pstSale.setDouble(3, bill.getTotal());
            pstSale.setDouble(4, tip);
            pstSale.setDouble(5, bill.getTotal() + tip);
            pstSale.setString(6, method);
            pstSale.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback(); 
            e.printStackTrace();
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processFinalPayment(Bill bill, double tip, String method) {
        String sqlBill = "UPDATE bills SET status_table = 'PAID' WHERE id = ?";
        String sqlTable = "UPDATE tables SET status_table = 'AVAILABLE' WHERE table_id = ?"; // Liberar mesa [cite: 109]
        String sqlSale = "INSERT INTO sales (bill_id, table_id, subtotal, tip, total, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psB = conn.prepareStatement(sqlBill);
                PreparedStatement psT = conn.prepareStatement(sqlTable);
                PreparedStatement psS = conn.prepareStatement(sqlSale)) {
                psB.setInt(1, bill.getId());
                psB.executeUpdate();

                psT.setInt(1, bill.getTableId());
                psT.executeUpdate();

                psS.setInt(1, bill.getId());
                psS.setInt(2, bill.getTableId());
                psS.setDouble(3, bill.getTotal());
                psS.setDouble(4, tip);
                psS.setDouble(5, bill.getTotal() + tip);
                psS.setString(6, method);
                psS.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            }
            } catch (SQLException e) {
                showErrorAlert("Error al procesar el pago", "No se pudo conectar con la base de datos.");
            }
    }
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

