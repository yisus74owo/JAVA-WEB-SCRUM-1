import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "JUAncHitoM4R$n1t0"; // Pon aquí tu contraseña si tiene

    public static Connection conectar() {
        Connection conexion = null;

        try {
            // Cargar el driver de MySQL (opcional desde JDBC 4.0, pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el driver de MySQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }

        return conexion;
    }

    public static void main(String[] args) {
        // Probar conexión
        conectar();
    }
}
