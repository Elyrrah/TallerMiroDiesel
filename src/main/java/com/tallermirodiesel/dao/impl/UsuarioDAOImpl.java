/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.RolDAO;
import com.tallermirodiesel.dao.UsuarioDAO;
import com.tallermirodiesel.model.Usuario;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    // Usamos RolDAO para cargar el rol completo (con permisos) de cada usuario
    private final RolDAO rolDAO = new RolDAOImpl();

    // =============================================
    // SQL
    // =============================================
    private static final String SQL_CREAR = """
            INSERT INTO public.usuarios
                (username, password, nombre, apellido, id_tipo_documento,
                 numero_documento, email, telefono, id_rol, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id_usuario
            """;

    private static final String SQL_ACTUALIZAR = """
            UPDATE public.usuarios
            SET nombre            = ?,
                apellido          = ?,
                id_tipo_documento = ?,
                numero_documento  = ?,
                email             = ?,
                telefono          = ?,
                id_rol            = ?,
                activo            = ?
            WHERE id_usuario = ?
            """;

    private static final String SQL_CAMBIAR_PASSWORD = """
            UPDATE public.usuarios
            SET password = ?
            WHERE id_usuario = ?
            """;

    private static final String SQL_ACTIVAR = """
            UPDATE public.usuarios
            SET activo = true
            WHERE id_usuario = ?
            """;

    private static final String SQL_DESACTIVAR = """
            UPDATE public.usuarios
            SET activo = false
            WHERE id_usuario = ?
            """;

    private static final String SQL_BUSCAR_POR_ID = """
            SELECT id_usuario, username, password, nombre, apellido,
                   id_tipo_documento, numero_documento, email, telefono,
                   id_rol, activo, fecha_creacion
            FROM public.usuarios
            WHERE id_usuario = ?
            """;

    private static final String SQL_BUSCAR_POR_USERNAME = """
            SELECT id_usuario, username, password, nombre, apellido,
                   id_tipo_documento, numero_documento, email, telefono,
                   id_rol, activo, fecha_creacion
            FROM public.usuarios
            WHERE UPPER(TRIM(username)) = UPPER(TRIM(?))
            """;

    private static final String SQL_BUSCAR_POR_NOMBRE_PARCIAL = """
            SELECT id_usuario, username, password, nombre, apellido,
                   id_tipo_documento, numero_documento, email, telefono,
                   id_rol, activo, fecha_creacion
            FROM public.usuarios
            WHERE UPPER(nombre)   LIKE UPPER(?)
               OR UPPER(apellido) LIKE UPPER(?)
               OR UPPER(username) LIKE UPPER(?)
            ORDER BY apellido ASC, nombre ASC
            """;
    
    private static final String SQL_LISTAR_TODOS = """
            SELECT id_usuario, username, password, nombre, apellido,
                   id_tipo_documento, numero_documento, email, telefono,
                   id_rol, activo, fecha_creacion
            FROM public.usuarios
            ORDER BY apellido ASC, nombre ASC
            """;

    private static final String SQL_LISTAR_ACTIVOS = """
            SELECT id_usuario, username, password, nombre, apellido,
                   id_tipo_documento, numero_documento, email, telefono,
                   id_rol, activo, fecha_creacion
            FROM public.usuarios
            WHERE activo = true
            ORDER BY apellido ASC, nombre ASC
            """;

    private static final String SQL_LISTAR_INACTIVOS = """
            SELECT id_usuario, username, password, nombre, apellido,
                   id_tipo_documento, numero_documento, email, telefono,
                   id_rol, activo, fecha_creacion
            FROM public.usuarios
            WHERE activo = false
            ORDER BY apellido ASC, nombre ASC
            """;

    // =============================================
    // MAPEO
    // =============================================

    // Mapea un ResultSet a un objeto Usuario (el rol se carga por separado)
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setIdTipoDocumento(rs.getLong("id_tipo_documento"));
        u.setNumeroDocumento(rs.getString("numero_documento"));
        u.setEmail(rs.getString("email"));
        u.setTelefono(rs.getString("telefono"));
        u.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            u.setFechaCreacion(ts.toLocalDateTime());
        }

        // Cargamos el rol completo (con sus permisos) usando RolDAO
        Long idRol = rs.getLong("id_rol");
        rolDAO.buscarPorId(idRol).ifPresent(u::setRol);

        return u;
    }

    // =============================================
    // MÉTODOS
    // =============================================

    // Crea un nuevo usuario y devuelve el id generado
    @Override
    public Long crear(Usuario usuario) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_CREAR)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellido());
            ps.setLong(5, usuario.getIdTipoDocumento());
            ps.setString(6, usuario.getNumeroDocumento());
            ps.setString(7, usuario.getEmail());
            ps.setString(8, usuario.getTelefono());
            ps.setLong(9, usuario.getRol().getIdRol());
            ps.setBoolean(10, usuario.getActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_usuario");
                }
                throw new RuntimeException("No se generó id_usuario al crear el usuario.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear usuario: " + e.getMessage(), e);
        }
    }

    // Actualiza los datos de un usuario existente
    @Override
    public boolean actualizar(Usuario usuario) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setLong(3, usuario.getIdTipoDocumento());
            ps.setString(4, usuario.getNumeroDocumento());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getTelefono());
            ps.setLong(7, usuario.getRol().getIdRol());
            ps.setBoolean(8, usuario.getActivo());
            ps.setLong(9, usuario.getIdUsuario());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar usuario: " + e.getMessage(), e);
        }
    }

    // Actualiza solo la contraseña de un usuario (ya hasheada)
    @Override
    public boolean cambiarPassword(Long id, String hashNuevo) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_CAMBIAR_PASSWORD)) {

            ps.setString(1, hashNuevo);
            ps.setLong(2, id);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al cambiar contraseña: " + e.getMessage(), e);
        }
    }

    // Activa un usuario
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar usuario: " + e.getMessage(), e);
        }
    }

    // Desactiva un usuario
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DESACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar usuario: " + e.getMessage(), e);
        }
    }

    // Busca un usuario por su id
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar usuario por id: " + e.getMessage(), e);
        }
    }

    // Busca un usuario por su username (clave para el login)
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_USERNAME)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar usuario por username: " + e.getMessage(), e);
        }
    }

    // Busca usuarios cuyo nombre, apellido o username coincida parcialmente
    @Override
    public List<Usuario> buscarPorNombreParcial(String filtro) {
        List<Usuario> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE_PARCIAL)) {

            String f = "%" + (filtro == null ? "" : filtro.trim()) + "%";
            ps.setString(1, f);
            ps.setString(2, f);
            ps.setString(3, f);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearUsuario(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar usuarios por nombre parcial: " + e.getMessage(), e);
        }
    }

    // Lista todos los usuarios
    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los usuarios: " + e.getMessage(), e);
        }
    }

    // Lista solo los usuarios activos
    @Override
    public List<Usuario> listarActivos() {
        List<Usuario> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar usuarios activos: " + e.getMessage(), e);
        }
    }

    // Lista solo los usuarios inactivos
    @Override
    public List<Usuario> listarInactivos() {
        List<Usuario> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar usuarios inactivos: " + e.getMessage(), e);
        }
    }
}