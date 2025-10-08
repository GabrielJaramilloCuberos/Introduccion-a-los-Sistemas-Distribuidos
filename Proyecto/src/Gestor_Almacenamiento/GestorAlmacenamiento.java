import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GestorAlmacenamiento extends Remote {

    boolean aplicarDevolucionEnBD(String codigoLibro, String usuarioId) throws RemoteException;
    boolean aplicarRenovacionEnBD(String codigoLibro, String usuarioId, String nuevaFechaEntrega) throws RemoteException;
    boolean aplicarPrestamoEnBD(String codigoLibro, String usuarioId) throws RemoteException;

    String dumpDB() throws RemoteException;
    void replicarOperacion(String op, String codigoLibro, String usuarioId, String fecha) throws RemoteException;
}
