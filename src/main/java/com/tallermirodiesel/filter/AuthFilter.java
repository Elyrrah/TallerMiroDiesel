/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.tallermirodiesel.model.SesionDeUsuario;

/**
 * @author elyrr
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    // Intercepta todas las peticiones antes de que lleguen a los Servlets
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // 1. Dejamos pasar recursos estáticos (CSS, JS, imágenes, etc.)
        if (esRecursoEstatico(uri)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Dejamos pasar la URL de login (es la única página pública)
        if (uri.endsWith("/login")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Verificamos si hay un usuario autenticado en la sesión
        HttpSession sesion = httpRequest.getSession(false);
        SesionDeUsuario usuarioSesion = (sesion != null)
                ? (SesionDeUsuario) sesion.getAttribute("usuarioSesion")
                : null;

        // 4. Si no hay sesión activa, redirigimos al login
        if (usuarioSesion == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // 5. Hay sesión activa, dejamos pasar la petición
        chain.doFilter(request, response);
    }

    // Devuelve true si la URL corresponde a un recurso estático
    private boolean esRecursoEstatico(String uri) {
        return uri.endsWith(".css")
            || uri.endsWith(".js")
            || uri.endsWith(".png")
            || uri.endsWith(".jpg")
            || uri.endsWith(".ico")
            || uri.endsWith(".woff")
            || uri.endsWith(".woff2");
    }

    @Override
    public void destroy() {
    }
}