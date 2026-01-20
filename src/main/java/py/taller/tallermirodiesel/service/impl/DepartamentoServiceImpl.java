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
    
    // DAO utilizado por el Service para acceder a la capa de persistencia.
    // El Service delega en el DAO todas las operaciones de acceso a datos.
    private final DepartamentoDAO departamentoDAO;

    // Constructor del Service.
    // Inicializa la implementación concreta del DAO que se usará para ejecutar las operaciones contra la base de datos.
    public DepartamentoServiceImpl() {
        this.departamentoDAO = new DepartamentoDAOImpl();
    }

    
    //  Validaciones para Crear un Departamento.
    @Override
    public Long crear(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser null.");
        }

        if (departamento.getIdPais() == null) {
            throw new IllegalArgumentException("El país (idPais) es obligatorio.");
        }
        if (departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser mayor a 0.");
        }

        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }

        departamento.setNombre(nombre);

        return departamentoDAO.crear(departamento);
    }
    
    //  Validaciones para Actualizar un Departamento.
    @Override
    public boolean actualizar(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser null.");
        }
        if (departamento.getIdDepartamento() == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio para actualizar.");
        }

        if (departamento.getIdPais() == null) {
            throw new IllegalArgumentException("El país (idPais) es obligatorio.");
        }
        if (departamento.getIdPais() <= 0) {
            throw new IllegalArgumentException("El país (idPais) debe ser mayor a 0.");
        }

        String nombre = (departamento.getNombre() == null) ? null : departamento.getNombre().trim().toUpperCase();
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del departamento es obligatorio.");
        }
        departamento.setNombre(nombre);

        Optional<Departamento> existente = departamentoDAO.buscarPorId(departamento.getIdDepartamento());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + departamento.getIdDepartamento());
        }

        return departamentoDAO.actualizar(departamento);
    }

    //  Validaciones para Activar un Departamento.
    @Override
    public boolean activar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio para activar.");
        }

        Departamento departamento = departamentoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un departamento con id: " + id));

        if (departamento.isActivo()) {
            throw new IllegalStateException("El departamento ya se encuentra activo.");
        }

        return departamentoDAO.activar(id);
    }

    //  Validaciones para Desactivar un Departamento.
    @Override
    public boolean desactivar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio para desactivar.");
        }

        Departamento departamento = departamentoDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un departamento con id: " + id));

        if (!departamento.isActivo()) {
            throw new IllegalStateException("El departamento ya se encuentra inactivo.");
        }

        return departamentoDAO.desactivar(id);
    }


    @Override
    public Optional<Departamento> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del departamento es obligatorio.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El id del departamento debe ser mayor a 0.");
        }
        return departamentoDAO.buscarPorId(id);
    }

    
    @Override
    public List<Departamento> listarTodos() {
        return departamentoDAO.listarTodos();
    }

    @Override
    public List<Departamento> listarActivos() {
        return departamentoDAO.listarActivos();
    }

    @Override
    public List<Departamento> listarInactivos() {
        return departamentoDAO.listarInactivos();
    }
        
    @Override
    public List<Departamento> listarPorPais(Long idPais) {
        if (idPais == null) {
            throw new IllegalArgumentException("El idPais es obligatorio.");
        }
        if (idPais <= 0) {
            throw new IllegalArgumentException("El idPais debe ser mayor a 0.");
        }
        return departamentoDAO.listarPorPais(idPais);
    }
}
