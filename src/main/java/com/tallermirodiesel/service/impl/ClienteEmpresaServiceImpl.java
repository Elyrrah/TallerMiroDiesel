/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ClienteEmpresaDAO;
import com.tallermirodiesel.dao.impl.ClienteEmpresaDAOImpl;
import com.tallermirodiesel.model.ClienteEmpresa;
import com.tallermirodiesel.service.ClienteEmpresaService;

/**
 * @author elyrr
 */
public class ClienteEmpresaServiceImpl implements ClienteEmpresaService {

    private final ClienteEmpresaDAO clienteEmpresaDAO;

    // Inicialización de la implementación del DAO para la gestión de datos corporativos de clientes
    public ClienteEmpresaServiceImpl() {
        this.clienteEmpresaDAO = new ClienteEmpresaDAOImpl();
    }

    // Constructor para inyección de dependencias del DAO de clientes empresa
    public ClienteEmpresaServiceImpl(ClienteEmpresaDAO clienteEmpresaDAO) {
        this.clienteEmpresaDAO = clienteEmpresaDAO;
    }

    // Validaciones de obligatoriedad para razón social y normalización de nombres comerciales
    @Override
    public boolean guardar(ClienteEmpresa empresa) {
        if (empresa == null) {
            throw new IllegalArgumentException("El objeto empresa no puede ser null.");
        }

        if (empresa.getIdCliente() == null || empresa.getIdCliente() <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        String razonSocial = empresa.getRazonSocial();
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("La razón social es obligatoria.");
        }
        razonSocial = razonSocial.trim();
        empresa.setRazonSocial(razonSocial);

        String nombreFantasia = empresa.getNombreFantasia();
        if (nombreFantasia != null) {
            nombreFantasia = nombreFantasia.trim();
            if (nombreFantasia.isEmpty()) {
                nombreFantasia = null;
            }
            empresa.setNombreFantasia(nombreFantasia);
        }

        return clienteEmpresaDAO.guardar(empresa);
    }

    // Lógica para verificar si un cliente ya tiene registrados datos de empresa en el sistema
    @Override
    public boolean existePorIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }
        return clienteEmpresaDAO.existePorIdCliente(idCliente);
    }

    // Lógica para recuperar la información corporativa detallada asociada a un cliente
    @Override
    public Optional<ClienteEmpresa> buscarPorIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }
        return clienteEmpresaDAO.buscarPorIdCliente(idCliente);
    }

    // Lógica para obtener el listado completo de todas las empresas registradas
    @Override
    public List<ClienteEmpresa> listarTodos() {
        return clienteEmpresaDAO.listarTodos();
    }

    // Lógica para eliminar el registro corporativo de un cliente específico
    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }
        return clienteEmpresaDAO.eliminarPorIdCliente(idCliente);
    }
}