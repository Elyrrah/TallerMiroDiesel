package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.DepartamentoDAO;
import com.tallermirodiesel.dao.impl.DepartamentoDAOImpl;
import com.tallermirodiesel.model.Departamento;
import com.tallermirodiesel.service.DepartamentoService;

/**
 * @author elyrr
 */
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoDAO departamentoDAO;

    public DepartamentoServiceImpl() {
        this.departamentoDAO = new DepartamentoDAOImpl();
    }

    private void validarCampos(Departamento departamento) {
        if (departamento.getIdPais() == null || departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser válido.");
        }

        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }

        departamento.setNombre(nombre);
    }

    @Override
    public Long crear(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser null.");
        }

        validarCampos(departamento);

        if (departamentoDAO.buscarPorNombre(departamento.getNombre(), departamento.getIdPais()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un departamento con el nombre: " + departamento.getNombre() + " para el país seleccionado.");
        }

        return departamentoDAO.crear(departamento);
    }

    @Override
    public boolean actualizar(Departamento departamento) {
        if (departamento == null || departamento.getIdDepartamento() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        if (departamento.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }

        validarCampos(departamento);

        if (departamentoDAO.buscarPorId(departamento.getIdDepartamento()).isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + departamento.getIdDepartamento());
        }

        Optional<Departamento> porNombre = departamentoDAO.buscarPorNombre(departamento.getNombre(), departamento.getIdPais());
        if (porNombre.isPresent() && !porNombre.get().getIdDepartamento().equals(departamento.getIdDepartamento())) {
            throw new IllegalArgumentException("Ya existe otro departamento con el nombre: " + departamento.getNombre() + " para el país seleccionado.");
        }

        return departamentoDAO.actualizar(departamento);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        if (departamentoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

        return departamentoDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser válido.");
        }

        if (departamentoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

        return departamentoDAO.desactivar(id);
    }

    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del departamento no es válido.");
        }

        return departamentoDAO.buscarPorId(id);
    }

    /**
     * No usar este método para Departamento.
     * Usar buscarPorNombre(String nombre, Long idPais) en su lugar,
     * ya que el nombre de un departamento solo es único dentro de un país.
     */
    @Override
    public Optional<Departamento> buscarPorNombre(String nombre) {
        throw new UnsupportedOperationException(
            "Para departamentos usa buscarPorNombre(String nombre, Long idPais)."
        );
    }

    @Override
    public Optional<Departamento> buscarPorNombre(String nombre, Long idPais) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }

        if (idPais == null || idPais <= 0) {
            throw new IllegalArgumentException("El id del país debe ser válido.");
        }

        return departamentoDAO.buscarPorNombre(nombre.trim().toUpperCase(), idPais);
    }

    @Override
    public List<Departamento> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }

        return departamentoDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Departamento> listarTodos() {
        return departamentoDAO.listarTodos();
    }

    @Override
    public List<Departamento> listarActivos() {
        return departamentoDAO.listarActivos();
    }

    @Override
    public List<Departamento> listarInactivos() {
        return departamentoDAO.listarInactivos();
    }

    @Override
    public List<Departamento> listarPorPais(Long idPais) {
        if (idPais == null || idPais <= 0) {
            throw new IllegalArgumentException("El id del país debe ser válido.");
        }

        return departamentoDAO.listarPorPais(idPais);
    }
}