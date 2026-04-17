package com.equipok;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;



public class LoginController {
    
    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLogin() throws IOException {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        // Basic validation logic
        if ("admin".equals(user) && "1234".equals(pass)) {
            App.setRoot("primary");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Credenciales incorrectas");
            alert.showAndWait();
        }
    }

}
