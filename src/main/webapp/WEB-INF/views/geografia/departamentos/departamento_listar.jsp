<%-- 
    Document   : departamento_listar
    Created on : 20 ene. 2026, 11:24:36 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Departamentos</title>

    <style>
        /* Buscador simple */
        .buscador {
            margin: 10px 0 15px 0;
        }
        .buscador input[type="text"] {
            padding: 6px;
            width: 260px;
        }
        .buscador button, .buscador a {
            padding: 6px 10px;
            margin-left: 6px;
        }

        /* Toggle tipo switch (link) */
        .switch {
            display: inline-block;
            width: 46px;
            height: 24px;
            border-radius: 999px;
            position: relative;
            vertical-align: middle;
            text-decoration: none;
            border: 1px solid #999;
            background: #ddd;
        }
        .switch::after {
            content: "";
            position: absolute;
            top: 3px;
            left: 3px;
            width: 18px;
            height: 18px;
            border-radius: 50%;
            background: #fff;
            border: 1px solid #999;
            transition: left 0.15s ease-in-out;
        }
        .switch.on {
            background: #4CAF50;
            border-color: #3E8E41;
        }
        .switch.on::after {
            left: 24px;
        }
        .switch.off {
            background: #e74c3c;
            border-color: #c0392b;
        }
        .switch.off::after {
            border-color: #c0392b;
        }

        .switch:hover {
            filter: brightness(0.95);
        }
    </style>
</head>
<body>

<!-- Volver a la página principal -->
<p>
    <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
</p>

<h1>Departamentos</h1>

<c:if test="${not empty error}">
    <div style="color: red; margin-bottom: 10px;">
        ${error}
    </div>
</c:if>

<div style="margin-bottom: 12px;">
    <a href="${pageContext.request.contextPath}/departamentos?action=nuevo">Nuevo Departamento</a>
</div>

<!-- FILTRO POR PAÍS -->
<form method="get" action="${pageContext.request.contextPath}/departamentos" style="margin-bottom: 12px;">
    <input type="hidden" name="action" value="list" />

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

    <a href="${pageContext.request.contextPath}/departamentos?action=list" style="margin-left: 10px;">
        Limpiar
    </a>
</form>

<!-- Buscador / filtro -->
<form class="buscador" method="get" action="${pageContext.request.contextPath}/departamentos">
    <input type="hidden" name="action" value="list" />

    <c:if test="${not empty idPaisSeleccionado}">
        <input type="hidden" name="idPais" value="${idPaisSeleccionado}" />
    </c:if>

    <label>Buscar:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Nombre..." />
    <button type="submit">Buscar</button>

    <a href="${pageContext.request.contextPath}/departamentos?action=list<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if>">
        Limpiar
    </a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>País</th>
            <th>Departamento</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>

    <tbody>
        <c:forEach var="d" items="${departamentos}">
            <tr>
                <td>${d.idDepartamento}</td>
                <td>${d.nombrePais}</td>
                <td>${d.nombre}</td>

                <td>
                    <c:choose>
                        <c:when test="${d.activo}">SI</c:when>
                        <c:otherwise>NO</c:otherwise>
                    </c:choose>
                </td>

                <td>
                    <a href="${pageContext.request.contextPath}/departamentos?action=edit&id=${d.idDepartamento}<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if><c:if test='${not empty filtro}'>&filtro=${filtro}</c:if>">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${d.activo}">
                            | <a class="switch on"
                                 title="Desactivar"
                                 href="${pageContext.request.contextPath}/departamentos?action=deactivate&id=${d.idDepartamento}<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if><c:if test='${not empty filtro}'>&filtro=${filtro}</c:if>">
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off"
                                 title="Activar"
                                 href="${pageContext.request.contextPath}/departamentos?action=activate&id=${d.idDepartamento}<c:if test='${not empty idPaisSeleccionado}'>&idPais=${idPaisSeleccionado}</c:if><c:if test='${not empty filtro}'>&filtro=${filtro}</c:if>">
                              </a>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>

        <c:if test="${empty departamentos}">
            <tr>
                <td colspan="5">No hay departamentos para mostrar.</td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>
