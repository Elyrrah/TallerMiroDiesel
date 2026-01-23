/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.util.Optional;
import py.taller.tallermirodiesel.model.Pais;

/**
 * @author elyrr
 */
public interface PaisDAO extends CatalogoCrudDAO<Pais, Long> {
    
    //  Busca un Pais por su ISO2
    Optional<Pais> buscarPorIso2(String iso2);
}