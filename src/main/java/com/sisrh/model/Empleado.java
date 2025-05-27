package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Entidad que representa a un empleado en el sistema
 */
@Entity
public class Empleado implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String cedula;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private String genero;
    private String estadoCivil;
    
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contrato> contratos = new ArrayList<>();
    
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Salario> salarios = new ArrayList<>();
    
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialLaboral> historialLaboral = new ArrayList<>();
    
    @ManyToOne
    private Cargo cargoActual;
    
    private boolean activo;
    
    // Constructor vacío requerido por JPA
    public Empleado() {
    }
    
    // Constructor con parámetros básicos
    public Empleado(String cedula, String nombre, String apellido) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public List<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<Contrato> contratos) {
        this.contratos = contratos;
    }

    public List<Salario> getSalarios() {
        return salarios;
    }

    public void setSalarios(List<Salario> salarios) {
        this.salarios = salarios;
    }

    public List<HistorialLaboral> getHistorialLaboral() {
        return historialLaboral;
    }

    public void setHistorialLaboral(List<HistorialLaboral> historialLaboral) {
        this.historialLaboral = historialLaboral;
    }

    public Cargo getCargoActual() {
        return cargoActual;
    }

    public void setCargoActual(Cargo cargoActual) {
        this.cargoActual = cargoActual;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    // Métodos de utilidad
    
    /**
     * Agrega un contrato al empleado
     * @param contrato el contrato a agregar
     */
    public void agregarContrato(Contrato contrato) {
        contratos.add(contrato);
        contrato.setEmpleado(this);
    }
    
    /**
     * Agrega un registro de salario al empleado
     * @param salario el salario a agregar
     */
    public void agregarSalario(Salario salario) {
        salarios.add(salario);
        salario.setEmpleado(this);
    }
    
    /**
     * Agrega un registro al historial laboral del empleado
     * @param historial el registro de historial a agregar
     */
    public void agregarHistorialLaboral(HistorialLaboral historial) {
        historialLaboral.add(historial);
        historial.setEmpleado(this);
    }
    
    /**
     * Obtiene el contrato activo del empleado
     * @return el contrato activo o null si no tiene
     */
    public Contrato getContratoActivo() {
        return contratos.stream()
                .filter(Contrato::isActivo)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Obtiene el contrato actual del empleado (alias para getContratoActivo)
     * @return el contrato actual o null si no tiene
     */
    public Contrato getContratoActual() {
        return getContratoActivo();
    }
    
    /**
     * Obtiene el último salario registrado del empleado
     * @return el último salario o null si no tiene
     */
    public Salario getUltimoSalario() {
        if (salarios.isEmpty()) {
            return null;
        }
        return salarios.get(salarios.size() - 1);
    }
    
    /**
     * Retorna el nombre completo del empleado
     * @return nombre y apellido concatenados
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", activo=" + activo +
                '}';
    }
}