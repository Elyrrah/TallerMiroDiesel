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
import py.taller.tallermirodiesel.dao.DepartamentoDAO;
import py.taller.tallermirodiesel.model.Departamento;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class DepartamentoDAOImpl implements DepartamentoDAO{
    
    //  Mapear Departamento
    private Departamento mapearDepartamento(ResultSet rs) throws SQLException {
        Departamento d = new Departamento();
        d.setIdDepartamento(rs.getLong("id_departamento"));
        d.setIdPais(rs.getLong("id_pais"));
        d.setNombre(rs.getString("nombre"));
        d.setActivo(rs.getBoolean("activo"));

        // nombre_pais viene del JOIN (AS nombre_pais)
        d.setNombrePais(rs.getString("nombre_pais"));
                
        return d;
    }
    
    //  Crea un nuevo Departamento
    @Override
    public Long crear(Departamento departamento) {
        String sql = """
                INSERT INTO public.departamentos (id_pais, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_departamento
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, departamento.getIdPais());
            ps.setString(2, departamento.getNombre());
            ps.setBoolean(3, departamento.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_departamento");
                }
                throw new RuntimeException("No se generó id_departamento al crear el departamento.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando departamento: " + e.getMessage(), e);
        }
    }

    //  Actualiza un Departamento
    @Override
    public boolean actualizar(Departamento departamento) {
        String sql = """
                UPDATE public.departamentos
                SET id_pais = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_departamento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, departamento.getIdPais());
            ps.setString(2, departamento.getNombre());
            ps.setBoolean(3, departamento.isActivo());
            ps.setLong(4, departamento.getIdDepartamento());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando departamento: " + e.getMessage(), e);
        }
    }

    //  Elimina un Departamento
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.departamentos WHERE id_departamento = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando departamento: " + e.getMessage(), e);
        }
    }    

    //  Activa un Departamento
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.departamentos
            SET activo = true
            WHERE id_departamento = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando departamento: " + e.getMessage(), e);
        }
    }

    //  Desactiva un Departamento
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.departamentos
                SET activo = false
                WHERE id_departamento = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando departamento: " + e.getMessage(), e);
        }
    }

    //  Busca un Departamento por su id
    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        String sql = """
            SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
            FROM public.departamentos d
            JOIN public.paises p ON p.id_pais = d.id_pais
            WHERE d.id_departamento = ?
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDepartamento(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando departamento por id: " + e.getMessage(), e);
        }
    }

    //  Busca un Departamento por su nombre
    @Override
    public Optional<Departamento> buscarPorNombre(String nombre) {

        // Validación: si viene null o vacío, no consultamos a la base (evita comparar con NULL)
        if (nombre == null || nombre.isBlank()) {
            return Optional.empty();
        }

        String sql = """
            SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
            FROM public.departamentos d
            JOIN public.paises p ON p.id_pais = d.id_pais
            WHERE UPPER(TRIM(d.nombre)) = UPPER(TRIM(?))
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDepartamento(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando departamento por nombre: " + e.getMessage(), e);
        }
    }

    //  Busca Departamentos cuyo nombre coincida parcialmente
    @Override
    public List<Departamento> buscarPorNombreParcial(String filtro) {

        // Validación: si viene null o vacío, devolvemos lista vacía (evita ILIKE '%%')
        if (filtro == null || filtro.isBlank()) {
            return List.of();
        }

        String sql = """
            SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
            FROM public.departamentos d
            JOIN public.paises p ON p.id_pais = d.id_pais
            WHERE d.nombre ILIKE ?
            ORDER BY p.nombre ASC, d.nombre ASC
            """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDepartamento(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error buscando departamento por nombre parcial: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Departamentos
    @Override
    public List<Departamento> listarTodos() {
        String sql = """
            SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
            FROM public.departamentos d
            JOIN public.paises p ON p.id_pais = d.id_pais
            ORDER BY p.nombre ASC, d.nombre ASC
            """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando departamentos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Departamentos Activos
    @Override
    public List<Departamento> listarActivos() {
        String sql = """
            SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
            FROM public.departamentos d
            JOIN public.paises p ON p.id_pais = d.id_pais
            WHERE d.activo = true
            ORDER BY p.nombre ASC, d.nombre ASC
            """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando departamentos activos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Departamentos Inactivos
    @Override
    public List<Departamento> listarInactivos() {
        String sql = """
            SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
            FROM public.departamentos d
            JOIN public.paises p ON p.id_pais = d.id_pais
            WHERE d.activo = false
            ORDER BY p.nombre ASC, d.nombre ASC
            """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDepartamento(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando departamentos inactivos: " + e.getMessage(), e);
        }
    }
    
    //  Lista todos los Departamentos de un Pais
    @Override
    public List<Departamento> listarPorPais(Long idPais) {

        // Validación: evita NullPointerException por unboxing en ps.setLong(...)
        if (idPais == null) {
            return List.of();
        }

        String sql = """
        SELECT d.id_departamento, d.id_pais, d.nombre, d.activo, p.nombre AS nombre_pais
        FROM public.departamentos d
        JOIN public.paises p ON p.id_pais = d.id_pais
        WHERE d.id_pais = ?
        ORDER BY p.nombre ASC, d.nombre ASC
        """;

        List<Departamento> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPais);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDepartamento(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando departamentos por país: " + e.getMessage(), e);
        }
    }
}
