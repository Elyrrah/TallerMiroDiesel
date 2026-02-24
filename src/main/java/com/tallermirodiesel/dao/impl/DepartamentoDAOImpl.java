package com.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.DepartamentoDAO;
import com.tallermirodiesel.model.Departamento;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class DepartamentoDAOImpl implements DepartamentoDAO {

    // Inicialización de consultas SQL
    private static final String SQL_CREAR =
        "INSERT INTO public.departamentos (id_pais, nombre, activo) " +
        "VALUES (?, ?, ?) RETURNING id_departamento";

    private static final String SQL_ACTUALIZAR =
        "UPDATE public.departamentos SET " +
        "id_pais = ?, nombre = ?, activo = ? " +
        "WHERE id_departamento = ?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM public.departamentos WHERE id_departamento = ?";

    private static final String SQL_ACTIVAR =
        "UPDATE public.departamentos SET activo = true WHERE id_departamento = ?";

    private static final String SQL_DESACTIVAR =
        "UPDATE public.departamentos SET activo = false WHERE id_departamento = ?";

    private static final String SQL_BUSCAR_POR_ID =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "WHERE d.id_departamento = ?";

    private static final String SQL_BUSCAR_POR_NOMBRE =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "WHERE UPPER(TRIM(d.nombre)) = UPPER(TRIM(?)) " +
        "AND d.id_pais = ?";

    private static final String SQL_BUSCAR_POR_NOMBRE_PARCIAL =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "WHERE UPPER(d.nombre) LIKE UPPER(?) " +
        "ORDER BY p.nombre ASC, d.nombre ASC";

    private static final String SQL_LISTAR_TODOS =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "ORDER BY p.nombre ASC, d.nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "WHERE d.activo = true " +
        "ORDER BY p.nombre ASC, d.nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "WHERE d.activo = false " +
        "ORDER BY p.nombre ASC, d.nombre ASC";

    private static final String SQL_LISTAR_POR_PAIS =
        "SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais " +
        "FROM public.departamentos d " +
        "JOIN public.paises p ON p.id_pais = d.id_pais " +
        "WHERE d.id_pais = ? " +
        "ORDER BY d.nombre ASC";

    // Método para Mapear un Departamento
    private Departamento mapearDepartamento(ResultSet rs) throws SQLException {
        Departamento d = new Departamento();
        d.setIdDepartamento(rs.getLong("id_departamento"));
        d.setIdPais(rs.getLong("id_pais"));
        d.setNombre(rs.getString("nombre"));
        d.setActivo(rs.getBoolean("activo"));
        d.setNombrePais(rs.getString("nombre_pais"));
        return d;
    }

    // Método para crear un Departamento
    @Override
    public Long crear(Departamento departamento) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_CREAR)) {

            ps.setLong(1, departamento.getIdPais());
            ps.setString(2, departamento.getNombre());
            ps.setBoolean(3, departamento.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_departamento");
                }
                throw new RuntimeException("No se generó id_departamento");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear departamento: " + e.getMessage(), e);
        }
    }

    // Método para Actuallizar un Departamento
    @Override
    public boolean actualizar(Departamento departamento) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setLong(1, departamento.getIdPais());
            ps.setString(2, departamento.getNombre());
            ps.setBoolean(3, departamento.isActivo());
            ps.setLong(4, departamento.getIdDepartamento());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar departamento: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar un Departamento
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar departamento: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Departamento
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar departamento: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Departamento
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DESACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar departamento: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Departamento por su id
    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearDepartamento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar departamento por ID: " + e.getMessage(), e);
        }
    }

    // Método no usado en este caso.
    @Override
    public Optional<Departamento> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para departamentos usa buscarPorNombre(String nombre, Long idPais)."
        );
    }

    // Método para Buscar un Departamento por nombre.
    @Override
    public Optional<Departamento> buscarPorNombre(String nombre, Long idPais) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);
            ps.setLong(2, idPais);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearDepartamento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar departamento por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Departamento de forma parcial por nombre
    @Override
    public List<Departamento> buscarPorNombreParcial(String filtro) {
        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE_PARCIAL)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDepartamento(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar departamentos por nombre parcial: " + e.getMessage(), e);
        }
    }

    // Método para listar todos los Departamentos
    @Override
    public List<Departamento> listarTodos() {
        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los departamentos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Departamentos Activos
    @Override
    public List<Departamento> listarActivos() {
        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar departamentos activos: " + e.getMessage(), e);
        }
    }

    // Métodos para Listar todos los Departamentos Inactivos
    @Override
    public List<Departamento> listarInactivos() {
        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar departamentos inactivos: " + e.getMessage(), e);
        }
    }

    // Metodo para Listar todos los Departamentos de un País
    @Override
    public List<Departamento> listarPorPais(Long idPais) {
        if (idPais == null) {
            return List.of();
        }

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_PAIS)) {

            ps.setLong(1, idPais);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDepartamento(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar departamentos por país: " + e.getMessage(), e);
        }
    }
}