/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ClienteDocumentoDAO;
import com.tallermirodiesel.dao.impl.ClienteDocumentoDAOImpl;
import com.tallermirodiesel.model.ClienteDocumento;
import com.tallermirodiesel.service.ClienteDocumentoService;

/**
 * @author elyrr
 */
public class ClienteDocumentoServiceImpl implements ClienteDocumentoService {

    private final ClienteDocumentoDAO clienteDocumentoDAO;

    // Inicialización de la implementación del DAO para la gestión de documentos asociados a clientes
    public ClienteDocumentoServiceImpl() {
        this.clienteDocumentoDAO = new ClienteDocumentoDAOImpl();
    }

    // Constructor para inyección de dependencias del DAO de documentos de cliente
    public ClienteDocumentoServiceImpl(ClienteDocumentoDAO clienteDocumentoDAO) {
        this.clienteDocumentoDAO = clienteDocumentoDAO;
    }

    // Validaciones para guardar o actualizar un documento y gestión de la marca de documento principal
    @Override
    public boolean guardar(ClienteDocumento clienteDocumento) {
        if (clienteDocumento == null) {
            throw new IllegalArgumentException("El clienteDocumento no puede ser null");
        }

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

        boolean ok = clienteDocumentoDAO.guardar(clienteDocumento);

        if (ok && clienteDocumento.isPrincipal() && clienteDocumento.getIdClienteDocumento() != null) {
            clienteDocumentoDAO.definirPrincipal(
                    clienteDocumento.getIdCliente(),
                    clienteDocumento.getIdClienteDocumento()
            );
        }

        return ok;
    }

    // Lógica para verificar la existencia de un registro de documento por su ID
    @Override
    public boolean existePorId(Long idClienteDocumento) {
        if (idClienteDocumento == null) {
            return false;
        }
        return clienteDocumentoDAO.existePorId(idClienteDocumento);
    }

    // Lógica para cambiar el estado de habilitación de un documento de cliente
    @Override
    public boolean setActivo(Long idClienteDocumento, boolean activo) {
        if (idClienteDocumento == null) {
            throw new IllegalArgumentException("El idClienteDocumento es obligatorio");
        }
        return clienteDocumentoDAO.setActivo(idClienteDocumento, activo);
    }

    // Validaciones para activar un documento específico de un cliente
    @Override
    public boolean activar(Long idClienteDocumento) {
        return setActivo(idClienteDocumento, true);
    }

    // Validaciones para desactivar un documento específico de un cliente
    @Override
    public boolean desactivar(Long idClienteDocumento) {
        return setActivo(idClienteDocumento, false);
    }

    // Lógica para establecer un documento como el contacto o identificador principal del cliente
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

    // Lógica para comprobar si un cliente ya tiene registrado un tipo de documento con un número específico
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

    // Lógica para recuperar la información de un documento de cliente mediante su ID
    @Override
    public Optional<ClienteDocumento> buscarPorId(Long idClienteDocumento) {
        if (idClienteDocumento == null) {
            return Optional.empty();
        }
        return clienteDocumentoDAO.buscarPorId(idClienteDocumento);
    }

    // Lógica para listar todos los documentos vinculados a un cliente específico
    @Override
    public List<ClienteDocumento> listarPorCliente(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        return clienteDocumentoDAO.listarPorCliente(idCliente);
    }

    // Lógica para listar solo los documentos vigentes de un cliente
    @Override
    public List<ClienteDocumento> listarActivosPorCliente(Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        return clienteDocumentoDAO.listarActivosPorCliente(idCliente);
    }

    // Lógica para obtener el documento marcado como principal de un cliente
    @Override
    public Optional<ClienteDocumento> obtenerPrincipalPorCliente(Long idCliente) {
        if (idCliente == null) {
            return Optional.empty();
        }
        return clienteDocumentoDAO.obtenerPrincipalPorCliente(idCliente);
    }
}