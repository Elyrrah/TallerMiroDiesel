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
import com.tallermirodiesel.dao.ClientePersonaDAO;
import com.tallermirodiesel.model.ClientePersona;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClientePersonaDAOImpl implements ClientePersonaDAO {

    private static final String SQL_INSERT =
        "INSERT INTO clientes_persona (id_cliente, nombre, apellido, apodo) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE clientes_persona SET nombre = ?, apellido = ?, apodo = ? WHERE id_cliente = ?";

    private static final String SQL_EXISTE_ID_CLIENTE =
        "SELECT 1 FROM clientes_persona WHERE id_cliente = ?";

    private static final String SQL_BUSCAR_ID_CLIENTE =
        "SELECT id_cliente, nombre, apellido, apodo FROM clientes_persona WHERE id_cliente = ?";

    private static final String SQL_DELETE_ID_CLIENTE =
        "DELETE FROM clientes_persona WHERE id_cliente = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id_cliente, nombre, apellido, apodo FROM clientes_persona ORDER BY id_cliente ASC";

    @Override
    public boolean guardar(ClientePersona persona) {
        if (persona == null || persona.getIdCliente() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConexion()) {
            if (actualizar(conn, persona)) {
                return true;
            }
            return insertar(conn, persona);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al guardar cliente persona: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existePorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al verificar existencia de cliente persona: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ClientePersona> buscarPorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_ID_CLIENTE)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPersona(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar cliente persona: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar cliente persona: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClientePersona> listarTodos() {
        List<ClientePersona> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapPersona(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar clientes persona: " + e.getMessage(), e);
        }

        return lista;
    }

    private boolean insertar(Connection conn, ClientePersona persona) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, persona.getIdCliente());
            ps.setString(2, persona.getNombre());
            ps.setString(3, persona.getApellido());

            if (persona.getApodo() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, persona.getApodo());
            }

            return ps.executeUpdate() > 0;
        }
    }

    private boolean actualizar(Connection conn, ClientePersona persona) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());

            if (persona.getApodo() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, persona.getApodo());
            }

            ps.setLong(4, persona.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    private ClientePersona mapPersona(ResultSet rs) throws SQLException {
        ClientePersona p = new ClientePersona();
        p.setIdCliente(rs.getLong("id_cliente"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setApodo(rs.getString("apodo"));
        return p;
    }
}