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

    private final ClienteService clienteService = new ClienteServiceImpl();
    private final ClienteListadoService clienteListadoService = new ClienteListadoServiceImpl();
    private final DistritoService distritoService = new DistritoServiceImpl();
    private final LocalidadService localidadService = new LocalidadServiceImpl();
    private final TipoDocumentoService tipoDocumentoService = new TipoDocumentoServiceImpl();
    private final ClientePersonaService clientePersonaService = new ClientePersonaServiceImpl();
    private final ClienteEmpresaService clienteEmpresaService = new ClienteEmpresaServiceImpl();
    private final ClienteDocumentoService clienteDocumentoService = new ClienteDocumentoServiceImpl();

    // Orquestación de peticiones GET para navegación y filtrado avanzado
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
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

    // Procesamiento de operaciones de escritura y cambios de estado de clientes
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar"      -> guardar(request, response);
                case "toggleActivo" -> cambiarEstadoActivo(request, response);
                default             -> response.sendRedirect(request.getContextPath() + "/clientes?action=listar");
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());

            if (e.getCause() != null && e.getCause().getMessage() != null) {
                request.setAttribute("errorDetalle", e.getCause().getMessage());
            }

            if ("toggleActivo".equalsIgnoreCase(action)) {
                response.sendRedirect(construirUrlRetornoListado(request));
                return;
            }

            reenviarFormularioConDatos(request, response);
        }
    }

    // Generación del listado dual (Personas y Empresas) con filtros de búsqueda y estado
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String q = request.getParameter("q");
        if (q == null) q = request.getParameter("filtro");

        String estado = request.getParameter("estado");

        Boolean activo = null;
        if (estado != null) {
            if ("ACTIVOS".equalsIgnoreCase(estado)) activo = true;
            if ("INACTIVOS".equalsIgnoreCase(estado)) activo = false;
        }

        request.setAttribute("q", q);
        request.setAttribute("estado", estado);

        List<ClientePersonaListadoDTO> listaClientesPersona = clienteListadoService.listarPersonas(q, activo);
        List<ClienteEmpresaListadoDTO> listaClientesEmpresa = clienteListadoService.listarEmpresas(q, activo);

        request.setAttribute("listaClientesPersona", listaClientesPersona);
        request.setAttribute("listaClientesEmpresa", listaClientesEmpresa);

        request.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/clientes/cliente_listar.jsp").forward(request, response);
    }

    // Inicialización del formulario de cliente con sus dependencias geográficas y documentales
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarCombos(request);
        request.setAttribute("cliente", new Cliente());
        request.setAttribute("tipo", "PERSONA");

        request.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/clientes/cliente_form.jsp").forward(request, response);
    }

    // Lógica para la alternancia de estado (Activo/Inactivo) preservando la navegación actual
    private void cambiarEstadoActivo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        boolean activoActual = Boolean.parseBoolean(request.getParameter("activo_actual"));
        boolean nuevoActivo = !activoActual;

        clienteService.setActivo(id, nuevoActivo);
        response.sendRedirect(construirUrlRetornoListado(request));
    }

    // Transacción manual para la creación secuencial de Cliente, Perfil (Persona/Empresa) y Documento
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String tipo = request.getParameter("tipo");
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de cliente (PERSONA o EMPRESA).");
        }

        HttpSession sesion = request.getSession(false);
        SesionDeUsuario usuarioSesion = (SesionDeUsuario) sesion.getAttribute("usuarioSesion");

        Cliente c = new Cliente();
        c.setIdUsuarioCreador(usuarioSesion.getIdUsuario());
        c.setTelefono(request.getParameter("telefono"));
        c.setIdDistrito(parseLongNullableEstricto(request.getParameter("id_distrito")));
        c.setIdLocalidad(parseLongNullableEstricto(request.getParameter("id_localidad")));
        c.setActivo(true);

        Long idCreado = clienteService.guardar(c);
        if (idCreado == null) {
            throw new RuntimeException("No se pudo guardar el cliente.");
        }

        if ("PERSONA".equalsIgnoreCase(tipo)) {
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String apodo = request.getParameter("apodo");

            if (nombre == null || nombre.isBlank()) {
                throw new IllegalArgumentException("Nombre es obligatorio.");
            }
            if (apellido == null || apellido.isBlank()) {
                throw new IllegalArgumentException("Apellido es obligatorio.");
            }

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
            String razonSocial = request.getParameter("razon_social");
            String nombreFantasia = request.getParameter("nombre_fantasia");

            if (razonSocial == null || razonSocial.isBlank()) {
                throw new IllegalArgumentException("Razón Social es obligatoria.");
            }

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

        Long idTipoDocumento = parseLongNullableEstricto(request.getParameter("id_tipo_documento"));
        String numeroDocumento = request.getParameter("numero_documento");

        boolean principalDocumento = false;
        String principalParam = request.getParameter("principal_documento");
        if (principalParam != null) {
            principalDocumento = principalParam.equalsIgnoreCase("true")
                    || principalParam.equalsIgnoreCase("on")
                    || principalParam.equalsIgnoreCase("1")
                    || principalParam.equalsIgnoreCase("yes");
        }

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

        response.sendRedirect(request.getContextPath() + "/clientes?action=listar");
    }

    // Inyección de catálogos geográficos y tipos de identidad para formularios
    private void cargarCombos(HttpServletRequest request) {
        List<Distrito> distritos = distritoService.listarActivos();
        request.setAttribute("distritos", distritos);

        List<Localidad> localidades = localidadService.listarActivos();
        request.setAttribute("localidades", localidades);

        List<TipoDocumento> tiposDocumento = tipoDocumentoService.listarActivos();
        request.setAttribute("tiposDocumento", tiposDocumento);
    }

    // Reconstrucción del estado del formulario para gestión de errores de validación
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        cargarCombos(request);

        Cliente c = new Cliente();
        c.setTelefono(request.getParameter("telefono"));
        c.setIdDistrito(parseLongNullableEstricto(request.getParameter("id_distrito")));
        c.setIdLocalidad(parseLongNullableEstricto(request.getParameter("id_localidad")));

        request.setAttribute("cliente", c);

        String tipo = request.getParameter("tipo");
        request.setAttribute("tipo", tipo);

        if ("PERSONA".equalsIgnoreCase(tipo)) {
            request.setAttribute("nombre", request.getParameter("nombre"));
            request.setAttribute("apellido", request.getParameter("apellido"));
            request.setAttribute("apodo", request.getParameter("apodo"));
        } else if ("EMPRESA".equalsIgnoreCase(tipo)) {
            request.setAttribute("razon_social", request.getParameter("razon_social"));
            request.setAttribute("nombre_fantasia", request.getParameter("nombre_fantasia"));
        }

        request.setAttribute("id_tipo_documento", parseLongNullableEstricto(request.getParameter("id_tipo_documento")));
        request.setAttribute("numero_documento", request.getParameter("numero_documento"));
        request.setAttribute("principal_documento", request.getParameter("principal_documento"));

        request.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/clientes/cliente_form.jsp").forward(request, response);
    }

    // Validación y parseo estricto de identificadores numéricos
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

    // Validación y parseo flexible para campos numéricos opcionales
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

    // Lógica de resolución de identidad para visualización de nombres de clientes
    private String resolverNombreCliente(Long idCliente) {
        if (idCliente == null) return null;

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

    // Utilidad para la persistencia de filtros en la navegación del cliente
    private String construirUrlRetornoListado(HttpServletRequest request) {
        String url = request.getContextPath() + "/clientes?action=listar";

        String q = request.getParameter("q");
        if (q != null && !q.isBlank()) {
            url += "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
        }

        String estado = request.getParameter("estado");
        if (estado != null && !estado.isBlank()) {
            url += "&estado=" + URLEncoder.encode(estado, StandardCharsets.UTF_8);
        }

        return url;
    }
}