package com.sisrh.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.model.Empleado;

/**
 * Clase DAO para operaciones de persistencia de Empleados
 */
public class EmpleadoDAO {
    
    private EntityManager em;
    
    /**
     * Constructor que recibe un EntityManager
     * @param em EntityManager para operaciones de persistencia
     */
    public EmpleadoDAO(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Guarda un empleado en la base de datos
     * @param empleado el empleado a guardar
     * @return el empleado guardado con su ID asignado
     */
    public Empleado guardar(Empleado empleado) {
        try {
            em.getTransaction().begin();
            if (empleado.getId() == null) {
                em.persist(empleado);
            } else {
                empleado = em.merge(empleado);
            }
            em.getTransaction().commit();
            return empleado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Busca un empleado por su ID
     * @param id el ID del empleado
     * @return el empleado encontrado o null si no existe
     */
    public Empleado buscarPorId(Long id) {
        return em.find(Empleado.class, id);
    }
    
    /**
     * Busca un empleado por su número de cédula
     * @param cedula el número de cédula
     * @return el empleado encontrado o null si no existe
     */
    public Empleado buscarPorCedula(String cedula) {
        try {
            return em.createQuery("SELECT e FROM Empleado e WHERE e.cedula = :cedula", Empleado.class)
                    .setParameter("cedula", cedula)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene todos los empleados
     * @return lista de todos los empleados
     */
    public List<Empleado> listarTodos() {
        return em.createQuery("SELECT e FROM Empleado e ORDER BY e.apellido, e.nombre", Empleado.class)
                .getResultList();
    }
    
    /**
     * Obtiene todos los empleados activos
     * @return lista de empleados activos
     */
    public List<Empleado> listarActivos() {
        return em.createQuery("SELECT e FROM Empleado e WHERE e.activo = true ORDER BY e.apellido, e.nombre", Empleado.class)
                .getResultList();
    }
    
    /**
     * Busca empleados por nombre o apellido
     * @param texto el texto a buscar
     * @return lista de empleados que coinciden con la búsqueda
     */
    public List<Empleado> buscarPorNombreOApellido(String texto) {
        String busqueda = "%" + texto.toLowerCase() + "%";
        return em.createQuery(
                "SELECT e FROM Empleado e WHERE LOWER(e.nombre) LIKE :busqueda OR LOWER(e.apellido) LIKE :busqueda ORDER BY e.apellido, e.nombre",
                Empleado.class)
                .setParameter("busqueda", busqueda)
                .getResultList();
    }
    
    /**
     * Busca empleados por nombre, apellido o cédula
     * @param criterio el criterio de búsqueda
     * @return lista de empleados que coinciden con la búsqueda
     */
    public List<Empleado> buscarPorNombreApellidoOCedula(String criterio) {
        String busqueda = "%" + criterio.toLowerCase() + "%";
        return em.createQuery(
                "SELECT e FROM Empleado e WHERE LOWER(e.nombre) LIKE :busqueda OR LOWER(e.apellido) LIKE :busqueda OR LOWER(e.cedula) LIKE :busqueda ORDER BY e.apellido, e.nombre",
                Empleado.class)
                .setParameter("busqueda", busqueda)
                .getResultList();
    }
    
    /**
     * Elimina un empleado de la base de datos
     * @param empleado el empleado a eliminar
     */
    public void eliminar(Empleado empleado) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(empleado) ? empleado : em.merge(empleado));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Desactiva un empleado (baja lógica)
     * @param empleado el empleado a desactivar
     * @return el empleado actualizado
     */
    public Empleado desactivar(Empleado empleado) {
        empleado.setActivo(false);
        return guardar(empleado);
    }
}