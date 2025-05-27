package com.sisrh.controller;

import java.time.LocalDate;
import java.util.List;

import com.sisrh.model.Contrato;
import com.sisrh.model.Contrato.TipoContrato;
import com.sisrh.model.Empleado;
import com.sisrh.service.EmpleadoService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controlador para el formulario de gestión de contratos
 */
public class ContratoFormController {
    
    @FXML
    private ComboBox<Empleado> cboEmpleado;
    
    @FXML
    private ComboBox<TipoContrato> cboTipoContrato;
    
    @FXML
    private DatePicker dpFechaInicio;
    
    @FXML
    private DatePicker dpFechaFin;
    
    @FXML
    private CheckBox chkRenovacionAutomatica;
    
    @FXML
    private TextArea txtObservaciones;
    
    @FXML
    private Button btnGuardar;
    
    @FXML
    private Button btnCancelar;
    
    private EmpleadoService empleadoService;
    private Contrato contrato;
    private boolean esNuevoContrato = true;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        empleadoService = new EmpleadoService();
        
        // Cargar los empleados en el combo
        List<Empleado> empleados = empleadoService.listarTodos();
        cboEmpleado.setItems(FXCollections.observableArrayList(empleados));
        cboEmpleado.setConverter(new EmpleadoStringConverter());
        
        // Cargar los tipos de contrato en el combo
        cboTipoContrato.setItems(FXCollections.observableArrayList(TipoContrato.values()));
        
        // Establecer la fecha actual como fecha de inicio por defecto
        dpFechaInicio.setValue(LocalDate.now());
        
        // Configurar listener para el tipo de contrato
        cboTipoContrato.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == TipoContrato.INDEFINIDO) {
                // Si es contrato indefinido, deshabilitar fecha fin
                dpFechaFin.setValue(null);
                dpFechaFin.setDisable(true);
                chkRenovacionAutomatica.setSelected(false);
                chkRenovacionAutomatica.setDisable(true);
            } else {
                // Si no es indefinido, habilitar fecha fin y renovación
                dpFechaFin.setDisable(false);
                chkRenovacionAutomatica.setDisable(false);
                
                // Si es un contrato nuevo, establecer fecha fin por defecto a 6 meses
                if (esNuevoContrato && dpFechaInicio.getValue() != null) {
                    dpFechaFin.setValue(dpFechaInicio.getValue().plusMonths(6));
                }
            }
        });
    }
    
    /**
     * Establece el contrato a editar
     * @param contrato el contrato a editar
     */
    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
        this.esNuevoContrato = false;
        
        // Cargar los datos del contrato en el formulario
        cboEmpleado.setValue(contrato.getEmpleado());
        cboEmpleado.setDisable(true); // No permitir cambiar el empleado en edición
        
        cboTipoContrato.setValue(contrato.getTipo());
        dpFechaInicio.setValue(contrato.getFechaInicio());
        dpFechaFin.setValue(contrato.getFechaFin());
        chkRenovacionAutomatica.setSelected(contrato.isRenovacionAutomatica());
        txtObservaciones.setText(contrato.getObservaciones());
        
        // Actualizar el título del botón
        btnGuardar.setText("Actualizar");
    }
    
    /**
     * Maneja el evento de guardar el contrato
     * @param event el evento
     */
    @FXML
    public void handleGuardar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }
        
        try {
            // Obtener los datos del formulario
            Empleado empleado = cboEmpleado.getValue();
            TipoContrato tipo = cboTipoContrato.getValue();
            LocalDate fechaInicio = dpFechaInicio.getValue();
            LocalDate fechaFin = dpFechaFin.getValue();
            boolean renovacionAutomatica = chkRenovacionAutomatica.isSelected();
            String observaciones = txtObservaciones.getText();
            
            if (esNuevoContrato) {
                // Crear un nuevo contrato
                contrato = new Contrato(empleado, tipo, fechaInicio);
                contrato.setFechaFin(fechaFin);
                contrato.setRenovacionAutomatica(renovacionAutomatica);
                contrato.setObservaciones(observaciones);
                
                // Registrar el contrato
                empleadoService.registrarContrato(empleado, contrato);
                
                mostrarMensaje("Éxito", "Contrato registrado", "El contrato ha sido registrado exitosamente.", AlertType.INFORMATION);
            } else {
                // Actualizar el contrato existente
                contrato.setTipo(tipo);
                contrato.setFechaInicio(fechaInicio);
                contrato.setFechaFin(fechaFin);
                contrato.setRenovacionAutomatica(renovacionAutomatica);
                contrato.setObservaciones(observaciones);
                
                // Guardar los cambios
                empleadoService.actualizarEmpleado(contrato.getEmpleado());
                
                mostrarMensaje("Éxito", "Contrato actualizado", "El contrato ha sido actualizado exitosamente.", AlertType.INFORMATION);
            }
            
            // Cerrar la ventana
            cerrarVentana();
            
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al guardar", "Ha ocurrido un error al guardar el contrato: " + e.getMessage(), AlertType.ERROR);
        }
    }
    
    /**
     * Maneja el evento de cancelar
     * @param event el evento
     */
    @FXML
    public void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    /**
     * Valida que los campos del formulario estén correctamente completados
     * @return true si el formulario es válido, false en caso contrario
     */
    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();
        
        if (cboEmpleado.getValue() == null) {
            errores.append("- Debe seleccionar un empleado\n");
        }
        
        if (cboTipoContrato.getValue() == null) {
            errores.append("- Debe seleccionar un tipo de contrato\n");
        }
        
        if (dpFechaInicio.getValue() == null) {
            errores.append("- Debe seleccionar una fecha de inicio\n");
        }
        
        if (cboTipoContrato.getValue() != TipoContrato.INDEFINIDO && dpFechaFin.getValue() == null) {
            errores.append("- Debe seleccionar una fecha de fin para contratos no indefinidos\n");
        }
        
        if (dpFechaInicio.getValue() != null && dpFechaFin.getValue() != null && 
            dpFechaInicio.getValue().isAfter(dpFechaFin.getValue())) {
            errores.append("- La fecha de inicio no puede ser posterior a la fecha de fin\n");
        }
        
        if (errores.length() > 0) {
            mostrarMensaje("Error de validación", "Por favor corrija los siguientes errores:", 
                    errores.toString(), AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Cierra la ventana actual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Muestra un mensaje al usuario
     * @param titulo el título de la ventana
     * @param header el encabezado del mensaje
     * @param contenido el contenido del mensaje
     * @param tipo el tipo de alerta
     */
    private void mostrarMensaje(String titulo, String header, String contenido, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    
    /**
     * Clase para convertir Empleado a String y viceversa en el ComboBox
     */
    private class EmpleadoStringConverter extends javafx.util.StringConverter<Empleado> {
        @Override
        public String toString(Empleado empleado) {
            if (empleado == null) {
                return null;
            }
            return empleado.getNombre() + " " + empleado.getApellido();
        }
        
        @Override
        public Empleado fromString(String string) {
            // No es necesario implementar esta conversión para este caso
            return null;
        }
    }
}