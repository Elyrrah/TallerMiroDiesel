/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.Optional;
import py.taller.tallermirodiesel.model.Servicio;

/**
 * @author elyrr
 */
public interface ServicioService extends CatalogoCrudService<Servicio, Long>{
    
    //  Busca un Servicio por su codigo
    Optional<Servicio> buscarPorCodigo(String codigo);
}