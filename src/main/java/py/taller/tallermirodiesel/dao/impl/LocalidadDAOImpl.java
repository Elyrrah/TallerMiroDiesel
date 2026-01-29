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
import py.taller.tallermirodiesel.model.Localidad;
import py.taller.tallermirodiesel.util.DatabaseConnection;
import py.taller.tallermirodiesel.dao.LocalidadDAO;

/**
 * @author elyrr
 */
public class LocalidadDAOImpl implements LocalidadDAO {

    //  Mapear Localidad
    private Localidad mapearLocalidad(ResultSet rs) throws SQLException {
        Localidad l = new Localidad();
        l.setIdLocalidad(rs.getLong("id_localidad")); // FIX: antes decía id_distrito
        l.setIdDistrito(rs.getLong("id_distrito"));   // FIX: antes decía id_ciudad
        l.setNombre(rs.getString("nombre"));
        l.setActivo(rs.getBoolean("activo"));

        // Nota: nombre_distrito solo existe cuando el SELECT hace JOIN con distritos
        try {
            l.setNombreDistrito(rs.getString("nombre_distrito")); // FIX: antes decía nombre_ciudad
        } catch (SQLException ignore) {
            l.setNombreDistrito(null);
        }

        return l;
    }

    //  Crea una nueva Localidad
    @Override
    public Long crear(Localidad localidad) {
        String sql = """
                INSERT INTO public.localidades (id_distrito, nombre, activo)
                VALUES (?, ?, ?)
                RETURNING id_localidad
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, localidad.getIdDistrito());
            ps.setString(2, localidad.getNombre());
            ps.setBoolean(3, localidad.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_localidad");
                }
                throw new RuntimeException("No se generó id_localidad al crear la localidad.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando localidad: " + e.getMessage(), e);
        }
    }

    //  Actualiza una Localidad
    @Override
    public boolean actualizar(Localidad localidad) {
        String sql = """
                UPDATE public.localidades
                SET id_distrito = ?,
                    nombre = ?,
                    activo = ?
                WHERE id_localidad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, localidad.getIdDistrito());
            ps.setString(2, localidad.getNombre());
            ps.setBoolean(3, localidad.isActivo());
            ps.setLong(4, localidad.getIdLocalidad());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando localidad: " + e.getMessage(), e);
        }
    }

    //  Elimina una Localidad
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.localidades WHERE id_localidad = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando localidad: " + e.getMessage(), e);
        }
    }

    //  Activa una Localidad
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.localidades
            SET activo = true
            WHERE id_localidad = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando localidad: " + e.getMessage(), e);
        }
    }

    //  Desactiva una Localidad
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.localidades
                SET activo = false
                WHERE id_localidad = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando localidad: " + e.getMessage(), e);
        }
    }

    //  Busca una Localidad por su id
    @Override
    public Optional<Localidad> buscarPorId(Long id) {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            WHERE l.id_localidad = ?
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLocalidad(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando localidad por id: " + e.getMessage(), e);
        }
    }

    //  Busca una Localidad por su nombre
    @Override
    public Optional<Localidad> buscarPorNombre(String nombre) {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            WHERE UPPER(TRIM(l.nombre)) = UPPER(TRIM(?))
            """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String nombreNorm = (nombre == null) ? null : nombre.trim();
            ps.setString(1, nombreNorm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLocalidad(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando localidad por nombre: " + e.getMessage(), e);
        }
    }

    //  Busca Localidades cuyo nombre coincida parcialmente
    @Override
    public List<Localidad> buscarPorNombreParcial(String filtro) {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            WHERE l.nombre ILIKE ?
            ORDER BY di.nombre ASC, l.nombre ASC
            """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String filtroNorm = (filtro == null) ? "" : filtro.trim();
            ps.setString(1, "%" + filtroNorm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLocalidad(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error buscando localidad por nombre parcial: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Localidades
    @Override
    public List<Localidad> listarTodos() {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            ORDER BY di.nombre ASC, l.nombre ASC
            """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando localidades: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Localidades Activas
    @Override
    public List<Localidad> listarActivos() {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            WHERE l.activo = true
            ORDER BY di.nombre ASC, l.nombre ASC
            """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando localidades activas: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Localidades Inactivas
    @Override
    public List<Localidad> listarInactivos() {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            WHERE l.activo = false
            ORDER BY di.nombre ASC, l.nombre ASC
            """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearLocalidad(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando localidades inactivas: " + e.getMessage(), e);
        }
    }

    //  Lista todas las Localidades de un Distrito
    @Override
    public List<Localidad> listarPorDistrito(Long idDistrito) {
        String sql = """
            SELECT  l.id_localidad,
                    l.id_distrito,
                    l.nombre,
                    l.activo,
                    di.nombre AS nombre_distrito
            FROM public.localidades l
            JOIN public.distritos di ON di.id_distrito = l.id_distrito
            WHERE l.id_distrito = ?
            ORDER BY l.nombre
            """;

        List<Localidad> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idDistrito);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLocalidad(rs));
                }
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando localidades por distrito: " + e.getMessage(), e);
        }
    }
}
