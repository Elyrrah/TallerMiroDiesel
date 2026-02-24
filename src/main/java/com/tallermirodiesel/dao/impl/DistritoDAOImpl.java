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
import com.tallermirodiesel.model.Distrito;
import com.tallermirodiesel.util.DatabaseConnection;
import com.tallermirodiesel.dao.DistritoDAO;

/**
 * @author elyrr
 */
public class DistritoDAOImpl implements DistritoDAO {

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = """
            INSERT INTO public.distritos (id_departamento, nombre, activo)
            VALUES (?, ?, ?)
            RETURNING id_distrito
            """;

    private static final String SQL_UPDATE = """
            UPDATE public.distritos
            SET id_departamento = ?,
                nombre = ?,
                activo = ?
            WHERE id_distrito = ?
            """;

    private static final String SQL_DELETE = "DELETE FROM public.distritos WHERE id_distrito = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.distritos SET activo = ? WHERE id_distrito = ?";

    private static final String SQL_SELECT_BASE = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo,
                   dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp
              ON dp.id_departamento = di.id_departamento
            """;

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE di.id_distrito = ?";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + """
            WHERE UPPER(TRIM(di.nombre)) = UPPER(TRIM(?))
              AND di.id_departamento = ?
            """;

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + """
            WHERE UPPER(di.nombre) LIKE UPPER(?)
            ORDER BY di.nombre ASC
            """;

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY di.nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE di.activo = true ORDER BY di.nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE di.activo = false ORDER BY di.nombre ASC";

    private static final String SQL_LISTAR_POR_DEPTO = SQL_SELECT_BASE + " WHERE di.id_departamento = ? ORDER BY di.nombre ASC";

    // Método para Mapear un Distrito
    private Distrito mapearDistrito(ResultSet rs) throws SQLException {
        Distrito d = new Distrito();
        d.setIdDistrito(rs.getLong("id_distrito"));
        d.setIdDepartamento(rs.getLong("id_departamento"));
        d.setNombre(rs.getString("nombre"));
        d.setActivo(rs.getBoolean("activo"));
        d.setNombreDepartamento(rs.getString("nombre_departamento"));
        return d;
    }

    // Método para crear un Distrito
    @Override
    public Long crear(Distrito distrito) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, distrito.getIdDepartamento());
            ps.setString(2, distrito.getNombre());
            ps.setBoolean(3, distrito.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_distrito");
                }
                throw new RuntimeException("No se generó id_distrito");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear distrito: " + e.getMessage(), e);
        }
    }

    // Método para Actualizar un Distrito
    @Override
    public boolean actualizar(Distrito distrito) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setLong(1, distrito.getIdDepartamento());
            ps.setString(2, distrito.getNombre());
            ps.setBoolean(3, distrito.isActivo());
            ps.setLong(4, distrito.getIdDistrito());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar distrito: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar un Distrito
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar distrito: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Distrito
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar distrito: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Distrito
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar distrito: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Distrito por su ID
    @Override
    public Optional<Distrito> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearDistrito(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar distrito por ID: " + e.getMessage(), e);
        }
    }

    // Método no usado en este caso
    @Override
    public Optional<Distrito> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para distritos usa buscarPorNombre(String nombre, Long idDepartamento)."
        );
    }

    // Método para Buscar un Distrito por nombre y departamento
    @Override
    public Optional<Distrito> buscarPorNombre(String nombre, Long idDepartamento) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();

            ps.setString(1, nombreNorm);
            ps.setLong(2, idDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearDistrito(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar distrito por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Distrito de forma parcial por nombre
    @Override
    public List<Distrito> buscarPorNombreParcial(String filtro) {
        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDistrito(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar distritos por nombre parcial: " + e.getMessage(), e);
        }
    }

    // Método para listar todos los Distritos
    @Override
    public List<Distrito> listarTodos() {
        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDistrito(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los distritos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Distritos Activos
    @Override
    public List<Distrito> listarActivos() {
        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDistrito(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar distritos activos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Distritos Inactivos
    @Override
    public List<Distrito> listarInactivos() {
        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDistrito(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar distritos inactivos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Distritos de un Departamento
    @Override
    public List<Distrito> listarPorDepartamento(Long idDepartamento) {
        if (idDepartamento == null) {
            return List.of();
        }

        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_DEPTO)) {

            ps.setLong(1, idDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDistrito(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar distritos por departamento: " + e.getMessage(), e);
        }
    }
}