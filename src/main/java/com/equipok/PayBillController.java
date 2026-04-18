package com.equipok;

import com.equipok.model.Bill;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "SELECT * FROM bills WHERE status_bill = 'PENDING'";
        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                billsList.add(new Bill(
                    rs.getInt("id"),
                    rs.getInt("table_id"),
                    rs.getString("items"),
                    rs.getDouble("total")
                ));
            }
            billsTable.setItems(billsList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                updateBillStatus(selectedBill.getId());
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

    private void updateBillStatus(int id) {
        String sql = "UPDATE bills SET status_bill = 'PAID' WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
