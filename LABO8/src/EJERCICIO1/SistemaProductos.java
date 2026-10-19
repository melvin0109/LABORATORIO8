package EJERCICIO1;


import java.sql.*;
import java.util.Scanner;

public class SistemaProductos {

    private static final Scanner sc = new Scanner(System.in);
    private static final String CLAVE = "1234";

    public static void main(String[] args) {

        try {
            // Verificar la conexión y crear la tabla.
            try (Connection con = Conexion.conectar()) {
                System.out.println("Base de datos conectada correctamente.");
            }

            int opcion;

            do {
                System.out.println("\n===== MENU DE PRODUCTOS =====");
                System.out.println("1. Ingresar producto");
                System.out.println("2. Borrar producto");
                System.out.println("3. Actualizar producto");
                System.out.println("4. Mostrar productos");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opcion: ");

                opcion = Integer.parseInt(sc.nextLine());

                try {
                    switch (opcion) {
                        case 1:
                            ingresar();
                            break;
                        case 2:
                            borrar();
                            break;
                        case 3:
                            actualizar();
                            break;
                        case 4:
                            mostrar();
                            break;
                        case 0:
                            System.out.println("Programa finalizado.");
                            break;
                        default:
                            System.out.println("Opcion no valida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Debe ingresar un numero valido.");
                } catch (SQLException e) {
                    System.out.println("Error en la base de datos: "
                                       + e.getMessage());
                }

            } while (opcion != 0);

        } catch (SQLException e) {
            System.out.println("No se pudo conectar: " + e.getMessage());
        }
    }

    // INSERT: registrar un producto.
    public static void ingresar() throws SQLException {

        System.out.print("Nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Precio: ");
        double precio = Double.parseDouble(sc.nextLine());

        System.out.print("Stock: ");
        int stock = Integer.parseInt(sc.nextLine());

        if (nombre.trim().isEmpty() || precio < 0 || stock < 0) {
            System.out.println("Datos invalidos.");
            return;
        }

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);

            try {
                String sql = "INSERT INTO Producto(nombre, precio, stock) "
                           + "VALUES (?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setDouble(2, precio);
                    ps.setInt(3, stock);
                    ps.executeUpdate();
                }

                confirmar(con);

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // DELETE: eliminar un producto.
    public static void borrar() throws SQLException {

        System.out.print("ID del producto que desea borrar: ");
        int id = Integer.parseInt(sc.nextLine());

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);

            try {
                String sql = "DELETE FROM Producto WHERE id = ?";

                int filas;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    con.rollback();
                    System.out.println("No existe ese producto.");
                } else {
                    confirmar(con);
                }

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // UPDATE: modificar un producto.
    public static void actualizar() throws SQLException {

        System.out.print("ID del producto: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Nuevo nombre: ");
        String nombre = sc.nextLine();

        System.out.print("Nuevo precio: ");
        double precio = Double.parseDouble(sc.nextLine());

        System.out.print("Nuevo stock: ");
        int stock = Integer.parseInt(sc.nextLine());

        if (nombre.trim().isEmpty() || precio < 0 || stock < 0) {
            System.out.println("Datos invalidos.");
            return;
        }

        try (Connection con = Conexion.conectar()) {
            con.setAutoCommit(false);

            try {
                String sql = "UPDATE Producto "
                           + "SET nombre = ?, precio = ?, stock = ? "
                           + "WHERE id = ?";

                int filas;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setDouble(2, precio);
                    ps.setInt(3, stock);
                    ps.setInt(4, id);
                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    con.rollback();
                    System.out.println("No existe ese producto.");
                } else {
                    confirmar(con);
                }

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // SELECT: mostrar todos los productos.
    public static void mostrar() throws SQLException {

        String sql = "SELECT * FROM Producto ORDER BY id";

        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== PRODUCTOS REGISTRADOS =====");

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );

                System.out.println(p);
            }
        }
    }

    // Confirma los cambios solamente si la clave es correcta.
    private static void confirmar(Connection con)
            throws SQLException {

        System.out.print("Ingrese la clave para confirmar: ");
        String clave = sc.nextLine();

        if (CLAVE.equals(clave)) {
            con.commit();
            System.out.println("Cambios confirmados correctamente.");
        } else {
            con.rollback();
            System.out.println("Clave incorrecta. Cambios revertidos.");
        }
    }
}
