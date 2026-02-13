/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Pais {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idPais;
    private String nombre;
    private String iso2;
    private String iso3;
    private boolean activo;

    // Constructor vacío
    public Pais() {
    }

    // Constructor con parámetros
    public Pais(Long idPais, String nombre, String iso2, String iso3, boolean activo) {
        this.idPais = idPais;
        this.nombre = nombre;
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.activo = activo;
    }
    
    // Getters y setters
    public Long getIdPais() {
        return idPais;
    }
    public void setIdPais(Long idPais) {
        this.idPais = idPais;
    }
//
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
//
    public String getIso2() {
        return iso2;
    }
    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }
//
    public String getIso3() {
        return iso3;
    }
    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}