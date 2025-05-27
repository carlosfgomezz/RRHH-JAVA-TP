package com.sisrh.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sisrh.model.Empleado;
import com.sisrh.model.Salario;
import com.sisrh.service.EmpleadoService;
import com.sisrh.service.SalarioService;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;

/**
 * Controlador para la vista de generación de salarios
 */
public class GenerarSalariosController {
    
    @FXML
    private ComboBox<String> cboMes;
    
    @FXML
    private ComboBox<Integer> cboAnio;
    
    @FXML
    private RadioButton rbTodos;
    
    @FXML
    private RadioButton rbIndividual;
    
    @FXML
    private ToggleGroup tipoGeneracion;
    
    @FXML
    private ComboBox<Empleado> cboEmpleado;
    
    @FXML
    private TableView<Salario> tblSalarios;
    
    @FXML
    private TableColumn<Salario, String> colEmpleado;
    
    @FXML
    private TableColumn<Salario, String> colCargo;
    
    @FXML
    private TableColumn<Salario, String> colSalarioBruto;
    
    @FXML
    private TableColumn<Salario, String> colAporteIPS;
    
    @FXML
    private TableColumn<Salario, String> colSalarioNeto;
    
    private SalarioService salarioService;
    private EmpleadoService empleadoService;
    private ObservableList<Salario> listaSalarios;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        salarioService = new SalarioService();
        empleadoService = new EmpleadoService();
        
        // Configurar las columnas de la tabla
        configurarColumnas();
        
        // Configurar los combos
        configurarCombos();
        
        // Configurar los radio buttons
        configurarRadioButtons();
        
