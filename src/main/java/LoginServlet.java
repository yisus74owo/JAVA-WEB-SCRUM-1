import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Credenciales hardcodeadas (admin@gmail.com / admin)
        if("admin@gmail.com".equals(email) && "admin123".equals(password)) {
            // Crear objeto Usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setPassword(password);

            // Crear sesión y guardar usuario
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);

            // Redirigir a página principal
            response.sendRedirect("main.html");
        } else {
            // Credenciales incorrectas, redirigir de vuelta al login con mensaje de error
            response.sendRedirect("login.html?error=1");
        }
    }
}