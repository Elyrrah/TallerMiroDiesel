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
import com.tallermirodiesel.model.Departamento;
import com.tallermirodiesel.service.DepartamentoService;
import com.tallermirodiesel.service.impl.DepartamentoServiceImpl;
import com.tallermirodiesel.service.PaisService;
import com.tallermirodiesel.service.impl.PaisServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "DepartamentoServlet", urlPatterns = {"/departamentos"})
public class DepartamentoServlet extends HttpServlet {

    private DepartamentoService departamentoService;
    private PaisService paisService;

    // Inicialización de los servicios de geografía (Departamentos y Países) al cargar el servlet
    @Override
    public void init() {
        this.departamentoService = new DepartamentoServiceImpl();
        this.paisService = new PaisServiceImpl();
    }

    // Gestión de peticiones de lectura, navegación de formularios y cambios de estado vía GET
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

    // Gestión de procesamiento de datos enviados desde formularios para persistencia vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/departamentos?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("paises", paisService.listarActivos());

            Departamento d = new Departamento();
            Long idDepartamento = parseLong(req.getParameter("idDepartamento"));
            d.setIdDepartamento(idDepartamento);
            d.setIdPais(parseLong(req.getParameter("idPais")));
            d.setNombre(req.getParameter("nombre"));
            d.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("departamento", d);
            req.getRequestDispatcher("/WEB-INF/views/geografia/departamentos/departamento_form.jsp").forward(req, resp);
        }
    }

    // Lógica para recuperar departamentos filtrados por país o nombre y despachar la vista de listado
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long idPais = parseLong(req.getParameter("idPais"));
        String filtro = req.getParameter("filtro");

        req.setAttribute("paises", paisService.listarActivos());
        req.setAttribute("idPaisSeleccionado", idPais);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        List<Departamento> lista;

        if (idPais != null && filtro != null && !filtro.isBlank()) {
            List<Departamento> base = departamentoService.listarPorPais(idPais);
            String filtroUpper = filtro.trim().toUpperCase(Locale.ROOT);
            lista = base.stream()
                    .filter(d -> d.getNombre() != null && d.getNombre().toUpperCase(Locale.ROOT).contains(filtroUpper))
                    .collect(Collectors.toList());
        } else if (idPais != null) {
            lista = departamentoService.listarPorPais(idPais);
        } else if (filtro != null && !filtro.isBlank()) {
            lista = departamentoService.buscarPorNombreParcial(filtro);
        } else {
            lista = departamentoService.listarTodos();
        }

        req.setAttribute("departamentos", lista);
        req.getRequestDispatcher("/WEB-INF/views/geografia/departamentos/departamento_listar.jsp").forward(req, resp);
    }

    // Preparación de datos (lista de países activos) y despacho del formulario para un nuevo registro
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("paises", paisService.listarActivos());
        req.setAttribute("departamento", new Departamento());
        req.getRequestDispatcher("/WEB-INF/views/geografia/departamentos/departamento_form.jsp").forward(req, resp);
    }

    // Recuperación de datos existentes junto con la lista de países para edición de departamento
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Departamento> departamento = departamentoService.buscarPorId(id);

        if (departamento.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

        req.setAttribute("paises", paisService.listarActivos());
        req.setAttribute("departamento", departamento.get());
        req.getRequestDispatcher("/WEB-INF/views/geografia/departamentos/departamento_form.jsp").forward(req, resp);
    }

    // Procesamiento de habilitación de departamento con mantenimiento de filtros de búsqueda en la redirección
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        departamentoService.activar(id);

        String url = req.getContextPath() + "/departamentos?action=listar";
        Long idPais = parseLong(req.getParameter("idPais"));
        if (idPais != null) url += "&idPais=" + idPais;

        String filtro = req.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        resp.sendRedirect(url);
    }

    // Procesamiento de inhabilitación de departamento con mantenimiento de filtros de búsqueda en la redirección
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        departamentoService.desactivar(id);

        String url = req.getContextPath() + "/departamentos?action=listar";
        Long idPais = parseLong(req.getParameter("idPais"));
        if (idPais != null) url += "&idPais=" + idPais;

        String filtro = req.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        resp.sendRedirect(url);
    }

    // Lógica para recolectar datos, persistir cambios y redireccionar manteniendo el contexto del país seleccionado
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long idDepartamento = parseLong(req.getParameter("idDepartamento"));

        Departamento d = new Departamento();
        d.setIdDepartamento(idDepartamento);
        d.setIdPais(parseLong(req.getParameter("idPais")));
        d.setNombre(req.getParameter("nombre"));

        if (idDepartamento == null) {
            d.setActivo(true);
            departamentoService.crear(d);
        } else {
            d.setActivo("true".equals(req.getParameter("activo")));
            departamentoService.actualizar(d);
        }

        String url = req.getContextPath() + "/departamentos?action=listar";
        Long idPais = parseLong(req.getParameter("idPais"));
        if (idPais != null) url += "&idPais=" + idPais;

        resp.sendRedirect(url);
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