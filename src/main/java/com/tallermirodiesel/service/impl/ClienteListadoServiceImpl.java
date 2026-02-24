/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import com.tallermirodiesel.dao.ClienteListadoDAO;
import com.tallermirodiesel.dao.impl.ClienteListadoDAOImpl;
import com.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import com.tallermirodiesel.dto.ClientePersonaListadoDTO;
import com.tallermirodiesel.service.ClienteListadoService;

/**
 * @author elyrr
 */
public class ClienteListadoServiceImpl implements ClienteListadoService {

    private final ClienteListadoDAO clienteListadoDAO;

    // Inicialización de la implementación del DAO especializado en consultas complejas y reportes de clientes
    public ClienteListadoServiceImpl() {
        this.clienteListadoDAO = new ClienteListadoDAOImpl();
    }

    // Lógica para listar y filtrar personas físicas aplicando normalización de parámetros de búsqueda
    @Override
    public List<ClientePersonaListadoDTO> listarPersonas(String q, Boolean activo) {
        String qNorm = (q == null || q.trim().isEmpty()) ? null : q.trim();
        return clienteListadoDAO.listarPersonas(qNorm, activo);
    }

    // Lógica para listar y filtrar empresas aplicando normalización de parámetros de búsqueda
    @Override
    public List<ClienteEmpresaListadoDTO> listarEmpresas(String q, Boolean activo) {
        String qNorm = (q == null || q.trim().isEmpty()) ? null : q.trim();
        return clienteListadoDAO.listarEmpresas(qNorm, activo);
    }
}