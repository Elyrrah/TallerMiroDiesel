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
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Distrito;
import com.tallermirodiesel.model.Departamento;
import com.tallermirodiesel.service.DepartamentoService;
import com.tallermirodiesel.service.impl.DepartamentoServiceImpl;
import com.tallermirodiesel.service.DistritoService;
import com.tallermirodiesel.service.impl.DistritoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "DistritoServlet", urlPatterns = {"/distritos"})
// Bloque: Mapeo del servlet (todas las acciones de Distrito entran por /distritos).
public class DistritoServlet extends HttpServlet {

    // Service utilizado por el Servlet para aplicar validaciones y reglas de negocio.
    private final DistritoService distritoService = new DistritoServiceImpl();

    // Service utilizado para cargar departamentos (combo en el formulario y/o filtro en listado).
    private final DepartamentoService departamentoService = new DepartamentoServiceImpl();

    // ========== ========== ========== ========== ========== 
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué caso ejecutar.
        String action = request.getParameter("action");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        if (action == null || action.isBlank()) action = "listar";

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
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

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    // ========== ========== ========== ========== ========== 
    // MANEJO DE POST (ACCIONES QUE MODIFICAN DATOS).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué operación ejecutar.
        String action = request.getParameter("action");

        // 2. Si no viene acción, se asume "guardar" como comportamiento por defecto.
        if (action == null || action.isBlank()) action = "guardar";

        // 3. Router de acciones POST.
        try {
            switch (action) {
                case "guardar" -> guardar(request, response);
                default -> response.sendRedirect(request.getContextPath() + "/distritos?action=listar");
            }

        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());

            // 4. Re-armar objeto para no perder datos del usuario.
            Distrito distrito = construirDesdeRequest(request);
            request.setAttribute("distrito", distrito);

            // 5. Cargar combo de departamentos.
            cargarDepartamentos(request);

            // 6. Re-render del formulario correspondiente.
            request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
        }
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES GET
    // ========== ========== ========== 

    // LISTADO.
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee filtro opcional por departamento.
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));

        // 2. Lee filtro opcional por nombre (búsqueda parcial).
        String filtro = request.getParameter("filtro");

        // 3. Define la lista a renderizar (filtrada o completa).
        List<Distrito> lista;

        if (idDepartamento != null && filtro != null && !filtro.isBlank()) {
            // Si hay idDepartamento + filtro: se filtra por departamento y luego por nombre (en memoria).
            List<Distrito> base = distritoService.listarPorDepartamento(idDepartamento);
            String filtroUpper = filtro.trim().toUpperCase();
            lista = base.stream()
                    .filter(d -> d.getNombre() != null && d.getNombre().toUpperCase().contains(filtroUpper))
                    .toList();

            request.setAttribute("idDepartamento", idDepartamento);
            request.setAttribute("filtro", filtro);

        } else if (idDepartamento != null) {
            // Solo filtro por departamento.
            lista = distritoService.listarPorDepartamento(idDepartamento);
            request.setAttribute("idDepartamento", idDepartamento);

        } else if (filtro != null && !filtro.isBlank()) {
            // Solo filtro por nombre (SQL).
            lista = distritoService.buscarPorNombreParcial(filtro);
            request.setAttribute("filtro", filtro);

        } else {
            // Sin filtros.
            lista = distritoService.listarTodos();
        }

        // 4. Envía la lista a la vista.
        request.setAttribute("lista", lista);

        // 5. Carga departamentos para filtro/combos en la vista.
        cargarDepartamentos(request);

        // 6. Renderiza el listado de distritos.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_listar.jsp").forward(request, response);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        // Reutilizamos la misma lógica del listado para mantener un solo comportamiento.
        listar(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Envía un Distrito vacío a la vista para llenado inicial.
        request.setAttribute("distrito", new Distrito());

        // 2. Carga departamentos activos para poblar el combo.
        cargarDepartamentos(request);

        // 3. Renderiza el formulario de distrito.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca el distrito por id usando el service.
        Optional<Distrito> distritoOpt = distritoService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (distritoOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        // 4. Coloca el distrito encontrado en request para precargar el formulario.
        request.setAttribute("distrito", distritoOpt.get());

        // 5. Carga departamentos activos para poblar el combo.
        cargarDepartamentos(request);

        // 6. Renderiza el formulario con datos cargados.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Cambia el estado del distrito a activo usando la capa service.
        distritoService.activar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Cambia el estado del distrito a inactivo usando la capa service.
        distritoService.desactivar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Construye el objeto Distrito a partir de los parámetros del formulario.
        Distrito distrito = construirDesdeRequest(request);

        // 2. Si no tiene id, se crea; si tiene id, se actualiza.
        if (distrito.getIdDistrito() == null) {
            distritoService.crear(distrito);
        } else {
            distritoService.actualizar(distrito);
        }

        // 3. Redirige al listado para evitar re-envío del formulario al refrescar.
        response.sendRedirect(request.getContextPath() + "/distritos?action=listar");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ========== 

    // CONSTRUIR DISTRITO DESDE REQUEST.
    private Distrito construirDesdeRequest(HttpServletRequest request) {
        Distrito d = new Distrito();

        // 1. Lee idDistrito si viene en el request (modo edición).
        Long idDistrito = parseLong(request.getParameter("idDistrito"));
        if (idDistrito != null) {
            d.setIdDistrito(idDistrito);
        }

        // 2. Lee idDepartamento si viene en el request (FK obligatoria para distrito).
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) {
            d.setIdDepartamento(idDepartamento);
        }

        // 3. Lee el nombre del distrito desde el formulario.
        d.setNombre(request.getParameter("nombre"));

        // 4. Toggle activo/inactivo (checkbox): si viene el parámetro => true; si no viene => false
        //    En creación, si no hay checkbox, por defecto queda true (igual que País/Departamento).
        String activoParam = request.getParameter("activo");
        if (idDistrito == null) {
            d.setActivo(true);
        } else {
            d.setActivo(activoParam != null);
        }

        return d;
    }

    // CARGAR DEPARTAMENTOS (COMBO/FILTRO).
    private void cargarDepartamentos(HttpServletRequest request) {
        List<Departamento> departamentos = departamentoService.listarActivos();
        request.setAttribute("departamentos", departamentos);
    }

    // ARMAR URL DE RETORNO PRESERVANDO FILTROS.
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/distritos?action=listar";

        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) url += "&idDepartamento=" + idDepartamento;

        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        return url;
    }

    // PARSEO.
    private Long parseLong(String value) {

        // 1. Si no hay valor, se devuelve null para que el llamador valide.
        if (value == null || value.isBlank()) return null;

        try {
            // 2. Convierte el string a Long.
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            // 3. Si el valor no es numérico, se devuelve null para evitar romper el flujo.
            return null;
        }
    }
}
