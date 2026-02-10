/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.model;

/**
 * @author elyrr
 */
public class ClienteEmpresa extends Cliente {

    // Columnas específicas de clientes_empresa
    private String razonSocial;
    private String nombreFantasia;

    // Constructor vacío
    public ClienteEmpresa() {
        super();
    }

    // Constructor con parámetros (solo campos propios)
    public ClienteEmpresa(String razonSocial, String nombreFantasia) {
        super();
        this.razonSocial = razonSocial;
        this.nombreFantasia = nombreFantasia;
    }

    // Getters y setters
    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreFantasia() {
        return nombreFantasia;
    }

    public void setNombreFantasia(String nombreFantasia) {
        this.nombreFantasia = nombreFantasia;
    }
}
