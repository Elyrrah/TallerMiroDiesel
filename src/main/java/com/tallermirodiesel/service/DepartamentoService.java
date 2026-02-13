/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.service;

import java.util.List;
import com.tallermirodiesel.model.Departamento;

/**
 * @author elyrr
 */
public interface DepartamentoService extends CatalogoCrudService<Departamento, Long>{
    
    //  Lista todos los Departamento por su Pais
    List<Departamento> listarPorPais(Long idPais);
}
