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

    private final UsuarioDAO usuarioDAO;

    // Inicialización de la implementación del DAO para el servicio de gestión de usuarios y seguridad
    public UsuarioServiceImpl() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    // Validaciones para registrar un nuevo usuario con cifrado de contraseña
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

    // Validaciones para actualizar los datos personales y de rol de un usuario
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

    // Lógica para actualizar la credencial de acceso mediante cifrado seguro
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

    // Validaciones para habilitar el acceso de un usuario al sistema
    @Override
    public boolean activar(Long id) {
        usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return usuarioDAO.activar(id);
    }

    // Validaciones para restringir el acceso de un usuario al sistema
    @Override
    public boolean desactivar(Long id) {
        usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return usuarioDAO.desactivar(id);
    }

    // Lógica para obtener la información de un usuario por su identificador único
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    // Lógica para localizar un usuario mediante su nombre de cuenta (username)
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioDAO.buscarPorUsername(username);
    }

    // Lógica para filtrar usuarios por coincidencias en nombres, apellidos o cuenta
    @Override
    public List<Usuario> buscarPorNombreParcial(String filtro) {
        return usuarioDAO.buscarPorNombreParcial(filtro);
    }

    // Lógica para obtener la lista completa de usuarios registrados en el sistema
    @Override
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    // Lógica para listar únicamente los usuarios con permisos de acceso vigentes
    @Override
    public List<Usuario> listarActivos() {
        return usuarioDAO.listarActivos();
    }

    // Lógica para listar únicamente los usuarios con acceso restringido
    @Override
    public List<Usuario> listarInactivos() {
        return usuarioDAO.listarInactivos();
    }
}