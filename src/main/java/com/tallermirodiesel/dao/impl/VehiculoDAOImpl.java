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

    // BASE SELECT con JOIN a marcas y modelos para traer los nombres
    private static final String SELECT_BASE = """
            SELECT v.id_vehiculo,
                   v.placa,
                   v.id_marca,
                   v.id_modelo,
                   v.anio,
                   v.tipo_vehiculo,
                   v.observaciones,
                   v.activo,
                   ma.nombre AS nombre_marca,
                   mo.nombre AS nombre_modelo
            FROM public.vehiculos v
            JOIN public.marcas ma ON ma.id_marca = v.id_marca
            LEFT JOIN public.modelos mo ON mo.id_modelo = v.id_modelo
            """;

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

    @Override
    public Long crear(Vehiculo v) {
        String sql = """
                INSERT INTO public.vehiculos
                    (placa, id_marca, id_modelo, anio, tipo_vehiculo, observaciones, activo)
                VALUES (?, ?, ?, ?, ?::public.tipo_vehiculo_enum, ?, ?)
                RETURNING id_vehiculo
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    @Override
    public boolean actualizar(Vehiculo v) {
        String sql = """
                UPDATE public.vehiculos
                SET placa         = ?,
                    id_marca      = ?,
                    id_modelo     = ?,
                    anio          = ?,
                    tipo_vehiculo = ?::public.tipo_vehiculo_enum,
                    observaciones = ?,
                    activo        = ?
                WHERE id_vehiculo = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

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

    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.vehiculos WHERE id_vehiculo = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar vehículo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activar(Long id) {
        String sql = "UPDATE public.vehiculos SET activo = true WHERE id_vehiculo = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar vehículo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean desactivar(Long id) {
        String sql = "UPDATE public.vehiculos SET activo = false WHERE id_vehiculo = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar vehículo: " + e.getMessage(), e);
        }
    }

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

    @Override
    public Optional<Vehiculo> buscarPorNombre(String nombre) {
        // En vehículos el "nombre" equivale a la placa
        return buscarPorPlaca(nombre);
    }

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

    @Override
    public List<Vehiculo> buscarPorNombreParcial(String filtro) {
        // En vehículos buscamos por placa parcial
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

    @Override
    public List<Vehiculo> listarTodos() {
        String sql = SELECT_BASE + " ORDER BY ma.nombre ASC, v.placa ASC";

        List<Vehiculo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar todos los vehículos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Vehiculo> listarActivos() {
        String sql = SELECT_BASE + " WHERE v.activo = true ORDER BY ma.nombre ASC, v.placa ASC";

        List<Vehiculo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar vehículos activos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Vehiculo> listarInactivos() {
        String sql = SELECT_BASE + " WHERE v.activo = false ORDER BY ma.nombre ASC, v.placa ASC";

        List<Vehiculo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al listar vehículos inactivos: " + e.getMessage(), e);
        }
    }

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
}