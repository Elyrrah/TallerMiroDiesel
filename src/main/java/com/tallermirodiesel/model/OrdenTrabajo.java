/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.EstadoPagoEnum;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.model.enums.TipoIngresoOrdenEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author elyrr
 */
public class OrdenTrabajo {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idOrdenTrabajo;
    private String numeroOrden;
    private Long idUsuario;
    private Long idCliente;
    private TipoIngresoOrdenEnum tipoIngreso;
    private Long idVehiculo;
    private Long idComponente;
    private Short cantidadPicos;
    private FuenteReferenciaClienteEnum fuenteReferencia;
    private Long idReferidor;
    private LocalDateTime fechaIngreso;
    private String problemaReportado;
    private String observacionesIngreso;
    private EstadoOrdenTrabajoEnum estado;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaFin;
    private BigDecimal totalTrabajo;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private EstadoPagoEnum estadoPago;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public OrdenTrabajo() {
    }

    // Constructor con parámetros
    public OrdenTrabajo(Long idOrdenTrabajo, String numeroOrden, Long idUsuario, Long idCliente,
                        TipoIngresoOrdenEnum tipoIngreso, Long idVehiculo, Long idComponente,
                        Short cantidadPicos, FuenteReferenciaClienteEnum fuenteReferencia,
                        Long idReferidor, LocalDateTime fechaIngreso, String problemaReportado,
                        String observacionesIngreso, EstadoOrdenTrabajoEnum estado,
                        LocalDateTime fechaEntrega, LocalDateTime fechaFin,
                        BigDecimal totalTrabajo, BigDecimal totalPagado, BigDecimal saldoPendiente,
                        EstadoPagoEnum estadoPago, boolean activo, LocalDateTime fechaCreacion) {
        this.idOrdenTrabajo       = idOrdenTrabajo;
        this.numeroOrden          = numeroOrden;
        this.idUsuario            = idUsuario;
        this.idCliente            = idCliente;
        this.tipoIngreso          = tipoIngreso;
        this.idVehiculo           = idVehiculo;
        this.idComponente         = idComponente;
        this.cantidadPicos        = cantidadPicos;
        this.fuenteReferencia     = fuenteReferencia;
        this.idReferidor          = idReferidor;
        this.fechaIngreso         = fechaIngreso;
        this.problemaReportado    = problemaReportado;
        this.observacionesIngreso = observacionesIngreso;
        this.estado               = estado;
        this.fechaEntrega         = fechaEntrega;
        this.fechaFin             = fechaFin;
        this.totalTrabajo         = totalTrabajo;
        this.totalPagado          = totalPagado;
        this.saldoPendiente       = saldoPendiente;
        this.estadoPago           = estadoPago;
        this.activo               = activo;
        this.fechaCreacion        = fechaCreacion;
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
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
//
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
//
    public TipoIngresoOrdenEnum getTipoIngreso() {
        return tipoIngreso;
    }
    public void setTipoIngreso(TipoIngresoOrdenEnum tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }
//
    public Long getIdVehiculo() {
        return idVehiculo;
    }
    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }
//
    public Long getIdComponente() {
        return idComponente;
    }
    public void setIdComponente(Long idComponente) {
        this.idComponente = idComponente;
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
    public Long getIdReferidor() {
        return idReferidor;
    }
    public void setIdReferidor(Long idReferidor) {
        this.idReferidor = idReferidor;
    }
//
    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }
    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
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
    public EstadoOrdenTrabajoEnum getEstado() {
        return estado;
    }
    public void setEstado(EstadoOrdenTrabajoEnum estado) {
        this.estado = estado;
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
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}