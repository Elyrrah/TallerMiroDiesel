/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ClientePersonaDAO;
import com.tallermirodiesel.dao.impl.ClientePersonaDAOImpl;
import com.tallermirodiesel.model.ClientePersona;
import com.tallermirodiesel.service.ClientePersonaService;

/**
 * @author elyrr
 */
public class ClientePersonaServiceImpl implements ClientePersonaService {

    private final ClientePersonaDAO clientePersonaDAO;

    public ClientePersonaServiceImpl() {
        this.clientePersonaDAO = new ClientePersonaDAOImpl();
    }

    public ClientePersonaServiceImpl(ClientePersonaDAO clientePersonaDAO) {
        this.clientePersonaDAO = clientePersonaDAO;
    }

    @Override
    public boolean guardar(ClientePersona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("El objeto persona no puede ser null.");
        }

        if (persona.getIdCliente() == null || persona.getIdCliente() <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        String nombre = persona.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        nombre = nombre.trim();
        persona.setNombre(nombre);

        String apellido = persona.getApellido();
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        apellido = apellido.trim();
        persona.setApellido(apellido);

        String apodo = persona.getApodo();
        if (apodo != null) {
            apodo = apodo.trim();
            if (apodo.isEmpty()) {
                apodo = null;
            }
            persona.setApodo(apodo);
        }

        return clientePersonaDAO.guardar(persona);
    }

    @Override
    public boolean existePorIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }
        return clientePersonaDAO.existePorIdCliente(idCliente);
    }

    @Override
    public Optional<ClientePersona> buscarPorIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }
        return clientePersonaDAO.buscarPorIdCliente(idCliente);
    }

    @Override
    public List<ClientePersona> listarTodos() {
        return clientePersonaDAO.listarTodos();
    }

    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }
        return clientePersonaDAO.eliminarPorIdCliente(idCliente);
    }
}