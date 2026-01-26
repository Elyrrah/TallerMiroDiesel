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
import py.taller.tallermirodiesel.dao.MarcaDAO;
import py.taller.tallermirodiesel.model.Marca;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 *
 * @author elyrr
 */
public class MarcaDAOImpl implements MarcaDAO {

    //  Mapear Marca
    private Marca mapearMarca(ResultSet rs) throws SQLException {
        Marca m = new Marca();
        m.setIdMarca(rs.getLong("id_marca"));
        m.setNombre(rs.getString("nombre"));
        m.setActivo(rs.getBoolean("activo"));

        return m;
    }

    //  Crea una nueva Marca
    @Override
    public Long crear(Marca marca) {
        String sql = """
                INSERT INTO public.marcas (nombre, activo)
                VALUES (?, ?)
                RETURNING id_marca
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, marca.getNombre());
            ps.setBoolean(2, marca.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_marca");
                }
                throw new RuntimeException("No se generó id_marca al crear la marca.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando marca: " + e.getMessage(), e);
        }
    }

    //  Actualiza un Marca
    @Override
    public boolean actualizar(Marca marca) {
        String sql = """
                UPDATE public.marcas
                SET nombre = ?,
                    activo = ?
                WHERE id_marca = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, marca.getNombre());
            ps.setBoolean(2, marca.isActivo());
            ps.setLong(3, marca.getIdMarca());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando marca: " + e.getMessage(), e);
        }
    }

    //  Elimina un Marca
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.marcas WHERE id_marca = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando marca: " + e.getMessage(), e);
        }
    }

    //  Activa un Marca
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.marcas
            SET activo = true
            WHERE id_marca = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando marca: " + e.getMessage(), e);
        }
    }

    //  Desactiva un Marca
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.marcas
                SET activo = false
                WHERE id_marca = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando marca: " + e.getMessage(), e);
        }
    }

    //  Busca un Marca por su id
    @Override
    public Optional<Marca> buscarPorId(Long id) {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE id_marca = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMarca(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando marca por id: " + e.getMessage(), e);
        }
    }

    //  Busca un Marca por su nombre
    @Override
    public Optional<Marca> buscarPorNombre(String nombre) {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE UPPER(TRIM(nombre)) = UPPER(TRIM(?))
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? null : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMarca(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando marca por nombre: " + e.getMessage(), e);
        }
    }

    //  Busca Marcas cuyo nombre coincida parcialmente
    @Override
    public List<Marca> buscarPorNombreParcial(String filtro) {
        String sql = """
                SELECT id_marca, nombre, activo
                FROM public.marcas
                WHERE nombre ILIKE ?
                ORDER BY nombre ASC
                """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMarca(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error buscando marca por nombre parcial: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Marcas
    @Override
    public List<Marca> listarTodos() {
        String sql = """
            SELECT id_marca, nombre, activo
            FROM public.marcas
            ORDER BY nombre ASC
            """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando marcas: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Marcas Activos
    @Override
    public List<Marca> listarActivos() {
        String sql = """
            SELECT id_marca, nombre, activo
            FROM public.marcas
            WHERE activo = true
            ORDER BY nombre ASC
            """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando marcas activos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Marcas Inactivas
    @Override
    public List<Marca> listarInactivos() {
        String sql = """
            SELECT id_marca, nombre, activo
            FROM public.marcas
            WHERE activo = false
            ORDER BY nombre ASC
            """;

        List<Marca> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMarca(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando marcas inactivos: " + e.getMessage(), e);
        }
    }
}
