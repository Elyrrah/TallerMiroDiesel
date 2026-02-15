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
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Pais;
import com.tallermirodiesel.service.PaisService;
import com.tallermirodiesel.service.impl.PaisServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "PaisServlet", urlPatterns = {"/paises"})
public class PaisServlet extends HttpServlet {

    private PaisService paisService;

    @Override
    public void init() {
        this.paisService = new PaisServiceImpl();
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
                default -> resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());

            Pais p = new Pais();
            Long idPais = parseLong(req.getParameter("idPais"));
            p.setIdPais(idPais);
            p.setNombre(req.getParameter("nombre"));
            p.setIso2(req.getParameter("iso2"));
            p.setIso3(req.getParameter("iso3"));
            p.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("pais", p);
            req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("paises", paisService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("paises", paisService.listarTodos());
        }

        req.getRequestDispatcher("/WEB-INF/views/paises/pais_listar.jsp").forward(req, resp);
    }

    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        List<Pais> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = paisService.listarTodos();
        } else {
            lista = paisService.buscarPorNombreParcial(filtro);
        }

        req.setAttribute("paises", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_listar.jsp").forward(req, resp);
    }

    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pais", new Pais());
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
    }

    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Pais> pais = paisService.buscarPorId(id);

        if (pais.isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + id);
        }

        req.setAttribute("pais", pais.get());
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
    }

    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        paisService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
    }

    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        paisService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Pais p = new Pais();

        Long idPais = parseLong(req.getParameter("idPais"));
        p.setIdPais(idPais);
        p.setNombre(req.getParameter("nombre"));
        p.setIso2(req.getParameter("iso2"));
        p.setIso3(req.getParameter("iso3"));

        if (idPais == null) {
            p.setActivo(true);
            paisService.crear(p);
        } else {
            p.setActivo("true".equals(req.getParameter("activo")));
            paisService.actualizar(p);
        }

        resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
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
}