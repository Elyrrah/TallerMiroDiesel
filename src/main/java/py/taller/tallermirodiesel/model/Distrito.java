/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Distrito {

    // Inicializamos las columnas de la tabla como variables privadas
    private Long idDistrito;
    private Long idCiudad;
    private String nombre;
    private boolean activo;

    // Campo auxiliar para mostrar el nombre de la ciudad con JOIN
    private String nombreCiudad;

    // Constructor vacío
    public Distrito() {
    }

    // Constructor con parámetros
    public Distrito(Long idDistrito, Long idCiudad, String nombre, boolean activo) {
        this.idDistrito = idDistrito;
        this.idCiudad = idCiudad;
        this.nombre = nombre;
        this.activo = activo;
    }

    // Constructor EXTENDIDO (útil para listados con JOIN)
    public Distrito(Long idDistrito, Long idCiudad, String nombre, boolean activo, String nombreCiudad) {
        this.idDistrito = idDistrito;
        this.idCiudad = idCiudad;
        this.nombre = nombre;
        this.activo = activo;
        this.nombreCiudad = nombreCiudad;
    }

    // Getters y setters
    public Long getIdDistrito() {
        return idDistrito;
    }
    public void setIdDistrito(Long idDistrito) {
        this.idDistrito = idDistrito;
    }
//
    public Long getIdCiudad() {
        return idCiudad;
    }
    public void setIdCiudad(Long idCiudad) {
        this.idCiudad = idCiudad;
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
    public String getNombreCiudad() {
        return nombreCiudad;
    }
    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }
}