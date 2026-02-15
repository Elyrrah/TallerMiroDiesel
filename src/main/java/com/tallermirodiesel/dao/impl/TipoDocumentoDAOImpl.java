/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.TipoDocumentoDAO;
import com.tallermirodiesel.model.TipoDocumento;
import com.tallermirodiesel.model.enums.TipoDocumentoAplicaEnum;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class TipoDocumentoDAOImpl implements TipoDocumentoDAO {

    private TipoDocumento mapearTipoDocumento(ResultSet rs) throws SQLException {
        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setIdTipoDocumento(rs.getLong("id_tipo_documento"));
        tipoDocumento.setNombre(rs.getString("nombre"));
        tipoDocumento.setCodigo(rs.getString("codigo"));

        String aplicaA = rs.getString("aplica_a");
        tipoDocumento.setAplicaA(aplicaA == null ? null : TipoDocumentoAplicaEnum.valueOf(aplicaA));

        tipoDocumento.setActivo(rs.getBoolean("activo"));
        return tipoDocumento;
    }

    @Override
    public Long crear(TipoDocumento tipoDocumento) {
        String sql = """
                INSERT INTO public.tipos_documento (nombre, codigo, aplica_a, activo)
                VALUES (?, ?, ?, ?)
                RETURNING id_tipo_documento
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento.getNombre());
            ps.setString(2, tipoDocumento.getCodigo());

            TipoDocumentoAplicaEnum aplicaA = tipoDocumento.getAplicaA();
            if (aplicaA == null) {
                ps.setNull(3, Types.OTHER);
            } else {
                ps.setObject(3, aplicaA.name(), Types.OTHER);
            }

            ps.setBoolean(4, tipoDocumento.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_tipo_documento");
                }
                throw new RuntimeException("No se generó id_tipo_documento");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear tipo documento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean actualizar(TipoDocumento tipoDocumento) {
        String sql = """
                UPDATE public.tipos_documento
                SET nombre = ?,
                    codigo = ?,
                    aplica_a = ?,
                    activo = ?
                WHERE id_tipo_documento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento.getNombre());
            ps.setString(2, tipoDocumento.getCodigo());

            TipoDocumentoAplicaEnum aplicaA = tipoDocumento.getAplicaA();
            if (aplicaA == null) {
                ps.setNull(3, Types.OTHER);
            } else {
                ps.setObject(3, aplicaA.name(), Types.OTHER);
            }

            ps.setBoolean(4, tipoDocumento.isActivo());
            ps.setLong(5, tipoDocumento.getIdTipoDocumento());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar tipo documento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.tipos_documento WHERE id_tipo_documento = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar tipo documento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = """
                UPDATE public.tipos_documento
                SET activo = true
                WHERE id_tipo_documento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar tipo documento: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.tipos_documento
                SET activo = false
                WHERE id_tipo_documento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar tipo documento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<TipoDocumento> buscarPorId(Long id) {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE id_tipo_documento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoDocumento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo documento por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<TipoDocumento> buscarPorCodigo(String codigo) {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE UPPER(TRIM(codigo)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoDocumento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo documento por código: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<TipoDocumento> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoDocumento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo documento por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoDocumento> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE nombre ILIKE ?
                ORDER BY nombre ASC
                """;

        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTipoDocumento(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipos documento por nombre parcial: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoDocumento> listarTodos() {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                ORDER BY nombre ASC
                """;

        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoDocumento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los tipos documento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoDocumento> listarActivos() {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE activo = true
                ORDER BY nombre ASC
                """;

        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoDocumento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos documento activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoDocumento> listarInactivos() {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE activo = false
                ORDER BY nombre ASC
                """;

        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoDocumento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos documento inactivos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE aplica_a = ?
                ORDER BY nombre ASC
                """;

        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, aplicaA.name(), Types.OTHER);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTipoDocumento(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos documento por aplicaA: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        String sql = """
                SELECT id_tipo_documento, nombre, codigo, aplica_a, activo
                FROM public.tipos_documento
                WHERE activo = true AND aplica_a = ?
                ORDER BY nombre ASC
                """;

        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setObject(1, aplicaA.name(), Types.OTHER);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTipoDocumento(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos documento activos por aplicaA: " + e.getMessage(), e);
        }
    }
}