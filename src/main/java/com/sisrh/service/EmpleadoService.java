package com.sisrh.service;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.DatabaseManager;
import com.sisrh.dao.EmpleadoDAO;
import com.sisrh.model.Cargo;
import com.sisrh.model.Contrato;
import com.sisrh.model.Empleado;
import com.sisrh.model.HistorialLaboral;

/**
 * Servicio para la gestión de empleados
 */
public class EmpleadoService {
    
    private EmpleadoDAO empleadoDAO;
    
    /**
     * Constructor por defecto
     */
    public EmpleadoService() {
        EntityManager em = DatabaseManager.getInstance().getEntityManager();
        this.empleadoDAO = new EmpleadoDAO(em);
    }
    
    /**
     * Constructor que recibe un DAO específico (útil para pruebas)
     * @param empleadoDAO el DAO a utilizar
     */
    public EmpleadoService(EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }
    
    /**
     * Registra un nuevo empleado en el sistema
     * @param empleado el empleado a registrar
     * @return el empleado registrado
     */
    public Empleado registrarEmpleado(Empleado empleado) {
        // Verificar que no exista otro empleado con la misma cédula
        Empleado existente = empleadoDAO.buscarPorCedula(empleado.getCedula());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un empleado con la cédula " + empleado.getCedula());
        }
        
        return empleadoDAO.guardar(empleado);
    }
    
    /**
     * Actualiza los datos de un empleado
     * @param empleado el empleado con los datos actualizados
     * @return el empleado actualizado
     */
    public Empleado actualizarEmpleado(Empleado empleado) {
        // Verificar que el empleado exista
        Empleado existente = empleadoDAO.buscarPorId(empleado.getId());
        if (existente == null) {
            throw new IllegalArgumentException("El empleado no existe");
        }
        
        // Verificar que no se duplique la cédula con otro empleado
        Empleado otroPorCedula = empleadoDAO.buscarPorCedula(empleado.getCedula());
        if (otroPorCedula != null && !otroPorCedula.getId().equals(empleado.getId())) {
            throw new IllegalArgumentException("Ya existe otro empleado con la cédula " + empleado.getCedula());
        }
        
        return empleadoDAO.guardar(empleado);
    }
    
    /**
     * Asigna un cargo a un empleado y registra el cambio en el historial
     * @param empleado el empleado
     * @param cargo el nuevo cargo
     * @param motivo motivo del cambio
     * @return el empleado actualizado
     */
    public Empleado asignarCargo(Empleado empleado, Cargo cargo, String motivo) {
        Cargo cargoAnterior = empleado.getCargoActual();
        empleado.setCargoActual(cargo);
        
        // Registrar en el historial laboral
        HistorialLaboral historial = new HistorialLaboral(empleado, cargoAnterior, cargo, motivo);
        empleado.agregarHistorialLaboral(historial);
        
        return empleadoDAO.guardar(empleado);
    }
    
    /**
     * Registra un nuevo contrato para un empleado
     * @param empleado el empleado
     * @param contrato el nuevo contrato
     * @return el empleado actualizado
     */
    public Empleado registrarContrato(Empleado empleado, Contrato contrato) {
        // Desactivar contratos anteriores si hay alguno activo
        Contrato contratoActivo = empleado.getContratoActivo();
        if (contratoActivo != null) {
            contratoActivo.setActivo(false);
            contratoActivo.setFechaFin(LocalDate.now());
        }
        
        // Agregar el nuevo contrato
        empleado.agregarContrato(contrato);
        
        return empleadoDAO.guardar(empleado);
    }
    
    /**
     * Da de baja a un empleado
     * @param empleado el empleado a dar de baja
     * @return el empleado actualizado
     */
    public Empleado darDeBaja(Empleado empleado) {
        // Desactivar el empleado
        empleado.setActivo(false);
        
        // Finalizar contrato activo si existe
        Contrato contratoActivo = empleado.getContratoActivo();
        if (contratoActivo != null) {
            contratoActivo.finalizar();
        }
        
        return empleadoDAO.guardar(empleado);
    }
    
    /**
     * Busca un empleado por su ID
     * @param id el ID del empleado
     * @return el empleado encontrado o null si no existe
     */
    public Empleado buscarPorId(Long id) {
        return empleadoDAO.buscarPorId(id);
    }
    
    /**
     * Busca un empleado por su cédula
     * @param cedula la cédula del empleado
     * @return el empleado encontrado o null si no existe
     */
    public Empleado buscarPorCedula(String cedula) {
        return empleadoDAO.buscarPorCedula(cedula);
    }
    
    /**
     * Obtiene todos los empleados
     * @return lista de todos los empleados
     */
    public List<Empleado> listarTodos() {
        return empleadoDAO.listarTodos();
    }
    
    /**
     * Obtiene todos los empleados activos
     * @return lista de empleados activos
     */
    public List<Empleado> listarActivos() {
        return empleadoDAO.listarActivos();
    }
    
    /**
     * Busca empleados por nombre o apellido
     * @param texto el texto a buscar
     * @return lista de empleados que coinciden con la búsqueda
     */
    public List<Empleado> buscarPorNombreOApellido(String texto) {
        return empleadoDAO.buscarPorNombreOApellido(texto);
    }
    
    /**
     * Busca empleados por nombre, apellido o cédula
     * @param criterio el criterio de búsqueda
     * @return lista de empleados que coinciden con la búsqueda
     */
    public List<Empleado> buscarPorNombreApellidoOCedula(String criterio) {
        return empleadoDAO.buscarPorNombreApellidoOCedula(criterio);
    }
    
    /**
     * Desactiva un empleado
     * @param empleado el empleado a desactivar
     * @return el empleado actualizado
     */
    public Empleado desactivarEmpleado(Empleado empleado) {
        return darDeBaja(empleado);
    }
    
    /**
     * Obtiene todos los cargos activos
     * @return lista de cargos activos
     */
    public List<Cargo> listarCargosActivos() {
        EntityManager em = DatabaseManager.getInstance().getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cargo c WHERE c.activo = true ORDER BY c.nombre", Cargo.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}