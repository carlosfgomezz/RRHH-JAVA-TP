package com.sisrh.dao;

import java.time.YearMonth;
import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.model.Empleado;
import com.sisrh.model.Salario;

/**
 * Clase DAO para operaciones de persistencia de Salarios
 */
public class SalarioDAO {
    
    private EntityManager em;
    
    /**
     * Constructor que recibe un EntityManager
     * @param em EntityManager para operaciones de persistencia
     */
    public SalarioDAO(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Guarda un salario en la base de datos
     * @param salario el salario a guardar
     * @return el salario guardado con su ID asignado
     */
    public Salario guardar(Salario salario) {
        try {
            em.getTransaction().begin();
            if (salario.getId() == null) {
                em.persist(salario);
            } else {
                salario = em.merge(salario);
            }
            em.getTransaction().commit();
            return salario;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Busca un salario por su ID
     * @param id el ID del salario
     * @return el salario encontrado o null si no existe
     */
    public Salario buscarPorId(Long id) {
        return em.find(Salario.class, id);
    }
    
    /**
     * Obtiene todos los salarios
     * @return lista de todos los salarios
     */
    public List<Salario> listarTodos() {
        return em.createQuery("SELECT s FROM Salario s ORDER BY s.periodo DESC", Salario.class)
                .getResultList();
    }
    
    /**
     * Obtiene los salarios de un empleado específico
     * @param empleado el empleado
     * @return lista de salarios del empleado
     */
    public List<Salario> listarPorEmpleado(Empleado empleado) {
        return em.createQuery("SELECT s FROM Salario s WHERE s.empleado = :empleado ORDER BY s.periodo DESC", Salario.class)
                .setParameter("empleado", empleado)
                .getResultList();
    }
    
    /**
     * Obtiene los salarios de un período específico
     * @param periodo el período (mes y año)
     * @return lista de salarios del período
     */
    public List<Salario> listarPorPeriodo(YearMonth periodo) {
        return em.createQuery("SELECT s FROM Salario s WHERE s.periodo = :periodo ORDER BY s.empleado.apellido, s.empleado.nombre", Salario.class)
                .setParameter("periodo", periodo)
                .getResultList();
    }
    
    /**
     * Busca el salario de un empleado en un período específico
     * @param empleado el empleado
     * @param periodo el período (mes y año)
     * @return el salario encontrado o null si no existe
     */
    public Salario buscarPorEmpleadoYPeriodo(Empleado empleado, YearMonth periodo) {
        try {
            return em.createQuery("SELECT s FROM Salario s WHERE s.empleado = :empleado AND s.periodo = :periodo", Salario.class)
                    .setParameter("empleado", empleado)
                    .setParameter("periodo", periodo)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene los salarios pendientes de pago
     * @return lista de salarios no pagados
     */
    public List<Salario> listarNoPagados() {
        return em.createQuery("SELECT s FROM Salario s WHERE s.pagado = false ORDER BY s.periodo", Salario.class)
                .getResultList();
    }
    
    /**
     * Calcula el total de aportes al IPS para un período específico
     * @param periodo el período (mes y año)
     * @return objeto con totales de aportes personales y patronales
     */
    public TotalAportesIPS calcularTotalAportesIPS(YearMonth periodo) {
        List<Salario> salarios = listarPorPeriodo(periodo);
        
        double totalPersonal = 0;
        double totalPatronal = 0;
        
        for (Salario salario : salarios) {
            totalPersonal += salario.getAportePersonalIPS();
            totalPatronal += salario.getAportePatronalIPS();
        }
        
        return new TotalAportesIPS(totalPersonal, totalPatronal);
    }
    
    /**
     * Clase para representar los totales de aportes IPS
     */
    public static class TotalAportesIPS {
        private double totalPersonal;
        private double totalPatronal;
        
        public TotalAportesIPS(double totalPersonal, double totalPatronal) {
            this.totalPersonal = totalPersonal;
            this.totalPatronal = totalPatronal;
        }
        
        public double getTotalPersonal() {
            return totalPersonal;
        }
        
        public double getTotalPatronal() {
            return totalPatronal;
        }
        
        public double getTotal() {
            return totalPersonal + totalPatronal;
        }
    }
}