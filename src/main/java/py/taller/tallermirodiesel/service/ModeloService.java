/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import py.taller.tallermirodiesel.model.Modelo;

/**
  * @author elyrr
 */
public interface ModeloService extends CatalogoCrudService<Modelo, Long>{
    
    //  Lista todos los Modelos de una Marca
    List<Modelo> listarPorMarca(Long idMarca);
}