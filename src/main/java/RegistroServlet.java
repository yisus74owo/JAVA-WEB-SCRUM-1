import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {

            String nombreUsuario = request.getParameter("Nombre_usuario");
            String apellidoUsuario = request.getParameter("Apellido_usuario");
            String correo = request.getParameter("Correo");
            String contrasena = request.getParameter("Contrasena");
            String telefono = request.getParameter("Telefono");
            String direccion = request.getParameter("Direccion");
            String tipoDocumento = request.getParameter("Tipo_documento");
            String documentoUsuario = request.getParameter("Documento_usuario");
            String nombreRolSeleccionado = request.getParameter("rol");

            System.out.println("DEBUG - RegistroServlet: Recibiendo datos:");
            System.out.println("DEBUG - Nombre_usuario: '" + (nombreUsuario != null ? nombreUsuario : "NULL") + "'");
            System.out.println("DEBUG - Apellido_usuario: '" + (apellidoUsuario != null ? apellidoUsuario : "NULL") + "'");
            System.out.println("DEBUG - Correo: '" + (correo != null ? correo : "NULL") + "'");
            System.out.println("DEBUG - Contrasena: '" + (contrasena != null ? contrasena : "NULL") + "'");
            System.out.println("DEBUG - Telefono: '" + (telefono != null ? telefono : "NULL") + "'");
            System.out.println("DEBUG - Direccion: '" + (direccion != null ? direccion : "NULL") + "'");
            System.out.println("DEBUG - Tipo_documento: '" + (tipoDocumento != null ? tipoDocumento : "NULL") + "'");
            System.out.println("DEBUG - Documento_usuario: '" + (documentoUsuario != null ? documentoUsuario : "NULL") + "'");
            System.out.println("DEBUG - rol: '" + (nombreRolSeleccionado != null ? nombreRolSeleccionado : "NULL") + "'");

            if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                    apellidoUsuario == null || apellidoUsuario.trim().isEmpty() ||
                    correo == null || correo.trim().isEmpty() ||
                    contrasena == null || contrasena.trim().isEmpty() ||
                    telefono == null || telefono.trim().isEmpty() ||
                    direccion == null || direccion.trim().isEmpty() ||
                    tipoDocumento == null || tipoDocumento.trim().isEmpty() ||
                    documentoUsuario == null || documentoUsuario.trim().isEmpty() ||
                    nombreRolSeleccionado == null || nombreRolSeleccionado.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Todos los campos son obligatorios.\"}");
                System.err.println("ERROR: Uno o más campos obligatorios están vacíos o son null.");
                if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) System.err.println("  - Nombre_usuario es nulo/vacío.");
                if (apellidoUsuario == null || apellidoUsuario.trim().isEmpty()) System.err.println("  - Apellido_usuario es nulo/vacío.");
                if (correo == null || correo.trim().isEmpty()) System.err.println("  - Correo es nulo/vacío.");
                if (contrasena == null || contrasena.trim().isEmpty()) System.err.println("  - Contrasena es nulo/vacío.");
                if (telefono == null || telefono.trim().isEmpty()) System.err.println("  - Telefono es nulo/vacío.");
                if (direccion == null || direccion.trim().isEmpty()) System.err.println("  - Direccion es nulo/vacío.");
                if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) System.err.println("  - Tipo_documento es nulo/vacío.");
                if (documentoUsuario == null || documentoUsuario.trim().isEmpty()) System.err.println("  - Documento_usuario es nulo/vacío.");
                if (nombreRolSeleccionado == null || nombreRolSeleccionado.trim().isEmpty()) System.err.println("  - rol es nulo/vacío.");
                return;
            }

            if (contrasena.length() < 8 || contrasena.length() > 16) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"La contraseña debe tener entre 8 y 16 caracteres.\"}");
                return;
            }

            if (!correo.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Formato de correo electrónico inválido.\"}");
                return;
            }

            // Cifrar la contraseña con BCrypt
            String hashedPassword = BCrypt.hashpw(contrasena, BCrypt.gensalt());

            try (Connection conn = Conexion.conectar()) {

                // Validar que el correo no exista
                String checkEmail = "SELECT COUNT(*) FROM usuarios WHERE Correo = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkEmail)) {
                    ps.setString(1, correo);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.write("{\"error\":\"El correo ya está registrado.\"}");
                        return;
                    }
                }

                // Obtener el ID del rol
                int rolId = -1;
                String rolSql = "SELECT idRol FROM roles WHERE Nombre_rol = ?";
                try (PreparedStatement ps = conn.prepareStatement(rolSql)) {
                    ps.setString(1, nombreRolSeleccionado);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        rolId = rs.getInt("idRol");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"error\":\"Rol inválido o no existe.\"}");
                        System.err.println("ERROR: Rol '" + nombreRolSeleccionado + "' no encontrado en la base de datos.");
                        return;
                    }
                }

                // Insertar usuario con contraseña cifrada
                String insertSql = "INSERT INTO usuarios (Nombre_usuario, Apellido_usuario, Telefono, Correo, Direccion, Contraseña, Estado_usuario, Tipo_documento, Documento_usuario, Rol_IdRol) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, nombreUsuario);
                    ps.setString(2, apellidoUsuario);
                    ps.setString(3, telefono);
                    ps.setString(4, correo);
                    ps.setString(5, direccion);
                    ps.setString(6, hashedPassword);
                    ps.setInt(7, 1);
                    ps.setString(8, tipoDocumento);
                    ps.setString(9, documentoUsuario);
                    ps.setInt(10, rolId);

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        out.write("{\"success\":true,\"message\":\"Usuario registrado exitosamente.\"}");
                        System.out.println("Usuario registrado exitosamente: " + correo);
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.write("{\"error\":\"No se pudo registrar el usuario.\"}");
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error SQL al registrar usuario: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                String userErrorMessage = "Error de base de datos al registrar. Por favor, intente de nuevo más tarde.";
                if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("'Correo'")) {
                    userErrorMessage = "El correo electrónico ya está registrado.";
                } else if (e.getMessage().contains("Data too long for column")) {
                    userErrorMessage = "Uno de los campos es demasiado largo. Por favor, revise los datos.";
                } else if (e.getMessage().contains("Unknown column")) {
                    userErrorMessage = "Problema con la configuración de la base de datos (columna no encontrada).";
                    System.err.println("ERROR: Verifica el 'INSERT INTO usuarios' y los nombres de las columnas en tu DB.");
                } else if (e.getMessage().contains("Cannot add or update a child row: a foreign key constraint fails")) {
                    userErrorMessage = "Error de rol. Asegúrate de que el rol seleccionado exista.";
                } else if (e.getMessage().contains("Communications link failure") || e.getMessage().contains("No suitable driver found")) {
                    userErrorMessage = "Error de conexión con la base de datos. Asegúrate de que MySQL esté corriendo y los datos de conexión sean correctos.";
                }
                out.write("{\"error\":\"" + escapeJson(userErrorMessage) + "\"}");
            } catch (Exception e) {
                System.err.println("Error inesperado al registrar usuario: " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"error\":\"Error interno del servidor al registrar: " + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private String escapeJson(String input) {
        return input == null ? "" : input.replace("\"", "'").replace("\n", " ").replace("\r", " ");
    }
}