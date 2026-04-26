package com.equipok;

import com.equipok.DAO.ISalesDAO;
import com.equipok.DAO.SalesDAOImpl;
import com.equipok.model.Sales;
import java.time.LocalDateTime; 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SalesReportController {

    @FXML private TableView<Sales> salesTable;
    @FXML private TableColumn<Sales, Integer> colId;
    @FXML private TableColumn<Sales, Double> colTotal;
    @FXML private TableColumn<Sales, String> colMethod;
    @FXML private TableColumn<Sales, LocalDateTime> colDate; 

    private ISalesDAO salesDAO = new SalesDAOImpl();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalFinal")); 
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

        loadSales();
    }

    private void loadSales() {
        ObservableList<Sales> data = FXCollections.observableArrayList(salesDAO.getAllSales());
        salesTable.setItems(data);
    }

    @FXML
    private void goBack() throws Exception {
        App.setRoot("primary");
    }
}