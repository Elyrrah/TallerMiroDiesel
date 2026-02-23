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
import com.tallermirodiesel.dao.ComponenteDAO;
import com.tallermirodiesel.model.Componente;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ComponenteDAOImpl implements ComponenteDAO {

    private static final String SELECT_BASE = """
            SELECT c.id_componente,
                   c.id_tipo_componente,
                   c.id_marca,
                   c.id_modelo,
                   c.numero_serie,
                   c.observaciones,
                   c.activo,
                   tc.nombre AS nombre_tipo_componente,
                   ma.nombre AS nombre_marca,
                   mo.nombre AS nombre_modelo
            FROM public.componentes c
            JOIN public.tipos_componente tc ON tc.id_tipo_componente = c.id_tipo_componente
            JOIN public.marcas ma           ON ma.id_marca           = c.id_marca
            JOIN public.modelos mo          ON mo.id_modelo          = c.id_modelo
            """;

    private Componente mapear(ResultSet rs) throws SQLException {
        Componente c = new Componente();
        c.setIdComponente(rs.getLong("id_componente"));
        c.setIdTipoComponente(rs.getLong("id_tipo_componente"));
        c.setIdMarca(rs.getLong("id_marca"));
        c.setIdModelo(rs.getLong("id_modelo"));
        c.setNumeroSerie(rs.getString("numero_serie"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setActivo(rs.getBoolean("activo"));
        c.setNombreTipoComponente(rs.getString("nombre_tipo_componente"));
        c.setNombreMarca(rs.getString("nombre_marca"));
        c.setNombreModelo(rs.getString("nombre_modelo"));
        return c;
    }

    @Override
    public Long crear(Componente c) {
        String sql = """
                INSERT INTO public.componentes
                    (id_tipo_componente, id_marca, id_modelo, numero_serie, observaciones, activo)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id_componente
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, c.getIdTipoComponente());
            ps.setLong(2, c.getIdMarca());
            ps.setLong(3, c.getIdModelo());
            ps.setString(4, c.getNumeroSerie());
            ps.setString(5, c.getObservaciones());
            ps.setBoolean(6, c.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_componente");
                }
                throw new RuntimeException("No se generó id_componente");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(Componente c) {
        String sql = """
                UPDATE public.componentes
                SET id_tipo_componente = ?,
                    id_marca           = ?,
                    id_modelo          = ?,
                    numero_serie       = ?,
                    observaciones      = ?,
                    activo             = ?
                WHERE id_componente = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, c.getIdTipoComponente());
            ps.setLong(2, c.getIdMarca());
            ps.setLong(3, c.getIdModelo());
            ps.setString(4, c.getNumeroSerie());
            ps.setString(5, c.getObservaciones());
            ps.setBoolean(6, c.isActivo());
            ps.setLong(7, c.getIdComponente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.componentes WHERE id_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = "UPDATE public.componentes SET activo = true WHERE id_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar componente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = "UPDATE public.componentes SET activo = false WHERE id_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar componente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Componente> buscarPorId(Long id) {
        String sql = SELECT_BASE + " WHERE c.id_componente = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar componente por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Componente> buscarPorNombre(String numeroSerie) {
        // En componentes el "nombre" equivale al número de serie
        return buscarPorNumeroSerie(numeroSerie);
    }

    @Override
    public Optional<Componente> buscarPorNumeroSerie(String numeroSerie) {
        String sql = SELECT_BASE + " WHERE UPPER(TRIM(c.numero_serie)) = UPPER(TRIM(?))";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numeroSerie);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar componente por número de serie: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> buscarPorNombreParcial(String filtro) {
        // En componentes buscamos por número de serie parcial
        String sql = SELECT_BASE + " WHERE c.numero_serie ILIKE ? ORDER BY tc.nombre ASC, ma.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar componentes por número de serie parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> listarTodos() {
        String sql = SELECT_BASE + " ORDER BY tc.nombre ASC, ma.nombre ASC, mo.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los componentes: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> listarActivos() {
        String sql = SELECT_BASE + " WHERE c.activo = true ORDER BY tc.nombre ASC, ma.nombre ASC, mo.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar componentes activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> listarInactivos() {
        String sql = SELECT_BASE + " WHERE c.activo = false ORDER BY tc.nombre ASC, ma.nombre ASC, mo.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar componentes inactivos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> listarPorTipoComponente(Long idTipoComponente) {
        String sql = SELECT_BASE + " WHERE c.id_tipo_componente = ? ORDER BY ma.nombre ASC, mo.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTipoComponente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar componentes por tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> listarPorMarca(Long idMarca) {
        String sql = SELECT_BASE + " WHERE c.id_marca = ? ORDER BY tc.nombre ASC, mo.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idMarca);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar componentes por marca: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Componente> listarPorModelo(Long idModelo) {
        String sql = SELECT_BASE + " WHERE c.id_modelo = ? ORDER BY tc.nombre ASC";

        List<Componente> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idModelo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar componentes por modelo: " + e.getMessage(), e);
        }
    }
}