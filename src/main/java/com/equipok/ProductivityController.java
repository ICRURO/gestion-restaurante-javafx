package com.equipok;

import com.equipok.DAO.EmpleadoDAOImpl;
import com.equipok.DAO.IEmpleadoDAO;
import com.equipok.model.Empleado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class ProductivityController {

    @FXML private TableView<Empleado> tableProductivity;
    @FXML private TableColumn<Empleado, Integer> colWaiterId;
    @FXML private TableColumn<Empleado, String> colWaiterName;
    @FXML private TableColumn<Empleado, String> colTotalOrders;

    private final IEmpleadoDAO empleadoDAO = new EmpleadoDAOImpl();

    @FXML
    public void initialize() {
        colWaiterId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colWaiterName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTotalOrders.setCellValueFactory(new PropertyValueFactory<>("puesto")); 

        loadReportData();
    }

    private void loadReportData() {
        ObservableList<Empleado> metricsData = FXCollections.observableArrayList(
            ((EmpleadoDAOImpl) empleadoDAO).getWaiterProductivityReport()
        );
        tableProductivity.setItems(metricsData);
    }

    @FXML
    private void handleRefreshReport() {
        loadReportData();
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
