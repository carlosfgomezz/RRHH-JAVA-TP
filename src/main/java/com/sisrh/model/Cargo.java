package com.sisrh.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entidad que representa un cargo laboral en la empresa
 */
@Entity
public class Cargo implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String nombre;
    private String descripcion;
    
    @ManyToOne
    private Categoria categoria;
    
    private double salarioBase;
    private boolean activo;
    
    // Constructor vacío requerido por JPA
    public Cargo() {
    }
    
    // Constructor con parámetros básicos
    public Cargo(String nombre, Categoria categoria, double salarioBase) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.salarioBase = salarioBase;
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "Cargo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "Sin categoría") +
                ", salarioBase=" + salarioBase +
                ", activo=" + activo +
                '}';
    }
}