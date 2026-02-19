/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import com.tallermirodiesel.dao.UsuarioDAO;
import com.tallermirodiesel.dao.impl.UsuarioDAOImpl;
import com.tallermirodiesel.model.Usuario;
import com.tallermirodiesel.service.AutenticacionService;
import com.tallermirodiesel.util.PasswordHash;

/**
 * @author elyrr
 */
public class AutenticacionServiceImpl implements AutenticacionService {

    // DAO para buscar usuarios en la base de datos
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    // Verifica las credenciales y devuelve el usuario completo si son correctas
    // Lanza una excepción si el usuario no existe, está inactivo o la contraseña es incorrecta
    @Override
    public Usuario login(String username, String password) {

        // 1. Verificamos que los campos no vengan vacíos
        if (username == null || username.isBlank()) {
            throw new RuntimeException("El nombre de usuario es obligatorio.");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria.");
        }

        // 2. Buscamos el usuario por su username
        Usuario usuario = usuarioDAO.buscarPorUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos."));

        // 3. Verificamos que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("El usuario se encuentra inactivo. Contacte al administrador.");
        }

        // 4. Verificamos la contraseña contra el hash guardado en la BD
        if (!PasswordHash.verificar(password, usuario.getPassword())) {
            throw new RuntimeException("Usuario o contraseña incorrectos.");
        }

        // 5. Todo correcto, devolvemos el usuario completo
        return usuario;
    }

    // Actualiza la contraseña del usuario autenticado
    // Lanza una excepción si la contraseña actual es incorrecta
    @Override
    public boolean cambiarPassword(Long idUsuario, String passwordActual, String passwordNueva) {

        // 1. Buscamos el usuario por su id
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 2. Verificamos que la contraseña actual sea correcta
        if (!PasswordHash.verificar(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta.");
        }

        // 3. Hasheamos la nueva contraseña y la guardamos
        String hashNuevo = PasswordHash.hashear(passwordNueva);
        return usuarioDAO.cambiarPassword(idUsuario, hashNuevo);
    }
}