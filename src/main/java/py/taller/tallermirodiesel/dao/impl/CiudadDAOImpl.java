/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.CiudadDAO;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.util.DatabaseConnection;
/**
 * @author elyrr
 */
public class CiudadDAOImpl implements CiudadDAO {
    //  Mapear Ciudad
    private Ciudad mapearCiudad(ResultSet rs) throws SQLException {
        Ciudad c = new Ciudad();
        c.setIdCiudad(rs.getLong("id_ciudad"));
        c.setIdDepartamento(rs.getLong("id_departamento"));
        c.setNombre(rs.getString("nombre"));
        c.setActivo(rs.getBoolean("activo"));

        try {
            c.setNombreDepartamento(rs.getString("nombre_departamento"));
        } catch (SQLException ignore) {
            c.setNombreDepartamento(null);
        }

        return c;
    }
    
    //  Crea una nueva Ciudad
    @Override
    public Long crear(Ciudad ciudad) {
        String sql = """
                INSERT INTO public.ciudades (id_departamento, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_ciudad
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, ciudad.getIdDepartamento());
            ps.setString(2, ciudad.getNombre() == null ? null : ciudad.getNombre().trim().toUpperCase());
            ps.setBoolean(3, ciudad.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_ciudad");
                }
                throw new RuntimeException("No se generó id_ciudad al crear el ciudad.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando ciudad: " + e.getMessage(), e);
        }
    }

    //  Actualiza una Ciudad
    @Override
    public boolean actualizar(Ciudad ciudad) {
        String sql = """
                UPDATE public.ciudades
                SET id_departamento = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_ciudad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, ciudad.getIdDepartamento());
            ps.setString(2, ciudad.getNombre() == null ? null : ciudad.getNombre().trim().toUpperCase());
            ps.setBoolean(3, ciudad.isActivo());
            ps.setLong(4, ciudad.getIdCiudad());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando ciudad: " + e.getMessage(), e);
        }
    }

    //  Elimina una Ciudad
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.ciudades WHERE id_ciudad = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando ciudad: " + e.getMessage(), e);
        }
    }    

    //  Activa una ciudad
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.ciudades
            SET activo = true
            WHERE id_ciudad = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando ciudad: " + e.getMessage(), e);
        }
    }

    //  Desactiva una Ciudad
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.ciudades
                SET activo = false
                WHERE id_ciudad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando ciudad: " + e.getMessage(), e);
        }
    }

    //  Busca una Ciudad por su id
    @Override
    public Optional<Ciudad> buscarPorId(Long id) {
        String sql = """
            SELECT c.id_ciudad, c.id_departamento, d.nombre, d.activo, d.nombre AS nombre_departamento
            FROM public.ciudades c
            JOIN public.departamentos d ON d.id_departamento = c.id_departamento
            WHERE c.id_ciudad = ?
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCiudad(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando ciudad por id: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Ciudades
    @Override
    public List<Ciudad> listarTodos() {
        String sql = """
                SELECT c.id_ciudad, c.id_departamento, c.nombre, c.activo, d.nombre AS nombre_departamento
                FROM public.ciudades c
                JOIN public.departamentos d ON d.id_departamento = c.id_departamento
                ORDER BY d.nombre, c.nombre
                """;

        List<Ciudad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCiudad(rs));
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando ciudades: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Ciudades Activas
    @Override
    public List<Ciudad> listarActivos() {
        String sql = """
                SELECT c.id_ciudad, c.id_departamento, c.nombre, c.activo, d.nombre AS nombre_departamento
                FROM public.ciudades c
                JOIN public.departamentos d ON d.id_departamento = c.id_departamento
                WHERE c.activo = true
                ORDER BY d.nombre, c.nombre
                """;

        List<Ciudad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCiudad(rs));
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando ciudades activas: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Ciudades Inactivas
    @Override
    public List<Ciudad> listarInactivos() {
        String sql = """
                SELECT c.id_ciudad, c.id_departamento, c.nombre, c.activo, d.nombre AS nombre_departamento
                FROM public.ciudades c
                JOIN public.departamentos d ON d.id_departamento = c.id_departamento
                WHERE c.activo = false
                ORDER BY d.nombre, c.nombre
                """;

        List<Ciudad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCiudad(rs));
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando ciudades inactivas: " + e.getMessage(), e);
        }
    }
    
    
    //  Lista todas las Ciudades de un Departamento
    @Override
    public List<Ciudad> listarPorDepartamento(Long idDepartamento) {
        String sql = """
                SELECT c.id_ciudad, c.id_departamento, c.nombre, c.activo, d.nombre AS nombre_departamento
                FROM public.ciudades c
                JOIN public.departamentos d ON d.id_departamento = c.id_departamento
                WHERE c.id_departamento = ?
                ORDER BY c.nombre
                """;

        List<Ciudad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCiudad(rs));
                }
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando ciudades por departamento: " + e.getMessage(), e);
        }
    }
}
