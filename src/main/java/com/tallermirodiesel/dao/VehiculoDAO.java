/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Vehiculo;
import com.tallermirodiesel.model.enums.TipoVehiculoEnum;

/**
 * @author elyrr
 */
public interface VehiculoDAO extends CatalogoCrudDAO<Vehiculo, Long> {

    // Busca un vehículo por su placa
    Optional<Vehiculo> buscarPorPlaca(String placa);

    // Lista vehículos por marca
    List<Vehiculo> listarPorMarca(Long idMarca);

    // Lista vehículos por tipo
    List<Vehiculo> listarPorTipo(TipoVehiculoEnum tipo);
}