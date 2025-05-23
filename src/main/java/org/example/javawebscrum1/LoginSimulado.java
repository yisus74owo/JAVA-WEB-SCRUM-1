package org.example.javawebscrum1;

import java.util.Scanner;

public class LoginSimulado {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Usuario y contraseña válidos simulados
        String usuarioValido = "admin@ohana.com";
        String contrasenaValida = "ohana123";

        System.out.print("Ingrese su correo: ");
        String correoIngresado = scanner.nextLine();

        System.out.print("Ingrese su contraseña: ");
        String contrasenaIngresada = scanner.nextLine();

        if (correoIngresado.equals(usuarioValido) && contrasenaIngresada.equals(contrasenaValida)) {
            System.out.println("Inicio de sesión exitoso. Redirigiendo al inventario...");
            // Aquí puedes simular que se abre otra ventana o función
        } else {
            System.out.println("Usuario inexistente o contraseña incorrecta.");
        }
    }
}

