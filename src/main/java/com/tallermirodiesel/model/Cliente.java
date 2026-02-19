/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import java.time.LocalDateTime;

/**
 * @author elyrr
 */
public class Cliente {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idCliente;
    private Long idUsuarioCreador;
    private Long idDistrito;
    private Long idLocalidad;
    private String telefono;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor con parámetros
    public Cliente(Long idCliente, Long idUsuarioCreador, Long idDistrito, Long idLocalidad, String telefono, boolean activo, LocalDateTime fechaCreacion) {
        this.idCliente = idCliente;
        this.idUsuarioCreador = idUsuarioCreador;
        this.idDistrito = idDistrito;
        this.idLocalidad = idLocalidad;
        this.telefono = telefono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
//
    public Long getIdUsuarioCreador() {
        return idUsuarioCreador;
    }
    public void setIdUsuarioCreador(Long idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }
//
    public Long getIdDistrito() {
        return idDistrito;
    }
    public void setIdDistrito(Long idDistrito) {
        this.idDistrito = idDistrito;
    }
//
    public Long getIdLocalidad() {
        return idLocalidad;
    }
    public void setIdLocalidad(Long idLocalidad) {
        this.idLocalidad = idLocalidad;
    }
//
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
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