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

    // Constructor por defecto (usa implementación concreta)
    public ClienteServiceImpl() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    // Constructor para inyección (tests / configuración)
    public ClienteServiceImpl(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // Crea o actualiza un cliente (la lógica persona/empresa se resuelve en el Impl)
    @Override
    public Long guardar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        // Normalización mínima (opcional)
        if (cliente.getTelefono() != null) {
            String tel = cliente.getTelefono().trim();
            cliente.setTelefono(tel.isEmpty() ? null : tel);
        }

        // Si viene con id, validamos existencia para evitar "actualizar" un id inexistente
        if (cliente.getIdCliente() != null && !clienteDAO.existePorId(cliente.getIdCliente())) {
            throw new IllegalArgumentException("No existe el cliente con id: " + cliente.getIdCliente());
        }

        // Guardado (insert/update) delegando al DAO
        return clienteDAO.guardar(cliente);
    }

    // Cambia el estado activo/inactivo
    @Override
    public boolean setActivo(Long idCliente, boolean activo) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }

        // (Opcional) validar existencia para dar un error más claro
        if (!clienteDAO.existePorId(idCliente)) {
            throw new IllegalArgumentException("No existe el cliente con id: " + idCliente);
        }

        return clienteDAO.setActivo(idCliente, activo);
    }

    // Verifica si existe un cliente por ID
    @Override
    public boolean existePorId(Long idCliente) {
        if (idCliente == null) {
            return false;
        }
        return clienteDAO.existePorId(idCliente);
    }

    // Busca un cliente por su ID
    @Override
    public Optional<Cliente> buscarPorId(Long idCliente) {
        if (idCliente == null) {
            return Optional.empty();
        }
        return clienteDAO.buscarPorId(idCliente);
    }

    // Lista todos los clientes
    @Override
    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    // Lista solo clientes activos
    @Override
    public List<Cliente> listarActivos() {
        return clienteDAO.listarActivos();
    }

    // Lista solo clientes inactivos
    @Override
    public List<Cliente> listarInactivos() {
        return clienteDAO.listarInactivos();
    }

    // Búsqueda por texto y filtro opcional de activo
    @Override
    public List<Cliente> buscar(String q, Boolean activo) {
        // Normalización mínima (el DAO también normaliza, pero mejor ser consistente)
        if (q != null) {
            q = q.trim();
            if (q.isEmpty()) {
                q = null;
            }
        }
        return clienteDAO.buscar(q, activo);
    }
}
