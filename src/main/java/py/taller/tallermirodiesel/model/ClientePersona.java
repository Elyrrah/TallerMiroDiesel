/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class ClientePersona extends Cliente {

    // Columnas específicas de clientes_persona
    private String nombre;
    private String apellido;
    private String apodo;

    // Constructor vacío
    public ClientePersona() {
        super();
    }

    // Constructor con parámetros (solo campos propios)
    public ClientePersona(String nombre, String apellido, String apodo) {
        super();
        this.nombre = nombre;
        this.apellido = apellido;
        this.apodo = apodo;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }
}
