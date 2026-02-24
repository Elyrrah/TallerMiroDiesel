/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import java.math.BigDecimal;

/**
 * @author elyrr
 */
public class OrdenTrabajoDetalle {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idDetalle;
    private Long idOrdenTrabajo;
    private Long idServicio;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private Short garantiaMeses;
    private Short garantiaDias;
    private String observaciones;
    private boolean activo;

    // Constructor vacío
    public OrdenTrabajoDetalle() {
    }

    // Constructor con parámetros
    public OrdenTrabajoDetalle(Long idDetalle, Long idOrdenTrabajo, Long idServicio,
                               BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                               Short garantiaMeses, Short garantiaDias, String observaciones,
                               boolean activo) {
        this.idDetalle      = idDetalle;
        this.idOrdenTrabajo = idOrdenTrabajo;
        this.idServicio     = idServicio;
        this.cantidad       = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal       = subtotal;
        this.garantiaMeses  = garantiaMeses;
        this.garantiaDias   = garantiaDias;
        this.observaciones  = observaciones;
        this.activo         = activo;
    }

    // Getters y setters
    public Long getIdDetalle() {
        return idDetalle;
    }
    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }
//
    public Long getIdOrdenTrabajo() {
        return idOrdenTrabajo;
    }
    public void setIdOrdenTrabajo(Long idOrdenTrabajo) {
        this.idOrdenTrabajo = idOrdenTrabajo;
    }
//
    public Long getIdServicio() {
        return idServicio;
    }
    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }
//
    public BigDecimal getCantidad() {
        return cantidad;
    }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
//
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
//
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
//
    public Short getGarantiaMeses() {
        return garantiaMeses;
    }
    public void setGarantiaMeses(Short garantiaMeses) {
        this.garantiaMeses = garantiaMeses;
    }
//
    public Short getGarantiaDias() {
        return garantiaDias;
    }
    public void setGarantiaDias(Short garantiaDias) {
        this.garantiaDias = garantiaDias;
    }
//
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}