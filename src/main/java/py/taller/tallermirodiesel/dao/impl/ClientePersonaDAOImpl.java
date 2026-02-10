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
import py.taller.tallermirodiesel.dao.ClientePersonaDAO;
import py.taller.tallermirodiesel.model.ClientePersona;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClientePersonaDAOImpl implements ClientePersonaDAO {

    // Definimos las sentencias SQL utilizadas por este DAO
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

    // Crea o actualiza según exista el id.
    // IMPORTANTE: este DAO NO hace validaciones de negocio. Eso debe ir en el Service.
    // Acá "guardar" NO decide por existencia: intenta UPDATE; si no actualiza filas, intenta INSERT.
    @Override
    public boolean guardar(ClientePersona persona) {
        if (persona == null || persona.getIdCliente() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConexion()) {

            // Intentamos primero actualizar (si no existe, update afectará 0 filas)
            if (actualizar(conn, persona)) {
                return true;
            }

            // Si no actualizó, intentamos insertar (si ya existía, la BD rechazará por PK)
            return insertar(conn, persona);

        } catch (Exception e) {
            throw new RuntimeException("Error al guardar cliente persona", e);
        }
    }

    // Verifica si el cliente ya tiene datos de persona
    @Override
    public boolean existePorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia de cliente persona", e);
        }
    }

    // Busca los datos de persona por id_cliente
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

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar cliente persona", e);
        }
    }

    // Elimina el registro de persona (caso excepcional)
    @Override
    public boolean eliminarPorIdCliente(Long idCliente) {
        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ID_CLIENTE)) {

            ps.setLong(1, idCliente);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar cliente persona", e);
        }
    }

    // Lista todos los clientes persona
    @Override
    public List<ClientePersona> listarTodos() {
        List<ClientePersona> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapPersona(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al listar clientes persona", e);
        }

        return lista;
    }

    // -------------------------
    // Métodos internos (DAO puro)
    // -------------------------

    private boolean insertar(Connection conn, ClientePersona persona) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, persona.getIdCliente());
            ps.setString(2, persona.getNombre());
            ps.setString(3, persona.getApellido());

            // apodo es nullable
            if (persona.getApodo() == null) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, persona.getApodo());
            }

            return ps.executeUpdate() > 0;
        }
    }

    private boolean actualizar(Connection conn, ClientePersona persona) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getApellido());

            // apodo es nullable
            if (persona.getApodo() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, persona.getApodo());
            }

            ps.setLong(4, persona.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    private ClientePersona mapPersona(ResultSet rs) throws Exception {
        ClientePersona p = new ClientePersona();
        p.setIdCliente(rs.getLong("id_cliente"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setApodo(rs.getString("apodo"));
        return p;
    }
}
