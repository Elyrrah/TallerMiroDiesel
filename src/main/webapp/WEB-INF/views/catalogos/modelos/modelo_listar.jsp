<%-- 
    Document   : modelo_listar
    Created on : 26 ene. 2026, 2:30:23 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modelos</title>

    <style>
        .buscador {
            margin: 10px 0 15px 0;
        }
        .buscador select, .buscador input[type="text"] {
            padding: 6px;
        }
        .buscador input[type="text"] {
            width: 200px;
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
<h2>Catálogo de Modelos</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/modelos?action=nuevo">
        Nuevo Modelo
    </a>
</p>

<form class="buscador" method="get" action="${pageContext.request.contextPath}/modelos">
    <%-- CORRECCIÓN: value="buscar" igual que en el patrón del repositorio --%>
    <input type="hidden" name="action" value="buscar" />

    <label>Marca:</label>
    <select name="idMarca">
        <option value="">-- Todas --</option>
        <c:forEach var="marca" items="${marcas}">
            <option value="${marca.idMarca}"
                    <c:if test="${idMarca == marca.idMarca}">selected</c:if>>
                ${marca.nombre}
            </option>
        </c:forEach>
    </select>

    <label>Buscar:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Nombre del modelo..." />

    <button type="submit">Filtrar</button>
    <a href="${pageContext.request.contextPath}/modelos?action=listar">Limpiar</a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Marca</th>
            <th>Nombre</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="m" items="${listaModelos}">
            <tr>
                <td>${m.idModelo}</td>
                <td>${m.nombreMarca}</td>
                <td>${m.nombre}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/modelos?action=editar&id=${m.idModelo}<c:if test='${not empty idMarca}'>&idMarca=${idMarca}</c:if><c:if test='${not empty filtro}'>&filtro=${filtro}</c:if>">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${m.activo}">
                            | <a class="switch on"
                                 title="Desactivar"
                                 href="${pageContext.request.contextPath}/modelos?action=desactivar&id=${m.idModelo}<c:if test='${not empty idMarca}'>&idMarca=${idMarca}</c:if><c:if test='${not empty filtro}'>&filtro=${filtro}</c:if>">
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off"
                                 title="Activar"
                                 href="${pageContext.request.contextPath}/modelos?action=activar&id=${m.idModelo}<c:if test='${not empty idMarca}'>&idMarca=${idMarca}</c:if><c:if test='${not empty filtro}'>&filtro=${filtro}</c:if>">
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