/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.dao.impl.ModeloDAOImpl;
import com.tallermirodiesel.model.Vehiculo;
import com.tallermirodiesel.model.enums.TipoVehiculoEnum;
import com.tallermirodiesel.service.VehiculoService;
import com.tallermirodiesel.service.impl.VehiculoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "VehiculoServlet", urlPatterns = {"/vehiculos"})
public class VehiculoServlet extends HttpServlet {

    private VehiculoService vehiculoService;
    private MarcaDAO marcaDAO;
    private ModeloDAO modeloDAO;

    // Inicialización de servicios y acceso a datos para marcas y modelos
    @Override
    public void init() {
        this.vehiculoService = new VehiculoServiceImpl();
        this.marcaDAO        = new MarcaDAOImpl();
        this.modeloDAO       = new ModeloDAOImpl();
    }

    // Procesamiento de navegación y consultas GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            switch (action) {
                case "nuevo"      -> mostrarFormularioNuevo(req, resp);
                case "editar"     -> mostrarFormularioEditar(req, resp);
                case "activar"    -> activar(req, resp);
                case "desactivar" -> desactivar(req, resp);
                case "buscar"     -> listar(req, resp);
                case "listar"     -> listar(req, resp);
                default           -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    // Procesamiento de envío de formularios vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/vehiculos?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(req, resp);
        }
    }

    // Recuperación de la lista de vehículos registrados
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("vehiculos", vehiculoService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("vehiculos", vehiculoService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/vehiculos/vehiculo_listar.jsp").forward(req, resp);
    }

    // Despacho del formulario de creación con carga de catálogos y enums
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cargarDatosFormulario(req);
        req.setAttribute("vehiculo", new Vehiculo());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/vehiculos/vehiculo_form.jsp").forward(req, resp);
    }

    // Carga de vehículo existente para edición y preparación de selectores
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Vehiculo> v = vehiculoService.buscarPorId(id);

        if (v.isEmpty()) {
            throw new IllegalArgumentException("No existe un vehículo con id: " + id);
        }

        cargarDatosFormulario(req);
        req.setAttribute("vehiculo", v.get());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/vehiculos/vehiculo_form.jsp").forward(req, resp);
    }

    // Lógica para habilitar un vehículo
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) throw new IllegalArgumentException("ID inválido");
        vehiculoService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/vehiculos?action=listar");
    }

    // Lógica para inhabilitar un vehículo
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) throw new IllegalArgumentException("ID inválido");
        vehiculoService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/vehiculos?action=listar");
    }

    // Ejecución de persistencia de datos (Crear/Actualizar)
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Vehiculo v = construirDesdeRequest(req);

        if (v.getIdVehiculo() == null) {
            vehiculoService.crear(v);
        } else {
            vehiculoService.actualizar(v);
        }

        resp.sendRedirect(req.getContextPath() + "/vehiculos?action=listar");
    }

    // Mapeo de parámetros del Request hacia el objeto Vehiculo
    private Vehiculo construirDesdeRequest(HttpServletRequest req) {
        Vehiculo v = new Vehiculo();
        v.setIdVehiculo(parseLong(req.getParameter("idVehiculo")));
        v.setPlaca(req.getParameter("placa"));
        v.setIdMarca(parseLong(req.getParameter("idMarca")));
        v.setIdModelo(parseLong(req.getParameter("idModelo")));
        v.setAnio(parseShort(req.getParameter("anio")));

        String tipoParam = req.getParameter("tipoVehiculo");
        if (tipoParam != null && !tipoParam.isBlank()) {
            try {
                v.setTipoVehiculo(TipoVehiculoEnum.valueOf(tipoParam));
            } catch (IllegalArgumentException e) {
                v.setTipoVehiculo(null);
            }
        }

        v.setObservaciones(req.getParameter("observaciones"));

        if (v.getIdVehiculo() != null) {
            v.setActivo("true".equals(req.getParameter("activo")));
        }

        return v;
    }

    // Reenvío al formulario con persistencia de datos ante errores
    private void reenviarFormularioConDatos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cargarDatosFormulario(req);
        req.setAttribute("vehiculo", construirDesdeRequest(req));
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/vehiculos/vehiculo_form.jsp").forward(req, resp);
    }

    // Inyección de catálogos y valores de enumeración para la interfaz
    private void cargarDatosFormulario(HttpServletRequest req) {
        req.setAttribute("marcas", marcaDAO.listarActivos());
        req.setAttribute("modelos", modeloDAO.listarActivos());
        req.setAttribute("tiposVehiculo", TipoVehiculoEnum.values());
    }

    // Utilidad de parseo para identificadores
    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Utilidad de parseo para el campo de año
    private Short parseShort(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}