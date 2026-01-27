/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author elyrr
 */
public class Servicio {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idServicio;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private boolean activo;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Servicio() {
    }

    // Constructor con parámetros
    public Servicio(Long idServicio, String codigo, String nombre, String descripcion, BigDecimal precioBase, boolean activo, LocalDateTime fechaCreacion) {
        this.idServicio = idServicio;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }
    
    // Getters y setters
    public Long getIdServicio() {
        return idServicio;
    }
//
    public void setIdServicio(Long idServicio) {
        this.idServicio = idServicio;
    }
//    
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
    public BigDecimal getPrecioBase() {
        return precioBase;
    }
    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
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