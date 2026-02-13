/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dao.PaisDAO;
import com.tallermirodiesel.dao.impl.PaisDAOImpl;
import com.tallermirodiesel.model.Pais;
import com.tallermirodiesel.service.PaisService;

/**
 * @author elyrr
 */
public class PaisServiceImpl implements PaisService {
    
    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final PaisDAO paisDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public PaisServiceImpl() {
        this.paisDAO = new PaisDAOImpl();
    }
    
    //  VALIDACIONES PARA CREAR UN PAÍS.
    @Override
    public Long crear(Pais pais) {

        // 1. Verificamos que el objeto pais no sea null.
        if (pais == null) {
            throw new IllegalArgumentException("El país no puede ser null.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = pais.getNombre() == null ? null : pais.getNombre().trim().toUpperCase();
        String iso2 = pais.getIso2() == null ? null : pais.getIso2().trim().toUpperCase();
        String iso3 = pais.getIso3() == null ? null : pais.getIso3().trim().toUpperCase();

        // 3. Aseguramos que los códigos tengan el largo correcto.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio.");
        }
        if (iso2 == null || iso2.isBlank()) {
            throw new IllegalArgumentException("El ISO2 del país es obligatorio.");
        }
        if (iso2.length() != 2) {
            throw new IllegalArgumentException("El ISO2 debe tener exactamente 2 caracteres.");
        }
        if (iso3 != null && !iso3.isBlank() && iso3.length() != 3) {
            throw new IllegalArgumentException("El ISO3 debe tener exactamente 3 caracteres (si se provee).");
        }

        // 4. Cargamos el objeto con los datos normalizados.
        pais.setNombre(nombre);
        pais.setIso2(iso2);
        pais.setIso3(iso3);

        // 5. No permitimos dos países con el mismo código ISO2.
        if (paisDAO.buscarPorIso2(iso2).isPresent()) {
            throw new IllegalArgumentException("Ya existe un país con el ISO2: " + iso2);
        }

        // 6. Le pedimos a la base de datos que guarde el país.
        return paisDAO.crear(pais);
    }

    
    //  VALIDACIONES PARA ACTUALIZAR UN PAÍS.
    @Override
    public boolean actualizar(Pais pais) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (pais == null || pais.getIdPais() == null) {
            throw new IllegalArgumentException("Datos incompletos para actualizar.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = pais.getNombre() == null ? null : pais.getNombre().trim().toUpperCase();
        String iso2 = pais.getIso2() == null ? null : pais.getIso2().trim().toUpperCase();
        String iso3 = pais.getIso3() == null ? null : pais.getIso3().trim().toUpperCase();

        // 3. Asegurams que los codigos tengan el largo correcto.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio.");
        }
        if (iso2 == null || iso2.isBlank()) {
            throw new IllegalArgumentException("El ISO2 del país es obligatorio.");
        }
        if (iso2.length() != 2) {
            throw new IllegalArgumentException("El ISO2 debe tener exactamente 2 caracteres.");
        }
        if (iso3 != null && !iso3.isBlank() && iso3.length() != 3) {
            throw new IllegalArgumentException("El ISO3 debe tener exactamente 3 caracteres (si se provee).");
        }

        // 4. Actualizamos el objeto con los datos.
        pais.setNombre(nombre);
        pais.setIso2(iso2);
        pais.setIso3(iso3);

        // 5. Verificar que el pais a actualizar exista en el sistema.
        Optional<Pais> existente = paisDAO.buscarPorId(pais.getIdPais());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + pais.getIdPais());
        }

        // 6. No permitimos dos países con el mismo código ISO2.
        Optional<Pais> otroConMismoIso = paisDAO.buscarPorIso2(iso2);
        if (otroConMismoIso.isPresent() && !otroConMismoIso.get().getIdPais().equals(pais.getIdPais())) {
            throw new IllegalArgumentException("Ya existe otro país con el ISO2: " + iso2);
        }

        // 7. Le pedimos a la base de datos que actualice el pais.
        return paisDAO.actualizar(pais);
    }
    

    //  VALIDACIONES PARA ACTIVAR UN PAÍS.
    @Override
    public boolean activar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del país es obligatorio para activar.");
        }
        
        // 2. Verificamos que el pais exista.
        Pais pais = paisDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe un país con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (pais.isActivo()) {
            throw new IllegalStateException("El país ya se encuentra activo.");
        }
        
        // 4. Activa el pais.
        return paisDAO.activar(id);
    }

    
    //  VALIDACIONES PARA DESACTIVAR UN PAIS.
    @Override
    public boolean desactivar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del país es obligatorio para desactivar.");
        }

        // 2. Verificamos que el pais exista.
        Pais pais = paisDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe un país con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!pais.isActivo()) {
            throw new IllegalStateException("El país ya se encuentra inactivo.");
        }
        
        // 4. Activa el pais.
        return paisDAO.desactivar(id);
    }

    
    // BUSCA UN PAIS POR SU ID.
    @Override
    public Optional<Pais> buscarPorId(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id del país no existe.");
        }
        
        // 2. Devuelve el pais buscado.
        return paisDAO.buscarPorId(id);
    }

    
    // BUSCA UN PAIS POR SU NOMBRE.
    @Override
    public Optional<Pais> buscarPorNombre(String nombre) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del país es obligatorio.");
        }
        
        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();
        
        // 3. Devuelve el pais buscado.
        return paisDAO.buscarPorNombre(nombreNorm);
    }

    
    // BUSCA PAISES POR NOMBRE PARCIAL.
    @Override
    public List<Pais> buscarPorNombreParcial(String filtro) {

        // 1. Si el filtro es null o vacío, devolvemos lista vacía
        // (evitamos ILIKE '%%' que devuelve todo)
        if (filtro == null || filtro.isBlank()) {
            return List.of();
        }

        // 2. Quitamos espacios vacíos
        String filtroNorm = filtro.trim();

        // 3. Devuelve la lista filtrada
        return paisDAO.buscarPorNombreParcial(filtroNorm);
    }

    
    //  VALIDACIONES PARA BUSCAR POR ISO2.
    @Override
    public Optional<Pais> buscarPorIso2(String iso2) {

        // 1. Validamos entrada: si es null o vacío, no consultamos a la base
        if (iso2 == null || iso2.isBlank()) {
            return Optional.empty();
        }

        // 2. Normalizamos el valor
        String iso2Norm = iso2.trim().toUpperCase();

        // 3. Devuelve el país buscado
        return paisDAO.buscarPorIso2(iso2Norm);
    }


    // VALIDACIONES PARA LISTAR TODOS LOS PAISES.
    @Override
    public List<Pais> listarTodos() {
        return paisDAO.listarTodos();
    }

    // VALIDACIONES PARA LISTAR TODOS LOS PAISES ACTIVOS.
    @Override
    public List<Pais> listarActivos() {
        return paisDAO.listarActivos();
    }

    // VALIDACIONES PARA LISTAR TODOS LOS PAISES INACTIVOS. 
    @Override
    public List<Pais> listarInactivos() {
        return paisDAO.listarInactivos();
    }
}
