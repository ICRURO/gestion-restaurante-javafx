package com.equipok;

import com.equipok.model.Bill;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button btnPagar;

    private double totalAmount;
    private boolean paymentConfirmed = false;
    private Bill currentBill;

   @FXML
    private void initialize() {
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

    private void calculateChange(String input) {
        try {
            if (input.isEmpty() || input.equals(".")) {
                lblChange.setText("0.00");
                btnPagar.setDisable(true);
                return;
            }
            double received = Double.parseDouble(input);
            double total = currentBill.getTotal(); 
            double change = received - total;
            lblChange.setText(String.format("%.2f", Math.max(0, change)));
            btnPagar.setDisable(received < total);
        } catch (NumberFormatException e) {
            lblChange.setText("0.00");
            btnPagar.setDisable(true);
        }
    }

    @FXML
    private void handleConfirm() {
        Ticket.print(currentBill, cbMethod.getValue());
        paymentConfirmed = true;
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        paymentConfirmed = false;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnPagar.getScene().getWindow();
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
}

