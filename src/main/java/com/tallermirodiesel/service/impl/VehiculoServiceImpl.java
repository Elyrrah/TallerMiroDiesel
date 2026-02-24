/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.dao.VehiculoDAO;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.dao.impl.ModeloDAOImpl;
import com.tallermirodiesel.dao.impl.VehiculoDAOImpl;
import com.tallermirodiesel.model.Vehiculo;
import com.tallermirodiesel.model.enums.TipoVehiculoEnum;
import com.tallermirodiesel.service.VehiculoService;

/**
 * @author elyrr
 */
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoDAO vehiculoDAO;
    private final MarcaDAO marcaDAO;
    private final ModeloDAO modeloDAO;

    // Inicialización de las implementaciones DAO para el servicio de vehículos, marcas y modelos
    public VehiculoServiceImpl() {
        this.vehiculoDAO = new VehiculoDAOImpl();
        this.marcaDAO    = new MarcaDAOImpl();
        this.modeloDAO   = new ModeloDAOImpl();
    }

    // Validaciones de integridad referencial, formato de placa y obligatoriedad de campos para un vehículo
    private void validarCampos(Vehiculo v) {
        // Marca obligatoria
        if (v.getIdMarca() == null || v.getIdMarca() <= 0) {
            throw new IllegalArgumentException("La marca es obligatoria.");
        }
        if (marcaDAO.buscarPorId(v.getIdMarca()).isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + v.getIdMarca());
        }

        // Modelo opcional, pero si viene debe existir y pertenecer a la marca
        if (v.getIdModelo() != null) {
            var modelo = modeloDAO.buscarPorId(v.getIdModelo());
            if (modelo.isEmpty()) {
                throw new IllegalArgumentException("No existe un modelo con id: " + v.getIdModelo());
            }
            if (!modelo.get().getIdMarca().equals(v.getIdMarca())) {
                throw new IllegalArgumentException("El modelo no pertenece a la marca seleccionada.");
            }
        }

        // Tipo de vehículo obligatorio
        if (v.getTipoVehiculo() == null) {
            throw new IllegalArgumentException("El tipo de vehículo es obligatorio.");
        }

        // Placa opcional, pero si viene la normalizamos
        if (v.getPlaca() != null && !v.getPlaca().isBlank()) {
            v.setPlaca(v.getPlaca().trim().toUpperCase());
        } else {
            v.setPlaca(null);
        }

        // Observaciones opcionales
        if (v.getObservaciones() != null && v.getObservaciones().isBlank()) {
            v.setObservaciones(null);
        }
    }

    // Validaciones para registrar un nuevo vehículo en el sistema
    @Override
    public Long crear(Vehiculo v) {
        if (v == null) {
            throw new IllegalArgumentException("El vehículo no puede ser null.");
        }

        validarCampos(v);

        // Si viene placa, verificar que no exista ya
        if (v.getPlaca() != null && vehiculoDAO.buscarPorPlaca(v.getPlaca()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehículo con la placa: " + v.getPlaca());
        }

        v.setActivo(true);
        return vehiculoDAO.crear(v);
    }

    // Validaciones para actualizar la información técnica de un vehículo
    @Override
    public boolean actualizar(Vehiculo v) {
        if (v == null || v.getIdVehiculo() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        validarCampos(v);

        if (vehiculoDAO.buscarPorId(v.getIdVehiculo()).isEmpty()) {
            throw new IllegalArgumentException("No existe un vehículo con id: " + v.getIdVehiculo());
        }

        // Si viene placa, verificar que no la tenga otro vehículo
        if (v.getPlaca() != null) {
            Optional<Vehiculo> existente = vehiculoDAO.buscarPorPlaca(v.getPlaca());
            if (existente.isPresent() && !existente.get().getIdVehiculo().equals(v.getIdVehiculo())) {
                throw new IllegalArgumentException("Ya existe otro vehículo con la placa: " + v.getPlaca());
            }
        }

        return vehiculoDAO.actualizar(v);
    }

    // Validaciones para activar un vehículo para su uso en órdenes de trabajo
    @Override
    public boolean activar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del vehículo debe ser válido.");
        }
        if (vehiculoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un vehículo con id: " + id);
        }
        return vehiculoDAO.activar(id);
    }

    // Validaciones para desactivar un vehículo del sistema
    @Override
    public boolean desactivar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del vehículo debe ser válido.");
        }
        if (vehiculoDAO.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un vehículo con id: " + id);
        }
        return vehiculoDAO.desactivar(id);
    }

    // Lógica para obtener los detalles de un vehículo por su identificador único
    @Override
    public Optional<Vehiculo> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del vehículo no es válido.");
        }
        return vehiculoDAO.buscarPorId(id);
    }

    // Lógica para redirigir la búsqueda por nombre hacia la búsqueda por placa
    @Override
    public Optional<Vehiculo> buscarPorNombre(String placa) {
        return buscarPorPlaca(placa);
    }

    // Lógica para localizar un vehículo mediante su número de placa exacto
    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa no puede estar vacía.");
        }
        return vehiculoDAO.buscarPorPlaca(placa.trim().toUpperCase());
    }

    // Lógica para filtrar vehículos según una coincidencia parcial en la placa u otro criterio
    @Override
    public List<Vehiculo> buscarPorNombreParcial(String filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }
        return vehiculoDAO.buscarPorNombreParcial(filtro.trim());
    }

    // Lógica para obtener el listado completo de vehículos registrados
    @Override
    public List<Vehiculo> listarTodos() {
        return vehiculoDAO.listarTodos();
    }

    // Lógica para listar únicamente los vehículos habilitados en el sistema
    @Override
    public List<Vehiculo> listarActivos() {
        return vehiculoDAO.listarActivos();
    }

    // Lógica para listar únicamente los vehículos que han sido desactivados
    @Override
    public List<Vehiculo> listarInactivos() {
        return vehiculoDAO.listarInactivos();
    }

    // Lógica para obtener todos los vehículos pertenecientes a una marca específica
    @Override
    public List<Vehiculo> listarPorMarca(Long idMarca) {
        if (idMarca == null || idMarca <= 0) {
            throw new IllegalArgumentException("El id de marca no es válido.");
        }
        return vehiculoDAO.listarPorMarca(idMarca);
    }

    // Lógica para filtrar vehículos según su categoría técnica (Camión, Camioneta, etc.)
    @Override
    public List<Vehiculo> listarPorTipo(TipoVehiculoEnum tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser null.");
        }
        return vehiculoDAO.listarPorTipo(tipo);
    }
}