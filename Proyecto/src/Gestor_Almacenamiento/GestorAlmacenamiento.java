import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GestorAlmacenamiento extends Remote {
    public boolean aplicarDevolucionEnBD(String codigoLibro, String usuarioId) throws RemoteException;
    public boolean aplicarRenovacionEnBD(String codigoLibro, String usuarioId, String nuevaFechaEntrega) throws RemoteException;
    public String dumpDB() throws RemoteException;
    public void replicarOperacion(String op, String codigoLibro, String usuarioId, String fecha) throws RemoteException;
}
