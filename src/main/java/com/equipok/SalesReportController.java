package com.equipok;

import com.equipok.DAO.ISalesDAO;
import com.equipok.DAO.SalesDAOImpl;
import com.equipok.model.Sales;
import java.time.LocalDateTime; 
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;

public class SalesReportController {

    @FXML 
    private TableView<Sales> salesTable;
    @FXML 
    private TableColumn<Sales, Integer> colId;
    @FXML 
    private TableColumn<Sales, Double> colTotal;
    @FXML 
    private TableColumn<Sales, String> colMethod;
    @FXML 
    private TableColumn<Sales, LocalDateTime> colDate; 
    @FXML 
    private TableColumn<Sales, String> colItems;
    @FXML 
    private ComboBox<String> cbFilter;

    private ISalesDAO salesDAO = new SalesDAOImpl();
    private ObservableList<Sales> allSales = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalFinal")); 
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colItems.setCellValueFactory(new PropertyValueFactory<>("items"));
        allSales.addAll(salesDAO.getAllSales());
        if (cbFilter != null) {
            cbFilter.getItems().addAll("Todos", "Diario", "Semanal", "Mensual");
            cbFilter.setValue("Todos");
            cbFilter.valueProperty().addListener((obs, old, val) -> applyFilter(val));
        }
        applyFilter("Todos");
    }

    private void applyFilter(String filter) {
        LocalDate today = LocalDate.now();
        List<Sales> filtered = allSales.stream().filter(s -> {
            if (s.getSaleDate() == null) return false;
            LocalDate d = s.getSaleDate().toLocalDate();
            switch (filter) {
                case "Diario": return d.isEqual(today);
                case "Semanal": return !d.isBefore(today.minusDays(7)); 
                case "Mensual": return d.getMonth() == today.getMonth() && d.getYear() == today.getYear();
                default: return true;
            }
        }).collect(Collectors.toList());
        salesTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void generatePDF() {
        String filterName = cbFilter != null ? cbFilter.getValue().toUpperCase() : "GENERAL";
        SalesReport.print(filterName, salesTable.getItems()); //Comentar solo si se quiere imprimir en terminal
        SalesReport.debugToConsole(filterName, salesTable.getItems());
    }

    @FXML
    private void goBack() throws Exception {
        App.setRoot("primary");
    }
}