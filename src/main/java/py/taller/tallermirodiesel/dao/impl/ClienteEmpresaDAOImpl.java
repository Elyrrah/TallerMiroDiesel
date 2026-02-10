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
import py.taller.tallermirodiesel.dao.ClienteEmpresaDAO;
import py.taller.tallermirodiesel.model.ClienteEmpresa;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteEmpresaDAOImpl implements ClienteEmpresaDAO {

    // Definimos las sentencias SQL utilizadas por este DAO
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

    // Crea o actualiza según exista el id.
    // IMPORTANTE: este DAO NO hace validaciones de negocio. Eso debe ir en el Service.
    // Acá "guardar" NO decide por existencia: intenta UPDATE; si no actualiza filas, intenta INSERT.
    @Override
    public boolean guardar(ClienteEmpresa empresa) {
        if (empresa == null || empresa.getIdCliente() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConexion()) {

            // Intentamos primero actualizar (si no existe, update afectará 0 filas)
            if (actualizar(conn, empresa)) {
                return true;
            }

            // Si no actualizó, intentamos insertar (si ya existía, la BD rechazará por PK)
            return insertar(conn, empresa);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cliente empresa", e);
        }
    }

    // Verifica si el cliente ya tiene datos de empresa
    @Override
    public boolean existePorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de cliente empresa", e);
        }
    }

    // Busca los datos de empresa por id_cliente
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

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente empresa", e);
        }
    }

    // Elimina el registro de empresa (caso excepcional)
    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar cliente empresa", e);
        }
    }

    // Lista todos los clientes empresa
    @Override
    public List<ClienteEmpresa> listarTodos() {
        List<ClienteEmpresa> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapEmpresa(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al listar clientes empresa", e);
        }

        return lista;
    }

    // -------------------------
    // Métodos internos (DAO puro)
    // -------------------------

    private boolean insertar(Connection conn, ClienteEmpresa empresa) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, empresa.getIdCliente());
            ps.setString(2, empresa.getRazonSocial());

            // nombre_fantasia es nullable
            if (empresa.getNombreFantasia() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, empresa.getNombreFantasia());
            }

            return ps.executeUpdate() > 0;
        }
    }

    private boolean actualizar(Connection conn, ClienteEmpresa empresa) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, empresa.getRazonSocial());

            // nombre_fantasia es nullable
            if (empresa.getNombreFantasia() == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, empresa.getNombreFantasia());
            }

            ps.setLong(3, empresa.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    private ClienteEmpresa mapEmpresa(ResultSet rs) throws Exception {
        ClienteEmpresa e = new ClienteEmpresa();
        e.setIdCliente(rs.getLong("id_cliente"));
        e.setRazonSocial(rs.getString("razon_social"));
        e.setNombreFantasia(rs.getString("nombre_fantasia"));
        return e;
    }
}
