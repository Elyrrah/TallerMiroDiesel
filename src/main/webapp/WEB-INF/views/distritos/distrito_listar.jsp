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

        <p>
            <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
        </p>

        <h2>Catálogo de Distritos</h2>

        <c:if test="${not empty error}">
            <div style="color:red; margin-bottom:10px;">
                ${error}
            </div>
        </c:if>

        <p>
            <a href="${pageContext.request.contextPath}/distritos?accion=nuevo">
                Nuevo Distrito
            </a>
        </p>

        <!-- Buscador / filtro -->
        <form class="buscador" method="get" action="${pageContext.request.contextPath}/distritos">
            <input type="hidden" name="accion" value="listar"/>

            <label>Departamento:</label>
            <select name="idDepartamento">
                <option value="">-- Todos --</option>
                <c:forEach var="d" items="${departamentos}">
                    <option value="${d.idDepartamento}"
                            <c:if test="${not empty idDepartamento and idDepartamento == d.idDepartamento}">selected</c:if>>
                        ${d.nombre}
                    </option>
                </c:forEach>
            </select>

            <label>Buscar:</label>
            <input type="text" name="filtro" value="${filtro}" placeholder="Nombre..." />

            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/distritos?accion=listar">Limpiar</a>
        </form>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Departamento</th>
                    <th>Nombre</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>

                <c:if test="${empty lista}">
                    <tr>
                        <td colspan="4">No hay distritos registrados.</td>
                    </tr>
                </c:if>

                <c:forEach var="di" items="${lista}">
                    <tr>
                        <td>${di.idDistrito}</td>
                        <td>${di.nombreDepartamento}</td>
                        <td>${di.nombre}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/distritos?accion=editar&id=${di.idDistrito}">
                                Editar
                            </a>

                            <!-- Toggle activar/desactivar (preserva filtros) -->
                            <c:choose>
                                <c:when test="${di.activo}">
                                    | <a class="switch on"
                                         title="Desactivar"
                                         href="${pageContext.request.contextPath}/distritos?accion=desactivar&id=${di.idDistrito}&idDepartamento=${idDepartamento}&filtro=${filtro}">
                                      </a>
                                </c:when>
                                <c:otherwise>
                                    | <a class="switch off"
                                         title="Activar"
                                         href="${pageContext.request.contextPath}/distritos?accion=activar&id=${di.idDistrito}&idDepartamento=${idDepartamento}&filtro=${filtro}">
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
