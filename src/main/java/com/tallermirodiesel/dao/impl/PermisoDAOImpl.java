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
import com.tallermirodiesel.model.Permiso;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class PermisoDAOImpl implements PermisoDAO {

    // Mapea un ResultSet a un objeto Permiso
    private Permiso mapearPermiso(ResultSet rs) throws SQLException {
        Permiso p = new Permiso();
        p.setIdPermiso(rs.getLong("id_permiso"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    // Busca un permiso por su id
    @Override
    public Optional<Permiso> buscarPorId(Long id) {
        String sql = """
                SELECT id_permiso, nombre, descripcion, activo
                FROM public.permisos
                WHERE id_permiso = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPermiso(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar permiso por id: " + e.getMessage(), e);
        }
    }

    // Lista todos los permisos activos asignados a un rol
    @Override
    public List<Permiso> listarPorRol(Long idRol) {
        String sql = """
                SELECT p.id_permiso, p.nombre, p.descripcion, p.activo
                FROM public.permisos p
                JOIN public.roles_permisos rp ON rp.id_permiso = p.id_permiso
                WHERE rp.id_rol = ?
                AND rp.activo = true
                AND p.activo = true
                ORDER BY p.nombre
                """;

        List<Permiso> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idRol);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPermiso(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar permisos por rol: " + e.getMessage(), e);
        }
    }
}