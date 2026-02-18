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

    // Inicializa los servicios directamente
    private final DistritoService distritoService = new DistritoServiceImpl();
    private final DepartamentoService departamentoService = new DepartamentoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtiene el parámetro 'action' de la URL
        String action = request.getParameter("action");

        // Si no hay acción, por defecto es 'listar'
        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            // Switch para manejar las diferentes acciones GET
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
            // Si hay error, lo muestra en la página de listado
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtiene el parámetro 'action' de la petición POST
        String action = request.getParameter("action");

        // Si no hay acción, por defecto es 'guardar'
        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            // Switch para manejar las diferentes acciones POST
            switch (action) {
                case "guardar" -> guardar(request, response);
                default -> response.sendRedirect(request.getContextPath() + "/distritos?action=listar");
            }
        } catch (RuntimeException e) {
            // Si hay error al guardar, vuelve al formulario con los datos ingresados
            request.setAttribute("error", e.getMessage());

            // Reconstruye el objeto Distrito con los datos del formulario
            Distrito distrito = construirDesdeRequest(request);
            request.setAttribute("distrito", distrito);

            // Carga la lista de departamentos para el select del formulario
            cargarDepartamentos(request);
            request.getRequestDispatcher("/WEB-INF/views/geografia/distritos/distrito_form.jsp").forward(request, response);
        }
    }

    /**
     * Muestra el listado de distritos (con filtros opcionales por departamento y nombre)
     */
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        String filtro = request.getParameter("filtro");

        // Siempre seteamos los atributos de filtro para que el JSP nunca los reciba como null
        request.setAttribute("idDepartamento", idDepartamento != null ? idDepartamento : "");
        request.setAttribute("filtro", filtro != null ? filtro : "");

        List<Distrito> lista;

        // Aplica filtros según los parámetros recibidos
        if (idDepartamento != null && filtro != null && !filtro.isBlank()) {
            // Filtro combinado: por departamento y por nombre
            List<Distrito> base = distritoService.listarPorDepartamento(idDepartamento);
            String filtroUpper = filtro.trim().toUpperCase();
            lista = base.stream()
                    .filter(d -> d.getNombre() != null && d.getNombre().toUpperCase().contains(filtroUpper))
                    .collect(Collectors.toList());

        } else if (idDepartamento != null) {
            // Solo filtro por departamento
            lista = distritoService.listarPorDepartamento(idDepartamento);

        } else if (filtro != null && !filtro.isBlank()) {
            // Solo filtro por nombre
            lista = distritoService.buscarPorNombreParcial(filtro);

        } else {
            // Sin filtros, lista todos
            lista = distritoService.listarTodos();
        }

        request.setAttribute("lista", lista);
        cargarDepartamentos(request);
        // Redirige a la vista de listado
        request.getRequestDispatcher("/WEB-INF/views/geografia/distritos/distrito_listar.jsp").forward(request, response);
    }

    /**
     * Muestra el formulario para crear un nuevo distrito
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Carga la lista de departamentos activos para el select del formulario
        cargarDepartamentos(request);
        request.setAttribute("distrito", new Distrito());
        request.getRequestDispatcher("/WEB-INF/views/geografia/distritos/distrito_form.jsp").forward(request, response);
    }

    /**
     * Muestra el formulario para editar un distrito existente
     */
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtiene el ID del distrito a editar
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        // Busca el distrito en la base de datos
        Optional<Distrito> distritoOpt = distritoService.buscarPorId(id);

        if (distritoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        // Envía el distrito y la lista de departamentos al formulario
        request.setAttribute("distrito", distritoOpt.get());
        cargarDepartamentos(request);
        request.getRequestDispatcher("/WEB-INF/views/geografia/distritos/distrito_form.jsp").forward(request, response);
    }

    /**
     * Activa un distrito (lo marca como activo en la BD)
     */
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        // Llama al servicio para activar el distrito
        distritoService.activar(id);
        // Redirige al listado preservando los filtros
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    /**
     * Desactiva un distrito (lo marca como inactivo en la BD)
     */
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        // Llama al servicio para desactivar el distrito
        distritoService.desactivar(id);
        // Redirige al listado preservando los filtros
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    /**
     * Guarda un distrito (crea nuevo o actualiza existente)
     */
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Construye el objeto Distrito desde los parámetros del request
        Distrito distrito = construirDesdeRequest(request);

        // Si no tiene ID, es un nuevo registro
        if (distrito.getIdDistrito() == null) {
            distritoService.crear(distrito);
        } else {
            // Si tiene ID, es una actualización
            distritoService.actualizar(distrito);
        }

        // Redirige al listado después de guardar
        response.sendRedirect(request.getContextPath() + "/distritos?action=listar");
    }

    /**
     * Construye un objeto Distrito desde los parámetros del request
     */
    private Distrito construirDesdeRequest(HttpServletRequest request) {
        Distrito d = new Distrito();

        // Obtiene el ID del distrito si existe
        Long idDistrito = parseLong(request.getParameter("idDistrito"));
        if (idDistrito != null) {
            d.setIdDistrito(idDistrito);
        }

        // Obtiene el ID del departamento
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) {
            d.setIdDepartamento(idDepartamento);
        }

        // Obtiene el nombre del distrito
        d.setNombre(request.getParameter("nombre"));

        // Maneja el estado activo/inactivo
        String activoParam = request.getParameter("activo");
        if (idDistrito == null) {
            // Los nuevos distritos siempre están activos
            d.setActivo(true);
        } else {
            d.setActivo("true".equals(activoParam));
        }

        return d;
    }

    /**
     * Carga la lista de departamentos activos en el request
     */
    private void cargarDepartamentos(HttpServletRequest request) {
        List<Departamento> departamentos = departamentoService.listarActivos();
        request.setAttribute("departamentos", departamentos);
    }

    /**
     * Construye la URL de retorno al listado preservando los filtros
     */
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/distritos?action=listar";

        // Preserva el filtro de departamento si existe
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) url += "&idDepartamento=" + idDepartamento;

        // Preserva el filtro de nombre si existe
        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        return url;
    }

    /**
     * Convierte un String a Long de forma segura
     * @param value String a convertir
     * @return Long convertido o null si no es válido
     */
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