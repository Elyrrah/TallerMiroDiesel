/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.util.Optional;
import py.taller.tallermirodiesel.model.TipoDocumento;

/**
 * @author elyrr
 */
public interface TipoDocumentoDAO extends CatalogoCrudDAO<TipoDocumento, Long> {

    //  Busca un TipoDocumento por su codigo
    Optional<TipoDocumento> buscarPorCodigo(String codigo);
}
