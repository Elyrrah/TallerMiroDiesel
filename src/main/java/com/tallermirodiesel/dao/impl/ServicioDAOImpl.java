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

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = "INSERT INTO public.servicios (codigo, nombre, descripcion, precio_base, activo) VALUES (?, ?, ?, ?, ?) RETURNING id_servicio";

    private static final String SQL_UPDATE = "UPDATE public.servicios SET codigo = ?, nombre = ?, descripcion = ?, precio_base = ?, activo = ? WHERE id_servicio = ?";

    private static final String SQL_DELETE = "DELETE FROM public.servicios WHERE id_servicio = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.servicios SET activo = ? WHERE id_servicio = ?";

    private static final String SQL_SELECT_BASE = "SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion FROM public.servicios";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE id_servicio = ?";

    private static final String SQL_BUSCAR_CODIGO = SQL_SELECT_BASE + " WHERE UPPER(TRIM(codigo)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + " WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + " WHERE UPPER(nombre) LIKE UPPER(?) ORDER BY nombre ASC";

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE activo = true ORDER BY nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE activo = false ORDER BY nombre ASC";

    // Método para Mapear un Servicio
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

    // Método para crear un Servicio
    @Override
    public Long crear(Servicio servicio) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

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

    // Método para Actualizar un Servicio
    @Override
    public boolean actualizar(Servicio servicio) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

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

    // Método para Eliminar un Servicio
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar servicio: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Servicio
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar servicio: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Servicio
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar servicio: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Servicio por su id
    @Override
    public Optional<Servicio> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearServicio(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicio por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Servicio por código
    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_CODIGO)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearServicio(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicio por código: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Servicio por nombre
    @Override
    public Optional<Servicio> buscarPorNombre(String nombre) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearServicio(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar servicio por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Servicio de forma parcial por nombre
    @Override
    public List<Servicio> buscarPorNombreParcial(String filtro) {
        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

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

    // Método para listar todos los Servicios
    @Override
    public List<Servicio> listarTodos() {
        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los servicios: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Servicios Activos
    @Override
    public List<Servicio> listarActivos() {
        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar servicios activos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Servicios Inactivos
    @Override
    public List<Servicio> listarInactivos() {
        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
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