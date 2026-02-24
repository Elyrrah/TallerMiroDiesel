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

    // Gestión de peticiones de lectura, búsqueda y navegación de formularios de localidades vía GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            switch (action) {
                case "nuevo"      -> mostrarFormularioNuevo(request, response);
                case "editar"     -> mostrarFormularioEditar(request, response);
                case "activar"    -> activar(request, response);
                case "desactivar" -> desactivar(request, response);
                case "buscar"     -> listar(request, response);
                case "listar"     -> listar(request, response);
                default           -> listar(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    // Gestión de procesamiento de datos para la persistencia de localidades vía POST
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

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    // Lógica para recuperar localidades con soporte de filtrado jerárquico por distrito y búsqueda de texto
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        String filtro = request.getParameter("filtro");

        request.setAttribute("idDistrito", idDistrito != null ? idDistrito : "");
        request.setAttribute("filtro", filtro != null ? filtro : "");

        cargarDistritos(request);

        List<Localidad> lista;

        if (idDistrito != null && filtro != null && !filtro.isBlank()) {
            List<Localidad> base = localidadService.listarPorDistrito(idDistrito);
            String filtroUpper = filtro.trim().toUpperCase(Locale.ROOT);
            lista = base.stream()
                    .filter(l -> l.getNombre() != null && l.getNombre().toUpperCase(Locale.ROOT).contains(filtroUpper))
                    .collect(Collectors.toList());

        } else if (idDistrito != null) {
            lista = localidadService.listarPorDistrito(idDistrito);

        } else if (filtro != null && !filtro.isBlank()) {
            lista = localidadService.buscarPorNombreParcial(filtro);

        } else {
            lista = localidadService.listarTodos();
        }

        request.setAttribute("lista", lista);
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_listar.jsp").forward(request, response);
    }

    // Preparación del contexto relacional y despacho del formulario para nueva localidad
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("localidad", new Localidad());
        cargarDistritos(request);
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_form.jsp").forward(request, response);
    }

    // Recuperación de la entidad localidad y carga de distritos para el modo edición
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long id = parseLong(request.getParameter("id"));
        Optional<Localidad> localidadOpt = localidadService.buscarPorId(id);

        if (localidadOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + id);
        }

        request.setAttribute("localidad", localidadOpt.get());
        cargarDistritos(request);
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_form.jsp").forward(request, response);
    }

    // Procesamiento de habilitación de localidad manteniendo los filtros de navegación actuales
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        localidadService.activar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // Procesamiento de inhabilitación de localidad manteniendo los filtros de navegación actuales
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        localidadService.desactivar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // Lógica para persistir la entidad localidad (crear o actualizar) y redirección al listado
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Localidad l = construirDesdeRequest(request);

        if (l.getIdLocalidad() == null) {
            localidadService.crear(l);
        } else {
            localidadService.actualizar(l);
        }

        response.sendRedirect(request.getContextPath() + "/localidades?action=listar");
    }

    // Utilidad interna para el mapeo de parámetros HTTP hacia la entidad Localidad
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

    // Inyección de distritos activos en el alcance de la petición para componentes select
    private void cargarDistritos(HttpServletRequest request) {
        List<Distrito> distritos = distritoService.listarActivos();
        request.setAttribute("distritos", distritos);
    }

    // Lógica de recuperación ante errores para evitar la pérdida de datos en el formulario
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Localidad l = construirDesdeRequest(request);
        request.setAttribute("localidad", l);
        cargarDistritos(request);
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_form.jsp").forward(request, response);
    }

    // Utilidad para la reconstrucción de la URL con parámetros de búsqueda y filtrado por distrito
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

    // Utilidad para la conversión obligatoria de cadenas a Long con validación de valor positivo
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

    // Utilidad para la conversión opcional de cadenas a Long, retornando null ante invalidez
    private Long parseLongNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long n = Long.valueOf(value);
            return (n <= 0) ? null : n;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}