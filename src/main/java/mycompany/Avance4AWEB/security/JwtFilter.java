package mycompany.Avance4AWEB.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mycompany.Avance4AWEB.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    //rutas que no piden token
    private boolean isPublicRoute(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (method.equalsIgnoreCase("OPTIONS")) return true;
        if (path.equals("/api/auth/login") && method.equals("POST")) return true;
        if (path.equals("/api/clientes") && method.equals("POST")) return true;
        if (path.equals("/api/productos") && method.equals("GET")) return true;
        if (path.startsWith("/api/productos/") && method.equals("GET")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        if (isPublicRoute(request)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Se requiere token de autenticación");
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            sendUnauthorized(response, "Token inválido o expirado");
            return;
        }

        //se guarda el correo del token para que los controllers sepan quien es
        String correo = jwtService.extractCorreo(token);
        request.setAttribute("correo", correo);

        chain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = String.format("{\"error\": \"%s\"}", message);
        response.getWriter().write(json);
    }
}