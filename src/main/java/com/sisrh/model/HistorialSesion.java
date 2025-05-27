package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entidad que representa un registro de sesión de usuario en el sistema
 */
@Entity
public class HistorialSesion implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Usuario usuario;
    
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String ipAcceso;
    private String navegador;
    private boolean activa;
    
    // Constructor vacío requerido por JPA
    public HistorialSesion() {
    }
    
    // Constructor con parámetros básicos
    public HistorialSesion(Usuario usuario, String ipAcceso) {
        this.usuario = usuario;
        this.ipAcceso = ipAcceso;
        this.fechaInicio = LocalDateTime.now();
        this.activa = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getIpAcceso() {
        return ipAcceso;
    }

    public void setIpAcceso(String ipAcceso) {
        this.ipAcceso = ipAcceso;
    }

    public String getNavegador() {
        return navegador;
    }

    public void setNavegador(String navegador) {
        this.navegador = navegador;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    /**
     * Finaliza la sesión actual
     */
    public void finalizarSesion() {
        this.fechaFin = LocalDateTime.now();
        this.activa = false;
    }
    
    /**
     * Calcula la duración de la sesión en minutos
     * @return duración en minutos o -1 si la sesión sigue activa
     */
    public long getDuracionMinutos() {
        if (fechaFin == null) {
            return -1;
        }
        return java.time.Duration.between(fechaInicio, fechaFin).toMinutes();
    }
    
    @Override
    public String toString() {
        return "HistorialSesion{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getUsername() : "desconocido") +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", ipAcceso='" + ipAcceso + '\'' +
                ", activa=" + activa +
                '}';
    }
}