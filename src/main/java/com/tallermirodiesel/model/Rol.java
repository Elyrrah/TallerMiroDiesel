/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import java.util.List;

/**
 * @author elyrr
 */
public class Rol {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idRol;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private List<Permiso> permisos;    // Lista de permisos asignados a este rol (se carga desde roles_permisos)

    // Constructor vacío
    public Rol() {
    }

    // Constructor con parámetros
    public Rol(Long idRol, String nombre, String descripcion, Boolean activo, List<Permiso> permisos) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.permisos = permisos;
    }

    // Getters y setters
    public Long getIdRol() {
        return idRol;
    }
    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }
//
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
//
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
//
    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
//
    public List<Permiso> getPermisos() {
        return permisos;
    }
    public void setPermisos(List<Permiso> permisos) {
        this.permisos = permisos;
    }
}