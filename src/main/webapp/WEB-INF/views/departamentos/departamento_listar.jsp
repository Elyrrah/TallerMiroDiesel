<%-- 
    Document   : departamento_listar
    Created on : 20 ene. 2026, 11:24:36 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Departamentos</title>
</head>
<body>

<h1>Departamentos</h1>

<c:if test="${not empty error}">
    <div style="color: red; margin-bottom: 10px;">
        ${error}
    </div>
</c:if>

<div style="margin-bottom: 12px;">
    <a href="${pageContext.request.contextPath}/departamentos?accion=nuevo">Nuevo Departamento</a>
</div>

<!-- FILTRO POR PAÍS -->
<form method="get" action="${pageContext.request.contextPath}/departamentos" style="margin-bottom: 12px;">
    <input type="hidden" name="accion" value="listar" />

    <label>Filtrar por país:</label>
    <select name="idPais">
        <option value="">-- Todos --</option>
        <c:forEach var="p" items="${paises}">
            <option value="${p.idPais}"
                <c:if test="${p.idPais == idPaisSeleccionado}">selected</c:if>>
                ${p.nombre}
            </option>
        </c:forEach>
    </select>

    <button type="submit">Filtrar</button>

    <a href="${pageContext.request.contextPath}/departamentos?accion=listar" style="margin-left: 10px;">
        Limpiar
    </a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Departamento</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach var="d" items="${departamentos}">
            <tr>
                <td>${d.idDepartamento}</td>

                <!-- País - Departamento -->
                <td>${d.nombrePais} - ${d.nombre}</td>

                <td>
                    <c:choose>
                        <c:when test="${d.activo}">SI</c:when>
                        <c:otherwise>NO</c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <!-- Link Editar preserva filtro -->
                    <a href="${pageContext.request.contextPath}/departamentos?accion=editar&id=${d.idDepartamento}<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if>">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${d.activo}">
                            | <a href="${pageContext.request.contextPath}/departamentos?accion=desactivar&id=${d.idDepartamento}<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if>">
                                Desactivar
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/departamentos?accion=activar&id=${d.idDepartamento}<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if>">
                                Activar
                              </a>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>


        <c:if test="${empty departamentos}">
            <tr>
                <td colspan="4">No hay departamentos para mostrar.</td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>
