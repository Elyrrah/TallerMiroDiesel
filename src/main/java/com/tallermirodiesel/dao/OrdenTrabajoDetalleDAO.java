/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import com.tallermirodiesel.model.OrdenTrabajoDetalle;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public interface OrdenTrabajoDetalleDAO {

    // Crea una línea de servicio a la OT y devuelve el ID generado
    Long crear(OrdenTrabajoDetalle detalle);

    // Actualiza los datos de una línea de servicio existente
    boolean actualizar(OrdenTrabajoDetalle detalle);

    // Desactiva la línea (el trigger recalcula el total de la OT automáticamente)
    boolean desactivar(Long idDetalle);

    // Busca una línea de servicio por su ID
    Optional<OrdenTrabajoDetalle> buscarPorId(Long idDetalle);

    // Lista todas las líneas activas de una OT
    List<OrdenTrabajoDetalle> listarPorOrden(Long idOrdenTrabajo);
}