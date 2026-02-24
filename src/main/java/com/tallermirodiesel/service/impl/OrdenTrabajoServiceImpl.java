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
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.service.OrdenTrabajoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author elyrr
 */
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private final OrdenTrabajoDAO ordenTrabajoDAO;
    private final OrdenTrabajoDetalleDAO ordenTrabajoDetalleDAO;

    // Inicialización de las implementaciones de DAO para la gestión de órdenes de trabajo
    public OrdenTrabajoServiceImpl() {
        this.ordenTrabajoDAO        = new OrdenTrabajoDAOImpl();
        this.ordenTrabajoDetalleDAO = new OrdenTrabajoDetalleDAOImpl();
    }

    // Constructor para inyección de dependencias de los DAOs de órdenes de trabajo
    public OrdenTrabajoServiceImpl(OrdenTrabajoDAO ordenTrabajoDAO, OrdenTrabajoDetalleDAO ordenTrabajoDetalleDAO) {
        this.ordenTrabajoDAO        = ordenTrabajoDAO;
        this.ordenTrabajoDetalleDAO = ordenTrabajoDetalleDAO;
    }

    // Validaciones del tipo de ingreso, objeto asociado y datos de referencia de la cabecera
    private void validarCabecera(OrdenTrabajo ot) {
        if (ot.getTipoIngreso() == null) {
            throw new IllegalArgumentException("El tipo de ingreso es obligatorio (VEHICULO o COMPONENTE).");
        }

        switch (ot.getTipoIngreso()) {
            case VEHICULO -> {
                if (ot.getIdVehiculo() == null || ot.getIdVehiculo() <= 0) {
                    throw new IllegalArgumentException("Debe indicar el vehículo para una OT de tipo VEHICULO.");
                }
                if (ot.getIdComponente() != null) {
                    throw new IllegalArgumentException("Una OT de tipo VEHICULO no puede tener un componente asociado.");
                }
                if (ot.getCantidadPicos() != null) {
                    throw new IllegalArgumentException("La cantidad de picos solo aplica para OTs de tipo COMPONENTE.");
                }
            }
            case COMPONENTE -> {
                if (ot.getIdComponente() == null || ot.getIdComponente() <= 0) {
                    throw new IllegalArgumentException("Debe indicar el componente para una OT de tipo COMPONENTE.");
                }
                if (ot.getIdVehiculo() != null) {
                    throw new IllegalArgumentException("Una OT de tipo COMPONENTE no puede tener un vehículo asociado.");
                }
                if (ot.getCantidadPicos() != null && (ot.getCantidadPicos() < 1 || ot.getCantidadPicos() > 12)) {
                    throw new IllegalArgumentException("La cantidad de picos debe estar entre 1 y 12.");
                }
            }
        }

        if (ot.getFuenteReferencia() == null) {
            ot.setFuenteReferencia(FuenteReferenciaClienteEnum.NINGUNA);
        }

        if (ot.getFuenteReferencia() == FuenteReferenciaClienteEnum.NINGUNA) {
            if (ot.getIdReferidor() != null) {
                throw new IllegalArgumentException("Si la fuente de referencia es NINGUNA, no debe indicarse un referidor.");
            }
        } else {
            if (ot.getIdReferidor() == null || ot.getIdReferidor() <= 0) {
                throw new IllegalArgumentException("Debe indicar el referidor cuando la fuente de referencia es " + ot.getFuenteReferencia().name() + ".");
            }
            if (ot.getIdReferidor().equals(ot.getIdCliente())) {
                throw new IllegalArgumentException("El referidor no puede ser el mismo cliente de la orden.");
            }
        }
    }

    // Verificación de que la OT no esté en un estado cerrado que impida modificaciones
    private void verificarEditable(OrdenTrabajo ot) {
        if (ot.getEstado() == EstadoOrdenTrabajoEnum.FINALIZADA
                || ot.getEstado() == EstadoOrdenTrabajoEnum.ENTREGADA
                || ot.getEstado() == EstadoOrdenTrabajoEnum.CANCELADA) {
            throw new IllegalStateException(
                "No se puede modificar una OT en estado " + ot.getEstado().name() + "."
            );
        }
    }

    // Validaciones de usuario, cliente y cabecera antes de registrar una nueva OT en el sistema
    @Override
    public Long crear(OrdenTrabajo ot) {
        if (ot == null) {
            throw new IllegalArgumentException("La orden de trabajo no puede ser null.");
        }
        if (ot.getIdUsuario() == null || ot.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El usuario que registra la OT es obligatorio.");
        }
        if (ot.getIdCliente() == null || ot.getIdCliente() <= 0) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }

        validarCabecera(ot);
        ot.setActivo(true);

        return ordenTrabajoDAO.crear(ot);
    }

    // Verificación del estado editable y validación de cabecera antes de aplicar cambios a la OT
    @Override
    public boolean actualizar(OrdenTrabajo ot) {
        if (ot == null || ot.getIdOrdenTrabajo() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar la OT.");
        }

        OrdenTrabajo existente = ordenTrabajoDAO.buscarPorId(ot.getIdOrdenTrabajo())
                .orElseThrow(() -> new IllegalArgumentException(
                    "No existe una OT con id: " + ot.getIdOrdenTrabajo()
                ));

        verificarEditable(existente);
        validarCabecera(ot);

        return ordenTrabajoDAO.actualizar(ot);
    }

    // Lógica para ocultar una OT del sistema sin eliminarla físicamente de la base de datos
    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la OT debe ser válido.");
        }
        if (ordenTrabajoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una OT con id: " + id);
        }
        return ordenTrabajoDAO.desactivar(id);
    }

    // Lógica para rehabilitar una OT que había sido desactivada previamente
    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la OT debe ser válido.");
        }
        if (ordenTrabajoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una OT con id: " + id);
        }
        return ordenTrabajoDAO.activar(id);
    }

    // Lógica para eliminar físicamente una OT, bloqueando la operación si ya tiene servicios cargados
    @Override
    public boolean eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la OT debe ser válido.");
        }
        if (ordenTrabajoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una OT con id: " + id);
        }
        if (!ordenTrabajoDetalleDAO.listarPorOrden(id).isEmpty()) {
            throw new IllegalStateException(
                "No se puede eliminar la OT porque ya tiene servicios cargados. " +
                "Si desea ocultarla, use la opción Desactivar."
            );
        }
        return ordenTrabajoDAO.eliminar(id);
    }

    // Lógica para recuperar una OT por su ID, usada principalmente para el formulario de edición
    @Override
    public Optional<OrdenTrabajo> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la OT no es válido.");
        }
        return ordenTrabajoDAO.buscarPorId(id);
    }

    // Lógica para recuperar una OT a partir de su número de orden generado por la base de datos
    @Override
    public Optional<OrdenTrabajo> buscarPorNumeroOrden(String numeroOrden) {
        if (numeroOrden == null || numeroOrden.isBlank()) {
            throw new IllegalArgumentException("El número de orden es obligatorio.");
        }
        return ordenTrabajoDAO.buscarPorNumeroOrden(numeroOrden.trim().toUpperCase());
    }

    // Lógica para obtener el listado de OTs activas para mostrar en la vista principal
    @Override
    public List<OrdenTrabajo> listarActivos() {
        return ordenTrabajoDAO.listarActivos();
    }

    // Lógica para obtener el listado completo de OTs sin filtros de estado de activación
    @Override
    public List<OrdenTrabajo> listarTodos() {
        return ordenTrabajoDAO.listarTodos();
    }

    // Lógica para filtrar OTs por cliente, usada en la pantalla de historial del cliente
    @Override
    public List<OrdenTrabajo> listarPorCliente(Long idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("El id del cliente no es válido.");
        }
        return ordenTrabajoDAO.listarPorCliente(idCliente);
    }

    // Lógica para filtrar OTs por estado dentro del flujo de trabajo del taller
    @Override
    public List<OrdenTrabajo> listarPorEstado(EstadoOrdenTrabajoEnum estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser null.");
        }
        return ordenTrabajoDAO.listarPorEstado(estado);
    }

    // Lógica para filtrar OTs por rango de fechas, validando que el rango sea coherente
    @Override
    public List<OrdenTrabajo> listarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Las fechas desde y hasta son obligatorias.");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta.");
        }
        return ordenTrabajoDAO.listarPorRangoFechas(desde, hasta);
    }

    // Lógica para obtener todas las OTs ingresadas en un día específico
    @Override
    public List<OrdenTrabajo> listarPorDia(LocalDate dia) {
        if (dia == null) {
            throw new IllegalArgumentException("El día es obligatorio.");
        }
        return ordenTrabajoDAO.listarPorDia(dia);
    }
}