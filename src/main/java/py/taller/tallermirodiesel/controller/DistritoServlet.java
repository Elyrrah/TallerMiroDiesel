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
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.model.Distrito;
import py.taller.tallermirodiesel.service.CiudadService;
import py.taller.tallermirodiesel.service.DistritoService;
import py.taller.tallermirodiesel.service.impl.CiudadServiceImpl;
import py.taller.tallermirodiesel.service.impl.DistritoServiceImpl;
/**
 * @author elyrr
 */
@WebServlet(name = "DistritoServlet", urlPatterns = {"/distritos"})
// Bloque: Mapeo del servlet (todas las acciones de Distrito entran por /distritos).
public class DistritoServlet extends HttpServlet {

    // Service utilizado por el Servlet para acceder a la capa de negocio.
    private final DistritoService distritoService = new DistritoServiceImpl();

    // Service utilizado para cargar ciudades (combo en formulario y filtro en listado).
    private final CiudadService ciudadService = new CiudadServiceImpl();

    // ========== ========== ========== ========== ==========
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ==========
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué caso ejecutar.
        String accion = request.getParameter("accion");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        if (accion == null || accion.isBlank()) {
            accion = "listar";
        }

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

        // 5. Manejo simple de errores: setear mensaje y volver al listado
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
        if (accion == null || accion.isBlank()) {
            accion = "guardar";
        }

        // 3. Router de acciones POST.
        try {
            switch (accion) {
                case "guardar" -> guardar(request, response);
                default -> guardar(request, response);
            }
        
        // 4. Si falla guardar, devolvemos al formulario con el error y los valores cargados
        } catch (IOException e) {
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES GET
    // ========== ========== ==========

    // LISTAR (con filtro por ciudad y búsqueda por nombre).
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga lista de ciudades para filtro y formulario.
        cargarCiudades(request);

        // 2. Lee filtro opcional por ciudad y texto de búsqueda.
        Long idCiudad = parseLongNullable(request.getParameter("idCiudad"));

        // Mantener compatibilidad: si viene "q" lo tomamos, pero preferimos "filtro"
        String filtro = request.getParameter("filtro");
        if (filtro == null) {
            filtro = request.getParameter("q");
        }

        // 3. Guarda los valores para que el JSP los mantenga seleccionados.
        request.setAttribute("idCiudad", idCiudad);
        request.setAttribute("filtro", filtro);

        // 4. Define la lista base (completa o filtrada por ciudad).
        List<Distrito> listaBase = (idCiudad == null)
                ? distritoService.listarTodos()
                : distritoService.listarPorCiudad(idCiudad);

        // 5. Si NO hay idCiudad pero sí filtro, usamos SQL directo.
        if (idCiudad == null && filtro != null && !filtro.trim().isBlank()) {
            List<Distrito> listaSql = distritoService.buscarPorNombreParcial(filtro);
            request.setAttribute("listaDistritos", listaSql);
            request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_listar.jsp").forward(request, response);
            return;
        }

        // 6. Aplica búsqueda por nombre (opcional) sobre la lista base.
        if (filtro != null) {
            String f = filtro.trim();
            if (!f.isBlank()) {
                String needle = f.toUpperCase(Locale.ROOT);

                listaBase = listaBase.stream().filter(d -> d.getNombre() != null
                                && d.getNombre().toUpperCase(Locale.ROOT).contains(needle)).collect(Collectors.toList());
            }
        }

        // 7. Envía la lista final a la vista.
        request.setAttribute("listaDistritos", listaBase);

        // 8. Renderiza el listado de distritos.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_listar.jsp").forward(request, response);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Para no duplicar lógica, reutilizamos listar (que ya soporta filtro e idCiudad).
        listar(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga ciudades activas para poblar el combo del formulario.
        cargarCiudades(request);

        // 2. Envía un Distrito vacío a la vista para llenado inicial.
        request.setAttribute("distrito", new Distrito());

        // 3. Renderiza el formulario de distrito.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga ciudades activas para poblar el combo del formulario.
        cargarCiudades(request);

        // 2. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 3. Busca el distrito por id usando el service.
        Optional<Distrito> opt = distritoService.buscarPorId(id);

        // 4. Si no existe, se corta el flujo con error.
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        // 5. Coloca el distrito encontrado en request para precargar el formulario.
        request.setAttribute("distrito", opt.get());

        // 6. Renderiza el formulario con datos cargados.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado del distrito a activo usando la capa service.
        distritoService.activar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        String url = request.getContextPath() + "/distritos?accion=listar";

        Long idCiudad = parseLongNullable(request.getParameter("idCiudad"));
        if (idCiudad != null) url += "&idCiudad=" + idCiudad;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado del distrito a inactivo usando la capa service.
        distritoService.desactivar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        String url = request.getContextPath() + "/distritos?accion=listar";

        Long idCiudad = parseLongNullable(request.getParameter("idCiudad"));
        if (idCiudad != null) url += "&idCiudad=" + idCiudad;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES POST
    // ========== ========== ==========

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee parámetros del formulario (idDistrito puede venir vacío en creación).
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        Long idCiudad = parseLong(request.getParameter("idCiudad"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        // 2. Construye el objeto Distrito a partir de los parámetros.
        Distrito d = new Distrito();
        d.setIdDistrito(idDistrito);
        d.setIdCiudad(idCiudad);
        d.setNombre(nombre);
        d.setActivo(activo);

        // 3. Si no tiene id, se crea; si tiene id, se actualiza.
        if (idDistrito == null) {
            distritoService.crear(d);
        } else {
            distritoService.actualizar(d);
        }

        // 4. Redirige al listado preservando filtros.
        String url = request.getContextPath() + "/distritos?accion=listar";

        Long idCiudadFiltro = parseLongNullable(request.getParameter("idCiudadFiltro"));
        if (idCiudadFiltro != null) url += "&idCiudad=" + idCiudadFiltro;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        response.sendRedirect(url);
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ==========

    // CARGAR CIUDADES (COMBO/FILTRO).
    private void cargarCiudades(HttpServletRequest request) {
        
        // Preferible listarActivos para combos; si no lo tenés, usá listarTodos()
        List<Ciudad> ciudades = ciudadService.listarActivos();
        request.setAttribute("ciudades", ciudades);
    }

    // REENVIAR FORMULARIO CON DATOS (EN CASO DE ERROR EN GUARDAR).
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga ciudades para poblar el combo del formulario.
        cargarCiudades(request);

        // 2. Reconstruye el objeto Distrito con los parámetros del request.
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        Long idCiudad = parseLongNullable(request.getParameter("idCiudad"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        Distrito d = new Distrito();
        d.setIdDistrito(idDistrito);
        d.setIdCiudad(idCiudad);
        d.setNombre(nombre);
        d.setActivo(activo);

        // 3. Envía el objeto al JSP para que no se pierdan los datos ingresados.
        request.setAttribute("distrito", d);

        // 4. Renderiza el formulario nuevamente con error.
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
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

    // PARSEO DE BOOLEAN (DEFAULT TRUE).
    private boolean parseBooleanDefaultTrue(String value) {
        // Si el checkbox no viene, por defecto true (ajustalo si preferís false)
        if (value == null) {
            return true;
        }
        return value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("on")
                || value.equalsIgnoreCase("1")
                || value.equalsIgnoreCase("yes");
    }
}
