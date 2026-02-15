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
import com.tallermirodiesel.dao.ClienteListadoDAO;
import com.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import com.tallermirodiesel.dto.ClientePersonaListadoDTO;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ClienteListadoDAOImpl implements ClienteListadoDAO {

    private static final String SQL_LISTAR_PERSONAS = """
        SELECT
            c.id_cliente,
            c.id_localidad,
            c.id_distrito,
            c.id_cliente_referidor,
            c.fuente_referencia,
            c.telefono,
            c.activo,
            c.fecha_creacion,

            cp.nombre,
            cp.apellido,
            cp.apodo,

            d.nombre  AS nombre_distrito,
            l.nombre  AS nombre_localidad,

            COALESCE(
                NULLIF(TRIM(COALESCE(cp_ref.nombre,'') || ' ' || COALESCE(cp_ref.apellido,'')), ''),
                NULLIF(TRIM(ce_ref.nombre_fantasia), ''),
                NULLIF(TRIM(ce_ref.razon_social), '')
            ) AS nombre_referidor

        FROM public.clientes c
        JOIN public.clientes_persona cp ON cp.id_cliente = c.id_cliente
        LEFT JOIN public.distritos d ON d.id_distrito = c.id_distrito
        LEFT JOIN public.localidades l ON l.id_localidad = c.id_localidad
        LEFT JOIN public.clientes_persona cp_ref ON cp_ref.id_cliente = c.id_cliente_referidor
        LEFT JOIN public.clientes_empresa ce_ref ON ce_ref.id_cliente = c.id_cliente_referidor
        WHERE ( ? IS NULL OR (cp.nombre ILIKE '%'||?||'%' OR cp.apellido ILIKE '%'||?||'%' OR c.telefono ILIKE '%'||?||'%') )
          AND ( ? IS NULL OR c.activo = ? )
        ORDER BY c.id_cliente ASC
    """;

    private static final String SQL_LISTAR_EMPRESAS = """
        SELECT
            c.id_cliente,
            c.id_localidad,
            c.id_distrito,
            c.id_cliente_referidor,
            c.fuente_referencia,
            c.telefono,
            c.activo,
            c.fecha_creacion,

            ce.razon_social,
            ce.nombre_fantasia,

            d.nombre  AS nombre_distrito,
            l.nombre  AS nombre_localidad,

            COALESCE(
                NULLIF(TRIM(COALESCE(cp_ref.nombre,'') || ' ' || COALESCE(cp_ref.apellido,'')), ''),
                NULLIF(TRIM(ce_ref.nombre_fantasia), ''),
                NULLIF(TRIM(ce_ref.razon_social), '')
            ) AS nombre_referidor

        FROM public.clientes c
        JOIN public.clientes_empresa ce ON ce.id_cliente = c.id_cliente
        LEFT JOIN public.distritos d ON d.id_distrito = c.id_distrito
        LEFT JOIN public.localidades l ON l.id_localidad = c.id_localidad
        LEFT JOIN public.clientes_persona cp_ref ON cp_ref.id_cliente = c.id_cliente_referidor
        LEFT JOIN public.clientes_empresa ce_ref ON ce_ref.id_cliente = c.id_cliente_referidor
        WHERE ( ? IS NULL OR (ce.razon_social ILIKE '%'||?||'%' OR ce.nombre_fantasia ILIKE '%'||?||'%' OR c.telefono ILIKE '%'||?||'%') )
          AND ( ? IS NULL OR c.activo = ? )
        ORDER BY c.id_cliente ASC
    """;

    @Override
    public List<ClientePersonaListadoDTO> listarPersonas(String q, Boolean activo) {
        String qNorm = (q == null || q.trim().isBlank()) ? null : q.trim();

        List<ClientePersonaListadoDTO> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_PERSONAS)) {

            ps.setString(1, qNorm);
            ps.setString(2, qNorm);
            ps.setString(3, qNorm);
            ps.setString(4, qNorm);

            if (activo == null) {
                ps.setNull(5, Types.BOOLEAN);
                ps.setNull(6, Types.BOOLEAN);
            } else {
                ps.setBoolean(5, activo);
                ps.setBoolean(6, activo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapPersona(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar clientes persona (DTO): " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClienteEmpresaListadoDTO> listarEmpresas(String q, Boolean activo) {
        String qNorm = (q == null || q.trim().isBlank()) ? null : q.trim();

        List<ClienteEmpresaListadoDTO> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR_EMPRESAS)) {

            ps.setString(1, qNorm);
            ps.setString(2, qNorm);
            ps.setString(3, qNorm);
            ps.setString(4, qNorm);

            if (activo == null) {
                ps.setNull(5, Types.BOOLEAN);
                ps.setNull(6, Types.BOOLEAN);
            } else {
                ps.setBoolean(5, activo);
                ps.setBoolean(6, activo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapEmpresa(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar clientes empresa (DTO): " + e.getMessage(), e);
        }
    }

    private ClientePersonaListadoDTO mapPersona(ResultSet rs) throws SQLException {
        ClientePersonaListadoDTO dto = new ClientePersonaListadoDTO();

        dto.setIdCliente(rs.getLong("id_cliente"));
        dto.setIdLocalidad((Long) rs.getObject("id_localidad"));
        dto.setIdDistrito((Long) rs.getObject("id_distrito"));
        dto.setIdClienteReferidor((Long) rs.getObject("id_cliente_referidor"));

        String fuente = rs.getString("fuente_referencia");
        dto.setFuenteReferencia(fuente != null ? FuenteReferenciaClienteEnum.valueOf(fuente) : FuenteReferenciaClienteEnum.NINGUNA);

        dto.setTelefono(rs.getString("telefono"));
        dto.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) dto.setFechaCreacion(ts.toLocalDateTime());

        dto.setNombre(rs.getString("nombre"));
        dto.setApellido(rs.getString("apellido"));
        dto.setApodo(rs.getString("apodo"));

        dto.setNombreDistrito(rs.getString("nombre_distrito"));
        dto.setNombreLocalidad(rs.getString("nombre_localidad"));
        dto.setNombreReferidor(rs.getString("nombre_referidor"));

        return dto;
    }

    private ClienteEmpresaListadoDTO mapEmpresa(ResultSet rs) throws SQLException {
        ClienteEmpresaListadoDTO dto = new ClienteEmpresaListadoDTO();

        dto.setIdCliente(rs.getLong("id_cliente"));
        dto.setIdLocalidad((Long) rs.getObject("id_localidad"));
        dto.setIdDistrito((Long) rs.getObject("id_distrito"));
        dto.setIdClienteReferidor((Long) rs.getObject("id_cliente_referidor"));

        String fuente = rs.getString("fuente_referencia");
        dto.setFuenteReferencia(fuente != null ? FuenteReferenciaClienteEnum.valueOf(fuente) : FuenteReferenciaClienteEnum.NINGUNA);

        dto.setTelefono(rs.getString("telefono"));
        dto.setActivo(rs.getBoolean("activo"));

        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) dto.setFechaCreacion(ts.toLocalDateTime());

        dto.setRazonSocial(rs.getString("razon_social"));
        dto.setNombreFantasia(rs.getString("nombre_fantasia"));

        dto.setNombreDistrito(rs.getString("nombre_distrito"));
        dto.setNombreLocalidad(rs.getString("nombre_localidad"));
        dto.setNombreReferidor(rs.getString("nombre_referidor"));

        return dto;
    }
}