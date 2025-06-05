import java.io.Serializable; // Importa la interfaz Serializable, que permite que los objetos de esta clase se puedan guardar y recuperar (por ejemplo, en archivos o al enviarlos por red).

// Se crea una clase llamada "Usuario" que implementa la interfaz Serializable.
// Esto significa que se podrán convertir los objetos de esta clase a una secuencia de bytes (por ejemplo, para guardarlos).
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L; // Es un identificador único para la versión de esta clase, útil al serializar/deserializar.

    // Se definen tres atributos privados (solo accesibles dentro de la clase):
    private String email;     // Guarda el correo del usuario
    private String password;  // Guarda la contraseña del usuario
    private String nombre;    // Guarda el nombre del usuario

    // Constructor vacío (por defecto), necesario si no se pasa ningún dato al crear un usuario.
    public Usuario() {}

    // Constructor que recibe el email y la contraseña al crear un nuevo usuario.
    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Métodos "getters" y "setters" permiten acceder y modificar los atributos desde fuera de la clase.

    // Devuelve el correo del usuario
    public String getEmail() {
        return email;
    }

    // Permite cambiar el correo del usuario
    public void setEmail(String email) {
        this.email = email;
    }

    // Devuelve la contraseña del usuario
    public String getPassword() {
        return password;
    }

    // Permite cambiar la contraseña del usuario
    public void setPassword(String password) {
        this.password = password;
    }

    // Devuelve el nombre del usuario
    public String getNombre() {
        return nombre;
    }

    // Permite cambiar el nombre del usuario
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
