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

    // Inicialización de consultas SQL
    private static final String SQL_CREAR =
        "INSERT INTO public.paises (nombre, iso2, iso3, activo) " +
        "VALUES (?, ?, ?, ?) RETURNING id_pais";

    private static final String SQL_ACTUALIZAR =
        "UPDATE public.paises SET " +
        "nombre = ?, iso2 = ?, iso3 = ?, activo = ? " +
        "WHERE id_pais = ?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM public.paises WHERE id_pais = ?";

    private static final String SQL_ACTIVAR =
        "UPDATE public.paises SET activo = true WHERE id_pais = ?";

    private static final String SQL_DESACTIVAR =
        "UPDATE public.paises SET activo = false WHERE id_pais = ?";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises WHERE id_pais = ?";

    private static final String SQL_BUSCAR_POR_ISO2 =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises " +
        "WHERE UPPER(TRIM(iso2)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_POR_NOMBRE =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises " +
        "WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_POR_NOMBRE_PARCIAL =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises " +
        "WHERE UPPER(nombre) LIKE UPPER(?) " +
        "ORDER BY nombre ASC";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises WHERE activo = true ORDER BY nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS =
        "SELECT id_pais, nombre, iso2, iso3, activo " +
        "FROM public.paises WHERE activo = false ORDER BY nombre ASC";

    // Método para Mapear un País
    private Pais mapearPais(ResultSet rs) throws SQLException {
        Pais p = new Pais();
        p.setIdPais(rs.getLong("id_pais"));
        p.setNombre(rs.getString("nombre"));
        p.setIso2(rs.getString("iso2"));
        p.setIso3(rs.getString("iso3"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    // Método para crear un País
    @Override
    public Long crear(Pais pais) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_CREAR)) {

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

    // Método para Actuallizar un País
    @Override
    public boolean actualizar(Pais pais) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

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

    // Método para Eliminar un País
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar país: " + e.getMessage(), e);
        }
    }

    // Método para Activar un País
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar país: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un País
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DESACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar país: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un País por su id
    @Override
    public Optional<Pais> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearPais(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar país por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un País por si Iso2
    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ISO2)) {

            String iso2Norm = (iso2 == null) ? "" : iso2.trim();
            ps.setString(1, iso2Norm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearPais(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar país por ISO2: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Pais por su nombre
    @Override
    public Optional<Pais> buscarPorNombre(String nombre) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearPais(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar país por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Pais de forma parcial por nombre
    @Override
    public List<Pais> buscarPorNombreParcial(String filtro) {
        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE_PARCIAL)) {

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

    // Método para listar todos los Países
    @Override
    public List<Pais> listarTodos() {
        return listar(SQL_LISTAR_TODOS);
    }

    // Método para Listar todos los Países Activos
    @Override
    public List<Pais> listarActivos() {
        return listar(SQL_LISTAR_ACTIVOS);
    }

    // Métodos para Listar todos los Países Inactivos
    @Override
    public List<Pais> listarInactivos() {
        return listar(SQL_LISTAR_INACTIVOS);
    }

    private List<Pais> listar(String sql) {
        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar países: " + e.getMessage(), e);
        }

        return lista;
    }
}