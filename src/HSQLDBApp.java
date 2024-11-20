import java.sql.*;
import java.util.Scanner;

public class HSQLDBApp {

    private static final String URL = "jdbc:hsqldb:file:C:\\Users\\Usuario\\Downloads\\hsqldb-2.7.4\\hsqldb\\data";
    private static final String USUARIO = "SA";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Conectar conexion = new Conectar(URL, USUARIO, PASSWORD);
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = conexion.getConexion()) {
            System.out.println("Conexión exitosa a HSQLDB!");

            int opcion;
            do {
                System.out.println("\n--- Menú de opciones ---");
                System.out.println("1. Mostrar todos los datos de los alumnos.");
                System.out.println("2. Mostrar todos los datos de los profesores.");
                System.out.println("3. Mostrar las asignaturas impartidas por un profesor.");
                System.out.println("4. Mostrar los alumnos matriculados en una asignatura.");
                System.out.println("0. Salir.");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir la nueva línea

                switch (opcion) {
                    case 1 -> mostrarAlumnos(conn);
                    case 2 -> mostrarProfesores(conn);
                    case 3 -> mostrarAsignaturasPorProfesor(conn, scanner);
                    case 4 -> mostrarAlumnosPorAsignatura(conn, scanner);
                    case 0 -> System.out.println("Saliendo del programa...");
                    default -> System.out.println("Opción no válida. Intente nuevamente.");
                }
            } while (opcion != 0);

        } catch (SQLException e) {
            System.err.println("Error al conectar a HSQLDB: " + e.getMessage());
        }
    }

    private static void mostrarAlumnos(Connection conn) {
        String query = "SELECT * FROM alumnos";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n--- Datos de los Alumnos ---");
            while (rs.next()) {
                System.out.printf("DNI: %s, Nombre: %s, Apellidos: %s\n",
                        rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"));
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar los datos de los alumnos: " + e.getMessage());
        }
    }

    private static void mostrarProfesores(Connection conn) {
        String query = "SELECT * FROM profesores";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n--- Datos de los Profesores ---");
            while (rs.next()) {
                System.out.printf("DNI: %s, Nombre: %s, Apellidos: %s\n",
                        rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"));
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar los datos de los profesores: " + e.getMessage());
        }
    }

    private static void mostrarAsignaturasPorProfesor(Connection conn, Scanner scanner) {
        System.out.print("\nIngrese el DNI del profesor: ");
        String dni = scanner.nextLine();

        String query = "SELECT asignaturas.codigo, asignaturas.nombre " +
                "FROM asignaturas " +
                "INNER JOIN profesores_asignaturas ON asignaturas.codigo = profesores_asignaturas.codigo_asignatura " +
                "WHERE profesores_asignaturas.dni_profesor = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n--- Asignaturas impartidas por el profesor ---");
                while (rs.next()) {
                    System.out.printf("Código: %s, Nombre: %s\n",
                            rs.getString("codigo"), rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar las asignaturas del profesor: " + e.getMessage());
        }
    }

    private static void mostrarAlumnosPorAsignatura(Connection conn, Scanner scanner) {
        System.out.print("\nIngrese el código de la asignatura: ");
        String codigo = scanner.nextLine();

        String query = "SELECT alumnos.dni, alumnos.nombre, alumnos.apellidos " +
                "FROM alumnos " +
                "INNER JOIN alumnos_asignaturas ON alumnos.dni = alumnos_asignaturas.dni_alumno " +
                "WHERE alumnos_asignaturas.codigo_asignatura = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, codigo);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n--- Alumnos matriculados en la asignatura ---");
                while (rs.next()) {
                    System.out.printf("DNI: %s, Nombre: %s, Apellidos: %s\n",
                            rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar los alumnos matriculados: " + e.getMessage());
        }
    }
}