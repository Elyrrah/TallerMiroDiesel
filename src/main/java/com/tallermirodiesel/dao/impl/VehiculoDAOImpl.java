/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.VehiculoDAO;
import com.tallermirodiesel.model.Vehiculo;
import com.tallermirodiesel.model.enums.TipoVehiculoEnum;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class VehiculoDAOImpl implements VehiculoDAO {

    // Inicialización de consultas SQL en una sola línea
    private static final String SELECT_BASE = """
                        SELECT v.id_vehiculo, v.placa, v.id_marca, v.id_modelo, v.anio, v.tipo_vehiculo, v.observaciones, v.activo, 
                        ma.nombre AS nombre_marca, mo.nombre AS nombre_modelo 
                        FROM public.vehiculos v JOIN public.marcas ma ON ma.id_marca = v.id_marca 
                        LEFT JOIN public.modelos mo ON mo.id_modelo = v.id_modelo""";
    
    private static final String SQL_INSERT = """
                        INSERT INTO public.vehiculos (placa, id_marca, id_modelo, anio, tipo_vehiculo, observaciones, activo) 
                        VALUES (?, ?, ?, ?, ?::public.tipo_vehiculo_enum, ?, ?) RETURNING id_vehiculo""";
    
    private static final String SQL_UPDATE = """
                        UPDATE public.vehiculos SET placa = ?, id_marca = ?, id_modelo = ?, anio = ?, tipo_vehiculo = ?::public.tipo_vehiculo_enum, 
                        observaciones = ?, activo = ? WHERE id_vehiculo = ?""";
    
    private static final String SQL_DELETE = "DELETE FROM public.vehiculos WHERE id_vehiculo = ?";
    
    private static final String SQL_ACTIVAR = "UPDATE public.vehiculos SET activo = true WHERE id_vehiculo = ?";
    
    private static final String SQL_DESACTIVAR = "UPDATE public.vehiculos SET activo = false WHERE id_vehiculo = ?";

    // Método para Crear un nuevo Vehículo y retornar su ID
    @Override
    public Long crear(Vehiculo v) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setString(1, v.getPlaca());
            ps.setLong(2, v.getIdMarca());

            if (v.getIdModelo() != null) {
                ps.setLong(3, v.getIdModelo());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            if (v.getAnio() != null) {
                ps.setShort(4, v.getAnio());
            } else {
                ps.setNull(4, Types.SMALLINT);
            }

            ps.setString(5, v.getTipoVehiculo().name());
            ps.setString(6, v.getObservaciones());
            ps.setBoolean(7, v.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_vehiculo");
                }
                throw new RuntimeException("No se generó id_vehiculo");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear vehículo: " + e.getMessage(), e);
        }
    }

    // Método para Actualizar los datos de un Vehículo existente
    @Override
    public boolean actualizar(Vehiculo v) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, v.getPlaca());
            ps.setLong(2, v.getIdMarca());

            if (v.getIdModelo() != null) {
                ps.setLong(3, v.getIdModelo());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            if (v.getAnio() != null) {
                ps.setShort(4, v.getAnio());
            } else {
                ps.setNull(4, Types.SMALLINT);
            }

            ps.setString(5, v.getTipoVehiculo().name());
            ps.setString(6, v.getObservaciones());
            ps.setBoolean(7, v.isActivo());
            ps.setLong(8, v.getIdVehiculo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar vehículo: " + e.getMessage(), e);
        }
    }

    // Método para Eliminar físicamente un Vehículo por ID
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar vehículo: " + e.getMessage(), e);
        }
    }

    // Método para Activar un Vehículo (borrado lógico inverso)
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar vehículo: " + e.getMessage(), e);
        }
    }

    // Método para Desactivar un Vehículo (borrado lógico)
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DESACTIVAR)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar vehículo: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Vehículo por su ID
    @Override
    public Optional<Vehiculo> buscarPorId(Long id) {
        String sql = SELECT_BASE + " WHERE v.id_vehiculo = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar vehículo por ID: " + e.getMessage(), e);
        }
    }

    // Método para Buscar un Vehículo por nombre (enlace con placa)
    @Override
    public Optional<Vehiculo> buscarPorNombre(String nombre) {
        return buscarPorPlaca(nombre);
    }

    // Método para Buscar un Vehículo por su Placa exacta
    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        String sql = SELECT_BASE + " WHERE UPPER(TRIM(v.placa)) = UPPER(TRIM(?))";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar vehículo por placa: " + e.getMessage(), e);
        }
    }

    // Método para Buscar Vehículos por coincidencia parcial de Placa
    @Override
    public List<Vehiculo> buscarPorNombreParcial(String filtro) {
        String sql = SELECT_BASE + " WHERE v.placa ILIKE ? ORDER BY v.placa ASC";
        List<Vehiculo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + (filtro == null ? "" : filtro.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al buscar vehículos por placa parcial: " + e.getMessage(), e);
        }
    }

    // Método para Listar todos los Vehículos registrados
    @Override
    public List<Vehiculo> listarTodos() {
        String sql = SELECT_BASE + " ORDER BY ma.nombre ASC, v.placa ASC";
        return listarGenerico(sql);
    }

    // Método para Listar los Vehículos con estado Activo
    @Override
    public List<Vehiculo> listarActivos() {
        String sql = SELECT_BASE + " WHERE v.activo = true ORDER BY ma.nombre ASC, v.placa ASC";
        return listarGenerico(sql);
    }

    // Método para Listar los Vehículos con estado Inactivo
    @Override
    public List<Vehiculo> listarInactivos() {
        String sql = SELECT_BASE + " WHERE v.activo = false ORDER BY ma.nombre ASC, v.placa ASC";
        return listarGenerico(sql);
    }

    // Método para Listar Vehículos filtrados por Marca
    @Override
    public List<Vehiculo> listarPorMarca(Long idMarca) {
        String sql = SELECT_BASE + " WHERE v.id_marca = ? ORDER BY v.placa ASC";
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idMarca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar vehículos por marca: " + e.getMessage(), e);
        }
    }

    // Método para Listar Vehículos filtrados por Tipo
    @Override
    public List<Vehiculo> listarPorTipo(TipoVehiculoEnum tipo) {
        String sql = SELECT_BASE + " WHERE v.tipo_vehiculo = ?::public.tipo_vehiculo_enum ORDER BY ma.nombre ASC, v.placa ASC";
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar vehículos por tipo: " + e.getMessage(), e);
        }
    }

    // Método genérico privado para ejecutar consultas de listado
    private List<Vehiculo> listarGenerico(String sql) {
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar vehículos: " + e.getMessage(), e);
        }
    }

    // Método para Mapear el ResultSet a un objeto Vehiculo
    private Vehiculo mapear(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(rs.getLong("id_vehiculo"));
        v.setPlaca(rs.getString("placa"));
        v.setIdMarca(rs.getLong("id_marca"));

        long idModelo = rs.getLong("id_modelo");
        v.setIdModelo(rs.wasNull() ? null : idModelo);

        short anio = rs.getShort("anio");
        v.setAnio(rs.wasNull() ? null : anio);

        v.setTipoVehiculo(TipoVehiculoEnum.valueOf(rs.getString("tipo_vehiculo")));
        v.setObservaciones(rs.getString("observaciones"));
        v.setActivo(rs.getBoolean("activo"));
        v.setNombreMarca(rs.getString("nombre_marca"));
        v.setNombreModelo(rs.getString("nombre_modelo"));
        return v;
    }
}