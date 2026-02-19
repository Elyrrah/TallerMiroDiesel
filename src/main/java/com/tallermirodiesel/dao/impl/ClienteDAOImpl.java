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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ClienteDAO;
import com.tallermirodiesel.model.Cliente;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteDAOImpl implements ClienteDAO {

    private static final String SQL_INSERT =
        "INSERT INTO clientes (" +
        "id_usuario_creador, id_localidad, id_distrito, telefono, activo" +
        ") VALUES (?, ?, ?, ?, ?) " +
        "RETURNING id_cliente";

    private static final String SQL_UPDATE =
        "UPDATE clientes SET " +
        "id_localidad = ?, id_distrito = ?, telefono = ? " +
        "WHERE id_cliente = ?";

    private static final String SQL_EXISTE_ID =
        "SELECT 1 FROM clientes WHERE id_cliente = ?";

    private static final String SQL_SET_ACTIVO =
        "UPDATE clientes SET activo = ? WHERE id_cliente = ?";

    private static final String SQL_BUSCAR_ID =
        "SELECT * FROM clientes WHERE id_cliente = ?";

    private static final String SQL_LISTAR =
        "SELECT * FROM clientes ORDER BY id_cliente ASC";

    private static final String SQL_LISTAR_ACTIVOS =
        "SELECT * FROM clientes WHERE activo = true ORDER BY id_cliente ASC";

    private static final String SQL_LISTAR_INACTIVOS =
        "SELECT * FROM clientes WHERE activo = false ORDER BY id_cliente ASC";

    private static final String SQL_BUSCAR_BASE =
        "SELECT * FROM clientes " +
        "WHERE ( ? IS NULL OR telefono ILIKE '%' || ? || '%' ) " +
        "AND ( ? IS NULL OR activo = ? ) " +
        "ORDER BY id_cliente ASC";

    @Override
    public Long guardar(Cliente cliente) {
        try (Connection conn = DatabaseConnection.getConexion()) {
            if (cliente.getIdCliente() == null) {
                return insertar(conn, cliente);
            } else {
                actualizar(conn, cliente);
                return cliente.getIdCliente();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al guardar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean setActivo(Long idCliente, boolean activo) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, activo);
            ps.setLong(2, idCliente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al cambiar estado del cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existePorId(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID)) {

            ps.setLong(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al verificar existencia del cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCliente(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar cliente por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        return listar(SQL_LISTAR);
    }

    @Override
    public List<Cliente> listarActivos() {
        return listar(SQL_LISTAR_ACTIVOS);
    }

    @Override
    public List<Cliente> listarInactivos() {
        return listar(SQL_LISTAR_INACTIVOS);
    }

    @Override
    public List<Cliente> buscar(String q, Boolean activo) {
        List<Cliente> lista = new ArrayList<>();

        String qNormalizado = (q == null || q.trim().isBlank()) ? null : q.trim();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_BASE)) {

            ps.setString(1, qNormalizado);
            ps.setString(2, qNormalizado);

            if (activo == null) {
                ps.setNull(3, Types.BOOLEAN);
                ps.setNull(4, Types.BOOLEAN);
            } else {
                ps.setBoolean(3, activo);
                ps.setBoolean(4, activo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCliente(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar clientes: " + e.getMessage(), e);
        }
    }

    private Long insertar(Connection conn, Cliente cliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, cliente.getIdUsuarioCreador());
            ps.setObject(2, cliente.getIdLocalidad(), Types.BIGINT);
            ps.setObject(3, cliente.getIdDistrito(), Types.BIGINT);
            ps.setString(4, cliente.getTelefono());
            ps.setBoolean(5, cliente.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("id_cliente") : null;
            }
        }
    }

    private boolean actualizar(Connection conn, Cliente cliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setObject(1, cliente.getIdLocalidad(), Types.BIGINT);
            ps.setObject(2, cliente.getIdDistrito(), Types.BIGINT);
            ps.setString(3, cliente.getTelefono());
            ps.setLong(4, cliente.getIdCliente());

            return ps.executeUpdate() > 0;
        }
    }

    private List<Cliente> listar(String sql) {
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapCliente(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar clientes: " + e.getMessage(), e);
        }

        return lista;
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();

        c.setIdCliente(rs.getLong("id_cliente"));
        c.setIdUsuarioCreador(rs.getLong("id_usuario_creador"));
        c.setTelefono(rs.getString("telefono"));
        c.setIdDistrito((Long) rs.getObject("id_distrito"));
        c.setIdLocalidad((Long) rs.getObject("id_localidad"));
        c.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            c.setFechaCreacion(ts.toLocalDateTime());
        }

        return c;
    }
}