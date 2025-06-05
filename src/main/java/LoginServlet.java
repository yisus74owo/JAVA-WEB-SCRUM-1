// Importa clases necesarias para trabajar con servlets, sesiones y manejar excepciones
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Anotación que indica que este servlet se llamará "LoginServlet" y se activa cuando se accede a esa ruta
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // Identificador para la versión del servlet (útil al trabajar con versiones)

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se obtienen los datos del formulario: el correo y la contraseña
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Estas líneas evitan que el navegador guarde en caché la respuesta (para mayor seguridad)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Se verifica si el usuario y la contraseña son correctos (en este caso, un valor fijo "admin")
        if("admin@gmail.com".equals(email) && "admin123".equals(password)) {
            // Si son correctos, se crea un objeto Usuario con los datos
            Usuario usuario = new Usuario(email, password);

            // Se crea o recupera una sesión para el usuario
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario); // Se guarda el objeto usuario en la sesión

            session.setMaxInactiveInterval(30 * 60); // La sesión durará 30 minutos sin actividad

            // Redirige al usuario a la página principal (main.html)
            response.sendRedirect("main.html");
        } else {
            // Si los datos son incorrectos, redirige al login con un mensaje de error
            response.sendRedirect("login.html?error=1");
        }
    }
}
