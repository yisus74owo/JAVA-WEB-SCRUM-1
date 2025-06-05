// Importa las librerías necesarias para manejar servlets, sesiones y posibles errores
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Anotación que indica que este servlet se activa cuando se accede a "/CerrarSesionServlet"
@WebServlet("/CerrarSesionServlet")
public class CerrarSesionServlet extends HttpServlet {

    // Método que se ejecuta cuando se hace una petición GET (por ejemplo, al hacer clic en "Cerrar sesión")
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Estas líneas evitan que el navegador guarde la sesión en caché (más seguridad)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Se obtiene la sesión actual, si existe (false = no crea una nueva si no hay)
        HttpSession session = request.getSession(false);

        // Si hay una sesión activa, la eliminamos
        if (session != null) {
            session.removeAttribute("usuario"); // Borra solo el atributo "usuario"
            session.invalidate(); // Cierra completamente la sesión
        }

        // Redirige al usuario al login, indicando que la sesión se cerró con éxito
        response.sendRedirect("login.html?logout=success");
    }
}
