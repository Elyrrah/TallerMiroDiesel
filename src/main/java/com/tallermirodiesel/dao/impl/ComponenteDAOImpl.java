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
import com.tallermirodiesel.dao.ComponenteDAO;
import com.tallermirodiesel.model.Componente;
import com.tallermirodiesel.util.DatabaseConnection;

/**
 * @author elyrr
 */
public class ComponenteDAOImpl implements ComponenteDAO {

    // Consultas SQL base y constantes
    private static final String SELECT_BASE = """
            SELECT c.id_componente,
                   c.id_tipo_componente,
                   c.id_marca,
                   c.id_modelo,
                   c.numero_serie,
                   c.observaciones,
                   c.activo,
                   tc.nombre AS nombre_tipo_componente,
                   ma.nombre AS nombre_marca,
                   mo.nombre AS nombre_modelo
            FROM public.componentes c
            JOIN public.tipos_componente tc ON tc.id_tipo_componente = c.id_tipo_componente
            JOIN public.marcas ma           ON ma.id_marca           = c.id_marca
            JOIN public.modelos mo          ON mo.id_modelo          = c.id_modelo
            """;

    private static final String SQL_INSERT = """
            INSERT INTO public.componentes (id_tipo_componente, id_marca, id_modelo, numero_serie, observaciones, activo)
            VALUES (?, ?, ?, ?, ?, ?) RETURNING id_componente""";

    private static final String SQL_UPDATE = """
            UPDATE public.componentes
            SET id_tipo_componente = ?, id_marca = ?, id_modelo = ?, numero_serie = ?, observaciones = ?, activo = ? 
            WHERE id_componente = ?""";

    private static final String SQL_DELETE = "DELETE FROM public.componentes WHERE id_componente = ?";
    
    private static final String SQL_ACTIVAR = "UPDATE public.componentes SET activo = true WHERE id_componente = ?";
    
    private static final String SQL_DESACTIVAR = "UPDATE public.componentes SET activo = false WHERE id_componente = ?";

