package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Cliente;

/**
 * @author elyrr
 */
public interface ClienteService {

    // Crea o actualiza un cliente (la lógica persona/empresa se resuelve en el Impl)
    Long guardar(Cliente cliente);

    // Cambia el estado activo/inactivo
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

    // Búsqueda por texto y filtro opcional de activo
    List<Cliente> buscar(String q, Boolean activo);
}