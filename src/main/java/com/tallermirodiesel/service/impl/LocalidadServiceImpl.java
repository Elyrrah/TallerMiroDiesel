/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import com.tallermirodiesel.dao.impl.LocalidadDAOImpl;
import com.tallermirodiesel.model.Localidad;
import com.tallermirodiesel.service.LocalidadService;
import com.tallermirodiesel.dao.LocalidadDAO;

/**
 * @author elyrr
 */
public class LocalidadServiceImpl implements LocalidadService {

    private final LocalidadDAO localidadDAO;

    public LocalidadServiceImpl() {
        this.localidadDAO = new LocalidadDAOImpl();
    }

    private void validarCampos(Localidad localidad) {
        if (localidad.getIdDistrito() == null || localidad.getIdDistrito() <= 0) {
            throw new IllegalArgumentException("El distrito (idDistrito) debe ser válido.");
        }

        String nombre = (localidad.getNombre() == null) ? null : localidad.getNombre().trim().toUpperCase(Locale.ROOT);

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio.");
        }

        localidad.setNombre(nombre);
    }

    @Override
    public Long crear(Localidad localidad) {
        if (localidad == null) {
            throw new IllegalArgumentException("La localidad no puede ser null.");
        }

        validarCampos(localidad);

        if (localidadDAO.buscarPorNombre(localidad.getNombre(), localidad.getIdDistrito()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una localidad con el nombre: " + localidad.getNombre() + " para el distrito seleccionado.");
        }

        return localidadDAO.crear(localidad);
    }

    @Override
    public boolean actualizar(Localidad localidad) {
        if (localidad == null || localidad.getIdLocalidad() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        if (localidad.getIdLocalidad() <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser mayor a 0.");
        }

        validarCampos(localidad);

        if (localidadDAO.buscarPorId(localidad.getIdLocalidad()).isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + localidad.getIdLocalidad());
        }

        Optional<Localidad> porNombre = localidadDAO.buscarPorNombre(localidad.getNombre(), localidad.getIdDistrito());
        if (porNombre.isPresent() && !porNombre.get().getIdLocalidad().equals(localidad.getIdLocalidad())) {
            throw new IllegalArgumentException("Ya existe otra localidad con el nombre: " + localidad.getNombre() + " para el distrito seleccionado.");
        }

        return localidadDAO.actualizar(localidad);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser válido.");
        }

        if (localidadDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + id);
        }

        return localidadDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser válido.");
        }

        if (localidadDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + id);
        }

        return localidadDAO.desactivar(id);
    }

    @Override
    public Optional<Localidad> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la localidad no es válido.");
        }

        return localidadDAO.buscarPorId(id);
    }

    /**
     * No usar este método para Localidad.
     * Usar buscarPorNombre(String nombre, Long idDistrito) en su lugar,
     * ya que el nombre de una localidad solo es único dentro de un distrito.
     */
    @Override
    public Optional<Localidad> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para localidades usa buscarPorNombre(String nombre, Long idDistrito)."
        );
    }

    @Override
    public Optional<Localidad> buscarPorNombre(String nombre, Long idDistrito) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio.");
        }

        if (idDistrito == null || idDistrito <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser válido.");
        }

        return localidadDAO.buscarPorNombre(nombre.trim().toUpperCase(Locale.ROOT), idDistrito);
    }

    @Override
    public List<Localidad> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return localidadDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Localidad> listarTodos() {
        return localidadDAO.listarTodos();
    }

    @Override
    public List<Localidad> listarActivos() {
        return localidadDAO.listarActivos();
    }

    @Override
    public List<Localidad> listarInactivos() {
        return localidadDAO.listarInactivos();
    }

    @Override
    public List<Localidad> listarPorDistrito(Long idDistrito) {
        if (idDistrito == null || idDistrito <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser válido.");
        }

        return localidadDAO.listarPorDistrito(idDistrito);
    }
}