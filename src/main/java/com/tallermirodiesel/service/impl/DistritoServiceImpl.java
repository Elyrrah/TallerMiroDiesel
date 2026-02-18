/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import com.tallermirodiesel.dao.impl.DistritoDAOImpl;
import com.tallermirodiesel.model.Distrito;
import com.tallermirodiesel.dao.DistritoDAO;
import com.tallermirodiesel.service.DistritoService;

/**
 * @author elyrr
 */
public class DistritoServiceImpl implements DistritoService {

    private final DistritoDAO distritoDAO;

    public DistritoServiceImpl() {
        this.distritoDAO = new DistritoDAOImpl();
    }

    private void validarCampos(Distrito distrito) {
        if (distrito.getIdDepartamento() == null || distrito.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El departamento (idDepartamento) debe ser válido.");
        }

        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase(Locale.ROOT);

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        distrito.setNombre(nombre);
    }

    @Override
    public Long crear(Distrito distrito) {
        if (distrito == null) {
            throw new IllegalArgumentException("El distrito no puede estar vacío.");
        }

        validarCampos(distrito);

        if (distritoDAO.buscarPorNombre(distrito.getNombre(), distrito.getIdDepartamento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un distrito con el nombre: " + distrito.getNombre() + " para el departamento seleccionado.");
        }

        return distritoDAO.crear(distrito);
    }

    @Override
    public boolean actualizar(Distrito distrito) {
        if (distrito == null || distrito.getIdDistrito() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        if (distrito.getIdDistrito() <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }

        validarCampos(distrito);

        if (distritoDAO.buscarPorId(distrito.getIdDistrito()).isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + distrito.getIdDistrito());
        }

        Optional<Distrito> porNombre = distritoDAO.buscarPorNombre(distrito.getNombre(), distrito.getIdDepartamento());
        if (porNombre.isPresent() && !porNombre.get().getIdDistrito().equals(distrito.getIdDistrito())) {
            throw new IllegalArgumentException("Ya existe otro distrito con el nombre: " + distrito.getNombre() + " para el departamento seleccionado.");
        }

        return distritoDAO.actualizar(distrito);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser válido.");
        }

        if (distritoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        return distritoDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser válido.");
        }

        if (distritoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        return distritoDAO.desactivar(id);
    }

    @Override
    public Optional<Distrito> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del distrito no es válido.");
        }

        return distritoDAO.buscarPorId(id);
    }

    /**
     * No usar este método para Distrito.
     * Usar buscarPorNombre(String nombre, Long idDepartamento) en su lugar,
     * ya que el nombre de un distrito solo es único dentro de un departamento.
     */
    @Override
    public Optional<Distrito> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para distritos usa buscarPorNombre(String nombre, Long idDepartamento)."
        );
    }

    @Override
    public Optional<Distrito> buscarPorNombre(String nombre, Long idDepartamento) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        if (idDepartamento == null || idDepartamento <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        return distritoDAO.buscarPorNombre(nombre.trim().toUpperCase(Locale.ROOT), idDepartamento);
    }

    @Override
    public List<Distrito> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return distritoDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Distrito> listarTodos() {
        return distritoDAO.listarTodos();
    }

    @Override
    public List<Distrito> listarActivos() {
        return distritoDAO.listarActivos();
    }

    @Override
    public List<Distrito> listarInactivos() {
        return distritoDAO.listarInactivos();
    }

    @Override
    public List<Distrito> listarPorDepartamento(Long idDepartamento) {
        if (idDepartamento == null || idDepartamento <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        return distritoDAO.listarPorDepartamento(idDepartamento);
    }
}