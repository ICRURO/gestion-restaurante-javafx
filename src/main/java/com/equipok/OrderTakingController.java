package com.equipok;

import com.equipok.DAO.BillDAOImpl;
import com.equipok.DAO.IBillDAO;
import com.equipok.model.Bill;
import com.equipok.model.Product;
import java.util.List;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import java.io.IOException;
import java.sql.*;

public class OrderTakingController {

    @FXML private TableView<Product> orderTable;
    @FXML private TableColumn<Product, String> colItem;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private ComboBox<String> tableComboBox;
    @FXML private ComboBox<String> waiterComboBox;
    @FXML private ListView<Product> cartListView;
    
    private ObservableList<Product> cartProducts = FXCollections.observableArrayList();
    private Bill sourceBill = null;

    @FXML
    public void initialize() {
        colItem.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadProducts();
        loadTables();
        loadWaiters();
        if (cartListView != null) {
            cartListView.setItems(cartProducts);
            cartListView.setCellFactory(param -> new ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName() + " - $" + String.format("%.2f", item.getPrice()));
                    }
                }
            });
        }
    }

    private void loadProducts() {
        ObservableList<Product> productList = FXCollections.observableArrayList();
        String sql = "SELECT name, price FROM products";
        try (Connection conn = ConexionDB.obtenerConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productList.add(new Product(rs.getString("name"), rs.getDouble("price")));
            }
            orderTable.setItems(productList);
        } catch (SQLException e) {
            System.out.println("Error cargando productos: " + e.getMessage());
        }
    }

    private void loadTables() {
        String sql = "SELECT table_id FROM tables"; 
        try (Connection conn = ConexionDB.obtenerConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableComboBox.getItems().add(String.valueOf(rs.getInt("table_id")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadWaiters() {
    waiterComboBox.getItems().clear();   
    String sql = "SELECT name FROM waiters WHERE status = 'ACTIVE'";
    try (Connection conn = ConexionDB.obtenerConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
         
        while (rs.next()) {
            waiterComboBox.getItems().add(rs.getString("name"));
        }
    } catch (SQLException e) { 
        System.err.println("SQL Error inside loadWaiters combo renderer: " + e.getMessage());
    }
}

    @FXML
    private void handleAddToCart() {
        Product selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarAlerta("Error", "Selecciona un producto del menú primero.", Alert.AlertType.WARNING);
            return;
        }
        cartProducts.add(new Product(selected.getName(), selected.getPrice()));
    }

    @FXML
    private void handleRemoveFromCart() {
        Product selected = cartListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartProducts.remove(selected);
        }
    }

    @FXML
    private void handleConfirmOrder() {
        if (tableComboBox.getValue() == null || waiterComboBox.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar una mesa y un mesero.", Alert.AlertType.ERROR);
            return;
        }

        List<Product> selectedProducts = new ArrayList<>(cartProducts);
        if (selectedProducts.isEmpty()) {
            mostrarAlerta("Error", "Debes seleccionar al menos un producto.", Alert.AlertType.ERROR);
            return;
        }
        
        int tableId = Integer.parseInt(tableComboBox.getValue());
        String selectedWaiterName = waiterComboBox.getValue(); // Get the waiter name selected in the GUI
        IBillDAO billDAO = new BillDAOImpl();
        Bill activeBill = billDAO.getActiveBillByTable(tableId);
        
        if (activeBill != null) {
            // Case 1: Add products to an existing open table bill
            if (billDAO.addProductsToExistingBill(activeBill.getId(), selectedProducts)) {
                // Update the waiter reference on this bill in case a different one is handling it now
                updateWaiterInBill(activeBill.getId(), selectedWaiterName);
                
                mostrarAlerta("Éxito", "Productos agregados a la cuenta existente de la mesa " + tableId, Alert.AlertType.INFORMATION);
                try { navigateBack(); } catch(IOException e){}
            } else {
                mostrarAlerta("Error", "No se pudieron agregar los productos a la cuenta.", Alert.AlertType.ERROR);
            }
        } else {
            // Case 2: Create a completely new bill for this table
            double total = selectedProducts.stream().mapToDouble(Product::getPrice).sum();
            Bill newBill = new Bill(0, tableId, total);
            
            if (billDAO.saveBill(newBill, selectedProducts)) {
                // Fetch the recently generated bill ID from database to attach our waiter id column link
                Bill savedBill = billDAO.getActiveBillByTable(tableId);
                if (savedBill != null) {
                    updateWaiterInBill(savedBill.getId(), selectedWaiterName);
                }
                
                mostrarAlerta("Éxito", "Nueva cuenta creada y productos agregados.", Alert.AlertType.INFORMATION);
                try { navigateBack(); } catch(IOException e){}
            } else {
                mostrarAlerta("Error", "No se pudo crear la nueva cuenta.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        navigateBack();
    }

    private void navigateBack() throws IOException {
        if (sourceBill != null) {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("EditBill.fxml"));
            Parent root = loader.load();
            EditBillController controller = loader.getController();
            
            IBillDAO dao = new BillDAOImpl();
            Bill updatedBill = dao.getActiveBillByTable(sourceBill.getTableId());
            controller.setBill(updatedBill != null ? updatedBill : sourceBill);

            StackPane mainPane = (StackPane) tableComboBox.getScene().lookup("#mainPane");
            if (mainPane != null) {
                mainPane.getChildren().clear();
                mainPane.getChildren().add(root);
            } else {
                App.setRoot(root);
            }
        } else {
            App.setRoot("primary");
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    public void setPreselectedTable(int tableId) {
        tableComboBox.setValue(String.valueOf(tableId));
        tableComboBox.setDisable(true);
    }

    public void setSourceBill(Bill bill) {
        this.sourceBill = bill;
        setPreselectedTable(bill.getTableId());
    }

    private void updateWaiterInBill(int billId, String waiterName) {
        String queryWaiterId = "SELECT waiter_id FROM waiters WHERE name = ?";
        String updateBill = "UPDATE bills SET waiter_id = ? WHERE id = ?";
        
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement psQuery = conn.prepareStatement(queryWaiterId)) {
            
            psQuery.setString(1, waiterName);
            try (ResultSet rs = psQuery.executeQuery()) {
                if (rs.next()) {
                    int waiterId = rs.getInt("waiter_id");
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateBill)) {
                        psUpdate.setInt(1, waiterId);
                        psUpdate.setInt(2, billId);
                        psUpdate.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error actualizando mesero en la cuenta: " + e.getMessage());
        }
    }
}