package com.sisrh.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.sisrh.model.Usuario;
import com.sisrh.service.UsuarioService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * Controlador para la pantalla principal de la aplicación
 */
public class PrincipalController {
    
    @FXML
    private Label lblUsuario;
    
    @FXML
    private Label lblRol;
    
    @FXML
    private Label lblFecha;
    
    @FXML
    private Label lblEstado;
    
    @FXML
    private TabPane tabPane;
    
    private Usuario usuarioActual;
    private UsuarioService usuarioService;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        usuarioService = new UsuarioService();
        
        // Configurar la fecha actual
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFecha.setText(hoy.format(formatter));
    }
    
    /**
     * Establece el usuario actual y actualiza la interfaz
     * @param usuario el usuario autenticado
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        
        // Actualizar la interfaz con la información del usuario
        lblUsuario.setText(usuario.getNombre());
        lblRol.setText(usuario.getRol().toString());
        
        // Configurar permisos según el rol del usuario
        configurarPermisos();
    }
    
    /**
     * Configura los permisos de la interfaz según el rol del usuario
     */
    private void configurarPermisos() {
        // Implementar lógica para habilitar/deshabilitar elementos del menú según el rol
        // Por ahora, simplemente mostramos un mensaje en la barra de estado
        lblEstado.setText("Sesión iniciada como " + usuarioActual.getRol());
    }
    
    /**
     * Abre una nueva pestaña con el contenido especificado
     * @param title título de la pestaña
     * @param fxmlPath ruta al archivo FXML
     * @return el controlador de la vista cargada
     */
    public Object abrirNuevaTab(String title, String fxmlPath) {
        try {
            // Verificar si ya existe una pestaña con ese título
            for (Tab tab : tabPane.getTabs()) {
                if (tab.getText().equals(title)) {
                    tabPane.getSelectionModel().select(tab);
                    return null;
                }
            }
            
            // Cargar el contenido FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // Crear una nueva pestaña
            Tab tab = new Tab(title);
            tab.setContent(root);
            
            // Agregar la pestaña al TabPane y seleccionarla
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            
            return loader.getController();
        } catch (IOException e) {
            mostrarError("Error al abrir la pestaña", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Muestra un diálogo de error
     * @param titulo título del diálogo
     * @param mensaje mensaje de error
     */
    public void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Muestra un diálogo de información
     * @param titulo título del diálogo
     * @param mensaje mensaje informativo
     */
    public void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Manejadores de eventos del menú Archivo
    
    @FXML
    public void handleCambiarContrasena(ActionEvent event) {
        // Por implementar: Abrir diálogo para cambiar contraseña
        mostrarInfo("Cambiar Contraseña", "Funcionalidad por implementar");
    }
    
    @FXML
    public void handleCerrarSesion(ActionEvent event) {
        // Registrar cierre de sesión
        if (usuarioActual != null) {
            usuarioService.registrarCierreSesion(usuarioActual);
        }
        
        try {
            // Volver a la pantalla de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) tabPane.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/sisrh/css/application.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SISRH - Sistema Integral de Salarios y Recursos Humanos");
            stage.setMaximized(false);
            stage.show();
            
        } catch (IOException e) {
            mostrarError("Error al cerrar sesión", e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleSalir(ActionEvent event) {
        // Registrar cierre de sesión antes de salir
        if (usuarioActual != null) {
            usuarioService.registrarCierreSesion(usuarioActual);
        }
        
        // Cerrar la aplicación
        Stage stage = (Stage) tabPane.getScene().getWindow();
        stage.close();
    }
    
    // Manejadores de eventos del menú Empleados
    
    @FXML
    public void handleNuevoEmpleado(ActionEvent event) {
        abrirNuevaTab("Nuevo Empleado", "/com/sisrh/view/EmpleadoForm.fxml");
    }
    
    @FXML
    public void handleGestionEmpleados(ActionEvent event) {
        abrirNuevaTab("Gestión de Empleados", "/com/sisrh/view/EmpleadosList.fxml");
    }
    
    @FXML
    public void handleGestionContratos(ActionEvent event) {
        abrirNuevaTab("Gestión de Contratos", "/com/sisrh/view/ContratosList.fxml");
    }
    
    @FXML
    public void handleHistorialLaboral(ActionEvent event) {
        abrirNuevaTab("Historial Laboral", "/com/sisrh/view/HistorialLaboralList.fxml");
    }
    
    // Manejadores de eventos del menú Salarios
    
    @FXML
    public void handleGenerarSalarios(ActionEvent event) {
        abrirNuevaTab("Generar Salarios", "/com/sisrh/view/GenerarSalarios.fxml");
    }
    
    @FXML
    public void handleGestionSalarios(ActionEvent event) {
        abrirNuevaTab("Gestión de Salarios", "/com/sisrh/view/SalariosList.fxml");
    }
    
    @FXML
    public void handleReporteIPS(ActionEvent event) {
        abrirNuevaTab("Reporte IPS", "/com/sisrh/view/ReporteIPS.fxml");
    }
    
    // Manejadores de eventos del menú Reportes
    
    @FXML
    public void handleReporteNomina(ActionEvent event) {
        abrirNuevaTab("Nómina General", "/com/sisrh/view/ReporteNomina.fxml");
    }
    
    @FXML
    public void handleReporteContratos(ActionEvent event) {
        abrirNuevaTab("Contratos Activos", "/com/sisrh/view/ReporteContratos.fxml");
    }
    
    @FXML
    public void handleReporteVencimientos(ActionEvent event) {
        abrirNuevaTab("Vencimientos de Contratos", "/com/sisrh/view/ReporteVencimientos.fxml");
    }
    
    // Manejadores de eventos del menú Administración
    
    @FXML
    public void handleGestionUsuarios(ActionEvent event) {
        abrirNuevaTab("Gestión de Usuarios", "/com/sisrh/view/UsuariosList.fxml");
    }
    
    @FXML
    public void handleGestionCargos(ActionEvent event) {
        abrirNuevaTab("Gestión de Cargos y Categorías", "/com/sisrh/view/CargosCategoriasView.fxml");
    }
    
    @FXML
    public void handleConfiguracionSistema(ActionEvent event) {
        abrirNuevaTab("Configuración del Sistema", "/com/sisrh/view/ConfiguracionSistema.fxml");
    }
    
    // Manejadores de eventos del menú Ayuda
    
    @FXML
    public void handleAcercaDe(ActionEvent event) {
        mostrarInfo("Acerca de SISRH", 
                "SISRH - Sistema Integral de Salarios y Recursos Humanos\n\n" +
                "Versión 1.0\n" +
                "© 2023 Todos los derechos reservados\n\n" +
                "Sistema de gestión de empleados y cálculo de salarios\n" +
                "conforme a la normativa vigente del IPS.");
    }
}