package com.sisrh.controller;

import java.time.format.DateTimeFormatter;

import com.sisrh.controller.EmpleadoFormController;
import com.sisrh.controller.PrincipalController;
import com.sisrh.model.Empleado;
import com.sisrh.service.EmpleadoService;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controlador para la vista de listado de empleados
 */
public class EmpleadosListController {
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private TableView<Empleado> tblEmpleados;
    
    @FXML
    private TableColumn<Empleado, String> colCedula;
    
    @FXML
    private TableColumn<Empleado, String> colNombre;
    
    @FXML
    private TableColumn<Empleado, String> colApellido;
    
    @FXML
    private TableColumn<Empleado, String> colCargo;
    
    @FXML
    private TableColumn<Empleado, String> colFechaIngreso;
    
    @FXML
    private TableColumn<Empleado, String> colActivo;
    
    private EmpleadoService empleadoService;
    private ObservableList<Empleado> listaEmpleados;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        empleadoService = new EmpleadoService();
        
        // Configurar las columnas de la tabla
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        
        // Para el cargo, necesitamos un valor personalizado
        colCargo.setCellValueFactory(cellData -> {
            Empleado empleado = cellData.getValue();
            String cargoNombre = empleado.getCargoActual() != null ? 
                    empleado.getCargoActual().getNombre() : "Sin cargo";
            return new SimpleStringProperty(cargoNombre);
        });
        
        // Para la fecha de ingreso, formateamos la fecha
        colFechaIngreso.setCellValueFactory(cellData -> {
            Empleado empleado = cellData.getValue();
            String fechaIngreso = empleado.getContratoActual() != null ? 
                    empleado.getContratoActual().getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                    "Sin contrato";
            return new SimpleStringProperty(fechaIngreso);
        });
        
        // Para el estado activo
        colActivo.setCellValueFactory(cellData -> {
            Empleado empleado = cellData.getValue();
            return new SimpleStringProperty(empleado.isActivo() ? "Sí" : "No");
        });
        
        // Cargar los datos
        cargarEmpleados();
    }
    
    /**
     * Carga todos los empleados en la tabla
     */
    private void cargarEmpleados() {
        try {
            listaEmpleados = FXCollections.observableArrayList(empleadoService.listarTodos());
            tblEmpleados.setItems(listaEmpleados);
        } catch (Exception e) {
            mostrarError("Error al cargar empleados", e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de búsqueda
     */
    @FXML
    public void handleBuscar(ActionEvent event) {
        String criterio = txtBuscar.getText().trim();
        
        if (criterio.isEmpty()) {
            cargarEmpleados();
            return;
        }
        
        try {
            // Buscar por nombre, apellido o cédula
            listaEmpleados = FXCollections.observableArrayList(
                    empleadoService.buscarPorNombreApellidoOCedula(criterio));
            tblEmpleados.setItems(listaEmpleados);
        } catch (Exception e) {
            mostrarError("Error al buscar empleados", e.getMessage());
        }
    }
    
    /**
     * Limpia el campo de búsqueda y recarga todos los empleados
     */
    @FXML
    public void handleLimpiarBusqueda(ActionEvent event) {
        txtBuscar.clear();
        cargarEmpleados();
    }
    
    /**
     * Abre el formulario para crear un nuevo empleado
     */
    @FXML
    public void handleNuevoEmpleado(ActionEvent event) {
        try {
            // Obtener referencia al PrincipalController desde la ventana principal
            Stage stage = (Stage) tblEmpleados.getScene().getWindow();
            PrincipalController principalController = (PrincipalController) stage.getUserData();
            if (principalController != null) {
                principalController.abrirNuevaTab("Nuevo Empleado", "/com/sisrh/view/EmpleadoForm.fxml");
            }
        } catch (Exception e) {
            mostrarError("Error al abrir formulario", e.getMessage());
        }
    }
    
    /**
     * Abre el formulario para editar el empleado seleccionado
     */
    @FXML
    public void handleEditarEmpleado(ActionEvent event) {
        Empleado empleadoSeleccionado = tblEmpleados.getSelectionModel().getSelectedItem();
        
        if (empleadoSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un empleado para editar");
            return;
        }
        
        try {
            // Obtener referencia al PrincipalController desde la ventana principal
            Stage stage = (Stage) tblEmpleados.getScene().getWindow();
            PrincipalController principalController = (PrincipalController) stage.getUserData();
            if (principalController != null) {
                Object controller = principalController.abrirNuevaTab("Editar Empleado", "/com/sisrh/view/EmpleadoForm.fxml");
                if (controller instanceof EmpleadoFormController) {
                    EmpleadoFormController formController = (EmpleadoFormController) controller;
                    formController.setEmpleado(empleadoSeleccionado);
                }
            }
        } catch (Exception e) {
            mostrarError("Error al editar empleado", e.getMessage());
        }
    }
    
    /**
     * Muestra el historial laboral del empleado seleccionado
     */
    @FXML
    public void handleVerHistorial(ActionEvent event) {
        Empleado empleadoSeleccionado = tblEmpleados.getSelectionModel().getSelectedItem();
        
        if (empleadoSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un empleado para ver su historial");
            return;
        }
        
        // Por implementar: Mostrar historial laboral
        mostrarInfo("Historial Laboral", "Funcionalidad por implementar");
    }
    
    /**
     * Desactiva el empleado seleccionado
     */
    @FXML
    public void handleDesactivarEmpleado(ActionEvent event) {
        Empleado empleadoSeleccionado = tblEmpleados.getSelectionModel().getSelectedItem();
        
        if (empleadoSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un empleado para desactivar");
            return;
        }
        
        if (!empleadoSeleccionado.isActivo()) {
            mostrarError("Error", "El empleado ya está desactivado");
            return;
        }
        
        // Confirmar desactivación
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Desactivación");
        alert.setHeaderText("¿Está seguro que desea desactivar al empleado?");
        alert.setContentText("Empleado: " + empleadoSeleccionado.getNombreCompleto());
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                empleadoService.desactivarEmpleado(empleadoSeleccionado);
                mostrarInfo("Empleado Desactivado", "El empleado ha sido desactivado correctamente");
                cargarEmpleados(); // Recargar la lista
            } catch (Exception e) {
                mostrarError("Error al desactivar empleado", e.getMessage());
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