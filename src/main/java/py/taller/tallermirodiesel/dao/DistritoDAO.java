/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.util.List;
import py.taller.tallermirodiesel.model.Distrito;

/**
 * @author elyrr
 */
public interface DistritoDAO extends CatalogoCrudDAO <Distrito, Long>{
    
    //  Lista todos los Distritos de una Ciudad
    List<Distrito> listarPorCiudad(Long idCiudad);
}