    // Método para crear un componente y retornar su ID
    @Override
    public Long crear(Componente c) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, c.getIdTipoComponente());
            ps.setLong(2, c.getIdMarca());
            ps.setLong(3, c.getIdModelo());
            ps.setString(4, c.getNumeroSerie());
            ps.setString(5, c.getObservaciones());
            ps.setBoolean(6, c.isActivo());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id_componente");
                }
                throw new RuntimeException("No se generó id_componente");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al crear componente: " + e.getMessage(), e);
        }
    }

    // Método para actualizar todos los campos de un componente
    @Override
    public boolean actualizar(Componente c) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setLong(1, c.getIdTipoComponente());
            ps.setLong(2, c.getIdMarca());
            ps.setLong(3, c.getIdModelo());
            ps.setString(4, c.getNumeroSerie());
            ps.setString(5, c.getObservaciones());
            ps.setBoolean(6, c.isActivo());
            ps.setLong(7, c.getIdComponente());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al actualizar componente: " + e.getMessage(), e);
        }
    }

    // Método para eliminar el registro físicamente
    @Override
    public boolean eliminar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al eliminar componente: " + e.getMessage(), e);
        }
    }

    // Método para activar un componente
    @Override
    public boolean activar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTIVAR)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al activar componente: " + e.getMessage(), e);
        }
    }

    // Método para desactivar un componente
    @Override
    public boolean desactivar(Long id) {
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_DESACTIVAR)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en BD al desactivar componente: " + e.getMessage(), e);
        }
    }

    // Búsqueda por ID único
    @Override
    public Optional<Componente> buscarPorId(Long id) {
        String sql = SELECT_BASE + " WHERE c.id_componente = ?";
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por ID: " + e.getMessage(), e);
        }
    }

    // Búsqueda por nombre (redirecciona a número de serie)
    @Override
    public Optional<Componente> buscarPorNombre(String numeroSerie) {
        return buscarPorNumeroSerie(numeroSerie);
    }

    // Búsqueda por número de serie exacto (ignora mayúsculas y espacios)
    @Override
    public Optional<Componente> buscarPorNumeroSerie(String numeroSerie) {
        String sql = SELECT_BASE + " WHERE UPPER(TRIM(c.numero_serie)) = UPPER(TRIM(?))";
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numeroSerie);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar por serie: " + e.getMessage(), e);
        }
    }

    // Búsqueda parcial por número de serie
    @Override
    public List<Componente> buscarPorNombreParcial(String filtro) {
        String sql = SELECT_BASE + " WHERE c.numero_serie ILIKE ? ORDER BY tc.nombre ASC, ma.nombre ASC";
        return listarConParametroString(sql, "%" + (filtro == null ? "" : filtro.trim()) + "%");
    }

    // Listado de todos los componentes
    @Override
    public List<Componente> listarTodos() {
        String sql = SELECT_BASE + " ORDER BY tc.nombre ASC, ma.nombre ASC, mo.nombre ASC";
        return listarGenerico(sql);
    }

    // Listado de componentes activos
    @Override
    public List<Componente> listarActivos() {
        String sql = SELECT_BASE + " WHERE c.activo = true ORDER BY tc.nombre ASC, ma.nombre ASC";
        return listarGenerico(sql);
    }

    // Listado de componentes inactivos
    @Override
    public List<Componente> listarInactivos() {
        String sql = SELECT_BASE + " WHERE c.activo = false ORDER BY tc.nombre ASC, ma.nombre ASC";
        return listarGenerico(sql);
    }

    // Listado filtrado por el tipo de componente
    @Override
    public List<Componente> listarPorTipoComponente(Long idTipoComponente) {
        String sql = SELECT_BASE + " WHERE c.id_tipo_componente = ? ORDER BY ma.nombre ASC";
        return listarConParametroLong(sql, idTipoComponente);
    }

    // Listado filtrado por marca
    @Override
    public List<Componente> listarPorMarca(Long idMarca) {
        String sql = SELECT_BASE + " WHERE c.id_marca = ? ORDER BY tc.nombre ASC, mo.nombre ASC";
        return listarConParametroLong(sql, idMarca);
    }

    // Listado filtrado por modelo
    @Override
    public List<Componente> listarPorModelo(Long idModelo) {
        String sql = SELECT_BASE + " WHERE c.id_modelo = ? ORDER BY tc.nombre ASC";
        return listarConParametroLong(sql, idModelo);
    }

    // Método privado para mapear ResultSet a Objeto
    private Componente mapear(ResultSet rs) throws SQLException {
        Componente c = new Componente();
        c.setIdComponente(rs.getLong("id_componente"));
        c.setIdTipoComponente(rs.getLong("id_tipo_componente"));
        c.setIdMarca(rs.getLong("id_marca"));
        c.setIdModelo(rs.getLong("id_modelo"));
        c.setNumeroSerie(rs.getString("numero_serie"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setActivo(rs.getBoolean("activo"));
        c.setNombreTipoComponente(rs.getString("nombre_tipo_componente"));
        c.setNombreMarca(rs.getString("nombre_marca"));
        c.setNombreModelo(rs.getString("nombre_modelo"));
        return c;
    }

    // Auxiliar para listas sin parámetros
    private List<Componente> listarGenerico(String sql) {
        List<Componente> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en listado genérico: " + e.getMessage(), e);
        }
    }

    // Auxiliar para listas con un parámetro Long (ID)
    private List<Componente> listarConParametroLong(String sql, Long id) {
        List<Componente> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en listado por ID: " + e.getMessage(), e);
        }
    }

    // Auxiliar para listas con un parámetro String (LIKE/Filtro)
    private List<Componente> listarConParametroString(String sql, String valor) {
        List<Componente> lista = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en listado por filtro: " + e.getMessage(), e);
        }
    }
}