package com.sisrh.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.DatabaseManager;
import com.sisrh.dao.ConfiguracionDAO;
import com.sisrh.dao.SalarioDAO;
import com.sisrh.model.Configuracion;
import com.sisrh.model.Empleado;
import com.sisrh.model.Salario;

/**
 * Servicio para la gestión de salarios
 */
public class SalarioService {
    
    private SalarioDAO salarioDAO;
    private ConfiguracionDAO configuracionDAO;
    
    /**
     * Constructor por defecto
     */
    public SalarioService() {
        EntityManager em = DatabaseManager.getInstance().getEntityManager();
        this.salarioDAO = new SalarioDAO(em);
        this.configuracionDAO = new ConfiguracionDAO(em);
    }
    
    /**
     * Constructor que recibe DAOs específicos (útil para pruebas)
     * @param salarioDAO el DAO de salarios a utilizar
     * @param configuracionDAO el DAO de configuraciones a utilizar
     */
    public SalarioService(SalarioDAO salarioDAO, ConfiguracionDAO configuracionDAO) {
        this.salarioDAO = salarioDAO;
        this.configuracionDAO = configuracionDAO;
    }
    
    /**
     * Genera un nuevo salario para un empleado en un período específico
     * @param empleado el empleado
     * @param periodo el período (mes y año)
     * @return el salario generado
     */
    public Salario generarSalario(Empleado empleado, YearMonth periodo) {
        // Verificar que no exista ya un salario para este empleado y período
        Salario existente = salarioDAO.buscarPorEmpleadoYPeriodo(empleado, periodo);
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un salario para este empleado en el período " + periodo);
        }
        
        // Obtener el salario base del cargo del empleado
        double salarioBase = 0;
        if (empleado.getCargoActual() != null) {
            salarioBase = empleado.getCargoActual().getSalarioBase();
        } else {
            // Si no tiene cargo, usar el salario mínimo
            salarioBase = configuracionDAO.obtenerValorDecimal(
                    Configuracion.Claves.SALARIO_MINIMO, 2550423); // Valor por defecto
        }
        
        // Crear el nuevo salario
        Salario salario = new Salario(empleado, periodo, salarioBase);
        
        // Calcular aportes IPS
        salario.calcularAportePersonalIPS();
        salario.calcularAportePatronalIPS();
        
        // Guardar y retornar
        empleado.agregarSalario(salario);
        return salarioDAO.guardar(salario);
    }
    
    /**
     * Actualiza los datos de un salario
     * @param salario el salario con los datos actualizados
     * @return el salario actualizado
     */
    public Salario actualizarSalario(Salario salario) {
        // Verificar que el salario exista
        Salario existente = salarioDAO.buscarPorId(salario.getId());
        if (existente == null) {
            throw new IllegalArgumentException("El salario no existe");
        }
        
        // Verificar que no esté pagado
        if (existente.isPagado()) {
            throw new IllegalStateException("No se puede modificar un salario ya pagado");
        }
        
        // Recalcular aportes IPS
        salario.calcularAportePersonalIPS();
        salario.calcularAportePatronalIPS();
        
        return salarioDAO.guardar(salario);
    }
    
    /**
     * Registra el pago de un salario
     * @param salario el salario a pagar
     * @return el salario actualizado
     */
    public Salario pagarSalario(Salario salario) {
        // Verificar que el salario exista
        Salario existente = salarioDAO.buscarPorId(salario.getId());
        if (existente == null) {
            throw new IllegalArgumentException("El salario no existe");
        }
        
        // Verificar que no esté pagado
        if (existente.isPagado()) {
            throw new IllegalStateException("El salario ya fue pagado");
        }
        
        // Marcar como pagado y establecer fecha de pago
        salario.setPagado(true);
        salario.setFechaPago(LocalDate.now());
        
        return salarioDAO.guardar(salario);
    }
    
    /**
     * Genera los salarios para todos los empleados activos en un período
     * @param periodo el período (mes y año)
     * @param empleadoService servicio de empleados para obtener la lista de activos
     * @return lista de salarios generados
     */
    public List<Salario> generarSalariosMasivos(YearMonth periodo, EmpleadoService empleadoService) {
        List<Empleado> empleadosActivos = empleadoService.listarActivos();
        
        for (Empleado empleado : empleadosActivos) {
            try {
                generarSalario(empleado, periodo);
            } catch (Exception e) {
                // Registrar error pero continuar con los demás empleados
                System.err.println("Error al generar salario para " + empleado.getNombreCompleto() + ": " + e.getMessage());
            }
        }
        
        return salarioDAO.listarPorPeriodo(periodo);
    }
    
    /**
     * Busca un salario por su ID
     * @param id el ID del salario
     * @return el salario encontrado o null si no existe
     */
    public Salario buscarPorId(Long id) {
        return salarioDAO.buscarPorId(id);
    }
    
    /**
     * Obtiene todos los salarios
     * @return lista de todos los salarios
     */
    public List<Salario> listarTodos() {
        return salarioDAO.listarTodos();
    }
    
    /**
     * Obtiene los salarios de un empleado específico
     * @param empleado el empleado
     * @return lista de salarios del empleado
     */
    public List<Salario> listarPorEmpleado(Empleado empleado) {
        return salarioDAO.listarPorEmpleado(empleado);
    }
    
    /**
     * Obtiene los salarios de un período específico
     * @param periodo el período (mes y año)
     * @return lista de salarios del período
     */
    public List<Salario> listarPorPeriodo(YearMonth periodo) {
        return salarioDAO.listarPorPeriodo(periodo);
    }
    
    /**
     * Obtiene los salarios pendientes de pago
     * @return lista de salarios no pagados
     */
    public List<Salario> listarNoPagados() {
        return salarioDAO.listarNoPagados();
    }
    
    /**
     * Calcula el total de aportes al IPS para un período específico
     * @param periodo el período (mes y año)
     * @return objeto con totales de aportes personales y patronales
     */
    public SalarioDAO.TotalAportesIPS calcularTotalAportesIPS(YearMonth periodo) {
        return salarioDAO.calcularTotalAportesIPS(periodo);
    }
}