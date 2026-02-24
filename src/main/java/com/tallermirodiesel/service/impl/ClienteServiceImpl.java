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

    // Inicialización de la implementación del DAO para la gestión del núcleo de datos de clientes
    public ClienteServiceImpl() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    // Constructor para inyección de dependencias del DAO de base de clientes
    public ClienteServiceImpl(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // Validaciones de auditoría para el registro inicial y normalización de datos de contacto
    @Override
    public Long guardar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }

        // Valida que siempre haya un usuario creador al guardar un cliente nuevo
        if (cliente.getIdCliente() == null && cliente.getIdUsuarioCreador() == null) {
            throw new IllegalArgumentException("El usuario creador es obligatorio");
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

    // Lógica para modificar el estado de habilitación del cliente dentro del sistema
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

    // Lógica para verificar la existencia de un registro de cliente base por su ID
    @Override
    public boolean existePorId(Long idCliente) {
        if (idCliente == null) {
            return false;
        }
        return clienteDAO.existePorId(idCliente);
    }

    // Lógica para recuperar la información base de un cliente mediante su identificador único
    @Override
    public Optional<Cliente> buscarPorId(Long idCliente) {
        if (idCliente == null) {
            return Optional.empty();
        }
        return clienteDAO.buscarPorId(idCliente);
    }

    // Lógica para obtener el listado completo de clientes registrados sin filtros de estado
    @Override
    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    // Lógica para listar únicamente los clientes habilitados para nuevas órdenes de trabajo
    @Override
    public List<Cliente> listarActivos() {
        return clienteDAO.listarActivos();
    }

    // Lógica para listar los clientes que han sido suspendidos o dados de baja
    @Override
    public List<Cliente> listarInactivos() {
        return clienteDAO.listarInactivos();
    }

    // Lógica para realizar búsquedas generales filtradas por coincidencia de texto y estado
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