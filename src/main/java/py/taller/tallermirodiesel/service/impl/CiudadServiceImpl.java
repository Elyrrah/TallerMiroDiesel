/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.CiudadDAO;
import py.taller.tallermirodiesel.dao.impl.CiudadDAOImpl;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.service.CiudadService;

/**
 * @author elyrr
 */
public class CiudadServiceImpl implements CiudadService {

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final CiudadDAO ciudadDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public CiudadServiceImpl() {
        this.ciudadDAO = new CiudadDAOImpl();
    }

    //  VALIDACIONES PARA CREAR UNA CIUDAD.
    @Override
    public Long crear(Ciudad ciudad) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (ciudad == null) {
            throw new IllegalArgumentException("El id_ciudad no puede estar vacío.");
        }

        if (ciudad.getIdDepartamento() == null) {
            throw new IllegalArgumentException("El departamento (idDepartamento) es obligatorio.");
        }
        if (ciudad.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El departamento (idDepartamento) debe ser mayor a 0.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (ciudad.getNombre() == null) ? null : ciudad.getNombre().trim().toUpperCase();
        
        // 3. Aseguramos que se haya cargado el nombre de la ciudad.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio.");
        }

        // 4. Cargamos el objeto con los datos.
        ciudad.setNombre(nombre);

        // 5. Le pedimos a la base de datos que guarde la ciudad.
        return ciudadDAO.crear(ciudad);
    }

    //  VALIDACIONES PARA ACTUALIZAR UNA CIUDAD.
    @Override
    public boolean actualizar(Ciudad ciudad) {
        
        // 1. Verificamos que los campos estén completos correctamente.
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

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = ciudad.getNombre() == null ? null : ciudad.getNombre().trim().toUpperCase();
        
        // 3. Aseguramos que se haya cargado el nombre de la ciudad.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio.");
        }
        
        // 4. Actualizamos el objeto con los datos.
        ciudad.setNombre(nombre);

        // 5. Verificar que la ciudad a actualizar exista en el sistema.
        Optional<Ciudad> existente = ciudadDAO.buscarPorId(ciudad.getIdCiudad());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe una ciudad con id: " + ciudad.getIdCiudad());
        }

        // 7. Le pedimos a la base de datos que actualice la ciudad.
        return ciudadDAO.actualizar(ciudad);
    }

    //  VALIDACIONES PARA ACTIVAR UNA CIUDAD.
    @Override
    public boolean activar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio para activar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }

        // 2. Verificamos que la ciudad exista.
        Ciudad ciudad = ciudadDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe una ciudad con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (ciudad.isActivo()) {
            throw new IllegalStateException("La ciudad ya se encuentra activa.");
        }

        // 4. Activa la ciudad.
        return ciudadDAO.activar(id);
    }

    
    //  VALIDACIONES PARA DESACTIVAR UNA CIUDAD.
    @Override
    public boolean desactivar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio para desactivar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }

        // 2. Verificamos que la ciudad exista.
        Ciudad ciudad = ciudadDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe una ciudad con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!ciudad.isActivo()) {
            throw new IllegalStateException("La ciudad ya se encuentra inactiva.");
        }

        // 4. Activa la ciudad.
        return ciudadDAO.desactivar(id);
    }

    
    // BUSCA UNA CIUDAD POR SU ID.
    @Override
    public Optional<Ciudad> buscarPorId(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la ciudad es obligatorio.");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la ciudad debe ser mayor a 0.");
        }
        
        // 2. Devuelve la ciudad buscado.
        return ciudadDAO.buscarPorId(id);
    }

    
    // BUSCA UNA CIUDAD POR SU NOMBRE.
    @Override
    public Optional<Ciudad> buscarPorNombre(String nombre) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ciudad es obligatorio.");
        }
        
        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();
        
        // 3. Devuelve la ciudad buscada.
        return ciudadDAO.buscarPorNombre(nombreNorm);
    }

    
    // BUSCA CIUDADES POR NOMBRE PARCIAL.
    @Override
    public List<Ciudad> buscarPorNombreParcial(String filtro) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }
        
        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();
        
        // 3. Devuelve la lista filtrada.
        return ciudadDAO.buscarPorNombreParcial(filtroNorm);
    }

    
    // VALIDACIONES PARA LISTAR TODAS LAS CIUDADES.
    @Override
    public List<Ciudad> listarTodos() {
        return ciudadDAO.listarTodos();
    }

    
    // VALIDACIONES PARA LISTAR TODAS LAS CIUDADES ACTIVAS.
    @Override
    public List<Ciudad> listarActivos() {
        return ciudadDAO.listarActivos();
    }

    
    // VALIDACIONES PARA LISTAR TODAS LAS CIUDADES INACTIVAS.
    @Override
    public List<Ciudad> listarInactivos() {
        return ciudadDAO.listarInactivos();
    }

    
    // VALIDACIONES PARA LISTAR TODAS LAS CIUDADES DE UN DEPARTAMENTO.
    @Override
    public List<Ciudad> listarPorDepartamento(Long idDepartamento) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (idDepartamento == null) {
            throw new IllegalArgumentException("El idDepartamento es obligatorio.");
        }
        
        if (idDepartamento <= 0) {
            throw new IllegalArgumentException("El idDepartamento debe ser mayor a 0.");
        }
        
        // 2. Devuelve la lista de ciudades por departamento.
        return ciudadDAO.listarPorDepartamento(idDepartamento);
    }
}
