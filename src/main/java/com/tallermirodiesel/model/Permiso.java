/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Permiso {
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idPermiso;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    
    // Constructor vacío
    public Permiso() {
    }

    // Constructor con parámetros
    public Permiso(Long idPermiso, String nombre, String descripcion, Boolean activo) {
        this.idPermiso = idPermiso;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // Getters y setters

    public Long getIdPermiso() {
        return idPermiso;
    }
    public void setIdPermiso(Long idPermiso) {
        this.idPermiso = idPermiso;
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
}