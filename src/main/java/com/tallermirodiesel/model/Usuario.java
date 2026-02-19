/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import java.time.LocalDateTime;

/**
 * @author elyrr
 */
public class Usuario {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idUsuario;
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private Long idTipoDocumento;
    private String numeroDocumento;
    private String email;
    private String telefono;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private Rol rol;    // En vez de guardar solo el id_rol, guardamos el objeto Rol completo

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(Long idUsuario, String username, String password, String nombre,
                   String apellido, Long idTipoDocumento, String numeroDocumento,
                   String email, String telefono, Boolean activo,
                   LocalDateTime fechaCreacion, Rol rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idTipoDocumento = idTipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.rol = rol;
    }

    // Getters y setters
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
//
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
//
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
//
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
//
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
//
    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }
    public void setIdTipoDocumento(Long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }
//
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }
//
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
//
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
//
    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
//
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
//
    public Rol getRol() {
        return rol;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
}