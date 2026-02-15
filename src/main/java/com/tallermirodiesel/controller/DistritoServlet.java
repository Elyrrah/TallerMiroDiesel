/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.tallermirodiesel.model.Departamento;
import com.tallermirodiesel.model.Distrito;
import com.tallermirodiesel.service.DepartamentoService;
import com.tallermirodiesel.service.impl.DepartamentoServiceImpl;
import com.tallermirodiesel.service.DistritoService;
import com.tallermirodiesel.service.impl.DistritoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "DistritoServlet", urlPatterns = {"/distritos"})
public class DistritoServlet extends HttpServlet {

    private final DistritoService distritoService = new DistritoServiceImpl();
    private final DepartamentoService departamentoService = new DepartamentoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            switch (action) {
                case "nuevo" -> mostrarFormularioNuevo(request, response);
                case "editar" -> mostrarFormularioEditar(request, response);
                case "activar" -> activar(request, response);
                case "desactivar" -> desactivar(request, response);
                case "buscar" -> buscar(request, response);
                case "listar" -> listar(request, response);
                default -> listar(request, response);
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(request, response);
                default -> response.sendRedirect(request.getContextPath() + "/distritos?action=listar");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());

            Distrito distrito = construirDesdeRequest(request);
            request.setAttribute("distrito", distrito);

            cargarDepartamentos(request);
            request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        String filtro = request.getParameter("filtro");

        List<Distrito> lista;

        if (idDepartamento != null && filtro != null && !filtro.isBlank()) {
            List<Distrito> base = distritoService.listarPorDepartamento(idDepartamento);
            String filtroUpper = filtro.trim().toUpperCase();
            lista = base.stream()
                    .filter(d -> d.getNombre() != null && d.getNombre().toUpperCase().contains(filtroUpper))
                    .collect(Collectors.toList());

            request.setAttribute("idDepartamento", idDepartamento);
            request.setAttribute("filtro", filtro);

        } else if (idDepartamento != null) {
            lista = distritoService.listarPorDepartamento(idDepartamento);
            request.setAttribute("idDepartamento", idDepartamento);

        } else if (filtro != null && !filtro.isBlank()) {
            lista = distritoService.buscarPorNombreParcial(filtro);
            request.setAttribute("filtro", filtro);

        } else {
            lista = distritoService.listarTodos();
        }

        request.setAttribute("lista", lista);
        cargarDepartamentos(request);
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_listar.jsp").forward(request, response);
    }

    private void buscar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        listar(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarDepartamentos(request);
        request.setAttribute("distrito", new Distrito());
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Distrito> distritoOpt = distritoService.buscarPorId(id);

        if (distritoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        request.setAttribute("distrito", distritoOpt.get());
        cargarDepartamentos(request);
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        distritoService.activar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        distritoService.desactivar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Distrito distrito = construirDesdeRequest(request);

        if (distrito.getIdDistrito() == null) {
            distritoService.crear(distrito);
        } else {
            distritoService.actualizar(distrito);
        }

        response.sendRedirect(request.getContextPath() + "/distritos?action=listar");
    }

    private Distrito construirDesdeRequest(HttpServletRequest request) {
        Distrito d = new Distrito();

        Long idDistrito = parseLong(request.getParameter("idDistrito"));
        if (idDistrito != null) {
            d.setIdDistrito(idDistrito);
        }

        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) {
            d.setIdDepartamento(idDepartamento);
        }

        d.setNombre(request.getParameter("nombre"));

        String activoParam = request.getParameter("activo");
        if (idDistrito == null) {
            d.setActivo(true);
        } else {
            d.setActivo("true".equals(activoParam));
        }

        return d;
    }

    private void cargarDepartamentos(HttpServletRequest request) {
        List<Departamento> departamentos = departamentoService.listarActivos();
        request.setAttribute("departamentos", departamentos);
    }

    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/distritos?action=listar";

        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) url += "&idDepartamento=" + idDepartamento;

        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        return url;
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