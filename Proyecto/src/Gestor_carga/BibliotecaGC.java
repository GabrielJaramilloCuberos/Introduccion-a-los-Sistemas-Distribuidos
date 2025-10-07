/**************************************************************************************
* Fecha: 10/10/2025
* Autor: Gabriel Jaramillo, Roberth Méndez, Mariana Osorio Vasquez, Juan Esteban Vera
* Tema: 
* - Proyecto préstamo de libros (Sistema Distribuido)
* Descripción:
* - Clase Interfaz de Servicio Remoto (BibliotecaGC):
* - Define el contrato RMI (`Remote`) para el Gestor de Carga (GC).
* - Proporciona los métodos asíncronos (`*Async`) que usan los clientes 
* para enviar solicitudes de operación.
* - Proporciona los métodos de mensajería (`fetchNextMessage`, `ackMessage`) 
* que usan los Actores para consumir y confirmar la correcta recepción de eventos, 
* simulando el patrón PUB/SUB.
* - Permite a los clientes consultar el estado final de una operación (`getMessageStatus`)
* usando el ID del mensaje.
***************************************************************************************/
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BibliotecaGC extends Remote {
    public String devolverLibroAsync(String codigoLibro, String usuarioId) throws RemoteException;
    public String renovarLibroAsync(String codigoLibro, String usuarioId) throws RemoteException;
    public Message fetchNextMessage(String topic) throws RemoteException;
    public String getMessageStatus(String messageId) throws RemoteException;
    void ackMessage(String messageId, boolean success) throws RemoteException;
    public void setGestorAlmacenamientoEndpoint(String host) throws RemoteException;
}
