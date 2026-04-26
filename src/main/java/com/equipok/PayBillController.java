package com.equipok;

import com.equipok.DAO.BillDAOImpl;
import com.equipok.DAO.IBillDAO;
import com.equipok.model.Bill;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

public class PayBillController {
    
    @FXML
    private TableView<Bill> billsTable;
    
    @FXML
    private TableColumn<Bill, Integer> colTableId;

    @FXML
    private TableColumn<Bill, String> colItems;

    @FXML
    private TableColumn<Bill, Double> colTotal;

    @FXML
    private Button btnPay;

    private IBillDAO billDAO = new BillDAOImpl();
    private ObservableList<Bill> billsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colTableId.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        colItems.setCellValueFactory(new PropertyValueFactory<>("items"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        loadPendingBills();
        btnPay.disableProperty().bind(
            billsTable.getSelectionModel().selectedItemProperty().isNull()
        );
    } 

    private void loadPendingBills() {
        billsList.clear();
        billsList.addAll(billDAO.getUnpaidBills());
        billsTable.setItems(billsList);
    }

    @FXML
    private void handleProcessPayment(){
        Bill selectedBill = billsTable.getSelectionModel().getSelectedItem();
        if (selectedBill == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("payment.fxml"));
            Parent root = loader.load();
            PaymentController dialogController = loader.getController();
            dialogController.setBill(selectedBill);
            Stage stage = new Stage();
            stage.setTitle("Complete Payment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.showAndWait(); 
            if (dialogController.isPaymentConfirmed()) {
                if (billDAO.getPendingItems(selectedBill.getId()).isEmpty()) {
                    billDAO.updateBillStatus(selectedBill.getId());
                }
                loadPendingBills(); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}