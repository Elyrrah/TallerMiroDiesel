/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import com.tallermirodiesel.model.enums.TipoVehiculoEnum;

/**
 * @author elyrr
 */
public class Vehiculo {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idVehiculo;
    private String placa;
    private Long idMarca;
    private Long idModelo;
    private Short anio;
    private TipoVehiculoEnum tipoVehiculo;
    private String observaciones;
    private boolean activo;
    private String nombreMarca;    // Campos auxiliares para mostrar nombres con JOIN
    private String nombreModelo;    // Campos auxiliares para mostrar nombres con JOIN

    // Constructor vacío
    public Vehiculo() {
    }

    // Getters y setters
    public Long getIdVehiculo() {
        return idVehiculo;
    }
    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }
//
    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
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
    public Short getAnio() {
        return anio;
    }
    public void setAnio(Short anio) {
        this.anio = anio;
    }
//
    public TipoVehiculoEnum getTipoVehiculo() {
        return tipoVehiculo;
    }
    public void setTipoVehiculo(TipoVehiculoEnum tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
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