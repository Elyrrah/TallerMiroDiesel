/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import com.tallermirodiesel.model.OrdenTrabajo;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public interface OrdenTrabajoDAO {

    // Crea una nueva orden de trabajo y devuelve el ID generado
    Long crear(OrdenTrabajo ot);

    // Actualiza los datos editables de la cabecera de una OT existente
    boolean actualizar(OrdenTrabajo ot);

    // Cambia activo = false (la OT queda en el sistema pero oculta)
    boolean desactivar(Long id);

    // Cambia activo = true
    boolean activar(Long id);

    // Borra físicamente el registro. Solo debe usarse si la OT no tiene detalles ni pagos
    boolean eliminar(Long id);

    // Busca una OT por su ID
    Optional<OrdenTrabajo> buscarPorId(Long id);

    // Busca una OT por su número de orden (ej: OT-2026-02-001)
    Optional<OrdenTrabajo> buscarPorNumeroOrden(String numeroOrden);

    // Lista todas las OTs sin filtro
    List<OrdenTrabajo> listarTodos();

    // Lista solo las OTs activas
    List<OrdenTrabajo> listarActivos();

    // Lista solo las OTs inactivas
    List<OrdenTrabajo> listarInactivos();

    // Lista todas las OTs de un cliente específico
    List<OrdenTrabajo> listarPorCliente(Long idCliente);

    // Lista todas las OTs que tienen un estado específico
    List<OrdenTrabajo> listarPorEstado(EstadoOrdenTrabajoEnum estado);

    // Lista todas las OTs cuya fecha_ingreso esté dentro del rango [desde, hasta]
    List<OrdenTrabajo> listarPorRangoFechas(LocalDate desde, LocalDate hasta);

    // Lista todas las OTs cuya fecha_ingreso sea exactamente el día indicado
    List<OrdenTrabajo> listarPorDia(LocalDate dia);
}