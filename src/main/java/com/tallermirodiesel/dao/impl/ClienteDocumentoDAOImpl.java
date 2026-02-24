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
import com.tallermirodiesel.dao.ClienteDocumentoDAO;
import com.tallermirodiesel.model.ClienteDocumento;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteDocumentoDAOImpl implements ClienteDocumentoDAO {

    // Inicialización de constantes SQL
    private static final String SQL_INSERT = """
                INSERT INTO cliente_documentos (id_cliente, id_tipo_documento, numero, principal, activo) VALUES (?, ?, ?, ?, ?) RETURNING id_cliente_documento""";

    private static final String SQL_UPDATE = """
                UPDATE cliente_documentos SET id_tipo_documento = ?, numero = ?, principal = ?, activo = ? WHERE id_cliente_documento = ?""";

    private static final String SQL_EXISTE_ID = "SELECT 1 FROM cliente_documentos WHERE id_cliente_documento = ?";

    private static final String SQL_SET_ACTIVO = "UPDATE cliente_documentos SET activo = ? WHERE id_cliente_documento = ?";

    private static final String SQL_EXISTE_POR_CLIENTE_TIPO_NUMERO = "SELECT 1 FROM cliente_documentos WHERE id_cliente = ? AND id_tipo_documento = ? AND numero = ?";

    private static final String SQL_SELECT_BASE = "SELECT * FROM cliente_documentos";

    private static final String SQL_BUSCAR_ID = SQL_SELECT_BASE + " WHERE id_cliente_documento = ?";

    private static final String SQL_LISTAR_POR_CLIENTE = SQL_SELECT_BASE + " WHERE id_cliente = ? ORDER BY id_cliente_documento ASC";

    private static final String SQL_LISTAR_ACTIVOS_POR_CLIENTE = SQL_SELECT_BASE + " WHERE id_cliente = ? AND activo = true ORDER BY id_cliente_documento ASC";

    private static final String SQL_OBTENER_PRINCIPAL = SQL_SELECT_BASE + " WHERE id_cliente = ? AND principal = true AND activo = true";

    private static final String SQL_DESMARCAR_PRINCIPALES = "UPDATE cliente_documentos SET principal = false WHERE id_cliente = ?";

    private static final String SQL_MARCAR_PRINCIPAL = "UPDATE cliente_documentos SET principal = true WHERE id_cliente_documento = ?";

    // Método para Guardar un Documento de Cliente
    @Override
    public boolean guardar(ClienteDocumento clienteDocumento) {
        Long id = clienteDocumento.getIdClienteDocumento();

        if (id != null && existePorId(id)) {
            return actualizarInterno(clienteDocumento);
        }

        Long idGenerado = crearInterno(clienteDocumento);
        return idGenerado != null;
    }

    // Método para verificar si existe un Documento por su ID
    @Override
    public boolean existePorId(Long idClienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID)) {

            ps.setLong(1, idClienteDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al verificar existencia del documento: " + e.getMessage(), e);
        }
    }

    // Método para establecer el estado Activo/Inactivo de un Documento
    @Override
    public boolean setActivo(Long idClienteDocumento, boolean activo) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, activo);
            ps.setLong(2, idClienteDocumento);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al cambiar estado del documento: " + e.getMessage(), e);
        }
    }

    // Método para definir un Documento como Principal para un Cliente
    @Override
    public boolean definirPrincipal(Long idCliente, Long idClienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion()) {

            conn.setAutoCommit(false);

            try (
                PreparedStatement psDesmarcar = conn.prepareStatement(SQL_DESMARCAR_PRINCIPALES);
                PreparedStatement psMarcar = conn.prepareStatement(SQL_MARCAR_PRINCIPAL)
            ) {

                psDesmarcar.setLong(1, idCliente);
                psDesmarcar.executeUpdate();

                psMarcar.setLong(1, idClienteDocumento);
                int filas = psMarcar.executeUpdate();

                conn.commit();
                return filas > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al definir documento principal: " + e.getMessage(), e);
        }
    }

    // Método para verificar la existencia de un documento por Cliente, Tipo y Número
    @Override
    public boolean existePorClienteTipoNumero(Long idCliente, Long idTipoDocumento, String numero) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_POR_CLIENTE_TIPO_NUMERO)) {

            ps.setLong(1, idCliente);
            ps.setLong(2, idTipoDocumento);

            if (numero == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, numero);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al verificar existencia del documento por cliente/tipo/numero: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Documento por su ID
    @Override
    public Optional<ClienteDocumento> buscarPorId(Long idClienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, idClienteDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar cliente documento: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Documentos de un Cliente
    @Override
    public List<ClienteDocumento> listarPorCliente(Long idCliente) {
        return listar(SQL_LISTAR_POR_CLIENTE, idCliente);
    }

    // Método para Listar solo los Documentos Activos de un Cliente
    @Override
    public List<ClienteDocumento> listarActivosPorCliente(Long idCliente) {
        return listar(SQL_LISTAR_ACTIVOS_POR_CLIENTE, idCliente);
    }

    // Método para obtener el Documento Principal de un Cliente
    @Override
    public Optional<ClienteDocumento> obtenerPrincipalPorCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_OBTENER_PRINCIPAL)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al obtener documento principal: " + e.getMessage(), e);
        }
    }

    // Método privado para insertar internamente un documento
    private Long crearInterno(ClienteDocumento clienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, clienteDocumento.getIdCliente());
            ps.setLong(2, clienteDocumento.getIdTipoDocumento());

            if (clienteDocumento.getNumero() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, clienteDocumento.getNumero());
            }

            ps.setBoolean(4, clienteDocumento.isPrincipal());
            ps.setBoolean(5, clienteDocumento.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long idGenerado = rs.getLong("id_cliente_documento");
                    clienteDocumento.setIdClienteDocumento(idGenerado);
                    return idGenerado;
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear cliente documento: " + e.getMessage(), e);
        }
    }

    // Método privado para actualizar internamente un documento
    private boolean actualizarInterno(ClienteDocumento clienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setLong(1, clienteDocumento.getIdTipoDocumento());

            if (clienteDocumento.getNumero() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, clienteDocumento.getNumero());
            }

            ps.setBoolean(3, clienteDocumento.isPrincipal());
            ps.setBoolean(4, clienteDocumento.isActivo());
            ps.setLong(5, clienteDocumento.getIdClienteDocumento());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar cliente documento: " + e.getMessage(), e);
        }
    }

    // Método genérico para listar documentos por ID de Cliente
    private List<ClienteDocumento> listar(String sql, Long idCliente) {
        List<ClienteDocumento> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar documentos: " + e.getMessage(), e);
        }

        return lista;
    }

    // Método para Mapear un Documento de Cliente
    private ClienteDocumento mapear(ResultSet rs) throws SQLException {
        ClienteDocumento cd = new ClienteDocumento();
        cd.setIdClienteDocumento(rs.getLong("id_cliente_documento"));
        cd.setIdCliente(rs.getLong("id_cliente"));
        cd.setIdTipoDocumento(rs.getLong("id_tipo_documento"));
        cd.setNumero(rs.getString("numero"));
        cd.setPrincipal(rs.getBoolean("principal"));
        cd.setActivo(rs.getBoolean("activo"));
        return cd;
    }
}