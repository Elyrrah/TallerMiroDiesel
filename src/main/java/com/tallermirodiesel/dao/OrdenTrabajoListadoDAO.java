/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import com.tallermirodiesel.dto.OrdenTrabajoListadoDTO;
import com.tallermirodiesel.dto.OrdenTrabajoVerDTO;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 * DAO de lectura para las vistas de órdenes de trabajo.
 * Devuelve DTOs con JOINs ya resueltos, listos para mostrar en el JSP.
 */
public interface OrdenTrabajoListadoDAO {

    // Busca una OT por ID y devuelve todos los datos necesarios para orden_trabajo_ver.jsp
    Optional<OrdenTrabajoVerDTO> buscarVerPorId(Long id);

    // Lista solo las OTs activas (activo = true)
    List<OrdenTrabajoListadoDTO> listarActivos();

    // Lista todas las OTs sin importar si están activas o no
    List<OrdenTrabajoListadoDTO> listarTodos();

    // Lista las OTs que tengan un estado específico (PENDIENTE, EN_PROCESO, etc.)
    List<OrdenTrabajoListadoDTO> listarPorEstado(EstadoOrdenTrabajoEnum estado);

    // Lista las OTs cuya fecha de ingreso corresponda a un día específico
    List<OrdenTrabajoListadoDTO> listarPorDia(LocalDate dia);
}