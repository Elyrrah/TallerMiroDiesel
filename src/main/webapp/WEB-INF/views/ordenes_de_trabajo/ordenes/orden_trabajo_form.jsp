<%-- 
    Document   : oden_trabajo_form
    Created on : 23 feb. 2026, 1:44:50 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Orden de Trabajo</title>

    <%-- Estilos inline para los modales --%>
    <style>
        .modal-fondo {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.45);
            z-index: 1000;
            justify-content: center;
            align-items: flex-start;
            padding-top: 60px;
        }
        .modal-fondo.activo { display: flex; }
        .modal-caja {
            background: #fff;
            border: 1px solid #999;
            padding: 24px 28px;
            width: 460px;
            max-height: 80vh;
            overflow-y: auto;
            position: relative;
        }
        .modal-caja h3 { margin-top: 0; }
        .modal-cerrar {
            position: absolute;
            top: 10px; right: 14px;
            font-size: 20px;
            cursor: pointer;
            background: none;
            border: none;
        }
        .modal-error { color: #b00; margin-bottom: 8px; }
        .modal-caja label { display: block; margin-top: 10px; font-weight: bold; }
        .modal-caja input[type=text],
        .modal-caja input[type=number],
        .modal-caja select,
        .modal-caja textarea { width: 100%; box-sizing: border-box; padding: 4px; }
        .modal-caja .acciones { margin-top: 16px; }
    </style>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>
<p><a href="${pageContext.request.contextPath}/ordenes-trabajo?action=listar">Volver al listado</a></p>

<%-- Mensaje de error del formulario principal --%>
<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<c:set var="esEdicion" value="${not empty ot.idOrdenTrabajo}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Orden de Trabajo — ${ot.numeroOrden}</c:when>
        <c:otherwise>Nueva Orden de Trabajo</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/ordenes-trabajo">
    <input type="hidden" name="action" value="guardar" />
    <c:if test="${esEdicion}">
        <input type="hidden" name="idOrdenTrabajo" value="${ot.idOrdenTrabajo}" />
    </c:if>

    <%-- Cliente: datalist con personas y empresas activas --%>
    <fieldset>
        <legend>Cliente</legend>

        <input type="hidden" id="idCliente" name="idCliente" value="${ot.idCliente}" />

        <div>
            <label>Buscar cliente (nombre o razón social):</label><br/>
            <input type="text"
                   id="buscarCliente"
                   list="listaClientes"
                   placeholder="Escribí el nombre..."
                   autocomplete="off"
                   oninput="sincronizarId('listaClientes','idCliente',this.value)" />

            <datalist id="listaClientes">
                <c:forEach var="p" items="${clientesPersona}">
                    <option data-id="${p.idCliente}"
                            value="${p.nombre} ${p.apellido}<c:if test="${not empty p.apodo}"> (${p.apodo})</c:if>">
                    </option>
                </c:forEach>
                <c:forEach var="e" items="${clientesEmpresa}">
                    <option data-id="${e.idCliente}"
                            value="${e.razonSocial}<c:if test="${not empty e.nombreFantasia}"> (${e.nombreFantasia})</c:if>">
                    </option>
                </c:forEach>
            </datalist>
        </div>

        <div style="margin-top:6px;">
            <small>
                ¿No está en la lista?
                <a href="#" onclick="abrirModalCliente(); return false;">Registrar nuevo cliente</a>
            </small>
        </div>
    </fieldset>

    <br/>

    <%-- Tipo de ingreso: VEHICULO o COMPONENTE --%>
    <fieldset>
        <legend>Tipo de Ingreso</legend>
        <c:forEach var="tipo" items="${tiposIngreso}">
            <label style="display:inline; font-weight:normal;">
                <input type="radio"
                       name="tipoIngreso"
                       value="${tipo}"
                       ${ot.tipoIngreso == tipo ? 'checked' : ''}
                       onchange="mostrarSeccionTipo(this.value)" />
                ${tipo}
            </label>
            &nbsp;
        </c:forEach>
    </fieldset>

    <br/>

    <%-- Sección vehículo — visible solo si tipoIngreso = VEHICULO --%>
    <fieldset id="seccionVehiculo" style="display:none;">
        <legend>Vehículo</legend>

        <input type="hidden" id="idVehiculo" name="idVehiculo" value="${ot.idVehiculo}" />

        <div>
            <label>Buscar vehículo (placa — marca modelo):</label><br/>
            <input type="text"
                   id="buscarVehiculo"
                   list="listaVehiculos"
                   placeholder="Escribí la placa..."
                   autocomplete="off"
                   oninput="sincronizarId('listaVehiculos','idVehiculo',this.value)" />

            <datalist id="listaVehiculos">
                <c:forEach var="v" items="${vehiculos}">
                    <option data-id="${v.idVehiculo}"
                            value="${v.placa}<c:if test="${not empty v.nombreMarca}"> — ${v.nombreMarca}</c:if><c:if test="${not empty v.nombreModelo}"> ${v.nombreModelo}</c:if>">
                    </option>
                </c:forEach>
            </datalist>
        </div>

        <div style="margin-top:6px;">
            <small>
                ¿No está en la lista?
                <a href="#" onclick="abrirModalVehiculo(); return false;">Registrar nuevo vehículo</a>
            </small>
        </div>
    </fieldset>

    <%-- Sección componente — visible solo si tipoIngreso = COMPONENTE --%>
    <fieldset id="seccionComponente" style="display:none;">
        <legend>Componente</legend>

        <input type="hidden" id="idComponente" name="idComponente" value="${ot.idComponente}" />

        <div>
            <label>Buscar componente (serie — marca modelo):</label><br/>
            <input type="text"
                   id="buscarComponente"
                   list="listaComponentes"
                   placeholder="Escribí el número de serie..."
                   autocomplete="off"
                   oninput="sincronizarId('listaComponentes','idComponente',this.value); actualizarPicosPorComponente(this.value)" />

            <datalist id="listaComponentes">
                <c:forEach var="comp" items="${componentes}">
                    <option data-id="${comp.idComponente}"
                            value="${comp.numeroSerie}<c:if test="${not empty comp.nombreMarca}"> — ${comp.nombreMarca}</c:if><c:if test="${not empty comp.nombreModelo}"> ${comp.nombreModelo}</c:if>">
                    </option>
                </c:forEach>
            </datalist>
        </div>

        <div style="margin-top:6px;">
            <small>
                ¿No está en la lista?
                <a href="#" onclick="abrirModalComponente(); return false;">Registrar nuevo componente</a>
            </small>
        </div>

        <%-- Solo visible si el tipo de componente requiere picos (PICO INYECTOR o BOMBA Y PICO) --%>
        <div id="divCantidadPicos" style="margin-top:8px; display:none;">
            <label>Cantidad de Picos (opcional, 1-12):</label><br/>
            <input type="number"
                   name="cantidadPicos"
                   value="${ot.cantidadPicos}"
                   min="1"
                   max="12"
                   style="width:100px;" />
        </div>
    </fieldset>

    <br/>

    <%-- Fuente de referencia y referidor --%>
    <fieldset>
        <legend>¿Cómo llegó el cliente?</legend>

        <div>
            <label>Fuente de referencia:</label><br/>
            <select name="fuenteReferencia" id="fuenteReferencia" onchange="mostrarSeccionReferidor(this.value)">
                <c:forEach var="fuente" items="${fuentesReferencia}">
                    <option value="${fuente}" ${ot.fuenteReferencia == fuente ? 'selected' : ''}>${fuente}</option>
                </c:forEach>
            </select>
        </div>

        <%-- Referidor: visible solo si fuente != NINGUNA --%>
        <div id="seccionReferidor" style="margin-top:10px; display:none;">
            <input type="hidden" id="idReferidor" name="idReferidor" value="${ot.idReferidor}" />

            <label>Buscar referidor (nombre o razón social):</label><br/>
            <input type="text"
                   id="buscarReferidor"
                   list="listaReferidores"
                   placeholder="Escribí el nombre..."
                   autocomplete="off"
                   oninput="sincronizarId('listaReferidores','idReferidor',this.value)" />

            <%-- El referidor también es un cliente; reusa las mismas listas --%>
            <datalist id="listaReferidores">
                <c:forEach var="p" items="${clientesPersona}">
                    <option data-id="${p.idCliente}"
                            value="${p.nombre} ${p.apellido}<c:if test="${not empty p.apodo}"> (${p.apodo})</c:if>">
                    </option>
                </c:forEach>
                <c:forEach var="e" items="${clientesEmpresa}">
                    <option data-id="${e.idCliente}"
                            value="${e.razonSocial}<c:if test="${not empty e.nombreFantasia}"> (${e.nombreFantasia})</c:if>">
                    </option>
                </c:forEach>
            </datalist>
        </div>
    </fieldset>

    <br/>

    <%-- Datos generales de la OT --%>
    <fieldset>
        <legend>Datos de la Orden</legend>

        <div>
            <label>Problema Reportado:</label><br/>
            <textarea name="problemaReportado" rows="3" cols="50">${ot.problemaReportado}</textarea>
        </div>

        <div style="margin-top:8px;">
            <label>Observaciones de Ingreso:</label><br/>
            <textarea name="observacionesIngreso" rows="3" cols="50">${ot.observacionesIngreso}</textarea>
        </div>

        <div style="margin-top:8px;">
            <label>Fecha Estimada de Entrega (opcional):</label><br/>
            <input type="date"
                   name="fechaEntrega"
                   value="${not empty ot.fechaEntrega ? ot.fechaEntrega.toLocalDate() : ''}" />
        </div>
    </fieldset>

    <br/>
    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=listar">Cancelar</a>
</form>


<%-- ================================================================ --%>
<%-- MODAL: NUEVO CLIENTE                                              --%>
<%-- ================================================================ --%>
<div id="modalCliente" class="modal-fondo">
    <div class="modal-caja">
        <button class="modal-cerrar" onclick="cerrarModal('modalCliente')">✕</button>
        <h3>Registrar Nuevo Cliente</h3>
        <div id="errorModalCliente" class="modal-error" style="display:none;"></div>

        <%-- Tipo de cliente --%>
        <fieldset>
            <legend>Tipo</legend>
            <label style="display:inline; font-weight:normal;">
                <input type="radio" name="mc_tipo" value="PERSONA" checked onchange="toggleTipoCliente('PERSONA')" /> Persona
            </label>
            &nbsp;
            <label style="display:inline; font-weight:normal;">
                <input type="radio" name="mc_tipo" value="EMPRESA" onchange="toggleTipoCliente('EMPRESA')" /> Empresa
            </label>
        </fieldset>

        <%-- Campos de persona --%>
        <div id="mc_fsPersona">
            <label>Nombre *</label>
            <input type="text" id="mc_nombre" />
            <label>Apellido *</label>
            <input type="text" id="mc_apellido" />
            <label>Apodo <small>(opcional)</small></label>
            <input type="text" id="mc_apodo" />
        </div>

        <%-- Campos de empresa --%>
        <div id="mc_fsEmpresa" style="display:none;">
            <label>Razón Social *</label>
            <input type="text" id="mc_razonSocial" />
            <label>Nombre Fantasía <small>(opcional)</small></label>
            <input type="text" id="mc_nombreFantasia" />
        </div>

        <%-- Datos comunes --%>
        <label>Teléfono <small>(opcional)</small></label>
        <input type="text" id="mc_telefono" />

        <label>Distrito <small>(opcional)</small></label>
        <input type="text" id="mc_inputDistrito" list="mc_listaDistritos"
               placeholder="Escribí para buscar..." autocomplete="off"
               oninput="filtrarLocalidades()" />
        <datalist id="mc_listaDistritos"></datalist>
        <input type="hidden" id="mc_idDistrito" />

        <label>Localidad <small>(opcional)</small></label>
        <input type="text" id="mc_inputLocalidad" list="mc_listaLocalidades"
               placeholder="Seleccioná un distrito primero..." autocomplete="off"
               oninput="sincronizarLocalidad()" />
        <datalist id="mc_listaLocalidades"></datalist>
        <input type="hidden" id="mc_idLocalidad" />

        <div class="acciones">
            <button onclick="guardarCliente()">Guardar</button>
            <button onclick="cerrarModal('modalCliente')">Cancelar</button>
        </div>
    </div>
</div>


<%-- ================================================================ --%>
<%-- MODAL: NUEVO VEHÍCULO                                             --%>
<%-- ================================================================ --%>
<div id="modalVehiculo" class="modal-fondo">
    <div class="modal-caja">
        <button class="modal-cerrar" onclick="cerrarModal('modalVehiculo')">✕</button>
        <h3>Registrar Nuevo Vehículo</h3>
        <div id="errorModalVehiculo" class="modal-error" style="display:none;"></div>

        <label>Placa <small>(opcional)</small></label>
        <input type="text" id="mv_placa" placeholder="Ej: ABC 123" />

        <label>Marca *</label>
        <input type="text" id="mv_inputMarca" list="mv_listaMarcas"
               placeholder="Escribí para buscar..." autocomplete="off"
               oninput="filtrarModelosVehiculo()" />
        <datalist id="mv_listaMarcas"></datalist>
        <input type="hidden" id="mv_idMarca" />

        <label>Modelo <small>(opcional)</small></label>
        <input type="text" id="mv_inputModelo" list="mv_listaModelos"
               placeholder="Seleccioná una marca primero..." autocomplete="off"
               oninput="sincronizarModeloVehiculo()" />
        <datalist id="mv_listaModelos"></datalist>
        <input type="hidden" id="mv_idModelo" />

        <label>Año <small>(opcional)</small></label>
        <input type="number" id="mv_anio" min="1900" max="2100" placeholder="Ej: 2015" />

        <label>Tipo de Vehículo *</label>
        <select id="mv_tipo">
            <option value="">-- Seleccionar --</option>
        </select>

        <label>Observaciones <small>(opcional)</small></label>
        <textarea id="mv_observaciones" rows="2"></textarea>

        <div class="acciones">
            <button onclick="guardarVehiculo()">Guardar</button>
            <button onclick="cerrarModal('modalVehiculo')">Cancelar</button>
        </div>
    </div>
</div>


<%-- ================================================================ --%>
<%-- MODAL: NUEVO COMPONENTE                                           --%>
<%-- ================================================================ --%>
<div id="modalComponente" class="modal-fondo">
    <div class="modal-caja">
        <button class="modal-cerrar" onclick="cerrarModal('modalComponente')">✕</button>
        <h3>Registrar Nuevo Componente</h3>
        <div id="errorModalComponente" class="modal-error" style="display:none;"></div>

        <label>Tipo de Componente *</label>
        <input type="text" id="mco_inputTipo" list="mco_listaTipos"
               placeholder="Escribí para buscar..." autocomplete="off"
               oninput="sincronizarTipoComponente()" />
        <datalist id="mco_listaTipos"></datalist>
        <input type="hidden" id="mco_idTipo" />

        <label>Marca *</label>
        <input type="text" id="mco_inputMarca" list="mco_listaMarcas"
               placeholder="Escribí para buscar..." autocomplete="off"
               oninput="filtrarModelosComponente()" />
        <datalist id="mco_listaMarcas"></datalist>
        <input type="hidden" id="mco_idMarca" />

        <label>Modelo *</label>
        <input type="text" id="mco_inputModelo" list="mco_listaModelos"
               placeholder="Seleccioná una marca primero..." autocomplete="off"
               oninput="sincronizarModeloComponente()" />
        <datalist id="mco_listaModelos"></datalist>
        <input type="hidden" id="mco_idModelo" />

        <label>Número de Serie <small>(opcional)</small></label>
        <input type="text" id="mco_serie" placeholder="Ej: SN-12345" />

        <label>Observaciones <small>(opcional)</small></label>
        <textarea id="mco_observaciones" rows="2"></textarea>

        <div class="acciones">
            <button onclick="guardarComponente()">Guardar</button>
            <button onclick="cerrarModal('modalComponente')">Cancelar</button>
        </div>
    </div>
</div>


<%-- ================================================================ --%>
<%-- JAVASCRIPT                                                        --%>
<%-- ================================================================ --%>
<script>
    const BASE = '${pageContext.request.contextPath}';

    // Datos cargados desde el servidor al abrir cada modal
    let datosCliente    = null;
    let datosVehiculo   = null;
    let datosComponente = null;

    // ----------------------------------------------------------------
    // UTILIDADES GENERALES
    // ----------------------------------------------------------------

    // Sincroniza el campo oculto con el ID correspondiente a la opción elegida en un datalist
    function sincronizarId(idDatalist, idHidden, texto) {
        const opciones = document.querySelectorAll('#' + idDatalist + ' option');
        let encontrado = '';
        opciones.forEach(op => {
            if (op.value === texto) encontrado = op.getAttribute('data-id');
        });
        document.getElementById(idHidden).value = encontrado;
    }

    // Abre un modal y dispara la carga de datos si todavía no los tiene
    function abrirModal(idModal, cargarFn) {
        document.getElementById(idModal).classList.add('activo');
        cargarFn();
    }

    // Cierra un modal y limpia sus campos
    function cerrarModal(idModal) {
        document.getElementById(idModal).classList.remove('activo');
    }

    // Muestra u oculta el mensaje de error dentro de un modal
    function mostrarErrorModal(idError, mensaje) {
        const el = document.getElementById(idError);
        el.textContent = mensaje;
        el.style.display = mensaje ? 'block' : 'none';
    }

    // Puebla un datalist a partir de un array de objetos {id, nombre}
    function poblarDatalist(idDatalist, items) {
        const dl = document.getElementById(idDatalist);
        dl.innerHTML = '';
        items.forEach(item => {
            const op = document.createElement('option');
            op.value = item.nombre;
            op.setAttribute('data-id', item.id);
            dl.appendChild(op);
        });
    }

    // Agrega una opción a un datalist del formulario principal con data-id, y la selecciona
    function agregarYSeleccionarEnDatalist(idDatalist, idHidden, idInput, nuevoId, etiqueta) {
        const dl = document.getElementById(idDatalist);
        // Evita duplicados: si ya existe la opción la reusa
        let opExistente = null;
        Array.from(dl.options).forEach(op => {
            if (op.getAttribute('data-id') == nuevoId) opExistente = op;
        });
        if (!opExistente) {
            const op = document.createElement('option');
            op.value = etiqueta;
            op.setAttribute('data-id', nuevoId);
            dl.appendChild(op);
        }
        document.getElementById(idInput).value = etiqueta;
        document.getElementById(idHidden).value = nuevoId;
    }

    // ----------------------------------------------------------------
    // MODAL CLIENTE
    // ----------------------------------------------------------------

    function abrirModalCliente() {
        limpiarModalCliente();
        abrirModal('modalCliente', cargarDatosCliente);
    }

    function limpiarModalCliente() {
        ['mc_nombre','mc_apellido','mc_apodo','mc_razonSocial','mc_nombreFantasia',
         'mc_telefono','mc_inputDistrito','mc_idDistrito','mc_inputLocalidad','mc_idLocalidad']
            .forEach(id => { document.getElementById(id).value = ''; });
        document.querySelector('input[name="mc_tipo"][value="PERSONA"]').checked = true;
        toggleTipoCliente('PERSONA');
        mostrarErrorModal('errorModalCliente', '');
    }

    function cargarDatosCliente() {
        if (datosCliente) { poblarCombosCliente(); return; }
        fetch(BASE + '/ordenes-trabajo?action=ajaxClientes')
            .then(r => r.json())
            .then(data => {
                datosCliente = data;
                poblarCombosCliente();
            });
    }

    function poblarCombosCliente() {
        poblarDatalist('mc_listaDistritos', datosCliente.distritos);
        // Las localidades se filtran por distrito al elegir; por ahora cargamos todas
        poblarLocalidades(null);
    }

    function toggleTipoCliente(tipo) {
        document.getElementById('mc_fsPersona').style.display  = tipo === 'PERSONA' ? 'block' : 'none';
        document.getElementById('mc_fsEmpresa').style.display  = tipo === 'EMPRESA' ? 'block' : 'none';
    }

    function filtrarLocalidades() {
        const texto = document.getElementById('mc_inputDistrito').value.trim();
        const opciones = document.querySelectorAll('#mc_listaDistritos option');
        let idDistrito = '';
        opciones.forEach(op => { if (op.value === texto) idDistrito = op.getAttribute('data-id'); });
        document.getElementById('mc_idDistrito').value = idDistrito;
        document.getElementById('mc_inputLocalidad').value = '';
        document.getElementById('mc_idLocalidad').value = '';
        poblarLocalidades(idDistrito || null);
    }

    function poblarLocalidades(idDistrito) {
        if (!datosCliente) return;
        const filtradas = idDistrito
            ? datosCliente.localidades.filter(l => l.idDistrito == idDistrito)
            : datosCliente.localidades;
        poblarDatalist('mc_listaLocalidades', filtradas);
    }

    function sincronizarLocalidad() {
        const texto = document.getElementById('mc_inputLocalidad').value.trim();
        const opciones = document.querySelectorAll('#mc_listaLocalidades option');
        let idLoc = '';
        opciones.forEach(op => { if (op.value === texto) idLoc = op.getAttribute('data-id'); });
        document.getElementById('mc_idLocalidad').value = idLoc;
    }

    function guardarCliente() {
        mostrarErrorModal('errorModalCliente', '');
        const tipo = document.querySelector('input[name="mc_tipo"]:checked').value;
        const body = new URLSearchParams({
            action: 'ajaxGuardarCliente',
            tipo,
            telefono:       document.getElementById('mc_telefono').value,
            idDistrito:     document.getElementById('mc_idDistrito').value,
            idLocalidad:    document.getElementById('mc_idLocalidad').value,
            nombre:         document.getElementById('mc_nombre').value,
            apellido:       document.getElementById('mc_apellido').value,
            apodo:          document.getElementById('mc_apodo').value,
            razonSocial:    document.getElementById('mc_razonSocial').value,
            nombreFantasia: document.getElementById('mc_nombreFantasia').value,
        });

        fetch(BASE + '/ordenes-trabajo', { method: 'POST', body })
            .then(r => r.json())
            .then(data => {
                if (!data.ok) { mostrarErrorModal('errorModalCliente', data.error); return; }
                // Agrega el nuevo cliente al datalist del formulario principal y lo selecciona
                agregarYSeleccionarEnDatalist('listaClientes', 'idCliente', 'buscarCliente', data.id, data.etiqueta);
                // También lo agrega al datalist del referidor
                agregarYSeleccionarEnDatalist('listaReferidores', 'idReferidor', 'buscarReferidor', data.id, data.etiqueta);
                // Invalida el caché para que la próxima apertura del modal traiga el nuevo cliente
                datosCliente = null;
                cerrarModal('modalCliente');
            })
            .catch(() => mostrarErrorModal('errorModalCliente', 'Error de comunicación con el servidor.'));
    }

    // ----------------------------------------------------------------
    // MODAL VEHÍCULO
    // ----------------------------------------------------------------

    function abrirModalVehiculo() {
        limpiarModalVehiculo();
        abrirModal('modalVehiculo', cargarDatosVehiculo);
    }

    function limpiarModalVehiculo() {
        ['mv_placa','mv_inputMarca','mv_idMarca','mv_inputModelo','mv_idModelo','mv_anio','mv_observaciones']
            .forEach(id => { document.getElementById(id).value = ''; });
        document.getElementById('mv_tipo').value = '';
        mostrarErrorModal('errorModalVehiculo', '');
    }

    function cargarDatosVehiculo() {
        if (datosVehiculo) { poblarCombosVehiculo(); return; }
        fetch(BASE + '/ordenes-trabajo?action=ajaxVehiculos')
            .then(r => r.json())
            .then(data => {
                datosVehiculo = data;
                poblarCombosVehiculo();
            });
    }

    function poblarCombosVehiculo() {
        poblarDatalist('mv_listaMarcas', datosVehiculo.marcas);
        // Tipos de vehículo en el select
        const sel = document.getElementById('mv_tipo');
        sel.innerHTML = '<option value="">-- Seleccionar --</option>';
        datosVehiculo.tiposVehiculo.forEach(t => {
            const op = document.createElement('option');
            op.value = t; op.textContent = t;
            sel.appendChild(op);
        });
    }

    function filtrarModelosVehiculo() {
        const texto = document.getElementById('mv_inputMarca').value.trim();
        const opciones = document.querySelectorAll('#mv_listaMarcas option');
        let idMarca = '';
        opciones.forEach(op => { if (op.value === texto) idMarca = op.getAttribute('data-id'); });
        document.getElementById('mv_idMarca').value = idMarca;
        document.getElementById('mv_inputModelo').value = '';
        document.getElementById('mv_idModelo').value = '';
        if (!datosVehiculo) return;
        const filtrados = idMarca
            ? datosVehiculo.modelos.filter(m => m.idMarca == idMarca)
            : datosVehiculo.modelos;
        poblarDatalist('mv_listaModelos', filtrados);
    }

    function sincronizarModeloVehiculo() {
        const texto = document.getElementById('mv_inputModelo').value.trim();
        const opciones = document.querySelectorAll('#mv_listaModelos option');
        let id = '';
        opciones.forEach(op => { if (op.value === texto) id = op.getAttribute('data-id'); });
        document.getElementById('mv_idModelo').value = id;
    }

    function guardarVehiculo() {
        mostrarErrorModal('errorModalVehiculo', '');
        const body = new URLSearchParams({
            action:        'ajaxGuardarVehiculo',
            placa:         document.getElementById('mv_placa').value,
            idMarca:       document.getElementById('mv_idMarca').value,
            idModelo:      document.getElementById('mv_idModelo').value,
            anio:          document.getElementById('mv_anio').value,
            tipoVehiculo:  document.getElementById('mv_tipo').value,
            observaciones: document.getElementById('mv_observaciones').value,
        });

        fetch(BASE + '/ordenes-trabajo', { method: 'POST', body })
            .then(r => r.json())
            .then(data => {
                if (!data.ok) { mostrarErrorModal('errorModalVehiculo', data.error); return; }
                agregarYSeleccionarEnDatalist('listaVehiculos', 'idVehiculo', 'buscarVehiculo', data.id, data.etiqueta);
                datosVehiculo = null;
                cerrarModal('modalVehiculo');
            })
            .catch(() => mostrarErrorModal('errorModalVehiculo', 'Error de comunicación con el servidor.'));
    }

    // ----------------------------------------------------------------
    // MODAL COMPONENTE
    // ----------------------------------------------------------------

    function abrirModalComponente() {
        limpiarModalComponente();
        abrirModal('modalComponente', cargarDatosComponente);
    }

    function limpiarModalComponente() {
        ['mco_inputTipo','mco_idTipo','mco_inputMarca','mco_idMarca',
         'mco_inputModelo','mco_idModelo','mco_serie','mco_observaciones']
            .forEach(id => { document.getElementById(id).value = ''; });
        mostrarErrorModal('errorModalComponente', '');
    }

    function cargarDatosComponente() {
        if (datosComponente) { poblarCombosComponente(); return; }
        fetch(BASE + '/ordenes-trabajo?action=ajaxComponentes')
            .then(r => r.json())
            .then(data => {
                datosComponente = data;
                poblarCombosComponente();
            });
    }

    function poblarCombosComponente() {
        poblarDatalist('mco_listaTipos',   datosComponente.tiposComponente);
        poblarDatalist('mco_listaMarcas',  datosComponente.marcas);
    }

    // Detecta si un nombre de tipo de componente requiere mostrar el campo de cantidad de picos
    function tipoRequierePicos(nombreTipo) {
        if (!nombreTipo) return false;
        const n = nombreTipo.toUpperCase();
        return n.includes('PICO') || n.includes('BOMBA Y PICO');
    }

    // Muestra u oculta el campo de cantidad de picos en el formulario principal
    function mostrarCampoPicos(mostrar) {
        document.getElementById('divCantidadPicos').style.display = mostrar ? 'block' : 'none';
        if (!mostrar) {
            // Limpia el valor cuando se oculta para no enviar datos irrelevantes al servidor
            document.querySelector('input[name="cantidadPicos"]').value = '';
        }
    }

    // Cuando el usuario escribe en el datalist de componentes, detecta el tipo del componente elegido
    // y muestra u oculta el campo de picos según corresponda
    function actualizarPicosPorComponente(textoElegido) {
        const opciones = document.querySelectorAll('#listaComponentes option');
        let idElegido = '';
        opciones.forEach(op => { if (op.value === textoElegido) idElegido = op.getAttribute('data-id'); });

        if (!idElegido || !datosComponente) {
            mostrarCampoPicos(false);
            return;
        }

        // Busca el componente en el caché para obtener su tipo
        const comp = datosComponente.componentes.find(c => c.id == idElegido);
        if (!comp) { mostrarCampoPicos(false); return; }

        // El label del componente tiene formato "SERIE — MARCA MODELO"
        // El tipo está en el objeto tiposComponente del caché; lo buscamos por el idTipo del componente
        // Como el datalist solo tiene label, usamos el nombreTipoComponente que viene en comp.tipoNombre
        mostrarCampoPicos(tipoRequierePicos(comp.tipoNombre));
    }

    function sincronizarTipoComponente() {
        const texto = document.getElementById('mco_inputTipo').value.trim();
        const opciones = document.querySelectorAll('#mco_listaTipos option');
        let id = '';
        opciones.forEach(op => { if (op.value === texto) id = op.getAttribute('data-id'); });
        document.getElementById('mco_idTipo').value = id;
    }

    function filtrarModelosComponente() {
        const texto = document.getElementById('mco_inputMarca').value.trim();
        const opciones = document.querySelectorAll('#mco_listaMarcas option');
        let idMarca = '';
        opciones.forEach(op => { if (op.value === texto) idMarca = op.getAttribute('data-id'); });
        document.getElementById('mco_idMarca').value = idMarca;
        document.getElementById('mco_inputModelo').value = '';
        document.getElementById('mco_idModelo').value = '';
        if (!datosComponente) return;
        const filtrados = idMarca
            ? datosComponente.modelos.filter(m => m.idMarca == idMarca)
            : datosComponente.modelos;
        poblarDatalist('mco_listaModelos', filtrados);
    }

    function sincronizarModeloComponente() {
        const texto = document.getElementById('mco_inputModelo').value.trim();
        const opciones = document.querySelectorAll('#mco_listaModelos option');
        let id = '';
        opciones.forEach(op => { if (op.value === texto) id = op.getAttribute('data-id'); });
        document.getElementById('mco_idModelo').value = id;
    }

    function guardarComponente() {
        mostrarErrorModal('errorModalComponente', '');
        const body = new URLSearchParams({
            action:           'ajaxGuardarComponente',
            idTipoComponente: document.getElementById('mco_idTipo').value,
            idMarca:          document.getElementById('mco_idMarca').value,
            idModelo:         document.getElementById('mco_idModelo').value,
            numeroSerie:      document.getElementById('mco_serie').value,
            observaciones:    document.getElementById('mco_observaciones').value,
        });

        fetch(BASE + '/ordenes-trabajo', { method: 'POST', body })
            .then(r => r.json())
            .then(data => {
                if (!data.ok) { mostrarErrorModal('errorModalComponente', data.error); return; }
                agregarYSeleccionarEnDatalist('listaComponentes', 'idComponente', 'buscarComponente', data.id, data.etiqueta);
                datosComponente = null;
                cerrarModal('modalComponente');
            })
            .catch(() => mostrarErrorModal('errorModalComponente', 'Error de comunicación con el servidor.'));
    }

    // ----------------------------------------------------------------
    // LÓGICA DEL FORMULARIO PRINCIPAL
    // ----------------------------------------------------------------

    // Muestra la sección de vehículo o componente según el radio seleccionado
    function mostrarSeccionTipo(valor) {
        document.getElementById('seccionVehiculo').style.display   = valor === 'VEHICULO'   ? 'block' : 'none';
        document.getElementById('seccionComponente').style.display = valor === 'COMPONENTE' ? 'block' : 'none';
        // Limpia el campo oculto del tipo no seleccionado
        if (valor === 'VEHICULO')   document.getElementById('idComponente').value = '';
        else                        document.getElementById('idVehiculo').value   = '';
    }

    // Muestra u oculta la sección del referidor según la fuente elegida
    function mostrarSeccionReferidor(valor) {
        const visible = valor !== 'NINGUNA';
        document.getElementById('seccionReferidor').style.display = visible ? 'block' : 'none';
        if (!visible) {
            document.getElementById('idReferidor').value     = '';
            document.getElementById('buscarReferidor').value = '';
        }
    }

    // Al cargar la página deja visible la sección correcta según el valor guardado
    document.addEventListener('DOMContentLoaded', function () {
        const radios = document.querySelectorAll('input[name="tipoIngreso"]');
        radios.forEach(r => { if (r.checked) mostrarSeccionTipo(r.value); });

        const fuente = document.getElementById('fuenteReferencia');
        if (fuente) mostrarSeccionReferidor(fuente.value);

        // En edición, si ya hay un componente seleccionado, cargamos los datos del servidor
        // para saber su tipo y mostrar o no el campo de picos
        const idComponenteActual = document.getElementById('idComponente').value;
        if (idComponenteActual) {
            fetch(BASE + '/ordenes-trabajo?action=ajaxComponentes')
                .then(r => r.json())
                .then(data => {
                    datosComponente = data;
                    const comp = data.componentes.find(c => c.id == idComponenteActual);
                    if (comp) mostrarCampoPicos(tipoRequierePicos(comp.tipoNombre));
                });
        }
    });

</script>

</body>
</html>
