/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.UsuarioDAO;
import com.tallermirodiesel.dao.impl.UsuarioDAOImpl;
import com.tallermirodiesel.model.Usuario;
import com.tallermirodiesel.service.UsuarioService;
import com.tallermirodiesel.util.PasswordHash;

/**
 * @author elyrr
 */
public class UsuarioServiceImpl implements UsuarioService {

    // DAO para acceder a la base de datos
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    // Crea un nuevo usuario, recibe la contraseña en texto plano y la hashea internamente
    @Override
    public Long crear(Usuario usuario, String passwordPlana) {

        // 1. Validamos que los campos obligatorios no vengan vacíos
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new RuntimeException("El nombre de usuario es obligatorio.");
        }
        if (passwordPlana == null || passwordPlana.isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria.");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio.");
        }
        if (usuario.getRol() == null) {
            throw new RuntimeException("El rol es obligatorio.");
        }

        // 2. Verificamos que el username no esté ya en uso
        if (usuarioDAO.buscarPorUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        // 3. Hasheamos la contraseña y la asignamos al usuario
        usuario.setPassword(PasswordHash.hashear(passwordPlana));

        // 4. Guardamos el usuario en la BD y devolvemos el id generado
        return usuarioDAO.crear(usuario);
    }

    // Actualiza los datos de un usuario existente
    @Override
    public boolean actualizar(Usuario usuario) {

        // 1. Validamos que los campos obligatorios no vengan vacíos
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }
        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio.");
        }
        if (usuario.getRol() == null) {
            throw new RuntimeException("El rol es obligatorio.");
        }

        // 2. Verificamos que el usuario exista
        usuarioDAO.buscarPorId(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 3. Actualizamos el usuario en la BD
        return usuarioDAO.actualizar(usuario);
    }

    // Cambia la contraseña de un usuario, recibe la nueva en texto plano y la hashea internamente
    @Override
    public boolean cambiarPassword(Long id, String passwordNueva) {

        // 1. Validamos que la nueva contraseña no venga vacía
        if (passwordNueva == null || passwordNueva.isBlank()) {
            throw new RuntimeException("La nueva contraseña es obligatoria.");
        }

        // 2. Verificamos que el usuario exista
        usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 3. Hasheamos la nueva contraseña y la guardamos
        return usuarioDAO.cambiarPassword(id, PasswordHash.hashear(passwordNueva));
    }

    // Activa un usuario
    @Override
    public boolean activar(Long id) {
        usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return usuarioDAO.activar(id);
    }

    // Desactiva un usuario
    @Override
    public boolean desactivar(Long id) {
        usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return usuarioDAO.desactivar(id);
    }

    // Busca un usuario por su id
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    // Busca un usuario por su username
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioDAO.buscarPorUsername(username);
    }

    // Busca usuarios cuyo nombre, apellido o username coincida parcialmente
    @Override
    public List<Usuario> buscarPorNombreParcial(String filtro) {
        return usuarioDAO.buscarPorNombreParcial(filtro);
    }

    // Lista todos los usuarios
    @Override
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    // Lista solo los usuarios activos
    @Override
    public List<Usuario> listarActivos() {
        return usuarioDAO.listarActivos();
    }

    // Lista solo los usuarios inactivos
    @Override
    public List<Usuario> listarInactivos() {
        return usuarioDAO.listarInactivos();
    }
}