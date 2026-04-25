package com.equipok;

import com.equipok.DAO.BillDAOImpl;
import com.equipok.DAO.IBillDAO;
import com.equipok.model.Bill;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private boolean paymentConfirmed = false;
    private Bill currentBill;
    private IBillDAO billDAO = new BillDAOImpl();

   @FXML
   private void initialize() {
        txtTip.disableProperty().bind(chkAddTip.selectedProperty().not());
        cbMethod.getItems().addAll("Efectivo", "Tarjeta de crédito");
        txtReceived.disableProperty().bind(
            cbMethod.getSelectionModel().selectedItemProperty().isNotEqualTo("Efectivo")
        );
        cbMethod.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (!"Efectivo".equals(newVal)) {
                lblChange.setText("0.00");
                btnPay.setDisable(newVal == null);
            } 
        });
        txtReceived.textProperty().addListener((bs, old, newVal) -> calculateChange(newVal));
        txtTip.textProperty().addListener((obs, old, newVal) -> calculateChange(txtReceived.getText()));
        chkAddTip.selectedProperty().addListener((obs, old, newVal) -> calculateChange(txtReceived.getText()));
    };

   @FXML
    private void handleConfirm() {
        if (currentBill == null || cbMethod.getValue() == null) {
             showErrorAlert("Datos incompletos", "Por favor, asegúrese de que la factura y el método de pago estén seleccionados.");
            return;
        }
        //Correccion problema de impresión
        Ticket.print(currentBill, cbMethod.getValue());

        double tip = 0;
        if (chkAddTip.isSelected() && !txtTip.getText().isEmpty()) {
            try {
                tip = Double.parseDouble(txtTip.getText());
            } catch (NumberFormatException e) {tip = 0;} 
        }
        if (billDAO.processPayment(currentBill, tip, cbMethod.getValue())){
            paymentConfirmed = true;
            showSuccessAlert("Pago procesado", "cuenta pagada correctamente.");
            closeWindow();
        } else {
            showErrorAlert("Error al procesar el pago", "No se pudo actualizar el pago.");
        }
    }


    private void calculateChange(String receivedInput) {
        try {
            double tip = (chkAddTip.isSelected() && !txtTip.getText().isEmpty()) ? Double.parseDouble(txtTip.getText()) : 0;
            double totalWithTip = currentBill.getTotal() + tip;
            
            if (lblTotal != null) {
                lblTotal.setText(String.format("%.2f", totalWithTip));
            }

            if ("Efectivo".equals(cbMethod.getValue())) {
                // Si es efectivo, el botón se habilita solo si el dinero alcanza
                if (!receivedInput.isEmpty()) {
                    double received = Double.parseDouble(receivedInput);
                    btnPay.setDisable(received < totalWithTip);
                    lblChange.setText(String.format("%.2f", Math.max(0, received - totalWithTip)));
                } else {
                    btnPay.setDisable(true);
                    lblChange.setText("0.00");
                }
            } else if ("Tarjeta de crédito".equals(cbMethod.getValue())) {
                // Si es tarjeta, el botón siempre se habilita (txtReceived está deshabilitado)
                btnPay.setDisable(false);
                lblChange.setText("0.00");
            }
        } catch (NumberFormatException e) {
            btnPay.setDisable(true);
            lblChange.setText("0.00");
        }
    }

    @FXML
    private void handleCancel() {
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

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
