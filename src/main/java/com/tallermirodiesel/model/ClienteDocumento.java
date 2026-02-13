/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class ClienteDocumento {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idClienteDocumento;
    private Long idCliente;
    private Long idTipoDocumento;
    private String numero;
    private boolean principal;
    private boolean activo; // indica si el documento está activo o no

    // Constructor vacío
    public ClienteDocumento() {
    }

    // Constructor con parámetros
    public ClienteDocumento(Long idClienteDocumento, Long idCliente, Long idTipoDocumento, String numero, boolean principal, boolean activo) {
        this.idClienteDocumento = idClienteDocumento;
        this.idCliente = idCliente;
        this.idTipoDocumento = idTipoDocumento;
        this.numero = numero;
        this.principal = principal;
        this.activo = activo;
    }
    
    // Getters y setters
    public Long getIdClienteDocumento() {
        return idClienteDocumento;
    }
    public void setIdClienteDocumento(Long idClienteDocumento) {
        this.idClienteDocumento = idClienteDocumento;
    }
//
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
//
    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }
    public void setIdTipoDocumento(Long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }
//
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
//
    public boolean isPrincipal() {
        return principal;
    }
    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
//
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
