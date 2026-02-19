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

    // Mapea un ResultSet a un objeto Rol (sin permisos aún)
    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setIdRol(rs.getLong("id_rol"));
        r.setNombre(rs.getString("nombre"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setActivo(rs.getBoolean("activo"));
        return r;
    }

    // Busca un rol por su id, con su lista de permisos ya cargada
    @Override
    public Optional<Rol> buscarPorId(Long id) {
        String sql = """
                SELECT id_rol, nombre, descripcion, activo
                FROM public.roles
                WHERE id_rol = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    // Lista todos los roles con sus permisos cargados
    @Override
    public List<Rol> listarTodos() {
        String sql = """
                SELECT id_rol, nombre, descripcion, activo
                FROM public.roles
                ORDER BY nombre ASC
                """;

        List<Rol> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
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

    // Lista solo los roles activos con sus permisos cargados
    @Override
    public List<Rol> listarActivos() {
        String sql = """
                SELECT id_rol, nombre, descripcion, activo
                FROM public.roles
                WHERE activo = true
                ORDER BY nombre ASC
                """;

        List<Rol> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
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