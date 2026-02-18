package com.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Departamento;

/**
 * @author elyrr
 */
public interface DepartamentoService extends CatalogoCrudService<Departamento, Long> {

    // Busca un Departamento por nombre dentro de un País concreto
    Optional<Departamento> buscarPorNombre(String nombre, Long idPais);

    // Lista todos los Departamentos de un País
    List<Departamento> listarPorPais(Long idPais);
}