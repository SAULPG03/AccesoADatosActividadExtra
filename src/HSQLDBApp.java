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
            System.out.println("Conexion exitosa a HSQLDB!");

            int opcion;
            do {
                System.out.println("\nMenu");
                System.out.println("1. Mostrar todos los datos de los alumnos.");
                System.out.println("2. Mostrar todos los datos de los profesores.");
                System.out.println("3. Mostrar las asignaturas impartidas por un profesor.");
                System.out.println("4. Mostrar los alumnos matriculados en una asignatura.");
                System.out.println("0. Salir.");
                System.out.print("Seleccione una opcion: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        mostrarAlumnos(conn);
                        break;
                    case 2:
                        mostrarProfesores(conn);
                        break;
                    case 3:
                        mostrarAsignaturasPorProfesor(conn, scanner);
                        break;
                    case 4: 
                        mostrarAlumnosPorAsignatura(conn, scanner);
                        break;
                    case 0:
                        System.out.println("Te has salido.");
                        break;
                    default:
                        System.out.println("Opción no valida. Intente nuevamente.");
                        break;
                }
            } while (opcion != 0);

        } catch (SQLException e) {
            System.err.println("Error al conectar a HSQLDB: " + e.getMessage());
        }
    }

    private static void mostrarAlumnos(Connection conn) {
        String pregunta = "SELECT * FROM PUBLIC.ALUMNO";
        try (Statement llamada = conn.createStatement(); ResultSet rs = llamada.executeQuery(pregunta)) {
            System.out.println("\nDatos de los Alumnos");
            while (rs.next()) {
                System.out.printf("DNI: %s, Nombre: %s, Apellidos: %s, Direccion: %s, Telefono: %d, Nota: %s\n",
                        rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"),
                        rs.getString("dirección"), rs.getInt("tfno"), rs.getString("nota_expediente"));
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar los datos de los alumnos: " + e.getMessage());
        }
    }

    private static void mostrarProfesores(Connection conn) {
        String pregunta = "SELECT * FROM PUBLIC.PROFESOR";
        try (Statement llamada = conn.createStatement(); ResultSet rs = llamada.executeQuery(pregunta)) {
            System.out.println("\nDatos de los Profesores");
            while (rs.next()) {
                System.out.printf("DNI: %s, Nombre: %s, Apellidos: %s, Titulacion: %s\n",
                        rs.getString("dni_prof"), rs.getString("nombre"), rs.getString("apellidos"),
                        rs.getString("titulacion"));
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar los datos de los profesores: " + e.getMessage());
        }
    }

    private static void mostrarAsignaturasPorProfesor(Connection conn, Scanner scanner) {
        System.out.print("Ingrese el DNI del profesor: ");
        String dni = scanner.next();
        String pregunta = "SELECT asignatura.cod_asig, asignatura.nombre FROM PUBLIC.ASIGNATURA asignatura " +
                "JOIN PUBLIC.IMPARTE imparte ON asignatura.cod_asig = imparte.cod_asig " +
                "WHERE imparte.dni_prof = ?";
        try (PreparedStatement llamada = conn.prepareStatement(pregunta)) {
            llamada.setString(1, dni);
            try (ResultSet rs = llamada.executeQuery()) {
                System.out.println("\nAsignaturas Impartidas");
                while (rs.next()) {
                    System.out.printf("Codigo: %d, Nombre: %s\n",
                            rs.getInt("cod_asig"), rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar las asignaturas: " + e.getMessage());
        }
    }

    private static void mostrarAlumnosPorAsignatura(Connection conn, Scanner scanner) {
        System.out.print("Ingrese el codigo de la asignatura: ");
        int codAsig = scanner.nextInt();
        String pregunta = "SELECT asignatura.dni, asignatura.nombre, asignatura.apellidos FROM PUBLIC.ALUMNO asignatura " +
                "JOIN PUBLIC.MATRICULA matricula ON asignatura.dni = matricula.dni " +
                "WHERE matricula.cod_asig = ?";
        try (PreparedStatement llamada = conn.prepareStatement(pregunta)) {
            llamada.setInt(1, codAsig);
            try (ResultSet rs = llamada.executeQuery()) {
                System.out.println("\nAlumnos Matriculados");
                while (rs.next()) {
                    System.out.printf("DNI: %s, Nombre: %s, Apellidos: %s\n",
                            rs.getString("dni"), rs.getString("nombre"), rs.getString("apellidos"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar los alumnos: " + e.getMessage());
        }
    }
}