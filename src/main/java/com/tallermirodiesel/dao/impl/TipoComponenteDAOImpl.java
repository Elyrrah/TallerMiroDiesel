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
import com.tallermirodiesel.dao.TipoComponenteDAO;
import com.tallermirodiesel.model.TipoComponente;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class TipoComponenteDAOImpl implements TipoComponenteDAO {

    private TipoComponente mapearTipoComponente(ResultSet rs) throws SQLException {
        TipoComponente tc = new TipoComponente();
        tc.setIdTipoComponente(rs.getLong("id_tipo_componente"));
        tc.setNombre(rs.getString("nombre"));
        tc.setDescripcion(rs.getString("descripcion"));
        tc.setActivo(rs.getBoolean("activo"));
        return tc;
    }

    @Override
    public Long crear(TipoComponente tc) {
        String sql = """
                INSERT INTO public.tipos_componente (nombre, descripcion, activo)
                VALUES (?, ?, ?)
                RETURNING id_tipo_componente
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tc.getNombre());
            ps.setString(2, tc.getDescripcion());
            ps.setBoolean(3, tc.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_tipo_componente");
                }
                throw new RuntimeException("No se generó id_tipo_componente");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear tipo componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(TipoComponente tc) {
        String sql = """
                UPDATE public.tipos_componente
                SET nombre      = ?,
                    descripcion = ?,
                    activo      = ?
                WHERE id_tipo_componente = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tc.getNombre());
            ps.setString(2, tc.getDescripcion());
            ps.setBoolean(3, tc.isActivo());
            ps.setLong(4, tc.getIdTipoComponente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar tipo componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.tipos_componente WHERE id_tipo_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar tipo componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = "UPDATE public.tipos_componente SET activo = true WHERE id_tipo_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar tipo componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = "UPDATE public.tipos_componente SET activo = false WHERE id_tipo_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar tipo componente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<TipoComponente> buscarPorId(Long id) {
        String sql = """
                SELECT id_tipo_componente, nombre, descripcion, activo
                FROM public.tipos_componente
                WHERE id_tipo_componente = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoComponente(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo componente por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<TipoComponente> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_tipo_componente, nombre, descripcion, activo
                FROM public.tipos_componente
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoComponente(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo componente por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoComponente> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_tipo_componente, nombre, descripcion, activo
                FROM public.tipos_componente
                WHERE nombre ILIKE ?
                ORDER BY nombre ASC
                """;

        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTipoComponente(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipos componente por nombre parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoComponente> listarTodos() {
        String sql = """
                SELECT id_tipo_componente, nombre, descripcion, activo
                FROM public.tipos_componente
                ORDER BY nombre ASC
                """;

        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoComponente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los tipos componente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoComponente> listarActivos() {
        String sql = """
                SELECT id_tipo_componente, nombre, descripcion, activo
                FROM public.tipos_componente
                WHERE activo = true
                ORDER BY nombre ASC
                """;

        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoComponente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos componente activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoComponente> listarInactivos() {
        String sql = """
                SELECT id_tipo_componente, nombre, descripcion, activo
                FROM public.tipos_componente
                WHERE activo = false
                ORDER BY nombre ASC
                """;

        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoComponente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos componente inactivos: " + e.getMessage(), e);
        }
    }
}