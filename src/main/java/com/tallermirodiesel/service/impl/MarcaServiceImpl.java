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

    // Inicialización de la implementación del DAO para el servicio de marcas
    public MarcaServiceImpl() {
        this.marcaDAO = new MarcaDAOImpl();
    }

    // Validaciones para crear una marca
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

    // Validaciones para actualizar la información de una marca
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

    // Validaciones para activar una marca en el sistema
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

    // Validaciones para desactivar una marca en el sistema
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

    // Lógica para obtener la información de una marca por su identificador único
    @Override
    public Optional<Marca> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la marca no es válido.");
        }

        return marcaDAO.buscarPorId(id);
    }

    // Lógica para buscar una marca utilizando su nombre exacto
    @Override
    public Optional<Marca> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase();
        return marcaDAO.buscarPorNombre(nombreNorm);
    }

    // Lógica para filtrar marcas según una coincidencia parcial en el nombre
    @Override
    public List<Marca> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        String filtroNorm = filtro.trim();
        return marcaDAO.buscarPorNombreParcial(filtroNorm);
    }

    // Lógica para obtener la lista completa de marcas registradas
    @Override
    public List<Marca> listarTodos() {
        return marcaDAO.listarTodos();
    }

    // Lógica para listar únicamente las marcas con estado activo
    @Override
    public List<Marca> listarActivos() {
        return marcaDAO.listarActivos();
    }

    // Lógica para listar únicamente las marcas con estado inactivo
    @Override
    public List<Marca> listarInactivos() {
        return marcaDAO.listarInactivos();
    }
}