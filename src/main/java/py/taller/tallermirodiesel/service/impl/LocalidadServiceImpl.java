/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.impl.LocalidadDAOImpl;
import py.taller.tallermirodiesel.model.Localidad;
import py.taller.tallermirodiesel.service.LocalidadService;
import py.taller.tallermirodiesel.dao.LocalidadDAO;

/**
 * @author elyrr
 */
public class LocalidadServiceImpl implements LocalidadService {

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final LocalidadDAO localidadDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public LocalidadServiceImpl() {
        this.localidadDAO = new LocalidadDAOImpl();
    }

    //  VALIDACIONES PARA CREAR UNA LOCALIDAD.
    @Override
    public Long crear(Localidad localidad) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (localidad == null) {
            throw new IllegalArgumentException("La localidad no puede ser null.");
        }

        if (localidad.getIdDistrito() == null) {
            throw new IllegalArgumentException("El distrito (idDistrito) es obligatorio.");
        }

        if (localidad.getIdDistrito() <= 0) {
            throw new IllegalArgumentException("El distrito (idDistrito) debe ser mayor a 0.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (localidad.getNombre() == null) ? null : localidad.getNombre().trim().toUpperCase(Locale.ROOT);

        // 3. Aseguramos que se haya cargado el nombre de la localidad.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio.");
        }

        // 4. Cargamos el objeto con los datos.
        localidad.setNombre(nombre);

        // 4.1 VALIDACIÓN: no permitir duplicados (mismo nombre dentro del mismo distrito).
        List<Localidad> existentesEnDistrito = localidadDAO.listarPorDistrito(localidad.getIdDistrito());
        boolean existeDuplicado = existentesEnDistrito.stream()
                .anyMatch(l -> l.getNombre() != null && l.getNombre().trim().equalsIgnoreCase(nombre));
        if (existeDuplicado) {
            throw new IllegalArgumentException("Ya existe una localidad con el nombre: " + nombre + " para el distrito seleccionado.");
        }

        // 5. Le pedimos a la base de datos que guarde la localidad.
        return localidadDAO.crear(localidad);
    }

    //  VALIDACIONES PARA ACTUALIZAR UNA LOCALIDAD.
    @Override
    public boolean actualizar(Localidad localidad) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (localidad == null) {
            throw new IllegalArgumentException("La localidad no puede ser null.");
        }

        if (localidad.getIdLocalidad() == null) {
            throw new IllegalArgumentException("El id de la localidad es obligatorio para actualizar.");
        }

        if (localidad.getIdLocalidad() <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser mayor a 0.");
        }

        if (localidad.getIdDistrito() == null) {
            throw new IllegalArgumentException("El distrito (idDistrito) es obligatorio.");
        }

        if (localidad.getIdDistrito() <= 0) {
            throw new IllegalArgumentException("El distrito (idDistrito) debe ser mayor a 0.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (localidad.getNombre() == null) ? null : localidad.getNombre().trim().toUpperCase(Locale.ROOT);

        // 3. Aseguramos que se haya cargado el nombre de la localidad.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio.");
        }

        // 4. Actualizamos el objeto con los datos.
        localidad.setNombre(nombre);

        // 5. Verificar que la localidad a actualizar exista en el sistema.
        Optional<Localidad> existente = localidadDAO.buscarPorId(localidad.getIdLocalidad());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + localidad.getIdLocalidad());
        }

        // 6. VALIDACIÓN: no permitir duplicados (mismo nombre dentro del mismo distrito), excluyendo el propio id.
        List<Localidad> existentesEnDistrito = localidadDAO.listarPorDistrito(localidad.getIdDistrito());
        boolean existeDuplicado = existentesEnDistrito.stream()
                .anyMatch(l -> l.getNombre() != null
                        && l.getNombre().trim().equalsIgnoreCase(nombre)
                        && l.getIdLocalidad() != null
                        && !l.getIdLocalidad().equals(localidad.getIdLocalidad()));
        if (existeDuplicado) {
            throw new IllegalArgumentException("Ya existe otra localidad con el nombre: " + nombre + " para el distrito seleccionado.");
        }

        // 7. Le pedimos a la base de datos que actualice la localidad.
        return localidadDAO.actualizar(localidad);
    }

    //  VALIDACIONES PARA ACTIVAR UNA LOCALIDAD.
    @Override
    public boolean activar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la localidad es obligatorio para activar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser mayor a 0.");
        }

        // 2. Verificamos que la localidad exista.
        Localidad localidad = localidadDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una localidad con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (localidad.isActivo()) {
            throw new IllegalStateException("La localidad ya se encuentra activa.");
        }

        // 4. Activa la localidad.
        return localidadDAO.activar(id);
    }

    //  VALIDACIONES PARA DESACTIVAR UNA LOCALIDAD.
    @Override
    public boolean desactivar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la localidad es obligatorio para desactivar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser mayor a 0.");
        }

        // 2. Verificamos que la localidad exista.
        Localidad localidad = localidadDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una localidad con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!localidad.isActivo()) {
            throw new IllegalStateException("La localidad ya se encuentra inactiva.");
        }

        // 4. Desactiva la localidad.
        return localidadDAO.desactivar(id);
    }

    // BUSCA UNA LOCALIDAD POR SU ID.
    @Override
    public Optional<Localidad> buscarPorId(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la localidad es obligatorio.");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("El id de la localidad debe ser mayor a 0.");
        }

        // 2. Devuelve la localidad buscada.
        return localidadDAO.buscarPorId(id);
    }

    // BUSCA UNA LOCALIDAD POR SU NOMBRE.
    @Override
    public Optional<Localidad> buscarPorNombre(String nombre) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la localidad es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase(Locale.ROOT);

        // 3. Devuelve la localidad buscada.
        return localidadDAO.buscarPorNombre(nombreNorm);
    }

    // BUSCA LOCALIDADES POR NOMBRE PARCIAL.
    @Override
    public List<Localidad> buscarPorNombreParcial(String filtro) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada.
        return localidadDAO.buscarPorNombreParcial(filtroNorm);
    }

    // VALIDACIONES PARA LISTAR TODAS LAS LOCALIDADES.
    @Override
    public List<Localidad> listarTodos() {
        return localidadDAO.listarTodos();
    }

    // VALIDACIONES PARA LISTAR TODAS LAS LOCALIDADES ACTIVAS.
    @Override
    public List<Localidad> listarActivos() {
        return localidadDAO.listarActivos();
    }

    // VALIDACIONES PARA LISTAR TODAS LAS LOCALIDADES INACTIVAS.
    @Override
    public List<Localidad> listarInactivos() {
        return localidadDAO.listarInactivos();
    }

    // VALIDACIONES PARA LISTAR TODAS LAS LOCALIDADES DE UN DISTRITO.
    @Override
    public List<Localidad> listarPorDistrito(Long idDistrito) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (idDistrito == null) {
            throw new IllegalArgumentException("El idDistrito es obligatorio.");
        }

        if (idDistrito <= 0) {
            throw new IllegalArgumentException("El idDistrito debe ser mayor a 0.");
        }

        // 2. Devuelve la lista de localidades por distrito.
        return localidadDAO.listarPorDistrito(idDistrito);
    }
}