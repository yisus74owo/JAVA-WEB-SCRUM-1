import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter("/*")
public class AuthFilter implements Filter {

    // Los métodos init y destroy duplicados serán eliminados.
    // La inicialización y destrucción del filtro se manejan una sola vez.

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter inicializado.");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String method = request.getMethod();

        System.out.println("DEBUG - AuthFilter: Interceptando URI: '" + requestURI + "', Método: '" + method + "'");

        // Verificar si la solicitud es para una URI pública
        if (isPublicRequest(requestURI, contextPath)) {
            System.out.println("DEBUG - AuthFilter: URI '" + requestURI + "' es pública. Saltando autenticación.");
            chain.doFilter(request, response);
            return;
        }

        // Si la solicitud no es pública, verificar si el usuario está logueado
        // Tu atributo de sesión para el usuario logueado es "usuario"
        if (session == null || session.getAttribute("usuario") == null) {
            System.out.println("DEBUG - AuthFilter: Sesión no encontrada o usuario no logueado para URI: '" + requestURI + "'. Redirigiendo a login.");
            // Redirigir a login, pasando la URI original para posible redirección después del login
            response.sendRedirect(response.encodeRedirectURL(contextPath + "/login.html?redirect=" + requestURI));
            return;
        }

        // Si el usuario está logueado, obtener su rol
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = usuario.getRol(); // Asumiendo que Usuario tiene un método getRol() que devuelve el nombre del rol (ej. "Administrador")

        System.out.println("DEBUG - AuthFilter: Usuario autenticado. Rol: '" + rol + "' para URI: '" + requestURI + "'.");

        // Verificar permisos basados en el rol
        if (!hasPermission(requestURI, rol, method, contextPath)) {
            System.out.println("DEBUG - AuthFilter: Permiso denegado para Rol: '" + rol + "' en URI: '" + requestURI + "'.");
            handleUnauthorizedAccess(response, rol, contextPath);
            return;
        }

        // Si todo está bien, aplicar encabezados de seguridad y continuar la cadena de filtros
        if (!isResourceRequest(requestURI)) { // No aplicar encabezados de seguridad a recursos estáticos
            setSecurityHeaders(response);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter destruido.");
    }

    // --- Métodos Auxiliares ---

    // Verifica si la solicitud es para una URI que no requiere autenticación
    private boolean isPublicRequest(String requestURI, String contextPath) {
        // Rutas de páginas HTML públicas
        boolean loginPageRequest = requestURI.equals(contextPath + "/login.html");
        boolean registerPageRequest = requestURI.equals(contextPath + "/registro.html"); // Página HTML de registro
        boolean homePageRequest = requestURI.equals(contextPath + "/") ||
                requestURI.equals(contextPath + "/OHANA.html") ||
                requestURI.equals(contextPath + "/index.html");
        boolean recoverPageRequest = requestURI.equals(contextPath + "/restaurar.html");

        // Rutas de servlets/endpoints públicos
        boolean loginServletRequest = requestURI.equals(contextPath + "/LoginServlet");
        boolean registerServletRequest = requestURI.equals(contextPath + "/registro"); // ¡¡¡ESTA ES LA CLAVE!!! El servlet de registro
        boolean logoutServletRequest = requestURI.equals(contextPath + "/CerrarSesionServlet");

        // Rutas de recursos estáticos
        boolean resourceRequest = isResourceRequest(requestURI);

        return loginPageRequest || registerPageRequest || homePageRequest || recoverPageRequest ||
                loginServletRequest || registerServletRequest || logoutServletRequest ||
                resourceRequest;
    }

