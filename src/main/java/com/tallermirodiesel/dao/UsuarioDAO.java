/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Usuario;

/**
 * @author elyrr
 */
public interface UsuarioDAO {

    // Crea un nuevo usuario y devuelve el id generado
    Long crear(Usuario usuario);

    // Actualiza los datos de un usuario existente
    boolean actualizar(Usuario usuario);

    // Actualiza solo la contraseña de un usuario (ya hasheada)
    boolean cambiarPassword(Long id, String hashNuevo);

    // Activa un usuario
    boolean activar(Long id);

    // Desactiva un usuario
    boolean desactivar(Long id);

    // Busca un usuario por su id
    Optional<Usuario> buscarPorId(Long id);

    // Busca un usuario por su username (clave para el login)
    Optional<Usuario> buscarPorUsername(String username);

    // Busca usuarios cuyo nombre, apellido o username coincida parcialmente
    List<Usuario> buscarPorNombreParcial(String filtro);
    
    // Lista todos los usuarios
    List<Usuario> listarTodos();

    // Lista solo los usuarios activos
    List<Usuario> listarActivos();

    // Lista solo los usuarios inactivos
    List<Usuario> listarInactivos();
}