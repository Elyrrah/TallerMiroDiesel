<%-- 
    Document   : departamento_form
    Created on : 20 ene. 2026, 11:25:16 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Departamento</title>
</head>
<body>

<c:set var="esEdicion" value="${not empty departamento.idDepartamento}" />
<c:set var="accionForm" value="${esEdicion ? 'actualizar' : 'crear'}" />

<h1>
    <c:choose>
        <c:when test="${esEdicion}">Editar Departamento</c:when>
        <c:otherwise>Nuevo Departamento</c:otherwise>
    </c:choose>
</h1>

<c:if test="${not empty error}">
    <div style="color: red; margin-bottom: 10px;">
        ${error}
    </div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/departamentos">
    <input type="hidden" name="accion" value="${accionForm}" />

    <c:if test="${esEdicion}">
        <input type="hidden" name="idDepartamento" value="${departamento.idDepartamento}" />
    </c:if>

    <!-- SELECT DE PAÍS (equivalente a JComboBox) -->
    <div style="margin-bottom: 10px;">
        <label>País:</label><br/>
        <select name="idPais" required>
            <option value="">-- Seleccione un país --</option>

            <c:forEach var="p" items="${paises}">
                <option value="${p.idPais}"
                    <c:if test="${p.idPais == departamento.idPais}">selected</c:if>>
                    ${p.nombre}
                </option>
            </c:forEach>
        </select>
    </div>

    <div style="margin-bottom: 10px;">
        <label>Nombre del Departamento:</label><br/>
        <input type="text" name="nombre" value="${departamento.nombre}" required />
    </div>

    <c:if test="${esEdicion}">
        <div style="margin-bottom: 10px;">
            <label>Activo:</label><br/>
            <select name="activo">
                <option value="true"  ${departamento.activo ? 'selected' : ''}>SI</option>
                <option value="false" ${!departamento.activo ? 'selected' : ''}>NO</option>
            </select>
        </div>
    </c:if>

    <button type="submit">
        <c:choose>
            <c:when test="${esEdicion}">Guardar cambios</c:when>
            <c:otherwise>Crear</c:otherwise>
        </c:choose>
    </button>

    <a style="margin-left: 10px;"
       href="${pageContext.request.contextPath}/departamentos?accion=listar">
        Volver
    </a>
</form>

</body>
</html>
