package com.equipok;

import com.equipok.DAO.BillDAOImpl;
import com.equipok.DAO.IBillDAO;
import com.equipok.model.Bill;
import com.equipok.model.Product;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class EditBillController {
    @FXML 
    private TableView<Product> productsTable;

    @FXML 
    private TableColumn<Product, String> colProductName;

    @FXML 
    private TableColumn<Product, Double> colProductPrice;

    private Bill currentBill;
    private IBillDAO billDAO = new BillDAOImpl();

    @FXML
    private void initialize() {
         if (productsTable != null) {
            colProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colProductPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            productsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            productsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Product> c) -> {
            });
        }
    }

    public void setBill(Bill bill) {
        this.currentBill = bill;
        if (productsTable != null) {
            List<Product> pendingItems = billDAO.getPendingItems(bill.getId());
            productsTable.setItems(FXCollections.observableArrayList(pendingItems));
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Sin selección", "Por favor, seleccione un producto para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        if (currentBill != null) {
            if (billDAO instanceof BillDAOImpl) {
                boolean success = ((BillDAOImpl) billDAO).removeProductFromBill(currentBill.getId(), selectedProduct);
                if (success) {
                    showAlert("Producto eliminado", "El producto fue eliminado correctamente de la cuenta.", Alert.AlertType.INFORMATION);
                    setBill(currentBill); 
                } else {
                    showAlert("Error", "No se pudo eliminar el producto de la base de datos.", Alert.AlertType.ERROR);
                }
            }
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteBill() {
        if (currentBill == null) {
            showAlert("Error", "No hay una cuenta seleccionada para eliminar.", Alert.AlertType.ERROR);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de que deseas eliminar toda la cuenta? Esta acción liberará la mesa.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (billDAO.deleteBill(currentBill.getId())) {
                    showAlert("Cuenta eliminada", "La cuenta ha sido eliminada correctamente.", Alert.AlertType.INFORMATION);
                    handleReturn();
                } else {
                    showAlert("Error", "No se pudo eliminar la cuenta de la base de datos.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void handleAddProducts() {
        if (currentBill == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("orderTaking.fxml"));
            Parent root = loader.load();
            OrderTakingController controller = loader.getController();
            controller.setSourceBill(currentBill);
            StackPane mainPane = (StackPane) productsTable.getScene().lookup("#mainPane");
            if (mainPane != null) {
                mainPane.getChildren().clear();
                mainPane.getChildren().add(root);
            } else {
                App.setRoot(root);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Bills.fxml")); //Pendiente personalizar el fxml para editar
            Parent root = loader.load();
            StackPane mainPane = (StackPane) productsTable.getScene().lookup("#mainPane");
            if (mainPane != null) {
                mainPane.getChildren().clear();
                mainPane.getChildren().add(root);
            } else {
                App.setRoot(root);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
