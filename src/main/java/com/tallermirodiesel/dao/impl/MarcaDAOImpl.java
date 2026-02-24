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

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = "INSERT INTO public.marcas (nombre, activo) VALUES (?, ?) RETURNING id_marca";

    private static final String SQL_UPDATE = "UPDATE public.marcas SET nombre = ?, activo = ? WHERE id_marca = ?";

    private static final String SQL_DELETE = "DELETE FROM public.marcas WHERE id_marca = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.marcas SET activo = ? WHERE id_marca = ?";

    private static final String SQL_SELECT_BASE = "SELECT id_marca, nombre, activo FROM public.marcas";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE id_marca = ?";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + " WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + "WHERE UPPER(nombre) LIKE UPPER(?) ORDER BY nombre ASC";

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE activo = true ORDER BY nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE activo = false ORDER BY nombre ASC";

    // Mapea un ResultSet a un objeto Marca
    private Marca mapearMarca(ResultSet rs) throws SQLException {
        Marca m = new Marca();
        m.setIdMarca(rs.getLong("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));
        return m;
    }

    // Método para crear una Marca
    @Override
    public Long crear(Marca marca) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

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

    // Método para Actualizar una Marca
    @Override
    public boolean actualizar(Marca marca) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, marca.getNombre());
            ps.setBoolean(2, marca.isActivo());
            ps.setLong(3, marca.getIdMarca());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar marca: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar una Marca
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar marca: " + e.getMessage(), e);
        }
    }

    // Método para Activar una Marca
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar marca: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar una Marca
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar marca: " + e.getMessage(), e);
        }
    }

    // Método para Buscar una Marca por su id
    @Override
    public Optional<Marca> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

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

    // Método para Buscar una Marca por nombre.
    @Override
    public Optional<Marca> buscarPorNombre(String nombre) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

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

    // Método para Buscar una Marca de forma parcial por nombre
    @Override
    public List<Marca> buscarPorNombreParcial(String filtro) {
        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

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

    // Método para listar todas las Marcas
    @Override
    public List<Marca> listarTodos() {
        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todas las marcas: " + e.getMessage(), e);
        }
    }

    // Método para Listar todas las Marcas Activas
    @Override
    public List<Marca> listarActivos() {
        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar marcas activas: " + e.getMessage(), e);
        }
    }

    // Métodos para Listar todas las Marcas Inactivas
    @Override
    public List<Marca> listarInactivos() {
        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
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