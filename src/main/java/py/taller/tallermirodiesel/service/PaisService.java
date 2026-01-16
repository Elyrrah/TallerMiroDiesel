/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Pais;

/**
 *
 * @author elyrr
 */

public interface PaisService {

    List<Pais> listarActivos();

    List<Pais> listarInactivos();

    List<Pais> listarTodos();

    Optional<Pais> buscarPorId(Long id);

    Optional<Pais> buscarPorIso2(String iso2);

    boolean desactivar(Long id);

    boolean activar(Long id);
}
