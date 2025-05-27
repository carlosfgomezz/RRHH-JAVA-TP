package com.sisrh.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.sisrh.controller.ContratoFormController;
import com.sisrh.model.Contrato;
import com.sisrh.model.Contrato.TipoContrato;
import com.sisrh.model.Empleado;
import com.sisrh.service.EmpleadoService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador para la vista de lista de contratos
 */
public class ContratosListController {
    
    @FXML
    private ComboBox<Empleado> cboFiltroEmpleado;
    
    @FXML
    private ComboBox<TipoContrato> cboFiltroTipo;
    
    @FXML
    private ComboBox<String> cboFiltroEstado;
    
    @FXML
    private TextField txtBuscar;
    
    @FXML
    private TableView<Contrato> tblContratos;
    
    @FXML
    private TableColumn<Contrato, String> colEmpleado;
    
    @FXML
    private TableColumn<Contrato, String> colTipo;
    
    @FXML
    private TableColumn<Contrato, String> colFechaInicio;
    
    @FXML
    private TableColumn<Contrato, String> colFechaFin;
    
    @FXML
    private TableColumn<Contrato, String> colRenovacion;
    
    @FXML
    private TableColumn<Contrato, String> colEstado;
    
    private EmpleadoService empleadoService;
    private ObservableList<Contrato> listaContratos;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        empleadoService = new EmpleadoService();
        
        // Configurar las columnas de la tabla
        configurarColumnas();
        
        // Configurar los combos de filtro
        configurarFiltros();
        
        // Cargar los datos
        cargarContratos();
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarColumnas() {
        // Columna de empleado
        colEmpleado.setCellValueFactory(cellData -> {
            Contrato contrato = cellData.getValue();
            String nombreCompleto = contrato.getEmpleado().getNombre() + " " + contrato.getEmpleado().getApellido();
            return new SimpleStringProperty(nombreCompleto);
        });
        
        // Columna de tipo de contrato
        colTipo.setCellValueFactory(cellData -> {
            Contrato contrato = cellData.getValue();
            return new SimpleStringProperty(contrato.getTipo().toString());
        });
        
        // Columna de fecha de inicio
        colFechaInicio.setCellValueFactory(cellData -> {
            Contrato contrato = cellData.getValue();
            String fechaInicio = contrato.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new SimpleStringProperty(fechaInicio);
        });
        
        // Columna de fecha de fin
        colFechaFin.setCellValueFactory(cellData -> {
            Contrato contrato = cellData.getValue();
            if (contrato.getFechaFin() == null) {
                return new SimpleStringProperty("N/A");
            } else {
                String fechaFin = contrato.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return new SimpleStringProperty(fechaFin);
            }
        });
        
        // Columna de renovación automática
        colRenovacion.setCellValueFactory(cellData -> {
            Contrato contrato = cellData.getValue();
            return new SimpleStringProperty(contrato.isRenovacionAutomatica() ? "Sí" : "No");
        });
        
        // Columna de estado
        colEstado.setCellValueFactory(cellData -> {
            Contrato contrato = cellData.getValue();
            if (contrato.isActivo()) {
                return new SimpleStringProperty("Activo");
            } else {
                return new SimpleStringProperty("Inactivo");
            }
        });
    }
    
    /**
     * Configura los combos de filtro
     */
    private void configurarFiltros() {
        // Filtro de empleados
        List<Empleado> empleados = empleadoService.listarTodos();
        cboFiltroEmpleado.setItems(FXCollections.observableArrayList(empleados));
        cboFiltroEmpleado.setConverter(new EmpleadoStringConverter());
        
        // Filtro de tipos de contrato
        cboFiltroTipo.setItems(FXCollections.observableArrayList(TipoContrato.values()));
        
        // Filtro de estado
        cboFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cboFiltroEstado.setValue("Todos");
    }
    
    /**
     * Carga los contratos en la tabla
     */
    private void cargarContratos() {
        List<Empleado> empleados = empleadoService.listarTodos();
        List<Contrato> contratos = empleados.stream()
                .flatMap(e -> e.getContratos().stream())
                .collect(Collectors.toList());
        
        listaContratos = FXCollections.observableArrayList(contratos);
        tblContratos.setItems(listaContratos);
    }
    
