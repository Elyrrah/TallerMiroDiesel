<%-- 
    Document   : orden_trabajo_detalle_form
    Created on : 23 feb. 2026, 1:52:17 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Agregar Servicio</title>
</head>
<body>

<p>
    <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=ver&id=${idOrden}">
        Volver a la Orden de Trabajo
    </a>
</p>

<%-- Mensaje de error --%>
<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<c:set var="esEdicion" value="${not empty detalle.idDetalle}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Servicio</c:when>
        <c:otherwise>Agregar Servicio</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/ordenes-trabajo">
    <input type="hidden" name="action" value="guardarDetalle" />
    <input type="hidden" name="idOrdenTrabajo" value="${not empty detalle.idOrdenTrabajo ? detalle.idOrdenTrabajo : idOrden}" />

    <c:if test="${esEdicion}">
        <input type="hidden" name="idDetalle" value="${detalle.idDetalle}" />
    </c:if>

    <%-- Selector de servicio — carga el precio base al seleccionar --%>
    <div>
        <label>Servicio:</label><br/>
        <select name="idServicio" id="idServicio" required onchange="cargarPrecio(this)">
            <option value="">-- Seleccione un servicio --</option>
            <c:forEach var="s" items="${servicios}">
                <option value="${s.idServicio}"
                        data-precio="${s.precioBase}"
                        ${detalle.idServicio == s.idServicio ? 'selected' : ''}>
                    ${s.codigo} — ${s.nombre}
                </option>
            </c:forEach>
        </select>
    </div>

    <%-- Cantidad --%>
    <div style="margin-top:8px;">
        <label>Cantidad:</label><br/>
        <input type="number"
               name="cantidad"
               id="cantidad"
               value="${not empty detalle.cantidad ? detalle.cantidad : 1}"
               min="0.01"
               step="0.01"
               required
               onchange="calcularSubtotal()" />
    </div>

    <%-- Precio unitario — se autocompleta desde el catálogo pero es editable --%>
    <div style="margin-top:8px;">
        <label>Precio Unitario (₲):</label><br/>
        <input type="number"
               name="precioUnitario"
               id="precioUnitario"
               value="${not empty detalle.precioUnitario ? detalle.precioUnitario : ''}"
               min="0"
               step="1"
               onchange="calcularSubtotal()" />
        <small>Se completa automáticamente desde el catálogo. Podés modificarlo si corresponde.</small>
    </div>

    <%-- Subtotal calculado en tiempo real (solo informativo, no se envía al servidor) --%>
    <div style="margin-top:8px;">
        <label>Subtotal estimado (₲):</label><br/>
        <span id="subtotalMostrado">—</span>
    </div>

    <%-- Garantía opcional --%>
    <div style="margin-top:8px;">
        <label>Garantía (opcional):</label><br/>
        <input type="number"
               name="garantiaMeses"
               value="${detalle.garantiaMeses}"
               min="0"
               placeholder="Meses" style="width:80px;" />
        meses &nbsp;
        <input type="number"
               name="garantiaDias"
               value="${detalle.garantiaDias}"
               min="0"
               placeholder="Días" style="width:80px;" />
        días
    </div>

    <%-- Observaciones opcionales --%>
    <div style="margin-top:8px;">
        <label>Observaciones (opcional):</label><br/>
        <textarea name="observaciones" rows="2" cols="50">${detalle.observaciones}</textarea>
    </div>

    <br/>
    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=ver&id=${not empty detalle.idOrdenTrabajo ? detalle.idOrdenTrabajo : idOrden}">
        Cancelar
    </a>
</form>

<%-- JavaScript: carga el precio del catálogo y calcula el subtotal en tiempo real --%>
<script>

    // Cuando el usuario elige un servicio, carga el precio base desde el atributo data-precio
    function cargarPrecio(select) {
        const opcion = select.options[select.selectedIndex];
        const precio = opcion.getAttribute('data-precio');
        if (precio) {
            document.getElementById('precioUnitario').value = precio;
            calcularSubtotal();
        }
    }

    // Calcula el subtotal en tiempo real para que el usuario lo vea antes de guardar
    function calcularSubtotal() {
        const cantidad = parseFloat(document.getElementById('cantidad').value) || 0;
        const precio   = parseFloat(document.getElementById('precioUnitario').value) || 0;
        const subtotal = cantidad * precio;
        document.getElementById('subtotalMostrado').textContent =
            subtotal > 0 ? subtotal.toLocaleString('es-PY') + ' ₲' : '—';
    }

    // Al cargar la página, si ya hay un servicio seleccionado calcula el subtotal
    document.addEventListener('DOMContentLoaded', function () {
        const select = document.getElementById('idServicio');
        if (select.value) {
            calcularSubtotal();
        }
    });

</script>

</body>
</html>
