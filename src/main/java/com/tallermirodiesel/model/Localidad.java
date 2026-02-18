/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;
/**
 * @author elyrr
 */
public class Localidad {
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idLocalidad;
    private Long idDistrito;
    private String nombre;
    private boolean activo;
    // Campo auxiliar para mostrar el nombre del distrito con JOIN
    private String nombreDistrito;
    // Constructor vacío
    public Localidad() {
    }
    // Constructor con parámetros
    public Localidad(Long idLocalidad, Long idDistrito, String nombre, boolean activo) {
        this.idLocalidad = idLocalidad;
        this.idDistrito = idDistrito;
        this.nombre = nombre;
        this.activo = activo;
    }
    // Getters y setters
    public Long getIdLocalidad() {
        return idLocalidad;
    }
    public void setIdLocalidad(Long idLocalidad) {
        this.idLocalidad = idLocalidad;
    }
//
    public Long getIdDistrito() {
        return idDistrito;
    }
    public void setIdDistrito(Long idDistrito) {
        this.idDistrito = idDistrito;
    }
//
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
//
    public String getNombreDistrito() {
        return nombreDistrito;
    }
    public void setNombreDistrito(String nombreDistrito) {
        this.nombreDistrito = nombreDistrito;
    }
}