    // Verifica si la solicitud es para un recurso estático (CSS, JS, imágenes, etc.)
    private boolean isResourceRequest(String requestURI) {
        return requestURI.endsWith(".css") ||
                requestURI.endsWith(".js") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".gif") ||
                requestURI.endsWith(".ico") ||
                requestURI.endsWith(".woff") ||
                requestURI.endsWith(".ttf") ||
                requestURI.endsWith(".woff2") ||
                requestURI.contains("/images/") || // Para directorios de imágenes
                requestURI.contains("/assets/");   // Para directorios de assets
    }

    // Lógica de permisos basada en el rol del usuario
    private boolean hasPermission(String requestURI, String rol, String method, String contextPath) {
        final String ROL_ADMINISTRADOR = "Administrador";
        final String ROL_AUXILIAR = "Auxiliar"; // Asumiendo que este es el otro rol

        System.out.println("DEBUG - AuthFilter.hasPermission: Evaluando URI: '" + requestURI + "', Rol: '" + rol + "', Método: '" + method + "'");

        // Reglas para páginas HTML específicas que requieren Administrador
        if (requestURI.equals(contextPath + "/crearproducto.html") ||
                requestURI.equals(contextPath + "/producto.html")) {
            boolean tienePermiso = ROL_ADMINISTRADOR.equalsIgnoreCase(rol);
            System.out.println("DEBUG - AuthFilter.hasPermission: URI es página de crear/editar producto. Tiene permiso: " + tienePermiso);
            return tienePermiso;
        }

        // Reglas para APIs de productos (Crear, Editar, Eliminar)
        if (requestURI.startsWith(contextPath + "/api/productos")) {
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
                boolean tienePermiso = ROL_ADMINISTRADOR.equalsIgnoreCase(rol);
                System.out.println("DEBUG - AuthFilter.hasPermission: URI es API de producto (POST/PUT/DELETE). Tiene permiso: " + tienePermiso);
                return tienePermiso;
            }
            // Las solicitudes GET a /api/productos son permitidas para todos los logueados
            System.out.println("DEBUG - AuthFilter.hasPermission: URI es API de producto (GET). Permiso concedido.");
            return true;
        }

        // Rutas de API exclusivas del Administrador (aparte de productos específicos)
        if (requestURI.startsWith(contextPath + "/api/clientes") ||
                requestURI.startsWith(contextPath + "/api/usuarios")) {
            boolean tienePermiso = ROL_ADMINISTRADOR.equalsIgnoreCase(rol);
            System.out.println("DEBUG - AuthFilter.hasPermission: URI es API de clientes/usuarios. Tiene permiso: " + tienePermiso);
            return tienePermiso;
        }

        // Rutas que pueden acceder Administrador y Veterinario (si tienes rol Veterinario)
        // Si no tienes rol "Veterinario", puedes eliminar esta sección o ajustarla.
        if (requestURI.startsWith(contextPath + "/api/consultas") ||
                requestURI.startsWith(contextPath + "/api/historias")) {
            boolean tienePermiso = ROL_ADMINISTRADOR.equalsIgnoreCase(rol) || "Veterinario".equalsIgnoreCase(rol);
            System.out.println("DEBUG - AuthFilter.hasPermission: URI es API de consultas/historias. Tiene permiso: " + tienePermiso);
            return tienePermiso;
        }

        // Por defecto, si no hay una regla específica, permitir el acceso a los logueados.
        // CUIDADO: Esta es una regla de "permiso por defecto". Asegúrate de que todas las rutas sensibles estén cubiertas arriba.
        System.out.println("DEBUG - AuthFilter.hasPermission: URI no tiene regla específica. Permiso concedido por defecto.");
        return true;
    }

    // Maneja el acceso no autorizado, redirigiendo o enviando JSON
    private void handleUnauthorizedAccess(HttpServletResponse response, String rol, String contextPath) throws IOException {
        System.out.println("DEBUG - AuthFilter.handleUnauthorizedAccess: Intento de acceso no autorizado para rol '" + rol + "'.");

        // Si el rol es Auxiliar, redirigir a una página de acceso denegado específica
        if ("Auxiliar".equalsIgnoreCase(rol)) {
            response.sendRedirect(response.encodeRedirectURL(contextPath + "/acceso-denegado.html?rol=Auxiliar"));
        } else {
            // Para otros roles o si no se especifica una redirección, enviar 403 Forbidden
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No tienes permisos suficientes para acceder a este recurso.\"}");
        }
    }

    // Establece encabezados de seguridad en la respuesta HTTP
    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
    }
}