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
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.model.Marca;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class MarcaDAOImpl implements MarcaDAO {

    // Mapea un ResultSet a un objeto Marca
    private Marca mapearMarca(ResultSet rs) throws SQLException {
        Marca m = new Marca();
        m.setIdMarca(rs.getLong("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));
        return m;
    }

    @Override
    public Long crear(Marca marca) {
        String sql = """
                INSERT INTO public.marcas (nombre, activo)
                VALUES (?, ?)
                RETURNING id_marca
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, marca.getNombre());
            ps.setBoolean(2, marca.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_marca");
                }
                throw new RuntimeException("No se generó id_marca al crear la marca.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear marca: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Marca marca) {
        String sql = """
                UPDATE public.marcas
                SET nombre = ?,
                    activo = ?
                WHERE id_marca = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, marca.getNombre());
            ps.setBoolean(2, marca.isActivo());
            ps.setLong(3, marca.getIdMarca());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar marca: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.marcas WHERE id_marca = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar marca: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.marcas
            SET activo = true
            WHERE id_marca = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar marca: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.marcas
                SET activo = false
                WHERE id_marca = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar marca: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Marca> buscarPorId(Long id) {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE id_marca = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMarca(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar marca por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Marca> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMarca(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar marca por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Marca> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE UPPER(nombre) LIKE UPPER(?)
                ORDER BY nombre ASC
                """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMarca(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar marcas por nombre parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Marca> listarTodos() {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                ORDER BY nombre ASC
                """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todas las marcas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Marca> listarActivos() {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE activo = true
                ORDER BY nombre ASC
                """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar marcas activas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Marca> listarInactivos() {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE activo = false
                ORDER BY nombre ASC
                """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar marcas inactivas: " + e.getMessage(), e);
        }
    }
}