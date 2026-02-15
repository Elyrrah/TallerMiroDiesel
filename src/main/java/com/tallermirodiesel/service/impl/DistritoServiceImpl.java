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

    @Override
    public Long crear(Distrito distrito) {
        if (distrito == null) {
            throw new IllegalArgumentException("El distrito no puede estar vacío.");
        }

        if (distrito.getIdDepartamento() == null || distrito.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El departamento (idDepartamento) debe ser válido.");
        }

        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase(Locale.ROOT);

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        distrito.setNombre(nombre);

        List<Distrito> existentesEnDepartamento = distritoDAO.listarPorDepartamento(distrito.getIdDepartamento());
        boolean existeDuplicado = existentesEnDepartamento.stream()
                .anyMatch(d -> d.getNombre() != null && d.getNombre().trim().equalsIgnoreCase(nombre));
        if (existeDuplicado) {
            throw new IllegalArgumentException("Ya existe un distrito con el nombre: " + nombre + " para el departamento seleccionado.");
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

        if (distrito.getIdDepartamento() == null || distrito.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El departamento (idDepartamento) debe ser válido.");
        }

        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase(Locale.ROOT);

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        distrito.setNombre(nombre);

        Optional<Distrito> existente = distritoDAO.buscarPorId(distrito.getIdDistrito());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + distrito.getIdDistrito());
        }

        List<Distrito> existentesEnDepartamento = distritoDAO.listarPorDepartamento(distrito.getIdDepartamento());
        boolean existeDuplicado = existentesEnDepartamento.stream()
                .anyMatch(d -> d.getNombre() != null
                        && d.getNombre().trim().equalsIgnoreCase(nombre)
                        && d.getIdDistrito() != null
                        && !d.getIdDistrito().equals(distrito.getIdDistrito()));
        if (existeDuplicado) {
            throw new IllegalArgumentException("Ya existe otro distrito con el nombre: " + nombre + " para el departamento seleccionado.");
        }

        return distritoDAO.actualizar(distrito);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser válido.");
        }

        Optional<Distrito> distrito = distritoDAO.buscarPorId(id);
        if (distrito.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        return distritoDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser válido.");
        }

        Optional<Distrito> distrito = distritoDAO.buscarPorId(id);
        if (distrito.isEmpty()) {
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

    @Override
    public Optional<Distrito> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase(Locale.ROOT);
        return distritoDAO.buscarPorNombre(nombreNorm);
    }

    @Override
    public List<Distrito> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        String filtroNorm = filtro.trim();
        return distritoDAO.buscarPorNombreParcial(filtroNorm);
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