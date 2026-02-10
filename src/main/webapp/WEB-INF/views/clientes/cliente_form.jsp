<%-- 
    Document   : cliente_form
    Created on : 2 feb. 2026, 3:37:10 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nuevo Cliente</title>
</head>
<body>

    <p>
        <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/clientes?action=listar">Volver al listado</a>
    </p>

    <h2>Nuevo Cliente</h2>

    <c:if test="${not empty error}">
        <div style="color:#b00000; margin-bottom:10px;">
            ${error}
        </div>
    </c:if>

    <c:if test="${not empty errorDetalle}">
        <div style="color:#b00000; margin-bottom:10px;">
            ${errorDetalle}
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/clientes">
        <input type="hidden" name="action" value="guardar"/>

        <!-- ================= TIPO DE CLIENTE ================= -->
        <fieldset style="margin-bottom:12px;">
            <legend>Tipo de Cliente</legend>

            <label>
                <input type="radio" name="tipo" value="PERSONA"
                       <c:if test="${tipo == 'PERSONA' or empty tipo}">checked</c:if>
                       onclick="toggleTipo('PERSONA')"/>
                Persona
            </label>

            <label style="margin-left:14px;">
                <input type="radio" name="tipo" value="EMPRESA"
                       <c:if test="${tipo == 'EMPRESA'}">checked</c:if>
                       onclick="toggleTipo('EMPRESA')"/>
                Empresa
            </label>
        </fieldset>

        <!-- ================= DATOS COMUNES ================= -->
        <fieldset style="margin-bottom:12px;">
            <legend>Datos Comunes</legend>

            Teléfono:<br/>
            <input type="text" name="telefono" value="${cliente.telefono}"/><br/><br/>

            <!-- ========== DISTRITO ========== -->
            Distrito:<br/>
            <input type="text" id="buscaDistrito" placeholder="Filtrar distrito..." style="width:260px;"/><br/>
            <select name="id_distrito" id="selectDistrito" style="width:270px;">
                <option value="">-- Seleccione Distrito --</option>
                <c:forEach var="d" items="${distritos}">
                    <option value="${d.idDistrito}"
                            data-text="${d.nombre}"
                            <c:if test="${cliente.idDistrito == d.idDistrito}">selected</c:if>>
                        ${d.nombre}
                    </option>
                </c:forEach>
            </select>
            <br/><br/>

            <!-- ========== LOCALIDAD ========== -->
            Localidad:<br/>
            <input type="text" id="buscaLocalidad" placeholder="Filtrar localidad..." style="width:260px;"/><br/>
            <select name="id_localidad" id="selectLocalidad" style="width:270px;">
                <option value="">-- Seleccione Localidad --</option>
                <c:forEach var="l" items="${localidades}">
                    <option value="${l.idLocalidad}"
                            data-text="${l.nombre}"
                            <c:if test="${cliente.idLocalidad == l.idLocalidad}">selected</c:if>>
                        ${l.nombre}
                    </option>
                </c:forEach>
            </select>
            <br/><br/>

            <!-- ========== CLIENTE REFERIDOR ========== -->
            Cliente Referidor:<br/>
            <input type="text" id="buscaReferidor" placeholder="Filtrar referidor..." style="width:260px;"/><br/>
            <select name="id_cliente_referidor" id="selectReferidor" style="width:270px;">
                <option value="" data-text="">-- Sin referidor --</option>

                <c:forEach var="entry" items="${clientesReferidoresMap}">
                    <option value="${entry.key}"
                            data-text="${entry.value}"
                            <c:if test="${cliente.idClienteReferidor == entry.key}">selected</c:if>>
                        ${entry.value}
                    </option>
                </c:forEach>
            </select>
            <br/><br/>

            Fuente Referencia:<br/>
            <select name="fuente_referencia">
                <option value="">-- Seleccione --</option>
                <option value="RECOMENDACION"
                        <c:if test="${cliente.fuenteReferencia == 'RECOMENDACION'}">selected</c:if>>
                    RECOMENDACION
                </option>
                <option value="MECANICO"
                        <c:if test="${cliente.fuenteReferencia == 'MECANICO'}">selected</c:if>>
                    MECANICO
                </option>
            </select>
        </fieldset>

        <!-- ================= PERSONA ================= -->
        <fieldset id="fsPersona" style="margin-bottom:12px;">
            <legend>Datos de Persona</legend>

            Nombre:<br/>
            <input type="text" name="nombre" value="${nombre}"/><br/><br/>

            Apellido:<br/>
            <input type="text" name="apellido" value="${apellido}"/><br/><br/>

            Apodo:<br/>
            <input type="text" name="apodo" value="${apodo}"/><br/>
        </fieldset>

        <!-- ================= EMPRESA ================= -->
        <fieldset id="fsEmpresa" style="margin-bottom:12px;">
            <legend>Datos de Empresa</legend>

            Razón Social:<br/>
            <input type="text" name="razon_social" value="${razon_social}" style="width:360px;"/><br/><br/>

            Nombre Fantasía:<br/>
            <input type="text" name="nombre_fantasia" value="${nombre_fantasia}"/><br/>
        </fieldset>

        <!-- ================= DOCUMENTO (ALTA INICIAL) ================= -->
        <fieldset style="margin-bottom:12px;">
            <legend>Documento (opcional)</legend>

            Tipo Documento:<br/>
            <input type="text" id="buscaTipoDocumento" placeholder="Filtrar tipo documento..." style="width:260px;"/><br/>
            <select name="id_tipo_documento" id="selectTipoDocumento" style="width:270px;">
                <option value="">-- Seleccione Tipo Documento --</option>

                <c:forEach var="td" items="${tiposDocumento}">
                    <option value="${td.idTipoDocumento}"
                            data-text="${td.nombre}"
                            <c:if test="${id_tipo_documento == td.idTipoDocumento}">selected</c:if>>
                        ${td.nombre}
                    </option>
                </c:forEach>
            </select>
            <br/><br/>

            Número:<br/>
            <input type="text" name="numero_documento" value="${numero_documento}" style="width:260px;"/><br/><br/>

            <label>
                <input type="checkbox" name="principal_documento"
                       <c:if test="${principal_documento == 'on' or principal_documento == 'true' or principal_documento == '1'}">checked</c:if> />
                Marcar como principal
            </label>
        </fieldset>

        <button type="submit">Guardar</button>
    </form>

    <script>
        // Mostrar / ocultar secciones Persona / Empresa
        function toggleTipo(tipo) {
            document.getElementById('fsPersona').style.display = (tipo === 'PERSONA') ? 'block' : 'none';
            document.getElementById('fsEmpresa').style.display = (tipo === 'EMPRESA') ? 'block' : 'none';
        }

        // Filtro tipo JComboBox editable
        function wireFilter(inputId, selectId) {
            const input = document.getElementById(inputId);
            const select = document.getElementById(selectId);

            const options = Array.from(select.options).map(o => ({
                el: o,
                text: ((o.getAttribute("data-text") || o.text || "").toLowerCase()),
                placeholder: o.value === ""
            }));

            input.addEventListener("input", function () {
                const q = (input.value || "").trim().toLowerCase();
                options.forEach(o => {
                    if (o.placeholder) return;
                    o.el.hidden = (q && !o.text.includes(q));
                });

                const sel = select.options[select.selectedIndex];
                if (sel && sel.hidden) select.value = "";
            });
        }

        // Inicialización
        (function init() {
            var tipo = '${tipo}';
            if (!tipo) tipo = 'PERSONA';
            toggleTipo(tipo);

            wireFilter("buscaDistrito", "selectDistrito");
            wireFilter("buscaLocalidad", "selectLocalidad");
            wireFilter("buscaReferidor", "selectReferidor");

            // Documento
            wireFilter("buscaTipoDocumento", "selectTipoDocumento");
        })();
    </script>

</body>
</html>