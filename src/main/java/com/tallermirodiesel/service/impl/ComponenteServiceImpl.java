/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ComponenteDAO;
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.dao.TipoComponenteDAO;
import com.tallermirodiesel.dao.impl.ComponenteDAOImpl;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.dao.impl.ModeloDAOImpl;
import com.tallermirodiesel.dao.impl.TipoComponenteDAOImpl;
import com.tallermirodiesel.model.Componente;
import com.tallermirodiesel.service.ComponenteService;

/**
 * @author elyrr
 */
public class ComponenteServiceImpl implements ComponenteService {

    private final ComponenteDAO componenteDAO;
    private final TipoComponenteDAO tipoComponenteDAO;
    private final MarcaDAO marcaDAO;
    private final ModeloDAO modeloDAO;

    public ComponenteServiceImpl() {
        this.componenteDAO     = new ComponenteDAOImpl();
        this.tipoComponenteDAO = new TipoComponenteDAOImpl();
        this.marcaDAO          = new MarcaDAOImpl();
        this.modeloDAO         = new ModeloDAOImpl();
    }

    private void validarCampos(Componente c) {
        // Tipo de componente obligatorio
        if (c.getIdTipoComponente() == null || c.getIdTipoComponente() <= 0) {
            throw new IllegalArgumentException("El tipo de componente es obligatorio.");
        }
        if (tipoComponenteDAO.buscarPorId(c.getIdTipoComponente()).isEmpty()) {
            throw new IllegalArgumentException("No existe un tipo de componente con id: " + c.getIdTipoComponente());
        }

        // Marca obligatoria
        if (c.getIdMarca() == null || c.getIdMarca() <= 0) {
            throw new IllegalArgumentException("La marca es obligatoria.");
        }
        if (marcaDAO.buscarPorId(c.getIdMarca()).isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + c.getIdMarca());
        }

        // Modelo obligatorio y debe pertenecer a la marca
        if (c.getIdModelo() == null || c.getIdModelo() <= 0) {
            throw new IllegalArgumentException("El modelo es obligatorio.");
        }
        var modelo = modeloDAO.buscarPorId(c.getIdModelo());
        if (modelo.isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + c.getIdModelo());
        }
        if (!modelo.get().getIdMarca().equals(c.getIdMarca())) {
            throw new IllegalArgumentException("El modelo no pertenece a la marca seleccionada.");
        }

        // Número de serie opcional, pero si viene lo normalizamos
        if (c.getNumeroSerie() != null && !c.getNumeroSerie().isBlank()) {
            c.setNumeroSerie(c.getNumeroSerie().trim().toUpperCase());
        } else {
            c.setNumeroSerie(null);
        }

        // Observaciones opcionales
        if (c.getObservaciones() != null && c.getObservaciones().isBlank()) {
            c.setObservaciones(null);
        }
    }

    @Override
    public Long crear(Componente c) {
        if (c == null) {
            throw new IllegalArgumentException("El componente no puede ser null.");
        }

        validarCampos(c);

        // Si viene número de serie, verificar que no exista ya
        if (c.getNumeroSerie() != null && componenteDAO.buscarPorNumeroSerie(c.getNumeroSerie()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un componente con el número de serie: " + c.getNumeroSerie());
        }

        c.setActivo(true);
        return componenteDAO.crear(c);
    }

    @Override
    public boolean actualizar(Componente c) {
        if (c == null || c.getIdComponente() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(c);

        if (componenteDAO.buscarPorId(c.getIdComponente()).isEmpty()) {
            throw new IllegalArgumentException("No existe un componente con id: " + c.getIdComponente());
        }

        // Si viene número de serie, verificar que no lo tenga otro componente
        if (c.getNumeroSerie() != null) {
            Optional<Componente> existente = componenteDAO.buscarPorNumeroSerie(c.getNumeroSerie());
            if (existente.isPresent() && !existente.get().getIdComponente().equals(c.getIdComponente())) {
                throw new IllegalArgumentException("Ya existe otro componente con el número de serie: " + c.getNumeroSerie());
            }
        }

        return componenteDAO.actualizar(c);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del componente debe ser válido.");
        }
        if (componenteDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un componente con id: " + id);
        }
        return componenteDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del componente debe ser válido.");
        }
        if (componenteDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un componente con id: " + id);
        }
        return componenteDAO.desactivar(id);
    }

    @Override
    public Optional<Componente> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del componente no es válido.");
        }
        return componenteDAO.buscarPorId(id);
    }

    @Override
    public Optional<Componente> buscarPorNombre(String numeroSerie) {
        return buscarPorNumeroSerie(numeroSerie);
    }

    @Override
    public Optional<Componente> buscarPorNumeroSerie(String numeroSerie) {
        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new IllegalArgumentException("El número de serie no puede estar vacío.");
        }
        return componenteDAO.buscarPorNumeroSerie(numeroSerie.trim().toUpperCase());
    }

    @Override
    public List<Componente> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }
        return componenteDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Componente> listarTodos() {
        return componenteDAO.listarTodos();
    }

    @Override
    public List<Componente> listarActivos() {
        return componenteDAO.listarActivos();
    }

    @Override
    public List<Componente> listarInactivos() {
        return componenteDAO.listarInactivos();
    }

    @Override
    public List<Componente> listarPorTipoComponente(Long idTipoComponente) {
        if (idTipoComponente == null || idTipoComponente <= 0) {
            throw new IllegalArgumentException("El id de tipo componente no es válido.");
        }
        return componenteDAO.listarPorTipoComponente(idTipoComponente);
    }

    @Override
    public List<Componente> listarPorMarca(Long idMarca) {
        if (idMarca == null || idMarca <= 0) {
            throw new IllegalArgumentException("El id de marca no es válido.");
        }
        return componenteDAO.listarPorMarca(idMarca);
    }

    @Override
    public List<Componente> listarPorModelo(Long idModelo) {
        if (idModelo == null || idModelo <= 0) {
            throw new IllegalArgumentException("El id de modelo no es válido.");
        }
        return componenteDAO.listarPorModelo(idModelo);
    }
}