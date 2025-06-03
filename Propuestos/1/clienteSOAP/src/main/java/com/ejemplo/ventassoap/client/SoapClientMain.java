/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ejemplo.ventassoap.client;

import java.net.URL;
import java.util.List;
import java.util.Scanner;

/**
 * Cliente SOAP para el servicio de ventas
 * Este cliente consume el servicio SOAP de ventas corriendo en http://localhost:8080/ws/ventas
 */
public class SoapClientMain {
    
    public static void main(String[] args) {
        try {
            // URL del WSDL del servicio
            URL wsdlURL = new URL("http://localhost:8080/ws/ventas?wsdl");
            
            // Crear el servicio
            SalesServiceImplService service = new SalesServiceImplService(wsdlURL);
            SalesService port = service.getSalesServiceImplPort();
            
            Scanner scanner = new Scanner(System.in);
            int opcion;
            
            System.out.println("=== Cliente SOAP - Servicio de Ventas ===");
            
            do {
                mostrarMenu();
                System.out.print("Seleccione una opcion: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir la línea restante
                
                switch (opcion) {
                    case 1:
                        venderProducto(port, scanner);
                        break;
                    case 2:
                        listarProductos(port);
                        break;
                    case 3:
                        System.out.println("¡Gracias por usar el cliente SOAP!");
                        break;
                    default:
                        System.out.println("Opcion no valida. Intente nuevamente.");
                }
                
                System.out.println();
            } while (opcion != 3);
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio SOAP:");
            System.err.println("Asegurese de que el servicio este corriendo en http://localhost:8080/ws/ventas");
            e.printStackTrace();
        }
    }
    
    private static void mostrarMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1. Vender producto");
        System.out.println("2. Listar productos vendidos");
        System.out.println("3. Salir");
    }
    
    private static void venderProducto(SalesService port, Scanner scanner) {
        try {
            System.out.print("Ingrese el nombre del producto: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Ingrese el precio del producto: ");
            double precio = scanner.nextDouble();
            scanner.nextLine(); // Consumir la línea restante
            
            // Crear el producto
            Product producto = new Product();
            producto.setName(nombre);
            producto.setPrice(precio);
            
            // Llamar al servicio SOAP
            String resultado = port.sellProduct(producto);
            System.out.println("Respuesta del servicio: " + resultado);
            
        } catch (Exception e) {
            System.err.println("Error al vender producto: " + e.getMessage());
        }
    }
    
    private static void listarProductos(SalesService port) {
        try {
            List<Product> productos = port.listProducts();
            
            if (productos.isEmpty()) {
                System.out.println("No hay productos vendidos aún.");
            } else {
                System.out.println("=== Productos Vendidos ===");
                for (int i = 0; i < productos.size(); i++) {
                    Product p = productos.get(i);
                    System.out.printf("%d. %s - $%.2f%n", 
                        i + 1, p.getName(), p.getPrice());
                }
                System.out.printf("Total de productos vendidos: %d%n", productos.size());
                
                // Calcular total de ventas
                double totalVentas = productos.stream()
                    .mapToDouble(Product::getPrice)
                    .sum();
                System.out.printf("Total en ventas: $%.2f%n", totalVentas);
            }
            
        } catch (Exception e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
    }
}