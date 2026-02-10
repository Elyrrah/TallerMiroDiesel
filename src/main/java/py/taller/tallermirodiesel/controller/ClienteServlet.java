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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import py.taller.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import py.taller.tallermirodiesel.dto.ClientePersonaListadoDTO;
import py.taller.tallermirodiesel.model.Cliente;
import py.taller.tallermirodiesel.model.ClienteDocumento;
import py.taller.tallermirodiesel.model.ClienteEmpresa;
import py.taller.tallermirodiesel.model.ClientePersona;
import py.taller.tallermirodiesel.model.Distrito;
import py.taller.tallermirodiesel.model.Localidad;
import py.taller.tallermirodiesel.model.TipoDocumento;
import py.taller.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import py.taller.tallermirodiesel.service.ClienteDocumentoService;
import py.taller.tallermirodiesel.service.ClienteEmpresaService;
import py.taller.tallermirodiesel.service.ClienteListadoService;
import py.taller.tallermirodiesel.service.ClientePersonaService;
import py.taller.tallermirodiesel.service.ClienteService;
import py.taller.tallermirodiesel.service.DistritoService;
import py.taller.tallermirodiesel.service.LocalidadService;
import py.taller.tallermirodiesel.service.TipoDocumentoService;
import py.taller.tallermirodiesel.service.impl.ClienteDocumentoServiceImpl;
import py.taller.tallermirodiesel.service.impl.ClienteEmpresaServiceImpl;
import py.taller.tallermirodiesel.service.impl.ClienteListadoServiceImpl;
import py.taller.tallermirodiesel.service.impl.ClientePersonaServiceImpl;
import py.taller.tallermirodiesel.service.impl.ClienteServiceImpl;
import py.taller.tallermirodiesel.service.impl.DistritoServiceImpl;
import py.taller.tallermirodiesel.service.impl.LocalidadServiceImpl;
import py.taller.tallermirodiesel.service.impl.TipoDocumentoServiceImpl;

/**
 *
 * @author elyrr
 */
@WebServlet(name = "ClienteServlet", urlPatterns = {"/clientes"})
// Bloque: Mapeo del servlet (todas las acciones de Clientes entran por /clientes).
public class ClienteServlet extends HttpServlet {

    // Service utilizado por el Servlet para acceder a la capa de negocio.
    private final ClienteService clienteService = new ClienteServiceImpl();

    // Service de listados con JOIN (2 DTOs: persona y empresa)
    private final ClienteListadoService clienteListadoService = new ClienteListadoServiceImpl();

    // Services utilizados para combos.
    private final DistritoService distritoService = new DistritoServiceImpl();
    private final LocalidadService localidadService = new LocalidadServiceImpl();
    private final TipoDocumentoService tipoDocumentoService = new TipoDocumentoServiceImpl();

    // Services para resolver nombre del cliente (persona/empresa).
    private final ClientePersonaService clientePersonaService = new ClientePersonaServiceImpl();
    private final ClienteEmpresaService clienteEmpresaService = new ClienteEmpresaServiceImpl();

    // Service para documentos del cliente.
    private final ClienteDocumentoService clienteDocumentoService = new ClienteDocumentoServiceImpl();

    // ========== ========== ========== ========== ==========
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ==========
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué caso ejecutar.
        String action = request.getParameter("action");

        // 2. Si no viene acción, se asume "list" como comportamiento por defecto.
        if (action == null || action.isBlank()) {
            action = "list";
        }

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
        try {
            switch (action) {
                case "new" -> mostrarFormularioNuevo(request, response);

                // "search" queda como alias de list (tu JSP usa filtros por GET)
                case "search" -> listar(request, response);

                case "list" -> listar(request, response);
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

        // 1. Lee el parámetro "action" para decidir qué operación ejecutar.
        String action = request.getParameter("action");

        // 2. Si no viene acción, se asume "save" como comportamiento por defecto.
        if (action == null || action.isBlank()) {
            action = "save";
        }

        // 3. Router de acciones POST.
        try {
            switch (action) {
                case "save" -> guardar(request, response);

                // Toggle unificado Activar/Desactivar
                case "toggleActivo" -> toggleActivo(request, response);

                default -> response.sendRedirect(request.getContextPath() + "/clientes?action=list");
            }

            // 4. Si falla guardar, devolvemos al formulario con el error y los valores cargados
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());

            // Si querés ver el motivo REAL de PostgreSQL (trigger/constraint), lo mostramos aparte
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                request.setAttribute("errorDetalle", e.getCause().getMessage());
            }

            // Si el error fue en toggle, volvemos al listado (no al form)
            if ("toggleActivo".equalsIgnoreCase(action)) {
                response.sendRedirect(buildListRedirectUrl(request));
                return;
            }

            reenviarFormularioConDatos(request, response);
        }
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES GET
    // ========== ========== ==========

