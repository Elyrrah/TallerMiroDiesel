/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.service;

import java.util.Optional;
import py.taller.tallermirodiesel.model.TipoDocumento;

/**
 *
 * @author elyrr
 */
public interface TipoDocumentoService extends CatalogoCrudService<TipoDocumento, Long>{
 
    //  Busca un TipoDocumento por su codigo
    Optional<TipoDocumento> buscarPorCodigo(String codigo);
}