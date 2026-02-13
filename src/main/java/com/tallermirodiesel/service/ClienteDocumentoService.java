/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.ClienteDocumento;

/**
 * @author elyrr
 */
public interface ClienteDocumentoService {

    // Crea o actualiza un documento de cliente
    boolean guardar(ClienteDocumento clienteDocumento);

    // Verifica si existe un documento por ID
    boolean existePorId(Long idClienteDocumento);

    // Cambia el estado activo/inactivo
    boolean setActivo(Long idClienteDocumento, boolean activo);

    // Atajos de negocio
    boolean activar(Long idClienteDocumento);

    boolean desactivar(Long idClienteDocumento);

    // Define un documento como principal para el cliente
    boolean definirPrincipal(Long idCliente, Long idClienteDocumento);

    // Verifica si ya existe un documento con ese tipo y número para el cliente
    boolean existePorClienteTipoNumero(
            Long idCliente,
            Long idTipoDocumento,
            String numero
    );

    // Busca un documento por su ID
    Optional<ClienteDocumento> buscarPorId(Long idClienteDocumento);

    // Lista todos los documentos de un cliente
    List<ClienteDocumento> listarPorCliente(Long idCliente);

    // Lista solo los documentos activos de un cliente
    List<ClienteDocumento> listarActivosPorCliente(Long idCliente);

    // Obtiene el documento principal activo de un cliente
    Optional<ClienteDocumento> obtenerPrincipalPorCliente(Long idCliente);
}
