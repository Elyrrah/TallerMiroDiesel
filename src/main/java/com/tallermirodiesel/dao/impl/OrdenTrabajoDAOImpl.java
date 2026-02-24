/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import com.tallermirodiesel.dao.OrdenTrabajoDAO;
import com.tallermirodiesel.model.OrdenTrabajo;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.EstadoPagoEnum;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.model.enums.TipoIngresoOrdenEnum;
import com.tallermirodiesel.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public class OrdenTrabajoDAOImpl implements OrdenTrabajoDAO {

    // Inicialización de consultas SQL
    private static final String SQL_INSERT =
        "INSERT INTO public.ordenes_trabajo " +
        "(id_usuario, id_cliente, tipo_ingreso, id_vehiculo, id_componente, cantidad_picos, " +
        " fuente_referencia, id_referidor, problema_reportado, observaciones_ingreso, fecha_entrega, activo) " +
        "VALUES (?, ?, ?::public.tipo_ingreso_orden_enum, ?, ?, ?, ?::public.fuente_referencia_enum, ?, ?, ?, ?, ?) " +
        "RETURNING id_orden_trabajo";

    private static final String SQL_UPDATE =
        "UPDATE public.ordenes_trabajo " +
        "SET id_vehiculo = ?, id_componente = ?, cantidad_picos = ?, " +
        "    fuente_referencia = ?::public.fuente_referencia_enum, id_referidor = ?, " +
        "    problema_reportado = ?, observaciones_ingreso = ?, fecha_entrega = ? " +
        "WHERE id_orden_trabajo = ?";

    private static final String SQL_DESACTIVAR =
        "UPDATE public.ordenes_trabajo SET activo = false WHERE id_orden_trabajo = ?";

    private static final String SQL_ACTIVAR =
        "UPDATE public.ordenes_trabajo SET activo = true WHERE id_orden_trabajo = ?";

    private static final String SQL_ELIMINAR =
        "DELETE FROM public.ordenes_trabajo WHERE id_orden_trabajo = ?";

    private static final String SQL_SELECT_BASE =
        "SELECT ot.id_orden_trabajo, ot.numero_orden, ot.id_usuario, ot.id_cliente, " +
        "       ot.tipo_ingreso, ot.id_vehiculo, ot.id_componente, ot.cantidad_picos, " +
        "       ot.fuente_referencia, ot.id_referidor, ot.fecha_ingreso, " +
        "       ot.problema_reportado, ot.observaciones_ingreso, ot.estado, " +
        "       ot.fecha_entrega, ot.fecha_fin, ot.total_trabajo, ot.total_pagado, " +
        "       ot.saldo_pendiente, ot.estado_pago, ot.activo, ot.fecha_creacion " +
        "FROM public.ordenes_trabajo ot ";

    private static final String SQL_BUSCAR_ID =
        SQL_SELECT_BASE + "WHERE ot.id_orden_trabajo = ?";

    private static final String SQL_BUSCAR_NUMERO =
        SQL_SELECT_BASE + "WHERE ot.numero_orden = ?";

    private static final String SQL_LISTAR =
        SQL_SELECT_BASE + "ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_ACTIVOS =
        SQL_SELECT_BASE + "WHERE ot.activo = true ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_INACTIVOS =
        SQL_SELECT_BASE + "WHERE ot.activo = false ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_POR_CLIENTE =
        SQL_SELECT_BASE + "WHERE ot.id_cliente = ? ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_POR_ESTADO =
        SQL_SELECT_BASE + "WHERE ot.estado = ?::public.estado_orden_trabajo_enum ORDER BY ot.fecha_ingreso DESC";

    private static final String SQL_LISTAR_POR_RANGO_FECHAS =
        SQL_SELECT_BASE +
        "WHERE ot.fecha_ingreso >= ?::date " +
        "  AND ot.fecha_ingreso <  (?::date + INTERVAL '1 day') " +
        "ORDER BY ot.fecha_ingreso DESC";

    // Método para Crear una nueva Orden de Trabajo, devuelve el ID generado por la BD
    @Override
    public Long crear(OrdenTrabajo ot) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, ot.getIdUsuario());
            ps.setLong(2, ot.getIdCliente());
            ps.setString(3, ot.getTipoIngreso().name());
            ps.setObject(4, ot.getIdVehiculo(), Types.BIGINT);
            ps.setObject(5, ot.getIdComponente(), Types.BIGINT);
            ps.setObject(6, ot.getCantidadPicos(), Types.SMALLINT);
            ps.setString(7, ot.getFuenteReferencia().name());
            ps.setObject(8, ot.getIdReferidor(), Types.BIGINT);
            ps.setString(9, ot.getProblemaReportado());
            ps.setString(10, ot.getObservacionesIngreso());
            ps.setObject(11, ot.getFechaEntrega() != null
                    ? java.sql.Timestamp.valueOf(ot.getFechaEntrega()) : null, Types.TIMESTAMP);
            ps.setBoolean(12, ot.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id_orden_trabajo");
                throw new RuntimeException("No se generó id_orden_trabajo al crear la OT.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear la orden de trabajo: " + e.getMessage(), e);
        }
    }

    // Método para Actualizar los campos editables de una Orden de Trabajo existente
    @Override
    public boolean actualizar(OrdenTrabajo ot) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setObject(1, ot.getIdVehiculo(), Types.BIGINT);
            ps.setObject(2, ot.getIdComponente(), Types.BIGINT);
            ps.setObject(3, ot.getCantidadPicos(), Types.SMALLINT);
            ps.setString(4, ot.getFuenteReferencia().name());
            ps.setObject(5, ot.getIdReferidor(), Types.BIGINT);
            ps.setString(6, ot.getProblemaReportado());
            ps.setString(7, ot.getObservacionesIngreso());
            ps.setObject(8, ot.getFechaEntrega() != null
                    ? java.sql.Timestamp.valueOf(ot.getFechaEntrega()) : null, Types.TIMESTAMP);
            ps.setLong(9, ot.getIdOrdenTrabajo());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar la orden de trabajo: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar una Orden de Trabajo (borrado lógico)
    @Override
    public boolean desactivar(Long id) {
        return ejecutarUpdate(SQL_DESACTIVAR, id, "desactivar");
    }

    // Método para Activar una Orden de Trabajo previamente desactivada
    @Override
    public boolean activar(Long id) {
        return ejecutarUpdate(SQL_ACTIVAR, id, "activar");
    }

    // Método para Eliminar físicamente una Orden de Trabajo de la base de datos
    @Override
    public boolean eliminar(Long id) {
        return ejecutarUpdate(SQL_ELIMINAR, id, "eliminar");
    }

    // Método para Buscar una Orden de Trabajo por su ID
    @Override
    public Optional<OrdenTrabajo> buscarPorId(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar OT por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar una Orden de Trabajo por su número de orden (ej: OT-2025-01-001)
    @Override
    public Optional<OrdenTrabajo> buscarPorNumeroOrden(String numeroOrden) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_NUMERO)) {

            ps.setString(1, numeroOrden.trim().toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar OT por número de orden: " + e.getMessage(), e);
        }
    }

    // Método para Listar todas las Órdenes de Trabajo sin importar su estado
    @Override
    public List<OrdenTrabajo> listarTodos() {
        return listar(SQL_LISTAR);
    }

    // Método para Listar solo las Órdenes de Trabajo activas
    @Override
    public List<OrdenTrabajo> listarActivos() {
        return listar(SQL_LISTAR_ACTIVOS);
    }

    // Método para Listar solo las Órdenes de Trabajo inactivas
    @Override
    public List<OrdenTrabajo> listarInactivos() {
        return listar(SQL_LISTAR_INACTIVOS);
    }

    // Método para Listar todas las Órdenes de Trabajo de un Cliente específico
    @Override
    public List<OrdenTrabajo> listarPorCliente(Long idCliente) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_CLIENTE)) {

            ps.setLong(1, idCliente);
            return listarConPs(ps);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar OTs por cliente: " + e.getMessage(), e);
        }
    }

    // Método para Listar las Órdenes de Trabajo filtradas por un estado específico
    @Override
    public List<OrdenTrabajo> listarPorEstado(EstadoOrdenTrabajoEnum estado) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_ESTADO)) {

            ps.setString(1, estado.name());
            return listarConPs(ps);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar OTs por estado: " + e.getMessage(), e);
        }
    }

    // Método para Listar las Órdenes de Trabajo dentro de un rango de fechas de ingreso
    @Override
    public List<OrdenTrabajo> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_RANGO_FECHAS)) {

            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            return listarConPs(ps);

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar OTs por rango de fechas: " + e.getMessage(), e);
        }
    }

    // Método para Listar las Órdenes de Trabajo de un día específico
    @Override
    public List<OrdenTrabajo> listarPorDia(LocalDate dia) {
        return listarPorRangoFechas(dia, dia);
    }

    // Método privado para ejecutar un UPDATE simple (desactivar, activar, eliminar)
    private boolean ejecutarUpdate(String sql, Long id, String operacion) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al " + operacion + " la orden de trabajo: " + e.getMessage(), e);
        }
    }

    // Método genérico para listar Órdenes de Trabajo basándose en una consulta SQL sin parámetros
    private List<OrdenTrabajo> listar(String sql) {
        List<OrdenTrabajo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar órdenes de trabajo: " + e.getMessage(), e);
        }

        return lista;
    }

    // Método genérico para listar Órdenes de Trabajo con un PreparedStatement ya configurado
    private List<OrdenTrabajo> listarConPs(PreparedStatement ps) throws SQLException {
        List<OrdenTrabajo> lista = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    // Método para Mapear una Orden de Trabajo desde un ResultSet
    private OrdenTrabajo mapear(ResultSet rs) throws SQLException {
        OrdenTrabajo ot = new OrdenTrabajo();

        ot.setIdOrdenTrabajo(rs.getLong("id_orden_trabajo"));
        ot.setNumeroOrden(rs.getString("numero_orden"));
        ot.setIdUsuario(rs.getLong("id_usuario"));
        ot.setIdCliente(rs.getLong("id_cliente"));
        ot.setTipoIngreso(TipoIngresoOrdenEnum.valueOf(rs.getString("tipo_ingreso")));

        long idVehiculo = rs.getLong("id_vehiculo");
        ot.setIdVehiculo(rs.wasNull() ? null : idVehiculo);

        long idComponente = rs.getLong("id_componente");
        ot.setIdComponente(rs.wasNull() ? null : idComponente);

        short cantidadPicos = rs.getShort("cantidad_picos");
        ot.setCantidadPicos(rs.wasNull() ? null : cantidadPicos);

        ot.setFuenteReferencia(FuenteReferenciaClienteEnum.valueOf(rs.getString("fuente_referencia")));

        long idReferidor = rs.getLong("id_referidor");
        ot.setIdReferidor(rs.wasNull() ? null : idReferidor);

        var fechaIngreso = rs.getTimestamp("fecha_ingreso");
        ot.setFechaIngreso(fechaIngreso != null ? fechaIngreso.toLocalDateTime() : null);

        ot.setProblemaReportado(rs.getString("problema_reportado"));
        ot.setObservacionesIngreso(rs.getString("observaciones_ingreso"));
        ot.setEstado(EstadoOrdenTrabajoEnum.valueOf(rs.getString("estado")));

        var fechaEntrega = rs.getTimestamp("fecha_entrega");
        ot.setFechaEntrega(fechaEntrega != null ? fechaEntrega.toLocalDateTime() : null);

        var fechaFin = rs.getTimestamp("fecha_fin");
        ot.setFechaFin(fechaFin != null ? fechaFin.toLocalDateTime() : null);

        ot.setTotalTrabajo(rs.getBigDecimal("total_trabajo"));
        ot.setTotalPagado(rs.getBigDecimal("total_pagado"));
        ot.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
        ot.setEstadoPago(EstadoPagoEnum.valueOf(rs.getString("estado_pago")));
        ot.setActivo(rs.getBoolean("activo"));

        var fechaCreacion = rs.getTimestamp("fecha_creacion");
        ot.setFechaCreacion(fechaCreacion != null ? fechaCreacion.toLocalDateTime() : null);

        return ot;
    }
}