package com.sisrh.controller;

import java.time.LocalDate;

import com.sisrh.model.Cargo;
import com.sisrh.model.Contrato;
import com.sisrh.model.Empleado;
import com.sisrh.model.Contrato.TipoContrato;
import com.sisrh.service.EmpleadoService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Controlador para el formulario de empleados
 */
public class EmpleadoFormController {
    
    @FXML
    private TextField txtCedula;
    
    @FXML
    private TextField txtNombre;
    
    @FXML
    private TextField txtApellido;
    
    @FXML
    private DatePicker dpFechaNacimiento;
    
    @FXML
    private TextField txtDireccion;
    
    @FXML
    private TextField txtTelefono;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private ComboBox<String> cboEstadoCivil;
    
    @FXML
    private ComboBox<String> cboActivo;
    
    @FXML
    private ComboBox<Cargo> cboCargo;
    
    @FXML
    private DatePicker dpFechaIngreso;
    
    @FXML
    private ComboBox<TipoContrato> cboTipoContrato;
    
    @FXML
    private DatePicker dpFechaFinContrato;
    
    @FXML
    private TextField txtObservaciones;
    
    @FXML
    private Button btnCancelar;
    
    @FXML
    private Button btnGuardar;
    
    private EmpleadoService empleadoService;
    private Empleado empleado;
    private boolean modoEdicion = false;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        empleadoService = new EmpleadoService();
        
        // Inicializar combos
        cboEstadoCivil.setItems(FXCollections.observableArrayList(
                "Soltero/a", "Casado/a", "Divorciado/a", "Viudo/a"));
        
        cboActivo.setItems(FXCollections.observableArrayList("Sí", "No"));
        cboActivo.setValue("Sí");
        
        // Cargar cargos disponibles
        cargarCargos();
        
        // Cargar tipos de contrato
        cboTipoContrato.setItems(FXCollections.observableArrayList(TipoContrato.values()));
        cboTipoContrato.setValue(TipoContrato.INDEFINIDO);
        
        // Configurar fechas por defecto
        dpFechaNacimiento.setValue(LocalDate.now().minusYears(20));
        dpFechaIngreso.setValue(LocalDate.now());
        
        // Configurar validaciones
        configurarValidaciones();
        
