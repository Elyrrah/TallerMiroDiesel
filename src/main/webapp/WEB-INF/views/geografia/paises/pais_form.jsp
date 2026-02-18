<%-- 
    Document   : pais_form
    Created on : 19 ene. 2026, 10:01:12 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario País</title>
</head>
<body>
<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>
<c:set var="esEdicion" value="${not empty pais.idPais}" />
<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar País</c:when>
        <c:otherwise>Nuevo País</c:otherwise>
    </c:choose>
</h2>
<form method="post" action="${pageContext.request.contextPath}/paises">
    <input type="hidden" name="action" value="guardar" />
    <c:if test="${esEdicion}">
        <input type="hidden" name="idPais" value="${pais.idPais}" />
    </c:if>
    <div>
        <label>Nombre</label><br/>
        <input type="text"
               name="nombre"
               value="${pais.nombre}"
               required />
    </div>
    <div>
        <label>ISO2</label><br/>
        <input type="text"
               name="iso2"
               value="${pais.iso2}"
               maxlength="2"
               required />
    </div>
    <div>
        <label>ISO3</label><br/>
        <input type="text"
               name="iso3"
               value="${pais.iso3}"
               maxlength="3" />
    </div>
    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true" ${pais.activo ? 'selected' : ''}>Sí</option>
                <option value="false" ${!pais.activo ? 'selected' : ''}>No</option>
            </select>
        </div>
    </c:if>
    <br/>
    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/paises?action=listar">
        Cancelar
    </a>
</form>
</body>
</html>