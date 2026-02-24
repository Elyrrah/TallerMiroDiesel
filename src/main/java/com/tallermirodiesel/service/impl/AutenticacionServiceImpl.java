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

    private final UsuarioDAO usuarioDAO;

    // Inicialización de la implementación del DAO para el servicio de seguridad y validación de credenciales
    public AutenticacionServiceImpl() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    // Lógica para la validación de acceso al sistema mediante verificación de credenciales y estado del usuario
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

    // Lógica para la actualización de contraseñas con validación previa de la identidad actual
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