/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Cliente;

/**
 * @author elyrr
 * La lógica persona/empresa se maneja en DAOs separados y en el Service.
 */
public interface ClienteDAO {

    // Crea o actualiza según exista el id. Devuelve el id (nuevo o existente)
    Long guardar(Cliente cliente);

    // Cambia el estado activo/inactivo en una sola operación
    boolean setActivo(Long idCliente, boolean activo);

    // Verifica si existe un cliente por ID
    boolean existePorId(Long idCliente);

    // Busca un cliente por su ID
    Optional<Cliente> buscarPorId(Long idCliente);

    // Lista todos los clientes
    List<Cliente> listarTodos();

    // Lista solo clientes activos
    List<Cliente> listarActivos();

    // Lista solo clientes inactivos
    List<Cliente> listarInactivos();

    // Búsqueda simple por texto (q) y filtro opcional de activo (null = todos)
    List<Cliente> buscar(String q, Boolean activo);
}
