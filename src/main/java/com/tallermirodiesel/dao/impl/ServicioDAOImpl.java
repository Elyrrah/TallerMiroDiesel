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
import com.tallermirodiesel.dao.ServicioDAO;
import com.tallermirodiesel.model.Servicio;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ServicioDAOImpl implements ServicioDAO {

    private Servicio mapearServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setIdServicio(rs.getLong("id_servicio"));
        s.setCodigo(rs.getString("codigo"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setPrecioBase(rs.getBigDecimal("precio_base"));
        s.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            s.setFechaCreacion(ts.toLocalDateTime());
        }

        return s;
    }

    @Override
    public Long crear(Servicio servicio) {
        String sql = """
                INSERT INTO public.servicios (codigo, nombre, descripcion, precio_base, activo)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id_servicio
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getDescripcion());
            ps.setBigDecimal(4, servicio.getPrecioBase());
            ps.setBoolean(5, servicio.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_servicio");
                }
                throw new RuntimeException("No se generó id_servicio");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Servicio servicio) {
        String sql = """
                UPDATE public.servicios
                SET codigo = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio_base = ?,
                    activo = ?
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getDescripcion());
            ps.setBigDecimal(4, servicio.getPrecioBase());
            ps.setBoolean(5, servicio.isActivo());
            ps.setLong(6, servicio.getIdServicio());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.servicios WHERE id_servicio = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = """
                UPDATE public.servicios
                SET activo = true
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.servicios
                SET activo = false
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar servicio: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Servicio> buscarPorId(Long id) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearServicio(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicio por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE UPPER(TRIM(codigo)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearServicio(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicio por código: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Servicio> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearServicio(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicio por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Servicio> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE UPPER(nombre) LIKE UPPER(?)
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearServicio(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicios por nombre parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Servicio> listarTodos() {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los servicios: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Servicio> listarActivos() {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE activo = true
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar servicios activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Servicio> listarInactivos() {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE activo = false
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar servicios inactivos: " + e.getMessage(), e);
        }
    }
}