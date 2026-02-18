/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Distrito;

/**
 * @author elyrr
 */
public interface DistritoDAO extends CatalogoCrudDAO<Distrito, Long> {

    // Busca un Distrito por nombre dentro de un Departamento concreto
    Optional<Distrito> buscarPorNombre(String nombre, Long idDepartamento);

    // Lista todos los Distritos de un Departamento
    List<Distrito> listarPorDepartamento(Long idDepartamento);
}