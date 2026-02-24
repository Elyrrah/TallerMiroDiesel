/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import com.tallermirodiesel.dao.OrdenTrabajoDetalleDAO;
import com.tallermirodiesel.model.OrdenTrabajoDetalle;
import com.tallermirodiesel.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public class OrdenTrabajoDetalleDAOImpl implements OrdenTrabajoDetalleDAO {

    // Inicialización de consultas SQL
    private static final String SELECT_BASE =
        "SELECT " +
        "    d.id_detalle, d.id_orden_trabajo, d.id_servicio, d.cantidad, " +
        "    d.precio_unitario, d.subtotal, d.garantia_meses, d.garantia_dias, " +
        "    d.observaciones, d.activo " +
        "FROM public.orden_trabajo_detalles d ";

    private static final String SQL_INSERT =
        "INSERT INTO public.orden_trabajo_detalles (" +
        "    id_orden_trabajo, id_servicio, cantidad, precio_unitario, " +
        "    garantia_meses, garantia_dias, observaciones, activo" +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
        "RETURNING id_detalle";

    private static final String SQL_UPDATE =
        "UPDATE public.orden_trabajo_detalles SET " +
        "    id_servicio     = ?, " +
        "    cantidad        = ?, " +
        "    precio_unitario = ?, " +
        "    garantia_meses  = ?, " +
        "    garantia_dias   = ?, " +
        "    observaciones   = ? " +
        "WHERE id_detalle = ?";

    private static final String SQL_DESACTIVAR = "UPDATE public.orden_trabajo_detalles SET activo = false WHERE id_detalle = ?";

    private static final String SQL_BUSCAR_POR_ID = SELECT_BASE + "WHERE d.id_detalle = ?";

    private static final String SQL_LISTAR_POR_ORDEN = SELECT_BASE + "WHERE d.id_orden_trabajo = ? AND d.activo = true ORDER BY d.id_detalle ASC";

    // Método para Mapear un Detalle de Orden de Trabajo desde un ResultSet
    private OrdenTrabajoDetalle mapear(ResultSet rs) throws SQLException {
        OrdenTrabajoDetalle d = new OrdenTrabajoDetalle();

        d.setIdDetalle(rs.getLong("id_detalle"));
        d.setIdOrdenTrabajo(rs.getLong("id_orden_trabajo"));
        d.setIdServicio(rs.getLong("id_servicio"));
        d.setCantidad(rs.getBigDecimal("cantidad"));
        d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        d.setSubtotal(rs.getBigDecimal("subtotal"));

        short garantiaMeses = rs.getShort("garantia_meses");
        d.setGarantiaMeses(rs.wasNull() ? null : garantiaMeses);

        short garantiaDias = rs.getShort("garantia_dias");
        d.setGarantiaDias(rs.wasNull() ? null : garantiaDias);

        d.setObservaciones(rs.getString("observaciones"));
        d.setActivo(rs.getBoolean("activo"));

        return d;
    }

    // Método para Crear un nuevo Detalle de Orden de Trabajo y devolver el ID generado
    // Nota: si precio_unitario viene null, el trigger lo completa desde el catálogo de servicios
    @Override
    public Long crear(OrdenTrabajoDetalle d) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, d.getIdOrdenTrabajo());
            ps.setLong(2, d.getIdServicio());
            ps.setBigDecimal(3, d.getCantidad());
            ps.setObject(4, d.getPrecioUnitario(), Types.NUMERIC);
            ps.setObject(5, d.getGarantiaMeses(), Types.SMALLINT);
            ps.setObject(6, d.getGarantiaDias(), Types.SMALLINT);
            ps.setString(7, d.getObservaciones());
            ps.setBoolean(8, d.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id_detalle");
                throw new RuntimeException("No se generó id_detalle al crear el detalle.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear el detalle de la OT: " + e.getMessage(), e);
        }
    }

    // Método para Actualizar los campos editables de un Detalle existente
    @Override
    public boolean actualizar(OrdenTrabajoDetalle d) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setLong(1, d.getIdServicio());
            ps.setBigDecimal(2, d.getCantidad());
            ps.setBigDecimal(3, d.getPrecioUnitario());
            ps.setObject(4, d.getGarantiaMeses(), Types.SMALLINT);
            ps.setObject(5, d.getGarantiaDias(), Types.SMALLINT);
            ps.setString(6, d.getObservaciones());
            ps.setLong(7, d.getIdDetalle());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar el detalle de la OT: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Detalle, el trigger recalcula el total de la OT automáticamente
    @Override
    public boolean desactivar(Long idDetalle) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DESACTIVAR)) {

            ps.setLong(1, idDetalle);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar el detalle de la OT: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Detalle por su ID
    @Override
    public Optional<OrdenTrabajoDetalle> buscarPorId(Long idDetalle) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setLong(1, idDetalle);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar detalle por ID: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Detalles activos de una Orden de Trabajo
    @Override
    public List<OrdenTrabajoDetalle> listarPorOrden(Long idOrdenTrabajo) {
        List<OrdenTrabajoDetalle> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_ORDEN)) {

            ps.setLong(1, idOrdenTrabajo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar detalles de la OT: " + e.getMessage(), e);
        }
    }
}