        // Inicializar la lista de salarios
        listaSalarios = FXCollections.observableArrayList();
        tblSalarios.setItems(listaSalarios);
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarColumnas() {
        // Columna de empleado
        colEmpleado.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            Empleado empleado = salario.getEmpleado();
            return new SimpleStringProperty(empleado.getNombreCompleto());
        });
        
        // Columna de cargo
        colCargo.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            Empleado empleado = salario.getEmpleado();
            String cargo = empleado.getCargoActual() != null ? 
                    empleado.getCargoActual().getNombre() : "Sin cargo";
            return new SimpleStringProperty(cargo);
        });
        
        // Columna de salario bruto
        colSalarioBruto.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            return new SimpleStringProperty(String.format("%,.0f Gs.", salario.calcularSalarioBruto()));
        });
        
        // Columna de aporte IPS
        colAporteIPS.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            return new SimpleStringProperty(String.format("%,.0f Gs.", salario.getAportePersonalIPS()));
        });
        
        // Columna de salario neto
        colSalarioNeto.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            return new SimpleStringProperty(String.format("%,.0f Gs.", salario.calcularSalarioNeto()));
        });
    }
    
    /**
     * Configura los combos de mes, año y empleado
     */
    private void configurarCombos() {
        // Configurar combo de meses
        ObservableList<String> meses = FXCollections.observableArrayList();
        for (Month mes : Month.values()) {
            meses.add(mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
        }
        cboMes.setItems(meses);
        cboMes.setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
        
        // Configurar combo de años
        ObservableList<Integer> anios = FXCollections.observableArrayList();
        int anioActual = java.time.Year.now().getValue();
        for (int i = anioActual - 2; i <= anioActual + 1; i++) {
            anios.add(i);
        }
        cboAnio.setItems(anios);
        cboAnio.setValue(anioActual);
        
        // Configurar combo de empleados
        try {
            ObservableList<Empleado> empleados = FXCollections.observableArrayList(
                    empleadoService.listarActivos());
            cboEmpleado.setItems(empleados);
        } catch (Exception e) {
            mostrarError("Error al cargar empleados", e.getMessage());
        }
    }
    
    /**
     * Configura los radio buttons para seleccionar el tipo de generación
     */
    private void configurarRadioButtons() {
        // Habilitar/deshabilitar combo de empleado según selección
        rbIndividual.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                cboEmpleado.setDisable(!newValue);
            }
        });
    }
    
    /**
     * Genera los salarios según los criterios seleccionados
     */
    @FXML
    public void handleGenerarSalarios(ActionEvent event) {
        // Validar selección de periodo
        if (cboMes.getValue() == null || cboAnio.getValue() == null) {
            mostrarError("Error", "Debe seleccionar el mes y año para generar los salarios");
            return;
        }
        
        // Validar selección de empleado si es generación individual
        if (rbIndividual.isSelected() && cboEmpleado.getValue() == null) {
            mostrarError("Error", "Debe seleccionar un empleado para generar el salario individual");
            return;
        }
        
        try {
            // Obtener el periodo seleccionado
            int mes = cboMes.getSelectionModel().getSelectedIndex() + 1; // Meses en Month son 1-based
            int anio = cboAnio.getValue();
            YearMonth periodo = YearMonth.of(anio, mes);
            
            // Generar salarios según el tipo seleccionado
            if (rbTodos.isSelected()) {
                // Generar salarios para todos los empleados activos
                List<Salario> salariosGenerados = salarioService.generarSalariosMasivos(periodo, empleadoService);
                listaSalarios.setAll(salariosGenerados);
                
                mostrarInfo("Salarios Generados", 
                        "Se han generado " + salariosGenerados.size() + " salarios para el periodo " + 
                        periodo.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            } else {
                // Generar salario para un empleado específico
                Empleado empleado = cboEmpleado.getValue();
                Salario salarioGenerado = salarioService.generarSalario(empleado, periodo);
                
                listaSalarios.clear();
                listaSalarios.add(salarioGenerado);
                
                mostrarInfo("Salario Generado", 
                        "Se ha generado el salario para " + empleado.getNombreCompleto() + " para el periodo " + 
                        periodo.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            }
        } catch (Exception e) {
            mostrarError("Error al generar salarios", e.getMessage());
        }
    }
    
    /**
     * Verifica si ya existen salarios para el periodo seleccionado
     */
    @FXML
    public void handleVerificarExistentes(ActionEvent event) {
        // Validar selección de periodo
        if (cboMes.getValue() == null || cboAnio.getValue() == null) {
            mostrarError("Error", "Debe seleccionar el mes y año para verificar los salarios existentes");
            return;
        }
        
        try {
            // Obtener el periodo seleccionado
            int mes = cboMes.getSelectionModel().getSelectedIndex() + 1;
            int anio = cboAnio.getValue();
            YearMonth periodo = YearMonth.of(anio, mes);
            
            // Verificar salarios existentes
            List<Salario> salariosExistentes = salarioService.listarPorPeriodo(periodo);
            
            if (salariosExistentes.isEmpty()) {
                mostrarInfo("Verificación", "No existen salarios generados para el periodo " + 
                        periodo.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            } else {
                listaSalarios.setAll(salariosExistentes);
                mostrarInfo("Verificación", "Existen " + salariosExistentes.size() + " salarios generados para el periodo " + 
                        periodo.format(DateTimeFormatter.ofPattern("MM/yyyy")));
            }
        } catch (Exception e) {
            mostrarError("Error al verificar salarios existentes", e.getMessage());
        }
    }
    
    /**
     * Exporta a PDF los salarios generados
     */
    @FXML
    public void handleExportarPDF(ActionEvent event) {
        if (listaSalarios.isEmpty()) {
            mostrarError("Error", "No hay salarios generados para exportar");
            return;
        }
        
        // Por implementar: Exportar a PDF
        mostrarInfo("Exportar a PDF", "Funcionalidad por implementar");
    }
    
    /**
     * Exporta a Excel los salarios generados
     */
    @FXML
    public void handleExportarExcel(ActionEvent event) {
        if (listaSalarios.isEmpty()) {
            mostrarError("Error", "No hay salarios generados para exportar");
            return;
        }
        
        // Por implementar: Exportar a Excel
        mostrarInfo("Exportar a Excel", "Funcionalidad por implementar");
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