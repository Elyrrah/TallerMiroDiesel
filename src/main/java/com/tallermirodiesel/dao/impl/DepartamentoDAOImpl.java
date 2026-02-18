package com.tallermirodiesel.dao.impl;

import java.sql.Connection;import java.sql.PreparedStatement;
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

    private Departamento mapearDepartamento(ResultSet rs) throws SQLException {
        Departamento d = new Departamento();
        d.setIdDepartamento(rs.getLong("id_departamento"));
        d.setIdPais(rs.getLong("id_pais"));
        d.setNombre(rs.getString("nombre"));
        d.setActivo(rs.getBoolean("activo"));
        d.setNombrePais(rs.getString("nombre_pais"));
        return d;
    }

    @Override
    public Long crear(Departamento departamento) {
        String sql = """
                INSERT INTO public.departamentos (id_pais, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_departamento
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    @Override
    public boolean actualizar(Departamento departamento) {
        String sql = """
                UPDATE public.departamentos
                SET id_pais = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_departamento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, departamento.getIdPais());
            ps.setString(2, departamento.getNombre());
            ps.setBoolean(3, departamento.isActivo());
            ps.setLong(4, departamento.getIdDepartamento());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar departamento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.departamentos WHERE id_departamento = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar departamento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = """
                UPDATE public.departamentos
                SET activo = true
                WHERE id_departamento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar departamento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.departamentos
                SET activo = false
                WHERE id_departamento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar departamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                WHERE d.id_departamento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearDepartamento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar departamento por ID: " + e.getMessage(), e);
        }
    }

    /**
     * No usar este método para Departamento.
     * Usar buscarPorNombre(String nombre, Long idPais) en su lugar,
     * ya que el nombre de un departamento solo es único dentro de un país.
     */
    @Override
    public Optional<Departamento> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para departamentos usa buscarPorNombre(String nombre, Long idPais)."
        );
    }

    @Override
    public Optional<Departamento> buscarPorNombre(String nombre, Long idPais) {
        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                WHERE UPPER(TRIM(d.nombre)) = UPPER(TRIM(?))
                  AND d.id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    @Override
    public List<Departamento> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                WHERE UPPER(d.nombre) LIKE UPPER(?)
                ORDER BY p.nombre ASC, d.nombre ASC
                """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    @Override
    public List<Departamento> listarTodos() {
        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                ORDER BY p.nombre ASC, d.nombre ASC
                """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los departamentos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Departamento> listarActivos() {
        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                WHERE d.activo = true
                ORDER BY p.nombre ASC, d.nombre ASC
                """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar departamentos activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Departamento> listarInactivos() {
        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                WHERE d.activo = false
                ORDER BY p.nombre ASC, d.nombre ASC
                """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar departamentos inactivos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Departamento> listarPorPais(Long idPais) {
        if (idPais == null) {
            return List.of();
        }

        String sql = """
                SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
                FROM public.departamentos d
                JOIN public.paises p ON p.id_pais = d.id_pais
                WHERE d.id_pais = ?
                ORDER BY d.nombre ASC
                """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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