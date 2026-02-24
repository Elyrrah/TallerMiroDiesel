/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import com.tallermirodiesel.model.OrdenTrabajoDetalle;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 * Contrato de lógica de negocio para las líneas de servicio de una OT.
 */
public interface OrdenTrabajoDetalleService {

    // Agrega una línea de servicio a la OT
    Long crear(OrdenTrabajoDetalle detalle);

    // Modifica una línea de servicio existente
    boolean actualizar(OrdenTrabajoDetalle detalle);

    // Desactiva una línea de servicio (el trigger recalcula el total de la OT)
    boolean desactivar(Long idDetalle);

    // Busca una línea de servicio por su ID
    Optional<OrdenTrabajoDetalle> buscarPorId(Long idDetalle);

    // Lista todas las líneas activas de una OT
    List<OrdenTrabajoDetalle> listarPorOrden(Long idOrdenTrabajo);
}