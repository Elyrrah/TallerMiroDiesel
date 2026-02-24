/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import com.tallermirodiesel.model.OrdenTrabajo;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 * Contrato de lógica de negocio para las órdenes de trabajo.
 */
public interface OrdenTrabajoService {

    // Crea una nueva OT y devuelve el ID generado
    Long crear(OrdenTrabajo ot);

    // Actualiza los campos editables de la cabecera de una OT
    boolean actualizar(OrdenTrabajo ot);

    // Desactiva una OT (activo = false). Reversible.
    boolean desactivar(Long id);

    // Activa una OT desactivada
    boolean activar(Long id);

    // Elimina físicamente una OT. Solo se permite si no tiene detalles ni pagos.
    boolean eliminar(Long id);

    // Busca una OT por su ID
    Optional<OrdenTrabajo> buscarPorId(Long id);

    // Busca una OT por su número de orden (ej: OT-2026-02-001)
    Optional<OrdenTrabajo> buscarPorNumeroOrden(String numeroOrden);

    // Lista todas las OTs activas
    List<OrdenTrabajo> listarActivos();

    // Lista todas las OTs sin filtro
    List<OrdenTrabajo> listarTodos();

    // Lista todas las OTs de un cliente específico
    List<OrdenTrabajo> listarPorCliente(Long idCliente);

    // Lista todas las OTs con un estado específico
    List<OrdenTrabajo> listarPorEstado(EstadoOrdenTrabajoEnum estado);

    // Lista las OTs cuya fecha_ingreso esté dentro del rango [desde, hasta]
    List<OrdenTrabajo> listarPorRangoFechas(LocalDate desde, LocalDate hasta);

    // Lista las OTs de un día específico
    List<OrdenTrabajo> listarPorDia(LocalDate dia);
}