    // LISTAR (2 TABLAS: PERSONA y EMPRESA, cada una con su DTO y su JOIN).
    private void listar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // =========================
        // BLOQUE: Lectura de filtros
        // =========================
        String q = request.getParameter("q");
        if (q == null) q = request.getParameter("filtro");

        String estado = request.getParameter("estado"); // "TODOS" | "ACTIVOS" | "INACTIVOS"

        Boolean activo = null;
        if (estado != null) {
            if ("ACTIVOS".equalsIgnoreCase(estado)) activo = true;
            if ("INACTIVOS".equalsIgnoreCase(estado)) activo = false;
        }

        request.setAttribute("q", q);
        request.setAttribute("estado", estado);

        // =========================
        // BLOQUE: Cargar 2 listas (JOIN)
        // =========================
        List<ClientePersonaListadoDTO> listaClientesPersona = clienteListadoService.listarPersonas(q, activo);
        List<ClienteEmpresaListadoDTO> listaClientesEmpresa = clienteListadoService.listarEmpresas(q, activo);

        request.setAttribute("listaClientesPersona", listaClientesPersona);
        request.setAttribute("listaClientesEmpresa", listaClientesEmpresa);

        // =========================
        // BLOQUE: Forward
        // =========================
        request.getRequestDispatcher("/WEB-INF/views/clientes/cliente_listar.jsp").forward(request, response);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Carga combos (distritos/localidades/referidores/tiposDocumento).
        cargarCombos(request);

        // 2. Envía un Cliente vacío a la vista para llenado inicial.
        request.setAttribute("cliente", new Cliente());

        // 3. Tipo por defecto
        request.setAttribute("tipo", "PERSONA");

        // 4. Renderiza el formulario.
        request.getRequestDispatcher("/WEB-INF/views/clientes/cliente_form.jsp").forward(request, response);
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES POST
    // ========== ========== ==========

    // TOGGLE ACTIVO (unifica activar/desactivar).
    private void toggleActivo(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Long id = parseLong(request.getParameter("id"));

        // activo_actual llega como "true/false"
        boolean activoActual = Boolean.parseBoolean(request.getParameter("activo_actual"));
        boolean nuevoActivo = !activoActual;

        clienteService.setActivo(id, nuevoActivo);

        // Volver al listado preservando filtros
        response.sendRedirect(buildListRedirectUrl(request));
    }

    // GUARDAR (CREAR PERSONA O EMPRESA + DOCUMENTO OPCIONAL).
    // (Tu bloque actual queda igual; lo dejo tal cual lo venías usando)
    private void guardar(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 1. Tipo de cliente
        String tipo = request.getParameter("tipo");
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de cliente (PERSONA o EMPRESA).");
        }

        // 2. Datos comunes
        String telefono = request.getParameter("telefono");

        // OJO: con datalist, el ID real viene en el hidden (id_distrito / id_localidad / id_cliente_referidor)
        Long idDistrito = parseLongNullableStrict(request.getParameter("id_distrito"));
        Long idLocalidad = parseLongNullableStrict(request.getParameter("id_localidad"));

        if (idDistrito == null && idLocalidad == null) {
            throw new IllegalArgumentException("Debe especificar al menos Distrito o Localidad.");
        }

        Long idClienteReferidor = parseLongNullableStrict(request.getParameter("id_cliente_referidor"));
        String fuente = request.getParameter("fuente_referencia");

        // =========================
        // BLOQUE: Validación Fuente vs Referidor (con NINGUNA)
        // =========================
        boolean fuenteVacia = (fuente == null || fuente.isBlank());
        boolean fuenteEsNinguna = (!fuenteVacia && "NINGUNA".equalsIgnoreCase(fuente.trim()));

        if (fuenteEsNinguna) {
            idClienteReferidor = null;
        }

        if (!fuenteVacia && !fuenteEsNinguna && idClienteReferidor == null) {
            throw new IllegalArgumentException("Si la fuente de referencia no es NINGUNA, debe existir cliente referidor.");
        }

        if (idClienteReferidor != null && (fuenteVacia || fuenteEsNinguna)) {
            throw new IllegalArgumentException("Si existe cliente referidor, la fuente de referencia no puede ser NINGUNA.");
        }

        Cliente c = new Cliente();
        c.setTelefono(telefono);
        c.setIdDistrito(idDistrito);
        c.setIdLocalidad(idLocalidad);
        c.setIdClienteReferidor(idClienteReferidor);

        if (fuenteVacia || fuenteEsNinguna) {
            c.setFuenteReferencia(null);
        } else {
            try {
                c.setFuenteReferencia(FuenteReferenciaClienteEnum.valueOf(fuente.trim().toUpperCase(Locale.ROOT)));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Fuente de referencia inválida: " + fuente);
            }
        }

