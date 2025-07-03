import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        // Rutas públicas que no requieren autenticación
        boolean publicRequest = isPublicRequest(requestURI, contextPath);

        if (publicRequest) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar si el usuario está autenticado
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(contextPath + "/login.html");
            return;
        }

        // Obtener información del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = usuario.getRol();

        // Verificar permisos según el rol
        if (!hasPermission(requestURI, rol)) {
            handleUnauthorizedAccess(response, rol);
            return;
        }

        // Configurar cabeceras de seguridad para rutas no públicas
        if (!isResourceRequest(requestURI)) {
            setSecurityHeaders(response);
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicRequest(String requestURI, String contextPath) {
        // Definir rutas públicas
        boolean loginRequest = requestURI.equals(contextPath + "/login.html") ||
                requestURI.equals(contextPath + "/LoginServlet");

        boolean registerRequest = requestURI.equals(contextPath + "/registro.html");
        boolean logoutRequest = requestURI.equals(contextPath + "/CerrarSesionServlet");
        boolean recoverRequest = requestURI.equals(contextPath + "/restaurar.html");

        // Archivos estáticos
        boolean resourceRequest = requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".gif") ||
                requestURI.contains("/images/") ||
                requestURI.contains("/assets/");

        return loginRequest || registerRequest || logoutRequest ||
                recoverRequest || resourceRequest;
    }

    private boolean isResourceRequest(String requestURI) {
        return requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".gif") ||
                requestURI.contains("/images/") ||
                requestURI.contains("/assets/");
    }

    private boolean hasPermission(String requestURI, String rol) {
        // Rutas que solo el administrador puede acceder
        if (requestURI.contains("/api/productos/editar") ||
            requestURI.contains("/api/productos/crear") ||
            requestURI.contains("/api/productos/delete") ||
            requestURI.contains("/api/clientes") ||
            requestURI.contains("/api/usuarios")) {
            return "Administrador".equals(rol);
        }

        // Rutas para administrador y veterinario
        if (requestURI.contains("/api/consultas") ||
            requestURI.contains("/api/historias")) {
            return "Administrador".equals(rol) || "Veterinario".equals(rol);
        }

        // Rutas permitidas para todos los roles autenticados
        return true;
    }

    private void handleUnauthorizedAccess(HttpServletResponse response, String rol) throws IOException {
        if ("Auxiliar".equals(rol)) {
            // Redirigir a una página con mensaje de permisos insuficientes
            response.sendRedirect("acceso-denegado.html?rol=Auxiliar");
        } else {
            // Para otros casos (roles no reconocidos)
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "No tienes permisos para acceder a este recurso");
        }
    }

    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización si es necesaria
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}