/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import com.tallermirodiesel.model.Localidad;

/**
 * @author elyrr
 */
public interface LocalidadService extends CatalogoCrudService<Localidad, Long> {

    //  Lista todas las Localidades por su Distrito
    List<Localidad> listarPorDistrito(Long idDistrito);
}