        c.setActivo(true);

        // 3) Guardar cliente base (insert) => nos devuelve id
        Long idCreado = clienteService.guardar(c);
        if (idCreado == null) {
            throw new RuntimeException("No se pudo guardar el cliente base.");
        }

        // 4) Guardar datos específicos según tipo (persona/empresa)
        if ("PERSONA".equalsIgnoreCase(tipo)) {

            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String apodo = request.getParameter("apodo");

            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre es obligatorio.");
            if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("Apellido es obligatorio.");

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

            if (razonSocial == null || razonSocial.isBlank()) throw new IllegalArgumentException("Razón Social es obligatoria.");

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

        // 5. Documento opcional (si eligió tipo y cargó número)
        Long idTipoDocumento = parseLongNullableStrict(request.getParameter("id_tipo_documento"));
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

            if (principalDocumento) {
                Optional<Long> idDocOpt = findDocumentoIdByTipoNumero(idCreado, idTipoDocumento, cd.getNumero());
                if (idDocOpt.isPresent()) {
                    clienteDocumentoService.definirPrincipal(idCreado, idDocOpt.get());
                }
            }
        }

        // 6. Redirige al listado
        response.sendRedirect(request.getContextPath() + "/clientes?action=list");
    }

    // ========== ========== ==========
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ==========

    private void cargarCombos(HttpServletRequest request) {

        List<Distrito> distritos = distritoService.listarActivos();
        request.setAttribute("distritos", distritos);

        List<Localidad> localidades = localidadService.listarActivos();
        request.setAttribute("localidades", localidades);

        List<TipoDocumento> tiposDocumento = tipoDocumentoService.listarActivos();
        request.setAttribute("tiposDocumento", tiposDocumento);

        request.setAttribute("fuentesReferencia", FuenteReferenciaClienteEnum.values());

        List<Cliente> clientesReferidores = clienteService.listarActivos();

        Map<Long, String> clientesReferidoresMap = new LinkedHashMap<>();
        for (Cliente cRef : clientesReferidores) {
            Long idRef = cRef.getIdCliente();

            String nombre = resolveClientName(idRef);
            if (nombre == null || nombre.isBlank()) {
                if (cRef.getTelefono() != null && !cRef.getTelefono().trim().isBlank()) {
                    nombre = cRef.getTelefono().trim();
                } else {
                    nombre = "Cliente #" + idRef;
                }
            }

            clientesReferidoresMap.put(idRef, nombre);
        }
        request.setAttribute("clientesReferidoresMap", clientesReferidoresMap);
    }

    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        cargarCombos(request);

        Cliente c = new Cliente();
        c.setTelefono(request.getParameter("telefono"));
        c.setIdDistrito(parseLongNullableStrict(request.getParameter("id_distrito")));
        c.setIdLocalidad(parseLongNullableStrict(request.getParameter("id_localidad")));
        c.setIdClienteReferidor(parseLongNullableStrict(request.getParameter("id_cliente_referidor")));

        String fuente = request.getParameter("fuente_referencia");
        if (fuente != null && !fuente.isBlank()) {
            try {
                c.setFuenteReferencia(FuenteReferenciaClienteEnum.valueOf(fuente.trim().toUpperCase(Locale.ROOT)));
            } catch (Exception ex) {
                c.setFuenteReferencia(null);
            }
        }

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

        request.setAttribute("id_tipo_documento", parseLongNullableStrict(request.getParameter("id_tipo_documento")));
        request.setAttribute("numero_documento", request.getParameter("numero_documento"));
        request.setAttribute("principal_documento", request.getParameter("principal_documento"));

        request.getRequestDispatcher("/WEB-INF/views/clientes/cliente_form.jsp").forward(request, response);
    }

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

    private Long parseLongNullableStrict(String value) {
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

    private String resolveClientName(Long idCliente) {
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

    private String buildListRedirectUrl(HttpServletRequest request) {
        String url = request.getContextPath() + "/clientes?action=list";

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

    private Optional<Long> findDocumentoIdByTipoNumero(Long idCliente, Long idTipoDocumento, String numero) {
        if (idCliente == null || idTipoDocumento == null || numero == null || numero.isBlank()) {
            return Optional.empty();
        }

        List<ClienteDocumento> docs = clienteDocumentoService.listarPorCliente(idCliente);
        for (ClienteDocumento d : docs) {
            if (idTipoDocumento.equals(d.getIdTipoDocumento())
                    && numero.equalsIgnoreCase(d.getNumero() != null ? d.getNumero().trim() : "")) {
                return Optional.ofNullable(d.getIdClienteDocumento());
            }
        }
        return Optional.empty();
    }
}
