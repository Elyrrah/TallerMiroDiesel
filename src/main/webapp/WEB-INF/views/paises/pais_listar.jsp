<%-- 
    Document   : pais_listar
    Created on : 19 ene. 2026, 11:19:57 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Países</title>

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
            border-color: #c0392b
        }

        /* Opcional: cursor de mano */
        .switch:hover {
            filter: brightness(0.95);
        }
        
        /* Circulito cuando está apagado */
        .switch.off::after {
            border-color: #c0392b;
        }
    </style>
</head>
<body>
<!-- Volver a la página principal -->
<p>
    <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
</p>
<h2>Catálogo de Países</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/paises?action=nuevo">
        Nuevo País
    </a>
</p>

<!-- Buscador / filtro -->
<form class="buscador" method="get" action="${pageContext.request.contextPath}/paises">
    <input type="hidden" name="action" value="list" />
    <label>Buscar:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Nombre..." />
    <button type="submit">Filtrar</button>
    <a href="${pageContext.request.contextPath}/paises?action=list">Limpiar</a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>ISO2</th>
            <th>ISO3</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${paises}">
            <tr>
                <td>${p.idPais}</td>
                <td>${p.nombre}</td>
                <td>${p.iso2}</td>
                <td>${p.iso3}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/paises?action=edit&id=${p.idPais}">
                        Editar
                    </a>

                    <!-- Toggle activar/desactivar -->
                    <c:choose>
                        <c:when test="${p.activo}">
                            | <a class="switch on"
                                 title="Desactivar"
                                 href="${pageContext.request.contextPath}/paises?action=deactivate&id=${p.idPais}">
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off"
                                 title="Activar"
                                 href="${pageContext.request.contextPath}/paises?action=activate&id=${p.idPais}"
                                 >
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
