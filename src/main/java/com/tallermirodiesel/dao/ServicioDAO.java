/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.Optional;
import com.tallermirodiesel.model.Servicio;

/**
 * @author elyrr
 */
public interface ServicioDAO extends CatalogoCrudDAO<Servicio, Long>{
    
    //  Busca un Servicio por su codigo
    Optional<Servicio> buscarPorCodigo(String codigo);
}