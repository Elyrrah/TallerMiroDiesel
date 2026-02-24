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

    // Inicialización de las implementaciones DAO para el servicio de modelos y marcas
    public ModeloServiceImpl() {
        this.modeloDAO = new ModeloDAOImpl();
        this.marcaDAO = new MarcaDAOImpl();
    }

    // Validaciones de formato, integridad de marca y obligatoriedad para los campos de un modelo
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

    // Validaciones para crear un modelo
    @Override
    public Long crear(Modelo modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser null.");
        }

        validarCampos(modelo);

        return modeloDAO.crear(modelo);
    }

    // Validaciones para actualizar la información de un modelo
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

    // Validaciones para activar un modelo en el sistema
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

    // Validaciones para desactivar un modelo en el sistema
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

    // Lógica para obtener la información de un modelo por su identificador único
    @Override
    public Optional<Modelo> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del modelo no es válido.");
        }

        return modeloDAO.buscarPorId(id);
    }

    // Lógica para buscar un modelo utilizando su nombre exacto
    @Override
    public Optional<Modelo> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase();
        return modeloDAO.buscarPorNombre(nombreNorm);
    }

    // Lógica para filtrar modelos según una coincidencia parcial en el nombre
    @Override
    public List<Modelo> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return modeloDAO.buscarPorNombreParcial(filtro.trim());
    }

    // Lógica para obtener la lista completa de modelos registrados
    @Override
    public List<Modelo> listarTodos() {
        return modeloDAO.listarTodos();
    }

    // Lógica para listar únicamente los modelos con estado activo
    @Override
    public List<Modelo> listarActivos() {
        return modeloDAO.listarActivos();
    }

    // Lógica para listar únicamente los modelos con estado inactivo
    @Override
    public List<Modelo> listarInactivos() {
        return modeloDAO.listarInactivos();
    }

    // Lógica para listar todos los modelos pertenecientes a una marca específica
    @Override
    public List<Modelo> listarPorMarca(Long idMarca) {
        if (idMarca == null || idMarca <= 0) {
            throw new IllegalArgumentException("El id de la marca debe ser válido.");
        }

        return modeloDAO.listarPorMarca(idMarca);
    }
}