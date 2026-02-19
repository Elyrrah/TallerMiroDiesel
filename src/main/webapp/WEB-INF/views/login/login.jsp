<%-- 
    Document   : login
    Created on : 19 feb. 2026, 9:14:53 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Taller Miro Diesel — Iniciar Sesión</title>

    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f0f0f0;
            font-family: sans-serif;
        }

        .login-box {
            background: #fff;
            padding: 32px 40px;
            border: 1px solid #ccc;
            width: 320px;
        }

        .login-box h2 {
            margin: 0 0 24px 0;
            text-align: center;
        }

        .login-box label {
            display: block;
            margin-bottom: 4px;
            font-weight: bold;
            font-size: 14px;
        }

        .login-box input[type="text"],
        .login-box input[type="password"] {
            width: 100%;
            padding: 8px;
            margin-bottom: 16px;
            box-sizing: border-box;
            border: 1px solid #ccc;
        }

        .login-box button {
            width: 100%;
            padding: 10px;
            background-color: #333;
            color: #fff;
            border: none;
            cursor: pointer;
            font-size: 15px;
        }

        .login-box button:hover {
            background-color: #555;
        }

        .error {
            color: red;
            font-size: 13px;
            margin-bottom: 16px;
            text-align: center;
        }
    </style>
</head>
<body>

    <div class="login-box">

        <h2>Taller Miro Diesel</h2>

        <%-- Muestra el error si las credenciales son incorrectas --%>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <input type="hidden" name="action" value="login"/>

            <label>Usuario</label>
            <input type="text" name="username" value="${username}" autofocus autocomplete="off"/>

            <label>Contraseña</label>
            <input type="password" name="password"/>

            <button type="submit">Ingresar</button>
        </form>

    </div>

</body>
</html>