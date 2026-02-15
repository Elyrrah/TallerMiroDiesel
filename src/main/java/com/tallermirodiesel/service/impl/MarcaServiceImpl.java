/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.model.Marca;
import com.tallermirodiesel.service.MarcaService;

/**
 * @author elyrr
 */
public class MarcaServiceImpl implements MarcaService {

    private final MarcaDAO marcaDAO;

    public MarcaServiceImpl() {
        this.marcaDAO = new MarcaDAOImpl();
    }

    @Override
    public Long crear(Marca marca) {
        if (marca == null) {
            throw new IllegalArgumentException("La marca no puede ser null.");
        }

        String nombre = marca.getNombre() == null ? null : marca.getNombre().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        marca.setNombre(nombre);
        return marcaDAO.crear(marca);
    }

    @Override
    public boolean actualizar(Marca marca) {
        if (marca == null || marca.getIdMarca() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        String nombre = marca.getNombre() == null ? null : marca.getNombre().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        marca.setNombre(nombre);

        Optional<Marca> existente = marcaDAO.buscarPorId(marca.getIdMarca());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + marca.getIdMarca());
        }

        return marcaDAO.actualizar(marca);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la marca debe ser válido.");
        }

        Optional<Marca> marca = marcaDAO.buscarPorId(id);
        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + id);
        }

        return marcaDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la marca debe ser válido.");
        }

        Optional<Marca> marca = marcaDAO.buscarPorId(id);
        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + id);
        }

        return marcaDAO.desactivar(id);
    }

    @Override
    public Optional<Marca> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la marca no es válido.");
        }

        return marcaDAO.buscarPorId(id);
    }

    @Override
    public Optional<Marca> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase();
        return marcaDAO.buscarPorNombre(nombreNorm);
    }

    @Override
    public List<Marca> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        String filtroNorm = filtro.trim();
        return marcaDAO.buscarPorNombreParcial(filtroNorm);
    }

    @Override
    public List<Marca> listarTodos() {
        return marcaDAO.listarTodos();
    }

    @Override
    public List<Marca> listarActivos() {
        return marcaDAO.listarActivos();
    }

    @Override
    public List<Marca> listarInactivos() {
        return marcaDAO.listarInactivos();
    }
}