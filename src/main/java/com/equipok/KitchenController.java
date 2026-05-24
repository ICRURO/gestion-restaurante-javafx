package com.equipok;

import com.equipok.DAO.IKitchenDAO;
import com.equipok.DAO.KitchenDAOImpl;
import com.equipok.model.OrderKitchen;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class KitchenController {

    @FXML private TableView<OrderKitchen> tablePedidos;
    @FXML private TableColumn<OrderKitchen, Integer> colMesa;
    @FXML private TableColumn<OrderKitchen, String> colPlatillo;
    @FXML private TableColumn<OrderKitchen, String> colNotas;
    @FXML private TableColumn<OrderKitchen, String> colEstado;

    private final IKitchenDAO kitchenDAO = new KitchenDAOImpl();
    private ObservableList<OrderKitchen> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMesa.setCellValueFactory(cellData -> cellData.getValue().idTableProperty().asObject());
        colPlatillo.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colNotas.setCellValueFactory(cellData -> cellData.getValue().specialNoteProperty());
        colNotas.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (!item.trim().isEmpty()) {
                        setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-background-color: #fdeaea;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        cargarPedidos();
    }

    private void cargarPedidos() {
        masterData.clear();
        masterData.addAll(kitchenDAO.obtenerPedidosPendientes());
        tablePedidos.setItems(masterData);
    }

    @FXML
    private void handleEnPreparacion() {
        OrderKitchen seleccion = tablePedidos.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            if (kitchenDAO.actualizarEstadoPlatillo(seleccion.getIdItem(), "IN_PREPARATION")) {
                cargarPedidos();
            }
        } else {
            mostrarAlerta("Atención", "Seleccione un pedido de la lista.");
        }
    }

    @FXML
    private void handleListoServir() {
        OrderKitchen seleccion = tablePedidos.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            if (kitchenDAO.actualizarEstadoPlatillo(seleccion.getIdItem(), "READY")) {
                cargarPedidos(); // Desaparece automáticamente del monitor al estar cocinado
            }
        } else {
            mostrarAlerta("Atención", "Seleccione un pedido de la lista.");
        }
    }

    @FXML
    private void handleMarcarAgotado() {
        OrderKitchen seleccion = tablePedidos.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            if (kitchenDAO.marcarProductoAgotadoPorNombre(seleccion.getProductName())) {
                mostrarAlerta("Insumo Agotado", "El platillo '" + seleccion.getProductName() + "' se marcó con stock 0.");
                cargarPedidos();
            }
        } else {
            mostrarAlerta("Atención", "Seleccione qué platillo se terminó en cocina.");
        }
    }

    @FXML
    private void handleRefrescar() {
        cargarPedidos();
    }

    @FXML
    private void handleRegresarMenu() throws IOException {
        App.setRoot("primary");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
