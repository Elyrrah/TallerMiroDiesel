/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.ClienteEmpresa;

/**
 * @author elyrr
 * Maneja exclusivamente datos específicos de empresas.
 */
public interface ClienteEmpresaDAO {

    // Crea o actualiza según exista el id.
    boolean guardar(ClienteEmpresa empresa);
    
    // Verifica si el cliente ya tiene datos de empresa
    boolean existePorIdCliente(Long idCliente);

    // Elimina el registro de empresa (caso excepcional)
    boolean eliminarPorIdCliente(Long idCliente);

    // Busca los datos de empresa por id_cliente
    Optional<ClienteEmpresa> buscarPorIdCliente(Long idCliente);
    
    // Lista todas los Clientes Empresas
    List<ClienteEmpresa> listarTodos();
}