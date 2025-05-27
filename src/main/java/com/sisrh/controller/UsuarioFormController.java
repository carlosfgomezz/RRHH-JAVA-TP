package com.sisrh.controller;

import com.sisrh.model.Usuario;
import com.sisrh.service.UsuarioService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador para el formulario de creación y edición de usuarios
 */
public class UsuarioFormController {
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private ComboBox<Usuario.Rol> cboRol;
    
    private UsuarioService usuarioService;
    private Usuario usuario;
    private boolean modoEdicion = false;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = new UsuarioService();
        
        // Configurar el combo de roles
        cboRol.setItems(FXCollections.observableArrayList(Usuario.Rol.values()));
        cboRol.setValue(Usuario.Rol.RRHH); // Valor por defecto
    }
    
    /**
     * Establece el usuario a editar
     * @param usuario el usuario a editar
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.modoEdicion = true;
        
        // Cargar los datos del usuario en el formulario
        txtUsername.setText(usuario.getUsername());
        txtPassword.setText(""); // No mostrar la contraseña actual
        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        cboRol.setValue(usuario.getRol());
        
        // En modo edición, el nombre de usuario no se puede cambiar
        txtUsername.setDisable(true);
    }
    
    /**
     * Maneja el evento de guardar usuario
     */
    @FXML
    public void handleGuardar(ActionEvent event) {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }
        
        try {
            if (modoEdicion) {
                // Actualizar usuario existente
                usuario.setNombre(txtNombre.getText().trim());
                usuario.setEmail(txtEmail.getText().trim());
                usuario.setRol(cboRol.getValue());
                
                // Actualizar contraseña solo si se ha ingresado una nueva
                String nuevaPassword = txtPassword.getText().trim();
                if (!nuevaPassword.isEmpty()) {
                    usuario.setPassword(nuevaPassword);
                }
                
                usuarioService.actualizarUsuario(usuario);
                mostrarInfo("Usuario Actualizado", "El usuario ha sido actualizado correctamente");
            } else {
                // Crear nuevo usuario
                Usuario nuevoUsuario = new Usuario(
                        txtUsername.getText().trim(),
                        txtPassword.getText().trim(),
                        txtNombre.getText().trim(),
                        cboRol.getValue());
                nuevoUsuario.setEmail(txtEmail.getText().trim());
                
                usuarioService.registrarUsuario(nuevoUsuario);
                mostrarInfo("Usuario Creado", "El usuario ha sido creado correctamente");
            }
            
            // Cerrar la ventana
            cerrarVentana();
        } catch (Exception e) {
            mostrarError("Error al guardar usuario", e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de cancelar
     */
    @FXML
    public void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    /**
     * Valida los campos del formulario
     * @return true si los campos son válidos, false en caso contrario
     */
    private boolean validarCampos() {
        // Validar nombre de usuario
        if (txtUsername.getText().trim().isEmpty()) {
            mostrarError("Error de validación", "El nombre de usuario es obligatorio");
            txtUsername.requestFocus();
            return false;
        }
        
        // Validar contraseña (solo obligatoria en modo creación)
        if (!modoEdicion && txtPassword.getText().trim().isEmpty()) {
            mostrarError("Error de validación", "La contraseña es obligatoria");
            txtPassword.requestFocus();
            return false;
        }
        
        // Validar nombre completo
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarError("Error de validación", "El nombre completo es obligatorio");
            txtNombre.requestFocus();
            return false;
        }
        
        // Validar correo electrónico
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            mostrarError("Error de validación", "El formato del correo electrónico no es válido");
            txtEmail.requestFocus();
            return false;
        }
        
        // Validar rol
        if (cboRol.getValue() == null) {
            mostrarError("Error de validación", "Debe seleccionar un rol");
            cboRol.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Cierra la ventana actual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Muestra un diálogo de error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de información
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}