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

    // Inicialización de consultas SQL
    private static final String SQL_INSERT = "INSERT INTO public.tipos_documento (nombre, codigo, aplica_a, activo) VALUES (?, ?, ?, ?) RETURNING id_tipo_documento";

    private static final String SQL_UPDATE = "UPDATE public.tipos_documento SET nombre = ?, codigo = ?, aplica_a = ?, activo = ? WHERE id_tipo_documento = ?";

    private static final String SQL_DELETE = "DELETE FROM public.tipos_documento WHERE id_tipo_documento = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE public.tipos_documento SET activo = ? WHERE id_tipo_documento = ?";

    private static final String SQL_SELECT_BASE = "SELECT id_tipo_documento, nombre, codigo, aplica_a, activo FROM public.tipos_documento";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE id_tipo_documento = ?";

    private static final String SQL_BUSCAR_CODIGO = SQL_SELECT_BASE + " WHERE UPPER(TRIM(codigo)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_NOMBRE = SQL_SELECT_BASE + " WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))";

    private static final String SQL_BUSCAR_PARCIAL = SQL_SELECT_BASE + " WHERE nombre ILIKE ? ORDER BY nombre ASC";

    private static final String SQL_LISTAR_TODOS = SQL_SELECT_BASE + " ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS = SQL_SELECT_BASE + " WHERE activo = true ORDER BY nombre ASC";

    private static final String SQL_LISTAR_INACTIVOS = SQL_SELECT_BASE + " WHERE activo = false ORDER BY nombre ASC";

    private static final String SQL_LISTAR_POR_APLICA = SQL_SELECT_BASE + " WHERE aplica_a = ? ORDER BY nombre ASC";

    private static final String SQL_LISTAR_ACTIVOS_POR_APLICA = SQL_SELECT_BASE + " WHERE activo = true AND aplica_a = ? ORDER BY nombre ASC";

    // Mapea un ResultSet a un objeto TipoDocumento
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

    // Método para crear un Tipo de Documento
    @Override
    public Long crear(TipoDocumento tipoDocumento) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

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

    // Método para Actualizar un Tipo de Documento
    @Override
    public boolean actualizar(TipoDocumento tipoDocumento) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

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

    // Método para Eliminar un Tipo de Documento
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar tipo documento: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Tipo de Documento
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, true);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar tipo documento: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Tipo de Documento
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, false);
            ps.setLong(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar tipo documento: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Documento por su id
    @Override
    public Optional<TipoDocumento> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoDocumento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo documento por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Documento por código
    @Override
    public Optional<TipoDocumento> buscarPorCodigo(String codigo) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_CODIGO)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoDocumento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo documento por código: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Documento por nombre
    @Override
    public Optional<TipoDocumento> buscarPorNombre(String nombre) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NOMBRE)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearTipoDocumento(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar tipo documento por nombre: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Tipo de Documento de forma parcial por nombre
    @Override
    public List<TipoDocumento> buscarPorNombreParcial(String filtro) {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_PARCIAL)) {

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

    // Método para listar todos los Tipos de Documento
    @Override
    public List<TipoDocumento> listarTodos() {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoDocumento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los tipos documento: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Tipos de Documento Activos
    @Override
    public List<TipoDocumento> listarActivos() {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoDocumento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos documento activos: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Tipos de Documento Inactivos
    @Override
    public List<TipoDocumento> listarInactivos() {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_INACTIVOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTipoDocumento(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar tipos documento inactivos: " + e.getMessage(), e);
        }
    }

    // Método para Listar Tipos de Documento según a qué aplica
    @Override
    public List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_APLICA)) {

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

    // Método para Listar Tipos de Documento Activos según a qué aplica
    @Override
    public List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        List<TipoDocumento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS_POR_APLICA)) {

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