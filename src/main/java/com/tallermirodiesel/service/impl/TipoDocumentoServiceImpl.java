/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.TipoDocumentoDAO;
import com.tallermirodiesel.dao.impl.TipoDocumentoDAOImpl;
import com.tallermirodiesel.model.TipoDocumento;
import com.tallermirodiesel.model.enums.TipoDocumentoAplicaEnum;
import com.tallermirodiesel.service.TipoDocumentoService;

/**
 * @author elyrr
 */
public class TipoDocumentoServiceImpl implements TipoDocumentoService {

    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final TipoDocumentoDAO tipoDocumentoDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public TipoDocumentoServiceImpl() {
        this.tipoDocumentoDAO = new TipoDocumentoDAOImpl();
    }

    // VALIDACIONES PARA CREAR UN TIPODOCUMENTO.
    @Override
    public Long crear(TipoDocumento tipoDocumento) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("El TipoDocumento no puede ser null.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = tipoDocumento.getNombre() == null ? null : tipoDocumento.getNombre().trim().toUpperCase();
        String codigo = tipoDocumento.getCodigo() == null ? null : tipoDocumento.getCodigo().trim().toUpperCase();
        TipoDocumentoAplicaEnum aplicaA = tipoDocumento.getAplicaA();

        // 3. Aseguramos que los datos tengan el formato correcto.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El Codigo del TipoDocumento es obligatorio.");
        }
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El Codigo no puede tener más de 20 caracteres.");
        }
        if (aplicaA == null) {
            throw new IllegalArgumentException("El AplicaA es obligatorio.");
        }

        // 4. Cargamos el objeto con los datos normalizados.
        tipoDocumento.setNombre(nombre);
        tipoDocumento.setCodigo(codigo);
        tipoDocumento.setAplicaA(aplicaA);

        // 5. No permitimos dos TipoDocumento con el mismo código.
        if (tipoDocumentoDAO.buscarPorCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un TipoDocumento con el Codigo: " + codigo);
        }

        // 6. Le pedimos a la base de datos que guarde el TipoDocumento.
        return tipoDocumentoDAO.crear(tipoDocumento);
    }

    // VALIDACIONES PARA ACTUALIZAR UN TIPODOCUMENTO.
    @Override
    public boolean actualizar(TipoDocumento tipoDocumento) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (tipoDocumento == null || tipoDocumento.getIdTipoDocumento() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar el TipoDocumento.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = tipoDocumento.getNombre() == null ? null : tipoDocumento.getNombre().trim().toUpperCase();
        String codigo = tipoDocumento.getCodigo() == null ? null : tipoDocumento.getCodigo().trim().toUpperCase();
        TipoDocumentoAplicaEnum aplicaA = tipoDocumento.getAplicaA();

        // 3. Aseguramos que los datos tengan el formato correcto.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El Codigo del TipoDocumento es obligatorio.");
        }
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El Codigo no puede tener más de 20 caracteres.");
        }
        if (aplicaA == null) {
            throw new IllegalArgumentException("El AplicaA es obligatorio.");
        }

        // 4. Actualizamos el objeto con los datos normalizados.
        tipoDocumento.setNombre(nombre);
        tipoDocumento.setCodigo(codigo);
        tipoDocumento.setAplicaA(aplicaA);

        // 5. Verificamos que el TipoDocumento a actualizar exista en el sistema.
        Optional<TipoDocumento> existente = tipoDocumentoDAO.buscarPorId(tipoDocumento.getIdTipoDocumento());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + tipoDocumento.getIdTipoDocumento());
        }

        // 6. No permitimos dos TipoDocumento con el mismo código.
        Optional<TipoDocumento> otroConMismoCodigo = tipoDocumentoDAO.buscarPorCodigo(codigo);
        if (otroConMismoCodigo.isPresent()
                && !otroConMismoCodigo.get().getIdTipoDocumento().equals(tipoDocumento.getIdTipoDocumento())) {
            throw new IllegalArgumentException("Ya existe otro TipoDocumento con el Codigo: " + codigo);
        }

        // 7. Le pedimos a la base de datos que actualice el TipoDocumento.
        return tipoDocumentoDAO.actualizar(tipoDocumento);
    }

    // VALIDACIONES PARA ACTIVAR UN TIPODOCUMENTO.
    @Override
    public boolean activar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del TipoDocumento es obligatorio para activar.");
        }

        // 2. Verificamos que el TipoDocumento exista.
        TipoDocumento tipoDocumento = tipoDocumentoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un TipoDocumento con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (tipoDocumento.isActivo()) {
            throw new IllegalStateException("El TipoDocumento ya se encuentra activo.");
        }

        // 4. Activamos el TipoDocumento.
        return tipoDocumentoDAO.activar(id);
    }

    // VALIDACIONES PARA DESACTIVAR UN TIPODOCUMENTO.
    @Override
    public boolean desactivar(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del TipoDocumento es obligatorio para desactivar.");
        }

        // 2. Verificamos que el TipoDocumento exista.
        TipoDocumento tipoDocumento = tipoDocumentoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un TipoDocumento con id: " + id));

        // 3. Si ya está inactivo, no hacemos nada.
        if (!tipoDocumento.isActivo()) {
            throw new IllegalStateException("El TipoDocumento ya se encuentra inactivo.");
        }

        // 4. Desactivamos el TipoDocumento.
        return tipoDocumentoDAO.desactivar(id);
    }

    // BUSCA UN TIPODOCUMENTO POR SU ID.
    @Override
    public Optional<TipoDocumento> buscarPorId(Long id) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento no existe.");
        }

        // 2. Devuelve el TipoDocumento buscado.
        return tipoDocumentoDAO.buscarPorId(id);
    }

    // BUSCA UN TIPODOCUMENTO POR SU NOMBRE.
    @Override
    public Optional<TipoDocumento> buscarPorNombre(String nombre) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();

        // 3. Devuelve el TipoDocumento buscado.
        return tipoDocumentoDAO.buscarPorNombre(nombreNorm);
    }

    // BUSCA UN TIPODOCUMENTO POR SU CÓDIGO.
    @Override
    public Optional<TipoDocumento> buscarPorCodigo(String codigo) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El Codigo del TipoDocumento es obligatorio.");
        }

        // 2. Devuelve el TipoDocumento buscado.
        return tipoDocumentoDAO.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    // BUSCA TIPODOCUMENTOS POR NOMBRE PARCIAL.
    @Override
    public List<TipoDocumento> buscarPorNombreParcial(String filtro) {

        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada.
        return tipoDocumentoDAO.buscarPorNombreParcial(filtroNorm);
    }

    // VALIDACIONES PARA LISTAR TODOS LOS TIPODOCUMENTOS.
    @Override
    public List<TipoDocumento> listarTodos() {
        return tipoDocumentoDAO.listarTodos();
    }

    // VALIDACIONES PARA LISTAR TODOS LOS TIPODOCUMENTOS ACTIVOS.
    @Override
    public List<TipoDocumento> listarActivos() {
        return tipoDocumentoDAO.listarActivos();
    }

    // VALIDACIONES PARA LISTAR TODOS LOS TIPODOCUMENTOS INACTIVOS.
    @Override
    public List<TipoDocumento> listarInactivos() {
        return tipoDocumentoDAO.listarInactivos();
    }

    // LISTA TIPOS DE DOCUMENTO FILTRADOS POR APLICACIÓN.
    @Override
    public List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {

        // 1. Verificamos que el parámetro no sea null.
        if (aplicaA == null) {
            throw new IllegalArgumentException("El parámetro aplicaA no puede ser null.");
        }

        // 2. Devuelve la lista filtrada.
        return tipoDocumentoDAO.listarPorAplicaA(aplicaA);
    }

    // LISTA TIPOS DE DOCUMENTO ACTIVOS FILTRADOS POR APLICACIÓN.
    @Override
    public List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {

        // 1. Verificamos que el parámetro no sea null.
        if (aplicaA == null) {
            throw new IllegalArgumentException("El parámetro aplicaA no puede ser null.");
        }

        // 2. Devuelve la lista filtrada.
        return tipoDocumentoDAO.listarActivosPorAplicaA(aplicaA);
    }
}