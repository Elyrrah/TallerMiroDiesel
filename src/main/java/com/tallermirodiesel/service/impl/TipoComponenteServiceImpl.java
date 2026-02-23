/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.TipoComponenteDAO;
import com.tallermirodiesel.dao.impl.TipoComponenteDAOImpl;
import com.tallermirodiesel.model.TipoComponente;
import com.tallermirodiesel.service.TipoComponenteService;

/**
 * @author elyrr
 */
public class TipoComponenteServiceImpl implements TipoComponenteService {

    private final TipoComponenteDAO tipoComponenteDAO;

    public TipoComponenteServiceImpl() {
        this.tipoComponenteDAO = new TipoComponenteDAOImpl();
    }

    private void validarCampos(TipoComponente tc) {
        String nombre = tc.getNombre() == null ? null : tc.getNombre().trim().toUpperCase();
        String descripcion = tc.getDescripcion() == null ? null : tc.getDescripcion().trim();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoComponente es obligatorio.");
        }

        tc.setNombre(nombre);
        tc.setDescripcion(descripcion);
    }

    @Override
    public Long crear(TipoComponente tc) {
        if (tc == null) {
            throw new IllegalArgumentException("El TipoComponente no puede ser null.");
        }

        validarCampos(tc);

        if (tipoComponenteDAO.buscarPorNombre(tc.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un TipoComponente con el nombre: " + tc.getNombre());
        }

        tc.setActivo(true);
        return tipoComponenteDAO.crear(tc);
    }

    @Override
    public boolean actualizar(TipoComponente tc) {
        if (tc == null || tc.getIdTipoComponente() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(tc);

        if (tipoComponenteDAO.buscarPorId(tc.getIdTipoComponente()).isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoComponente con id: " + tc.getIdTipoComponente());
        }

        Optional<TipoComponente> existente = tipoComponenteDAO.buscarPorNombre(tc.getNombre());
        if (existente.isPresent() && !existente.get().getIdTipoComponente().equals(tc.getIdTipoComponente())) {
            throw new IllegalArgumentException("Ya existe otro TipoComponente con el nombre: " + tc.getNombre());
        }

        return tipoComponenteDAO.actualizar(tc);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoComponente debe ser válido.");
        }

        if (tipoComponenteDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoComponente con id: " + id);
        }

        return tipoComponenteDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoComponente debe ser válido.");
        }

        if (tipoComponenteDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoComponente con id: " + id);
        }

        return tipoComponenteDAO.desactivar(id);
    }

    @Override
    public Optional<TipoComponente> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoComponente no es válido.");
        }

        return tipoComponenteDAO.buscarPorId(id);
    }

    @Override
    public Optional<TipoComponente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoComponente es obligatorio.");
        }

        return tipoComponenteDAO.buscarPorNombre(nombre.trim().toUpperCase());
    }

    @Override
    public List<TipoComponente> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return tipoComponenteDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<TipoComponente> listarTodos() {
        return tipoComponenteDAO.listarTodos();
    }

    @Override
    public List<TipoComponente> listarActivos() {
        return tipoComponenteDAO.listarActivos();
    }

    @Override
    public List<TipoComponente> listarInactivos() {
        return tipoComponenteDAO.listarInactivos();
    }
}