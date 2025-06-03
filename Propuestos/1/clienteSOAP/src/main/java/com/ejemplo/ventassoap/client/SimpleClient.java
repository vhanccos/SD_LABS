package com.ejemplo.ventassoap.client;

import java.net.URL;
import java.util.List;

/**
 * Cliente SOAP simple para demostrar el uso básico del servicio de ventas
 */
public class SimpleClient {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== Cliente SOAP Simple - Servicio de Ventas ===");
            
            // URL del WSDL del servicio
            URL wsdlURL = new URL("http://localhost:8080/ws/ventas?wsdl");
            
            // Crear el servicio
            SalesServiceImplService service = new SalesServiceImplService(wsdlURL);
            SalesService port = service.getSalesServiceImplPort();
            
            System.out.println("Conectado al servicio SOAP exitosamente!");
            
            // Crear algunos productos de prueba
            Product producto1 = new Product();
            producto1.setName("Laptop");
            producto1.setPrice(1500.00);
            
            Product producto2 = new Product();
            producto2.setName("Mouse");
            producto2.setPrice(25.50);
            
            Product producto3 = new Product();
            producto3.setName("Teclado");
            producto3.setPrice(45.99);
            
            // Vender productos
            System.out.println("\n--- Vendiendo productos ---");
            String resultado1 = port.sellProduct(producto1);
            System.out.println(resultado1);
            
            String resultado2 = port.sellProduct(producto2);
            System.out.println(resultado2);
            
            String resultado3 = port.sellProduct(producto3);
            System.out.println(resultado3);
            
            // Listar productos vendidos
            System.out.println("\n--- Productos vendidos ---");
            List<Product> productos = port.listProducts();
            
            if (!productos.isEmpty()) {
                double total = 0;
                for (int i = 0; i < productos.size(); i++) {
                    Product p = productos.get(i);
                    System.out.printf("%d. %s - $%.2f%n", 
                        i + 1, p.getName(), p.getPrice());
                    total += p.getPrice();
                }
                System.out.printf("\nTotal de productos: %d%n", productos.size());
                System.out.printf("Total en ventas: $%.2f%n", total);
            } else {
                System.out.println("No hay productos vendidos.");
            }
            
            System.out.println("\n¡Prueba completada exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error al conectar con el servicio SOAP:");
            System.err.println("Asegúrese de que el servicio esté corriendo en http://localhost:8080/ws/ventas");
            e.printStackTrace();
        }
    }
}