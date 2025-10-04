import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GestorAlmacenamientoImpl extends UnicastRemoteObject implements GestorAlmacenamiento {

    private static final long serialVersionUID = 1L;
    private final BaseDatos baseDatos;
    private final String role;
    private String replicaHost = null;

    protected GestorAlmacenamientoImpl(BaseDatos bd, String role) throws RemoteException {
        super();
        this.baseDatos = bd;
        this.role = role;
    }

    @Override
    public boolean aplicarDevolucionEnBD(String codigoLibro, String usuarioId) throws RemoteException {
        boolean ok = baseDatos.devolverEjemplar(codigoLibro);
        System.out.println("[" + role + "] aplicarDevolucionEnBD(" + codigoLibro + ") -> " + ok);
        if (replicaHost != null) {
            System.out.println("[" + role + "] replicando devolucion a replica en " + replicaHost);
        }
        return ok;
    }

    @Override
    public boolean aplicarRenovacionEnBD(String codigoLibro, String usuarioId, String nuevaFechaEntrega) throws RemoteException {
        boolean ok = baseDatos.renovarPrestamo(codigoLibro, usuarioId, nuevaFechaEntrega);
        System.out.println("[" + role + "] aplicarRenovacionEnBD(" + codigoLibro + "," + usuarioId + ") -> " + ok);
        if (replicaHost != null) {
            System.out.println("[" + role + "] replicando renovacion a replica en " + replicaHost);
        }
        return ok;
    }

    @Override
    public String dumpDB() throws RemoteException {
        return baseDatos.dumpResumen();
    }

    @Override
    public void replicarOperacion(String op, String codigoLibro, String usuarioId, String fecha) throws RemoteException {
        System.out.println("[" + role + "] solicitar replicacion op=" + op + " libro=" + codigoLibro);
    }

    public void setReplicaHost(String host) {
        this.replicaHost = host;
    }
}
