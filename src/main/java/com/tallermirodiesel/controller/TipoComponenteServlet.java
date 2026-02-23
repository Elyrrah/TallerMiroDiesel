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
import com.tallermirodiesel.model.TipoComponente;
import com.tallermirodiesel.service.TipoComponenteService;
import com.tallermirodiesel.service.impl.TipoComponenteServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "TipoComponenteServlet", urlPatterns = {"/tipos-componente"})
public class TipoComponenteServlet extends HttpServlet {

    private TipoComponenteService tipoComponenteService;

    @Override
    public void init() {
        this.tipoComponenteService = new TipoComponenteServiceImpl();
    }

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/tipos-componente?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(req, resp);
        }
    }

    // LISTAR.
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("tiposComponente", tipoComponenteService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("tiposComponente", tipoComponenteService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipo_componente/tipo_componente_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoComponente", new TipoComponente());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipo_componente/tipo_componente_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<TipoComponente> tc = tipoComponenteService.buscarPorId(id);

        if (tc.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoComponente con id: " + id);
        }

        req.setAttribute("tipoComponente", tc.get());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipo_componente/tipo_componente_form.jsp").forward(req, resp);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoComponenteService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-componente?action=listar");
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoComponenteService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-componente?action=listar");
    }

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TipoComponente tc = construirDesdeRequest(req);

        if (tc.getIdTipoComponente() == null) {
            tipoComponenteService.crear(tc);
        } else {
            tipoComponenteService.actualizar(tc);
        }

        resp.sendRedirect(req.getContextPath() + "/tipos-componente?action=listar");
    }

    // CONSTRUIR TIPOCOMPONENTE DESDE REQUEST.
    private TipoComponente construirDesdeRequest(HttpServletRequest req) {
        TipoComponente tc = new TipoComponente();
        tc.setIdTipoComponente(parseLong(req.getParameter("idTipoComponente")));
        tc.setNombre(req.getParameter("nombre"));
        tc.setDescripcion(req.getParameter("descripcion"));

        if (tc.getIdTipoComponente() != null) {
            tc.setActivo("true".equals(req.getParameter("activo")));
        }

        return tc;
    }

    // REENVIAR FORMULARIO CON DATOS (EN CASO DE ERROR).
    private void reenviarFormularioConDatos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoComponente", construirDesdeRequest(req));
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipo_componente/tipo_componente_form.jsp").forward(req, resp);
    }

    // PARSEO DE LONG SEGURO.
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