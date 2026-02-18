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
// Bloque: Mapeo del servlet (todas las acciones de Localidad entran por /localidades).
public class LocalidadServlet extends HttpServlet {

    // Service utilizado por el Servlet para acceder a la capa de negocio de Localidad.
    private final LocalidadService localidadService = new LocalidadServiceImpl();

    // Service utilizado para cargar distritos (combo en formulario y filtro en listado).
    private final DistritoService distritoService = new DistritoServiceImpl();

    // ========== ========== ========== ========== ==========
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ==========
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué caso ejecutar.
        String action = request.getParameter("action");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        if (action == null || action.isBlank()) {
            action = "listar";
        }

        // 3. Router de acciones GET (controlador tipo front-controller por parámetro).
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

    // ========== ========== ========== ========== ==========
    // MANEJO DE POST (ACCIONES QUE MODIFICAN DATOS).
    // ========== ========== ========== ========== ==========
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué operación ejecutar.
        String action = request.getParameter("action");

        // 2. Si no viene acción, se asume "guardar" como comportamiento por defecto.
        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        // 3. Router de acciones POST.
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

    // ========== ========== ==========
    // BLOQUE DE ACCIONES GET
    // ========== ========== ==========

    // LISTAR (con filtro por distrito y búsqueda por nombre).
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee filtro opcional por distrito.
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));

        // 2. Lee filtro opcional de texto de búsqueda.
        String filtro = request.getParameter("filtro");

        // 3. Siempre seteamos los atributos de filtro para que el JSP nunca los reciba como null.
        request.setAttribute("idDistrito", idDistrito != null ? idDistrito : "");
        request.setAttribute("filtro", filtro != null ? filtro : "");

        // 4. Carga distritos activos para filtro y formulario.
        cargarDistritos(request);

        // 5. Define la lista a renderizar (filtrada o completa).
        List<Localidad> lista;

        if (idDistrito != null && filtro != null && !filtro.isBlank()) {
            // Si hay idDistrito + filtro: se filtra por distrito y luego por nombre (en memoria).
            List<Localidad> base = localidadService.listarPorDistrito(idDistrito);
            String filtroUpper = filtro.trim().toUpperCase(Locale.ROOT);
            lista = base.stream()
                    .filter(l -> l.getNombre() != null && l.getNombre().toUpperCase(Locale.ROOT).contains(filtroUpper))
                    .collect(Collectors.toList());

        } else if (idDistrito != null) {
            // Solo filtro por distrito.
            lista = localidadService.listarPorDistrito(idDistrito);

        } else if (filtro != null && !filtro.isBlank()) {
            // Solo filtro por nombre (SQL).
            lista = localidadService.buscarPorNombreParcial(filtro);

        } else {
            // Sin filtros.
            lista = localidadService.listarTodos();
        }

        // 6. Envía la lista a la vista.
        request.setAttribute("lista", lista);

        // 7. Renderiza el listado de localidades.
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_listar.jsp").forward(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Envía una Localidad vacía a la vista para llenado inicial.
        request.setAttribute("localidad", new Localidad());

        // 2. Carga distritos activos para poblar el combo.
        cargarDistritos(request);

        // 3. Renderiza el formulario de localidad.
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_form.jsp").forward(request, response);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Busca la localidad por id usando el service.
        Optional<Localidad> localidadOpt = localidadService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (localidadOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + id);
        }

        // 4. Coloca la localidad encontrada en request para precargar el formulario.
        request.setAttribute("localidad", localidadOpt.get());

        // 5. Carga distritos activos para poblar el combo.
        cargarDistritos(request);

        // 6. Renderiza el formulario con datos cargados.
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_form.jsp").forward(request, response);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado de la localidad a activo usando la capa service.
        localidadService.activar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado de la localidad a inactivo usando la capa service.
        localidadService.desactivar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES POST
    // ========== ========== ==========

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Construye el objeto Localidad a partir de los parámetros del formulario.
        Localidad l = construirDesdeRequest(request);

        // 2. Si no tiene id, se crea; si tiene id, se actualiza.
        if (l.getIdLocalidad() == null) {
            localidadService.crear(l);
        } else {
            localidadService.actualizar(l);
        }

        // 3. Redirige al listado para evitar re-envío del formulario al refrescar.
        response.sendRedirect(request.getContextPath() + "/localidades?action=listar");
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ==========

    // CONSTRUIR LOCALIDAD DESDE REQUEST.
    private Localidad construirDesdeRequest(HttpServletRequest request) {
        Localidad l = new Localidad();

        // 1. Lee idLocalidad si viene en el request (modo edición).
        Long idLocalidad = parseLongNullable(request.getParameter("idLocalidad"));
        if (idLocalidad != null) {
            l.setIdLocalidad(idLocalidad);
        }

        // 2. Lee idDistrito si viene en el request (FK obligatoria para localidad).
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        if (idDistrito != null) {
            l.setIdDistrito(idDistrito);
        }

        // 3. Lee el nombre de la localidad desde el formulario.
        l.setNombre(request.getParameter("nombre"));

        // 4. Toggle activo/inactivo (SELECT):
        //    En creación: true por defecto.
        //    En edición: lee el valor del select.
        String activoParam = request.getParameter("activo");
        if (idLocalidad == null) {
            l.setActivo(true);
        } else {
            l.setActivo("true".equals(activoParam));
        }

        return l;
    }

    // CARGAR DISTRITOS (COMBO/FILTRO).
    private void cargarDistritos(HttpServletRequest request) {
        List<Distrito> distritos = distritoService.listarActivos();
        request.setAttribute("distritos", distritos);
    }

    // REENVIAR FORMULARIO CON DATOS (EN CASO DE ERROR EN GUARDAR).
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Reconstruye el objeto Localidad con los parámetros del request.
        Localidad l = construirDesdeRequest(request);

        // 2. Envía el objeto al JSP para que no se pierdan los datos ingresados.
        request.setAttribute("localidad", l);

        // 3. Carga distritos para poblar el combo del formulario.
        cargarDistritos(request);

        // 4. Renderiza el formulario nuevamente con error.
        request.getRequestDispatcher("/WEB-INF/views/geografia/localidades/localidad_form.jsp").forward(request, response);
    }

    // ARMAR URL DE RETORNO PRESERVANDO FILTROS.
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/localidades?action=listar";

        // Preserva el filtro de distrito si existe.
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        if (idDistrito != null) url += "&idDistrito=" + idDistrito;

        // Preserva el filtro de nombre si existe, codificado para evitar problemas con caracteres especiales.
        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        return url;
    }

    // PARSEO DE LONG OBLIGATORIO.
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

    // PARSEO DE LONG OPCIONAL (RETORNA NULL SI NO APLICA O ES INVÁLIDO).
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