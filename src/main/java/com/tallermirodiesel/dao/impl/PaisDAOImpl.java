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
import com.tallermirodiesel.dao.PaisDAO;
import com.tallermirodiesel.model.Pais;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class PaisDAOImpl implements PaisDAO {

    private Pais mapearPais(ResultSet rs) throws SQLException {
        Pais p = new Pais();
        p.setIdPais(rs.getLong("id_pais"));
        p.setNombre(rs.getString("nombre"));
        p.setIso2(rs.getString("iso2"));
        p.setIso3(rs.getString("iso3"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    @Override
    public Long crear(Pais pais) {
        String sql = """
                INSERT INTO public.paises (nombre, iso2, iso3, activo)
                VALUES (?, ?, ?, ?)
                RETURNING id_pais
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pais.getNombre());
            ps.setString(2, pais.getIso2());
            ps.setString(3, pais.getIso3());
            ps.setBoolean(4, pais.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_pais");
                }
                throw new RuntimeException("No se generó id_pais");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear país: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Pais pais) {
        String sql = """
                UPDATE public.paises
                SET nombre = ?,
                    iso2 = ?,
                    iso3 = ?,
                    activo = ?
                WHERE id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pais.getNombre());
            ps.setString(2, pais.getIso2());
            ps.setString(3, pais.getIso3());
            ps.setBoolean(4, pais.isActivo());
            ps.setLong(5, pais.getIdPais());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar país: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.paises WHERE id_pais = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar país: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.paises
            SET activo = true
            WHERE id_pais = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar país: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.paises
                SET activo = false
                WHERE id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar país: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pais> buscarPorId(Long id) {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearPais(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar país por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE UPPER(TRIM(iso2)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String iso2Norm = (iso2 == null) ? "" : iso2.trim();
            ps.setString(1, iso2Norm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearPais(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar país por ISO2: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pais> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearPais(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar país por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pais> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE UPPER(nombre) LIKE UPPER(?)
                ORDER BY nombre ASC
                """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPais(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar países por nombre parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pais> listarTodos() {
        String sql = """
            SELECT id_pais, nombre, iso2, iso3, activo
            FROM public.paises
            ORDER BY nombre ASC
            """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los países: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pais> listarActivos() {
        String sql = """
            SELECT id_pais, nombre, iso2, iso3, activo
            FROM public.paises
            WHERE activo = true
            ORDER BY nombre ASC
            """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar países activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pais> listarInactivos() {
        String sql = """
            SELECT id_pais, nombre, iso2, iso3, activo
            FROM public.paises
            WHERE activo = false
            ORDER BY nombre ASC
            """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar países inactivos: " + e.getMessage(), e);
        }
    }
}