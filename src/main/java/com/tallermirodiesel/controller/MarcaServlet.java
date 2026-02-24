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
import com.tallermirodiesel.model.Marca;
import com.tallermirodiesel.service.MarcaService;
import com.tallermirodiesel.service.impl.MarcaServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "MarcaServlet", urlPatterns = {"/marcas"})
public class MarcaServlet extends HttpServlet {

    private MarcaService marcaService;

    // Inicialización del servicio de Marcas al cargar el servlet
    @Override
    public void init() {
        this.marcaService = new MarcaServiceImpl();
    }

    // Gestión de peticiones de lectura, búsqueda y navegación de formularios de marcas vía GET
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

    // Gestión de procesamiento de datos para la persistencia de marcas vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());

            Marca marca = new Marca();
            Long idMarca = parseLong(req.getParameter("idMarca"));
            marca.setIdMarca(idMarca);
            marca.setNombre(req.getParameter("nombre"));
            marca.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("marca", marca);
            req.getRequestDispatcher("/WEB-INF/views/catalogos/marcas/marca_form.jsp").forward(req, resp);
        }
    }

    // Lógica para recuperar la lista de marcas con soporte para filtrado por nombre parcial
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("marcas", marcaService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("marcas", marcaService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/catalogos/marcas/marca_listar.jsp").forward(req, resp);
    }

    // Preparación del objeto y despacho del formulario para la creación de una nueva marca
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("marca", new Marca());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/marcas/marca_form.jsp").forward(req, resp);
    }

    // Recuperación de la marca existente y despacho del formulario para su edición
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Marca> marca = marcaService.buscarPorId(id);

        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + id);
        }

        req.setAttribute("marca", marca.get());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/marcas/marca_form.jsp").forward(req, resp);
    }

    // Procesamiento de habilitación de marca y redirección al listado principal
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        marcaService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
    }

    // Procesamiento de inhabilitación de marca y redirección al listado principal
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        marcaService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
    }

    // Lógica para recolectar datos, persistir cambios (crear/actualizar) y redireccionar
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Marca marca = new Marca();

        Long idMarca = parseLong(req.getParameter("idMarca"));
        marca.setIdMarca(idMarca);
        marca.setNombre(req.getParameter("nombre"));

        if (idMarca == null) {
            marca.setActivo(true);
            marcaService.crear(marca);
        } else {
            marca.setActivo("true".equals(req.getParameter("activo")));
            marcaService.actualizar(marca);
        }

        resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
    }

    // Utilidad interna para la conversión segura de cadenas de texto a identificadores numéricos
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
}