    /**
     * Maneja el evento de buscar contratos
     */
    @FXML
    public void handleBuscar(ActionEvent event) {
        // Obtener los filtros seleccionados
        Empleado empleadoSeleccionado = cboFiltroEmpleado.getValue();
        TipoContrato tipoSeleccionado = cboFiltroTipo.getValue();
        String estadoSeleccionado = cboFiltroEstado.getValue();
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        
        // Obtener todos los contratos
        List<Empleado> empleados = empleadoService.listarTodos();
        List<Contrato> contratos = empleados.stream()
                .flatMap(e -> e.getContratos().stream())
                .collect(Collectors.toList());
        
        // Aplicar filtros
        if (empleadoSeleccionado != null) {
            contratos = contratos.stream()
                    .filter(c -> c.getEmpleado().getId().equals(empleadoSeleccionado.getId()))
                    .collect(Collectors.toList());
        }
        
        if (tipoSeleccionado != null) {
            contratos = contratos.stream()
                    .filter(c -> c.getTipo() == tipoSeleccionado)
                    .collect(Collectors.toList());
        }
        
        if (estadoSeleccionado != null && !estadoSeleccionado.equals("Todos")) {
            boolean activo = estadoSeleccionado.equals("Activos");
            contratos = contratos.stream()
                    .filter(c -> c.isActivo() == activo)
                    .collect(Collectors.toList());
        }
        
        if (!textoBusqueda.isEmpty()) {
            contratos = contratos.stream()
                    .filter(c -> {
                        String nombreCompleto = c.getEmpleado().getNombre() + " " + c.getEmpleado().getApellido();
                        return nombreCompleto.toLowerCase().contains(textoBusqueda) ||
                               c.getTipo().toString().toLowerCase().contains(textoBusqueda) ||
                               (c.getObservaciones() != null && c.getObservaciones().toLowerCase().contains(textoBusqueda));
                    })
                    .collect(Collectors.toList());
        }
        
        // Actualizar la tabla
        listaContratos = FXCollections.observableArrayList(contratos);
        tblContratos.setItems(listaContratos);
    }
    
    /**
     * Maneja el evento de crear un nuevo contrato
     */
    @FXML
    public void handleNuevoContrato(ActionEvent event) {
        try {
            // Cargar la vista del formulario de contrato
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/ContratoForm.fxml"));
            BorderPane root = loader.load();
            
            // Configurar el controlador
            ContratoFormController controller = loader.getController();
            
            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Nuevo Contrato");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            
            // Mostrar la ventana y esperar a que se cierre
            stage.showAndWait();
            
            // Recargar los contratos
            cargarContratos();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el formulario de contrato: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de editar un contrato
     */
    @FXML
    public void handleEditarContrato(ActionEvent event) {
        Contrato contratoSeleccionado = tblContratos.getSelectionModel().getSelectedItem();
        if (contratoSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un contrato para editar");
            return;
        }
        
        try {
            // Cargar la vista del formulario de contrato
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/ContratoForm.fxml"));
            BorderPane root = loader.load();
            
            // Configurar el controlador
            ContratoFormController controller = loader.getController();
            controller.setContrato(contratoSeleccionado);
            
            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Editar Contrato");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            
            // Mostrar la ventana y esperar a que se cierre
            stage.showAndWait();
            
            // Recargar los contratos
            cargarContratos();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el formulario de contrato: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de renovar un contrato
     */
    @FXML
    public void handleRenovarContrato(ActionEvent event) {
        Contrato contratoSeleccionado = tblContratos.getSelectionModel().getSelectedItem();
        if (contratoSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un contrato para renovar");
            return;
        }
        
        if (!contratoSeleccionado.isActivo()) {
            mostrarError("Error", "Solo se pueden renovar contratos activos");
            return;
        }
        
        if (contratoSeleccionado.getTipo() == TipoContrato.INDEFINIDO) {
            mostrarError("Error", "Los contratos indefinidos no requieren renovación");
            return;
        }
        
        try {
            // Cargar la vista del formulario de renovación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/ContratoRenovacionForm.fxml"));
            BorderPane root = loader.load();
            
            // Configurar el controlador
            ContratoRenovacionFormController controller = loader.getController();
            controller.setContrato(contratoSeleccionado);
            
            // Crear una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Renovar Contrato");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            
            // Mostrar la ventana y esperar a que se cierre
            stage.showAndWait();
            
            // Recargar los contratos
            cargarContratos();
            
        } catch (Exception e) {
            mostrarError("Error", "Error al abrir el formulario de renovación: " + e.getMessage());
        }
    }
    
    /**
     * Maneja el evento de finalizar un contrato
     */
    @FXML
    public void handleFinalizarContrato(ActionEvent event) {
        Contrato contratoSeleccionado = tblContratos.getSelectionModel().getSelectedItem();
        if (contratoSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un contrato para finalizar");
            return;
        }
        
        if (!contratoSeleccionado.isActivo()) {
            mostrarError("Error", "El contrato ya está finalizado");
            return;
        }
        
        // Confirmar la finalización
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Finalización");
        alert.setHeaderText("Finalizar Contrato");
        alert.setContentText("¿Está seguro que desea finalizar el contrato seleccionado?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    // Finalizar el contrato
                    contratoSeleccionado.finalizar();
                    
                    // Guardar los cambios
                    Empleado empleado = contratoSeleccionado.getEmpleado();
                    empleadoService.actualizarEmpleado(empleado);
                    
                    // Recargar los contratos
                    cargarContratos();
                    
                    // Mostrar mensaje de éxito
                    mostrarInfo("Contrato Finalizado", "El contrato ha sido finalizado correctamente");
                    
                } catch (Exception e) {
                    mostrarError("Error", "Error al finalizar el contrato: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Maneja el evento de exportar a PDF
     */
    @FXML
    public void handleExportarPDF(ActionEvent event) {
        // Implementar la exportación a PDF
        mostrarInfo("Exportar a PDF", "Funcionalidad de exportación a PDF no implementada");
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
    
    /**
     * Clase para convertir un Empleado a String y viceversa en el ComboBox
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