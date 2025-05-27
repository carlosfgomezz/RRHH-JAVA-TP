package com.sisrh.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entidad que representa una categoría de cargos en la empresa
 */
@Entity
public class Categoria implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String nombre;
    private String descripcion;
    private int nivel; // Nivel jerárquico (1 más alto, aumenta hacia abajo)
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Cargo> cargos = new ArrayList<>();
    
    // Constructor vacío requerido por JPA
    public Categoria() {
    }
    
    // Constructor con parámetros básicos
    public Categoria(String nombre, int nivel) {
        this.nombre = nombre;
        this.nivel = nivel;
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

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }
    
    /**
     * Agrega un cargo a esta categoría
     * @param cargo el cargo a agregar
     */
    public void agregarCargo(Cargo cargo) {
        cargos.add(cargo);
        cargo.setCategoria(this);
    }
    
    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nivel=" + nivel +
                '}';
    }
}