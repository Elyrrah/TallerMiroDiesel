/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dto;

import java.time.LocalDateTime;
import py.taller.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;

/**
 * @author elyrr
 * DTO: listado de Cliente Persona (clientes + clientes_persona).
 */
public class ClientePersonaListadoDTO {

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
    // BLOQUE: Persona (clientes_persona)
    // =========================
    private String nombre;
    private String apellido;
    private String apodo;

    // =========================
    // BLOQUE: Nombres (JOIN) para mostrar en JSP
    // =========================
    private String nombreDistrito;
    private String nombreLocalidad;
    private String nombreReferidor;

    public ClientePersonaListadoDTO() {}

    // =========================
    // BLOQUE: Getters/setters base
    // =========================
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

    // =========================
    // BLOQUE: Getters/setters persona
    // =========================
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }

    // =========================
    // BLOQUE: Getters/setters nombres (JOIN)
    // =========================
    public String getNombreDistrito() { return nombreDistrito; }
    public void setNombreDistrito(String nombreDistrito) { this.nombreDistrito = nombreDistrito; }

    public String getNombreLocalidad() { return nombreLocalidad; }
    public void setNombreLocalidad(String nombreLocalidad) { this.nombreLocalidad = nombreLocalidad; }

    public String getNombreReferidor() { return nombreReferidor; }
    public void setNombreReferidor(String nombreReferidor) { this.nombreReferidor = nombreReferidor; }

    // Helper opcional para JSP
    public String getNombreCompleto() {
        String n = (nombre == null) ? "" : nombre.trim();
        String a = (apellido == null) ? "" : apellido.trim();
        return (n + " " + a).trim();
    }
}
