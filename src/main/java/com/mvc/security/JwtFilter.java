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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                System.out.println("⚠️ Filtro JWT activado: " + ((HttpServletRequest) request).getRequestURI());

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        if (!path.startsWith("/api/admin") &&
        !path.startsWith("/api/logs") &&
        !path.startsWith("/api/empleado")
        ) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falta token JWT");
            return;
        }

        String token = authHeader.substring(7);
        try {
            Jws<Claims> claims = JwtUtil.validarToken(token);

            Long usuarioId = Long.valueOf(claims.getBody().getSubject());
            Integer rol = (Integer) claims.getBody().get("rol");

            if ((path.startsWith("/api/admin") || path.startsWith("/api/logs")) && rol != 3) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo administradores pueden acceder");
                return;
            }
            
            
            if (path.startsWith("/api/empleado") && rol < 2) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Solo empleados o administradores pueden acceder");
                return;
            }
            

            // Aquí podrías guardar usuarioId si lo necesitas
            request.setAttribute("usuarioId", usuarioId);

            chain.doFilter(request, response);

        } catch (JwtException e) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        }
    }
}
 
