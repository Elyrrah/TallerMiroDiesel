/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.PermisoDAO;
import com.tallermirodiesel.dao.RolDAO;
import com.tallermirodiesel.model.Rol;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class RolDAOImpl implements RolDAO {

    // Usamos PermisoDAO para cargar los permisos de cada rol
    private final PermisoDAO permisoDAO = new PermisoDAOImpl();

    // Inicialización de consultas SQL
    private static final String SQL_SELECT_BASE = "SELECT id_rol, nombre, descripcion, activo FROM public.roles";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE id_rol = ?";

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE activo = true ORDER BY nombre ASC";

    // Método para Mapear un Rol (sin permisos aún)
    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setIdRol(rs.getLong("id_rol"));
        r.setNombre(rs.getString("nombre"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setActivo(rs.getBoolean("activo"));
        return r;
    }

    // Método para Buscar un rol por su id, con su lista de permisos ya cargada
    @Override
    public Optional<Rol> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Rol rol = mapearRol(rs);
                    // Cargamos los permisos del rol usando PermisoDAO
                    rol.setPermisos(permisoDAO.listarPorRol(rol.getIdRol()));
                    return Optional.of(rol);
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar rol por id: " + e.getMessage(), e);
        }
    }

    // Método para Lista todos los roles con sus permisos cargados
    @Override
    public List<Rol> listarTodos() {
        List<Rol> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = mapearRol(rs);
                rol.setPermisos(permisoDAO.listarPorRol(rol.getIdRol()));
                lista.add(rol);
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los roles: " + e.getMessage(), e);
        }
    }

    // Método para Lista solo los roles activos con sus permisos cargados
    @Override
    public List<Rol> listarActivos() {
        List<Rol> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = mapearRol(rs);
                rol.setPermisos(permisoDAO.listarPorRol(rol.getIdRol()));
                lista.add(rol);
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar roles activos: " + e.getMessage(), e);
        }
    }
}