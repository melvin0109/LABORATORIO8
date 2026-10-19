package EJERCICIO2;


import java.util.Scanner;

public class ConsultasProductos {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        GestorProductos gestor = new GestorProductos();

        try {
            
            gestor.cargarDesdeBD();

            System.out.println("\n=== CONSULTA DE PRODUCTOS ===");

            System.out.println(
                "Campos disponibles: id, nombre, precio, stock"
            );

            System.out.print(
                "Campos que desea mostrar (separados por coma): "
            );

            String[] campos = sc.nextLine()
                                .toLowerCase()
                                .replace(" ", "")
                                .split(",");

            System.out.print(
                "Campo para la condicion (Enter para omitir): "
            );
            String campoCondicion = sc.nextLine().trim();

            String operador = null;
            String valor = null;

            if (!campoCondicion.isEmpty()) {

                System.out.println(
                    "Operadores: =, !=, >, <, >=, <=, contiene"
                );

                System.out.print("Operador: ");
                operador = sc.nextLine().trim();

                System.out.print("Valor de la condicion: ");
                valor = sc.nextLine().trim();
            } else {
                campoCondicion = null;
            }

            System.out.print(
                "Campo para ordenar (Enter para omitir): "
            );

            String campoOrden = sc.nextLine().trim();

            boolean ascendente = true;

            if (!campoOrden.isEmpty()) {

                System.out.print(
                    "Orden (ASC = ascendente, DESC = descendente): "
                );

                String orden = sc.nextLine().trim();

                if (!orden.equalsIgnoreCase("ASC") &&
                    !orden.equalsIgnoreCase("DESC")) {

                    throw new IllegalArgumentException(
                        "Debe escribir ASC o DESC."
                    );
                }

                ascendente = orden.equalsIgnoreCase("ASC");

            } else {
                campoOrden = null;
            }

            System.out.print(
                "Limite de registros (0 = todos): "
            );

            int limite = Integer.parseInt(sc.nextLine());

            System.out.println("\n=== RESULTADOS ===");

            gestor.consultar(
                campos,
                campoCondicion,
                operador,
                valor,
                campoOrden,
                ascendente,
                limite
            );
            
   

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
