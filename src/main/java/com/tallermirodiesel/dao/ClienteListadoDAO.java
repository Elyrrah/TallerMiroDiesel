/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import com.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import com.tallermirodiesel.dto.ClientePersonaListadoDTO;
/**
 * @author elyrr
 * DAO: ClienteListadoDAO
 * - Consultas específicas de listados con JOIN (evita N+1).
 */
public interface ClienteListadoDAO {

    /**
     * Lista clientes PERSONA (clientes + clientes_persona) con filtros opcionales.
     * @param q      texto opcional (nombre/apellido/apodo/telefono)
     * @param activo null = todos, true = activos, false = inactivos
     * @return 
     */
    List<ClientePersonaListadoDTO> listarPersonas(String q, Boolean activo);

    /**
     * Lista clientes EMPRESA (clientes + clientes_empresa) con filtros opcionales.
     * @param q      texto opcional (razon_social/nombre_fantasia/telefono)
     * @param activo null = todos, true = activos, false = inactivos
     * @return 
     */
    List<ClienteEmpresaListadoDTO> listarEmpresas(String q, Boolean activo);
}
