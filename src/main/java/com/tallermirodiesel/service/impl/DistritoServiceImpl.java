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

    // Inicialización de la implementación del DAO para el servicio de distritos
    public DistritoServiceImpl() {
        this.distritoDAO = new DistritoDAOImpl();
    }

    // Validaciones de formato y obligatoriedad para los campos de un distrito
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

    // Validaciones para crear un distrito
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

    // Validaciones para actualizar la información de un distrito
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

    // Validaciones para activar un distrito en el sistema
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

    // Validaciones para desactivar un distrito en el sistema
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

    // Lógica para obtener la información de un distrito por su identificador único
    @Override
    public Optional<Distrito> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del distrito no es válido.");
        }

        return distritoDAO.buscarPorId(id);
    }

    // Restricción de búsqueda de distrito únicamente por nombre global
    @Override
    public Optional<Distrito> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para distritos usa buscarPorNombre(String nombre, Long idDepartamento)."
        );
    }

    // Lógica para buscar un distrito utilizando su nombre dentro de un departamento específico
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

    // Lógica para filtrar distritos según una coincidencia parcial en el nombre
    @Override
    public List<Distrito> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return distritoDAO.buscarPorNombreParcial(filtro.trim());
    }

    // Lógica para obtener la lista completa de distritos registrados
    @Override
    public List<Distrito> listarTodos() {
        return distritoDAO.listarTodos();
    }

    // Lógica para listar únicamente los distritos con estado activo
    @Override
    public List<Distrito> listarActivos() {
        return distritoDAO.listarActivos();
    }

    // Lógica para listar únicamente los distritos con estado inactivo
    @Override
    public List<Distrito> listarInactivos() {
        return distritoDAO.listarInactivos();
    }

    // Lógica para listar todos los distritos pertenecientes a un departamento específico
    @Override
    public List<Distrito> listarPorDepartamento(Long idDepartamento) {
        if (idDepartamento == null || idDepartamento <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        return distritoDAO.listarPorDepartamento(idDepartamento);
    }
}