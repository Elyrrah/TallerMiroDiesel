/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.TipoDocumento;
import py.taller.tallermirodiesel.model.enums.TipoDocumentoAplicaEnum;

/**
 * @author elyrr
 */
public interface TipoDocumentoService extends CatalogoCrudService<TipoDocumento, Long>{
 
    //  Busca un TipoDocumento por su codigo
    Optional<TipoDocumento> buscarPorCodigo(String codigo);
    
    //  Lista tipos de documento filtrados por aplicación
    List<TipoDocumento> listarPorAplicaA(TipoDocumentoAplicaEnum aplicaA);
    
    //  Lista tipos de documento activos filtrados por aplicación
    List<TipoDocumento> listarActivosPorAplicaA(TipoDocumentoAplicaEnum aplicaA);
}