/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.ClientePersona;

/**
 * @author elyrr
 */
public interface ClientePersonaService {

    // Crea o actualiza los datos de persona para un cliente
    boolean guardar(ClientePersona persona);

    // Verifica si el cliente ya tiene datos de persona
    boolean existePorIdCliente(Long idCliente);

    // Busca los datos de persona por id_cliente
    Optional<ClientePersona> buscarPorIdCliente(Long idCliente);

    // Lista todos los clientes persona
    List<ClientePersona> listarTodos();

    // Elimina los datos de persona por id_cliente (caso excepcional)
    boolean eliminarPorIdCliente(Long idCliente);
}
