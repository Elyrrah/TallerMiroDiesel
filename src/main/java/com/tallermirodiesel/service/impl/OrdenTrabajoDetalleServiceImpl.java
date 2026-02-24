/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import com.tallermirodiesel.dao.OrdenTrabajoDAO;
import com.tallermirodiesel.dao.OrdenTrabajoDetalleDAO;
import com.tallermirodiesel.dao.impl.OrdenTrabajoDAOImpl;
import com.tallermirodiesel.dao.impl.OrdenTrabajoDetalleDAOImpl;
import com.tallermirodiesel.model.OrdenTrabajo;
import com.tallermirodiesel.model.OrdenTrabajoDetalle;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.service.OrdenTrabajoDetalleService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public class OrdenTrabajoDetalleServiceImpl implements OrdenTrabajoDetalleService {

    private final OrdenTrabajoDetalleDAO detalleDAO;
    private final OrdenTrabajoDAO ordenTrabajoDAO;

    // Inicialización de las implementaciones de DAO para la gestión de detalles de OT
    public OrdenTrabajoDetalleServiceImpl() {
        this.detalleDAO      = new OrdenTrabajoDetalleDAOImpl();
        this.ordenTrabajoDAO = new OrdenTrabajoDAOImpl();
    }

    // Constructor para inyección de dependencias de los DAOs de detalles y órdenes de trabajo
    public OrdenTrabajoDetalleServiceImpl(OrdenTrabajoDetalleDAO detalleDAO, OrdenTrabajoDAO ordenTrabajoDAO) {
        this.detalleDAO      = detalleDAO;
        this.ordenTrabajoDAO = ordenTrabajoDAO;
    }

    // Verificación de que la OT padre existe y se encuentra en un estado que permite modificar sus servicios
    private OrdenTrabajo verificarOTEditable(Long idOrdenTrabajo) {
        if (idOrdenTrabajo == null || idOrdenTrabajo <= 0) {
            throw new IllegalArgumentException("El id de la OT no es válido.");
        }

        OrdenTrabajo ot = ordenTrabajoDAO.buscarPorId(idOrdenTrabajo)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No existe una OT con id: " + idOrdenTrabajo
                ));

        if (ot.getEstado() == EstadoOrdenTrabajoEnum.FINALIZADA
                || ot.getEstado() == EstadoOrdenTrabajoEnum.ENTREGADA
                || ot.getEstado() == EstadoOrdenTrabajoEnum.CANCELADA) {
            throw new IllegalStateException(
                "No se pueden modificar los servicios de una OT en estado " + ot.getEstado().name() + "."
            );
        }

        return ot;
    }

    // Validaciones de campos obligatorios y rangos numéricos del detalle antes de persistirlo
    private void validarDetalle(OrdenTrabajoDetalle d) {
        if (d.getIdServicio() == null || d.getIdServicio() <= 0) {
            throw new IllegalArgumentException("El servicio es obligatorio.");
        }
        if (d.getCantidad() == null || d.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (d.getPrecioUnitario() != null && d.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
        if (d.getGarantiaMeses() != null && d.getGarantiaMeses() < 0) {
            throw new IllegalArgumentException("Los meses de garantía no pueden ser negativos.");
        }
        if (d.getGarantiaDias() != null && d.getGarantiaDias() < 0) {
            throw new IllegalArgumentException("Los días de garantía no pueden ser negativos.");
        }
        if (d.getObservaciones() != null && d.getObservaciones().isBlank()) {
            d.setObservaciones(null);
        }
    }

    // Validaciones de la OT padre y del detalle antes de agregar una nueva línea de servicio
    @Override
    public Long crear(OrdenTrabajoDetalle detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle no puede ser null.");
        }

        verificarOTEditable(detalle.getIdOrdenTrabajo());
        validarDetalle(detalle);
        detalle.setActivo(true);

        return detalleDAO.crear(detalle);
    }

    // Verificación del estado editable de la OT padre y validación del detalle antes de aplicar cambios
    @Override
    public boolean actualizar(OrdenTrabajoDetalle detalle) {
        if (detalle == null || detalle.getIdDetalle() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar el detalle.");
        }

        OrdenTrabajoDetalle existente = detalleDAO.buscarPorId(detalle.getIdDetalle())
                .orElseThrow(() -> new IllegalArgumentException(
                    "No existe un detalle con id: " + detalle.getIdDetalle()
                ));

        verificarOTEditable(existente.getIdOrdenTrabajo());
        validarDetalle(detalle);

        return detalleDAO.actualizar(detalle);
    }

    // Lógica para quitar un servicio de la OT verificando que esta aún se pueda modificar
    @Override
    public boolean desactivar(Long idDetalle) {
        if (idDetalle == null || idDetalle <= 0) {
            throw new IllegalArgumentException("El id del detalle no es válido.");
        }

        OrdenTrabajoDetalle existente = detalleDAO.buscarPorId(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No existe un detalle con id: " + idDetalle
                ));

        verificarOTEditable(existente.getIdOrdenTrabajo());

        return detalleDAO.desactivar(idDetalle);
    }

    // Lógica para recuperar un detalle por su ID, usada principalmente en el formulario de edición
    @Override
    public Optional<OrdenTrabajoDetalle> buscarPorId(Long idDetalle) {
        if (idDetalle == null || idDetalle <= 0) {
            throw new IllegalArgumentException("El id del detalle no es válido.");
        }
        return detalleDAO.buscarPorId(idDetalle);
    }

    // Lógica para obtener todas las líneas de servicio activas de una OT para mostrarlas en la vista
    @Override
    public List<OrdenTrabajoDetalle> listarPorOrden(Long idOrdenTrabajo) {
        if (idOrdenTrabajo == null || idOrdenTrabajo <= 0) {
            throw new IllegalArgumentException("El id de la OT no es válido.");
        }
        return detalleDAO.listarPorOrden(idOrdenTrabajo);
    }
}