/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.ClienteDocumentoDAO;
import py.taller.tallermirodiesel.model.ClienteDocumento;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteDocumentoDAOImpl implements ClienteDocumentoDAO {

    // Definimos las sentencias SQL utilizadas por este DAO
    private static final String SQL_INSERT =
        "INSERT INTO cliente_documentos (id_cliente, id_tipo_documento, numero, principal, activo) " +
        "VALUES (?, ?, ?, ?, ?) " +
        "RETURNING id_cliente_documento";

    private static final String SQL_UPDATE =
        "UPDATE cliente_documentos SET id_tipo_documento = ?, numero = ?, principal = ?, activo = ? " +
        "WHERE id_cliente_documento = ?";

    private static final String SQL_EXISTE_ID =
        "SELECT 1 FROM cliente_documentos WHERE id_cliente_documento = ?";

    private static final String SQL_SET_ACTIVO =
        "UPDATE cliente_documentos SET activo = ? WHERE id_cliente_documento = ?";

    private static final String SQL_EXISTE_POR_CLIENTE_TIPO_NUMERO =
        "SELECT 1 FROM cliente_documentos " +
        "WHERE id_cliente = ? AND id_tipo_documento = ? AND numero = ?";

    private static final String SQL_BUSCAR_ID =
        "SELECT * FROM cliente_documentos WHERE id_cliente_documento = ?";

    private static final String SQL_LISTAR_POR_CLIENTE =
        "SELECT * FROM cliente_documentos WHERE id_cliente = ? ORDER BY id_cliente_documento ASC";

    private static final String SQL_LISTAR_ACTIVOS_POR_CLIENTE =
        "SELECT * FROM cliente_documentos WHERE id_cliente = ? AND activo = true ORDER BY id_cliente_documento ASC";

    private static final String SQL_OBTENER_PRINCIPAL =
        "SELECT * FROM cliente_documentos WHERE id_cliente = ? AND principal = true AND activo = true";

    private static final String SQL_DESMARCAR_PRINCIPALES =
        "UPDATE cliente_documentos SET principal = false WHERE id_cliente = ?";

    private static final String SQL_MARCAR_PRINCIPAL =
        "UPDATE cliente_documentos SET principal = true WHERE id_cliente_documento = ?";

    // Guardar (crea o actualiza según exista el id)
    @Override
    public boolean guardar(ClienteDocumento clienteDocumento) {
        // Si tiene id y existe => UPDATE, si no => INSERT
        Long id = clienteDocumento.getIdClienteDocumento();

        if (id != null && existePorId(id)) {
            return actualizarInterno(clienteDocumento);
        }

        // ✅ Ahora crearInterno retorna el ID generado
        Long idGenerado = crearInterno(clienteDocumento);
        return idGenerado != null;
    }

    // Verifica si existe un documento por ID
    @Override
    public boolean existePorId(Long idClienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID)) {

            ps.setLong(1, idClienteDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia del documento", e);
        }
    }

    // Cambia el estado activo/inactivo en una sola operación
    @Override
    public boolean setActivo(Long idClienteDocumento, boolean activo) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, activo);
            ps.setLong(2, idClienteDocumento);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar estado del documento", e);
        }
    }

    // Define un documento como principal para el cliente (DAO puro: sin reglas/validaciones)
    @Override
    public boolean definirPrincipal(Long idCliente, Long idClienteDocumento) {
        try (Connection conn = DatabaseConnection.getConexion()) {

            conn.setAutoCommit(false);

            try (
                PreparedStatement psDesmarcar = conn.prepareStatement(SQL_DESMARCAR_PRINCIPALES);
                PreparedStatement psMarcar = conn.prepareStatement(SQL_MARCAR_PRINCIPAL)
            ) {

                // 1) Desmarcar todos los principales del cliente
                psDesmarcar.setLong(1, idCliente);
                psDesmarcar.executeUpdate();

                // 2) Marcar como principal el documento indicado
                psMarcar.setLong(1, idClienteDocumento);
                int filas = psMarcar.executeUpdate();

                conn.commit();
                return filas > 0;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al definir documento principal", e);
        }
    }

    // Verifica si ya existe un documento con ese tipo y número para el cliente
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

        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia del documento por cliente/tipo/numero", e);
        }
    }

    // Busca un documento por su ID
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

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente documento", e);
        }
    }

    // Lista todos los documentos de un cliente
    @Override
    public List<ClienteDocumento> listarPorCliente(Long idCliente) {
        return listar(SQL_LISTAR_POR_CLIENTE, idCliente);
    }

    // Lista solo los documentos activos de un cliente
    @Override
    public List<ClienteDocumento> listarActivosPorCliente(Long idCliente) {
        return listar(SQL_LISTAR_ACTIVOS_POR_CLIENTE, idCliente);
    }

    // Obtiene el documento principal activo de un cliente
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

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener documento principal", e);
        }
    }

    // ========================
    // Implementación interna (crear/actualizar)
    // ========================

    // Crear documento (interno) - RETORNA EL ID GENERADO
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
                    clienteDocumento.setIdClienteDocumento(idGenerado); // ✅ Setea el ID en el objeto
                    return idGenerado;
                }
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar (crear) cliente documento", e);
        }
    }

    // Actualizar documento (interno)
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

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar (actualizar) cliente documento", e);
        }
    }

    // ========================
    // Métodos auxiliares
    // ========================

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

        } catch (Exception e) {
            throw new RuntimeException("Error al listar documentos", e);
        }

        return lista;
    }

    private ClienteDocumento mapear(ResultSet rs) throws Exception {
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