/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;

import java.time.LocalDateTime;
import py.taller.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;

/**
 * @author elyrr
 */
public class Cliente {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idCliente;
    private Long idLocalidad;
    private Long idDistrito;
    private Long idClienteReferidor;
    private FuenteReferenciaClienteEnum fuenteReferencia;
    private String telefono;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor con parámetros
    public Cliente(Long idCliente, Long idDistrito, Long idLocalidad, Long idClienteReferidor, FuenteReferenciaClienteEnum fuenteReferencia, String telefono, boolean activo, LocalDateTime fechaCreacion) {
        this.idCliente = idCliente;
        this.idDistrito = idDistrito;
        this.idLocalidad = idLocalidad;
        this.idClienteReferidor = idClienteReferidor;
        this.fuenteReferencia = fuenteReferencia;
        this.telefono = telefono;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    public Long getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    //
    public Long getIdDistrito() {
        return idDistrito;
    }
    public void setIdDistrito(Long idDistrito) {
        this.idDistrito = idDistrito;
    }
    //
    public Long getIdLocalidad() {
        return idLocalidad;
    }
    public void setIdLocalidad(Long idLocalidad) {
        this.idLocalidad = idLocalidad;
    }
    //
    public Long getIdClienteReferidor() {
        return idClienteReferidor;
    }
    public void setIdClienteReferidor(Long idClienteReferidor) {
        this.idClienteReferidor = idClienteReferidor;
    }
    //
    public FuenteReferenciaClienteEnum getFuenteReferencia() {
        return fuenteReferencia;
    }
    public void setFuenteReferencia(FuenteReferenciaClienteEnum fuenteReferencia) {
        this.fuenteReferencia = fuenteReferencia;
    }
    //
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    //
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    //
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
