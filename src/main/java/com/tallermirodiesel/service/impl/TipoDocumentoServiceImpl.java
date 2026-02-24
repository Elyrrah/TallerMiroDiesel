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

    // Inicialización de la implementación del DAO para el servicio de tipos de documento
    public TipoDocumentoServiceImpl() {
        this.tipoDocumentoDAO = new TipoDocumentoDAOImpl();
    }

    // Validaciones de formato, longitud y obligatoriedad para los campos de un tipo de documento
    private void validarCampos(TipoDocumento tipoDocumento) {
        String nombre  = tipoDocumento.getNombre() == null ? null : tipoDocumento.getNombre().trim().toUpperCase();
        String codigo  = tipoDocumento.getCodigo() == null ? null : tipoDocumento.getCodigo().trim().toUpperCase();
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
    }

    // Validaciones para crear un tipo de documento
    @Override
    public Long crear(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null) {
            throw new IllegalArgumentException("El TipoDocumento no puede ser null.");
        }

        validarCampos(tipoDocumento);

        if (tipoDocumentoDAO.buscarPorCodigo(tipoDocumento.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un TipoDocumento con el código: " + tipoDocumento.getCodigo());
        }

        return tipoDocumentoDAO.crear(tipoDocumento);
    }

    // Validaciones para actualizar la información de un tipo de documento
    @Override
    public boolean actualizar(TipoDocumento tipoDocumento) {
        if (tipoDocumento == null || tipoDocumento.getIdTipoDocumento() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(tipoDocumento);

        if (tipoDocumentoDAO.buscarPorId(tipoDocumento.getIdTipoDocumento()).isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + tipoDocumento.getIdTipoDocumento());
        }

        Optional<TipoDocumento> porCodigo = tipoDocumentoDAO.buscarPorCodigo(tipoDocumento.getCodigo());
        if (porCodigo.isPresent() && !porCodigo.get().getIdTipoDocumento().equals(tipoDocumento.getIdTipoDocumento())) {
            throw new IllegalArgumentException("Ya existe otro TipoDocumento con el código: " + tipoDocumento.getCodigo());
        }

        return tipoDocumentoDAO.actualizar(tipoDocumento);
    }

    // Validaciones para activar un tipo de documento en el sistema
    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento debe ser válido.");
        }

        if (tipoDocumentoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + id);
        }

        return tipoDocumentoDAO.activar(id);
    }

    // Validaciones para desactivar un tipo de documento en el sistema
    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento debe ser válido.");
        }

        if (tipoDocumentoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + id);
        }

        return tipoDocumentoDAO.desactivar(id);
    }

    // Lógica para obtener la información de un tipo de documento por su identificador único
    @Override
    public Optional<TipoDocumento> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del TipoDocumento no existe.");
        }

        return tipoDocumentoDAO.buscarPorId(id);
    }

    // Lógica para buscar un tipo de documento utilizando su nombre exacto
    @Override
    public Optional<TipoDocumento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del TipoDocumento es obligatorio.");
        }

        return tipoDocumentoDAO.buscarPorNombre(nombre.trim().toUpperCase());
    }

    // Lógica para buscar un tipo de documento utilizando su código identificador
    @Override
    public Optional<TipoDocumento> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del TipoDocumento es obligatorio.");
        }

        return tipoDocumentoDAO.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    // Lógica para filtrar tipos de documento según una coincidencia parcial en el nombre
    @Override
    public List<TipoDocumento> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return tipoDocumentoDAO.buscarPorNombreParcial(filtro.trim());
    }

    // Lógica para obtener la lista completa de tipos de documento registrados
    @Override
    public List<TipoDocumento> listarTodos() {
        return tipoDocumentoDAO.listarTodos();
    }

    // Lógica para listar únicamente los tipos de documento con estado activo
    @Override
    public List<TipoDocumento> listarActivos() {
        return tipoDocumentoDAO.listarActivos();
    }

    // Lógica para listar únicamente los tipos de documento con estado inactivo
    @Override
    public List<TipoDocumento> listarInactivos() {
        return tipoDocumentoDAO.listarInactivos();
    }

    // Lógica para filtrar tipos de documento según la entidad a la que aplican (Persona, Empresa, etc.)
    @Override
    public List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        if (aplicaA == null) {
            throw new IllegalArgumentException("El parámetro aplicaA no puede ser null.");
        }

        return tipoDocumentoDAO.listarPorAplicaA(aplicaA);
    }

    // Lógica para listar tipos de documento activos filtrados por su ámbito de aplicación
    @Override
    public List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA) {
        if (aplicaA == null) {
            throw new IllegalArgumentException("El parámetro aplicaA no puede ser null.");
        }

        return tipoDocumentoDAO.listarActivosPorAplicaA(aplicaA);
    }
}