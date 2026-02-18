<%-- 
    Document   : tipos_documento_listar
    Created on : 27 ene. 2026, 9:55:28 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tipos de Documento</title>

    <style>
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
        .switch:hover {
            filter: brightness(0.95);
        }
        .switch.off::after {
            border-color: #c0392b;
        }
    </style>
</head>
<body>

<p>
    <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
</p>

<h2>Catálogo de Tipos de Documento</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/tipos-documento?action=nuevo">
        Nuevo Tipo de Documento
    </a>
</p>

<form class="buscador" method="get" action="${pageContext.request.contextPath}/tipos-documento">
    <%-- CORRECCIÓN: value="buscar" igual que el patrón del repositorio --%>
    <input type="hidden" name="action" value="buscar" />
    <label>Buscar:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Nombre..." />
    <button type="submit">Filtrar</button>
    <a href="${pageContext.request.contextPath}/tipos-documento?action=listar">Limpiar</a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Código</th>
            <th>Aplica A</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="td" items="${tiposDocumento}">
            <tr>
                <td>${td.idTipoDocumento}</td>
                <td>${td.nombre}</td>
                <td>${td.codigo}</td>
                <td>${td.aplicaA}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/tipos-documento?action=editar&id=${td.idTipoDocumento}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${td.activo}">
                            | <a class="switch on"
                                 title="Desactivar"
                                 href="${pageContext.request.contextPath}/tipos-documento?action=desactivar&id=${td.idTipoDocumento}">
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off"
                                 title="Activar"
                                 href="${pageContext.request.contextPath}/tipos-documento?action=activar&id=${td.idTipoDocumento}">
                              </a>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>