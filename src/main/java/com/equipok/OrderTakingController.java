package com.equipok;

import com.equipok.model.Product;
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

    @FXML
    public void initialize() {
        colItem.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        loadProducts();
        loadTables();
        loadWaiters();
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
        String sql = "SELECT table_id FROM tables WHERE status_table = 'AVAILABLE'";
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
    private void handleConfirmOrder() {
        if (tableComboBox.getValue() == null || waiterComboBox.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar una mesa y un mesero.", Alert.AlertType.ERROR);
            return;
        }

        try (Connection conn = ConexionDB.obtenerConexion()) {
            String sql = "INSERT INTO bills (table_id, items, total, status_bill) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, Integer.parseInt(tableComboBox.getValue()));
            stmt.setString(2, "Pedido tomado por " + waiterComboBox.getValue()); 
            stmt.setDouble(3, 0.0); 
            stmt.setString(4, "PENDING");
            
            stmt.executeUpdate();
            
            mostrarAlerta("Pedido", "¡Pedido completo!", Alert.AlertType.INFORMATION);
            
            App.setRoot("primary");
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el pedido: " + e.getMessage(), Alert.AlertType.ERROR);
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