/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.ClientePersona;

/**
 * @author elyrr
 * Maneja exclusivamente datos específicos de personas.
 */
public interface ClientePersonaDAO {

    // Crea o actualiza según exista el id.
    boolean guardar(ClientePersona persona);

    // Elimina el registro de persona (caso excepcional)
    boolean eliminarPorIdCliente(Long idCliente);
    
    // Verifica si el cliente ya tiene datos de persona
    boolean existePorIdCliente(Long idCliente);

    // Busca los datos de persona por id_cliente
    Optional<ClientePersona> buscarPorIdCliente(Long idCliente);
    
    // Lista todas los Clientes Personas
    List<ClientePersona> listarTodos();
}