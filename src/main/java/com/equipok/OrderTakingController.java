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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
        String sql = "SELECT name FROM waiters WHERE status = 'ACTIVE'";
        try (Connection conn = ConexionDB.obtenerConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                waiterComboBox.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
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
        IBillDAO billDAO = new BillDAOImpl();
        Bill activeBill = billDAO.getActiveBillByTable(tableId);
        if (activeBill != null) {
            if (billDAO.addProductsToExistingBill(activeBill.getId(), selectedProducts)) {
                mostrarAlerta("Éxito", "Productos agregados a la cuenta existente de la mesa " + tableId, Alert.AlertType.INFORMATION);
                try { App.setRoot("primary"); } catch(IOException e){}
            } else {
                mostrarAlerta("Error", "No se pudieron agregar los productos a la cuenta.", Alert.AlertType.ERROR);
            }
        } else {
            double total = selectedProducts.stream().mapToDouble(Product::getPrice).sum();
            Bill newBill = new Bill(0, tableId, total);
            if (billDAO.saveBill(newBill, selectedProducts)) {
                mostrarAlerta("Éxito", "Nueva cuenta creada y productos agregados.", Alert.AlertType.INFORMATION);
                try { App.setRoot("primary"); } catch(IOException e){}
            } else {
                mostrarAlerta("Error", "No se pudo crear la nueva cuenta.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancel() throws IOException {
        App.setRoot("primary");
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}