/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
    private String nombreUsuarioCreador;

    public ClientePersonaListadoDTO() {}

    // =========================
    // BLOQUE: Getters/setters base
    // =========================
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
//
    public Long getIdLocalidad() { return idLocalidad; }
    public void setIdLocalidad(Long idLocalidad) { this.idLocalidad = idLocalidad; }
//
    public Long getIdDistrito() { return idDistrito; }
    public void setIdDistrito(Long idDistrito) { this.idDistrito = idDistrito; }
//
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
//
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
//
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // =========================
    // BLOQUE: Getters/setters persona
    // =========================
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
//
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
//
    public String getApodo() { return apodo; }
    public void setApodo(String apodo) { this.apodo = apodo; }

    // =========================
    // BLOQUE: Getters/setters nombres (JOIN)
    // =========================
    public String getNombreDistrito() { return nombreDistrito; }
    public void setNombreDistrito(String nombreDistrito) { this.nombreDistrito = nombreDistrito; }
//
    public String getNombreLocalidad() { return nombreLocalidad; }
    public void setNombreLocalidad(String nombreLocalidad) { this.nombreLocalidad = nombreLocalidad; }
//
    public String getNombreUsuarioCreador() { return nombreUsuarioCreador; }
    public void setNombreUsuarioCreador(String nombreUsuarioCreador) { this.nombreUsuarioCreador = nombreUsuarioCreador; }

    // Helper opcional para JSP
    public String getNombreCompleto() {
        String n = (nombre == null) ? "" : nombre.trim();
        String a = (apellido == null) ? "" : apellido.trim();
        return (n + " " + a).trim();
    }

    // Método helper para JSTL
    public Date getFechaCreacionAsDate() {
        if (fechaCreacion == null) return null;
        return Date.from(fechaCreacion.atZone(ZoneId.systemDefault()).toInstant());
    }
}