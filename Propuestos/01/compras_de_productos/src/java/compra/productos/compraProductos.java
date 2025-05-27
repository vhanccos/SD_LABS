/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/WebService.java to edit this template
 */
package compra.productos;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Jean
 */
@WebService(serviceName = "compraProductos")
public class compraProductos {

    /**
     * Web service operation
     * @param CantidadPan
     * @param CantidadQueso
     * @param CantidadPlatanos
     * @param CantidadToronjas
     * @return 
     */
    @WebMethod(operationName = "comprasProductos")
    public String comprasProductos(@WebParam(name = "CantidadPan") int CantidadPan, @WebParam(name = "CantidadQueso") int CantidadQueso, @WebParam(name = "CantidadPlatanos") int CantidadPlatanos, @WebParam(name = "CantidadToronjas") int CantidadToronjas) {
        //TODO write your implementation code here:
        String mensaje = "";
        double total = 0;
        
        if (CantidadPan<1 || CantidadQueso < 1 || CantidadPlatanos<1 || CantidadToronjas< 1){
            mensaje += "Lo siento, ingrese una cantidad positiva.";
        }
        else {
            total += CantidadPan *0.5;
            mensaje += "\n Pan: "+CantidadPan +"Unidades --> SubTotal: "+CantidadPan*0.5;
            
            total += CantidadQueso *2.5;
            mensaje += "\n Queso: "+CantidadQueso +"Unidades --> SubTotal: "+CantidadQueso*2.5;
            
            total += CantidadPlatanos*0.4;
            mensaje += "\n Platanos: "+CantidadPlatanos +"Unidades --> SubTotal: "+CantidadPlatanos*0.4;
            
            total += CantidadToronjas*0.4;
            mensaje += "\n Toronjas: "+CantidadToronjas +"Unidades --> SubTotal: "+CantidadToronjas*0.4;
            
        }
        return mensaje;
    }


}
