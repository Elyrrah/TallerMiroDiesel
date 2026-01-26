/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.service.impl;

import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.dao.DepartamentoDAO;
import py.taller.tallermirodiesel.dao.impl.DepartamentoDAOImpl;
import py.taller.tallermirodiesel.model.Departamento;
import py.taller.tallermirodiesel.service.DepartamentoService;

/**
 * @author elyrr
 */
public class DepartamentoServiceImpl implements DepartamentoService {
    
    // Esta variable permite que el servicio hable con la base de datos a través del DAO.
    private final DepartamentoDAO departamentoDAO;

    // Al iniciar el servicio, le asignamos su herramienta de base de datos específica.
    public DepartamentoServiceImpl() {
        this.departamentoDAO = new DepartamentoDAOImpl();
    }
    
    //  VALIDACIONES PARA CREAR UN DEPARTAMENTO
    @Override
    public Long crear(Departamento departamento) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (departamento == null) {
            throw new IllegalArgumentException("El id_departamento no puede estar vacío.");
        }

        if (departamento.getIdPais() == null) {
            throw new IllegalArgumentException("El país (idPais) es obligatorio.");
        }
        
        if (departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser mayor a 0.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();

        // 3. Aseguramos que se haya cargado el nombre del departamento.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
        
        // 4. Cargamos el objeto con los datos.
        departamento.setNombre(nombre);

        // 5. Le pedimos a la base de datos que guarde el departamento.
        return departamentoDAO.crear(departamento);
    }
    
    //  VALIDACIONES PARA ACTUALIZAR UN DEPARTAMENTO.
    @Override
    public boolean actualizar(Departamento departamento) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser null.");
        }
        
        if (departamento.getIdDepartamento() == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio para actualizar.");
        }
        
        if (departamento.getIdDepartamento() <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }

        if (departamento.getIdPais() == null) {
            throw new IllegalArgumentException("El país (idPais) es obligatorio.");
        }
        
        if (departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser mayor a 0.");
        }

        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();
        
        // 3. Aseguramos que se haya cargado el nombre del departamento.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
        
        // 4. Actualizamos el objeto con los datos.
        departamento.setNombre(nombre);

        // 5. Verificar que el departamento a actualizar exista en el sistema.
        Optional<Departamento> existente = departamentoDAO.buscarPorId(departamento.getIdDepartamento());
        if (existente.isEmpty()) {throw new IllegalArgumentException("No existe un departamento con id: " + departamento.getIdDepartamento());
        }

        // 7. Le pedimos a la base de datos que actualice el departamento.
        return departamentoDAO.actualizar(departamento);
    }

    //  VALIDACIONES PARA ACTIVAR UN DEPARTAMENTO.
    @Override
    public boolean activar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio para activar.");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }
        
        // 2. Verificamos que el departamento exista.
        Departamento departamento = departamentoDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe un departamento con id: " + id));

        // 3. Si ya está activo, no hacemos nada.
        if (departamento.isActivo()) {
            throw new IllegalStateException("El departamento ya se encuentra activo.");
        }

        // 4. Activa el departamento.
        return departamentoDAO.activar(id);
    }

    
    //  VALIDACIONES PARA DESACTIVAR UN DEPARTAMENTO.
    @Override
    public boolean desactivar(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio para desactivar.");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }

        // 2. Verificamos que el departamento exista.
        Departamento departamento = departamentoDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("No existe un departamento con id: " + id));

        // 3. Si ya está Inactivo, no hacemos nada.
        if (!departamento.isActivo()) {
            throw new IllegalStateException("El departamento ya se encuentra inactivo.");
        }

        // 4. Activa el departamento.
        return departamentoDAO.desactivar(id);
    }

    
    // BUSCA UN DEPARTAMENTO POR SU ID.
    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (id == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio.");
        }
        
        if (id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }
        
        // 2. Devuelve el departamento buscado.
        return departamentoDAO.buscarPorId(id);
    }

    
    // BUSCA UN DEPARTAMENTO POR SU NOMBRE.
    @Override
    public Optional<Departamento> buscarPorNombre(String nombre) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
        
        // 2. Quitamos espacios vacíos y pasamos todo a MAYÚSCULAS.
        String nombreNorm = nombre.trim().toUpperCase();
        
        // 3. Devuelve el departamento buscado.
        return departamentoDAO.buscarPorNombre(nombreNorm);
    }

    
    // BUSCA DEPARTAMENTOS POR NOMBRE PARCIAL.
    @Override
    public List<Departamento> buscarPorNombreParcial(String filtro) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (filtro == null) {
            throw new IllegalArgumentException("El filtro no puede ser null.");
        }
        
        // 2. Quitamos espacios vacíos.
        String filtroNorm = filtro.trim();
        
        // 3. Devuelve la lista filtrada.
        return departamentoDAO.buscarPorNombreParcial(filtroNorm);
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DEPARTAMENTOS.
    @Override
    public List<Departamento> listarTodos() {
        return departamentoDAO.listarTodos();
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DEPARTAMENTOS ACTIVOS.
    @Override
    public List<Departamento> listarActivos() {
        return departamentoDAO.listarActivos();
    }

    
    // VALIDACIONES PARA LISTAR TODOS LOS DEPARTAMENTOS INACTIVOS.
    @Override
    public List<Departamento> listarInactivos() {
        return departamentoDAO.listarInactivos();
    }
        
    
    // VALIDACIONES PARA LISTAR TODOS LOS DEPARTAMENTOS DE UN PAIS.
    @Override
    public List<Departamento> listarPorPais(Long idPais) {
        
        // 1. Verificamos que los campos estén completos correctamente.
        if (idPais == null) {
            throw new IllegalArgumentException("El idPais es obligatorio.");
        }
        
        if (idPais <= 0) {
            throw new IllegalArgumentException("El idPais debe ser mayor a 0.");
        }
        
        // 2. Devuelve la lista de departamentos por país.
        return departamentoDAO.listarPorPais(idPais);
    }
}
