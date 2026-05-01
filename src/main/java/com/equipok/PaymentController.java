package com.equipok;

import com.equipok.DAO.BillDAOImpl;
import com.equipok.DAO.IBillDAO;
import com.equipok.model.Bill;
import com.equipok.model.Product;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    @FXML 
    private CheckBox chkAddDiscount;

    @FXML 
    private TextField txtDiscount;

    @FXML 
    private TableView<Product> productsTable;

    @FXML 
    private TableColumn<Product, String> colProductName;

    @FXML 
    private TableColumn<Product, Double> colProductPrice;

    @FXML
    private Pane barraSuperior;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean paymentConfirmed = false;
    private Bill currentBill;
    private IBillDAO billDAO = new BillDAOImpl();

   @FXML
   private void initialize() {
        if (productsTable != null) {
            colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            productsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            productsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Product> c) -> {
                calculateChange(txtReceived.getText());
            });
            if (barraSuperior != null) {
                barraSuperior.setOnMousePressed(event -> {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                });
                barraSuperior.setOnMouseDragged(event -> {
                    Stage stage = (Stage) barraSuperior.getScene().getWindow();
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                });
            }
        }
        txtTip.disableProperty().bind(chkAddTip.selectedProperty().not());
        if (txtDiscount != null && chkAddDiscount != null) {
            txtDiscount.disableProperty().bind(chkAddDiscount.selectedProperty().not());
        }
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
        if (txtDiscount != null) {
            txtDiscount.textProperty().addListener((obs, old, newVal) -> calculateChange(txtReceived.getText()));
        }
        if (chkAddDiscount != null) {
            chkAddDiscount.selectedProperty().addListener((obs, old, newVal) -> calculateChange(txtReceived.getText()));
        }
    };

   @FXML
    private void handleConfirm() {
        if (currentBill == null || cbMethod.getValue() == null) {
             showErrorAlert("Datos incompletos", "Por favor, asegúrese de que la factura y el método de pago estén seleccionados.");
            return;
        }
        List<Product> itemsToPay = new ArrayList<>();
        if (productsTable != null) {
            itemsToPay.addAll(productsTable.getSelectionModel().getSelectedItems());
        }
        if (itemsToPay.isEmpty()) {
            showErrorAlert("Sin productos", "Por favor, seleccione al menos un producto para pagar.");
            return;
        }
        double tip = 0;
        if (chkAddTip.isSelected() && !txtTip.getText().isEmpty()) {
            try {
                tip = Double.parseDouble(txtTip.getText());
            } catch (NumberFormatException e) {tip = 0;} 
        }
        double discount = 0;
        if (txtDiscount != null && !txtDiscount.getText().isEmpty() && (chkAddDiscount == null || chkAddDiscount.isSelected())) {
            try {
                discount = Double.parseDouble(txtDiscount.getText());
            } catch (NumberFormatException e) { discount = 0; }
        }
        Ticket.print(currentBill, itemsToPay, tip, discount, cbMethod.getValue()); //Ya se corrigió el ticket ya bien
        if (billDAO.processPayment(currentBill, itemsToPay, tip, discount, cbMethod.getValue())){
            paymentConfirmed = true;
            if (billDAO.getPendingItems(currentBill.getId()).isEmpty()) {
                billDAO.updateBillStatus(currentBill.getId());
            }
            showSuccessAlert("Pago procesado", "cuenta pagada correctamente.");
            closeWindow();
        } else {
            showErrorAlert("Error al procesar el pago", "No se pudo actualizar el pago.");
        }
    }

    private void calculateChange(String receivedInput) {
        try {
            double tip = (chkAddTip.isSelected() && !txtTip.getText().isEmpty()) ? Double.parseDouble(txtTip.getText()) : 0;
            double subtotal = 0;
            if (productsTable != null && productsTable.getSelectionModel() != null) {
                for (Product p : productsTable.getSelectionModel().getSelectedItems()) {
                    subtotal += p.getPrice();
                }
            } else if (currentBill != null) {
                subtotal = currentBill.getTotal();
            }
            double discount = 0;
            if (txtDiscount != null && !txtDiscount.getText().isEmpty() && (chkAddDiscount == null || chkAddDiscount.isSelected())) {
                discount = Double.parseDouble(txtDiscount.getText());
            }
            double totalWithTip = Math.max(0, subtotal - discount) + tip;
            if (lblTotal != null) {
                lblTotal.setText(String.format("%.2f", totalWithTip));
            }
            if ("Efectivo".equals(cbMethod.getValue())) {
                if (!receivedInput.isEmpty()) {
                    double received = Double.parseDouble(receivedInput);
                    btnPay.setDisable(received < totalWithTip);
                    lblChange.setText(String.format("%.2f", Math.max(0, received - totalWithTip)));
                } else {
                    btnPay.setDisable(true);
                    lblChange.setText("0.00");
                }
            } else if ("Tarjeta de crédito".equals(cbMethod.getValue())) {
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
        try {
            StackPane mainPane = (StackPane) btnPay.getScene().lookup("#mainPane");
            if (mainPane != null) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("payBill.fxml"));
                Parent root = loader.load();
                mainPane.getChildren().clear();
                mainPane.getChildren().add(root);
            } else {
                App.setRoot("payBill");
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPaymentConfirmed() { 
        return paymentConfirmed; 
    }

    public void setBill(Bill bill) {
        this.currentBill = bill;
        if (productsTable != null) {
            List<Product> pendingItems = billDAO.getPendingItems(bill.getId());
            productsTable.setItems(FXCollections.observableArrayList(pendingItems));
            productsTable.getSelectionModel().selectAll(); 
        }
        calculateChange(txtReceived != null ? txtReceived.getText() : "");
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

    @FXML
    private void barraSuperiorPresionada(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void barraSuperiorArrastrada(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
}
