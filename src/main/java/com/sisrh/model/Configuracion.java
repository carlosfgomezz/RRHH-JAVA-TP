package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entidad que representa la configuración del sistema
 */
@Entity
public class Configuracion implements Serializable {
    
    @Id
    private String clave;
    
    @Column(length = 1000)
    private String valor;
    
    private String descripcion;
    private LocalDate fechaModificacion;
    
    @ManyToOne
    private Usuario usuarioModificacion;
    
    // Constructor vacío requerido por JPA
    public Configuracion() {
    }
    
    // Constructor con parámetros básicos
    public Configuracion(String clave, String valor, String descripcion) {
        this.clave = clave;
        this.valor = valor;
        this.descripcion = descripcion;
        this.fechaModificacion = LocalDate.now();
    }
    
    // Getters y Setters
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
        this.fechaModificacion = LocalDate.now();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDate fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Usuario getUsuarioModificacion() {
        return usuarioModificacion;
    }

    public void setUsuarioModificacion(Usuario usuarioModificacion) {
        this.usuarioModificacion = usuarioModificacion;
    }
    
    /**
     * Actualiza el valor y registra quién lo modificó
     * @param valor nuevo valor
     * @param usuario usuario que realiza la modificación
     */
    public void actualizarValor(String valor, Usuario usuario) {
        this.valor = valor;
        this.fechaModificacion = LocalDate.now();
        this.usuarioModificacion = usuario;
    }
    
    /**
     * Obtiene el valor como entero
     * @return valor como entero o 0 si no es válido
     */
    public int getValorEntero() {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Obtiene el valor como decimal
     * @return valor como decimal o 0 si no es válido
     */
    public double getValorDecimal() {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Obtiene el valor como booleano
     * @return true si el valor es "true", "1", "si" o "yes", false en caso contrario
     */
    public boolean getValorBooleano() {
        if (valor == null) {
            return false;
        }
        String v = valor.toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("si") || v.equals("yes");
    }
    
    /**
     * Obtiene el valor como fecha
     * @return fecha o null si no es válida
     */
    public LocalDate getValorFecha() {
        try {
            return LocalDate.parse(valor);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return "Configuracion{" +
                "clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                ", fechaModificacion=" + fechaModificacion +
                '}';
    }
    
    /**
     * Claves de configuración predefinidas
     */
    public static class Claves {
        public static final String SALARIO_MINIMO = "SALARIO_MINIMO";
        public static final String PORCENTAJE_IPS_PERSONAL = "PORCENTAJE_IPS_PERSONAL";
        public static final String PORCENTAJE_IPS_PATRONAL = "PORCENTAJE_IPS_PATRONAL";
        public static final String DIA_PAGO = "DIA_PAGO";
        public static final String EMPRESA_NOMBRE = "EMPRESA_NOMBRE";
        public static final String EMPRESA_RUC = "EMPRESA_RUC";
        public static final String EMPRESA_DIRECCION = "EMPRESA_DIRECCION";
        public static final String EMPRESA_TELEFONO = "EMPRESA_TELEFONO";
    }
}