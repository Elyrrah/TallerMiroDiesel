/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Usuario;

/**
 * @author elyrr
 */
public interface UsuarioService {

    // Crea un nuevo usuario, recibe la contraseña en texto plano y la hashea internamente
    Long crear(Usuario usuario, String passwordPlana);

    // Actualiza los datos de un usuario existente
    boolean actualizar(Usuario usuario);

    // Cambia la contraseña de un usuario, recibe la nueva en texto plano y la hashea internamente
    boolean cambiarPassword(Long id, String passwordNueva);

    // Activa un usuario
    boolean activar(Long id);

    // Desactiva un usuario
    boolean desactivar(Long id);

    // Busca un usuario por su id
    Optional<Usuario> buscarPorId(Long id);

    // Busca un usuario por su username
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