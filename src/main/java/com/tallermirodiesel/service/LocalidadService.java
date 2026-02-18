/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Localidad;

/**
 * @author elyrr
 */
public interface LocalidadService extends CatalogoCrudService<Localidad, Long> {

    // Busca una Localidad por nombre dentro de un Distrito concreto
    Optional<Localidad> buscarPorNombre(String nombre, Long idDistrito);

    // Lista todas las Localidades de un Distrito
    List<Localidad> listarPorDistrito(Long idDistrito);
}