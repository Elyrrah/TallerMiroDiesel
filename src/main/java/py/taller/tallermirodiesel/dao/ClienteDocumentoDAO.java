/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.ClienteDocumento;

/**
 * @author elyrr
 * Maneja exclusivamente datos de documentos de clientes.
 */
public interface ClienteDocumentoDAO {

    // Crea o actualiza un documento de cliente
    boolean guardar(ClienteDocumento clienteDocumento);

    // Verifica si existe un documento por ID
    boolean existePorId(Long idClienteDocumento);

    // Cambia el estado activo/inactivo en una sola operación
    boolean setActivo(Long idClienteDocumento, boolean activo);

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
