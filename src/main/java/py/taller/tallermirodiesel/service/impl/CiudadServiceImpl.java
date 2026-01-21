/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.dao.CiudadDAO;
import py.taller.tallermirodiesel.dao.impl.CiudadDAOImpl;
import py.taller.tallermirodiesel.service.CiudadService;

/**
 * @author elyrr
 */
public class CiudadServiceImpl implements CiudadService {

    // DAO utilizado por el Service para acceder a la capa de persistencia.
    // El Service delega en el DAO todas las operaciones de acceso a datos.
    private final CiudadDAO ciudadDAO;

    // Constructor del Service.
    // Inicializa la implementación concreta del DAO que se usará para ejecutar las operaciones contra la base de datos.
    public CiudadServiceImpl() {
        this.ciudadDAO = new CiudadDAOImpl();
    }

    // Método utilitario para normalizar el nombre de la ciudad.
    // Se centraliza aquí para evitar duplicación de lógica.
    private String normalizarNombre(String nombre) {
        return (nombre == null) ? null : nombre.trim().toUpperCase();
    }

    //  Validaciones para Crear una Ciudad.
    @Override
    public Long crear(Ciudad ciudad) {
        if (ciudad == null) {
            throw new IllegalArgumentException("La ciudad no puede ser null.");
        }

        if (ciudad.getIdDepartamento() == null) {
            throw new IllegalArgumentException("El departamento (idDepartamento) es obligatorio.");
        }
        if (ciudad.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El departamento (idDepartamento) debe ser mayor a 0.");
        }

        String nombre = normalizarNombre(ciudad.getNombre());
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio.");
        }

        ciudad.setNombre(nombre);

        return ciudadDAO.crear(ciudad);
    }

    //  Validaciones para Actualizar un Ciudad.
    @Override
    public boolean actualizar(Ciudad ciudad) {
        if (ciudad == null) {
            throw new IllegalArgumentException("La ciudad no puede ser null.");
        }
        if (ciudad.getIdCiudad() == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio para actualizar.");
        }
        if (ciudad.getIdCiudad() <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }

        if (ciudad.getIdDepartamento() == null) {
            throw new IllegalArgumentException("El departamento (idDepartamento) es obligatorio.");
        }
        if (ciudad.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El departamento (idDepartamento) debe ser mayor a 0.");
        }

        String nombre = normalizarNombre(ciudad.getNombre());
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio.");
        }
        ciudad.setNombre(nombre);

        Optional<Ciudad> existente = ciudadDAO.buscarPorId(ciudad.getIdCiudad());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe una ciudad con id: " + ciudad.getIdCiudad());
        }

        return ciudadDAO.actualizar(ciudad);
    }

    //  Validaciones para Activar una Ciudad.
    @Override
    public boolean activar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio para activar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }

        Ciudad ciudad = ciudadDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una ciudad con id: " + id));

        if (ciudad.isActivo()) {
            throw new IllegalStateException("La ciudad ya se encuentra activa.");
        }

        return ciudadDAO.activar(id);
    }

    //  Validaciones para Desactivar un Ciudad.
    @Override
    public boolean desactivar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio para desactivar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }

        Ciudad ciudad = ciudadDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una ciudad con id: " + id));

        if (!ciudad.isActivo()) {
            throw new IllegalStateException("La ciudad ya se encuentra inactiva.");
        }

        return ciudadDAO.desactivar(id);
    }

    @Override
    public Optional<Ciudad> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }
        return ciudadDAO.buscarPorId(id);
    }

    @Override
    public List<Ciudad> listarTodos() {
        return ciudadDAO.listarTodos();
    }

    @Override
    public List<Ciudad> listarActivos() {
        return ciudadDAO.listarActivos();
    }

    @Override
    public List<Ciudad> listarInactivos() {
        return ciudadDAO.listarInactivos();
    }

    @Override
    public List<Ciudad> listarPorDepartamento(Long idDepartamento) {
        if (idDepartamento == null) {
            throw new IllegalArgumentException("El idDepartamento es obligatorio.");
        }
        if (idDepartamento <= 0) {
            throw new IllegalArgumentException("El idDepartamento debe ser mayor a 0.");
        }
        return ciudadDAO.listarPorDepartamento(idDepartamento);
    }
}
