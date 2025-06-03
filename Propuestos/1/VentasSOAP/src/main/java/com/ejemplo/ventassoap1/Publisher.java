package com.ejemplo.ventassoap1;

import jakarta.xml.ws.Endpoint;

public class Publisher {
    public static void main(String[] args) {
        String url = "http://localhost:8080/ws/ventas";
        Endpoint.publish(url, new SalesServiceImpl());
        System.out.println("Servicio SOAP publicado en: " + url);
    }
}