/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ServicioDAO;
import com.tallermirodiesel.dao.impl.ServicioDAOImpl;
import com.tallermirodiesel.model.Servicio;
import com.tallermirodiesel.service.ServicioService;

/**
 * @author elyrr
 */
public class ServicioServiceImpl implements ServicioService {

    private final ServicioDAO servicioDAO;

    // Inicialización de la implementación del DAO para el servicio de servicios técnicos
    public ServicioServiceImpl() {
        this.servicioDAO = new ServicioDAOImpl();
    }

    // Validaciones de formato, obligatoriedad y coherencia de precios para los campos de un servicio
    private void validarCampos(Servicio servicio) {
        String codigo      = servicio.getCodigo()      == null ? null : servicio.getCodigo().trim().toUpperCase();
        String nombre      = servicio.getNombre()      == null ? null : servicio.getNombre().trim().toUpperCase();
        String descripcion = servicio.getDescripcion() == null ? null : servicio.getDescripcion().trim();

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase() == null) {
            throw new IllegalArgumentException("El precio base del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase().signum() < 0) {
            throw new IllegalArgumentException("El precio base del servicio no puede ser negativo.");
        }

        servicio.setCodigo(codigo);
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);
    }

    // Validaciones para crear un nuevo servicio
    @Override
    public Long crear(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser null.");
        }

        validarCampos(servicio);

        if (servicioDAO.buscarPorCodigo(servicio.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un servicio con código: " + servicio.getCodigo());
        }

        return servicioDAO.crear(servicio);
    }

    // Validaciones para actualizar la información de un servicio existente
    @Override
    public boolean actualizar(Servicio servicio) {
        if (servicio == null || servicio.getIdServicio() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(servicio);

        if (servicioDAO.buscarPorId(servicio.getIdServicio()).isEmpty()) {
            throw new IllegalArgumentException("No existe un servicio con id: " + servicio.getIdServicio());
        }

        Optional<Servicio> porCodigo = servicioDAO.buscarPorCodigo(servicio.getCodigo());
        if (porCodigo.isPresent() && !porCodigo.get().getIdServicio().equals(servicio.getIdServicio())) {
            throw new IllegalArgumentException("Ya existe otro servicio con el código: " + servicio.getCodigo());
        }

        return servicioDAO.actualizar(servicio);
    }

    // Validaciones para activar un servicio en el catálogo
    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del servicio debe ser válido.");
        }

        if (servicioDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un servicio con id: " + id);
        }

        return servicioDAO.activar(id);
    }

    // Validaciones para desactivar un servicio del catálogo
    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del servicio debe ser válido.");
        }

        if (servicioDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un servicio con id: " + id);
        }

        return servicioDAO.desactivar(id);
    }

    // Lógica para obtener la información de un servicio por su identificador único
    @Override
    public Optional<Servicio> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del servicio no es válido.");
        }

        return servicioDAO.buscarPorId(id);
    }

    // Lógica para buscar un servicio utilizando su código identificador único
    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio es obligatorio.");
        }

        return servicioDAO.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    // Lógica para buscar un servicio utilizando su nombre exacto
    @Override
    public Optional<Servicio> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }

        return servicioDAO.buscarPorNombre(nombre.trim().toUpperCase());
    }

    // Lógica para filtrar servicios según una coincidencia parcial en el nombre
    @Override
    public List<Servicio> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return servicioDAO.buscarPorNombreParcial(filtro.trim());
    }

    // Lógica para obtener la lista completa de servicios registrados
    @Override
    public List<Servicio> listarTodos() {
        return servicioDAO.listarTodos();
    }

    // Lógica para listar únicamente los servicios con estado activo
    @Override
    public List<Servicio> listarActivos() {
        return servicioDAO.listarActivos();
    }

    // Lógica para listar únicamente los servicios con estado inactivo
    @Override
    public List<Servicio> listarInactivos() {
        return servicioDAO.listarInactivos();
    }
}