<%-- 
    Document   : usuario_listar
    Created on : 19 feb. 2026, 9:21:13 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Usuarios</title>

    <style>
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 6px; border: 1px solid #000; text-align: left; }

        .buscador { margin: 10px 0 15px 0; }
        .buscador input[type="text"] { padding: 6px; width: 260px; }
        .buscador button, .buscador a { padding: 6px 10px; margin-left: 6px; }

        .switch {
            display: inline-block;
            width: 46px; height: 24px;
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
            top: 3px; left: 3px;
            width: 18px; height: 18px;
            border-radius: 50%;
            background: #fff;
            border: 1px solid #999;
            transition: left 0.15s ease-in-out;
        }
        .switch.on  { background: #4CAF50; border-color: #3E8E41; }
        .switch.on::after  { left: 24px; }
        .switch.off { background: #e74c3c; border-color: #c0392b; }
        .switch.off::after { border-color: #c0392b; }
        .switch:hover { filter: brightness(0.95); }
    </style>
</head>
<body>

    <p>
        <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </p>

    <h2>Usuarios</h2>

    <%-- Mensaje de error --%>
    <c:if test="${not empty error}">
        <div style="color:red; margin-bottom:10px;">${error}</div>
    </c:if>

    <%-- Botón nuevo usuario --%>
    <p>
        <a href="${pageContext.request.contextPath}/usuarios?action=nuevo">Nuevo Usuario</a>
    </p>

    <%-- Filtro de búsqueda --%>
    <form class="buscador" method="get" action="${pageContext.request.contextPath}/usuarios">
        <input type="hidden" name="action" value="buscar"/>
        <label>Buscar (nombre o username):</label>
        <input type="text" name="filtro" value="${filtro}"/>
        <button type="submit">Filtrar</button>
        <a href="${pageContext.request.contextPath}/usuarios?action=listar">Limpiar</a>
    </form>

    <%-- Tabla de usuarios --%>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="u" items="${usuarios}">
                <tr>
                    <td>${u.idUsuario}</td>
                    <td>${u.username}</td>
                    <td>${u.nombre}</td>
                    <td>${u.apellido}</td>
                    <td>${u.rol.nombre}</td>
                    <td>
                        <c:choose>
                            <c:when test="${u.activo}">ACTIVO</c:when>
                            <c:otherwise>INACTIVO</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <%-- Editar --%>
                        <a href="${pageContext.request.contextPath}/usuarios?action=editar&id=${u.idUsuario}">
                            Editar
                        </a>

                        <%-- Cambiar contraseña --%>
                        | <a href="${pageContext.request.contextPath}/usuarios?action=cambiarPassword&id=${u.idUsuario}">
                            Cambiar contraseña
                        </a>

                        <%-- Toggle activar/desactivar --%>
                        <c:choose>
                            <c:when test="${u.activo}">
                                | <a class="switch on"
                                     title="Desactivar"
                                     href="${pageContext.request.contextPath}/usuarios?action=desactivar&id=${u.idUsuario}<c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
                                  </a>
                            </c:when>
                            <c:otherwise>
                                | <a class="switch off"
                                     title="Activar"
                                     href="${pageContext.request.contextPath}/usuarios?action=activar&id=${u.idUsuario}<c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
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