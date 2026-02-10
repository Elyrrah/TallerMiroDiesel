/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import py.taller.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import py.taller.tallermirodiesel.dto.ClientePersonaListadoDTO;
/**
 * @author elyrr
 * Service: ClienteListadoService
 * - Expone listados (persona/empresa) listos para el servlet/JSP.
 */
public interface ClienteListadoService {

    List<ClientePersonaListadoDTO> listarPersonas(String q, Boolean activo);

    List<ClienteEmpresaListadoDTO> listarEmpresas(String q, Boolean activo);
}
