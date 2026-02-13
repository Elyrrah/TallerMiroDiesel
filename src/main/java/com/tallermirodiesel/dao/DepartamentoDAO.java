/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import com.tallermirodiesel.model.Departamento;

/**
 * @author elyrr
 */
public interface DepartamentoDAO extends CatalogoCrudDAO<Departamento, Long> {
    
    //  Lista todos los Departamento de un Pais
    List<Departamento> listarPorPais(Long idPais);
}