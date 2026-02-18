package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.PaisDAO;
import com.tallermirodiesel.dao.impl.PaisDAOImpl;
import com.tallermirodiesel.model.Pais;
import com.tallermirodiesel.service.PaisService;

/**
 * @author elyrr
 */
public class PaisServiceImpl implements PaisService {

    private final PaisDAO paisDAO;

    public PaisServiceImpl() {
        this.paisDAO = new PaisDAOImpl();
    }

    private void validarCampos(Pais pais) {
        String nombre = pais.getNombre() == null ? null : pais.getNombre().trim().toUpperCase();
        String iso2   = pais.getIso2()   == null ? null : pais.getIso2().trim().toUpperCase();
        String iso3   = pais.getIso3()   == null ? null : pais.getIso3().trim().toUpperCase();

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio.");
        }
        if (iso2 == null || iso2.isBlank()) {
            throw new IllegalArgumentException("El ISO2 del país es obligatorio.");
        }
        if (iso2.length() != 2) {
            throw new IllegalArgumentException("El ISO2 debe tener exactamente 2 caracteres.");
        }
        if (iso3 != null && !iso3.isBlank() && iso3.length() != 3) {
            throw new IllegalArgumentException("El ISO3 debe tener exactamente 3 caracteres.");
        }

        pais.setNombre(nombre);
        pais.setIso2(iso2);
        pais.setIso3(iso3);
    }

    @Override
    public Long crear(Pais pais) {
        if (pais == null) {
            throw new IllegalArgumentException("El país no puede ser null.");
        }

        validarCampos(pais);

        if (paisDAO.buscarPorIso2(pais.getIso2()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un país con el ISO2: " + pais.getIso2());
        }

        return paisDAO.crear(pais);
    }

    @Override
    public boolean actualizar(Pais pais) {
        if (pais == null || pais.getIdPais() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(pais);

        if (paisDAO.buscarPorId(pais.getIdPais()).isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + pais.getIdPais());
        }

        Optional<Pais> porIso2 = paisDAO.buscarPorIso2(pais.getIso2());
        if (porIso2.isPresent() && !porIso2.get().getIdPais().equals(pais.getIdPais())) {
            throw new IllegalArgumentException("Ya existe otro país con el ISO2: " + pais.getIso2());
        }

        return paisDAO.actualizar(pais);
    }

    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del país debe ser válido.");
        }
        if (paisDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + id);
        }
        return paisDAO.activar(id);
    }

    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del país debe ser válido.");
        }
        if (paisDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + id);
        }
        return paisDAO.desactivar(id);
    }

    @Override
    public Optional<Pais> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del país no existe.");
        }
        return paisDAO.buscarPorId(id);
    }

    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        if (iso2 == null || iso2.isBlank()) {
            throw new IllegalArgumentException("El código ISO2 es obligatorio.");
        }
        return paisDAO.buscarPorIso2(iso2.trim().toUpperCase());
    }

    @Override
    public Optional<Pais> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio.");
        }
        return paisDAO.buscarPorNombre(nombre.trim().toUpperCase());
    }

    @Override
    public List<Pais> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }
        return paisDAO.buscarPorNombreParcial(filtro.trim());
    }

    @Override
    public List<Pais> listarTodos() {
        return paisDAO.listarTodos();
    }

    @Override
    public List<Pais> listarActivos() {
        return paisDAO.listarActivos();
    }

    @Override
    public List<Pais> listarInactivos() {
        return paisDAO.listarInactivos();
    }
}