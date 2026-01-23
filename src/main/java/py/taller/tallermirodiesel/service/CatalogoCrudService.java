/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
/**
 * @author elyrr
 * @param <T>   Tipo de Entidad del Catálogo
 * @param <ID>  Tipo del Identificador primario de la Entidad
 */
public interface CatalogoCrudService<T, ID> {
    //  Crea una nueva instancia de la clase
    ID crear(T entidad);
    
    //  Actualiza una instancia de la clase
    boolean actualizar(T entidad);
    
    //  Activa una instancia de la clase
    boolean activar(ID id);
    
    //  Desactiva una instancia de la clase
    boolean desactivar(ID id);

    //  Busca una instancia de la clase por su id
    Optional<T> buscarPorId(ID id);
    
    //  Busca una instancia de la clase por su nombre
    Optional<T> buscarPorNombre(String nombre);
    
    //  Busca instancias cuyo nombre coincida parcialmente
    List<T> buscarPorNombreParcial(String filtro);
    
    //  Lista todas las instancias de la clase
    List<T> listarTodos();
    
    //  Lista todas las instancias Activas de la clase
    List<T> listarActivos();
    
    //  Lista todas las instancias inactivas de la clase
    List<T> listarInactivos();
}
