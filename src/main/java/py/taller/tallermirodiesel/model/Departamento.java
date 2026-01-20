/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;
/**
 * @author elyrr
 */
public class Departamento {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idDepartamento;
    private Long idPais;
    private String nombre;
    private boolean activo;
    private String nombrePais;

    // Constructor vacío
    public Departamento() {
    }

    // Getters y setters
    public Long getIdDepartamento() {
        return idDepartamento;
    }
    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }
//
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
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
//
    public String getNombrePais() {
        return nombrePais;
    }
    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }
    
 // Constructor con parámetros
    public Departamento(Long idDepartamento, Long idPais, String nombre, boolean activo) {
        this.idDepartamento = idDepartamento;
        this.idPais = idPais;
        this.nombre = nombre;
        this.activo = activo;
    }
}