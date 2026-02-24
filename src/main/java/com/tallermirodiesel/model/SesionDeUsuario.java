/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author elyrr
 */
public class SesionDeUsuario {

    // Datos básicos del usuario autenticado
    private Long idUsuario;
    private String username;
    private String nombre;
    private String apellido;
    private String nombreRol;
    private Set<String> permisos = new HashSet<>();    // Conjunto de códigos de permisos del usuario (ej: "SISTEMA.USUARIO.LEER")

    // Constructor vacío
    public SesionDeUsuario() {
    }

    // Constructor con parámetros — se construye a partir de un Usuario completo
    public SesionDeUsuario(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.username  = usuario.getUsername();
        this.nombre    = usuario.getNombre();
        this.apellido  = usuario.getApellido();

        if (usuario.getRol() != null) {
            this.nombreRol = usuario.getRol().getNombre();

            // Extraemos solo los nombres de los permisos y los guardamos en el Set
            if (usuario.getRol().getPermisos() != null) {
                usuario.getRol().getPermisos()
                        .forEach(p -> this.permisos.add(p.getNombre()));
            }
        }
    }

    // Devuelve true si el usuario tiene el permiso indicado
    public boolean tienePermiso(String codigoPermiso) {
        return permisos.contains(codigoPermiso);
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
    public String getNombreRol() {
        return nombreRol;
    }
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
//
    public Set<String> getPermisos() {
        return permisos;
    }
    public void setPermisos(Set<String> permisos) {
        this.permisos = permisos;
    }
}