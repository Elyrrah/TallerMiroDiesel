/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class Marca {
    
    // Inicializamos las columnas de la tabla como variables privadas
    private Long idMarca;
    private String nombre;
    private boolean activo;

    // Constructor vacío
    public Marca() {
    }
    
    // Constructor con parámetros
    public Marca(Long idMarca, String nombre, boolean activo) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.activo = activo;
    }

    // Getters y setters
    public Long getIdMarca() {
        return idMarca;
    }
    public void setIdMarca(Long idMarca) {
        this.idMarca = idMarca;
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
}