package EJERCICIO2;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GestorProductos {

    private Producto[] productos = new Producto[0];

    // Carga los registros de SQLite en el arreglo.
    public void cargarDesdeBD() throws Exception {

        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT id, nombre, precio, stock FROM Producto";

        try (Connection con = Conexion.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                );

                lista.add(p);
            }
        }

        productos = lista.toArray(new Producto[0]);

        System.out.println(
            "Registros cargados en memoria: " + productos.length
        );
    }

    // Método que permite consultar el arreglo de objetos.
    public void consultar(
            String[] campos,
            String campoCondicion,
            String operador,
            String valor,
            String campoOrden,
            boolean ascendente,
            int limite) {

        if (campos == null || campos.length == 0 || limite < 0) {
            throw new IllegalArgumentException(
                "Campos o limite no validos."
            );
        }

        // Validar los campos solicitados.
        for (String campo : campos) {
            validarCampo(campo);
        }

        if (campoCondicion != null && !campoCondicion.isEmpty()) {
            validarCampo(campoCondicion);

            if (operador == null || valor == null) {
                throw new IllegalArgumentException(
                    "Falta el operador o el valor."
                );
            }
        }

        if (campoOrden != null && !campoOrden.isEmpty()) {
            validarCampo(campoOrden);
        }

        // Copiar los registros para no modificar el arreglo original.
        List<Producto> resultado =
            new ArrayList<>(Arrays.asList(productos));

        // b) Filtrar mediante una condición.
        if (campoCondicion != null && !campoCondicion.isEmpty()) {

            resultado.removeIf(p ->
                !cumpleCondicion(
                    obtenerValor(p, campoCondicion),
                    operador,
                    valor,
                    campoCondicion
                )
            );
        }

        // c) Ordenar por el campo solicitado.
        if (campoOrden != null && !campoOrden.isEmpty()) {

            Comparator<Producto> comparador = (p1, p2) ->
                comparar(
                    obtenerValor(p1, campoOrden),
                    obtenerValor(p2, campoOrden),
                    campoOrden
                );

            if (!ascendente) {
                comparador = comparador.reversed();
            }

            resultado.sort(comparador);
        }

        // d) Limitar la cantidad de registros.
        if (limite > 0 && resultado.size() > limite) {
            resultado = new ArrayList<>(
                resultado.subList(0, limite)
            );
        }

        // a) Mostrar solamente los campos seleccionados.
        for (Producto p : resultado) {
            for (int i = 0; i < campos.length; i++) {
                String campo = campos[i];

                System.out.print(
                    campo + ": " + obtenerValor(p, campo)
                );

                if (i < campos.length - 1) {
                    System.out.print(" | ");
                }
            }

            System.out.println();
        }

        System.out.println(
            "Total de registros encontrados: " + resultado.size()
        );
    }

    // Obtener el valor de un campo de un objeto.
    private Object obtenerValor(Producto p, String campo) {

        switch (campo.toLowerCase()) {
            case "id":
                return p.getId();
            case "nombre":
                return p.getNombre();
            case "precio":
                return p.getPrecio();
            case "stock":
                return p.getStock();
            default:
                throw new IllegalArgumentException(
                    "Campo desconocido: " + campo
                );
        }
    }

    // Evitar que se utilicen campos inexistentes.
    private void validarCampo(String campo) {

        if (campo == null ||
            !(campo.equalsIgnoreCase("id") ||
              campo.equalsIgnoreCase("nombre") ||
              campo.equalsIgnoreCase("precio") ||
              campo.equalsIgnoreCase("stock"))) {

            throw new IllegalArgumentException(
                "Campo no valido: " + campo
            );
        }
    }

    // Evaluar las condiciones.
    private boolean cumpleCondicion(
            Object dato,
            String operador,
            String valor,
            String campo) {

        int comparacion;

        if (dato instanceof Number) {

            double numeroDato = ((Number) dato).doubleValue();
            double numeroValor;

            try {
                numeroValor = Double.parseDouble(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "La condicion requiere un numero."
                );
            }

            comparacion = Double.compare(numeroDato, numeroValor);

        } else {
            comparacion = dato.toString()
                              .compareToIgnoreCase(valor);
        }

        switch (operador) {
            case "=":
                return comparacion == 0;

            case "!=":
                return comparacion != 0;

            case ">":
                return comparacion > 0;

            case "<":
                return comparacion < 0;

            case ">=":
                return comparacion >= 0;

            case "<=":
                return comparacion <= 0;

            case "contiene":
                if (!(dato instanceof String)) {
                    throw new IllegalArgumentException(
                        "'contiene' requiere un campo de texto."
                    );
                }

                return ((String) dato).toLowerCase()
                    .contains(valor.toLowerCase());

            default:
                throw new IllegalArgumentException(
                    "Operador no valido: " + operador
                );
        }
    }

    // Comparar dos valores para el ordenamiento.
    private int comparar(Object a, Object b, String campo) {

        if (a instanceof Number && b instanceof Number) {
            return Double.compare(
                ((Number) a).doubleValue(),
                ((Number) b).doubleValue()
            );
        }

        return a.toString().compareToIgnoreCase(b.toString());
    }
}
