<%-- 
    Document   : vehiculo_form
    Created on : 23 feb. 2026, 8:42:43 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Vehículo</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>
<p><a href="${pageContext.request.contextPath}/vehiculos?action=listar">Volver al listado</a></p>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<c:set var="esEdicion" value="${not empty vehiculo.idVehiculo}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Vehículo</c:when>
        <c:otherwise>Nuevo Vehículo</c:otherwise>
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

    // Cuando el usuario elige una marca, filtra los modelos del datalist
    document.addEventListener('DOMContentLoaded', function () {

        const inputMarca    = document.getElementById('inputMarca');
        const hiddenMarca   = document.getElementById('hiddenMarca');
        const inputModelo   = document.getElementById('inputModelo');
        const hiddenModelo  = document.getElementById('hiddenModelo');
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
            // Limpia el modelo elegido porque puede no pertenecer a esta marca
            inputModelo.value  = '';
            hiddenModelo.value = '';
        }

        // Cuando cambia la marca
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

        // Cuando cambia el modelo
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
            // Restaura el nombre del modelo preseleccionado
            const modeloPre = modelos.find(m => m.id === hiddenModelo.value);
            if (modeloPre) inputModelo.value = modeloPre.nombre;
        }
    });
</script>

<form method="post" action="${pageContext.request.contextPath}/vehiculos">
    <input type="hidden" name="action" value="guardar" />
    <c:if test="${esEdicion}">
        <input type="hidden" name="idVehiculo" value="${vehiculo.idVehiculo}" />
    </c:if>

    <div>
        <label>Placa <small>(opcional)</small></label><br/>
        <input type="text" name="placa" value="${vehiculo.placa}" placeholder="Ej: ABC123" />
    </div><br/>

    <div>
        <label>Marca *</label><br/>
        <input type="text" id="inputMarca" list="listMarcas"
               placeholder="Escribí para buscar..." autocomplete="off"
               value="${vehiculo.nombreMarca}" required />
        <datalist id="listMarcas">
            <c:forEach var="m" items="${marcas}">
                <option value="${m.nombre}" data-id="${m.idMarca}"></option>
            </c:forEach>
        </datalist>
        <input type="hidden" name="idMarca" id="hiddenMarca" value="${vehiculo.idMarca}" />
    </div><br/>

    <div>
        <label>Modelo <small>(opcional)</small></label><br/>
        <input type="text" id="inputModelo" list="listModelos"
               placeholder="Seleccioná una marca primero..." autocomplete="off"
               value="${vehiculo.nombreModelo}" />
        <datalist id="listModelos">
            <%-- Se puebla por JavaScript al elegir la marca --%>
        </datalist>
        <input type="hidden" name="idModelo" id="hiddenModelo" value="${vehiculo.idModelo}" />
    </div><br/>

    <div>
        <label>Año <small>(opcional)</small></label><br/>
        <input type="number" name="anio" value="${vehiculo.anio}"
               min="1900" max="2100" placeholder="Ej: 2015" />
    </div><br/>

    <div>
        <label>Tipo de vehículo *</label><br/>
        <select name="tipoVehiculo" required>
            <option value="">-- Seleccionar tipo --</option>
            <c:forEach var="tipo" items="${tiposVehiculo}">
                <option value="${tipo}"
                    ${vehiculo.tipoVehiculo == tipo ? 'selected' : ''}>
                    ${tipo}
                </option>
            </c:forEach>
        </select>
    </div><br/>

    <div>
        <label>Observaciones <small>(opcional)</small></label><br/>
        <textarea name="observaciones" rows="3" cols="40">${vehiculo.observaciones}</textarea>
    </div><br/>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true"  ${vehiculo.activo  ? 'selected' : ''}>Sí</option>
                <option value="false" ${!vehiculo.activo ? 'selected' : ''}>No</option>
            </select>
        </div><br/>
    </c:if>

    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/vehiculos?action=listar">Cancelar</a>
</form>

</body>
</html>