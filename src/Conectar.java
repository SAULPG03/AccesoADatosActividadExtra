import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conectar {
    private String url;
    private String usuario;
    private String password;

    public Conectar(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}