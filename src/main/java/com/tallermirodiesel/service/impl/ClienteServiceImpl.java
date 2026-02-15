/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ClienteDAO;
import com.tallermirodiesel.dao.impl.ClienteDAOImpl;
import com.tallermirodiesel.model.Cliente;
import com.tallermirodiesel.service.ClienteService;

/**
 * @author elyrr
 */
public class ClienteServiceImpl implements ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteServiceImpl() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public ClienteServiceImpl(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    @Override
    public Long guardar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        if (cliente.getTelefono() != null) {
            String tel = cliente.getTelefono().trim();
            cliente.setTelefono(tel.isEmpty() ? null : tel);
        }

        if (cliente.getIdCliente() != null && !clienteDAO.existePorId(cliente.getIdCliente())) {
            throw new IllegalArgumentException("No existe el cliente con id: " + cliente.getIdCliente());
        }

        return clienteDAO.guardar(cliente);
    }

    @Override
    public boolean setActivo(Long idCliente, boolean activo) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }

        if (!clienteDAO.existePorId(idCliente)) {
            throw new IllegalArgumentException("No existe el cliente con id: " + idCliente);
        }

        return clienteDAO.setActivo(idCliente, activo);
    }

    @Override
    public boolean existePorId(Long idCliente) {
        if (idCliente == null) {
            return false;
        }
        return clienteDAO.existePorId(idCliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long idCliente) {
        if (idCliente == null) {
            return Optional.empty();
        }
        return clienteDAO.buscarPorId(idCliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    @Override
    public List<Cliente> listarActivos() {
        return clienteDAO.listarActivos();
    }

    @Override
    public List<Cliente> listarInactivos() {
        return clienteDAO.listarInactivos();
    }

    @Override
    public List<Cliente> buscar(String q, Boolean activo) {
        if (q != null) {
            q = q.trim();
            if (q.isEmpty()) {
                q = null;
            }
        }
        return clienteDAO.buscar(q, activo);
    }
}