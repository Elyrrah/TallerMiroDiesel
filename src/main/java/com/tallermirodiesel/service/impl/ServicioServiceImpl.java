/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ServicioDAO;
import com.tallermirodiesel.dao.impl.ServicioDAOImpl;
import com.tallermirodiesel.model.Servicio;
import com.tallermirodiesel.service.ServicioService;

/**
 * @author elyrr
 */
public class ServicioServiceImpl implements ServicioService {

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final ServicioDAO servicioDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public ServicioServiceImpl() {
        this.servicioDAO = new ServicioDAOImpl();
    }

    //  VALIDACIONES PARA CREAR UN SERVICIO.
    @Override
    public Long crear(Servicio servicio) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser null.");
        }

        // 2. Quitamos espacios vacíos y pasamos a MAYÚSCULAS donde corresponde.
        String codigo = servicio.getCodigo() == null ? null : servicio.getCodigo().trim().toUpperCase();
        String nombre = servicio.getNombre() == null ? null : servicio.getNombre().trim().toUpperCase();
        String descripcion = servicio.getDescripcion() == null ? null : servicio.getDescripcion().trim();

        // 3. Aseguramos que los campos obligatorios estén cargados.
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase() == null) {
            throw new IllegalArgumentException("El precio base del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase().signum() < 0) {
            throw new IllegalArgumentException("El precio base del servicio no puede ser negativo.");
        }

        // 4. Cargamos el objeto con los datos normalizados.
        servicio.setCodigo(codigo);
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);

        // 5. (Opcional) Verificar duplicidad por código.
        Optional<Servicio> existente = servicioDAO.buscarPorCodigo(codigo);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya existe un servicio con código: " + codigo);
        }

        // 6. Le pedimos a la base de datos que guarde el servicio.
        return servicioDAO.crear(servicio);
    }

    //  VALIDACIONES PARA ACTUALIZAR UN SERVICIO.
    @Override
    public boolean actualizar(Servicio servicio) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (servicio == null || servicio.getIdServicio() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        // 2. Quitamos espacios vacíos y pasamos a MAYÚSCULAS donde corresponde.
        String codigo = servicio.getCodigo() == null ? null : servicio.getCodigo().trim().toUpperCase();
        String nombre = servicio.getNombre() == null ? null : servicio.getNombre().trim().toUpperCase();
        String descripcion = servicio.getDescripcion() == null ? null : servicio.getDescripcion().trim();

        // 3. Aseguramos que los campos obligatorios estén cargados.
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase() == null) {
            throw new IllegalArgumentException("El precio base del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase().signum() < 0) {
            throw new IllegalArgumentException("El precio base del servicio no puede ser negativo.");
        }

        // 4. Actualizamos el objeto con los datos normalizados.
        servicio.setCodigo(codigo);
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);

        // 5. Verificar que el servicio a actualizar exista en el sistema.
        Optional<Servicio> existente = servicioDAO.buscarPorId(servicio.getIdServicio());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un servicio con id: " + servicio.getIdServicio());
        }

        // 6. Verificar duplicidad de código (si el código se está usando en otro registro).
        Optional<Servicio> porCodigo = servicioDAO.buscarPorCodigo(codigo);
        if (porCodigo.isPresent() && !porCodigo.get().getIdServicio().equals(servicio.getIdServicio())) {
            throw new IllegalArgumentException("Ya existe otro servicio con código: " + codigo);
        }

        // 7. Le pedimos a la base de datos que actualice el servicio.
        return servicioDAO.actualizar(servicio);
    }

    //  VALIDACIONES PARA ACTIVAR UN SERVICIO.
    @Override
    public boolean activar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del servicio es obligatorio para activar.");
        }

        // 2. Verificamos que el servicio exista.
        Servicio servicio = servicioDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un servicio con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (servicio.isActivo()) {
            throw new IllegalStateException("El servicio ya se encuentra activo.");
        }

        // 4. Activa el servicio.
        return servicioDAO.activar(id);
    }

    //  VALIDACIONES PARA DESACTIVAR UN SERVICIO.
    @Override
    public boolean desactivar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del servicio es obligatorio para desactivar.");
        }

        // 2. Verificamos que el servicio exista.
        Servicio servicio = servicioDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un servicio con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!servicio.isActivo()) {
            throw new IllegalStateException("El servicio ya se encuentra inactivo.");
        }

        // 4. Desactiva el servicio.
        return servicioDAO.desactivar(id);
    }

    // BUSCA UN SERVICIO POR SU ID.
    @Override
    public Optional<Servicio> buscarPorId(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del servicio no es válido.");
        }

        // 2. Devuelve el servicio buscado.
        return servicioDAO.buscarPorId(id);
    }

    // BUSCA UN SERVICIO POR SU NOMBRE.
    @Override
    public Optional<Servicio> buscarPorNombre(String nombre) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();

        // 3. Devuelve el servicio buscado.
        return servicioDAO.buscarPorNombre(nombreNorm);
    }

    // BUSCA UN SERVICIO POR SU CÓDIGO.
    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String codigoNorm = codigo.trim().toUpperCase();

        // 3. Devuelve el servicio buscado.
        return servicioDAO.buscarPorCodigo(codigoNorm);
    }

    // BUSCA SERVICIOS POR NOMBRE PARCIAL.
    @Override
    public List<Servicio> buscarPorNombreParcial(String filtro) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada.
        return servicioDAO.buscarPorNombreParcial(filtroNorm);
    }

    // VALIDACIONES PARA LISTAR TODOS LOS SERVICIOS.
    @Override
    public List<Servicio> listarTodos() {
        return servicioDAO.listarTodos();
    }

    // VALIDACIONES PARA LISTAR TODOS LOS SERVICIOS ACTIVOS.
    @Override
    public List<Servicio> listarActivos() {
        return servicioDAO.listarActivos();
    }

    // VALIDACIONES PARA LISTAR TODOS LOS SERVICIOS INACTIVOS.
    @Override
    public List<Servicio> listarInactivos() {
        return servicioDAO.listarInactivos();
    }
}
