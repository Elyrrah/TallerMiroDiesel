<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="py.taller.tallermirodiesel.model.Pais"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Países</title>
    </head>
    <body>
        <h1>Países</h1>

        <%
            List<Pais> paises = (List<Pais>) request.getAttribute("paises");
        %>

        <p>Total: <%= (paises == null ? 0 : paises.size()) %></p>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>ISO2</th>
                    <th>ISO3</th>
                    <th>Activo</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (paises != null) {
                        for (Pais p : paises) {
                %>
                <tr>
                    <td><%= p.getIdPais() %></td>
                    <td><%= p.getNombre() %></td>
                    <td><%= p.getIso2() %></td>
                    <td><%= p.getIso3() %></td>
                    <td><%= p.isActivo() %></td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>

        <p><a href="<%= request.getContextPath() %>/">Volver</a></p>
    </body>
</html>
