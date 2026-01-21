/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.DistritoDAO;
import py.taller.tallermirodiesel.dao.impl.DistritoDAOImpl;
import py.taller.tallermirodiesel.model.Distrito;
import py.taller.tallermirodiesel.service.DistritoService;

/**
 * @author elyrr
 */
public class DistritoServiceImpl implements DistritoService {

     // El "Conector": Esta variable permite que el servicio hable con la base de datos.
    private final DistritoDAO distritoDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public DistritoServiceImpl() {
        this.distritoDAO = new DistritoDAOImpl();
    }

    //  Validaciones para Crear una Distrito.
    @Override
    public Long crear(Distrito distrito) {
        if (distrito == null) {
            throw new IllegalArgumentException("El distrito no puede ser null.");
        }

        if (distrito.getIdCiudad() == null) {
            throw new IllegalArgumentException("La ciudad (idCiudad) es obligatoria.");
        }
        if (distrito.getIdCiudad() <= 0) {
            throw new IllegalArgumentException("La ciudad (idCiudad) debe ser mayor a 0.");
        }

        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase();
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        // Nota: la normalización queda en el Service (trim + mayúsculas)
        distrito.setNombre(nombre);

        return distritoDAO.crear(distrito);
    }

    //  Validaciones para Actualizar un Distrito.
    @Override
    public boolean actualizar(Distrito distrito) {
        if (distrito == null) {
            throw new IllegalArgumentException("El distrito no puede ser null.");
        }
        if (distrito.getIdDistrito() == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio para actualizar.");
        }
        if (distrito.getIdDistrito() <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }

        if (distrito.getIdCiudad() == null) {
            throw new IllegalArgumentException("La ciudad (idCiudad) es obligatoria.");
        }
        if (distrito.getIdCiudad() <= 0) {
            throw new IllegalArgumentException("La ciudad (idCiudad) debe ser mayor a 0.");
        }

        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase();
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }
        distrito.setNombre(nombre);

        Optional<Distrito> existente = distritoDAO.buscarPorId(distrito.getIdDistrito());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + distrito.getIdDistrito());
        }

        return distritoDAO.actualizar(distrito);
    }

    //  Validaciones para Activar una Distrito.
    @Override
    public boolean activar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio para activar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }

        Distrito distrito = distritoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un distrito con id: " + id));

        if (distrito.isActivo()) {
            throw new IllegalStateException("El distrito ya se encuentra activo.");
        }

        return distritoDAO.activar(id);
    }

    //  Validaciones para Desactivar un Distrito.
    @Override
    public boolean desactivar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio para desactivar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }

        Distrito distrito = distritoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un distrito con id: " + id));

        if (!distrito.isActivo()) {
            throw new IllegalStateException("El distrito ya se encuentra inactivo.");
        }

        return distritoDAO.desactivar(id);
    }

    @Override
    public Optional<Distrito> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }
        return distritoDAO.buscarPorId(id);
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
    public List<Distrito> listarPorCiudad(Long idCiudad) {
        if (idCiudad == null) {
            throw new IllegalArgumentException("El idCiudad es obligatorio.");
        }
        if (idCiudad <= 0) {
            throw new IllegalArgumentException("El idCiudad debe ser mayor a 0.");
        }
        return distritoDAO.listarPorCiudad(idCiudad);
    }
}
//