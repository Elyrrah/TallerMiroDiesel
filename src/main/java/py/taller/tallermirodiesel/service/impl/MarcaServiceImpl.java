/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.MarcaDAO;
import py.taller.tallermirodiesel.dao.impl.MarcaDAOImpl;
import py.taller.tallermirodiesel.model.Marca;
import py.taller.tallermirodiesel.service.MarcaService;

/**
 * @author elyrr
 */
public class MarcaServiceImpl implements MarcaService {

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final MarcaDAO marcaDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public MarcaServiceImpl() {
        this.marcaDAO = new MarcaDAOImpl();
    }

    //  VALIDACIONES PARA CREAR UNA MARCA.
    @Override
    public Long crear(Marca marca) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (marca == null) {
            throw new IllegalArgumentException("La marca no puede ser null.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = marca.getNombre() == null ? null : marca.getNombre().trim().toUpperCase();

        // 3. Aseguramos que el nombre esté cargadp.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        // 4. Cargamos el objeto con los datos.
        marca.setNombre(nombre);

        // 6. Le pedimos a la base de datos que guarde la marca.
        return marcaDAO.crear(marca);
    }

    //  VALIDACIONES PARA ACTUALIZAR UNA MARCA.
    @Override
    public boolean actualizar(Marca marca) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (marca == null || marca.getIdMarca() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = marca.getNombre() == null ? null : marca.getNombre().trim().toUpperCase();

        // 3. Asegurams que el nombre esté cargado.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        // 4. Actualizamos el objeto con los datos.
        marca.setNombre(nombre);

        // 5. Verificar que la marca a actualizar exista en el sistema.
        Optional<Marca> existente = marcaDAO.buscarPorId(marca.getIdMarca());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + marca.getIdMarca());
        }

        // 6. Le pedimos a la base de datos que actualice la marca.
        return marcaDAO.actualizar(marca);
    }

    //  VALIDACIONES PARA ACTIVAR UN PAÍS.
    @Override
    public boolean activar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la marca es obligatorio para activar.");
        }

        // 2. Verificamos que el marca exista.
        Marca marca = marcaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una marca con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (marca.isActivo()) {
            throw new IllegalStateException("La marca ya se encuentra activa.");
        }

        // 4. Activa la marca.
        return marcaDAO.activar(id);
    }

    //  VALIDACIONES PARA DESACTIVAR UNA MARCA.
    @Override
    public boolean desactivar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id de la marca es obligatorio para desactivar.");
        }

        // 2. Verificamos que el marca exista.
        Marca marca = marcaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una marca con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!marca.isActivo()) {
            throw new IllegalStateException("La marca ya se encuentra inactiva.");
        }

        // 4. Activa el marca.
        return marcaDAO.desactivar(id);
    }

    // BUSCA UNA MARCA POR SU ID.
    @Override
    public Optional<Marca> buscarPorId(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id de la marca no es válido.");
        }

        // 2. Devuelve el marca buscado.
        return marcaDAO.buscarPorId(id);
    }

    // BUSCA UNA MARCA POR SU NOMBRE.
    @Override
    public Optional<Marca> buscarPorNombre(String nombre) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la marca es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();

        // 3. Devuelve el marca buscado.
        return marcaDAO.buscarPorNombre(nombreNorm);
    }

    // BUSCA MARCAS POR NOMBRE PARCIAL.
    @Override
    public List<Marca> buscarPorNombreParcial(String filtro) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada.
        return marcaDAO.buscarPorNombreParcial(filtroNorm);
    }

    // VALIDACIONES PARA LISTAR TODAS LAS MARCAS.
    @Override
    public List<Marca> listarTodos() {
        return marcaDAO.listarTodos();
    }

    // VALIDACIONES PARA LISTAR TODAS LAS MAERCAS ACTIVAS.
    @Override
    public List<Marca> listarActivos() {
        return marcaDAO.listarActivos();
    }

    // VALIDACIONES PARA LISTAR TODOS LAS MARCAS INACTIVAS.
    @Override
    public List<Marca> listarInactivos() {
        return marcaDAO.listarInactivos();
    }
}
