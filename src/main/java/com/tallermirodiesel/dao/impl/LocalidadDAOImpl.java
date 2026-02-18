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
import com.tallermirodiesel.model.Localidad;
import com.tallermirodiesel.util.DatabaseConnection;
import com.tallermirodiesel.dao.LocalidadDAO;

/**
 * @author elyrr
 */
public class LocalidadDAOImpl implements LocalidadDAO {

    private Localidad mapearLocalidad(ResultSet rs) throws SQLException {
        Localidad l = new Localidad();
        l.setIdLocalidad(rs.getLong("id_localidad"));
        l.setIdDistrito(rs.getLong("id_distrito"));
        l.setNombre(rs.getString("nombre"));
        l.setActivo(rs.getBoolean("activo"));
        l.setNombreDistrito(rs.getString("nombre_distrito"));
        return l;
    }

    @Override
    public Long crear(Localidad localidad) {
        String sql = """
                INSERT INTO public.localidades (id_distrito, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_localidad
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, localidad.getIdDistrito());
            ps.setString(2, localidad.getNombre());
            ps.setBoolean(3, localidad.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_localidad");
                }
                throw new RuntimeException("No se generó id_localidad");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear localidad: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Localidad localidad) {
        String sql = """
                UPDATE public.localidades
                SET id_distrito = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_localidad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, localidad.getIdDistrito());
            ps.setString(2, localidad.getNombre());
            ps.setBoolean(3, localidad.isActivo());
            ps.setLong(4, localidad.getIdLocalidad());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar localidad: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.localidades WHERE id_localidad = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar localidad: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = """
                UPDATE public.localidades
                SET activo = true
                WHERE id_localidad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar localidad: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.localidades
                SET activo = false
                WHERE id_localidad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar localidad: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Localidad> buscarPorId(Long id) {
        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                WHERE l.id_localidad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearLocalidad(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar localidad por ID: " + e.getMessage(), e);
        }
    }

    /**
     * No usar este método para Localidad.
     * Usar buscarPorNombre(String nombre, Long idDistrito) en su lugar,
     * ya que el nombre de una localidad solo es único dentro de un distrito.
     */
    @Override
    public Optional<Localidad> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para localidades usa buscarPorNombre(String nombre, Long idDistrito)."
        );
    }

    @Override
    public Optional<Localidad> buscarPorNombre(String nombre, Long idDistrito) {
        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                WHERE UPPER(TRIM(l.nombre)) = UPPER(TRIM(?))
                  AND l.id_distrito = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);
            ps.setLong(2, idDistrito);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearLocalidad(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar localidad por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Localidad> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                WHERE UPPER(l.nombre) LIKE UPPER(?)
                ORDER BY l.nombre ASC
                """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLocalidad(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar localidades por nombre parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Localidad> listarTodos() {
        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                ORDER BY l.nombre ASC
                """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todas las localidades: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Localidad> listarActivos() {
        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                WHERE l.activo = true
                ORDER BY l.nombre ASC
                """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar localidades activas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Localidad> listarInactivos() {
        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                WHERE l.activo = false
                ORDER BY l.nombre ASC
                """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar localidades inactivas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Localidad> listarPorDistrito(Long idDistrito) {
        if (idDistrito == null) {
            return List.of();
        }

        String sql = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                WHERE l.id_distrito = ?
                ORDER BY l.nombre ASC
                """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idDistrito);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLocalidad(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar localidades por distrito: " + e.getMessage(), e);
        }
    }
}