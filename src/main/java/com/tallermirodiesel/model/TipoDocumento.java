/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

import com.tallermirodiesel.model.enums.TipoDocumentoAplicaEnum;

/**
 * @author elyrr
 */
public class TipoDocumento {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idTipoDocumento;
    private String nombre;
    private String codigo;
    private TipoDocumentoAplicaEnum aplicaA;
    private boolean activo;

    // Constructor vacío
    public TipoDocumento() {
    }
    
    // Constructor con parámetros
    public TipoDocumento(Long idTipoDocumento, String nombre, String codigo, TipoDocumentoAplicaEnum aplicaA, boolean activo) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.codigo = codigo;
        this.aplicaA = aplicaA;
        this.activo = activo;
    }

    // Getters y setters
    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }
    public void setIdTipoDocumento(Long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }
//
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
//
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
//
    public TipoDocumentoAplicaEnum getAplicaA() {
        return aplicaA;
    }
    public void setAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        this.aplicaA = aplicaA;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}