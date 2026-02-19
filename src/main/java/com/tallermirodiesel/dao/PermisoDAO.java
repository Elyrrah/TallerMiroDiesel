/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Permiso;

/**
 * @author elyrr
 */
public interface PermisoDAO {

    // Busca un permiso por su id
    Optional<Permiso> buscarPorId(Long id);

    // Lista todos los permisos asignados a un rol
    List<Permiso> listarPorRol(Long idRol);
}