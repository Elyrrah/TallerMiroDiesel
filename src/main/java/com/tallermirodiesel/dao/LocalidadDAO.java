/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import com.tallermirodiesel.model.Localidad;

/**
 * @author elyrr
 */
public interface LocalidadDAO extends CatalogoCrudDAO<Localidad, Long> {

    // Lista todas las Localidades de un Distrito
    List<Localidad> listarPorDistrito(Long idDistrito);
}