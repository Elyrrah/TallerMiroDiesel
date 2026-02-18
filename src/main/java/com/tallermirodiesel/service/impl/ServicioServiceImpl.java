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

    public ServicioServiceImpl() {
        this.servicioDAO = new ServicioDAOImpl();
    }

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

    @Override
    public Optional<Servicio> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del servicio no es válido.");
        }

        return servicioDAO.buscarPorId(id);
    }

    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio es obligatorio.");
        }

        return servicioDAO.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    @Override
    public Optional<Servicio> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }

        return servicioDAO.buscarPorNombre(nombre.trim().toUpperCase());
    }

    @Override
    public List<Servicio> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return servicioDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Servicio> listarTodos() {
        return servicioDAO.listarTodos();
    }

    @Override
    public List<Servicio> listarActivos() {
        return servicioDAO.listarActivos();
    }

    @Override
    public List<Servicio> listarInactivos() {
        return servicioDAO.listarInactivos();
    }
}