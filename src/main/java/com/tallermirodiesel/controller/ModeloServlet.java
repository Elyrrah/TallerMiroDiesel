/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tallermirodiesel.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import com.tallermirodiesel.model.Marca;
import com.tallermirodiesel.model.Modelo;
import com.tallermirodiesel.service.MarcaService;
import com.tallermirodiesel.service.ModeloService;
import com.tallermirodiesel.service.impl.MarcaServiceImpl;
import com.tallermirodiesel.service.impl.ModeloServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "ModeloServlet", urlPatterns = {"/modelos"})
public class ModeloServlet extends HttpServlet {

    private final ModeloService modeloService = new ModeloServiceImpl();
    private final MarcaService marcaService = new MarcaServiceImpl();

    // Gestión de peticiones de lectura y navegación de modelos vía GET
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
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    // Gestión de procesamiento de datos para la persistencia de modelos vía POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(request, response);
                default -> response.sendRedirect(request.getContextPath() + "/modelos?action=listar");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    // Lógica para recuperar modelos con soporte de filtrado por marca y búsqueda de texto
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        String filtro = request.getParameter("filtro");
        if (filtro == null) {
            filtro = request.getParameter("q");
        }

        request.setAttribute("idMarca", idMarca != null ? idMarca : "");
        request.setAttribute("filtro", filtro != null ? filtro : "");

        List<Modelo> listaBase = (idMarca == null)
                ? modeloService.listarTodos()
                : modeloService.listarPorMarca(idMarca);

        if (idMarca == null && filtro != null && !filtro.isBlank()) {
            request.setAttribute("listaModelos", modeloService.buscarPorNombreParcial(filtro));
            request.getRequestDispatcher("/WEB-INF/views/catalogos/modelos/modelo_listar.jsp").forward(request, response);
            return;
        }

        if (filtro != null && !filtro.isBlank()) {
            String needle = filtro.trim().toUpperCase(Locale.ROOT);
            listaBase = listaBase.stream()
                    .filter(m -> m.getNombre() != null && m.getNombre().toUpperCase(Locale.ROOT).contains(needle))
                    .collect(Collectors.toList());
        }

        request.setAttribute("listaModelos", listaBase);
        request.getRequestDispatcher("/WEB-INF/views/catalogos/modelos/modelo_listar.jsp").forward(request, response);
    }

    // Preparación del contexto relacional y despacho del formulario para nuevo modelo
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);
        request.setAttribute("modelo", new Modelo());
        request.getRequestDispatcher("/WEB-INF/views/catalogos/modelos/modelo_form.jsp").forward(request, response);
    }

    // Recuperación de la entidad modelo y carga de marcas para el modo edición
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);

        Long id = parseLong(request.getParameter("id"));

        Optional<Modelo> opt = modeloService.buscarPorId(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + id);
        }

        request.setAttribute("modelo", opt.get());
        request.getRequestDispatcher("/WEB-INF/views/catalogos/modelos/modelo_form.jsp").forward(request, response);
    }

    // Procesamiento de habilitación de modelo preservando los filtros de navegación
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        modeloService.activar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // Procesamiento de inhabilitación de modelo preservando los filtros de navegación
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        modeloService.desactivar(id);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // Lógica para persistir la entidad modelo y redireccionar al listado maestro
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Modelo m = construirDesdeRequest(request);

        if (m.getIdModelo() == null) {
            modeloService.crear(m);
        } else {
            modeloService.actualizar(m);
        }

        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // Utilidad interna para el mapeo de parámetros HTTP hacia la entidad Modelo
    private Modelo construirDesdeRequest(HttpServletRequest request) {
        Modelo m = new Modelo();

        Long idModelo = parseLongNullable(request.getParameter("idModelo"));
        m.setIdModelo(idModelo);

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        m.setIdMarca(idMarca);

        m.setNombre(request.getParameter("nombre"));

        String activoParam = request.getParameter("activo");
        if (idModelo == null) {
            m.setActivo(true);
        } else {
            m.setActivo("true".equals(activoParam));
        }

        return m;
    }

    // Inyección de marcas activas en el alcance de la petición para componentes select
    private void cargarMarcas(HttpServletRequest request) {
        List<Marca> marcas = marcaService.listarActivos();
        request.setAttribute("marcas", marcas);
    }

    // Lógica de recuperación ante errores para evitar la pérdida de datos en el formulario
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);
        request.setAttribute("modelo", construirDesdeRequest(request));
        request.getRequestDispatcher("/WEB-INF/views/catalogos/modelos/modelo_form.jsp").forward(request, response);
    }

    // Utilidad para la reconstrucción de la URL con parámetros de búsqueda y filtrado por marca
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/modelos?action=listar";

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        if (idMarca != null) url += "&idMarca=" + idMarca;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        return url;
    }

    // Utilidad para la conversión obligatoria de cadenas a Long con validación
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

    // Utilidad para la conversión opcional de cadenas a Long
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