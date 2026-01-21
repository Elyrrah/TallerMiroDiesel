/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.util.List;
import py.taller.tallermirodiesel.model.Ciudad;
/**
 * @author elyrr
 */
public interface CiudadDAO extends CatalogoCrudDAO<Ciudad, Long>{
    //  Lista todas las Ciudades de un Departamento
    List<Ciudad> listarPorDepartamento(Long idDepartamento);
}