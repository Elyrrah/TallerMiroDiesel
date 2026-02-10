/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import py.taller.tallermirodiesel.dao.ClienteListadoDAO;
import py.taller.tallermirodiesel.dao.impl.ClienteListadoDAOImpl;
import py.taller.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import py.taller.tallermirodiesel.dto.ClientePersonaListadoDTO;
import py.taller.tallermirodiesel.service.ClienteListadoService;
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
