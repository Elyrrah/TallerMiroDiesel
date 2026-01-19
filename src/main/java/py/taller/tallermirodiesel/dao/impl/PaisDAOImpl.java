/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.PaisDAO;
import py.taller.tallermirodiesel.model.Pais;
import py.taller.tallermirodiesel.util.DatabaseConnection;
/**
 * @author elyrr
 */
public class PaisDAOImpl implements PaisDAO {
    //  Mapear Pais
    private Pais mapearPais(ResultSet rs) throws Exception {
        Pais p = new Pais();
        p.setIdPais(rs.getLong("id_pais"));
        p.setNombre(rs.getString("nombre"));
        p.setIso2(rs.getString("iso2"));
        p.setIso3(rs.getString("iso3"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }
    //  Crea un nuevo Pais
    @Override
    public Long crear(Pais pais) {
        String sql = """
                INSERT INTO public.paises (nombre, iso2, iso3, activo)
                VALUES (?, ?, ?, ?)
                RETURNING id_pais
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pais.getNombre());
            ps.setString(2, pais.getIso2() == null ? null : pais.getIso2().trim().toUpperCase());
            ps.setString(3, pais.getIso3() == null ? null : pais.getIso3().trim().toUpperCase());
            ps.setBoolean(4, pais.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_pais");
                }
                throw new RuntimeException("No se generó id_pais al crear el país.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creando pais: " + e.getMessage(), e);
        }
    }
    
    //  Actualiza un Pais
    @Override
    public boolean actualizar(Pais pais) {
        String sql = """
                UPDATE public.paises
                SET nombre = ?,
                    iso2 = ?,
                    iso3 = ?,
                    activo = ?
                WHERE id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pais.getNombre());
            ps.setString(2, pais.getIso2() == null ? null : pais.getIso2().trim().toUpperCase());
            ps.setString(3, pais.getIso3() == null ? null : pais.getIso3().trim().toUpperCase());
            ps.setBoolean(4, pais.isActivo());
            ps.setLong(5, pais.getIdPais());

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando pais: " + e.getMessage(), e);
        }
    }
    
    //  Elimina un Pais
    @Override
    public boolean eliminar(Long id) {
        String sql = "DELETE FROM public.paises WHERE id_pais = ?";

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error eliminando pais: " + e.getMessage(), e);
        }
    }    
    
    //  Activa un Pais
    @Override
    public boolean activar(Long id) {
        String sql = """
            UPDATE public.paises
            SET activo = true
            WHERE id_pais = ?
        """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error activando país: " + e.getMessage(), e);
        }
    }
    
    //  Desactiva un Pais
    @Override
    public boolean desactivar(Long id) {
        String sql = """
                UPDATE public.paises
                SET activo = false
                WHERE id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("Error desactivando pais: " + e.getMessage(), e);
        }
    }    
    
    //  Busca un Pais por su id
    @Override
    public Optional<Pais> buscarPorId(Long id) {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE id_pais = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPais(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando pais por id: " + e.getMessage(), e);
        }
    }

    //  Busca un Pais por su Iso2
    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE iso2 = ?
                """;

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String iso2Norm = (iso2 == null) ? null : iso2.trim().toUpperCase();
            ps.setString(1, iso2Norm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPais(rs));
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error buscando pais por iso2: " + e.getMessage(), e);
        }
    }
    
    //  Lista todos los Paises
    @Override
    public List<Pais> listarTodos() {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                ORDER BY id_pais
                """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando paises: " + e.getMessage(), e);
        }
    }
    
    //  Lista todos los Paises Activos
    @Override
    public List<Pais> listarActivos() {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE activo = true
                ORDER BY id_pais
                """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando paises activos: " + e.getMessage(), e);
        }
    }
    
    //  Lista todos los Paises Inactivos
    @Override
    public List<Pais> listarInactivos() {
        String sql = """
                SELECT id_pais, nombre, iso2, iso3, activo
                FROM public.paises
                WHERE activo = false
                ORDER BY id_pais
                """;

        List<Pais> lista = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPais(rs));
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando paises inactivos: " + e.getMessage(), e);
        }
    }
}