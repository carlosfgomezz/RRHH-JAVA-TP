package com.sisrh.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.model.Configuracion;
import com.sisrh.model.Usuario;

/**
 * Clase DAO para operaciones de persistencia de Configuraciones
 */
public class ConfiguracionDAO {
    
    private EntityManager em;
    
    /**
     * Constructor que recibe un EntityManager
     * @param em EntityManager para operaciones de persistencia
     */
    public ConfiguracionDAO(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Guarda una configuración en la base de datos
     * @param configuracion la configuración a guardar
     * @return la configuración guardada
     */
    public Configuracion guardar(Configuracion configuracion) {
        try {
            em.getTransaction().begin();
            if (buscarPorClave(configuracion.getClave()) == null) {
                em.persist(configuracion);
            } else {
                configuracion = em.merge(configuracion);
            }
            em.getTransaction().commit();
            return configuracion;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Busca una configuración por su clave
     * @param clave la clave de la configuración
     * @return la configuración encontrada o null si no existe
     */
    public Configuracion buscarPorClave(String clave) {
        return em.find(Configuracion.class, clave);
    }
    
    /**
     * Obtiene todas las configuraciones
     * @return lista de todas las configuraciones
     */
    public List<Configuracion> listarTodas() {
        return em.createQuery("SELECT c FROM Configuracion c ORDER BY c.clave", Configuracion.class)
                .getResultList();
    }
    
    /**
     * Actualiza el valor de una configuración
     * @param clave la clave de la configuración
     * @param valor el nuevo valor
     * @param usuario el usuario que realiza la modificación
     * @return la configuración actualizada o null si no existe
     */
    public Configuracion actualizarValor(String clave, String valor, Usuario usuario) {
        Configuracion configuracion = buscarPorClave(clave);
        if (configuracion != null) {
            try {
                em.getTransaction().begin();
                configuracion.actualizarValor(valor, usuario);
                em.getTransaction().commit();
                return configuracion;
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return null;
    }
    
    /**
     * Obtiene el valor de una configuración como String
     * @param clave la clave de la configuración
     * @param valorPorDefecto valor por defecto si no existe la configuración
     * @return el valor de la configuración o el valor por defecto
     */
    public String obtenerValorString(String clave, String valorPorDefecto) {
        Configuracion configuracion = buscarPorClave(clave);
        return configuracion != null ? configuracion.getValor() : valorPorDefecto;
    }
    
    /**
     * Obtiene el valor de una configuración como entero
     * @param clave la clave de la configuración
     * @param valorPorDefecto valor por defecto si no existe la configuración
     * @return el valor de la configuración como entero o el valor por defecto
     */
    public int obtenerValorEntero(String clave, int valorPorDefecto) {
        Configuracion configuracion = buscarPorClave(clave);
        return configuracion != null ? configuracion.getValorEntero() : valorPorDefecto;
    }
    
    /**
     * Obtiene el valor de una configuración como decimal
     * @param clave la clave de la configuración
     * @param valorPorDefecto valor por defecto si no existe la configuración
     * @return el valor de la configuración como decimal o el valor por defecto
     */
    public double obtenerValorDecimal(String clave, double valorPorDefecto) {
        Configuracion configuracion = buscarPorClave(clave);
        return configuracion != null ? configuracion.getValorDecimal() : valorPorDefecto;
    }
    
    /**
     * Obtiene el valor de una configuración como booleano
     * @param clave la clave de la configuración
     * @param valorPorDefecto valor por defecto si no existe la configuración
     * @return el valor de la configuración como booleano o el valor por defecto
     */
    public boolean obtenerValorBooleano(String clave, boolean valorPorDefecto) {
        Configuracion configuracion = buscarPorClave(clave);
        return configuracion != null ? configuracion.getValorBooleano() : valorPorDefecto;
    }
    
    /**
     * Inicializa las configuraciones por defecto si no existen
     * @param usuario el usuario administrador
     */
    public void inicializarConfiguracionesPorDefecto(Usuario usuario) {
        // Configuraciones del sistema
        if (buscarPorClave(Configuracion.Claves.SALARIO_MINIMO) == null) {
            Configuracion config = new Configuracion(
                    Configuracion.Claves.SALARIO_MINIMO,
                    "2550423", // Valor actual del salario mínimo en Paraguay (ejemplo)
                    "Salario mínimo legal vigente");
            config.setUsuarioModificacion(usuario);
            guardar(config);
        }
        
        if (buscarPorClave(Configuracion.Claves.PORCENTAJE_IPS_PERSONAL) == null) {
            Configuracion config = new Configuracion(
                    Configuracion.Claves.PORCENTAJE_IPS_PERSONAL,
                    "9.0",
                    "Porcentaje de aporte personal al IPS");
            config.setUsuarioModificacion(usuario);
            guardar(config);
        }
        
        if (buscarPorClave(Configuracion.Claves.PORCENTAJE_IPS_PATRONAL) == null) {
            Configuracion config = new Configuracion(
                    Configuracion.Claves.PORCENTAJE_IPS_PATRONAL,
                    "16.5",
                    "Porcentaje de aporte patronal al IPS");
            config.setUsuarioModificacion(usuario);
            guardar(config);
        }
        
        if (buscarPorClave(Configuracion.Claves.DIA_PAGO) == null) {
            Configuracion config = new Configuracion(
                    Configuracion.Claves.DIA_PAGO,
                    "5",
                    "Día del mes para el pago de salarios");
            config.setUsuarioModificacion(usuario);
            guardar(config);
        }
        
        // Datos de la empresa
        if (buscarPorClave(Configuracion.Claves.EMPRESA_NOMBRE) == null) {
            Configuracion config = new Configuracion(
                    Configuracion.Claves.EMPRESA_NOMBRE,
                    "Mi Empresa S.A.",
                    "Nombre legal de la empresa");
            config.setUsuarioModificacion(usuario);
            guardar(config);
        }
        
        if (buscarPorClave(Configuracion.Claves.EMPRESA_RUC) == null) {
            Configuracion config = new Configuracion(
                    Configuracion.Claves.EMPRESA_RUC,
                    "80012345-6",
                    "RUC de la empresa");
            config.setUsuarioModificacion(usuario);
            guardar(config);
        }
    }
}