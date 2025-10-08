import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BibliotecaGC extends Remote {
    String devolverLibroAsync(String codigoLibro, String usuarioId) throws RemoteException;
    String renovarLibroAsync(String codigoLibro, String usuarioId) throws RemoteException;
    String prestarLibroAsync(String codigoLibro, String usuarioId) throws RemoteException; // nuevo
    Message fetchNextMessage(String topic) throws RemoteException;
    void ackMessage(String messageId, boolean success) throws RemoteException;
    String getMessageStatus(String messageId) throws RemoteException;
    void setGestorAlmacenamientoEndpoint(String host) throws RemoteException;
}
