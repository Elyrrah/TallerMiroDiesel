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
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import com.tallermirodiesel.dto.ClientePersonaListadoDTO;
import com.tallermirodiesel.model.Cliente;
import com.tallermirodiesel.model.ClienteDocumento;
import com.tallermirodiesel.model.ClienteEmpresa;
import com.tallermirodiesel.model.ClientePersona;
import com.tallermirodiesel.model.Distrito;
import com.tallermirodiesel.model.Localidad;
import com.tallermirodiesel.model.SesionDeUsuario;
import com.tallermirodiesel.model.TipoDocumento;
import com.tallermirodiesel.service.ClienteDocumentoService;
import com.tallermirodiesel.service.ClienteEmpresaService;
import com.tallermirodiesel.service.ClienteListadoService;
import com.tallermirodiesel.service.ClientePersonaService;
import com.tallermirodiesel.service.ClienteService;
import com.tallermirodiesel.service.DistritoService;
import com.tallermirodiesel.service.LocalidadService;
import com.tallermirodiesel.service.TipoDocumentoService;
import com.tallermirodiesel.service.impl.ClienteDocumentoServiceImpl;
import com.tallermirodiesel.service.impl.ClienteEmpresaServiceImpl;
import com.tallermirodiesel.service.impl.ClienteListadoServiceImpl;
import com.tallermirodiesel.service.impl.ClientePersonaServiceImpl;
import com.tallermirodiesel.service.impl.ClienteServiceImpl;
import com.tallermirodiesel.service.impl.DistritoServiceImpl;
import com.tallermirodiesel.service.impl.LocalidadServiceImpl;
import com.tallermirodiesel.service.impl.TipoDocumentoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "ClienteServlet", urlPatterns = {"/clientes"})
public class ClienteServlet extends HttpServlet {

