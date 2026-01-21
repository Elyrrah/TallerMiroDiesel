<%-- 
    Document   : ciudad_form
    Created on : 21 ene. 2026, 11:18:13 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Formulario Ciudad</title>
    </head>
    <body>

        <h1>Formulario Ciudad</h1>

        <c:if test="${not empty error}">
            <div style="color: red;">
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/ciudades">
            <input type="hidden" name="accion" value="guardar"/>

            <%-- idCiudad (solo para editar) --%>
            <c:if test="${not empty ciudad.idCiudad}">
                <input type="hidden" name="idCiudad" value="${ciudad.idCiudad}"/>
            </c:if>

            <div>
                <label>Departamento:</label>
                <select name="idDepartamento" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach var="d" items="${departamentos}">
                        <option value="${d.idDepartamento}"
                                <c:if test="${not empty ciudad.idDepartamento and ciudad.idDepartamento == d.idDepartamento}">selected</c:if>>
                            ${d.nombre}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label>Nombre:</label>
                <input type="text" name="nombre" value="${ciudad.nombre}" required/>
            </div>

            <div>
                <label>Activo:</label>
                <input type="checkbox" name="activo"
                       <c:if test="${ciudad.activo}">checked</c:if> />
            </div>

            <br/>

            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/ciudades?accion=listar">Cancelar</a>
        </form>

    </body>
</html>
