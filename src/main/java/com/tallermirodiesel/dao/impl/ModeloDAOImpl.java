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
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.model.Modelo;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ModeloDAOImpl implements ModeloDAO {

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = "INSERT INTO public.modelos (id_marca, nombre, activo) VALUES (?, ?, ?) RETURNING id_modelo";

    private static final String SQL_UPDATE = "UPDATE public.modelos SET id_marca = ?, nombre = ?, activo = ? WHERE id_modelo = ?";

    private static final String SQL_DELETE = "DELETE FROM public.modelos WHERE id_modelo = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.modelos SET activo = ? WHERE id_modelo = ?";

    private static final String SQL_SELECT_BASE = """
                SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
                FROM public.modelos mo
                JOIN public.marcas ma ON ma.id_marca = mo.id_marca""";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE mo.id_modelo = ?";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + " WHERE UPPER(TRIM(mo.nombre)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + "WHERE UPPER(mo.nombre) LIKE UPPER(?) ORDER BY ma.nombre ASC, mo.nombre ASC";

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY ma.nombre ASC, mo.nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE mo.activo = true ORDER BY ma.nombre ASC, mo.nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE mo.activo = false ORDER BY ma.nombre ASC, mo.nombre ASC";

    private static final String SQL_LISTAR_POR_MARCA = SQL_SELECT_BASE + " WHERE mo.id_marca = ? ORDER BY mo.nombre ASC";

    // Método para Mapear un Modelo
    private Modelo mapearModelo(ResultSet rs) throws SQLException {
        Modelo m = new Modelo();
        m.setIdModelo(rs.getLong("id_modelo"));
        m.setIdMarca(rs.getLong("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));
        m.setNombreMarca(rs.getString("nombre_marca"));
        return m;
    }

    // Método para crear un Modelo
    @Override
    public Long crear(Modelo modelo) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, modelo.getIdMarca());
            ps.setString(2, modelo.getNombre());
            ps.setBoolean(3, modelo.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_modelo");
                }
                throw new RuntimeException("No se generó id_modelo");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear modelo: " + e.getMessage(), e);
        }
    }

    // Método para Actualizar un Modelo
    @Override
    public boolean actualizar(Modelo modelo) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setLong(1, modelo.getIdMarca());
            ps.setString(2, modelo.getNombre());
            ps.setBoolean(3, modelo.isActivo());
            ps.setLong(4, modelo.getIdModelo());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar modelo: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar un Modelo
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar modelo: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Modelo
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar modelo: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Modelo
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar modelo: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Modelo por su id
    @Override
    public Optional<Modelo> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearModelo(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar modelo por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Modelo por nombre.
    @Override
    public Optional<Modelo> buscarPorNombre(String nombre) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearModelo(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar modelo por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Modelo de forma parcial por nombre
    @Override
    public List<Modelo> buscarPorNombreParcial(String filtro) {
        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearModelo(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar modelos por nombre parcial: " + e.getMessage(), e);
        }
    }

    // Método para listar todos los Modelos
    @Override
    public List<Modelo> listarTodos() {
        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearModelo(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los modelos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Modelos Activos
    @Override
    public List<Modelo> listarActivos() {
        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearModelo(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar modelos activos: " + e.getMessage(), e);
        }
    }

    // Métodos para Listar todos los Modelos Inactivas
    @Override
    public List<Modelo> listarInactivos() {
        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearModelo(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar modelos inactivos: " + e.getMessage(), e);
        }
    }

    // Metodo para Listar todos los Modelos de una Marca
    @Override
    public List<Modelo> listarPorMarca(Long idMarca) {
        if (idMarca == null) {
            return List.of();
        }

        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_MARCA)) {

            ps.setLong(1, idMarca);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearModelo(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar modelos por marca: " + e.getMessage(), e);
        }
    }
}