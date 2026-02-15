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

    private final TipoDocumentoDAO tipoDocumentoDAO;

    public TipoDocumentoServiceImpl() {
        this.tipoDocumentoDAO = new TipoDocumentoDAOImpl();
    }

    @Override
    public Long crear(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("El TipoDocumento no puede ser null.");
        }

        String nombre = tipoDocumento.getNombre() == null ? null : tipoDocumento.getNombre().trim().toUpperCase();
        String codigo = tipoDocumento.getCodigo() == null ? null : tipoDocumento.getCodigo().trim().toUpperCase();
        TipoDocumentoAplicaEnum aplicaA = tipoDocumento.getAplicaA();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del TipoDocumento es obligatorio.");
        }
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El código no puede tener más de 20 caracteres.");
        }
        if (aplicaA == null) {
            throw new IllegalArgumentException("El campo AplicaA es obligatorio.");
        }

        tipoDocumento.setNombre(nombre);
        tipoDocumento.setCodigo(codigo);
        tipoDocumento.setAplicaA(aplicaA);

        if (tipoDocumentoDAO.buscarPorCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un TipoDocumento con el código: " + codigo);
        }

        return tipoDocumentoDAO.crear(tipoDocumento);
    }

    @Override
    public boolean actualizar(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.getIdTipoDocumento() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        String nombre = tipoDocumento.getNombre() == null ? null : tipoDocumento.getNombre().trim().toUpperCase();
        String codigo = tipoDocumento.getCodigo() == null ? null : tipoDocumento.getCodigo().trim().toUpperCase();
        TipoDocumentoAplicaEnum aplicaA = tipoDocumento.getAplicaA();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del TipoDocumento es obligatorio.");
        }
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("El código no puede tener más de 20 caracteres.");
        }
        if (aplicaA == null) {
            throw new IllegalArgumentException("El campo AplicaA es obligatorio.");
        }

        tipoDocumento.setNombre(nombre);
        tipoDocumento.setCodigo(codigo);
        tipoDocumento.setAplicaA(aplicaA);

        Optional<TipoDocumento> existente = tipoDocumentoDAO.buscarPorId(tipoDocumento.getIdTipoDocumento());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + tipoDocumento.getIdTipoDocumento());
        }

        Optional<TipoDocumento> porCodigo = tipoDocumentoDAO.buscarPorCodigo(codigo);
        if (porCodigo.isPresent() && !porCodigo.get().getIdTipoDocumento().equals(tipoDocumento.getIdTipoDocumento())) {
            throw new IllegalArgumentException("Ya existe otro TipoDocumento con el código: " + codigo);
        }

        return tipoDocumentoDAO.actualizar(tipoDocumento);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento debe ser válido.");
        }

        Optional<TipoDocumento> tipoDocumento = tipoDocumentoDAO.buscarPorId(id);
        if (tipoDocumento.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + id);
        }

        return tipoDocumentoDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento debe ser válido.");
        }

        Optional<TipoDocumento> tipoDocumento = tipoDocumentoDAO.buscarPorId(id);
        if (tipoDocumento.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + id);
        }

        return tipoDocumentoDAO.desactivar(id);
    }

    @Override
    public Optional<TipoDocumento> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento no existe.");
        }

        return tipoDocumentoDAO.buscarPorId(id);
    }

    @Override
    public Optional<TipoDocumento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }

        String nombreNorm = nombre.trim().toUpperCase();
        return tipoDocumentoDAO.buscarPorNombre(nombreNorm);
    }

    @Override
    public Optional<TipoDocumento> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del TipoDocumento es obligatorio.");
        }

        return tipoDocumentoDAO.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    @Override
    public List<TipoDocumento> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        String filtroNorm = filtro.trim();
        return tipoDocumentoDAO.buscarPorNombreParcial(filtroNorm);
    }

    @Override
    public List<TipoDocumento> listarTodos() {
        return tipoDocumentoDAO.listarTodos();
    }

    @Override
    public List<TipoDocumento> listarActivos() {
        return tipoDocumentoDAO.listarActivos();
    }

    @Override
    public List<TipoDocumento> listarInactivos() {
        return tipoDocumentoDAO.listarInactivos();
    }

    @Override
    public List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        if (aplicaA == null) {
            throw new IllegalArgumentException("El parámetro aplicaA no puede ser null.");
        }

        return tipoDocumentoDAO.listarPorAplicaA(aplicaA);
    }

    @Override
    public List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        if (aplicaA == null) {
            throw new IllegalArgumentException("El parámetro aplicaA no puede ser null.");
        }

        return tipoDocumentoDAO.listarActivosPorAplicaA(aplicaA);
    }
}