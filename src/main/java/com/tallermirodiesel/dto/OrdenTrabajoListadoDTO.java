/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dto;

import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.EstadoPagoEnum;
import com.tallermirodiesel.model.enums.TipoIngresoOrdenEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author elyrr
 * DTO para el listado de órdenes de trabajo.
 * Contiene solo lo necesario para mostrar en orden_trabajo_listar.jsp.
 */
public class OrdenTrabajoListadoDTO {

    // =========================
    // BLOQUE: Identificación
    // =========================
    private Long idOrdenTrabajo;
    private String numeroOrden;
    private LocalDateTime fechaIngreso;
    private EstadoOrdenTrabajoEnum estado;
    private EstadoPagoEnum estadoPago;
    private boolean activo;

    // =========================
    // BLOQUE: Cliente (JOIN clientes + clientes_persona/empresa)
    // =========================
    private String nombreCliente;

    // =========================
    // BLOQUE: Objeto ingresado (JOIN vehiculos o componentes)
    // =========================
    private TipoIngresoOrdenEnum tipoIngreso;
    private String placaVehiculo;       // solo si tipoIngreso = VEHICULO
    private String marcaModelo;         // "Toyota Hilux" o "Marca Modelo" del componente

    // =========================
    // BLOQUE: Financiero
    // =========================
    private BigDecimal totalTrabajo;

    // Constructor vacío
    public OrdenTrabajoListadoDTO() {
    }

    // Constructor con parámetros
    public OrdenTrabajoListadoDTO(Long idOrdenTrabajo, String numeroOrden, LocalDateTime fechaIngreso,
                                  EstadoOrdenTrabajoEnum estado, EstadoPagoEnum estadoPago, boolean activo,
                                  String nombreCliente, TipoIngresoOrdenEnum tipoIngreso,
                                  String placaVehiculo, String marcaModelo, BigDecimal totalTrabajo) {
        this.idOrdenTrabajo = idOrdenTrabajo;
        this.numeroOrden    = numeroOrden;
        this.fechaIngreso   = fechaIngreso;
        this.estado         = estado;
        this.estadoPago     = estadoPago;
        this.activo         = activo;
        this.nombreCliente  = nombreCliente;
        this.tipoIngreso    = tipoIngreso;
        this.placaVehiculo  = placaVehiculo;
        this.marcaModelo    = marcaModelo;
        this.totalTrabajo   = totalTrabajo;
    }

    // Getters y setters
    public Long getIdOrdenTrabajo() {
        return idOrdenTrabajo;
    }
    public void setIdOrdenTrabajo(Long idOrdenTrabajo) {
        this.idOrdenTrabajo = idOrdenTrabajo;
    }
//
    public String getNumeroOrden() {
        return numeroOrden;
    }
    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }
//
    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }
    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
//
    public EstadoOrdenTrabajoEnum getEstado() {
        return estado;
    }
    public void setEstado(EstadoOrdenTrabajoEnum estado) {
        this.estado = estado;
    }
//
    public EstadoPagoEnum getEstadoPago() {
        return estadoPago;
    }
    public void setEstadoPago(EstadoPagoEnum estadoPago) {
        this.estadoPago = estadoPago;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
//
    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
//
    public TipoIngresoOrdenEnum getTipoIngreso() {
        return tipoIngreso;
    }
    public void setTipoIngreso(TipoIngresoOrdenEnum tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }
//
    public String getPlacaVehiculo() {
        return placaVehiculo;
    }
    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }
//
    public String getMarcaModelo() {
        return marcaModelo;
    }
    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }
//
    public BigDecimal getTotalTrabajo() {
        return totalTrabajo;
    }
    public void setTotalTrabajo(BigDecimal totalTrabajo) {
        this.totalTrabajo = totalTrabajo;
    }
}