/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import py.taller.tallermirodiesel.model.Distrito;

/**
 * @author elyrr
 */
public interface DistritoService extends CatalogoCrudService<Distrito, Long> {

    //  Lista todos los Distritos por su Ciudad
    List<Distrito> listarPorCiudad(Long idCiudad);
}
