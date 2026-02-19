/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Rol;

/**
 * @author elyrr
 */
public interface RolDAO {

    // Busca un rol por su id, con su lista de permisos ya cargada
    Optional<Rol> buscarPorId(Long id);

    // Lista todos los roles
    List<Rol> listarTodos();

    // Lista solo los roles activos
    List<Rol> listarActivos();
}