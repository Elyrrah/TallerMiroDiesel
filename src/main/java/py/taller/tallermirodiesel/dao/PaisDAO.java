/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Pais;

/**
 *
 * @author elyrr
 */

public interface PaisDAO extends CrudDAO<Pais, Long> {

    Optional<Pais> buscarPorIso2(String iso2);

    boolean desactivar(Long id);

    boolean activar(Long id);

    List<Pais> listarActivos();

    List<Pais> listarInactivos();
}