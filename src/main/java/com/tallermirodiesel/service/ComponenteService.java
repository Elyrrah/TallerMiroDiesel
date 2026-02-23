/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Componente;

/**
 * @author elyrr
 */
public interface ComponenteService extends CatalogoCrudService<Componente, Long> {

    // Lista componentes por tipo de componente
    List<Componente> listarPorTipoComponente(Long idTipoComponente);

    // Lista componentes por marca
    List<Componente> listarPorMarca(Long idMarca);

    // Lista componentes por modelo
    List<Componente> listarPorModelo(Long idModelo);

    // Busca un componente por número de serie
    Optional<Componente> buscarPorNumeroSerie(String numeroSerie);
}