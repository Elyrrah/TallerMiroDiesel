/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.model.Modelo;
import com.tallermirodiesel.util.DatabaseConnection;
/**
 *
 * @author elyrr
 */
public class ModeloDAOImpl implements ModeloDAO{

    //  Mapear Modelo
    private Modelo mapearModelo(ResultSet rs) throws SQLException {
        Modelo m = new Modelo();
        m.setIdModelo(rs.getLong("id_modelo"));
        m.setIdMarca(rs.getLong("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));

        // Nota: nombre_marca solo existe cuando el SELECT hace JOIN con marcas
        try {
            m.setNombreMarca(rs.getString("nombre_marca"));
        } catch (SQLException ignore) {
            m.setNombreMarca(null);
        }

        return m;
    }

    //  Crea un nuevo Modelo
    @Override
    public Long crear(Modelo modelo) {
        String sql = """
                INSERT INTO public.modelos (id_marca, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_modelo
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, modelo.getIdMarca());
            ps.setString(2, modelo.getNombre());
            ps.setBoolean(3, modelo.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_modelo");
                }
                throw new RuntimeException("No se generó id_modelo al crear el modelo.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando modelo: " + e.getMessage(), e);
        }
    }

    //  Actualiza un Modelo
    @Override
    public boolean actualizar(Modelo modelo) {
        String sql = """
                UPDATE public.modelos
                SET id_marca = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_modelo = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, modelo.getIdMarca());
            ps.setString(2, modelo.getNombre());
            ps.setBoolean(3, modelo.isActivo());
            ps.setLong(4, modelo.getIdModelo());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando modelo: " + e.getMessage(), e);
        }
    }

    //  Elimina un Modelo
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.modelos WHERE id_modelo = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando modelo: " + e.getMessage(), e);
        }
    }

    //  Activa un Modelo
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.modelos
            SET activo = true
            WHERE id_modelo = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando modelo: " + e.getMessage(), e);
        }
    }

    //  Desactiva un Modelo
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.modelos
                SET activo = false
                WHERE id_modelo = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando modelo: " + e.getMessage(), e);
        }
    }

    //  Busca un Modelo por su id
    @Override
    public Optional<Modelo> buscarPorId(Long id) {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            WHERE mo.id_modelo = ?
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearModelo(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando modelo por id: " + e.getMessage(), e);
        }
    }

    //  Busca un Modelo por su nombre
    @Override
    public Optional<Modelo> buscarPorNombre(String nombre) {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            WHERE UPPER(TRIM(mo.nombre)) = UPPER(TRIM(?))
            ORDER BY ma.nombre ASC, mo.nombre ASC
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? null : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearModelo(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando modelo por nombre: " + e.getMessage(), e);
        }
    }

    //  Busca Modelos cuyo nombre coincida parcialmente
    @Override
    public List<Modelo> buscarPorNombreParcial(String filtro) {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            WHERE mo.nombre ILIKE ?
            ORDER BY ma.nombre ASC, mo.nombre ASC
            """;

        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearModelo(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error buscando modelo por nombre parcial: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Modelos
    @Override
    public List<Modelo> listarTodos() {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            ORDER BY ma.nombre ASC, mo.nombre ASC
            """;

        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearModelo(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando modelos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Modelos Activos
    @Override
    public List<Modelo> listarActivos() {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            WHERE mo.activo = true
            ORDER BY ma.nombre ASC, mo.nombre ASC
            """;

        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearModelo(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando modelos activos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Modelos Inactivos
    @Override
    public List<Modelo> listarInactivos() {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            WHERE mo.activo = false
            ORDER BY ma.nombre ASC, mo.nombre ASC
            """;

        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearModelo(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando modelos inactivos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Modelos de una Marca
    @Override
    public List<Modelo> listarPorMarca(Long idMarca) {
        String sql = """
            SELECT mo.id_modelo, mo.id_marca, mo.nombre, mo.activo, ma.nombre AS nombre_marca
            FROM public.modelos mo
            JOIN public.marcas ma ON ma.id_marca = mo.id_marca
            WHERE mo.id_marca = ?
            ORDER BY mo.nombre ASC
            """;

        List<Modelo> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idMarca);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearModelo(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando modelos por marca: " + e.getMessage(), e);
        }
    }
}