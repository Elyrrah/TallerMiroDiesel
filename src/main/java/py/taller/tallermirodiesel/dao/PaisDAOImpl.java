/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Pais;
import py.taller.tallermirodiesel.util.DatabaseConnection;

/**
 *
 * @author elyrr
 */

public class PaisDAOImpl implements PaisDAO {

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
                Pais p = new Pais();
                p.setIdPais(rs.getLong("id_pais"));
                p.setNombre(rs.getString("nombre"));
                p.setIso2(rs.getString("iso2"));
                p.setIso3(rs.getString("iso3"));
                p.setActivo(rs.getBoolean("activo"));
                lista.add(p);
            }

            return lista;

        } catch (Exception e) {
            throw new RuntimeException("Error listando paises: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pais> buscarPorId(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long crear(Pais pais) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean actualizar(Pais pais) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean desactivar(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
