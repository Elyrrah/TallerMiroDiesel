<%-- 
    Document   : localidad_listar
    Created on : 21 ene. 2026, 4:01:11 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Localidades</title>

        <style>
            /* Buscador simple */
            .buscador {
                margin: 10px 0 15px 0;
            }
            .buscador input[type="text"], .buscador select {
                padding: 6px;
            }
            .buscador input[type="text"] {
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

        <h1>Localidades</h1>

        <c:if test="${not empty error}">
            <div style="color: red; margin-bottom: 10px;">
                ${error}
            </div>
        </c:if>

        <div style="margin-bottom: 12px;">
            <a href="${pageContext.request.contextPath}/localidades?action=nuevo">Nueva Localidad</a>
        </div>

        <!-- FILTRO POR DISTRITO + BÚSQUEDA -->
        <form class="buscador" method="get" action="${pageContext.request.contextPath}/localidades">
            <input type="hidden" name="action" value="listar"/>

            <label>Distrito:</label>
            <select name="idDistrito">
                <option value="">-- Todos --</option>
                <c:forEach var="d" items="${distritos}">
                    <option value="${d.idDistrito}"
                            <c:if test="${d.idDistrito == idDistrito}">selected</c:if>>
                        ${d.nombre}
                    </option>
                </c:forEach>
            </select>

            <label style="margin-left: 10px;">Buscar:</label>
            <input type="text" name="filtro" value="${filtro}" placeholder="Nombre..." />

            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/localidades?action=listar">Limpiar</a>
        </form>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Distrito</th>
                    <th>Localidad</th>
                    <th>Activo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>

                <c:if test="${empty lista}">
                    <tr>
                        <td colspan="5">No hay localidades para mostrar.</td>
                    </tr>
                </c:if>

                <c:forEach var="l" items="${lista}">
                    <tr>
                        <td>${l.idLocalidad}</td>
                        <td>${l.nombreDistrito}</td>
                        <td>${l.nombre}</td>

                        <td>
                            <c:choose>
                                <c:when test="${l.activo}">SÍ</c:when>
                                <c:otherwise>NO</c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/localidades?action=editar&amp;id=${l.idLocalidad}<c:if test='${not empty idDistrito}'>&amp;idDistrito=${idDistrito}</c:if><c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
                                Editar
                            </a>

                            <!-- Toggle activar/desactivar (preserva filtros) -->
                            <c:choose>
                                <c:when test="${l.activo}">
                                    | <a class="switch on"
                                         title="Desactivar"
                                         href="${pageContext.request.contextPath}/localidades?action=desactivar&amp;id=${l.idLocalidad}<c:if test='${not empty idDistrito}'>&amp;idDistrito=${idDistrito}</c:if><c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
                                      </a>
                                </c:when>
                                <c:otherwise>
                                    | <a class="switch off"
                                         title="Activar"
                                         href="${pageContext.request.contextPath}/localidades?action=activar&amp;id=${l.idLocalidad}<c:if test='${not empty idDistrito}'>&amp;idDistrito=${idDistrito}</c:if><c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
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