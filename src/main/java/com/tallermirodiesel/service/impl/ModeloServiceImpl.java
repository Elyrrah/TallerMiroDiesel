/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.dao.impl.ModeloDAOImpl;
import com.tallermirodiesel.model.Marca;
import com.tallermirodiesel.model.Modelo;
import com.tallermirodiesel.service.ModeloService;

/**
 * @author elyrr
 */
public class ModeloServiceImpl implements ModeloService {

    private final ModeloDAO modeloDAO;
    private final MarcaDAO marcaDAO;

    public ModeloServiceImpl() {
        this.modeloDAO = new ModeloDAOImpl();
        this.marcaDAO = new MarcaDAOImpl();
    }

    private void validarCampos(Modelo modelo) {
        if (modelo.getIdMarca() == null || modelo.getIdMarca() <= 0) {
            throw new IllegalArgumentException("El id de la marca debe ser válido.");
        }

        Optional<Marca> marca = marcaDAO.buscarPorId(modelo.getIdMarca());
        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + modelo.getIdMarca());
        }

        if (!marca.get().isActivo()) {
            throw new IllegalStateException("La marca con id " + modelo.getIdMarca() + " está inactiva.");
        }

        String nombre = (modelo.getNombre() == null) ? null : modelo.getNombre().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }

        modelo.setNombre(nombre);
    }

    @Override
    public Long crear(Modelo modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser null.");
        }

        validarCampos(modelo);

        return modeloDAO.crear(modelo);
    }

    @Override
    public boolean actualizar(Modelo modelo) {
        if (modelo == null || modelo.getIdModelo() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(modelo);

        if (modeloDAO.buscarPorId(modelo.getIdModelo()).isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + modelo.getIdModelo());
        }

        return modeloDAO.actualizar(modelo);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del modelo debe ser válido.");
        }

        if (modeloDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + id);
        }

        return modeloDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del modelo debe ser válido.");
        }

        if (modeloDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + id);
        }

        return modeloDAO.desactivar(id);
    }

    @Override
    public Optional<Modelo> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del modelo no es válido.");
        }

        return modeloDAO.buscarPorId(id);
    }

    @Override
    public Optional<Modelo> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase();
        return modeloDAO.buscarPorNombre(nombreNorm);
    }

    @Override
    public List<Modelo> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return modeloDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Modelo> listarTodos() {
        return modeloDAO.listarTodos();
    }

    @Override
    public List<Modelo> listarActivos() {
        return modeloDAO.listarActivos();
    }

    @Override
    public List<Modelo> listarInactivos() {
        return modeloDAO.listarInactivos();
    }

    @Override
    public List<Modelo> listarPorMarca(Long idMarca) {
        if (idMarca == null || idMarca <= 0) {
            throw new IllegalArgumentException("El id de la marca debe ser válido.");
        }

        return modeloDAO.listarPorMarca(idMarca);
    }
}