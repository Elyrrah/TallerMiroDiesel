/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class TipoComponente {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idTipoComponente;
    private String nombre;
    private String descripcion;
    private boolean activo;

    // Constructor vacío
    public TipoComponente() {
    }

    // Getters y setters
    public Long getIdTipoComponente() {
        return idTipoComponente;
    }
    public void setIdTipoComponente(Long idTipoComponente) {
        this.idTipoComponente = idTipoComponente;
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
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}