package com.sisrh.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sisrh.model.Usuario;
import com.sisrh.service.UsuarioService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/**
 * Controlador para la vista de gestión de usuarios
 */
public class UsuariosListController {
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private TableView<Usuario> tblUsuarios;
    
    @FXML
    private TableColumn<Usuario, String> colUsername;
    
    @FXML
    private TableColumn<Usuario, String> colNombreCompleto;
    
    @FXML
    private TableColumn<Usuario, String> colRol;
    
    @FXML
    private TableColumn<Usuario, String> colUltimoAcceso;
    
    @FXML
    private TableColumn<Usuario, String> colActivo;
    
    private UsuarioService usuarioService;
    private ObservableList<Usuario> listaUsuarios;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = new UsuarioService();
        
        // Configurar las columnas de la tabla
        configurarColumnas();
        
        // Cargar los usuarios
        cargarUsuarios();
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarColumnas() {
        // Columna de nombre de usuario
        colUsername.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            return new SimpleStringProperty(usuario.getUsername());
        });
        
        // Columna de nombre completo
        colNombreCompleto.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            return new SimpleStringProperty(usuario.getNombre());
        });
        
        // Columna de rol
        colRol.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            return new SimpleStringProperty(usuario.getRol().toString());
        });
        
        // Columna de último acceso
        colUltimoAcceso.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            if (usuario.getUltimoAcceso() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new SimpleStringProperty(usuario.getUltimoAcceso().format(formatter));
            } else {
                return new SimpleStringProperty("Nunca");
            }
        });
        
        // Columna de estado activo
        colActivo.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue();
            return new SimpleStringProperty(usuario.isActivo() ? "Sí" : "No");
        });
    }
    
    /**
     * Carga los usuarios en la tabla
     */
    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioService.listarTodos();
            listaUsuarios = FXCollections.observableArrayList(usuarios);
            tblUsuarios.setItems(listaUsuarios);
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios", e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de búsqueda de usuarios
     */
    @FXML
    public void handleBuscar(ActionEvent event) {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        
        if (textoBusqueda.isEmpty()) {
            cargarUsuarios();
            return;
        }
        
        // Filtrar la lista de usuarios según el texto de búsqueda
        ObservableList<Usuario> usuariosFiltrados = FXCollections.observableArrayList();
        
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getUsername().toLowerCase().contains(textoBusqueda) ||
                usuario.getNombre().toLowerCase().contains(textoBusqueda)) {
                usuariosFiltrados.add(usuario);
            }
        }
        
        tblUsuarios.setItems(usuariosFiltrados);
    }
    
    /**
     * Maneja el evento de creación de un nuevo usuario
     */
    @FXML
    public void handleNuevo(ActionEvent event) {
        // Por implementar: Abrir formulario de creación de usuario
        mostrarInfo("Nuevo Usuario", "Funcionalidad por implementar");
        
        // Ejemplo de implementación futura:
        // try {
        //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/UsuarioForm.fxml"));
        //     Parent root = loader.load();
        //     UsuarioFormController controller = loader.getController();
        //     
        //     Stage stage = new Stage();
        //     stage.setTitle("Nuevo Usuario");
        //     stage.setScene(new Scene(root));
        //     stage.initModality(Modality.WINDOW_MODAL);
        //     stage.initOwner(tblUsuarios.getScene().getWindow());
        //     stage.showAndWait();
        //     
        //     // Recargar la lista después de cerrar el formulario
        //     cargarUsuarios();
        // } catch (Exception e) {
        //     mostrarError("Error al abrir formulario", e.getMessage());
        // }
    }
    
    /**
     * Maneja el evento de edición de un usuario
     */
    @FXML
    public void handleEditar(ActionEvent event) {
        Usuario usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un usuario para editar");
            return;
        }
        
        // Por implementar: Abrir formulario de edición de usuario
        mostrarInfo("Editar Usuario", "Funcionalidad por implementar");
        
        // Ejemplo de implementación futura:
        // try {
        //     FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/UsuarioForm.fxml"));
        //     Parent root = loader.load();
        //     UsuarioFormController controller = loader.getController();
        //     controller.setUsuario(usuarioSeleccionado);
        //     
        //     Stage stage = new Stage();
        //     stage.setTitle("Editar Usuario");
        //     stage.setScene(new Scene(root));
        //     stage.initModality(Modality.WINDOW_MODAL);
        //     stage.initOwner(tblUsuarios.getScene().getWindow());
        //     stage.showAndWait();
        //     
        //     // Recargar la lista después de cerrar el formulario
        //     cargarUsuarios();
        // } catch (Exception e) {
        //     mostrarError("Error al abrir formulario", e.getMessage());
        // }
    }
    
    /**
     * Maneja el evento de reseteo de contraseña de un usuario
     */
    @FXML
    public void handleResetearContrasena(ActionEvent event) {
        Usuario usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un usuario para resetear la contraseña");
            return;
        }
        
        // Confirmar reseteo
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Reseteo");
        alert.setHeaderText("¿Está seguro que desea resetear la contraseña del usuario?");
        alert.setContentText("Usuario: " + usuarioSeleccionado.getUsername());
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            // Solicitar nueva contraseña
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nueva Contraseña");
            dialog.setHeaderText("Ingrese la nueva contraseña para el usuario");
            dialog.setContentText("Contraseña:");
            
            dialog.showAndWait().ifPresent(nuevaPassword -> {
                try {
                    usuarioService.cambiarPassword(usuarioSeleccionado, nuevaPassword);
                    mostrarInfo("Contraseña Reseteada", "La contraseña ha sido reseteada correctamente");
                } catch (Exception e) {
                    mostrarError("Error al resetear contraseña", e.getMessage());
                }
            });
        }
    }
    
    /**
     * Maneja el evento de activación/desactivación de un usuario
     */
    @FXML
    public void handleActivarDesactivar(ActionEvent event) {
        Usuario usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un usuario para activar/desactivar");
            return;
        }
        
        // Determinar la acción (activar o desactivar)
        String accion = usuarioSeleccionado.isActivo() ? "desactivar" : "activar";
        
        // Confirmar acción
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar " + accion);
        alert.setHeaderText("¿Está seguro que desea " + accion + " al usuario?");
        alert.setContentText("Usuario: " + usuarioSeleccionado.getUsername());
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                // Cambiar estado
                usuarioSeleccionado.setActivo(!usuarioSeleccionado.isActivo());
                usuarioService.actualizarUsuario(usuarioSeleccionado);
                
                // Actualizar la tabla
                cargarUsuarios();
                
                // Mostrar mensaje de éxito
                String mensaje = "El usuario ha sido " + (usuarioSeleccionado.isActivo() ? "activado" : "desactivado") + " correctamente";
                mostrarInfo("Usuario " + (usuarioSeleccionado.isActivo() ? "Activado" : "Desactivado"), mensaje);
            } catch (Exception e) {
                mostrarError("Error al " + accion + " usuario", e.getMessage());
            }
        }
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