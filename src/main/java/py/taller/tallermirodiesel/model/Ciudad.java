/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;
/**
 * @author elyrr
 */
public class Ciudad {
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idCiudad;
    private Long idDepartamento;
    private String nombre;
    private boolean activo;
    private String nombreDepartamento;

    // Constructor vacío
    public Ciudad() {
    }

    // Getters y setters
    public Long getIdCiudad() {
        return idCiudad;
    }
    public void setIdCiudad(Long idCiudad) {
        this.idCiudad = idCiudad;
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
    
 // Constructor con parámetros
    public Ciudad(Long idCiudad, Long idDepartamento, String nombre, boolean activo) {
        this.idCiudad = idCiudad;
        this.idDepartamento = idDepartamento;
        this.nombre = nombre;
        this.activo = activo;
    }
}
