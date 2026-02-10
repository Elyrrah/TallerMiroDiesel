/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.ClientePersonaDAO;
import py.taller.tallermirodiesel.dao.impl.ClientePersonaDAOImpl;
import py.taller.tallermirodiesel.model.ClientePersona;
import py.taller.tallermirodiesel.service.ClientePersonaService;

/**
 * @author elyrr
 */
public class ClientePersonaServiceImpl implements ClientePersonaService {

    private final ClientePersonaDAO clientePersonaDAO;

    // Constructor por defecto (usa implementación concreta)
    public ClientePersonaServiceImpl() {
        this.clientePersonaDAO = new ClientePersonaDAOImpl();
    }

    // Constructor para inyección (tests / configuración)
    public ClientePersonaServiceImpl(ClientePersonaDAO clientePersonaDAO) {
        this.clientePersonaDAO = clientePersonaDAO;
    }

    // Crea o actualiza los datos de persona para un cliente
    @Override
    public boolean guardar(ClientePersona persona) {

        // Validación: objeto no nulo
        if (persona == null) {
            throw new IllegalArgumentException("El objeto persona no puede ser null.");
        }

        // Validación: idCliente obligatorio
        if (persona.getIdCliente() == null || persona.getIdCliente() <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        // Normalización / Validación: nombre obligatorio
        String nombre = persona.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        nombre = nombre.trim();
        persona.setNombre(nombre);

        // Normalización / Validación: apellido obligatorio
        String apellido = persona.getApellido();
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        apellido = apellido.trim();
        persona.setApellido(apellido);

        // Normalización: apodo puede ser null, pero si viene vacío lo convertimos a null
        String apodo = persona.getApodo();
        if (apodo != null) {
            apodo = apodo.trim();
            if (apodo.isEmpty()) {
                apodo = null;
            }
            persona.setApodo(apodo);
        }

        // Guardado (create/update) delegando al DAO
        return clientePersonaDAO.guardar(persona);
    }

    // Verifica si el cliente ya tiene datos de persona
    @Override
    public boolean existePorIdCliente(Long idCliente) {

        // Validación: idCliente obligatorio
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        return clientePersonaDAO.existePorIdCliente(idCliente);
    }

    // Busca los datos de persona por id_cliente
    @Override
    public Optional<ClientePersona> buscarPorIdCliente(Long idCliente) {

        // Validación: idCliente obligatorio
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        return clientePersonaDAO.buscarPorIdCliente(idCliente);
    }

    // Lista todos los clientes persona
    @Override
    public List<ClientePersona> listarTodos() {
        return clientePersonaDAO.listarTodos();
    }

    // Elimina los datos de persona por id_cliente (caso excepcional)
    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {

        // Validación: idCliente obligatorio
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        // (Opcional) validar existencia para dar un error más claro
        if (!clientePersonaDAO.existePorIdCliente(idCliente)) {
            throw new IllegalArgumentException("No existen datos de persona para el cliente id: " + idCliente);
        }

        return clientePersonaDAO.eliminarPorIdCliente(idCliente);
    }
}
