import com.google.gson.Gson; // Asegúrate de que Gson está disponible si lo usas en otros servlets, aunque no directamente aquí.

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
// Importa la clase Usuario si está en un paquete diferente
// import tu.paquete.Usuario;

@WebServlet("/api/user-info")
public class UserInfoServlet extends HttpServlet {

    // Método para establecer los encabezados CORS
    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*"); // Permite cualquier origen, ajusta si es necesario
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Crucial para que se envíen las cookies
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        setCORSHeaders(response); // Asegurarse de que los encabezados CORS se envíen en GET también
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // Asegurar codificación UTF-8

        try {
            HttpSession session = request.getSession(false); // No crear nueva sesión

            if (session == null || session.getAttribute("usuario") == null) {
                // Log para depuración
                System.out.println("DEBUG - UserInfoServlet: Sesión nula o atributo 'usuario' no encontrado. Enviando 401.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"No autenticado\"}");
                return;
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");

            // Log para depuración
            System.out.println("DEBUG - UserInfoServlet: Usuario encontrado en sesión: " + usuario.getRol());

            String json = String.format(
                    "{\"nombre\":\"%s\", \"email\":\"%s\", \"rol\":\"%s\"}",
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol()
            );

            response.getWriter().write(json);

        } catch (ClassCastException e) {
            // Esto ocurre si el objeto en sesión no es un Usuario.
            System.err.println("ERROR - UserInfoServlet: Error de cast al recuperar usuario de sesión. " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno de sesión\"}");
        }
        catch (Exception e) {
            System.err.println("ERROR - UserInfoServlet: Error inesperado: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error del servidor\"}");
        }
    }
}