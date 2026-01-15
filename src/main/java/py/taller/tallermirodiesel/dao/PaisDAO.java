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

public interface PaisDAO {

    List<Pais> listarTodos();

    Optional<Pais> buscarPorId(Long id);

    Optional<Pais> buscarPorIso2(String iso2);

    Long crear(Pais pais);

    boolean actualizar(Pais pais);

    boolean desactivar(Long id);
}
