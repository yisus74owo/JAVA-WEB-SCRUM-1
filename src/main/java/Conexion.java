// Archivo: src/main/java/Conexion.java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Revisa estos valores con EXTREMA PRECISIÓN:
    // "localhost" si MySQL está en la misma máquina que Tomcat.
    // "3306" es el puerto por defecto de MySQL. Si lo cambiaste, ajústalo.
    // "mydb" debe ser el nombre EXACTO de tu base de datos (sensible a mayúsculas/minúsculas).
    // Los parámetros 'useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' son importantes para evitar warnings/errores en MySQL 8.x
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root"; // Tu usuario de MySQL
    private static final String PASSWORD = "JUAncHitoM4R$n1t0"; // Tu contraseña de MySQL

    public static Connection conectar() throws SQLException {
        try {
            // Este es el driver para MySQL Connector/J 8.x.
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver no encontrado. Asegúrate de que 'mysql-connector-java.jar' está en tu WEB-INF/lib.");
            e.printStackTrace();
            throw new SQLException("Error al cargar el driver de MySQL: " + e.getMessage(), e);
        }
    }
}