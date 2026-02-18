<%-- 
    Document   : distrito_listar
    Created on : 21 ene. 2026, 11:18:30 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Distritos</title>

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

        <h1>Distritos</h1>

        <c:if test="${not empty error}">
            <div style="color: red; margin-bottom: 10px;">
                ${error}
            </div>
        </c:if>

        <div style="margin-bottom: 12px;">
            <a href="${pageContext.request.contextPath}/distritos?action=nuevo">Nuevo Distrito</a>
        </div>

        <!-- FILTRO POR DEPARTAMENTO + BÚSQUEDA -->
        <form class="buscador" method="get" action="${pageContext.request.contextPath}/distritos">
            <input type="hidden" name="action" value="listar"/>

            <label>Departamento:</label>
            <select name="idDepartamento">
                <option value="">-- Todos --</option>
                <c:forEach var="d" items="${departamentos}">
                    <option value="${d.idDepartamento}"
                            <c:if test="${d.idDepartamento == idDepartamento}">selected</c:if>>
                        ${d.nombre}
                    </option>
                </c:forEach>
            </select>

            <label style="margin-left: 10px;">Buscar:</label>
            <input type="text" name="filtro" value="${filtro}" placeholder="Nombre..." />

            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/distritos?action=listar">Limpiar</a>
        </form>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Departamento</th>
                    <th>Distrito</th>
                    <th>Activo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>

                <c:if test="${empty lista}">
                    <tr>
                        <td colspan="5">No hay distritos para mostrar.</td>
                    </tr>
                </c:if>

                <c:forEach var="di" items="${lista}">
                    <tr>
                        <td>${di.idDistrito}</td>
                        <td>${di.nombreDepartamento}</td>
                        <td>${di.nombre}</td>

                        <td>
                            <c:choose>
                                <c:when test="${di.activo}">SÍ</c:when>
                                <c:otherwise>NO</c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/distritos?action=editar&amp;id=${di.idDistrito}<c:if test='${not empty idDepartamento}'>&amp;idDepartamento=${idDepartamento}</c:if><c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
                                Editar
                            </a>

                            <!-- Toggle activar/desactivar (preserva filtros) -->
                            <c:choose>
                                <c:when test="${di.activo}">
                                    | <a class="switch on"
                                         title="Desactivar"
                                         href="${pageContext.request.contextPath}/distritos?action=desactivar&amp;id=${di.idDistrito}<c:if test='${not empty idDepartamento}'>&amp;idDepartamento=${idDepartamento}</c:if><c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
                                      </a>
                                </c:when>
                                <c:otherwise>
                                    | <a class="switch off"
                                         title="Activar"
                                         href="${pageContext.request.contextPath}/distritos?action=activar&amp;id=${di.idDistrito}<c:if test='${not empty idDepartamento}'>&amp;idDepartamento=${idDepartamento}</c:if><c:if test='${not empty filtro}'>&amp;filtro=${filtro}</c:if>">
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