    // Inicializa todos los servicios necesarios para la gestión de clientes
    private final ClienteService clienteService = new ClienteServiceImpl();
    private final ClienteListadoService clienteListadoService = new ClienteListadoServiceImpl();
    private final DistritoService distritoService = new DistritoServiceImpl();
    private final LocalidadService localidadService = new LocalidadServiceImpl();
    private final TipoDocumentoService tipoDocumentoService = new TipoDocumentoServiceImpl();
    private final ClientePersonaService clientePersonaService = new ClientePersonaServiceImpl();
    private final ClienteEmpresaService clienteEmpresaService = new ClienteEmpresaServiceImpl();
    private final ClienteDocumentoService clienteDocumentoService = new ClienteDocumentoServiceImpl();

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
                case "nuevo"  -> mostrarFormularioNuevo(request, response);
                case "buscar" -> listar(request, response);
                case "listar" -> listar(request, response);
                default       -> listar(request, response);
            }
        } catch (RuntimeException e) {
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
                case "guardar"      -> guardar(request, response);
                case "toggleActivo" -> cambiarEstadoActivo(request, response);
                default             -> response.sendRedirect(request.getContextPath() + "/clientes?action=listar");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());

            // Si hay una causa subyacente, la muestra también
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                request.setAttribute("errorDetalle", e.getCause().getMessage());
            }

            // Para toggleActivo, redirige al listado; para guardar, vuelve al formulario
            if ("toggleActivo".equalsIgnoreCase(action)) {
                response.sendRedirect(construirUrlRetornoListado(request));
                return;
            }

            reenviarFormularioConDatos(request, response);
        }
    }

    /**
     * Muestra el listado de clientes (personas y empresas) con filtros opcionales
     */
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lee el filtro de búsqueda (acepta 'q' o 'filtro')
        String q = request.getParameter("q");
        if (q == null) q = request.getParameter("filtro");

        // Lee el filtro de estado (ACTIVOS, INACTIVOS, TODOS)
        String estado = request.getParameter("estado");

        // Convierte el estado a boolean para el filtro
        Boolean activo = null;
        if (estado != null) {
            if ("ACTIVOS".equalsIgnoreCase(estado)) activo = true;
            if ("INACTIVOS".equalsIgnoreCase(estado)) activo = false;
        }

        // Envía los filtros actuales a la vista
        request.setAttribute("q", q);
        request.setAttribute("estado", estado);

        // Obtiene las listas de clientes (personas y empresas) según los filtros
        List<ClientePersonaListadoDTO> listaClientesPersona = clienteListadoService.listarPersonas(q, activo);
        List<ClienteEmpresaListadoDTO> listaClientesEmpresa = clienteListadoService.listarEmpresas(q, activo);

        request.setAttribute("listaClientesPersona", listaClientesPersona);
        request.setAttribute("listaClientesEmpresa", listaClientesEmpresa);

        // Redirige a la vista de listado
        request.getRequestDispatcher("/WEB-INF/views/clientes/cliente_listar.jsp").forward(request, response);
    }

    /**
     * Muestra el formulario para crear un nuevo cliente
     */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Carga los combos necesarios (distritos, localidades, tipos de documento)
        cargarCombos(request);
        // Crea un objeto Cliente vacío para el formulario
        request.setAttribute("cliente", new Cliente());
        // Por defecto, el tipo es PERSONA
        request.setAttribute("tipo", "PERSONA");
        request.getRequestDispatcher("/WEB-INF/views/clientes/cliente_form.jsp").forward(request, response);
    }

    /**
     * Cambia el estado activo/inactivo de un cliente (toggle)
     */
    private void cambiarEstadoActivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        // Lee el estado actual y lo invierte
        boolean activoActual = Boolean.parseBoolean(request.getParameter("activo_actual"));
        boolean nuevoActivo = !activoActual;

        // Llama al servicio para cambiar el estado
        clienteService.setActivo(id, nuevoActivo);
        // Redirige al listado preservando los filtros
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    /**
     * Guarda un nuevo cliente (puede ser PERSONA o EMPRESA)
     * Este método coordina la creación del cliente base + sus datos específicos + documentos
     */
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Valida que se haya seleccionado el tipo de cliente
        String tipo = request.getParameter("tipo");
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de cliente (PERSONA o EMPRESA).");
        }

        // 2. Lee el usuario logueado desde la sesión
        HttpSession sesion = request.getSession(false);
        SesionDeUsuario usuarioSesion = (SesionDeUsuario) sesion.getAttribute("usuarioSesion");

        // 3. Crea el objeto Cliente base con datos comunes
        Cliente c = new Cliente();
        c.setIdUsuarioCreador(usuarioSesion.getIdUsuario());
        c.setTelefono(request.getParameter("telefono"));
        c.setIdDistrito(parseLongNullableEstricto(request.getParameter("id_distrito")));
        c.setIdLocalidad(parseLongNullableEstricto(request.getParameter("id_localidad")));
        c.setActivo(true); // Los nuevos clientes siempre están activos

        // 4. Guarda el cliente base y obtiene el ID generado
        Long idCreado = clienteService.guardar(c);
        if (idCreado == null) {
            throw new RuntimeException("No se pudo guardar el cliente.");
        }

        // 5. Guarda los datos específicos según el tipo de cliente
        if ("PERSONA".equalsIgnoreCase(tipo)) {
            // Valida campos obligatorios para personas
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String apodo = request.getParameter("apodo");

            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("Nombre es obligatorio.");
            }
            if (apellido == null || apellido.isBlank()) {
                throw new IllegalArgumentException("Apellido es obligatorio.");
            }

            // Crea y guarda los datos de persona
            ClientePersona p = new ClientePersona();
            p.setIdCliente(idCreado);
            p.setNombre(nombre);
            p.setApellido(apellido);
            p.setApodo(apodo);

            boolean ok = clientePersonaService.guardar(p);
            if (!ok) {
                throw new RuntimeException("No se pudieron guardar los datos de la persona.");
            }

        } else if ("EMPRESA".equalsIgnoreCase(tipo)) {
            // Valida campos obligatorios para empresas
            String razonSocial = request.getParameter("razon_social");
            String nombreFantasia = request.getParameter("nombre_fantasia");

            if (razonSocial == null || razonSocial.isBlank()) {
                throw new IllegalArgumentException("Razón Social es obligatoria.");
            }

            // Crea y guarda los datos de empresa
            ClienteEmpresa e = new ClienteEmpresa();
            e.setIdCliente(idCreado);
            e.setRazonSocial(razonSocial);
            e.setNombreFantasia(nombreFantasia);

            boolean ok = clienteEmpresaService.guardar(e);
            if (!ok) {
                throw new RuntimeException("No se pudieron guardar los datos de la empresa.");
            }

        } else {
            throw new IllegalArgumentException("Tipo inválido. Use PERSONA o EMPRESA.");
        }

        // 6. Si se proporcionó documento, lo guarda
        Long idTipoDocumento = parseLongNullableEstricto(request.getParameter("id_tipo_documento"));
        String numeroDocumento = request.getParameter("numero_documento");

        // Verifica si el documento es principal
        boolean principalDocumento = false;
        String principalParam = request.getParameter("principal_documento");
        if (principalParam != null) {
            principalDocumento = principalParam.equalsIgnoreCase("true")
                    || principalParam.equalsIgnoreCase("on")
                    || principalParam.equalsIgnoreCase("1")
                    || principalParam.equalsIgnoreCase("yes");
        }

        // Si hay tipo de documento y número, guarda el documento
        if (idTipoDocumento != null && numeroDocumento != null && !numeroDocumento.trim().isBlank()) {
            ClienteDocumento cd = new ClienteDocumento();
            cd.setIdCliente(idCreado);
            cd.setIdTipoDocumento(idTipoDocumento);
            cd.setNumero(numeroDocumento.trim());
            cd.setPrincipal(principalDocumento);
            cd.setActivo(true);

            boolean okDoc = clienteDocumentoService.guardar(cd);
            if (!okDoc) {
                throw new RuntimeException("No se pudo guardar el documento del cliente.");
            }
        }

        // 7. Redirige al listado después de guardar
        response.sendRedirect(request.getContextPath() + "/clientes?action=listar");
    }

    /**
     * Carga todos los combos necesarios para el formulario de cliente
     */
    private void cargarCombos(HttpServletRequest request) {
        // Carga distritos activos
        List<Distrito> distritos = distritoService.listarActivos();
        request.setAttribute("distritos", distritos);

        // Carga localidades activas
        List<Localidad> localidades = localidadService.listarActivos();
        request.setAttribute("localidades", localidades);

        // Carga tipos de documento activos
        List<TipoDocumento> tiposDocumento = tipoDocumentoService.listarActivos();
        request.setAttribute("tiposDocumento", tiposDocumento);
    }

    /**
     * Reenvía al formulario con los datos ingresados (en caso de error)
     */
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Carga los combos necesarios
        cargarCombos(request);

        // Reconstruye el objeto Cliente con los parámetros del request
        Cliente c = new Cliente();
        c.setTelefono(request.getParameter("telefono"));
        c.setIdDistrito(parseLongNullableEstricto(request.getParameter("id_distrito")));
        c.setIdLocalidad(parseLongNullableEstricto(request.getParameter("id_localidad")));

        request.setAttribute("cliente", c);

        // Recupera el tipo de cliente
        String tipo = request.getParameter("tipo");
        request.setAttribute("tipo", tipo);

        // Recupera los campos específicos según el tipo
        if ("PERSONA".equalsIgnoreCase(tipo)) {
            request.setAttribute("nombre", request.getParameter("nombre"));
            request.setAttribute("apellido", request.getParameter("apellido"));
            request.setAttribute("apodo", request.getParameter("apodo"));
        } else if ("EMPRESA".equalsIgnoreCase(tipo)) {
            request.setAttribute("razon_social", request.getParameter("razon_social"));
            request.setAttribute("nombre_fantasia", request.getParameter("nombre_fantasia"));
        }

        // Recupera los datos del documento
        request.setAttribute("id_tipo_documento", parseLongNullableEstricto(request.getParameter("id_tipo_documento")));
        request.setAttribute("numero_documento", request.getParameter("numero_documento"));
        request.setAttribute("principal_documento", request.getParameter("principal_documento"));

        // Renderiza el formulario nuevamente con los datos
        request.getRequestDispatcher("/WEB-INF/views/clientes/cliente_form.jsp").forward(request, response);
    }

    /**
     * Convierte un String a Long de forma obligatoria (lanza excepción si falla)
     */
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

    /**
     * Convierte un String a Long de forma opcional estricta
     * Retorna null si está vacío, pero lanza excepción si el formato es inválido
     */
    private Long parseLongNullableEstricto(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long n = Long.valueOf(value);
            if (n <= 0) {
                throw new IllegalArgumentException("El parámetro numérico debe ser mayor a 0: " + value);
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parámetro numérico inválido: " + value);
        }
    }

    /**
     * Resuelve el nombre completo de un cliente (persona o empresa)
     * @param idCliente ID del cliente
     * @return Nombre completo del cliente o null si no se encuentra
     */
    private String resolverNombreCliente(Long idCliente) {
        if (idCliente == null) return null;

        // Intenta buscar como persona
        Optional<ClientePersona> pOpt = clientePersonaService.buscarPorIdCliente(idCliente);
        if (pOpt.isPresent()) {
            ClientePersona p = pOpt.get();

            String nombre = (p.getNombre() != null) ? p.getNombre().trim() : "";
            String apellido = (p.getApellido() != null) ? p.getApellido().trim() : "";
            String full = (nombre + " " + apellido).trim();

            if (!full.isBlank()) return full;

            if (p.getApodo() != null && !p.getApodo().trim().isBlank()) {
                return p.getApodo().trim();
            }
            return "PERSONA";
        }

        // Intenta buscar como empresa
        Optional<ClienteEmpresa> eOpt = clienteEmpresaService.buscarPorIdCliente(idCliente);
        if (eOpt.isPresent()) {
            ClienteEmpresa e = eOpt.get();

            if (e.getNombreFantasia() != null && !e.getNombreFantasia().trim().isBlank()) {
                return e.getNombreFantasia().trim();
            }
            if (e.getRazonSocial() != null && !e.getRazonSocial().trim().isBlank()) {
                return e.getRazonSocial().trim();
            }
            return "EMPRESA";
        }

        return null;
    }

    /**
     * Construye la URL de retorno al listado preservando los filtros
     */
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/clientes?action=listar";

        // Preserva el filtro de búsqueda
        String q = request.getParameter("q");
        if (q != null && !q.isBlank()) {
            url += "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        }

        // Preserva el filtro de estado
        String estado = request.getParameter("estado");
        if (estado != null && !estado.isBlank()) {
            url += "&estado=" + URLEncoder.encode(estado, StandardCharsets.UTF_8);
        }

        return url;
    }
}