/**************************************************************************************
* Fecha: 10/10/2025
* Autor: Gabriel Jaramillo, Roberth Méndez, Mariana Osorio Vasquez, Juan Esteban Vera
* Tema: 
* - Proyecto préstamo de libros (Sistema Distribuido)
* Descripción:
* - Clase Implementación de Servicio Remoto (GestorAlmacenamientoImpl):
* - Implementa la interfaz RMI del Gestor de Almacenamiento (GA).
* - Actúa como el puente entre los Actores y la lógica de la BaseDatos local.
* - Contiene la lógica para aplicar las operaciones (Devolución y Renovación) 
* delegándolas a BaseDatos.
* - Incluye la funcionalidad de replicación asíncrona al Gestor de Almacenamiento 
* de la sede remota, indicando su rol ("Primario" o "Secundario").
***************************************************************************************/
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GestorAlmacenamientoImpl extends UnicastRemoteObject implements GestorAlmacenamiento {

    private static final long serialVersionUID = 1L;
    private final BaseDatos baseDatos;
    private final String rol;

    public GestorAlmacenamientoImpl(BaseDatos bd, String rol) throws RemoteException {
        super();
        this.baseDatos = bd;
        this.rol = rol;
    }

    @Override
    public synchronized boolean aplicarDevolucionEnBD(String codigoLibro, String usuarioId) throws RemoteException {
        boolean exito = baseDatos.devolverEjemplar(codigoLibro);
        System.out.println("GA (" + rol + "): devolución de " + codigoLibro + " -> " + exito);
        return exito;
    }

    @Override
    public synchronized boolean aplicarRenovacionEnBD(String codigoLibro, String usuarioId, String nuevaFechaEntrega) throws RemoteException {
        boolean exito = baseDatos.renovarPrestamo(codigoLibro, usuarioId, nuevaFechaEntrega);
        System.out.println("GA (" + rol + "): renovación de " + codigoLibro + " -> " + exito);
        return exito;
    }

    @Override
    public synchronized boolean aplicarPrestamoEnBD(String codigoLibro, String usuarioId) throws RemoteException {
        boolean exito = baseDatos.prestarEjemplar(codigoLibro);
        System.out.println("GA (" + rol + "): préstamo de " + codigoLibro + " -> " + exito);
        return exito;
    }

    @Override
    public String dumpDB() throws RemoteException {
        return baseDatos.dumpResumen();
    }

    @Override
    public void replicarOperacion(String op, String codigoLibro, String usuarioId, String fecha) throws RemoteException {
        System.out.println("GA (" + rol + ") replicando operación: " + op + " - " + codigoLibro + " - " + usuarioId);
    }
}
