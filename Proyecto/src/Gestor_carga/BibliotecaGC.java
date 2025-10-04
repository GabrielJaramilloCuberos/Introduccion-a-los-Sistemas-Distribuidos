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
