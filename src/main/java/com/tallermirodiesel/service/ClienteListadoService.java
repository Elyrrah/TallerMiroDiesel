/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import com.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import com.tallermirodiesel.dto.ClientePersonaListadoDTO;
/**
 * @author elyrr
 * Service: ClienteListadoService
 * - Expone listados (persona/empresa) listos para el servlet/JSP.
 */
public interface ClienteListadoService {

    List<ClientePersonaListadoDTO> listarPersonas(String q, Boolean activo);

    List<ClienteEmpresaListadoDTO> listarEmpresas(String q, Boolean activo);
}
