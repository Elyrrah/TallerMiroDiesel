<%-- 
    Document   : servicio_listar
    Created on : 27 ene. 2026, 10:47:50 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Servicios</title>

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

        .precio {
            display: flex;
            justify-content: space-between;
            align-items: center;
            min-width: 130px;
        }
        .precio .simbolo {
            margin-right: 6px;
        }
        .precio .monto {
            text-align: right;
            flex: 1;
        }
    </style>
</head>
<body>

<p>
    <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
</p>

<h2>Catálogo de Servicios</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/servicios?action=nuevo">
        Nuevo Servicio
    </a>
</p>

<form class="buscador" method="get" action="${pageContext.request.contextPath}/servicios">
    <%-- CORRECCIÓN: value="buscar" igual que el patrón del repositorio --%>
    <input type="hidden" name="action" value="buscar" />
    <label>Buscar:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Código o nombre..." />
    <button type="submit">Filtrar</button>
    <a href="${pageContext.request.contextPath}/servicios?action=listar">Limpiar</a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Código</th>
            <th>Nombre</th>
            <th>Precio Base</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="s" items="${servicios}">
            <tr>
                <td>${s.idServicio}</td>
                <td>${s.codigo}</td>
                <td>${s.nombre}</td>

                <td>
                    <div class="precio">
                        <span class="simbolo">₲</span>
                        <span class="monto">
                            <fmt:formatNumber value="${s.precioBase}" pattern="#,##0" />
                        </span>
                    </div>
                </td>

                <td>
                    <a href="${pageContext.request.contextPath}/servicios?action=editar&id=${s.idServicio}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${s.activo}">
                            | <a class="switch on"
                                 title="Desactivar"
                                 href="${pageContext.request.contextPath}/servicios?action=desactivar&id=${s.idServicio}&filtro=${filtro}">
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off"
                                 title="Activar"
                                 href="${pageContext.request.contextPath}/servicios?action=activar&id=${s.idServicio}&filtro=${filtro}">
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