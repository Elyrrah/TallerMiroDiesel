<%-- 
    Document   : componente_form
    Created on : 23 feb. 2026, 9:00:17 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Componente</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>
<p><a href="${pageContext.request.contextPath}/componentes?action=listar">Volver al listado</a></p>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<c:set var="esEdicion" value="${not empty componente.idComponente}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Componente</c:when>
        <c:otherwise>Nuevo Componente</c:otherwise>
    </c:choose>
</h2>

<%-- Datos embebidos en JS para filtrar modelos por marca sin llamadas al servidor --%>
<script>
    const marcas = {
        <c:forEach var="m" items="${marcas}" varStatus="st">
        "${m.idMarca}": "${m.nombre}"<c:if test="${!st.last}">,</c:if>
        </c:forEach>
    };

    const modelos = [
        <c:forEach var="mo" items="${modelos}" varStatus="st">
        { id: "${mo.idModelo}", nombre: "${mo.nombre}", idMarca: "${mo.idMarca}" }<c:if test="${!st.last}">,</c:if>
        </c:forEach>
    ];

    document.addEventListener('DOMContentLoaded', function () {

        const inputMarca      = document.getElementById('inputMarca');
        const hiddenMarca     = document.getElementById('hiddenMarca');
        const inputModelo     = document.getElementById('inputModelo');
        const hiddenModelo    = document.getElementById('hiddenModelo');
        const datalistModelos = document.getElementById('listModelos');

        function poblarTodosLosModelos() {
            datalistModelos.innerHTML = '';
            modelos.forEach(m => {
                const opt = document.createElement('option');
                opt.value = m.nombre;
                opt.setAttribute('data-id', m.id);
                opt.setAttribute('data-marca', m.idMarca);
                datalistModelos.appendChild(opt);
            });
        }

        function filtrarModelos(idMarca) {
            datalistModelos.innerHTML = '';
            modelos
                .filter(m => m.idMarca === idMarca)
                .forEach(m => {
                    const opt = document.createElement('option');
                    opt.value = m.nombre;
                    opt.setAttribute('data-id', m.id);
                    opt.setAttribute('data-marca', m.idMarca);
                    datalistModelos.appendChild(opt);
                });
            inputModelo.value  = '';
            hiddenModelo.value = '';
        }

        inputMarca.addEventListener('change', function () {
            const texto = this.value.trim().toUpperCase();
            const match = Object.entries(marcas).find(([id, nombre]) => nombre.toUpperCase() === texto);

            if (match) {
                hiddenMarca.value = match[0];
                filtrarModelos(match[0]);
            } else {
                hiddenMarca.value = '';
                poblarTodosLosModelos();
                inputModelo.value  = '';
                hiddenModelo.value = '';
            }
        });

        inputModelo.addEventListener('change', function () {
            const texto = this.value.trim().toUpperCase();
            const opciones = Array.from(datalistModelos.options);
            const match = opciones.find(o => o.value.toUpperCase() === texto);

            if (match) {
                hiddenModelo.value = match.getAttribute('data-id');
            } else {
                hiddenModelo.value = '';
            }
        });

        // Inicialización: si hay marca preseleccionada, filtra sus modelos
        poblarTodosLosModelos();
        if (hiddenMarca.value) {
            filtrarModelos(hiddenMarca.value);
            const modeloPre = modelos.find(m => m.id === hiddenModelo.value);
            if (modeloPre) inputModelo.value = modeloPre.nombre;
        }
    });
</script>

<form method="post" action="${pageContext.request.contextPath}/componentes">
    <input type="hidden" name="action" value="guardar" />
    <c:if test="${esEdicion}">
        <input type="hidden" name="idComponente" value="${componente.idComponente}" />
    </c:if>

    <div>
        <label>Tipo de componente *</label><br/>
        <input type="text" list="listTiposComponente"
               placeholder="Escribí para buscar..." autocomplete="off"
               id="inputTipoComponente" />
        <datalist id="listTiposComponente">
            <c:forEach var="tc" items="${tiposComponente}">
                <option value="${tc.nombre}" data-id="${tc.idTipoComponente}"></option>
            </c:forEach>
        </datalist>
        <input type="hidden" name="idTipoComponente" id="hiddenTipoComponente" value="${componente.idTipoComponente}" />
    </div><br/>

    <div>
        <label>Marca *</label><br/>
        <input type="text" id="inputMarca" list="listMarcas"
               placeholder="Escribí para buscar..." autocomplete="off"
               value="${componente.nombreMarca}" />
        <datalist id="listMarcas">
            <c:forEach var="m" items="${marcas}">
                <option value="${m.nombre}" data-id="${m.idMarca}"></option>
            </c:forEach>
        </datalist>
        <input type="hidden" name="idMarca" id="hiddenMarca" value="${componente.idMarca}" />
    </div><br/>

    <div>
        <label>Modelo *</label><br/>
        <input type="text" id="inputModelo" list="listModelos"
               placeholder="Seleccioná una marca primero..." autocomplete="off"
               value="${componente.nombreModelo}" />
        <datalist id="listModelos">
            <%-- Se puebla por JavaScript al elegir la marca --%>
        </datalist>
        <input type="hidden" name="idModelo" id="hiddenModelo" value="${componente.idModelo}" />
    </div><br/>

    <div>
        <label>Número de serie <small>(opcional)</small></label><br/>
        <input type="text" name="numeroSerie" value="${componente.numeroSerie}" placeholder="Ej: SN-12345" />
    </div><br/>

    <div>
        <label>Observaciones <small>(opcional)</small></label><br/>
        <textarea name="observaciones" rows="3" cols="40">${componente.observaciones}</textarea>
    </div><br/>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true"  ${componente.activo  ? 'selected' : ''}>Sí</option>
                <option value="false" ${!componente.activo ? 'selected' : ''}>No</option>
            </select>
        </div><br/>
    </c:if>

    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/componentes?action=listar">Cancelar</a>
</form>

<%-- Lógica del datalist de tipo componente --%>
<script>
    document.getElementById('inputTipoComponente').addEventListener('change', function () {
        const texto = this.value.trim().toUpperCase();
        const opciones = Array.from(document.getElementById('listTiposComponente').options);
        const match = opciones.find(o => o.value.toUpperCase() === texto);

        if (match) {
            document.getElementById('hiddenTipoComponente').value = match.getAttribute('data-id');
        } else {
            document.getElementById('hiddenTipoComponente').value = '';
        }
    });

    // Restaura el nombre del tipo preseleccionado en edición
    (function () {
        const hidden = document.getElementById('hiddenTipoComponente');
        if (hidden.value) {
            const opciones = Array.from(document.getElementById('listTiposComponente').options);
            const match = opciones.find(o => o.getAttribute('data-id') === hidden.value);
            if (match) document.getElementById('inputTipoComponente').value = match.value;
        }
    })();
</script>

</body>
</html>