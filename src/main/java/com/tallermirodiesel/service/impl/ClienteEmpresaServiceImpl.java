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

    // Constructor por defecto (usa implementación concreta)
    public ClienteEmpresaServiceImpl() {
        this.clienteEmpresaDAO = new ClienteEmpresaDAOImpl();
    }

    // Constructor para inyección (tests / configuración)
    public ClienteEmpresaServiceImpl(ClienteEmpresaDAO clienteEmpresaDAO) {
        this.clienteEmpresaDAO = clienteEmpresaDAO;
    }

    // Crea o actualiza los datos de empresa para un cliente
    @Override
    public boolean guardar(ClienteEmpresa empresa) {

        // Validación: objeto no nulo
        if (empresa == null) {
            throw new IllegalArgumentException("El objeto empresa no puede ser null.");
        }

        // Validación: idCliente obligatorio
        if (empresa.getIdCliente() == null || empresa.getIdCliente() <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        // Normalización / Validación: razonSocial obligatoria
        String razonSocial = empresa.getRazonSocial();
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            throw new IllegalArgumentException("La razón social es obligatoria.");
        }
        razonSocial = razonSocial.trim();
        empresa.setRazonSocial(razonSocial);

        // Normalización: nombreFantasia puede ser null, pero si viene vacío lo convertimos a null
        String nombreFantasia = empresa.getNombreFantasia();
        if (nombreFantasia != null) {
            nombreFantasia = nombreFantasia.trim();
            if (nombreFantasia.isEmpty()) {
                nombreFantasia = null;
            }
            empresa.setNombreFantasia(nombreFantasia);
        }

        // Guardado (create/update) delegando al DAO
        return clienteEmpresaDAO.guardar(empresa);
    }

    // Verifica si el cliente ya tiene datos de empresa
    @Override
    public boolean existePorIdCliente(Long idCliente) {

        // Validación: idCliente obligatorio
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        return clienteEmpresaDAO.existePorIdCliente(idCliente);
    }

    // Busca los datos de empresa por id_cliente
    @Override
    public Optional<ClienteEmpresa> buscarPorIdCliente(Long idCliente) {

        // Validación: idCliente obligatorio
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        return clienteEmpresaDAO.buscarPorIdCliente(idCliente);
    }

    // Lista todos los clientes empresa
    @Override
    public List<ClienteEmpresa> listarTodos() {
        return clienteEmpresaDAO.listarTodos();
    }

    // Elimina los datos de empresa por id_cliente (caso excepcional)
    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {

        // Validación: idCliente obligatorio
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("idCliente es obligatorio y debe ser mayor a 0.");
        }

        // (Opcional) validar existencia para dar un error más claro
        if (!clienteEmpresaDAO.existePorIdCliente(idCliente)) {
            throw new IllegalArgumentException("No existen datos de empresa para el cliente id: " + idCliente);
        }

        return clienteEmpresaDAO.eliminarPorIdCliente(idCliente);
    }
}
