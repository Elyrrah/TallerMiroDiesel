/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import com.tallermirodiesel.dao.OrdenTrabajoListadoDAO;
import com.tallermirodiesel.dto.OrdenTrabajoListadoDTO;
import com.tallermirodiesel.dto.OrdenTrabajoVerDTO;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.EstadoPagoEnum;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.model.enums.TipoIngresoOrdenEnum;
import com.tallermirodiesel.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public class OrdenTrabajoListadoDAOImpl implements OrdenTrabajoListadoDAO {

    // Inicialización de consultas SQL
    private static final String SQL_LISTAR_BASE = """
            SELECT
                ot.id_orden_trabajo,
                ot.numero_orden,
                ot.fecha_ingreso,
                ot.estado,
                ot.estado_pago,
                ot.activo,
                ot.total_trabajo,
                ot.tipo_ingreso,
                COALESCE(
                    TRIM(cp.nombre || ' ' || cp.apellido),
                    ce.razon_social
                )                                    AS nombre_cliente,
                v.placa                              AS placa_vehiculo,
                TRIM(mv.nombre || ' ' || mov.nombre) AS marca_modelo_vehiculo,
                TRIM(mc.nombre || ' ' || moc.nombre) AS marca_modelo_componente
            FROM public.ordenes_trabajo ot
            LEFT JOIN public.clientes_persona cp  ON cp.id_cliente      = ot.id_cliente
            LEFT JOIN public.clientes_empresa ce  ON ce.id_cliente      = ot.id_cliente
            LEFT JOIN public.vehiculos v           ON v.id_vehiculo      = ot.id_vehiculo
            LEFT JOIN public.marcas mv             ON mv.id_marca        = v.id_marca
            LEFT JOIN public.modelos mov           ON mov.id_modelo      = v.id_modelo
            LEFT JOIN public.componentes comp      ON comp.id_componente = ot.id_componente
            LEFT JOIN public.marcas mc             ON mc.id_marca        = comp.id_marca
            LEFT JOIN public.modelos moc           ON moc.id_modelo      = comp.id_modelo
            """;

    private static final String SQL_VER_BASE = """
            SELECT
                ot.id_orden_trabajo,
                ot.numero_orden,
                ot.fecha_ingreso,
                ot.fecha_entrega,
                ot.fecha_fin,
                ot.estado,
                ot.estado_pago,
                ot.activo,
                ot.problema_reportado,
                ot.observaciones_ingreso,
                ot.total_trabajo,
                ot.total_pagado,
                ot.saldo_pendiente,
                ot.tipo_ingreso,
                ot.fuente_referencia,
                ot.cantidad_picos,
                ot.id_cliente,
                COALESCE(
                    TRIM(cp.nombre || ' ' || cp.apellido),
                    ce.razon_social
                )                                    AS nombre_cliente,
                c.telefono                           AS telefono_cliente,
                d.nombre                             AS nombre_distrito_cliente,
                l.nombre                             AS nombre_localidad_cliente,
                v.placa                              AS placa_vehiculo,
                mv.nombre                            AS marca_vehiculo,
                mov.nombre                           AS modelo_vehiculo,
                comp.numero_serie                    AS numero_serie_componente,
                mc.nombre                            AS marca_componente,
                moc.nombre                           AS modelo_componente,
                COALESCE(
                    TRIM(rp.nombre || ' ' || rp.apellido),
                    re.razon_social
                )                                    AS nombre_referidor,
                TRIM(u.nombre || ' ' || u.apellido)  AS nombre_usuario
            FROM public.ordenes_trabajo ot
            LEFT JOIN public.clientes c           ON c.id_cliente       = ot.id_cliente
            LEFT JOIN public.clientes_persona cp  ON cp.id_cliente      = ot.id_cliente
            LEFT JOIN public.clientes_empresa ce  ON ce.id_cliente      = ot.id_cliente
            LEFT JOIN public.distritos d          ON d.id_distrito      = c.id_distrito
            LEFT JOIN public.localidades l        ON l.id_localidad     = c.id_localidad
            LEFT JOIN public.vehiculos v          ON v.id_vehiculo      = ot.id_vehiculo
            LEFT JOIN public.marcas mv            ON mv.id_marca        = v.id_marca
            LEFT JOIN public.modelos mov          ON mov.id_modelo      = v.id_modelo
            LEFT JOIN public.componentes comp     ON comp.id_componente = ot.id_componente
            LEFT JOIN public.marcas mc            ON mc.id_marca        = comp.id_marca
            LEFT JOIN public.modelos moc          ON moc.id_modelo      = comp.id_modelo
            LEFT JOIN public.clientes_persona rp  ON rp.id_cliente      = ot.id_referidor
            LEFT JOIN public.clientes_empresa re  ON re.id_cliente      = ot.id_referidor
            JOIN  public.usuarios u               ON u.id_usuario       = ot.id_usuario
            """;

    private static final String SQL_VER_POR_ID =
        SQL_VER_BASE + "WHERE ot.id_orden_trabajo = ?";

    private static final String SQL_LISTAR_ACTIVOS =
        SQL_LISTAR_BASE + "WHERE ot.activo = true ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_TODOS =
        SQL_LISTAR_BASE + "ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_POR_ESTADO =
        SQL_LISTAR_BASE + "WHERE ot.estado = ?::public.estado_orden_trabajo_enum ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_POR_DIA =
        SQL_LISTAR_BASE +
        "WHERE ot.fecha_ingreso >= ?::date " +
        "  AND ot.fecha_ingreso <  (?::date + INTERVAL '1 day') " +
        "ORDER BY ot.fecha_ingreso DESC";

    // Método para Buscar una OT por ID y devolver el DTO completo para orden_trabajo_ver.jsp
    @Override
    public Optional<OrdenTrabajoVerDTO> buscarVerPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_VER_POR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapearVer(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar OT por ID: " + e.getMessage(), e);
        }
    }

    // Método para Listar solo las OTs activas
    @Override
    public List<OrdenTrabajoListadoDTO> listarActivos() {
        return listar(SQL_LISTAR_ACTIVOS);
    }

    // Método para Listar todas las OTs sin importar su estado de activación
    @Override
    public List<OrdenTrabajoListadoDTO> listarTodos() {
        return listar(SQL_LISTAR_TODOS);
    }

    // Método para Listar las OTs filtradas por un estado específico
    @Override
    public List<OrdenTrabajoListadoDTO> listarPorEstado(EstadoOrdenTrabajoEnum estado) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_ESTADO)) {

            ps.setString(1, estado.name());
            return listarConPs(ps);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar OTs por estado: " + e.getMessage(), e);
        }
    }

    // Método para Listar las OTs cuya fecha de ingreso corresponda al día indicado
    @Override
    public List<OrdenTrabajoListadoDTO> listarPorDia(LocalDate dia) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_DIA)) {

            ps.setObject(1, dia);
            ps.setObject(2, dia);
            return listarConPs(ps);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar OTs por día: " + e.getMessage(), e);
        }
    }

    // Método genérico para listar OTs basándose en una consulta SQL sin parámetros
    private List<OrdenTrabajoListadoDTO> listar(String sql) {
        List<OrdenTrabajoListadoDTO> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearListado(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar órdenes de trabajo: " + e.getMessage(), e);
        }

        return lista;
    }

    // Método genérico para listar OTs con un PreparedStatement ya configurado
    private List<OrdenTrabajoListadoDTO> listarConPs(PreparedStatement ps) throws SQLException {
        List<OrdenTrabajoListadoDTO> lista = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearListado(rs));
            }
        }

        return lista;
    }

    // Método para Mapear una fila del ResultSet en un OrdenTrabajoListadoDTO (para el listado)
    private OrdenTrabajoListadoDTO mapearListado(ResultSet rs) throws SQLException {
        OrdenTrabajoListadoDTO dto = new OrdenTrabajoListadoDTO();

        dto.setIdOrdenTrabajo(rs.getLong("id_orden_trabajo"));
        dto.setNumeroOrden(rs.getString("numero_orden"));
        dto.setEstado(EstadoOrdenTrabajoEnum.valueOf(rs.getString("estado")));
        dto.setEstadoPago(EstadoPagoEnum.valueOf(rs.getString("estado_pago")));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setTotalTrabajo(rs.getBigDecimal("total_trabajo"));
        dto.setNombreCliente(rs.getString("nombre_cliente"));

        Timestamp fechaIngreso = rs.getTimestamp("fecha_ingreso");
        if (fechaIngreso != null) dto.setFechaIngreso(fechaIngreso.toLocalDateTime());

        TipoIngresoOrdenEnum tipo = TipoIngresoOrdenEnum.valueOf(rs.getString("tipo_ingreso"));
        dto.setTipoIngreso(tipo);

        if (tipo == TipoIngresoOrdenEnum.VEHICULO) {
            dto.setPlacaVehiculo(rs.getString("placa_vehiculo"));
            dto.setMarcaModelo(rs.getString("marca_modelo_vehiculo"));
        } else {
            dto.setMarcaModelo(rs.getString("marca_modelo_componente"));
        }

        return dto;
    }

    // Método para Mapear una fila del ResultSet en un OrdenTrabajoVerDTO (para la pantalla de detalle)
    private OrdenTrabajoVerDTO mapearVer(ResultSet rs) throws SQLException {
        OrdenTrabajoVerDTO dto = new OrdenTrabajoVerDTO();

        dto.setIdOrdenTrabajo(rs.getLong("id_orden_trabajo"));
        dto.setNumeroOrden(rs.getString("numero_orden"));
        dto.setEstado(EstadoOrdenTrabajoEnum.valueOf(rs.getString("estado")));
        dto.setEstadoPago(EstadoPagoEnum.valueOf(rs.getString("estado_pago")));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setProblemaReportado(rs.getString("problema_reportado"));
        dto.setObservacionesIngreso(rs.getString("observaciones_ingreso"));
        dto.setTotalTrabajo(rs.getBigDecimal("total_trabajo"));
        dto.setTotalPagado(rs.getBigDecimal("total_pagado"));
        dto.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
        dto.setNombreReferidor(rs.getString("nombre_referidor"));
        dto.setNombreUsuario(rs.getString("nombre_usuario"));
        dto.setIdCliente(rs.getLong("id_cliente"));
        dto.setNombreCliente(rs.getString("nombre_cliente"));
        dto.setTelefonoCliente(rs.getString("telefono_cliente"));
        dto.setNombreDistritoCliente(rs.getString("nombre_distrito_cliente"));
        dto.setNombreLocalidadCliente(rs.getString("nombre_localidad_cliente"));

        String fuenteStr = rs.getString("fuente_referencia");
        if (fuenteStr != null) dto.setFuenteReferencia(FuenteReferenciaClienteEnum.valueOf(fuenteStr));

        Timestamp fechaIngreso = rs.getTimestamp("fecha_ingreso");
        if (fechaIngreso != null) dto.setFechaIngreso(fechaIngreso.toLocalDateTime());

        Timestamp fechaEntrega = rs.getTimestamp("fecha_entrega");
        if (fechaEntrega != null) dto.setFechaEntrega(fechaEntrega.toLocalDateTime());

        Timestamp fechaFin = rs.getTimestamp("fecha_fin");
        if (fechaFin != null) dto.setFechaFin(fechaFin.toLocalDateTime());

        TipoIngresoOrdenEnum tipo = TipoIngresoOrdenEnum.valueOf(rs.getString("tipo_ingreso"));
        dto.setTipoIngreso(tipo);

        if (tipo == TipoIngresoOrdenEnum.VEHICULO) {
            dto.setPlacaVehiculo(rs.getString("placa_vehiculo"));
            dto.setMarcaVehiculo(rs.getString("marca_vehiculo"));
            dto.setModeloVehiculo(rs.getString("modelo_vehiculo"));
        } else {
            dto.setNumeroSerieComponente(rs.getString("numero_serie_componente"));
            dto.setMarcaComponente(rs.getString("marca_componente"));
            dto.setModeloComponente(rs.getString("modelo_componente"));
            short cantidadPicos = rs.getShort("cantidad_picos");
            dto.setCantidadPicos(rs.wasNull() ? null : cantidadPicos);
        }

        return dto;
    }
}