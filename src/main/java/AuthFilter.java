    // Importa las clases necesarias para crear un filtro que controle el acceso de usuarios
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

    // Anotación que indica que este filtro se aplicará a TODAS las rutas ("/*")
    @WebFilter("/*")
    public class AuthFilter implements Filter {

        // Este método se ejecuta por cada petición que entra a la aplicación (como un filtro de seguridad)
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
                throws IOException, ServletException {

            // Se convierten los objetos genéricos en objetos HTTP para poder usarlos como tal
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            // Se obtiene la sesión actual, si existe (false = no crea una nueva)
            HttpSession session = request.getSession(false);

            // Se obtiene la URL completa que está solicitando el usuario
            String requestURI = request.getRequestURI();

            // Se obtiene el contexto de la aplicación (normalmente algo como /miApp)
            String contextPath = request.getContextPath();

            // Se definen las rutas que NO necesitan que el usuario esté logueado
            boolean loginRequest = requestURI.equals(contextPath + "/login.html") ||
                    requestURI.equals(contextPath + "/LoginServlet");

            boolean registerRequest = requestURI.equals(contextPath + "/registro.html");
            boolean logoutRequest = requestURI.equals(contextPath + "/CerrarSesionServlet");
            boolean recoverRequest = requestURI.equals(contextPath + "/restaurar.html");

            // Archivos como imágenes, CSS y JS también deben permitirse sin validación
            boolean resourceRequest = requestURI.endsWith(".css") ||
                    requestURI.endsWith(".js") ||
                    requestURI.endsWith(".png") ||
                    requestURI.endsWith(".jpg") ||
                    requestURI.endsWith(".jpeg") ||
                    requestURI.endsWith(".gif") ||
                    requestURI.contains("/images/");

            // Si la ruta es una de las permitidas o si hay un usuario en sesión, se deja pasar
            boolean allowed = loginRequest || registerRequest || logoutRequest ||
                    recoverRequest || resourceRequest;

            if (allowed || (session != null && session.getAttribute("usuario") != null)) {

                // Si no es un recurso (como una imagen o archivo), se añaden cabeceras de seguridad
                if (!resourceRequest) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                }

                // Permite que la petición continúe (ya sea hacia un servlet, HTML, etc.)
                chain.doFilter(request, response);

            } else {
                // Si el usuario no tiene sesión y la ruta no está permitida, lo manda al login
                response.sendRedirect(contextPath + "/login.html");
            }
        }

        // Estos dos métodos se dejan vacíos, pero se ejecutan al iniciar y destruir el filtro (no son obligatorios)
        public void init(FilterConfig fConfig) throws ServletException {}
        public void destroy() {}
    }
