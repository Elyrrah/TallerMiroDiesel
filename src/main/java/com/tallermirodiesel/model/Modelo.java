/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Modelo {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idModelo;
    private Long idMarca;
    private String nombre;
    private boolean activo;

// Campo auxiliar para mostrar el nombre de la marca con JOIN
    private String nombreMarca;
    
    // Constructor vacío
    public Modelo() {
    }

    // Constructor con parámetros
    public Modelo(Long idModelo, Long idMarca, String nombre, boolean activo) {
        this.idModelo = idModelo;
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.activo = activo;
    }

    // Constructor extendido
    public Modelo(Long idModelo, Long idMarca, String nombre, boolean activo, String nombreMarca) {
        this.idModelo = idModelo;
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.activo = activo;
        this.nombreMarca = nombreMarca;
    }

    // Getters y setters
    public Long getIdModelo() {
        return idModelo;
    }
    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }
//
    public Long getIdMarca() {
        return idMarca;
    }
    public void setIdMarca(Long idMarca) {
        this.idMarca = idMarca;
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
    public String getNombreMarca() {
        return nombreMarca;
    }
    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }
}