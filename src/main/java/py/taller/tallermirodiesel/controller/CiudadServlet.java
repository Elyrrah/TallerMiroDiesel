/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.service.CiudadService;
import py.taller.tallermirodiesel.service.impl.CiudadServiceImpl;
import py.taller.tallermirodiesel.model.Departamento;
import py.taller.tallermirodiesel.service.DepartamentoService;
import py.taller.tallermirodiesel.service.impl.DepartamentoServiceImpl;
/**
 * @author elyrr
 */
@WebServlet(name = "CiudadServlet", urlPatterns = {"/ciudades"})
// Bloque: Mapeo del servlet (todas las acciones de Ciudad entran por /ciudades).
public class CiudadServlet extends HttpServlet {

    // Service utilizado por el Servlet para aplicar validaciones y reglas de negocio.
    private final CiudadService ciudadService = new CiudadServiceImpl();

    // Service utilizado para cargar departamentos (combo en el formulario y/o filtro en listado).
    private final DepartamentoService departamentoService = new DepartamentoServiceImpl();

    // ========== ========== ========== ========== ==========
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ==========
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué caso ejecutar.
        String accion = request.getParameter("accion");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        if (accion == null || accion.isBlank()) accion = "listar";

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
        try {
            switch (accion) {
                case "nuevo" -> mostrarFormularioNuevo(request, response);
                case "editar" -> mostrarFormularioEditar(request, response);
                case "activar" -> activar(request, response);
                case "desactivar" -> desactivar(request, response);
                case "buscar" -> buscar(request, response);
                case "listar" -> listar(request, response);
                default -> listar(request, response);
            }

        } catch (ServletException | IOException e) {
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    // ========== ========== ========== ========== ==========
    // MANEJO DE POST (ACCIONES QUE MODIFICAN DATOS).
    // ========== ========== ========== ========== ==========
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué operación ejecutar.
        String accion = request.getParameter("accion");

        // 2. Si no viene acción, se asume "guardar" como comportamiento por defecto.
        if (accion == null || accion.isBlank()) accion = "guardar";

        // 3. Router de acciones POST.
        try {
            switch (accion) {
                case "guardar" -> guardar(request, response);
                default -> response.sendRedirect(request.getContextPath() + "/ciudades?accion=listar");
            }

        } catch (IOException e) {
            request.setAttribute("error", e.getMessage());

            // 4. Re-armar objeto para no perder datos del usuario.
            Ciudad ciudad = construirDesdeRequest(request);
            request.setAttribute("ciudad", ciudad);

            // 5. Cargar combo de departamentos.
            cargarDepartamentos(request);

            // 6. Re-render del formulario correspondiente.
            request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_form.jsp").forward(request, response);
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
        List<Ciudad> lista;

        if (idDepartamento != null && filtro != null && !filtro.isBlank()) {
            // Si hay idDepartamento + filtro: se filtra por departamento y luego por nombre (en memoria).
            List<Ciudad> base = ciudadService.listarPorDepartamento(idDepartamento);
            String filtroUpper = filtro.trim().toUpperCase();
            lista = base.stream().filter(c -> c.getNombre() != null && 
                    c.getNombre().toUpperCase().contains(filtroUpper)).toList();

            request.setAttribute("idDepartamento", idDepartamento);
            request.setAttribute("filtro", filtro);

        } else if (idDepartamento != null) {
            // Solo filtro por departamento.
            lista = ciudadService.listarPorDepartamento(idDepartamento);
            request.setAttribute("idDepartamento", idDepartamento);

        } else if (filtro != null && !filtro.isBlank()) {
            // Solo filtro por nombre (SQL).
            lista = ciudadService.buscarPorNombreParcial(filtro);
            request.setAttribute("filtro", filtro);

        } else {
            // Sin filtros.
            lista = ciudadService.listarTodos();
        }

        // 4. Envía la lista a la vista.
        request.setAttribute("lista", lista);

        // 5. Carga departamentos para filtro/combos en la vista.
        cargarDepartamentos(request);

        // 6. Renderiza el listado de ciudades.
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_listar.jsp").forward(request, response);
    }
 
    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

        // 1. Lee filtros.
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        String filtro = request.getParameter("filtro");

        // 2. Define la lista a renderizar.
        List<Ciudad> lista;

        if (idDepartamento != null && filtro != null && !filtro.isBlank()) {
            // Si hay idDepartamento + filtro: se filtra por departamento y luego por nombre (en memoria).
            List<Ciudad> base = ciudadService.listarPorDepartamento(idDepartamento);
            String filtroUpper = filtro.trim().toUpperCase();
            lista = base.stream() .filter(c -> c.getNombre() != null && 
                    c.getNombre().toUpperCase().contains(filtroUpper)) .toList();

            request.setAttribute("idDepartamento", idDepartamento);
            request.setAttribute("filtro", filtro);

        } else if (idDepartamento != null) {
            lista = ciudadService.listarPorDepartamento(idDepartamento);
            request.setAttribute("idDepartamento", idDepartamento);

        } else if (filtro != null && !filtro.isBlank()) {
            lista = ciudadService.buscarPorNombreParcial(filtro);
            request.setAttribute("filtro", filtro);

        } else {
            lista = ciudadService.listarTodos();
        }

        // 3. Envía la lista a la vista.
        request.setAttribute("lista", lista);

        // 4. Carga departamentos para filtro/combos en la vista.
        cargarDepartamentos(request);

        // 5. Renderiza el listado de ciudades.
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_listar.jsp").forward(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Envía una Ciudad vacía a la vista para llenado inicial.
        request.setAttribute("ciudad", new Ciudad());

        // 2. Carga departamentos activos para poblar el combo.
        cargarDepartamentos(request);

        // 3. Renderiza el formulario de ciudad.
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_form.jsp").forward(request, response);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca la ciudad por id usando el service.
        Optional<Ciudad> ciudadOpt = ciudadService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (ciudadOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una ciudad con id: " + id);
        }

        // 4. Coloca la ciudad encontrada en request para precargar el formulario.
        request.setAttribute("ciudad", ciudadOpt.get());

        // 5. Carga departamentos activos para poblar el combo.
        cargarDepartamentos(request);

        // 6. Renderiza el formulario con datos cargados.
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_form.jsp").forward(request, response);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Cambia el estado de la ciudad a activo usando la capa service.
        ciudadService.activar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        String url = request.getContextPath() + "/ciudades?accion=listar";

        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) url += "&idDepartamento=" + idDepartamento;

        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Cambia el estado de la ciudad a inactivo usando la capa service.
        ciudadService.desactivar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        String url = request.getContextPath() + "/ciudades?accion=listar";

        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) url += "&idDepartamento=" + idDepartamento;

        String filtro = request.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES POST
    // ========== ========== ==========

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Construye el objeto Ciudad a partir de los parámetros del formulario.
        Ciudad ciudad = construirDesdeRequest(request);

        // 2. Si no tiene id, se crea; si tiene id, se actualiza.
        if (ciudad.getIdCiudad() == null) {
            ciudadService.crear(ciudad);
        } else {
            ciudadService.actualizar(ciudad);
        }

        // 3. Redirige al listado para evitar re-envío del formulario al refrescar.
        response.sendRedirect(request.getContextPath() + "/ciudades?accion=listar");
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ==========

    // CONSTRUIR CIUDAD DESDE REQUEST.
    private Ciudad construirDesdeRequest(HttpServletRequest request) {
        Ciudad c = new Ciudad();

        // 1. Lee idCiudad si viene en el request (modo edición).
        Long idCiudad = parseLong(request.getParameter("idCiudad"));
        if (idCiudad != null) {
            c.setIdCiudad(idCiudad);
        }

        // 2. Lee idDepartamento si viene en el request (FK obligatoria para ciudad).
        Long idDepartamento = parseLong(request.getParameter("idDepartamento"));
        if (idDepartamento != null) {
            c.setIdDepartamento(idDepartamento);
        }

        // 3. Lee el nombre de la ciudad desde el formulario.
        c.setNombre(request.getParameter("nombre"));

        // 4. En creación: activo por defecto true; en edición: tomar el valor del form
        if (idCiudad == null) {
            c.setActivo(true);
        } else {
            String activoParam = request.getParameter("activo");
            c.setActivo("true".equals(activoParam) || activoParam != null);
        }

        return c;
    }

    // CARGAR DEPARTAMENTOS (COMBO/FILTRO).
    private void cargarDepartamentos(HttpServletRequest request) {
        List<Departamento> departamentos = departamentoService.listarActivos();
        request.setAttribute("departamentos", departamentos);
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
