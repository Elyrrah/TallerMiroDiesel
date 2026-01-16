/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.PaisDAO;
import py.taller.tallermirodiesel.dao.PaisDAOImpl;
import py.taller.tallermirodiesel.model.Pais;

/**
 *
 * @author elyrr
 */

public class PaisServiceImpl implements PaisService {

    private final PaisDAO paisDAO;

    public PaisServiceImpl() {
        this.paisDAO = new PaisDAOImpl();
    }

    // Si más adelante quieres inyectar un DAO mock o distinto:
    public PaisServiceImpl(PaisDAO paisDAO) {
        this.paisDAO = paisDAO;
    }

    @Override
    public List<Pais> listarActivos() {
        return paisDAO.listarActivos();
    }

    @Override
    public List<Pais> listarInactivos() {
        return paisDAO.listarInactivos();
    }

    @Override
    public List<Pais> listarTodos() {
        return paisDAO.listarTodos();
    }

    @Override
    public Optional<Pais> buscarPorId(Long id) {
        return paisDAO.buscarPorId(id);
    }

    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        return paisDAO.buscarPorIso2(iso2);
    }

    @Override
    public boolean desactivar(Long id) {
        return paisDAO.desactivar(id);
    }

    @Override
    public boolean activar(Long id) {
        return paisDAO.activar(id);
    }
}
