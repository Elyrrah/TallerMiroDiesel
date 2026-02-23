<%-- 
    Document   : tipo_componente_form
    Created on : 20 feb. 2026, 11:38:43 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Tipo de Componente</title>
</head>
<body>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<c:set var="esEdicion" value="${not empty tipoComponente.idTipoComponente}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Tipo de Componente</c:when>
        <c:otherwise>Nuevo Tipo de Componente</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/tipos-componente">
    <input type="hidden" name="action" value="guardar" />
    <c:if test="${esEdicion}">
        <input type="hidden"
               name="idTipoComponente"
               value="${tipoComponente.idTipoComponente}" />
    </c:if>

    <div>
        <label>Nombre</label><br/>
        <input type="text"
               name="nombre"
               value="${tipoComponente.nombre}"
               required />
    </div>

    <div>
        <label>Descripción</label><br/>
        <textarea name="descripcion" rows="3" cols="40">${tipoComponente.descripcion}</textarea>
    </div>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true"  ${tipoComponente.activo ? 'selected' : ''}>Sí</option>
                <option value="false" ${!tipoComponente.activo ? 'selected' : ''}>No</option>
            </select>
        </div>
    </c:if>

    <br/>
    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/tipos-componente?action=listar">
        Cancelar
    </a>
</form>

</body>
</html>