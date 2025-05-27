package com.sisrh.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sisrh.model.Empleado;
import com.sisrh.model.Salario;
import com.sisrh.service.SalarioService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Controlador para la vista de listado de salarios
 */
public class SalariosListController {
    
    @FXML
    private ComboBox<String> cboPeriodo;
    
    @FXML
    private ComboBox<String> cboEstado;
    
    @FXML
    private TableView<Salario> tblSalarios;
    
    @FXML
    private TableColumn<Salario, String> colPeriodo;
    
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
    
    @FXML
    private TableColumn<Salario, String> colPagado;
    
    private SalarioService salarioService;
    private ObservableList<Salario> listaSalarios;
    
    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        salarioService = new SalarioService();
        
        // Configurar las columnas de la tabla
        configurarColumnas();
        
        // Configurar los combos de filtro
        configurarFiltros();
        
        // Cargar los datos
        cargarSalarios();
    }
    
    /**
     * Configura las columnas de la tabla
     */
    private void configurarColumnas() {
        // Columna de periodo
        colPeriodo.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            String periodo = salario.getPeriodo().format(DateTimeFormatter.ofPattern("MM/yyyy"));
            return new SimpleStringProperty(periodo);
        });
        
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
        
        // Columna de pagado
        colPagado.setCellValueFactory(cellData -> {
            Salario salario = cellData.getValue();
            return new SimpleStringProperty(salario.isPagado() ? "Sí" : "No");
        });
    }
    
    /**
     * Configura los combos de filtro
     */
    private void configurarFiltros() {
        // Configurar combo de periodos
        ObservableList<String> periodos = FXCollections.observableArrayList();
        periodos.add("Todos");
        
        // Obtener los últimos 12 periodos
        YearMonth periodoActual = YearMonth.now();
        for (int i = 0; i < 12; i++) {
            YearMonth periodo = periodoActual.minusMonths(i);
            periodos.add(periodo.format(DateTimeFormatter.ofPattern("MM/yyyy")));
        }
        
        cboPeriodo.setItems(periodos);
        cboPeriodo.setValue("Todos");
        
        // Configurar combo de estados
        ObservableList<String> estados = FXCollections.observableArrayList(
                "Todos", "Pagados", "Pendientes");
        cboEstado.setItems(estados);
        cboEstado.setValue("Todos");
    }
    
    /**
     * Carga todos los salarios en la tabla
     */
    private void cargarSalarios() {
        try {
            listaSalarios = FXCollections.observableArrayList(salarioService.listarTodos());
            tblSalarios.setItems(listaSalarios);
        } catch (Exception e) {
            mostrarError("Error al cargar salarios", e.getMessage());
        }
    }
    
    /**
     * Filtra los salarios según los criterios seleccionados
     */
    @FXML
    public void handleFiltrar(ActionEvent event) {
        String periodoSeleccionado = cboPeriodo.getValue();
        String estadoSeleccionado = cboEstado.getValue();
        
        try {
            List<Salario> salariosFiltrados;
            
            // Filtrar por periodo
            if ("Todos".equals(periodoSeleccionado)) {
                salariosFiltrados = salarioService.listarTodos();
            } else {
                // Convertir el string de periodo a YearMonth
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                YearMonth periodo = YearMonth.parse(periodoSeleccionado, formatter);
                salariosFiltrados = salarioService.listarPorPeriodo(periodo);
            }
            
            // Filtrar por estado
            if (!"Todos".equals(estadoSeleccionado)) {
                boolean pagado = "Pagados".equals(estadoSeleccionado);
                salariosFiltrados.removeIf(salario -> salario.isPagado() != pagado);
            }
            
            listaSalarios = FXCollections.observableArrayList(salariosFiltrados);
            tblSalarios.setItems(listaSalarios);
        } catch (Exception e) {
            mostrarError("Error al filtrar salarios", e.getMessage());
        }
    }
    
    /**
     * Limpia los filtros y recarga todos los salarios
     */
    @FXML
    public void handleLimpiarFiltros(ActionEvent event) {
        cboPeriodo.setValue("Todos");
        cboEstado.setValue("Todos");
        cargarSalarios();
    }
    
    /**
     * Muestra el detalle del salario seleccionado
     */
    @FXML
    public void handleVerDetalle(ActionEvent event) {
        Salario salarioSeleccionado = tblSalarios.getSelectionModel().getSelectedItem();
        
        if (salarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un salario para ver el detalle");
            return;
        }
        
        // Por implementar: Mostrar detalle del salario
        mostrarInfo("Detalle de Salario", 
                "Empleado: " + salarioSeleccionado.getEmpleado().getNombreCompleto() + "\n" +
                "Periodo: " + salarioSeleccionado.getPeriodo().format(DateTimeFormatter.ofPattern("MM/yyyy")) + "\n" +
                "Salario Bruto: " + String.format("%,.0f Gs.", salarioSeleccionado.calcularSalarioBruto()) + "\n" +
                "Aporte IPS Personal: " + String.format("%,.0f Gs.", salarioSeleccionado.getAportePersonalIPS()) + "\n" +
                "Aporte IPS Patronal: " + String.format("%,.0f Gs.", salarioSeleccionado.getAportePatronalIPS()) + "\n" +
                "Salario Neto: " + String.format("%,.0f Gs.", salarioSeleccionado.calcularSalarioNeto()) + "\n" +
                "Estado: " + (salarioSeleccionado.isPagado() ? "Pagado" : "Pendiente"));
    }
    
    /**
     * Marca como pagado el salario seleccionado
     */
    @FXML
    public void handlePagarSalario(ActionEvent event) {
        Salario salarioSeleccionado = tblSalarios.getSelectionModel().getSelectedItem();
        
        if (salarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un salario para pagar");
            return;
        }
        
        if (salarioSeleccionado.isPagado()) {
            mostrarError("Error", "El salario ya está pagado");
            return;
        }
        
        // Confirmar pago
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Pago");
        alert.setHeaderText("¿Está seguro que desea marcar este salario como pagado?");
        alert.setContentText("Empleado: " + salarioSeleccionado.getEmpleado().getNombreCompleto() + "\n" +
                "Periodo: " + salarioSeleccionado.getPeriodo().format(DateTimeFormatter.ofPattern("MM/yyyy")));
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                salarioService.pagarSalario(salarioSeleccionado);
                mostrarInfo("Salario Pagado", "El salario ha sido marcado como pagado correctamente");
                cargarSalarios(); // Recargar la lista
            } catch (Exception e) {
                mostrarError("Error al pagar salario", e.getMessage());
            }
        }
    }
    
    /**
     * Genera un recibo de salario para el salario seleccionado
     */
    @FXML
    public void handleGenerarRecibo(ActionEvent event) {
        Salario salarioSeleccionado = tblSalarios.getSelectionModel().getSelectedItem();
        
        if (salarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un salario para generar el recibo");
            return;
        }
        
        // Por implementar: Generar recibo de salario
        mostrarInfo("Generar Recibo", "Funcionalidad por implementar");
    }
    
    /**
     * Exporta a PDF el salario seleccionado
     */
    @FXML
    public void handleExportarPDF(ActionEvent event) {
        Salario salarioSeleccionado = tblSalarios.getSelectionModel().getSelectedItem();
        
        if (salarioSeleccionado == null) {
            mostrarError("Error", "Debe seleccionar un salario para exportar a PDF");
            return;
        }
        
        // Por implementar: Exportar a PDF
        mostrarInfo("Exportar a PDF", "Funcionalidad por implementar");
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