        // Configurar comportamiento del tipo de contrato
        cboTipoContrato.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == TipoContrato.INDEFINIDO) {
                dpFechaFinContrato.setDisable(true);
                dpFechaFinContrato.setValue(null);
            } else {
                dpFechaFinContrato.setDisable(false);
                // Para contratos a plazo fijo, establecer fecha fin a 1 año por defecto
                if (newVal == TipoContrato.PLAZO_FIJO && dpFechaIngreso.getValue() != null) {
                    dpFechaFinContrato.setValue(dpFechaIngreso.getValue().plusYears(1));
                }
            }
        });
        
        // Inicializar empleado nuevo
        empleado = new Empleado();
    }
    
    /**
     * Carga los cargos disponibles en el combo
     */
    private void cargarCargos() {
        try {
            cboCargo.setItems(FXCollections.observableArrayList(
                    empleadoService.listarCargosActivos()));
        } catch (Exception e) {
            mostrarError("Error al cargar cargos", e.getMessage());
        }
    }
    
    /**
     * Configura las validaciones de los campos
     */
    private void configurarValidaciones() {
        // Validar que cédula solo contenga números
        txtCedula.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txtCedula.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
        
        // Validar que teléfono solo contenga números y guiones
        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[\\d-]*")) {
                txtTelefono.setText(newVal.replaceAll("[^\\d-]", ""));
            }
        });
    }
    
    /**
     * Establece el empleado para edición
     * @param empleado el empleado a editar
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
        this.modoEdicion = true;
        
        // Cargar datos del empleado en el formulario
        txtCedula.setText(empleado.getCedula());
        txtNombre.setText(empleado.getNombre());
        txtApellido.setText(empleado.getApellido());
        dpFechaNacimiento.setValue(empleado.getFechaNacimiento());
        txtDireccion.setText(empleado.getDireccion());
        txtTelefono.setText(empleado.getTelefono());
        txtEmail.setText(empleado.getEmail());
        cboEstadoCivil.setValue(empleado.getEstadoCivil());
        cboActivo.setValue(empleado.isActivo() ? "Sí" : "No");
        
        // Cargar datos del contrato actual si existe
        Contrato contratoActual = empleado.getContratoActual();
        if (contratoActual != null) {
            cboCargo.setValue(empleado.getCargoActual());
            dpFechaIngreso.setValue(contratoActual.getFechaInicio());
            cboTipoContrato.setValue(contratoActual.getTipo());
            dpFechaFinContrato.setValue(contratoActual.getFechaFin());
            txtObservaciones.setText(contratoActual.getObservaciones());
        }
        
        // Deshabilitar cédula en modo edición
        txtCedula.setDisable(true);
    }
    
    /**
     * Maneja el evento de guardar
     * @param event evento de acción
     */
    @FXML
    public void handleGuardar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            // Actualizar datos del empleado
            empleado.setCedula(txtCedula.getText());
            empleado.setNombre(txtNombre.getText());
            empleado.setApellido(txtApellido.getText());
            empleado.setFechaNacimiento(dpFechaNacimiento.getValue());
            empleado.setDireccion(txtDireccion.getText());
            empleado.setTelefono(txtTelefono.getText());
            empleado.setEmail(txtEmail.getText());
            empleado.setEstadoCivil(cboEstadoCivil.getValue());
            empleado.setActivo(cboActivo.getValue().equals("Sí"));
            
            // Guardar empleado
            if (modoEdicion) {
                empleadoService.actualizarEmpleado(empleado);
            } else {
                empleadoService.registrarEmpleado(empleado);
            }
            
            // Registrar contrato
            Cargo cargoSeleccionado = cboCargo.getValue();
            TipoContrato tipoContrato = cboTipoContrato.getValue();
            LocalDate fechaInicio = dpFechaIngreso.getValue();
            LocalDate fechaFin = dpFechaFinContrato.getValue();
            String observaciones = txtObservaciones.getText();
            
            empleadoService.registrarContrato(empleado, cargoSeleccionado, tipoContrato, 
                    fechaInicio, fechaFin, observaciones);
            
            // Mostrar mensaje de éxito
            mostrarInfo("Empleado guardado", 
                    "El empleado ha sido guardado correctamente.");
            
            // Limpiar formulario
            limpiarFormulario();
            
        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Maneja el evento de cancelar
     * @param event evento de acción
     */
    @FXML
    public void handleCancelar(ActionEvent event) {
        limpiarFormulario();
    }
    
    /**
     * Valida el formulario antes de guardar
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();
        
        if (txtCedula.getText().isEmpty()) {
            errores.append("- La cédula es obligatoria\n");
        }
        
        if (txtNombre.getText().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }
        
        if (txtApellido.getText().isEmpty()) {
            errores.append("- El apellido es obligatorio\n");
        }
        
        if (dpFechaNacimiento.getValue() == null) {
            errores.append("- La fecha de nacimiento es obligatoria\n");
        } else if (dpFechaNacimiento.getValue().isAfter(LocalDate.now().minusYears(18))) {
            errores.append("- El empleado debe ser mayor de edad\n");
        }
        
        if (cboCargo.getValue() == null) {
            errores.append("- Debe seleccionar un cargo\n");
        }
        
        if (dpFechaIngreso.getValue() == null) {
            errores.append("- La fecha de ingreso es obligatoria\n");
        }
        
        if (cboTipoContrato.getValue() != TipoContrato.INDEFINIDO && 
                dpFechaFinContrato.getValue() == null) {
            errores.append("- La fecha de fin de contrato es obligatoria para contratos a plazo fijo\n");
        }
        
        if (errores.length() > 0) {
            mostrarError("Errores de validación", 
                    "Por favor, corrija los siguientes errores:\n" + errores.toString());
            return false;
        }
        
        return true;
    }
    
    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        dpFechaNacimiento.setValue(LocalDate.now().minusYears(20));
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        cboEstadoCivil.setValue(null);
        cboActivo.setValue("Sí");
        cboCargo.setValue(null);
        dpFechaIngreso.setValue(LocalDate.now());
        cboTipoContrato.setValue(TipoContrato.INDEFINIDO);
        dpFechaFinContrato.setValue(null);
        txtObservaciones.setText("");
        
        // Resetear modo edición
        modoEdicion = false;
        empleado = new Empleado();
        txtCedula.setDisable(false);
    }
    
    /**
     * Muestra un diálogo de error
     * @param titulo título del diálogo
     * @param mensaje mensaje de error
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
     * @param titulo título del diálogo
     * @param mensaje mensaje informativo
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}