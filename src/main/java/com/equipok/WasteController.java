package com.equipok;

import com.equipok.DAO.IProductDAO;
import com.equipok.DAO.IWasteDAO;
import com.equipok.DAO.ProductDAOImpl;
import com.equipok.DAO.WasteDAOImpl;
import com.equipok.model.Product;
import com.equipok.model.Waste;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class WasteController implements Initializable {

    @FXML private ComboBox<Product> cbProducts;
    @FXML private TextField txtQuantity;
    @FXML private TextArea txtReason;
    @FXML private TableView<Waste> tblWastes;
    @FXML private TableColumn<Waste, Integer> colId;
    @FXML private TableColumn<Waste, Integer> colProductId;
    @FXML private TableColumn<Waste, Integer> colQuantity;
    @FXML private TableColumn<Waste, String> colReason;
    @FXML private TableColumn<Waste, Timestamp> colDate;

    private IWasteDAO wasteDAO = new WasteDAOImpl();
    private IProductDAO productDAO = new ProductDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTableColumns();
        loadProducts();
        loadWasteReport();
    }

    private void configureTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("wasteId"));
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("wasteDate"));
    }

    private void loadProducts() {
        List<Product> products = productDAO.obtenerProductos();
        ObservableList<Product> items = FXCollections.observableArrayList(products);
        cbProducts.setItems(items);
    }

    private void loadWasteReport() {
        List<Waste> wastes = wasteDAO.getWastes();
        ObservableList<Waste> reportItems = FXCollections.observableArrayList(wastes);
        tblWastes.setItems(reportItems); // Refresca la tabla visualmente
    }

    @FXML
    private void saveRecord() {
        Product selectedProduct = cbProducts.getSelectionModel().getSelectedItem();
        String quantityStr = txtQuantity.getText().trim();
        String reason = txtReason.getText().trim();

        if (selectedProduct == null || quantityStr.isEmpty() || reason.isEmpty()) {
            showAlert("Missing Fields", "Please fill in all fields before saving.", AlertType.WARNING);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);

            if (quantity <= 0) {
                showAlert("Invalid Quantity", "The quantity must be a positive integer value.", AlertType.ERROR);
                return;
            }

            Waste newWaste = new Waste(selectedProduct.getId(), quantity, reason);
            boolean success = wasteDAO.addWaste(newWaste);

            if (success) {
                showAlert("Success", "The waste record has been saved successfully.", AlertType.INFORMATION);
                clearFields();
                loadWasteReport(); // <<-- DETALLE CLAVE: Refresca el reporte inmediatamente en la pantalla
            } else {
                showAlert("Database Error", "The record could not be saved to the database.", AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Format Error", "The quantity field must contain only integer numbers.", AlertType.ERROR);
        }
    }

    @FXML
    private void cancelOperation() throws IOException {
        clearFields();
        App.setRoot("primary");
    }

    private void clearFields() {
        cbProducts.getSelectionModel().clearSelection();
        txtQuantity.clear();
        txtReason.clear();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
