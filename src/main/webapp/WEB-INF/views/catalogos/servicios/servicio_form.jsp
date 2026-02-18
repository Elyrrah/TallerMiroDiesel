<%-- 
    Document   : servicio_form
    Created on : 27 ene. 2026, 10:47:39 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Servicio</title>
</head>
<body>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<c:set var="esEdicion" value="${not empty servicio.idServicio}" />
<fmt:formatNumber value="${servicio.precioBase}" pattern="0" var="precioFormateado" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Servicio</c:when>
        <c:otherwise>Nuevo Servicio</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/servicios">
    <input type="hidden" name="action" value="guardar" />
    <c:if test="${esEdicion}">
        <input type="hidden" name="idServicio" value="${servicio.idServicio}" />
    </c:if>

    <div>
        <label>Código</label><br/>
        <input type="text"
               name="codigo"
               value="${servicio.codigo}"
               required />
    </div>

    <div>
        <label>Nombre</label><br/>
        <input type="text"
               name="nombre"
               value="${servicio.nombre}"
               required />
    </div>

    <div>
        <label>Descripción</label><br/>
        <textarea name="descripcion" rows="4" cols="40">${servicio.descripcion}</textarea>
    </div>

    <div>
        <label>Precio Base (₲)</label><br/>
        <input type="number"
               name="precioBase"
               value="${precioFormateado}"
               step="1"
               min="0"
               required />
    </div>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true"  ${servicio.activo ? 'selected' : ''}>Sí</option>
                <option value="false" ${!servicio.activo ? 'selected' : ''}>No</option>
            </select>
        </div>
    </c:if>

    <br/>
    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/servicios?action=listar">
        Cancelar
    </a>
</form>

</body>
</html>