package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entidad que representa un usuario del sistema
 */
@Entity
public class Usuario implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String username;
    
    private String password; // Debe almacenarse encriptada
    private String nombre;
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    private boolean activo;
    private LocalDateTime ultimoAcceso;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<HistorialSesion> historialSesiones = new ArrayList<>();
    
    // Constructor vacío requerido por JPA
    public Usuario() {
    }
    
    // Constructor con parámetros básicos
    public Usuario(String username, String password, String nombre, Rol rol) {
        this.username = username;
        this.password = password; // Debería encriptarse antes de asignar
        this.nombre = nombre;
        this.rol = rol;
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public List<HistorialSesion> getHistorialSesiones() {
        return historialSesiones;
    }

    public void setHistorialSesiones(List<HistorialSesion> historialSesiones) {
        this.historialSesiones = historialSesiones;
    }
    
    /**
     * Registra un nuevo acceso al sistema
     * @param ip dirección IP desde donde se accede
     * @return el registro de historial creado
     */
    public HistorialSesion registrarAcceso(String ip) {
        HistorialSesion sesion = new HistorialSesion(this, ip);
        historialSesiones.add(sesion);
        this.ultimoAcceso = sesion.getFechaInicio();
        return sesion;
    }
    
    /**
     * Verifica si el usuario tiene permisos para una operación específica
     * @param operacion la operación a verificar
     * @return true si tiene permiso, false en caso contrario
     */
    public boolean tienePermiso(String operacion) {
        // Implementar lógica de permisos según el rol
        switch (rol) {
            case ADMIN:
                return true; // El administrador tiene todos los permisos
            case RRHH:
                // RRHH puede hacer todo excepto configuración del sistema
                return !operacion.startsWith("CONFIG_");
            case CONTABILIDAD:
                // Contabilidad solo puede ver reportes y gestionar salarios
                return operacion.startsWith("REPORTE_") || operacion.startsWith("SALARIO_");
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol=" + rol +
                ", activo=" + activo +
                '}';
    }
    
    /**
     * Enumeración para los roles de usuario
     */
    public enum Rol {
        ADMIN,
        RRHH,
        CONTABILIDAD
    }
}