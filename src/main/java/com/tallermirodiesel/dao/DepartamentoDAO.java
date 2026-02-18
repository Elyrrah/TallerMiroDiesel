/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Departamento;

/**
 * @author elyrr
 */
public interface DepartamentoDAO extends CatalogoCrudDAO<Departamento, Long> {

    // Busca un Departamento por nombre dentro de un País concreto
    Optional<Departamento> buscarPorNombre(String nombre, Long idPais);

    // Lista todos los Departamentos de un País
    List<Departamento> listarPorPais(Long idPais);
}