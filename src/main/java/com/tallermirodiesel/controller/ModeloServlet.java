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
                default -> response.sendRedirect(request.getContextPath() + "/modelos?action=listar");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        String filtro = request.getParameter("filtro");
        if (filtro == null) {
            filtro = request.getParameter("q");
        }

        request.setAttribute("idMarca", idMarca);
        request.setAttribute("filtro", filtro);

        List<Modelo> listaBase = (idMarca == null)
                ? modeloService.listarTodos()
                : modeloService.listarPorMarca(idMarca);

        if (idMarca == null && filtro != null && !filtro.isBlank()) {
            List<Modelo> listaSql = modeloService.buscarPorNombreParcial(filtro);
            request.setAttribute("listaModelos", listaSql);
            request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_listar.jsp").forward(request, response);
            return;
        }

        if (filtro != null) {
            String f = filtro.trim();
            if (!f.isBlank()) {
                String needle = f.toUpperCase(Locale.ROOT);
                listaBase = listaBase.stream()
                        .filter(m -> m.getNombre() != null && m.getNombre().toUpperCase(Locale.ROOT).contains(needle))
                        .collect(Collectors.toList());
            }
        }

        request.setAttribute("listaModelos", listaBase);
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_listar.jsp").forward(request, response);
    }

    private void buscar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        listar(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);
        request.setAttribute("modelo", new Modelo());
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_form.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);

        Long id = parseLong(request.getParameter("id"));

        Optional<Modelo> opt = modeloService.buscarPorId(id);

        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + id);
        }

        request.setAttribute("modelo", opt.get());
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_form.jsp").forward(request, response);
    }

    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        modeloService.activar(id);

        String url = request.getContextPath() + "/modelos?action=listar";

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        if (idMarca != null) url += "&idMarca=" + idMarca;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        response.sendRedirect(url);
    }

    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        modeloService.desactivar(id);

        String url = request.getContextPath() + "/modelos?action=listar";

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        if (idMarca != null) url += "&idMarca=" + idMarca;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        response.sendRedirect(url);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long idModelo = parseLongNullable(request.getParameter("idModelo"));
        Long idMarca = parseLong(request.getParameter("idMarca"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        Modelo m = new Modelo();
        m.setIdModelo(idModelo);
        m.setIdMarca(idMarca);
        m.setNombre(nombre);
        m.setActivo(activo);

        if (idModelo == null) {
            modeloService.crear(m);
        } else {
            modeloService.actualizar(m);
        }

        String url = request.getContextPath() + "/modelos?action=listar";

        Long idMarcaFiltro = parseLongNullable(request.getParameter("idMarcaFiltro"));
        if (idMarcaFiltro != null) url += "&idMarca=" + idMarcaFiltro;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        response.sendRedirect(url);
    }

    private void cargarMarcas(HttpServletRequest request) {
        List<Marca> marcas = marcaService.listarActivos();
        request.setAttribute("marcas", marcas);
    }

    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarMarcas(request);

        Long idModelo = parseLongNullable(request.getParameter("idModelo"));
        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        Modelo m = new Modelo();
        m.setIdModelo(idModelo);
        m.setIdMarca(idMarca);
        m.setNombre(nombre);
        m.setActivo(activo);

        request.setAttribute("modelo", m);
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_form.jsp").forward(request, response);
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

    private boolean parseBooleanDefaultTrue(String value) {
        if (value == null) {
            return true;
        }
        return value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("on")
                || value.equalsIgnoreCase("1")
                || value.equalsIgnoreCase("yes");
    }
}