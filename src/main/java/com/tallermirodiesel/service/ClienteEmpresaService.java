/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.ClienteEmpresa;

/**
 * @author elyrr
 */
public interface ClienteEmpresaService {

    // Crea o actualiza los datos de empresa para un cliente
    boolean guardar(ClienteEmpresa empresa);

    // Verifica si el cliente ya tiene datos de empresa
    boolean existePorIdCliente(Long idCliente);

    // Busca los datos de empresa por id_cliente
    Optional<ClienteEmpresa> buscarPorIdCliente(Long idCliente);

    // Lista todos los clientes empresa
    List<ClienteEmpresa> listarTodos();

    // Elimina los datos de empresa por id_cliente (caso excepcional)
    boolean eliminarPorIdCliente(Long idCliente);
}
