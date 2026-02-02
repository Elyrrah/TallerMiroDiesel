/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
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
import py.taller.tallermirodiesel.model.TipoDocumento;
import py.taller.tallermirodiesel.model.enums.TipoDocumentoAplicaA;
import py.taller.tallermirodiesel.service.TipoDocumentoService;
import py.taller.tallermirodiesel.service.impl.TipoDocumentoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "TipoDocumentoServlet", urlPatterns = {"/tipos-documento"})
// Bloque: Mapeo del servlet (todas las acciones de TipoDocumento entran por /tipos-documento).
public class TipoDocumentoServlet extends HttpServlet {

    // Service usado por el controlador para ejecutar la lógica de negocio de TipoDocumento.
    private TipoDocumentoService tipoDocumentoService;

    // CICLO DE VIDA DEL SERVLET.
    @Override
    public void init() {
        this.tipoDocumentoService = new TipoDocumentoServiceImpl();
    }

    // ========== ========== ========== ========== ========== 
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué caso ejecutar.
        // AJUSTE: ahora usamos "action" en lugar de "accion" para unificar en todo el proyecto.
        String accion = req.getParameter("action");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        // AJUSTE: ahora el default es "list" en lugar de "listar".
        if (accion == null || accion.isBlank()) accion = "list";

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
        try {
            switch (accion) {
                case "new" -> mostrarFormularioNuevo(req, resp);        // antes: "nuevo"
                case "edit" -> mostrarFormularioEditar(req, resp);      // antes: "editar"
                case "activate" -> activar(req, resp);                  // antes: "activar"
                case "deactivate" -> desactivar(req, resp);             // antes: "desactivar"
                case "search" -> buscar(req, resp);                     // antes: "buscar"
                case "list" -> listar(req, resp);                       // antes: "listar"
                default -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    // ========== ========== ========== ========== ========== 
    // MANEJO DE POST (ACCIONES QUE MODIFICAN DATOS).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué operación ejecutar.
        // AJUSTE: ahora usamos "action" también en POST.
        String accion = req.getParameter("action");

        // 2. Si no viene acción, se define un default para evitar nulls.
        // AJUSTE: ahora el default para POST será "save".
        if (accion == null || accion.isBlank()) accion = "save";

        // 3. Router de acciones POST.
        try {
            switch (accion) {
                case "save" -> guardar(req, resp); // antes: "guardar"
                default -> resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=list");
            }
        } catch (RuntimeException e) {

            // 4. Avisa en caso de error.
            req.setAttribute("error", e.getMessage());

            // 5. Re-render del formulario correspondiente.
            // Re-cargamos opciones del enum para el select.
            req.setAttribute("aplicaAOptions", TipoDocumentoAplicaA.values());

            // Volvemos al mismo form (con el objeto reconstruido).
            TipoDocumento td = new TipoDocumento();
            Long idTipoDocumento = parseLong(req.getParameter("idTipoDocumento"));
            td.setIdTipoDocumento(idTipoDocumento);
            td.setNombre(req.getParameter("nombre"));
            td.setCodigo(req.getParameter("codigo"));

            String aplicaA = req.getParameter("aplicaA");
            if (aplicaA != null && !aplicaA.isBlank()) {
                try {
                    td.setAplicaA(TipoDocumentoAplicaA.valueOf(aplicaA.trim().toUpperCase()));
                } catch (Exception ignored) {
                }
            }

            td.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("tipoDocumento", td);
            req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
        }
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES GET
    // ========== ========== ========== 

    // LISTADO.
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 0. Lee el filtro opcional (para búsquedas automáticas por SQL).
        String filtro = req.getParameter("filtro");

        // 1. Pone la lista en request para que el JSP la recorra.
        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("tiposDocumento", tipoDocumentoService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("tiposDocumento", tipoDocumentoService.listarTodos());
        }

        // 2. Renderiza la vista protegida dentro de WEB-INF.
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_listar.jsp").forward(req, resp);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro filtro.
        String filtro = req.getParameter("filtro");

        // 2. Devuelve una lista filtrada (si filtro vacío, devuelve lista completa).
        List<TipoDocumento> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = tipoDocumentoService.listarTodos();
        } else {
            // Si querés búsqueda automática por código exacto, descomentá esto:
            // Optional<TipoDocumento> porCodigo = tipoDocumentoService.buscarPorCodigo(filtro.trim().toUpperCase());
            // if (porCodigo.isPresent()) { lista = List.of(porCodigo.get()); } else { lista = tipoDocumentoService.buscarPorNombreParcial(filtro); }

            lista = tipoDocumentoService.buscarPorNombreParcial(filtro);
        }

        // 3. Envía la lista al JSP de listado.
        req.setAttribute("tiposDocumento", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        // 4. Renderiza el listado.
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Envía un TipoDocumento vacío a la vista para llenado inicial.
        req.setAttribute("tipoDocumento", new TipoDocumento());

        // 2. Envía opciones del enum para el select (combo).
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaA.values());

        // 3. Renderiza el formulario.
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Convierte el parámetro "id" a Long, si es null se lanza un error.
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca el TipoDocumento por id usando el service.
        Optional<TipoDocumento> tipoDocumento = tipoDocumentoService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (tipoDocumento.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + id);
        }

        // 4. Coloca el TipoDocumento encontrado en request para precargar el formulario.
        req.setAttribute("tipoDocumento", tipoDocumento.get());

        // 5. Envía opciones del enum para el select (combo).
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaA.values());

        // 6. Renderiza el mismo JSP de formulario, pero con datos cargados.
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para activar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede activar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del TipoDocumento a activo usando la capa service.
        tipoDocumentoService.activar(id);

        // 4. Redirige al listado tras la operación.
        // AJUSTE: ahora usamos action=list
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=list");
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para desactivar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede desactivar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del TipoDocumento a inactivo usando la capa service.
        tipoDocumentoService.desactivar(id);

        // 4. Redirige al listado tras la operación.
        // AJUSTE: ahora usamos action=list
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=list");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TipoDocumento td = new TipoDocumento();

        // Si viene idTipoDocumento, se trata de edición; si no, es creación
        Long idTipoDocumento = parseLong(req.getParameter("idTipoDocumento"));
        td.setIdTipoDocumento(idTipoDocumento);

        td.setNombre(req.getParameter("nombre"));
        td.setCodigo(req.getParameter("codigo"));

        String aplicaA = req.getParameter("aplicaA");
        if (aplicaA != null && !aplicaA.isBlank()) {
            td.setAplicaA(TipoDocumentoAplicaA.valueOf(aplicaA.trim().toUpperCase()));
        }

        // En creación: activo por defecto true; en edición: tomar el valor del form
        if (idTipoDocumento == null) {
            td.setActivo(true);
            tipoDocumentoService.crear(td);
        } else {
            td.setActivo("true".equals(req.getParameter("activo")));
            tipoDocumentoService.actualizar(td);
        }

        // AJUSTE: ahora usamos action=list
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=list");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ========== 

    // PARSEO
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
