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
import py.taller.tallermirodiesel.model.Distrito;
import py.taller.tallermirodiesel.util.DatabaseConnection;
import py.taller.tallermirodiesel.dao.DistritoDAO;

/**
 * @author elyrr
 */
public class DistritoDAOImpl implements DistritoDAO {
    
    //  Mapear Distrito
    private Distrito mapearDistrito(ResultSet rs) throws SQLException {
        Distrito d = new Distrito();
        d.setIdDistrito(rs.getLong("id_distrito")); // FIX: antes decía id_ciudad
        d.setIdDepartamento(rs.getLong("id_departamento"));
        d.setNombre(rs.getString("nombre"));
        d.setActivo(rs.getBoolean("activo"));

        // Nota: nombre_departamento solo existe cuando el SELECT hace JOIN con departamentos
        try {
            d.setNombreDepartamento(rs.getString("nombre_departamento"));
        } catch (SQLException ignore) {
            d.setNombreDepartamento(null);
        }

        return d;
    }
    
    //  Crea un nuevo Distrito
    @Override
    public Long crear(Distrito distrito) {
        String sql = """
                INSERT INTO public.distritos (id_departamento, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_distrito
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setLong(1, distrito.getIdDepartamento());
            ps.setString(2, distrito.getNombre());
            ps.setBoolean(3, distrito.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_distrito");
                }
                throw new RuntimeException("No se generó id_distrito al crear el distrito.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando distrito: " + e.getMessage(), e);
        }
    }

    //  Actualiza un Distrito
    @Override
    public boolean actualizar(Distrito distrito) {
        String sql = """
                UPDATE public.distritos
                SET id_departamento = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_distrito = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, distrito.getIdDepartamento());
            ps.setString(2, distrito.getNombre());
            ps.setBoolean(3, distrito.isActivo());
            ps.setLong(4, distrito.getIdDistrito());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando distrito: " + e.getMessage(), e);
        }
    }

    //  Elimina un Distrito
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.distritos WHERE id_distrito = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando distrito: " + e.getMessage(), e);
        }
    }    

    //  Activa un distrito
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.distritos
            SET activo = true
            WHERE id_distrito = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando distrito: " + e.getMessage(), e);
        }
    }

    //  Desactiva un Distrito
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.distritos
                SET activo = false
                WHERE id_distrito = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando distrito: " + e.getMessage(), e);
        }
    }

    //  Busca un Distrito por su id
    @Override
    public Optional<Distrito> buscarPorId(Long id) {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            WHERE di.id_distrito = ?
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDistrito(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando distrito por id: " + e.getMessage(), e);
        }
    }

    //  Busca un Distrito por su nombre
    @Override
    public Optional<Distrito> buscarPorNombre(String nombre) {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            WHERE UPPER(TRIM(di.nombre)) = UPPER(TRIM(?))
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? null : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDistrito(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando distrito por nombre: " + e.getMessage(), e);
        }
    }

    //  Busca Distritos cuyo nombre coincida parcialmente
    @Override
    public List<Distrito> buscarPorNombreParcial(String filtro) {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            WHERE di.nombre ILIKE ?
            ORDER BY dp.nombre ASC, di.nombre ASC
            """;

        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDistrito(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error buscando distrito por nombre parcial: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Distritos
    @Override
    public List<Distrito> listarTodos() {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            ORDER BY di.nombre ASC
            """;

        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDistrito(rs));
            }
            
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando distritos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Distritos Activos
    @Override
    public List<Distrito> listarActivos() {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            WHERE di.activo = true
            ORDER BY di.nombre ASC
            """;

        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDistrito(rs));
            }
            
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando distritos activos: " + e.getMessage(), e);
        }
    }

    //  Lista todos los Distritos Inactivos
    @Override
    public List<Distrito> listarInactivos() {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            WHERE di.activo = false
            ORDER BY di.nombre ASC
            """;

        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearDistrito(rs));
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando distritos inactivos: " + e.getMessage(), e);
        }
    }
    
    
    //  Lista todos los Distritos de un Departamento
    @Override
    public List<Distrito> listarPorDepartamento(Long idDepartamento) {
        String sql = """
            SELECT di.id_distrito, di.id_departamento, di.nombre, di.activo, dp.nombre AS nombre_departamento
            FROM public.distritos di
            JOIN public.departamentos dp ON dp.id_departamento = di.id_departamento
            WHERE di.id_departamento = ?
            ORDER BY di.nombre
                """;

        List<Distrito> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idDepartamento);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDistrito(rs));
                }
            }
            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando distritos por departamento: " + e.getMessage(), e);
        }
    }
}
