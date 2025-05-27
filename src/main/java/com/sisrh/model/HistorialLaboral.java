package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entidad que representa un registro en el historial laboral de un empleado
 */
@Entity
public class HistorialLaboral implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Empleado empleado;
    
    @ManyToOne
    private Cargo cargoAnterior;
    
    @ManyToOne
    private Cargo cargoNuevo;
    
    private LocalDate fechaCambio;
    private String motivo;
    private String observaciones;
    
    // Constructor vacío requerido por JPA
    public HistorialLaboral() {
    }
    
    // Constructor con parámetros básicos
    public HistorialLaboral(Empleado empleado, Cargo cargoAnterior, Cargo cargoNuevo, String motivo) {
        this.empleado = empleado;
        this.cargoAnterior = cargoAnterior;
        this.cargoNuevo = cargoNuevo;
        this.motivo = motivo;
        this.fechaCambio = LocalDate.now();
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

    public Cargo getCargoAnterior() {
        return cargoAnterior;
    }

    public void setCargoAnterior(Cargo cargoAnterior) {
        this.cargoAnterior = cargoAnterior;
    }

    public Cargo getCargoNuevo() {
        return cargoNuevo;
    }

    public void setCargoNuevo(Cargo cargoNuevo) {
        this.cargoNuevo = cargoNuevo;
    }

    public LocalDate getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDate fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    /**
     * Determina si el cambio fue una promoción (subida de nivel)
     * @return true si fue promoción, false en caso contrario
     */
    public boolean esPromocion() {
        if (cargoAnterior == null || cargoNuevo == null) {
            return false;
        }
        
        Categoria categoriaAnterior = cargoAnterior.getCategoria();
        Categoria categoriaNueva = cargoNuevo.getCategoria();
        
        if (categoriaAnterior == null || categoriaNueva == null) {
            return false;
        }
        
        // Nivel más bajo numéricamente significa más alto jerárquicamente
        return categoriaNueva.getNivel() < categoriaAnterior.getNivel();
    }
    
    @Override
    public String toString() {
        return "HistorialLaboral{" +
                "id=" + id +
                ", empleado=" + (empleado != null ? empleado.getNombreCompleto() : "Sin empleado") +
                ", cargoAnterior=" + (cargoAnterior != null ? cargoAnterior.getNombre() : "Sin cargo") +
                ", cargoNuevo=" + (cargoNuevo != null ? cargoNuevo.getNombre() : "Sin cargo") +
                ", fechaCambio=" + fechaCambio +
                '}';
    }
}