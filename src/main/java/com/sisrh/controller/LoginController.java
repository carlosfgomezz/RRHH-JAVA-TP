package com.sisrh.controller;

import java.io.IOException;

import com.sisrh.model.Usuario;
import com.sisrh.service.UsuarioService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador para la pantalla de inicio de sesión
 */
public class LoginController {
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private Button btnLogin;
    
    @FXML
    private Button btnCancel;
    
    @FXML
    private Label lblError;
    
    private UsuarioService usuarioService;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = new UsuarioService();
        
        // Inicializar el sistema si es necesario (crear usuario admin por defecto)
        usuarioService.inicializarSistema();
        
        // Limpiar mensaje de error
        lblError.setText("");
    }
    
    /**
     * Maneja el evento de inicio de sesión
     * @param event evento de acción
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        
        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor, complete todos los campos");
            return;
        }
        
        // Intentar autenticar al usuario
        Usuario usuario = usuarioService.autenticar(username, password, "127.0.0.1");
        
        if (usuario != null) {
            // Usuario autenticado exitosamente
            // Note: Session logging can be implemented if needed
            
            try {
                // Cargar la pantalla principal
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/Principal.fxml"));
                Parent root = loader.load();
                
                // Pasar el usuario autenticado al controlador principal
                PrincipalController controller = loader.getController();
                controller.setUsuario(usuario);
                
                // Mostrar la pantalla principal
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/sisrh/css/application.css").toExternalForm());
                
                // Establecer la referencia al controlador principal en el Stage
                stage.setUserData(controller);
                
                stage.setScene(scene);
                stage.setTitle("SISRH - Sistema Integral de Salarios y Recursos Humanos");
                stage.setMaximized(true);
                stage.show();
                
            } catch (IOException e) {
                lblError.setText("Error al cargar la pantalla principal");
                e.printStackTrace();
            }
        } else {
            lblError.setText("Usuario o contraseña incorrectos");
        }
    }
    
    /**
     * Maneja el evento de cancelar
     * @param event evento de acción
     */
    @FXML
    public void handleCancel(ActionEvent event) {
        txtUsername.setText("");
        txtPassword.setText("");
        lblError.setText("");
    }
    
    /**
     * Obtiene la dirección IP del cliente (simulado)
     * @return dirección IP
     */
    private String obtenerIpCliente() {
        return "127.0.0.1"; // En una aplicación de escritorio, podríamos obtener la IP local
    }
    
    /**
     * Obtiene información del navegador (simulado)
     * @return información del navegador
     */
    private String obtenerNavegador() {
        return "JavaFX Application"; // En una aplicación de escritorio, podríamos obtener información del sistema
    }
}