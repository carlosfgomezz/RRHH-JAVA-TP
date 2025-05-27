package com.sisrh.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Entidad que representa el salario de un empleado en un período específico
 */
@Entity
public class Salario implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Empleado empleado;
    
    private YearMonth periodo; // Mes y año al que corresponde el salario
    private LocalDate fechaPago; // Fecha efectiva de pago
    
    private double salarioBase; // Salario base según contrato
    private double bonificaciones; // Bonificaciones adicionales
    private double horasExtras; // Monto por horas extras
    private double adelantos; // Adelantos de salario
    private double ausencias; // Descuentos por ausencias
    private double otrosDescuentos; // Otros descuentos
    
    private double aportePersonalIPS; // 9% del salario bruto
    private double aportePatronalIPS; // 16.5% del salario bruto
    
    private boolean pagado; // Indica si ya fue pagado
    
    // Constructor vacío requerido por JPA
    public Salario() {
    }
    
    // Constructor con parámetros básicos
    public Salario(Empleado empleado, YearMonth periodo, double salarioBase) {
        this.empleado = empleado;
        this.periodo = periodo;
        this.salarioBase = salarioBase;
        this.pagado = false;
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

    public YearMonth getPeriodo() {
        return periodo;
    }

    public void setPeriodo(YearMonth periodo) {
        this.periodo = periodo;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public double getBonificaciones() {
        return bonificaciones;
    }

    public void setBonificaciones(double bonificaciones) {
        this.bonificaciones = bonificaciones;
    }

    public double getHorasExtras() {
        return horasExtras;
    }

    public void setHorasExtras(double horasExtras) {
        this.horasExtras = horasExtras;
    }

    public double getAdelantos() {
        return adelantos;
    }

    public void setAdelantos(double adelantos) {
        this.adelantos = adelantos;
    }

    public double getAusencias() {
        return ausencias;
    }

    public void setAusencias(double ausencias) {
        this.ausencias = ausencias;
    }

    public double getOtrosDescuentos() {
        return otrosDescuentos;
    }

    public void setOtrosDescuentos(double otrosDescuentos) {
        this.otrosDescuentos = otrosDescuentos;
    }

    public double getAportePersonalIPS() {
        return aportePersonalIPS;
    }

    public void setAportePersonalIPS(double aportePersonalIPS) {
        this.aportePersonalIPS = aportePersonalIPS;
    }

    public double getAportePatronalIPS() {
        return aportePatronalIPS;
    }

    public void setAportePatronalIPS(double aportePatronalIPS) {
        this.aportePatronalIPS = aportePatronalIPS;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
    
    // Métodos de cálculo
    
    /**
     * Calcula el salario bruto (base + bonificaciones + horas extras)
     * @return el monto del salario bruto
     */
    public double calcularSalarioBruto() {
        return salarioBase + bonificaciones + horasExtras;
    }
    
    /**
     * Calcula el total de descuentos (IPS personal + adelantos + ausencias + otros)
     * @return el monto total de descuentos
     */
    public double calcularTotalDescuentos() {
        return aportePersonalIPS + adelantos + ausencias + otrosDescuentos;
    }
    
    /**
     * Calcula el salario neto (bruto - descuentos)
     * @return el monto del salario neto a pagar
     */
    public double calcularSalarioNeto() {
        return calcularSalarioBruto() - calcularTotalDescuentos();
    }
    
    /**
     * Calcula el aporte personal al IPS (9% del salario bruto)
     */
    public void calcularAportePersonalIPS() {
        this.aportePersonalIPS = calcularSalarioBruto() * 0.09;
    }
    
    /**
     * Calcula el aporte patronal al IPS (16.5% del salario bruto)
     */
    public void calcularAportePatronalIPS() {
        this.aportePatronalIPS = calcularSalarioBruto() * 0.165;
    }
    
    /**
     * Realiza todos los cálculos necesarios para el salario
     */
    public void calcularTodo() {
        calcularAportePersonalIPS();
        calcularAportePatronalIPS();
    }
    
    /**
     * Marca el salario como pagado y establece la fecha de pago
     */
    public void pagar() {
        this.pagado = true;
        this.fechaPago = LocalDate.now();
    }
    
    @Override
    public String toString() {
        return "Salario{" +
                "id=" + id +
                ", periodo=" + periodo +
                ", salarioBase=" + salarioBase +
                ", salarioBruto=" + calcularSalarioBruto() +
                ", salarioNeto=" + calcularSalarioNeto() +
                ", pagado=" + pagado +
                '}';
    }
}