package EJERCICIO2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String URL = "jdbc:sqlite:tienda.db";

    public static Connection conectar() throws SQLException {
        Connection con = DriverManager.getConnection(URL);

        try (Statement st = con.createStatement()) {
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS Producto (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "precio REAL NOT NULL, " +
                "stock INTEGER NOT NULL)"
            );
        }

        return con;
    }
}
