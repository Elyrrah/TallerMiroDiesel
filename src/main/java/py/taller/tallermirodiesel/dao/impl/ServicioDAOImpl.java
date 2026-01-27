/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.ServicioDAO;
import py.taller.tallermirodiesel.model.Servicio;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ServicioDAOImpl implements ServicioDAO {

    //  Mapear Servicio
    private Servicio mapearServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setIdServicio(rs.getLong("id_servicio"));
        s.setCodigo(rs.getString("codigo"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        s.setPrecioBase(rs.getBigDecimal("precio_base"));
        s.setActivo(rs.getBoolean("activo"));

        //  fecha_creacion (si existe en el SELECT)
        //  Nota: usamos Timestamp porque ResultSet no tiene getLocalDateTime en todos los drivers/versiones.
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            s.setFechaCreacion(ts.toLocalDateTime());
        }

        return s;
    }

    //  Crea un nuevo Servicio
    @Override
    public Long crear(Servicio servicio) {
        String sql = """
                INSERT INTO public.servicios (codigo, nombre, descripcion, precio_base, activo)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id_servicio
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getDescripcion());
            ps.setBigDecimal(4, servicio.getPrecioBase());
            ps.setBoolean(5, servicio.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_servicio");
                }
                throw new RuntimeException("No se generó id_servicio al crear el servicio.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando servicio: " + e.getMessage(), e);
        }
    }

    //  Actualiza un Servicio
    @Override
    public boolean actualizar(Servicio servicio) {
        String sql = """
                UPDATE public.servicios
                SET codigo = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio_base = ?,
                    activo = ?
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, servicio.getCodigo());
            ps.setString(2, servicio.getNombre());
            ps.setString(3, servicio.getDescripcion());
            ps.setBigDecimal(4, servicio.getPrecioBase());
            ps.setBoolean(5, servicio.isActivo());
            ps.setLong(6, servicio.getIdServicio());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando servicio: " + e.getMessage(), e);
        }
    }

    //  Elimina un Servicio
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.servicios WHERE id_servicio = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando servicio: " + e.getMessage(), e);
        }
    }

    //  Activa un Servicio
    @Override
    public boolean activar(Long id) {
        String sql = """
                UPDATE public.servicios
                SET activo = true
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando servicio: " + e.getMessage(), e);
        }
    }

    //  Desactiva un Servicio
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.servicios
                SET activo = false
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando servicio: " + e.getMessage(), e);
        }
    }

    //  Busca un Servicio por su id
    @Override
    public Optional<Servicio> buscarPorId(Long id) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE id_servicio = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServicio(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando servicio por id: " + e.getMessage(), e);
        }
    }

    //  Busca un Servicio por su nombre (exacto)
    @Override
    public Optional<Servicio> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? "" : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServicio(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando servicio por nombre: " + e.getMessage(), e);
        }
    }
    
    //  Busca un Servicio por su codigo
    @Override
    public Optional<Servicio> buscarPorCodigo(String codigo) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE UPPER(TRIM(codigo)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String codigoNorm = (codigo == null) ? "" : codigo.trim();
            ps.setString(1, codigoNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServicio(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando servicio por codigo: " + e.getMessage(), e);
        }
    }
    
    //  Busca Servicios cuyo nombre coincida parcialmente
    @Override
    public List<Servicio> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE nombre ILIKE ?
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearServicio(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error buscando servicio por nombre parcial: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Servicios
    @Override
    public List<Servicio> listarTodos() {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando servicios: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Servicios Activos
    @Override
    public List<Servicio> listarActivos() {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE activo = true
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando servicios activos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Servicios Inactivos
    @Override
    public List<Servicio> listarInactivos() {
        String sql = """
                SELECT id_servicio, codigo, nombre, descripcion, precio_base, activo, fecha_creacion
                FROM public.servicios
                WHERE activo = false
                ORDER BY nombre ASC
                """;

        List<Servicio> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearServicio(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando servicios inactivos: " + e.getMessage(), e);
        }
    }
}