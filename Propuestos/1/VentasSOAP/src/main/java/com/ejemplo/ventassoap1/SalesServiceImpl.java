package com.ejemplo.ventassoap1;

import jakarta.jws.WebService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "com.ejemplo.ventassoap1.SalesService")
public class SalesServiceImpl implements SalesService {
    private final List<Product> productList = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public String sellProduct(Product product) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] VENTA REGISTRADA:");
        System.out.println("   - Producto: " + product.getName());
        System.out.println("   - Precio: $" + product.getPrice());
        System.out.println("   - Total productos en lista: " + (productList.size() + 1));
        
        productList.add(product);
        
        String response = "Producto vendido: " + product.getName();
        System.out.println("   - Respuesta enviada: " + response);
        System.out.println("----------------------------------------");
        
        return response;
    }
    
    @Override
    public List<Product> listProducts() {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] CONSULTA DE PRODUCTOS:");
        System.out.println("   - Productos en lista: " + productList.size());
        
        if (!productList.isEmpty()) {
            System.out.println("   - Lista de productos:");
            for (int i = 0; i < productList.size(); i++) {
                Product p = productList.get(i);
                System.out.println("     " + (i+1) + ". " + p.getName() + " - $" + p.getPrice());
            }
        }
        
        System.out.println("----------------------------------------");
        return productList;
    }
}