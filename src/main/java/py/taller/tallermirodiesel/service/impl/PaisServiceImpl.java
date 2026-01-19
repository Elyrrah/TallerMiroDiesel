/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.PaisDAO;
import py.taller.tallermirodiesel.dao.impl.PaisDAOImpl;
import py.taller.tallermirodiesel.model.Pais;
import py.taller.tallermirodiesel.service.PaisService;

/**
 *
 * @author elyrr
 */
public class PaisServiceImpl implements PaisService {

    // DAO utilizado por el Service para acceder a la capa de persistencia.
    // El Service delega en el DAO todas las operaciones de acceso a datos.
    private final PaisDAO paisDAO;

    // Constructor del Service.
    // Inicializa la implementación concreta del DAO que se usará para ejecutar las operaciones contra la base de datos.
    public PaisServiceImpl() {
        this.paisDAO = new PaisDAOImpl();
    }


    //  Validaciones para Crear un Pais.
    @Override
    public Long crear(Pais pais) {
        if (pais == null) {
            throw new IllegalArgumentException("El país no puede ser null.");
        }

        String nombre = pais.getNombre() == null ? null : pais.getNombre().trim().toUpperCase();
        String iso2 = pais.getIso2() == null ? null : pais.getIso2().trim().toUpperCase();
        String iso3 = pais.getIso3() == null ? null : pais.getIso3().trim().toUpperCase();

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
            throw new IllegalArgumentException("El ISO3 debe tener exactamente 3 caracteres (si se provee).");
        }

        
        pais.setNombre(nombre);
        pais.setIso2(iso2);
        pais.setIso3(iso3);

        if (paisDAO.buscarPorIso2(iso2).isPresent()) {
            throw new IllegalArgumentException("Ya existe un país con el ISO2: " + iso2);
        }

        return paisDAO.crear(pais);
    }
    
    //  Validaciones para Actualizar un Pais.
    @Override
    public boolean actualizar(Pais pais) {
        if (pais == null) {
            throw new IllegalArgumentException("El país no puede ser null.");
        }
        if (pais.getIdPais() == null) {
            throw new IllegalArgumentException("El id del país es obligatorio para actualizar.");
        }

        String nombre = pais.getNombre() == null ? null : pais.getNombre().trim().toUpperCase();
        String iso2 = pais.getIso2() == null ? null : pais.getIso2().trim().toUpperCase();
        String iso3 = pais.getIso3() == null ? null : pais.getIso3().trim().toUpperCase();

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
            throw new IllegalArgumentException("El ISO3 debe tener exactamente 3 caracteres (si se provee).");
        }

        pais.setNombre(nombre);
        pais.setIso2(iso2);
        pais.setIso3(iso3);

        Optional<Pais> existente = paisDAO.buscarPorId(pais.getIdPais());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + pais.getIdPais());
        }

        Optional<Pais> otroConMismoIso = paisDAO.buscarPorIso2(iso2);
        if (otroConMismoIso.isPresent()
                && !otroConMismoIso.get().getIdPais().equals(pais.getIdPais())) {
            throw new IllegalArgumentException(
                    "Ya existe otro país con el ISO2: " + iso2);
        }

        return paisDAO.actualizar(pais);
    }

    //  Validaciones para Activar un Pais.
    @Override
    public boolean activar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del país es obligatorio para activar.");
        }

        Pais pais = paisDAO.buscarPorId(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("No existe un país con id: " + id));

        if (pais.isActivo()) {
            throw new IllegalStateException("El país ya se encuentra activo.");
        }

        return paisDAO.activar(id);
    }

    //  Validaciones para Desactivar un Pais.
    @Override
    public boolean desactivar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del país es obligatorio para desactivar.");
        }

        Pais pais = paisDAO.buscarPorId(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("No existe un país con id: " + id));

        if (!pais.isActivo()) {
            throw new IllegalStateException("El país ya se encuentra inactivo.");
        }

        return paisDAO.desactivar(id);
    }


    @Override
    public Optional<Pais> buscarPorId(Long id) {
        return paisDAO.buscarPorId(id);
    }

    //  Validaciones para Buscar por ISO2.
    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        String iso2Norm = (iso2 == null) ? null : iso2.trim().toUpperCase();
        return paisDAO.buscarPorIso2(iso2Norm);
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