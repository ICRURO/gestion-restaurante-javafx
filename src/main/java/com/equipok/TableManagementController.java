package com.equipok;

import com.equipok.DAO.TableDAO;
import com.equipok.model.Table;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class TableManagementController {
    @FXML private TextField txtTableId, txtCapacity;
    @FXML private TableView<Table> tblTables;
    @FXML private TableColumn<Table, Integer> colId, colCapacity;
    @FXML private TableColumn<Table, String> colStatus;

    private TableDAO tableDAO = new TableDAO();
    private Table selectedTable = null;

    @FXML
    public void initialize() {
        try {
            if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("tableId"));
            if (colCapacity != null) colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
            if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            if (tblTables != null) {
                loadData();
                tblTables.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
                    if (val != null) {
                        selectedTable = val;
                        if (txtTableId != null) txtTableId.setText(String.valueOf(val.getTableId()));
                        if (txtCapacity != null) txtCapacity.setText(String.valueOf(val.getCapacity()));
                        if (txtTableId != null) txtTableId.setEditable(false);
                    }
                });
            } else {
                System.out.println("ADVERTENCIA: La tabla es nula. Faltan asignar los fx:id en el archivo FXML.");
            }
        } catch (Exception e) {
            System.out.println("Error al inicializar la vista de mesas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSave() {
        try {
            int id = Integer.parseInt(txtTableId.getText());
            int cap = Integer.parseInt(txtCapacity.getText());

            if (selectedTable == null) {
                tableDAO.save(new Table(id, "AVAILABLE", cap));
            } else {
                selectedTable.setCapacity(cap);
                tableDAO.update(selectedTable);
            }
            loadData();
            clearFields();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Invalid data. Please check numbers.").show();
        }
    }

    @FXML
    private void onDelete() {
        if (selectedTable != null) {
            tableDAO.delete(selectedTable.getTableId());
            loadData();
            clearFields();
        }
    }

    private void loadData() {
        tblTables.setItems(FXCollections.observableArrayList(tableDAO.findAll()));
    }

    private void clearFields() {
        txtTableId.clear();
        txtCapacity.clear();
        txtTableId.setEditable(true);
        selectedTable = null;
    }

    @FXML
    private void regresarAlMenu() throws IOException {
        App.setRoot("primary");
    }
}