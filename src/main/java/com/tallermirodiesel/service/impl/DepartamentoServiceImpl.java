/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.DepartamentoDAO;
import com.tallermirodiesel.dao.impl.DepartamentoDAOImpl;
import com.tallermirodiesel.model.Departamento;
import com.tallermirodiesel.service.DepartamentoService;

/**
 * @author elyrr
 */
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoDAO departamentoDAO;

    public DepartamentoServiceImpl() {
        this.departamentoDAO = new DepartamentoDAOImpl();
    }

    @Override
    public Long crear(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser null.");
        }

        if (departamento.getIdPais() == null || departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser válido.");
        }

        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }

        departamento.setNombre(nombre);

        List<Departamento> existentesEnPais = departamentoDAO.listarPorPais(departamento.getIdPais());
        boolean existeDuplicado = existentesEnPais.stream()
                .anyMatch(d -> d.getNombre() != null && d.getNombre().trim().equalsIgnoreCase(nombre));
        if (existeDuplicado) {
            throw new IllegalArgumentException("Ya existe un departamento con el nombre: " + nombre + " para el país seleccionado.");
        }

        return departamentoDAO.crear(departamento);
    }

    @Override
    public boolean actualizar(Departamento departamento) {
        if (departamento == null || departamento.getIdDepartamento() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        if (departamento.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }

        if (departamento.getIdPais() == null || departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser válido.");
        }

        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }

        departamento.setNombre(nombre);

        Optional<Departamento> existente = departamentoDAO.buscarPorId(departamento.getIdDepartamento());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + departamento.getIdDepartamento());
        }

        List<Departamento> existentesEnPais = departamentoDAO.listarPorPais(departamento.getIdPais());
        boolean existeDuplicado = existentesEnPais.stream()
                .anyMatch(d -> d.getNombre() != null
                        && d.getNombre().trim().equalsIgnoreCase(nombre)
                        && d.getIdDepartamento() != null
                        && !d.getIdDepartamento().equals(departamento.getIdDepartamento()));
        if (existeDuplicado) {
            throw new IllegalArgumentException("Ya existe otro departamento con el nombre: " + nombre + " para el país seleccionado.");
        }

        return departamentoDAO.actualizar(departamento);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        Optional<Departamento> departamento = departamentoDAO.buscarPorId(id);
        if (departamento.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

        return departamentoDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        Optional<Departamento> departamento = departamentoDAO.buscarPorId(id);
        if (departamento.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

        return departamentoDAO.desactivar(id);
    }

    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del departamento no es válido.");
        }

        return departamentoDAO.buscarPorId(id);
    }

    @Override
    public Optional<Departamento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase();
        return departamentoDAO.buscarPorNombre(nombreNorm);
    }

    @Override
    public List<Departamento> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        String filtroNorm = filtro.trim();
        return departamentoDAO.buscarPorNombreParcial(filtroNorm);
    }

    @Override
    public List<Departamento> listarTodos() {
        return departamentoDAO.listarTodos();
    }

    @Override
    public List<Departamento> listarActivos() {
        return departamentoDAO.listarActivos();
    }

    @Override
    public List<Departamento> listarInactivos() {
        return departamentoDAO.listarInactivos();
    }

    @Override
    public List<Departamento> listarPorPais(Long idPais) {
        if (idPais == null || idPais <= 0) {
            throw new IllegalArgumentException("El id del país debe ser válido.");
        }

        return departamentoDAO.listarPorPais(idPais);
    }
}