/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package py.taller.tallermirodiesel.controller;

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
import py.taller.tallermirodiesel.model.Marca;
import py.taller.tallermirodiesel.model.Modelo;
import py.taller.tallermirodiesel.service.MarcaService;
import py.taller.tallermirodiesel.service.ModeloService;
import py.taller.tallermirodiesel.service.impl.MarcaServiceImpl;
import py.taller.tallermirodiesel.service.impl.ModeloServiceImpl;

/**
 *
 * @author elyrr
 */
@WebServlet(name = "ModeloServlet", urlPatterns = {"/modelos"})
// Bloque: Mapeo del servlet (todas las acciones de Modelo entran por /modelos).
public class ModeloServlet extends HttpServlet {

    // Service utilizado por el Servlet para acceder a la capa de negocio.
    private final ModeloService modeloService = new ModeloServiceImpl();

    // Service utilizado para cargar marcas (combo en formulario y filtro en listado).
    private final MarcaService marcaService = new MarcaServiceImpl();

    // ========== ========== ========== ========== ========== 
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué caso ejecutar.
        // AJUSTE: ahora usamos "action" en lugar de "accion" para unificar en todo el proyecto.
        String accion = request.getParameter("action");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        // AJUSTE: ahora el default es "list" en lugar de "listar".
        if (accion == null || accion.isBlank()) {
            accion = "list";
        }

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
        try {
            switch (accion) {
                case "new" -> mostrarFormularioNuevo(request, response);        // antes: "nuevo"
                case "edit" -> mostrarFormularioEditar(request, response);      // antes: "editar"
                case "activate" -> activar(request, response);                  // antes: "activar"
                case "deactivate" -> desactivar(request, response);             // antes: "desactivar"
                case "search" -> buscar(request, response);                     // antes: "buscar"
                case "list" -> listar(request, response);                       // antes: "listar"
                default -> listar(request, response);
            }

        // 5. Manejo simple de errores: setear mensaje y volver al listado
        } catch (RuntimeException e) {
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
        // AJUSTE: ahora usamos "action" también en POST.
        String accion = request.getParameter("action");

        // 2. Si no viene acción, se asume "guardar" como comportamiento por defecto.
        // AJUSTE: ahora el default para POST será "save".
        if (accion == null || accion.isBlank()) {
            accion = "save";
        }

        // 3. Router de acciones POST.
        try {
            switch (accion) {
                case "save" -> guardar(request, response); // antes: "guardar"
                default -> response.sendRedirect(request.getContextPath() + "/modelos?action=list");
            }

        // 4. Si falla guardar, devolvemos al formulario con el error y los valores cargados
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES GET
    // ========== ========== ========== 

    // LISTAR (con filtro por marca y búsqueda por nombre).
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga lista de marcas para filtro y formulario.
        cargarMarcas(request);

        // 2. Lee filtro opcional por marca y texto de búsqueda.
        Long idMarca = parseLongNullable(request.getParameter("idMarca"));

        // Mantener compatibilidad: si viene "q" lo tomamos, pero preferimos "filtro"
        String filtro = request.getParameter("filtro");
        if (filtro == null) {
            filtro = request.getParameter("q");
        }

        // 3. Guarda los valores para que el JSP los mantenga seleccionados.
        request.setAttribute("idMarca", idMarca);
        request.setAttribute("filtro", filtro);

        // 4. Define la lista base (completa o filtrada por marca).
        List<Modelo> listaBase = (idMarca == null)
                ? modeloService.listarTodos()
                : modeloService.listarPorMarca(idMarca);

        // 5. Si NO hay idMarca pero sí filtro, usamos SQL directo.
        if (idMarca == null && filtro != null && !filtro.trim().isBlank()) {
            List<Modelo> listaSql = modeloService.buscarPorNombreParcial(filtro);
            request.setAttribute("listaModelos", listaSql);
            request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_listar.jsp").forward(request, response);
            return;
        }

        // 6. Aplica búsqueda por nombre (opcional) sobre la lista base.
        if (filtro != null) {
            String f = filtro.trim();
            if (!f.isBlank()) {
                String needle = f.toUpperCase(Locale.ROOT);

                listaBase = listaBase.stream()
                        .filter(m -> m.getNombre() != null
                                && m.getNombre().toUpperCase(Locale.ROOT).contains(needle))
                        .collect(Collectors.toList());
            }
        }

        // 7. Envía la lista final a la vista.
        request.setAttribute("listaModelos", listaBase);

        // 8. Renderiza el listado de modelos.
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_listar.jsp").forward(request, response);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Para no duplicar lógica, reutilizamos listar (que ya soporta filtro e idMarca).
        listar(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga marcas activas para poblar el combo del formulario.
        cargarMarcas(request);

        // 2. Envía un Modelo vacío a la vista para llenado inicial.
        request.setAttribute("modelo", new Modelo());

        // 3. Renderiza el formulario de modelo.
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_form.jsp").forward(request, response);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga marcas activas para poblar el combo del formulario.
        cargarMarcas(request);

        // 2. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 3. Busca el modelo por id usando el service.
        Optional<Modelo> opt = modeloService.buscarPorId(id);

        // 4. Si no existe, se corta el flujo con error.
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe un modelo con id: " + id);
        }

        // 5. Coloca el modelo encontrado en request para precargar el formulario.
        request.setAttribute("modelo", opt.get());

        // 6. Renderiza el formulario con datos cargados.
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_form.jsp").forward(request, response);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado del modelo a activo usando la capa service.
        modeloService.activar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        // AJUSTE: ahora usamos action=list
        String url = request.getContextPath() + "/modelos?action=list";

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        if (idMarca != null) url += "&idMarca=" + idMarca;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee el id desde parámetros y valida.
        Long id = parseLong(request.getParameter("id"));

        // 2. Cambia el estado del modelo a inactivo usando la capa service.
        modeloService.desactivar(id);

        // 3. Reconstruye la URL preservando filtros si venían en la petición.
        // AJUSTE: ahora usamos action=list
        String url = request.getContextPath() + "/modelos?action=list";

        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        if (idMarca != null) url += "&idMarca=" + idMarca;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        // 4. Redirige al listado tras la operación.
        response.sendRedirect(url);
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Lee parámetros del formulario (idModelo puede venir vacío en creación).
        Long idModelo = parseLongNullable(request.getParameter("idModelo"));
        Long idMarca = parseLong(request.getParameter("idMarca"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        // 2. Construye el objeto Modelo a partir de los parámetros.
        Modelo m = new Modelo();
        m.setIdModelo(idModelo);
        m.setIdMarca(idMarca);
        m.setNombre(nombre);
        m.setActivo(activo);

        // 3. Si no tiene id, se crea; si tiene id, se actualiza.
        if (idModelo == null) {
            modeloService.crear(m);
        } else {
            modeloService.actualizar(m);
        }

        // 4. Redirige al listado preservando filtros.
        // AJUSTE: ahora usamos action=list
        String url = request.getContextPath() + "/modelos?action=list";

        Long idMarcaFiltro = parseLongNullable(request.getParameter("idMarcaFiltro"));
        if (idMarcaFiltro != null) url += "&idMarca=" + idMarcaFiltro;

        String filtro = request.getParameter("filtro");
        if (filtro == null) filtro = request.getParameter("q");
        if (filtro != null && !filtro.isBlank()) {
            url += "&filtro=" + URLEncoder.encode(filtro, StandardCharsets.UTF_8);
        }

        response.sendRedirect(url);
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ========== 

    // CARGAR MARCAS (COMBO/FILTRO).
    private void cargarMarcas(HttpServletRequest request) {

        // Preferible listarActivos para combos; si no lo tenés, usá listarTodos()
        List<Marca> marcas = marcaService.listarActivos();
        request.setAttribute("marcas", marcas);
    }

    // REENVIAR FORMULARIO CON DATOS (EN CASO DE ERROR EN GUARDAR).
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga marcas para poblar el combo del formulario.
        cargarMarcas(request);

        // 2. Reconstruye el objeto Modelo con los parámetros del request.
        Long idModelo = parseLongNullable(request.getParameter("idModelo"));
        Long idMarca = parseLongNullable(request.getParameter("idMarca"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        Modelo m = new Modelo();
        m.setIdModelo(idModelo);
        m.setIdMarca(idMarca);
        m.setNombre(nombre);
        m.setActivo(activo);

        // 3. Envía el objeto al JSP para que no se pierdan los datos ingresados.
        request.setAttribute("modelo", m);

        // 4. Renderiza el formulario nuevamente con error.
        request.getRequestDispatcher("/WEB-INF/views/modelos/modelo_form.jsp").forward(request, response);
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
