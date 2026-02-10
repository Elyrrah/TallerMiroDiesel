/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.ClienteDocumentoDAO;
import py.taller.tallermirodiesel.dao.impl.ClienteDocumentoDAOImpl;
import py.taller.tallermirodiesel.model.ClienteDocumento;
import py.taller.tallermirodiesel.service.ClienteDocumentoService;

/**
 * @author elyrr
 */
public class ClienteDocumentoServiceImpl implements ClienteDocumentoService {

    private final ClienteDocumentoDAO clienteDocumentoDAO;

    // Constructor por defecto (usa implementación concreta)
    public ClienteDocumentoServiceImpl() {
        this.clienteDocumentoDAO = new ClienteDocumentoDAOImpl();
    }

    // Constructor para inyección (tests / configuración)
    public ClienteDocumentoServiceImpl(ClienteDocumentoDAO clienteDocumentoDAO) {
        this.clienteDocumentoDAO = clienteDocumentoDAO;
    }

    // Crea o actualiza un documento de cliente
    @Override
    public boolean guardar(ClienteDocumento clienteDocumento) {
        if (clienteDocumento == null) {
            throw new IllegalArgumentException("El clienteDocumento no puede ser null");
        }

        // Validaciones mínimas (reglas de negocio)
        if (clienteDocumento.getIdCliente() == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        if (clienteDocumento.getIdTipoDocumento() == null) {
            throw new IllegalArgumentException("El idTipoDocumento es obligatorio");
        }

        String numero = clienteDocumento.getNumero();
        if (numero != null) {
            numero = numero.trim();
            if (numero.isEmpty()) {
                numero = null;
            }
            clienteDocumento.setNumero(numero);
        }

        // Reglas comunes:
        // - Si viene como principal, lo definimos como principal (para que quede único).
        // - Si NO viene como principal, solo guardamos.
        boolean ok = clienteDocumentoDAO.guardar(clienteDocumento);

        // Si se guardó y debe ser principal, aseguramos unicidad marcando principal.
        // OJO: para marcar principal se requiere idClienteDocumento. Si en tu modelo todavía
        // no se setea el id al insertar, entonces esta parte debe resolverse en el DAO
        // retornando el id o buscando el registro insertado.
        if (ok && clienteDocumento.isPrincipal() && clienteDocumento.getIdClienteDocumento() != null) {
            clienteDocumentoDAO.definirPrincipal(
                    clienteDocumento.getIdCliente(),
                    clienteDocumento.getIdClienteDocumento()
            );
        }

        return ok;
    }

    @Override
    public boolean existePorId(Long idClienteDocumento) {
        if (idClienteDocumento == null) {
            return false;
        }
        return clienteDocumentoDAO.existePorId(idClienteDocumento);
    }

    @Override
    public boolean setActivo(Long idClienteDocumento, boolean activo) {
        if (idClienteDocumento == null) {
            throw new IllegalArgumentException("El idClienteDocumento es obligatorio");
        }
        return clienteDocumentoDAO.setActivo(idClienteDocumento, activo);
    }

    @Override
    public boolean activar(Long idClienteDocumento) {
        return setActivo(idClienteDocumento, true);
    }

    @Override
    public boolean desactivar(Long idClienteDocumento) {
        return setActivo(idClienteDocumento, false);
    }

    @Override
    public boolean definirPrincipal(Long idCliente, Long idClienteDocumento) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        if (idClienteDocumento == null) {
            throw new IllegalArgumentException("El idClienteDocumento es obligatorio");
        }
        return clienteDocumentoDAO.definirPrincipal(idCliente, idClienteDocumento);
    }

    @Override
    public boolean existePorClienteTipoNumero(Long idCliente, Long idTipoDocumento, String numero) {
        if (idCliente == null || idTipoDocumento == null) {
            return false;
        }

        if (numero != null) {
            numero = numero.trim();
            if (numero.isEmpty()) {
                numero = null;
            }
        }

        return clienteDocumentoDAO.existePorClienteTipoNumero(idCliente, idTipoDocumento, numero);
    }

    @Override
    public Optional<ClienteDocumento> buscarPorId(Long idClienteDocumento) {
        if (idClienteDocumento == null) {
            return Optional.empty();
        }
        return clienteDocumentoDAO.buscarPorId(idClienteDocumento);
    }

    @Override
    public List<ClienteDocumento> listarPorCliente(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        return clienteDocumentoDAO.listarPorCliente(idCliente);
    }

    @Override
    public List<ClienteDocumento> listarActivosPorCliente(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        return clienteDocumentoDAO.listarActivosPorCliente(idCliente);
    }

    @Override
    public Optional<ClienteDocumento> obtenerPrincipalPorCliente(Long idCliente) {
        if (idCliente == null) {
            return Optional.empty();
        }
        return clienteDocumentoDAO.obtenerPrincipalPorCliente(idCliente);
    }
}
