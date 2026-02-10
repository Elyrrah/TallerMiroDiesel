/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import py.taller.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;

/**
 * @author elyrr
 * DTO: listado de Cliente Empresa (clientes + clientes_empresa).
 */
public class ClienteEmpresaListadoDTO {

    // =========================
    // BLOQUE: Base (clientes)
    // =========================
    private Long idCliente;
    private Long idLocalidad;
    private Long idDistrito;
    private Long idClienteReferidor;
    private FuenteReferenciaClienteEnum fuenteReferencia;
    private String telefono;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // =========================
    // BLOQUE: Empresa (clientes_empresa)
    // =========================
    private String razonSocial;
    private String nombreFantasia;

    // =========================
    // BLOQUE: Nombres (JOIN) para mostrar en JSP
    // =========================
    private String nombreDistrito;
    private String nombreLocalidad;
    private String nombreReferidor;

    public ClienteEmpresaListadoDTO() {}

    // Base
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Long getIdLocalidad() { return idLocalidad; }
    public void setIdLocalidad(Long idLocalidad) { this.idLocalidad = idLocalidad; }

    public Long getIdDistrito() { return idDistrito; }
    public void setIdDistrito(Long idDistrito) { this.idDistrito = idDistrito; }

    public Long getIdClienteReferidor() { return idClienteReferidor; }
    public void setIdClienteReferidor(Long idClienteReferidor) { this.idClienteReferidor = idClienteReferidor; }

    public FuenteReferenciaClienteEnum getFuenteReferencia() { return fuenteReferencia; }
    public void setFuenteReferencia(FuenteReferenciaClienteEnum fuenteReferencia) { this.fuenteReferencia = fuenteReferencia; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Empresa
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreFantasia() { return nombreFantasia; }
    public void setNombreFantasia(String nombreFantasia) { this.nombreFantasia = nombreFantasia; }

    // Nombres JOIN
    public String getNombreDistrito() { return nombreDistrito; }
    public void setNombreDistrito(String nombreDistrito) { this.nombreDistrito = nombreDistrito; }

    public String getNombreLocalidad() { return nombreLocalidad; }
    public void setNombreLocalidad(String nombreLocalidad) { this.nombreLocalidad = nombreLocalidad; }

    public String getNombreReferidor() { return nombreReferidor; }
    public void setNombreReferidor(String nombreReferidor) { this.nombreReferidor = nombreReferidor; }

    // Helper opcional: nombre principal para mostrar
    public String getNombre() {
        if (nombreFantasia != null && !nombreFantasia.trim().isBlank()) return nombreFantasia.trim();
        if (razonSocial != null && !razonSocial.trim().isBlank()) return razonSocial.trim();
        return "";
    }
    
    // Método helper para JSTL
    public Date getFechaCreacionAsDate() {
        if (fechaCreacion == null) return null;
        return Date.from(fechaCreacion.atZone(ZoneId.systemDefault()).toInstant());
    }
}