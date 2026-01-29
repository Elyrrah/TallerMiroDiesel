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
import py.taller.tallermirodiesel.model.Distrito;
import py.taller.tallermirodiesel.model.Localidad;
import py.taller.tallermirodiesel.service.LocalidadService;
import py.taller.tallermirodiesel.service.impl.DistritoServiceImpl;
import py.taller.tallermirodiesel.service.impl.LocalidadServiceImpl;
import py.taller.tallermirodiesel.service.DistritoService;

/**
 * @author elyrr
 */
@WebServlet(name = "LocalidadServlet", urlPatterns = {"/localidades"})
// Bloque: Mapeo del servlet (todas las acciones de Localidad entran por /localidades).
public class LocalidadServlet extends HttpServlet {

    // Service utilizado por el Servlet para acceder a la capa de negocio.
    private final LocalidadService localidadService = new LocalidadServiceImpl();

    // Service utilizado para cargar ciudades (combo en formulario y filtro en listado).
    private final DistritoService ciudadService = new DistritoServiceImpl();

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
                default -> response.sendRedirect(request.getContextPath() + "/localidades?accion=listar");
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

    // LISTAR (con filtro por distrito y búsqueda por nombre).
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga lista de ciudades para filtro y formulario.
        cargarDistritoes(request);

        // 2. Lee filtro opcional por distrito y texto de búsqueda.
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));

        // Mantener compatibilidad: si viene "q" lo tomamos, pero preferimos "filtro"
        String filtro = request.getParameter("filtro");
        if (filtro == null) {
            filtro = request.getParameter("q");
        }

        // 3. Guarda los valores para que el JSP los mantenga seleccionados.
        request.setAttribute("idDistrito", idDistrito);
        request.setAttribute("filtro", filtro);

        // 4. Define la lista base (completa o filtrada por distrito).
        List<Localidad> listaBase = (idDistrito == null)
                ? localidadService.listarTodos()
                : localidadService.listarPorDistrito(idDistrito);

        // 5. Si NO hay idDistrito pero sí filtro, usamos SQL directo.
        if (idDistrito == null && filtro != null && !filtro.trim().isBlank()) {
            List<Localidad> listaSql = localidadService.buscarPorNombreParcial(filtro);
            request.setAttribute("listaLocalidades", listaSql);
            request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_listar.jsp").forward(request, response);
            return;
        }

        // 6. Aplica búsqueda por nombre (opcional) sobre la lista base.
        if (filtro != null) {
            String f = filtro.trim();
            if (!f.isBlank()) {
                String needle = f.toUpperCase(Locale.ROOT);

                listaBase = listaBase.stream()
                        .filter(l -> l.getNombre() != null
                        && l.getNombre().toUpperCase(Locale.ROOT).contains(needle))
                        .collect(Collectors.toList());
            }
        }

        // 7. Envía la lista final a la vista.
        request.setAttribute("listaLocalidades", listaBase);

        // 8. Renderiza el listado de localidades.
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_listar.jsp").forward(request, response);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Para no duplicar lógica, reutilizamos listar (que ya soporta filtro e idDistrito).
        listar(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga ciudades activas para poblar el combo del formulario.
        cargarDistritoes(request);

        // 2. Envía un Localidad vacío a la vista para llenado inicial.
        request.setAttribute("localidad", new Localidad());

        // 3. Renderiza el formulario de localidad.
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_form.jsp").forward(request, response);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga ciudades activas para poblar el combo del formulario.
        cargarDistritoes(request);

        // 2. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 3. Busca la localidad por id usando el service.
        Optional<Localidad> opt = localidadService.buscarPorId(id);

        // 4. Si no existe, se corta el flujo con error.
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe una localidad con id: " + id);
        }

        // 5. Coloca la localidad encontrada en request para precargar el formulario.
        request.setAttribute("localidad", opt.get());

        // 6. Renderiza el formulario con datos cargados.
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_form.jsp").forward(request, response);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado de la localidad a activo usando la capa service.
        localidadService.activar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        String url = construirUrlRetornoListado(request);

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado de la localidad a inactivo usando la capa service.
        localidadService.desactivar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        String url = construirUrlRetornoListado(request);

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee parámetros del formulario (idLocalidad puede venir vacío en creación).
        Long idLocalidad = parseLongNullable(request.getParameter("idLocalidad"));
        Long idDistrito = parseLong(request.getParameter("idDistrito"));
        String nombre = request.getParameter("nombre");

        // FIX: Toggle activo/inactivo como en País/Departamento:
        // - En creación: true por defecto
        // - En edición: true si viene el checkbox; false si no viene
        boolean activo;
        if (idLocalidad == null) {
            activo = true;
        } else {
            activo = request.getParameter("activo") != null;
        }

        // 2. Construye el objeto Localidad a partir de los parámetros.
        Localidad l = new Localidad();
        l.setIdLocalidad(idLocalidad);
        l.setIdDistrito(idDistrito);
        l.setNombre(nombre);
        l.setActivo(activo);

        // 3. Si no tiene id, se crea; si tiene id, se actualiza.
        if (idLocalidad == null) {
            localidadService.crear(l);
        } else {
            localidadService.actualizar(l);
        }

        // 4. Redirige al listado preservando filtros.
        String url = request.getContextPath() + "/localidades?accion=listar";

        Long idDistritoFiltro = parseLongNullable(request.getParameter("idDistrito"));
        if (idDistritoFiltro != null) url += "&idDistrito=" + idDistritoFiltro;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        response.sendRedirect(url);
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ========== 

    // CARGAR CIUDADES (COMBO/FILTRO).
    private void cargarDistritoes(HttpServletRequest request) {

        // Preferible listarActivos para combos; si no lo tenés, usá listarTodos()
        List<Distrito> ciudades = ciudadService.listarActivos();

        // FIX: la vista debe recibir "distritos" (no "ciudades") para que sea consistente
        request.setAttribute("distritos", ciudades);
    }

    // REENVIAR FORMULARIO CON DATOS (EN CASO DE ERROR EN GUARDAR).
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga ciudades para poblar el combo del formulario.
        cargarDistritoes(request);

        // 2. Reconstruye el objeto Localidad con los parámetros del request.
        Long idLocalidad = parseLongNullable(request.getParameter("idLocalidad"));
        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        String nombre = request.getParameter("nombre");

        // FIX: misma lógica de toggle que en guardar()
        boolean activo;
        if (idLocalidad == null) {
            activo = true;
        } else {
            activo = request.getParameter("activo") != null;
        }

        Localidad l = new Localidad();
        l.setIdLocalidad(idLocalidad);
        l.setIdDistrito(idDistrito);
        l.setNombre(nombre);
        l.setActivo(activo);

        // 3. Envía el objeto al JSP para que no se pierdan los datos ingresados.
        request.setAttribute("localidad", l);

        // 4. Renderiza el formulario nuevamente con error.
        request.getRequestDispatcher("/WEB-INF/views/localidades/localidad_form.jsp").forward(request, response);
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

    // FIX: URL de retorno al listado preservando filtros (para activar/desactivar)
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/localidades?accion=listar";

        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        if (idDistrito != null) url += "&idDistrito=" + idDistrito;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        return url;
    }
}
