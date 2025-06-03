
package com.ejemplo.ventassoap1;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import java.util.List;

@WebService
public interface SalesService {

    @WebMethod
    String sellProduct(Product product);

    @WebMethod
    List<Product> listProducts();
}