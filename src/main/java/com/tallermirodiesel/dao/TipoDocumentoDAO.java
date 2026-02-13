/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.tallermirodiesel.dao;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.TipoDocumento;
import com.tallermirodiesel.model.enums.TipoDocumentoAplicaEnum;

/**
 * @author elyrr
 */
public interface TipoDocumentoDAO extends CatalogoCrudDAO<TipoDocumento, Long> {
    
    //  Busca un TipoDocumento por su codigo
    Optional<TipoDocumento> buscarPorCodigo(String codigo);
    
    //  Lista tipos de documento filtrados por aplicación
    List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA);
    
    //  Lista tipos de documento activos filtrados por aplicación
    List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA);
}