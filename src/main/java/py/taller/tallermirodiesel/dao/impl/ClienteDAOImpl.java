/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.ClienteDAO;
import py.taller.tallermirodiesel.model.Cliente;
import py.taller.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteDAOImpl implements ClienteDAO {

    // Definimos las sentencias SQL utilizadas por este DAO
    private static final String SQL_INSERT =
        "INSERT INTO clientes (" +
        "id_localidad, id_distrito, id_cliente_referidor, fuente_referencia, telefono, activo" +
        ") VALUES (?, ?, ?, ?::fuente_referencia_cliente_enum, ?, ?) " +
        "RETURNING id_cliente";

    private static final String SQL_UPDATE =
        "UPDATE clientes SET " +
        "id_localidad = ?, id_distrito = ?, " +
        "id_cliente_referidor = ?, fuente_referencia = ?::fuente_referencia_cliente_enum, telefono = ? " +
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

    // Crea o actualiza según exista el id. Devuelve el id (nuevo o existente)
    // IMPORTANTE: este DAO NO hace validaciones de negocio. Eso debe ir en el Service.
    @Override
    public Long guardar(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        try (Connection conn = DatabaseConnection.getConexion()) {

            // INSERT si no hay id
            if (cliente.getIdCliente() == null) {
                Long idGenerado = insertar(conn, cliente);
                if (idGenerado != null) {
                    cliente.setIdCliente(idGenerado);
                }
                return idGenerado;
            }

            // UPDATE si hay id (sin validar existencia: eso lo maneja el Service)
            boolean ok = actualizar(conn, cliente);
            return ok ? cliente.getIdCliente() : null;

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cliente", e);
        }
    }

    // Cambia el estado activo/inactivo en una sola operación
    @Override
    public boolean setActivo(Long idCliente, boolean activo) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SET_ACTIVO)) {

            ps.setBoolean(1, activo);
            ps.setLong(2, idCliente);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar estado del cliente", e);
        }
    }

    // Verifica si existe un cliente por ID
    @Override
    public boolean existePorId(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID)) {

            ps.setLong(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia del cliente", e);
        }
    }

    // Busca un cliente por su ID
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

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente", e);
        }
    }

    // Lista todos los clientes
    @Override
    public List<Cliente> listarTodos() {
        return listar(SQL_LISTAR);
    }

    // Lista solo clientes activos
    @Override
    public List<Cliente> listarActivos() {
        return listar(SQL_LISTAR_ACTIVOS);
    }

    // Lista solo clientes inactivos
    @Override
    public List<Cliente> listarInactivos() {
        return listar(SQL_LISTAR_INACTIVOS);
    }

    // Búsqueda simple por texto (q) y filtro opcional de activo (null = todos)
    @Override
    public List<Cliente> buscar(String q, Boolean activo) {
        List<Cliente> lista = new ArrayList<>();

        String qNormalizado = (q == null || q.trim().isEmpty()) ? null : q.trim();

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

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar clientes", e);
        }
    }

    // -------------------------
    // Métodos internos (DAO puro)
    // -------------------------

    private Long insertar(Connection conn, Cliente cliente) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setObject(1, cliente.getIdLocalidad(), Types.BIGINT);
            ps.setObject(2, cliente.getIdDistrito(), Types.BIGINT);
            ps.setObject(3, cliente.getIdClienteReferidor(), Types.BIGINT);

            // Fuente de referencia (enum o null). Las reglas (null vs NINGUNA) van en Service.
            ps.setString(4, cliente.getFuenteReferencia() != null ? cliente.getFuenteReferencia().name() : null);

            ps.setString(5, cliente.getTelefono());
            ps.setBoolean(6, cliente.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("id_cliente") : null;
            }
        }
    }

    private boolean actualizar(Connection conn, Cliente cliente) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setObject(1, cliente.getIdLocalidad(), Types.BIGINT);
            ps.setObject(2, cliente.getIdDistrito(), Types.BIGINT);
            ps.setObject(3, cliente.getIdClienteReferidor(), Types.BIGINT);

            // Fuente de referencia (enum o null). Las reglas (null vs NINGUNA) van en Service.
            ps.setString(4, cliente.getFuenteReferencia() != null ? cliente.getFuenteReferencia().name() : null);

            ps.setString(5, cliente.getTelefono());
            ps.setLong(6, cliente.getIdCliente());

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

        } catch (Exception e) {
            throw new RuntimeException("Error al listar clientes", e);
        }

        return lista;
    }

    // Mapeo centralizado de ResultSet -> Cliente
    private Cliente mapCliente(ResultSet rs) throws Exception {
        Cliente c = new Cliente();

        c.setIdCliente(rs.getLong("id_cliente"));
        c.setTelefono(rs.getString("telefono"));

        c.setIdDistrito((Long) rs.getObject("id_distrito"));
        c.setIdLocalidad((Long) rs.getObject("id_localidad"));
        c.setIdClienteReferidor((Long) rs.getObject("id_cliente_referidor"));

        String fuente = rs.getString("fuente_referencia");
        if (fuente != null) {
            c.setFuenteReferencia(FuenteReferenciaClienteEnum.valueOf(fuente));
        } else {
            c.setFuenteReferencia(FuenteReferenciaClienteEnum.NINGUNA);
        }

        c.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            c.setFechaCreacion(ts.toLocalDateTime());
        }

        return c;
    }
}
