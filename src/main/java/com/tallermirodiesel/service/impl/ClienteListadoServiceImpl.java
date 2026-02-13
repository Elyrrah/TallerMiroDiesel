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
 * ServiceImpl: ClienteListadoServiceImpl
 * - Validaciones mínimas / normalización.
 * - El DAO hace el JOIN (consulta).
 */
public class ClienteListadoServiceImpl implements ClienteListadoService {

    private final ClienteListadoDAO clienteListadoDAO = new ClienteListadoDAOImpl();

    @Override
    public List<ClientePersonaListadoDTO> listarPersonas(String q, Boolean activo) {
        String qNorm = (q == null || q.trim().isEmpty()) ? null : q.trim();
        return clienteListadoDAO.listarPersonas(qNorm, activo);
    }

    @Override
    public List<ClienteEmpresaListadoDTO> listarEmpresas(String q, Boolean activo) {
        String qNorm = (q == null || q.trim().isEmpty()) ? null : q.trim();
        return clienteListadoDAO.listarEmpresas(qNorm, activo);
    }
}
