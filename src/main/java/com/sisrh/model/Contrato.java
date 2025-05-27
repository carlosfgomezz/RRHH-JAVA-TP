package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entidad que representa un contrato laboral en el sistema
 */
@Entity
public class Contrato implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Empleado empleado;
    
    @Enumerated(EnumType.STRING)
    private TipoContrato tipo;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean renovacionAutomatica;
    private boolean activo;
    private String observaciones;
    
    // Constructor vacío requerido por JPA
    public Contrato() {
    }
    
    // Constructor con parámetros básicos
    public Contrato(Empleado empleado, TipoContrato tipo, LocalDate fechaInicio) {
        this.empleado = empleado;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.activo = true;
        
        // Si es contrato indefinido, no tiene fecha de fin
        if (tipo == TipoContrato.INDEFINIDO) {
            this.fechaFin = null;
        }
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public TipoContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoContrato tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    /**
     * Verifica si el contrato está vigente según la fecha actual
     * @return true si el contrato está vigente, false en caso contrario
     */
    public boolean isVigente() {
        LocalDate hoy = LocalDate.now();
        
        // Si no está activo, no está vigente
        if (!activo) {
            return false;
        }
        
        // Si es indefinido y está activo, siempre está vigente
        if (tipo == TipoContrato.INDEFINIDO) {
            return true;
        }
        
        // Si tiene fecha de fin, verificar que no haya pasado
        return fechaFin == null || !hoy.isAfter(fechaFin);
    }
    
    /**
     * Renueva el contrato por un período adicional
     * @param nuevaFechaFin la nueva fecha de fin del contrato
     */
    public void renovar(LocalDate nuevaFechaFin) {
        this.fechaFin = nuevaFechaFin;
        this.activo = true;
    }
    
    /**
     * Finaliza el contrato en la fecha actual
     */
    public void finalizar() {
        this.fechaFin = LocalDate.now();
        this.activo = false;
    }
    
    @Override
    public String toString() {
        return "Contrato{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", activo=" + activo +
                '}';
    }
    
    /**
     * Enumeración para los tipos de contrato
     */
    public enum TipoContrato {
        INDEFINIDO,
        PLAZO_FIJO,
        PASANTIA
    }
}