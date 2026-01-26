<%-- 
    Document   : modelo_form
    Created on : 26 ene. 2026, 2:30:07 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Modelo</title>
</head>
<body>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<c:set var="esEdicion" value="${not empty modelo.idModelo}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Modelo</c:when>
        <c:otherwise>Nuevo Modelo</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/modelos">

    <input type="hidden" name="accion" value="guardar" />

    <c:if test="${esEdicion}">
        <input type="hidden" name="idModelo" value="${modelo.idModelo}" />
    </c:if>

    <%-- Preservar filtros del listado --%>
    <input type="hidden" name="idMarcaFiltro" value="${param.idMarca}" />
    <input type="hidden" name="filtro" value="${param.filtro}" />

    <div>
        <label>Marca</label><br/>
        <select name="idMarca" required>
            <option value="">-- Seleccione --</option>
            <c:forEach var="m" items="${marcas}">
                <option value="${m.idMarca}"
                        <c:if test="${modelo.idMarca == m.idMarca}">selected</c:if>>
                    ${m.nombre}
                </option>
            </c:forEach>
        </select>
    </div>

    <div>
        <label>Nombre</label><br/>
        <input type="text"
               name="nombre"
               value="${modelo.nombre}"
               required />
    </div>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true" ${modelo.activo ? 'selected' : ''}>Sí</option>
                <option value="false" ${!modelo.activo ? 'selected' : ''}>No</option>
            </select>
        </div>
    </c:if>

    <br/>

    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/modelos?accion=listar&idMarca=${param.idMarca}&filtro=${param.filtro}">
        Cancelar
    </a>
</form>

</body>
</html>
