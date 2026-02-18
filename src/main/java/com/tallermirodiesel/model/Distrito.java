/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Distrito {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idDistrito;
    private Long idDepartamento;
    private String nombre;
    private boolean activo;
    private String nombreDepartamento;    // Campo auxiliar para mostrar el nombre del Departamento con JOIN

    // Constructor vacío
    public Distrito() {
    }
    
    // Constructor con parámetros
    public Distrito(Long idDistrito, Long idDepartamento, String nombre, boolean activo) {
        this.idDistrito = idDistrito;
        this.idDepartamento = idDepartamento;
        this.nombre = nombre;
        this.activo = activo;
    }
    
    // Getters y setters
    public Long getIdDistrito() {
        return idDistrito;
    }
    public void setIdDistrito(Long idDistrito) {
        this.idDistrito = idDistrito;
    }
//
    public Long getIdDepartamento() {
        return idDepartamento;
    }
    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
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
    public String getNombreDepartamento() {
        return nombreDepartamento;
    }
    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
}