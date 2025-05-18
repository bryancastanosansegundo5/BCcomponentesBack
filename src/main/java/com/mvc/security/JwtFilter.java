package com.mvc.security;

import com.mvc.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter implements Filter {
    // Clase que implementa un filtro de servlet para proteger rutas con JWT
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Método principal del filtro: intercepta todas las peticiones HTTP

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // Se convierte a las clases específicas HTTP para acceder a headers, URI, etc.

        String path = req.getRequestURI();
        // Se obtiene la ruta de la petición

        if (!path.startsWith("/api/admin") &&
                !path.startsWith("/api/logs") &&
                !path.startsWith("/api/empleado")) {
            // Si la ruta no requiere autenticación especial, se continúa sin validar token
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        // Se obtiene el header "Authorization" (esperado: "Bearer <token>")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si falta o está mal formado, se rechaza con 401
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falta token JWT");
            return;
        }

        String token = authHeader.substring(7);
        // Se extrae el token quitando el prefijo "Bearer "

        try {
            Jws<Claims> claims = jwtUtil.validarToken(token);
            // Se valida el token y se obtienen los datos codificados

            Long usuarioId = Long.valueOf(claims.getBody().getSubject());
            Integer rol = (Integer) claims.getBody().get("rol");
            // Se extraen el ID del usuario y su rol

            if ((path.startsWith("/api/admin") || path.startsWith("/api/logs")) && rol != 3) {
                // Si accede a /admin o /logs pero no es administrador (rol 3), se bloquea
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden acceder");
                return;
            }

            if (path.startsWith("/api/empleado") && rol < 2) {
                // Si accede a /empleado pero no es empleado (rol 2) o admin (rol 3), se bloquea
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo empleados o administradores pueden acceder");
                return;
            }

            request.setAttribute("usuarioId", usuarioId);
            // Se guarda el ID del usuario en la request (útil para usarlo más adelante)

            chain.doFilter(request, response);
            // Continúa con el siguiente filtro o controlador

        } catch (JwtException e) {
            // Si el token es inválido o está expirado, se devuelve error 401
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        }
    }
}
