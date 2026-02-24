/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dto;

import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.EstadoPagoEnum;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.model.enums.TipoIngresoOrdenEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author elyrr
 * DTO para la pantalla de detalle de una orden de trabajo.
 * Contiene todos los datos necesarios para orden_trabajo_ver.jsp.
 */
public class OrdenTrabajoVerDTO {

    // =========================
    // BLOQUE: Identificación de la OT
    // =========================
    private Long idOrdenTrabajo;
    private String numeroOrden;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaFin;
    private EstadoOrdenTrabajoEnum estado;
    private EstadoPagoEnum estadoPago;
    private boolean activo;
    private String problemaReportado;
    private String observacionesIngreso;

    // =========================
    // BLOQUE: Cliente (JOIN clientes + clientes_persona/empresa + distritos + localidades)
    // =========================
    private Long idCliente;
    private String nombreCliente;
    private String telefonoCliente;
    private String nombreDistritoCliente;
    private String nombreLocalidadCliente;

    // =========================
    // BLOQUE: Tipo de ingreso
    // =========================
    private TipoIngresoOrdenEnum tipoIngreso;

    // Vehículo (solo si tipoIngreso = VEHICULO)
    private String placaVehiculo;
    private String marcaVehiculo;
    private String modeloVehiculo;

    // Componente (solo si tipoIngreso = COMPONENTE)
    private String numeroSerieComponente;
    private String marcaComponente;
    private String modeloComponente;
    private Short cantidadPicos;

    // =========================
    // BLOQUE: Referidor (JOIN clientes + clientes_persona/empresa)
    // =========================
    private FuenteReferenciaClienteEnum fuenteReferencia;
    private String nombreReferidor;

    // =========================
    // BLOQUE: Usuario que registró (JOIN usuarios)
    // =========================
    private String nombreUsuario;

    // =========================
    // BLOQUE: Financiero
    // =========================
    private BigDecimal totalTrabajo;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;

    // Constructor vacío
    public OrdenTrabajoVerDTO() {
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
    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }
    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }
//
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
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
    public String getProblemaReportado() {
        return problemaReportado;
    }
    public void setProblemaReportado(String problemaReportado) {
        this.problemaReportado = problemaReportado;
    }
//
    public String getObservacionesIngreso() {
        return observacionesIngreso;
    }
    public void setObservacionesIngreso(String observacionesIngreso) {
        this.observacionesIngreso = observacionesIngreso;
    }
//
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
//
    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
//
    public String getTelefonoCliente() {
        return telefonoCliente;
    }
    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }
//
    public String getNombreDistritoCliente() {
        return nombreDistritoCliente;
    }
    public void setNombreDistritoCliente(String nombreDistritoCliente) {
        this.nombreDistritoCliente = nombreDistritoCliente;
    }
//
    public String getNombreLocalidadCliente() {
        return nombreLocalidadCliente;
    }
    public void setNombreLocalidadCliente(String nombreLocalidadCliente) {
        this.nombreLocalidadCliente = nombreLocalidadCliente;
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
    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }
    public void setMarcaVehiculo(String marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }
//
    public String getModeloVehiculo() {
        return modeloVehiculo;
    }
    public void setModeloVehiculo(String modeloVehiculo) {
        this.modeloVehiculo = modeloVehiculo;
    }
//
    public String getNumeroSerieComponente() {
        return numeroSerieComponente;
    }
    public void setNumeroSerieComponente(String numeroSerieComponente) {
        this.numeroSerieComponente = numeroSerieComponente;
    }
//
    public String getMarcaComponente() {
        return marcaComponente;
    }
    public void setMarcaComponente(String marcaComponente) {
        this.marcaComponente = marcaComponente;
    }
//
    public String getModeloComponente() {
        return modeloComponente;
    }
    public void setModeloComponente(String modeloComponente) {
        this.modeloComponente = modeloComponente;
    }
//
    public Short getCantidadPicos() {
        return cantidadPicos;
    }
    public void setCantidadPicos(Short cantidadPicos) {
        this.cantidadPicos = cantidadPicos;
    }
//
    public FuenteReferenciaClienteEnum getFuenteReferencia() {
        return fuenteReferencia;
    }
    public void setFuenteReferencia(FuenteReferenciaClienteEnum fuenteReferencia) {
        this.fuenteReferencia = fuenteReferencia;
    }
//
    public String getNombreReferidor() {
        return nombreReferidor;
    }
    public void setNombreReferidor(String nombreReferidor) {
        this.nombreReferidor = nombreReferidor;
    }
//
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
//
    public BigDecimal getTotalTrabajo() {
        return totalTrabajo;
    }
    public void setTotalTrabajo(BigDecimal totalTrabajo) {
        this.totalTrabajo = totalTrabajo;
    }
//
    public BigDecimal getTotalPagado() {
        return totalPagado;
    }
    public void setTotalPagado(BigDecimal totalPagado) {
        this.totalPagado = totalPagado;
    }
//
    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }
    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }
}