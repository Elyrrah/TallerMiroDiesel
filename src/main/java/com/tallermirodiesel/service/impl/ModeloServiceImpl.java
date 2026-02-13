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
public class ModeloServiceImpl implements ModeloService{

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final ModeloDAO modeloDAO;
    private final MarcaDAO marcaDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public ModeloServiceImpl() {
        this.modeloDAO = new ModeloDAOImpl();
        this.marcaDAO = new MarcaDAOImpl();
    }

    //  VALIDACIONES PARA CREAR UN MODELO.
    @Override
    public Long crear(Modelo modelo) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser null.");
        }

        if (modelo.getIdMarca() == null) {
            throw new IllegalArgumentException("El id de la marca (idMarca) es obligatorio.");
        }

        if (modelo.getIdMarca() <= 0) {
            throw new IllegalArgumentException("El id de la marca (idMarca) debe ser mayor a 0.");
        }

        // 2. Verificamos que la marca exista en el sistema.
        Optional<Marca> marca = marcaDAO.buscarPorId(modelo.getIdMarca());
        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + modelo.getIdMarca());
        }

        // 3. Verificamos que la marca esté activa.
        if (!marca.get().isActivo()) {
            throw new IllegalStateException("La marca con id " + modelo.getIdMarca() + " está inactiva.");
        }

        // 4. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (modelo.getNombre() == null) ? null : modelo.getNombre().trim().toUpperCase();

        // 5. Aseguramos que se haya cargado el nombre del modelo.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }

        // 6. Cargamos el objeto con los datos.
        modelo.setNombre(nombre);

        // 7. Le pedimos a la base de datos que guarde el modelo.
        return modeloDAO.crear(modelo);
    }

    //  VALIDACIONES PARA ACTUALIZAR UN MODELO.
    @Override
    public boolean actualizar(Modelo modelo) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (modelo == null) {
            throw new IllegalArgumentException("El modelo no puede ser null.");
        }

        if (modelo.getIdModelo() == null) {
            throw new IllegalArgumentException("El id del modelo (idModelo) es obligatorio para actualizar.");
        }

        if (modelo.getIdModelo() <= 0) {
            throw new IllegalArgumentException("El id del modelo (idModelo) debe ser mayor a 0.");
        }

        if (modelo.getIdMarca() == null) {
            throw new IllegalArgumentException("El id de la marca (idMarca) es obligatorio.");
        }

        if (modelo.getIdMarca() <= 0) {
            throw new IllegalArgumentException("El id de la marca (idMarca) debe ser mayor a 0.");
        }

        // 2. Verificamos que la marca exista en el sistema.
        Optional<Marca> marca = marcaDAO.buscarPorId(modelo.getIdMarca());
        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + modelo.getIdMarca());
        }

        // 3. Verificamos que la marca esté activa.
        if (!marca.get().isActivo()) {
            throw new IllegalStateException("La marca con id " + modelo.getIdMarca() + " está inactiva.");
        }

        // 4. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (modelo.getNombre() == null) ? null : modelo.getNombre().trim().toUpperCase();

        // 5. Aseguramos que se haya cargado el nombre del modelo.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }

        // 6. Actualizamos el objeto con los datos.
        modelo.setNombre(nombre);

        // 7. Verificar que el modelo a actualizar exista en el sistema.
        Optional<Modelo> existente = modeloDAO.buscarPorId(modelo.getIdModelo());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + modelo.getIdModelo());
        }

        // 8. Le pedimos a la base de datos que actualice el modelo.
        return modeloDAO.actualizar(modelo);
    }

    //  VALIDACIONES PARA ACTIVAR UN MODELO.
    @Override
    public boolean activar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del modelo es obligatorio para activar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del modelo debe ser mayor a 0.");
        }

        // 2. Verificamos que el modelo exista.
        Modelo modelo = modeloDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un modelo con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (modelo.isActivo()) {
            throw new IllegalStateException("El modelo ya se encuentra activo.");
        }

        // 4. Activa el modelo.
        return modeloDAO.activar(id);
    }


    //  VALIDACIONES PARA DESACTIVAR UN MODELO.
    @Override
    public boolean desactivar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del modelo es obligatorio para desactivar.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del modelo debe ser mayor a 0.");
        }

        // 2. Verificamos que el modelo exista.
        Modelo modelo = modeloDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un modelo con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!modelo.isActivo()) {
            throw new IllegalStateException("El modelo ya se encuentra inactivo.");
        }

        // 4. Desactiva el modelo.
        return modeloDAO.desactivar(id);
    }


    // BUSCA UN MODELO POR SU ID.
    @Override
    public Optional<Modelo> buscarPorId(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del modelo es obligatorio.");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("El id del modelo debe ser mayor a 0.");
        }

        // 2. Devuelve el modelo buscado.
        return modeloDAO.buscarPorId(id);
    }


    // BUSCA UN MODELO POR SU NOMBRE.
    @Override
    public Optional<Modelo> buscarPorNombre(String nombre) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del modelo es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();

        // 3. Devuelve el modelo buscado.
        return modeloDAO.buscarPorNombre(nombreNorm);
    }


    // BUSCA MODELOS POR NOMBRE PARCIAL.
    @Override
    public List<Modelo> buscarPorNombreParcial(String filtro) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada.
        return modeloDAO.buscarPorNombreParcial(filtroNorm);
    }


    // VALIDACIONES PARA LISTAR TODOS LOS MODELOS.
    @Override
    public List<Modelo> listarTodos() {
        return modeloDAO.listarTodos();
    }


    // VALIDACIONES PARA LISTAR TODOS LOS MODELOS ACTIVOS.
    @Override
    public List<Modelo> listarActivos() {
        return modeloDAO.listarActivos();
    }


    // VALIDACIONES PARA LISTAR TODOS LOS MODELOS INACTIVOS.
    @Override
    public List<Modelo> listarInactivos() {
        return modeloDAO.listarInactivos();
    }


    // VALIDACIONES PARA LISTAR TODOS LOS MODELOS DE UNA MARCA.
    @Override
    public List<Modelo> listarPorMarca(Long idMarca) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (idMarca == null) {
            throw new IllegalArgumentException("El id de la marca (idMarca) es obligatorio.");
        }

        if (idMarca <= 0) {
            throw new IllegalArgumentException("El id de la marca (idMarca) debe ser mayor a 0.");
        }

        // 2. Devuelve la lista de modelos por marca.
        return modeloDAO.listarPorMarca(idMarca);
    }
}