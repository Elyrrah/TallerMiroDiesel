/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author elyrr
 */

public interface CrudService<T, ID> {

    List<T> listarActivos();

    List<T> listarInactivos();

    List<T> listarTodos();

    Optional<T> buscarPorId(ID id);

    boolean activar(ID id);

    boolean desactivar(ID id);
}
