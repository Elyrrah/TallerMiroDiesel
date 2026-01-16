/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

/**
 *
 * @author elyrr
 */
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> {

    List<T> listarTodos();

    Optional<T> buscarPorId(ID id);

    ID crear(T entidad);

    boolean actualizar(T entidad);

    boolean eliminar(ID id);
}