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

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = "INSERT INTO public.tipos_componente (nombre, descripcion, activo) VALUES (?, ?, ?) RETURNING id_tipo_componente";

    private static final String SQL_UPDATE = "UPDATE public.tipos_componente SET nombre = ?, descripcion = ?, activo = ? WHERE id_tipo_componente = ?";

    private static final String SQL_DELETE = "DELETE FROM public.tipos_componente WHERE id_tipo_componente = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.tipos_componente SET activo = ? WHERE id_tipo_componente = ?";

    private static final String SQL_SELECT_BASE = "SELECT id_tipo_componente, nombre, descripcion, activo FROM public.tipos_componente";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE id_tipo_componente = ?";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + " WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + " WHERE nombre ILIKE ? ORDER BY nombre ASC";

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE activo = true ORDER BY nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE activo = false ORDER BY nombre ASC";

    // Método para Mapear un Tipo de Componente
    private TipoComponente mapearTipoComponente(ResultSet rs) throws SQLException {
        TipoComponente tc = new TipoComponente();
        tc.setIdTipoComponente(rs.getLong("id_tipo_componente"));
        tc.setNombre(rs.getString("nombre"));
        tc.setDescripcion(rs.getString("descripcion"));
        tc.setActivo(rs.getBoolean("activo"));
        return tc;
    }

    // Método para crear un Tipo de Componente
    @Override
    public Long crear(TipoComponente tc) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

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

    // Método para Actualizar un Tipo de Componente
    @Override
    public boolean actualizar(TipoComponente tc) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, tc.getNombre());
            ps.setString(2, tc.getDescripcion());
            ps.setBoolean(3, tc.isActivo());
            ps.setLong(4, tc.getIdTipoComponente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar tipo componente: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar un Tipo de Componente
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar tipo componente: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Tipo de Componente
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar tipo componente: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Tipo de Componente
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar tipo componente: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Componente por su id
    @Override
    public Optional<TipoComponente> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoComponente(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo componente por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Componente por nombre
    @Override
    public Optional<TipoComponente> buscarPorNombre(String nombre) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoComponente(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo componente por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Componente de forma parcial por nombre
    @Override
    public List<TipoComponente> buscarPorNombreParcial(String filtro) {
        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

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

    // Método para listar todos los Tipos de Componente
    @Override
    public List<TipoComponente> listarTodos() {
        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoComponente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los tipos componente: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Tipos de Componente Activos
    @Override
    public List<TipoComponente> listarActivos() {
        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoComponente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos componente activos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Tipos de Componente Inactivos
    @Override
    public List<TipoComponente> listarInactivos() {
        List<TipoComponente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
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