<%-- 
    Document   : marca_form
    Created on : 26 ene. 2026, 9:34:10 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Marca</title>
</head>
<body>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<c:set var="esEdicion" value="${not empty marca.idMarca}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Marca</c:when>
        <c:otherwise>Nueva Marca</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/marcas">

    <input type="hidden" name="accion" value="guardar" />

    <c:if test="${esEdicion}">
        <input type="hidden" name="idMarca" value="${marca.idMarca}" />
    </c:if>

    <div>
        <label>Nombre</label><br/>
        <input type="text"
               name="nombre"
               value="${marca.nombre}"
               required />
    </div>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true" ${marca.activo ? 'selected' : ''}>Sí</option>
                <option value="false" ${!marca.activo ? 'selected' : ''}>No</option>
            </select>
        </div>
    </c:if>

    <br/>

    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/marcas?accion=listar">
        Cancelar
    </a>
</form>

</body>
</html>
