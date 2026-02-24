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
import java.math.BigDecimal;
import java.util.Optional;
import com.tallermirodiesel.model.Servicio;
import com.tallermirodiesel.service.ServicioService;
import com.tallermirodiesel.service.impl.ServicioServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "ServicioServlet", urlPatterns = {"/servicios"})
public class ServicioServlet extends HttpServlet {

    private ServicioService servicioService;

    // Inicialización del servicio de gestión de trabajos y mantenimiento
    @Override
    public void init() {
        this.servicioService = new ServicioServiceImpl();
    }

    // Gestión de peticiones de lectura, búsqueda y navegación de formularios vía GET
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

    // Gestión de procesamiento de datos para la persistencia de servicios vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(req, resp);
        }
    }

    // Lógica para recuperar la lista de servicios con soporte para filtrado por nombre
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("servicios", servicioService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("servicios", servicioService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/catalogos/servicios/servicio_listar.jsp").forward(req, resp);
    }

    // Preparación del objeto vacío y despacho del formulario para nuevo registro
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("servicio", new Servicio());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/servicios/servicio_form.jsp").forward(req, resp);
    }

    // Recuperación de la entidad por ID y despacho del formulario para edición
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Servicio> servicio = servicioService.buscarPorId(id);

        if (servicio.isEmpty()) {
            throw new IllegalArgumentException("No existe un servicio con id: " + id);
        }

        req.setAttribute("servicio", servicio.get());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/servicios/servicio_form.jsp").forward(req, resp);
    }

    // Procesamiento de habilitación de servicio y redirección al listado
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        servicioService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    // Procesamiento de inhabilitación de servicio y redirección al listado
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        servicioService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    // Lógica para recolectar datos, persistir cambios y redireccionar
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Servicio servicio = construirDesdeRequest(req);

        if (servicio.getIdServicio() == null) {
            servicio.setActivo(true);
            servicioService.crear(servicio);
        } else {
            servicioService.actualizar(servicio);
        }

        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    // Utilidad para el mapeo de parámetros HTTP incluyendo el tratamiento de BigDecimal para precios
    private Servicio construirDesdeRequest(HttpServletRequest req) {
        Servicio servicio = new Servicio();

        Long idServicio = parseLong(req.getParameter("idServicio"));
        servicio.setIdServicio(idServicio);
        servicio.setCodigo(req.getParameter("codigo"));
        servicio.setNombre(req.getParameter("nombre"));
        servicio.setDescripcion(req.getParameter("descripcion"));
        servicio.setPrecioBase(parseBigDecimal(req.getParameter("precioBase")));

        if (idServicio != null) {
            servicio.setActivo("true".equals(req.getParameter("activo")));
        }

        return servicio;
    }

    // Lógica de recuperación ante errores para prevenir la pérdida de información en el formulario
    private void reenviarFormularioConDatos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("servicio", construirDesdeRequest(req));
        req.getRequestDispatcher("/WEB-INF/views/catalogos/servicios/servicio_form.jsp").forward(req, resp);
    }

    // Utilidad interna para la conversión segura de cadenas a identificadores numéricos
    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Utilidad interna para la conversión segura de cadenas a valores decimales financieros
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}