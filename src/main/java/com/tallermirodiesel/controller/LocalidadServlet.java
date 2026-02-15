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
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import com.tallermirodiesel.model.Distrito;
import com.tallermirodiesel.model.Localidad;
import com.tallermirodiesel.service.LocalidadService;
import com.tallermirodiesel.service.impl.DistritoServiceImpl;
import com.tallermirodiesel.service.impl.LocalidadServiceImpl;
import com.tallermirodiesel.service.DistritoService;

/**
 * @author elyrr
 */
@WebServlet(name = "LocalidadServlet", urlPatterns = {"/localidades"})
public class LocalidadServlet extends HttpServlet {

    private final LocalidadService localidadService = new LocalidadServiceImpl();
    private final DistritoService distritoService = new DistritoServiceImpl();

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
                default -> response.sendRedirect(request.getContextPath() + "/localidades?action=listar");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        String filtro = request.getParameter("filtro");

        cargarDistritos(request);

        List<Localidad> lista;

        if (idDistrito != null && filtro != null && !filtro.isBlank()) {
            List<Localidad> base = localidadService.listarPorDistrito(idDistrito);
            String filtroUpper = filtro.trim().toUpperCase(Locale.ROOT);
            lista = base.stream()
                    .filter(l -> l.getNombre() != null && l.getNombre().toUpperCase(Locale.ROOT).contains(filtroUpper))
                    .collect(Collectors.toList());

            request.setAttribute("idDistrito", idDistrito);
            request.setAttribute("filtro", filtro);

        } else if (idDistrito != null) {
            lista = localidadService.listarPorDistrito(idDistrito);
            request.setAttribute("idDistrito", idDistrito);

        } else if (filtro != null && !filtro.isBlank()) {
            lista = localidadService.buscarPorNombreParcial(filtro);
            request.setAttribute("filtro", filtro);

        } else {
            lista = localidadService.listarTodos();
        }

        request.setAttribute("lista", lista);
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_listar.jsp").forward(request, response);
    }

    private void buscar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        listar(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarDistritos(request);
        request.setAttribute("localidad", new Localidad());
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Localidad> localidadOpt = localidadService.buscarPorId(id);

        if (localidadOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + id);
        }

        request.setAttribute("localidad", localidadOpt.get());
        cargarDistritos(request);
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_form.jsp").forward(request, response);
    }

    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        localidadService.activar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        localidadService.desactivar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Localidad l = construirDesdeRequest(request);

        if (l.getIdLocalidad() == null) {
            localidadService.crear(l);
        } else {
            localidadService.actualizar(l);
        }

        response.sendRedirect(request.getContextPath() + "/localidades?action=listar");
    }

    private Localidad construirDesdeRequest(HttpServletRequest request) {
        Localidad l = new Localidad();

        Long idLocalidad = parseLongNullable(request.getParameter("idLocalidad"));
        if (idLocalidad != null) {
            l.setIdLocalidad(idLocalidad);
        }

        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        if (idDistrito != null) {
            l.setIdDistrito(idDistrito);
        }

        l.setNombre(request.getParameter("nombre"));

        String activoParam = request.getParameter("activo");
        if (idLocalidad == null) {
            l.setActivo(true);
        } else {
            l.setActivo("true".equals(activoParam));
        }

        return l;
    }

    private void cargarDistritos(HttpServletRequest request) {
        List<Distrito> distritos = distritoService.listarActivos();
        request.setAttribute("distritos", distritos);
    }

    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Localidad l = construirDesdeRequest(request);
        request.setAttribute("localidad", l);
        cargarDistritos(request);
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_form.jsp").forward(request, response);
    }

    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/localidades?action=listar";

        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        if (idDistrito != null) url += "&idDistrito=" + idDistrito;

        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        return url;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Parámetro numérico obligatorio.");
        }
        try {
            Long n = Long.valueOf(value);
            if (n <= 0) {
                throw new IllegalArgumentException("El parámetro numérico debe ser mayor a 0.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parámetro numérico inválido: " + value);
        }
    }

    private Long parseLongNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long n = Long.valueOf(value);
            if (n <= 0) {
                return null;
            }
            return n;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}