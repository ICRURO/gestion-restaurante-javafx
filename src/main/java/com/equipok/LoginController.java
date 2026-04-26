package com.equipok; //Queda pendiente hacerlo bonito

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    
    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private CheckBox chkShowPassword;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        if (chkShowPassword != null && txtPasswordVisible != null) {
            txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
            txtPasswordVisible.visibleProperty().bind(chkShowPassword.selectedProperty());
            txtPasswordVisible.managedProperty().bind(chkShowPassword.selectedProperty());
            txtPassword.visibleProperty().bind(chkShowPassword.selectedProperty().not());
            txtPassword.managedProperty().bind(chkShowPassword.selectedProperty().not());
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        String usuario = txtUsuario.getText();
        String contrasenia = txtPassword.getText();
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasenia = ?";
        try (Connection conexion = ConexionDB.obtenerConexion();
            PreparedStatement pstmt = conexion.prepareStatement(sql)){
                pstmt.setString(1, usuario);
                pstmt.setString(2, contrasenia);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    App.setRoot("primary");
                } else {
                    mostrarAlerta("Error", "Usuario o contraseña incorrectos");
                }
            }catch (SQLException e){
                e.printStackTrace();
                mostrarAlerta("Error", "Error al conectar a la base de datos");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
