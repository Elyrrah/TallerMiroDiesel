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
import com.tallermirodiesel.dao.ClienteEmpresaDAO;
import com.tallermirodiesel.model.ClienteEmpresa;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteEmpresaDAOImpl implements ClienteEmpresaDAO {

    private static final String SQL_INSERT =
        "INSERT INTO clientes_empresa (id_cliente, razon_social, nombre_fantasia) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE clientes_empresa SET razon_social = ?, nombre_fantasia = ? WHERE id_cliente = ?";

    private static final String SQL_EXISTE_ID_CLIENTE =
        "SELECT 1 FROM clientes_empresa WHERE id_cliente = ?";

    private static final String SQL_BUSCAR_ID_CLIENTE =
        "SELECT id_cliente, razon_social, nombre_fantasia FROM clientes_empresa WHERE id_cliente = ?";

    private static final String SQL_DELETE_ID_CLIENTE =
        "DELETE FROM clientes_empresa WHERE id_cliente = ?";

    private static final String SQL_LISTAR_TODOS =
        "SELECT id_cliente, razon_social, nombre_fantasia FROM clientes_empresa ORDER BY id_cliente ASC";

    @Override
    public boolean guardar(ClienteEmpresa empresa) {
        if (empresa == null || empresa.getIdCliente() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConexion()) {
            if (actualizar(conn, empresa)) {
                return true;
            }
            return insertar(conn, empresa);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al guardar cliente empresa: " + e.getMessage(), e);
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
            throw new RuntimeException("Error en BD al verificar existencia de cliente empresa: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ClienteEmpresa> buscarPorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_ID_CLIENTE)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapEmpresa(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar cliente empresa: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar cliente empresa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClienteEmpresa> listarTodos() {
        List<ClienteEmpresa> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapEmpresa(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar clientes empresa: " + e.getMessage(), e);
        }

        return lista;
    }

    private boolean insertar(Connection conn, ClienteEmpresa empresa) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, empresa.getIdCliente());
            ps.setString(2, empresa.getRazonSocial());

            if (empresa.getNombreFantasia() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, empresa.getNombreFantasia());
            }

            return ps.executeUpdate() > 0;
        }
    }

    private boolean actualizar(Connection conn, ClienteEmpresa empresa) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, empresa.getRazonSocial());

            if (empresa.getNombreFantasia() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, empresa.getNombreFantasia());
            }

            ps.setLong(3, empresa.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    private ClienteEmpresa mapEmpresa(ResultSet rs) throws SQLException {
        ClienteEmpresa e = new ClienteEmpresa();
        e.setIdCliente(rs.getLong("id_cliente"));
        e.setRazonSocial(rs.getString("razon_social"));
        e.setNombreFantasia(rs.getString("nombre_fantasia"));
        return e;
    }
}