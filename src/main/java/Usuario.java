import java.io.Serializable;

// La interfaz Serializable permite que los objetos de esta clase se puedan guardar y recuperar.
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador único para la versión de la clase.

    private String email;     // Correo del usuario
    private String password;  // Contraseña del usuario (¡ADVERTENCIA: No se usa en sesión, pero está definida!)
    private String nombre;    // Nombre del usuario
    private String rol;       // Rol del usuario (Administrador, Veterinario, Auxiliar, etc.)

    // Constructor vacío
    public Usuario() {}

    // Constructor con email y password (útil para la autenticación inicial)
    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters para acceder y modificar los atributos

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}