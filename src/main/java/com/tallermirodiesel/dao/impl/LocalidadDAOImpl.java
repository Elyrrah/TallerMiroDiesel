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

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = """
                INSERT INTO public.localidades (id_distrito, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_localidad
                """;

    private static final String SQL_UPDATE = """
                UPDATE public.localidades
                SET id_distrito = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_localidad = ?
                """;

    private static final String SQL_DELETE = "DELETE FROM public.localidades WHERE id_localidad = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.localidades SET activo = ? WHERE id_localidad = ?";

    private static final String SQL_SELECT_BASE = """
                SELECT l.id_localidad, l.id_distrito, l.nombre, l.activo, d.nombre AS nombre_distrito
                FROM public.localidades l
                JOIN public.distritos d ON d.id_distrito = l.id_distrito
                """;

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE l.id_localidad = ?";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + """
                WHERE UPPER(TRIM(l.nombre)) = UPPER(TRIM(?))
                  AND l.id_distrito = ?
                """;

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + """
                WHERE UPPER(l.nombre) LIKE UPPER(?)
                ORDER BY l.nombre ASC
                """;

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY l.nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE l.activo = true ORDER BY l.nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE l.activo = false ORDER BY l.nombre ASC";

    private static final String SQL_LISTAR_POR_DISTRITO = SQL_SELECT_BASE + " WHERE l.id_distrito = ? ORDER BY l.nombre ASC";

    // Método para Mapear una Localidad
    private Localidad mapearLocalidad(ResultSet rs) throws SQLException {
        Localidad l = new Localidad();
        l.setIdLocalidad(rs.getLong("id_localidad"));
        l.setIdDistrito(rs.getLong("id_distrito"));
        l.setNombre(rs.getString("nombre"));
        l.setActivo(rs.getBoolean("activo"));
        l.setNombreDistrito(rs.getString("nombre_distrito"));
        return l;
    }

    // Método para crear una Localidad
    @Override
    public Long crear(Localidad localidad) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

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

    // Método para Actualizar una Localidad
    @Override
    public boolean actualizar(Localidad localidad) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setLong(1, localidad.getIdDistrito());
            ps.setString(2, localidad.getNombre());
            ps.setBoolean(3, localidad.isActivo());
            ps.setLong(4, localidad.getIdLocalidad());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar localidad: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar una Localidad
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar localidad: " + e.getMessage(), e);
        }
    }

    // Método para Activar una Localidad
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar localidad: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar una Localidad
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar localidad: " + e.getMessage(), e);
        }
    }

    // Método para Buscar una Localidad por su ID
    @Override
    public Optional<Localidad> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearLocalidad(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar localidad por ID: " + e.getMessage(), e);
        }
    }

    // Método no usado en este caso
    @Override
    public Optional<Localidad> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para localidades usa buscarPorNombre(String nombre, Long idDistrito)."
        );
    }

    // Método para Buscar una Localidad por nombre y distrito
    @Override
    public Optional<Localidad> buscarPorNombre(String nombre, Long idDistrito) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

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

    // Método para Buscar una Localidad de forma parcial por nombre
    @Override
    public List<Localidad> buscarPorNombreParcial(String filtro) {
        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

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

    // Método para listar todas las Localidades
    @Override
    public List<Localidad> listarTodos() {
        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todas las localidades: " + e.getMessage(), e);
        }
    }

    // Método para Listar todas las Localidades Activas
    @Override
    public List<Localidad> listarActivos() {
        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar localidades activas: " + e.getMessage(), e);
        }
    }

    // Método para Listar todas las Localidades Inactivas
    @Override
    public List<Localidad> listarInactivos() {
        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar localidades inactivas: " + e.getMessage(), e);
        }
    }

    // Método para Listar todas las Localidades de un Distrito
    @Override
    public List<Localidad> listarPorDistrito(Long idDistrito) {
        if (idDistrito == null) {
            return List.of();
        }

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_DISTRITO)) {

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