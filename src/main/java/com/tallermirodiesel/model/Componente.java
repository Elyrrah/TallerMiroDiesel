/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Componente {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idComponente;
    private Long idTipoComponente;
    private Long idMarca;
    private Long idModelo;
    private String numeroSerie;
    private String observaciones;
    private boolean activo;
    private String nombreTipoComponente;    // Campos auxiliares para mostrar nombres con JOIN
    private String nombreMarca;    // Campos auxiliares para mostrar nombres con JOIN
    private String nombreModelo;    // Campos auxiliares para mostrar nombres con JOIN


    // Constructor vacío
    public Componente() {
    }

    // Getters y setters
    public Long getIdComponente() {
        return idComponente;
    }
    public void setIdComponente(Long idComponente) {
        this.idComponente = idComponente;
    }
//
    public Long getIdTipoComponente() {
        return idTipoComponente;
    }
    public void setIdTipoComponente(Long idTipoComponente) {
        this.idTipoComponente = idTipoComponente;
    }
//
    public Long getIdMarca() {
        return idMarca;
    }
    public void setIdMarca(Long idMarca) {
        this.idMarca = idMarca;
    }
//
    public Long getIdModelo() {
        return idModelo;
    }
    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }
//
    public String getNumeroSerie() {
        return numeroSerie;
    }
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
//
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
//
    public String getNombreTipoComponente() {
        return nombreTipoComponente;
    }
    public void setNombreTipoComponente(String nombreTipoComponente) {
        this.nombreTipoComponente = nombreTipoComponente;
    }
//
    public String getNombreMarca() {
        return nombreMarca;
    }
    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }
//
    public String getNombreModelo() {
        return nombreModelo;
    }
    public void setNombreModelo(String nombreModelo) {
        this.nombreModelo = nombreModelo;
    }
}