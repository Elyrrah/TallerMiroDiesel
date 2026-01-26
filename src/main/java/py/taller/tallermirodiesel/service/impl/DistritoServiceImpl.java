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

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final DistritoDAO distritoDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public DistritoServiceImpl() {
        this.distritoDAO = new DistritoDAOImpl();
    }

    //  VALIDACIONES PARA CREAR UN DISTRITO.
    @Override
    public Long crear(Distrito distrito) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (distrito == null) {
            throw new IllegalArgumentException("El id_distrito no puede ser null.");
        }

        if (distrito.getIdCiudad() == null) {
            throw new IllegalArgumentException("La ciudad (idCiudad) es obligatoria.");
        }
        
        if (distrito.getIdCiudad() <= 0) {
            throw new IllegalArgumentException("La ciudad (idCiudad) debe ser mayor a 0.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase();
        
        // 3. Aseguramos que se haya cargado el nombre del distrito.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        // 4. Cargamos el objeto con los datos.
        distrito.setNombre(nombre);

        // 5. Le pedimos a la base de datos que guarde el distrito.
        return distritoDAO.crear(distrito);
    }

    //  VALIDACIONES PARA ACTUALIZAR UN DISTRITO.
    @Override
    public boolean actualizar(Distrito distrito) {
        
        // 1. Verificamos que los campos estén completos correctamente.
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

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (distrito.getNombre() == null) ? null : distrito.getNombre().trim().toUpperCase();
        
        // 3. Aseguramos que se haya cargado el nombre del distrito.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }
        
        // 4. Actualizamos el objeto con los datos.
        distrito.setNombre(nombre);

        // 5. Verificar que el distrito a actualizar exista en el sistema.
        Optional<Distrito> existente = distritoDAO.buscarPorId(distrito.getIdDistrito());
        if (existente.isEmpty()) {throw new IllegalArgumentException("No existe un distrito con id: " + distrito.getIdDistrito());
        }

        // 7. Le pedimos a la base de datos que actualice el distrito.
        return distritoDAO.actualizar(distrito);
    }

    //  VALIDACIONES PARA ACTIVAR UN DISTRITO.
    @Override
    public boolean activar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio para activar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }

        // 2. Verificamos que el distrito exista.
        Distrito distrito = distritoDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe un distrito con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (distrito.isActivo()) {
            throw new IllegalStateException("El distrito ya se encuentra activo.");
        }

        // 4. Activa el distrito.
        return distritoDAO.activar(id);
    }

    
    //  VALIDACIONES PARA DESACTIVAR UN DISTRITO.
    @Override
    public boolean desactivar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio para desactivar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }

        // 2. Verificamos que el distrito exista.
        Distrito distrito = distritoDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe un distrito con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!distrito.isActivo()) {
            throw new IllegalStateException("El distrito ya se encuentra inactivo.");
        }

        // 4. Activa el distrito.
        return distritoDAO.desactivar(id);
    }

    
    // BUSCA UN DISTRITO POR SU ID.
    @Override
    public Optional<Distrito> buscarPorId(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del distrito es obligatorio.");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("El id del distrito debe ser mayor a 0.");
        }
        
        // 2. Devuelve el distrito buscado.
        return distritoDAO.buscarPorId(id);
    }

    
    // BUSCA UN DISTRITO POR SU NOMBRE.
    @Override
    public Optional<Distrito> buscarPorNombre(String nombre) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del distrito es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();

        // 3. Devuelve el distrito buscado.
        return distritoDAO.buscarPorNombre(nombreNorm);
    }

    
    // BUSCA DISTRITOS POR NOMBRE PARCIAL.
    @Override
    public List<Distrito> buscarPorNombreParcial(String filtro) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada.
        return distritoDAO.buscarPorNombreParcial(filtroNorm);
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DISTRITOS.
    @Override
    public List<Distrito> listarTodos() {
        return distritoDAO.listarTodos();
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DISTRITOS ACTIVOS.
    @Override
    public List<Distrito> listarActivos() {
        return distritoDAO.listarActivos();
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DISTRITOS INACTIVOS.
    @Override
    public List<Distrito> listarInactivos() {
        return distritoDAO.listarInactivos();
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DISTRITOS DE UNA CIUDAD.
    @Override
    public List<Distrito> listarPorCiudad(Long idCiudad) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (idCiudad == null) {
            throw new IllegalArgumentException("El idCiudad es obligatorio.");
        }
        
        if (idCiudad <= 0) {
            throw new IllegalArgumentException("El idCiudad debe ser mayor a 0.");
        }
        
        // 2. Devuelve la lista de distritos por ciudad.
        return distritoDAO.listarPorCiudad(idCiudad);
    }
}
