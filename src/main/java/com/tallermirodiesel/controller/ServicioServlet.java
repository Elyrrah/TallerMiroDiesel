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
import java.util.List;
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

    @Override
    public void init() {
        this.servicioService = new ServicioServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            switch (action) {
                case "nuevo" -> mostrarFormularioNuevo(req, resp);
                case "editar" -> mostrarFormularioEditar(req, resp);
                case "activar" -> activar(req, resp);
                case "desactivar" -> desactivar(req, resp);
                case "buscar" -> buscar(req, resp);
                case "listar" -> listar(req, resp);
                default -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

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

            Servicio servicio = new Servicio();
            Long idServicio = parseLong(req.getParameter("idServicio"));
            servicio.setIdServicio(idServicio);
            servicio.setCodigo(req.getParameter("codigo"));
            servicio.setNombre(req.getParameter("nombre"));
            servicio.setDescripcion(req.getParameter("descripcion"));
            servicio.setPrecioBase(parseBigDecimal(req.getParameter("precioBase")));
            servicio.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("servicio", servicio);
            req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_form.jsp").forward(req, resp);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("servicios", servicioService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("servicios", servicioService.listarTodos());
        }

        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_listar.jsp").forward(req, resp);
    }

    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        List<Servicio> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = servicioService.listarTodos();
        } else {
            lista = servicioService.buscarPorNombreParcial(filtro);
        }

        req.setAttribute("servicios", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_listar.jsp").forward(req, resp);
    }

    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("servicio", new Servicio());
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_form.jsp").forward(req, resp);
    }

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
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_form.jsp").forward(req, resp);
    }

    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        servicioService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        servicioService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Servicio servicio = new Servicio();

        Long idServicio = parseLong(req.getParameter("idServicio"));
        servicio.setIdServicio(idServicio);
        servicio.setCodigo(req.getParameter("codigo"));
        servicio.setNombre(req.getParameter("nombre"));
        servicio.setDescripcion(req.getParameter("descripcion"));
        servicio.setPrecioBase(parseBigDecimal(req.getParameter("precioBase")));

        if (idServicio == null) {
            servicio.setActivo(true);
            servicioService.crear(servicio);
        } else {
            servicio.setActivo("true".equals(req.getParameter("activo")));
            servicioService.actualizar(servicio);
        }

        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

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