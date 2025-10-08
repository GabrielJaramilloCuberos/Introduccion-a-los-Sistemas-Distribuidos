import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class BibliotecaGCImpl extends UnicastRemoteObject implements BibliotecaGC {
    private static final long serialVersionUID = 1L;

    private final ConcurrentLinkedQueue<Message> colaDevolucion = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> colaRenovacion = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Message> colaPrestamo = new ConcurrentLinkedQueue<>();

    private final Map<String, Message> messagesMap = new ConcurrentHashMap<>();
    private final Map<String, String> messageStatus = new ConcurrentHashMap<>();

    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

    protected BibliotecaGCImpl() throws RemoteException {
        super();
    }

    @Override
    public String devolverLibroAsync(String codigoLibro, String usuarioId) throws RemoteException {
        String id = UUID.randomUUID().toString();
        String fecha = LocalDate.now().format(fmt);
        Message m = new Message(id, "Devolucion", codigoLibro, usuarioId, fecha, null);
        colaDevolucion.add(m);
        messagesMap.put(id, m);
        messageStatus.put(id, "PENDING");
        System.out.println("GC publicó devolución: " + m);
        return id;
    }

    @Override
    public String renovarLibroAsync(String codigoLibro, String usuarioId) throws RemoteException {
        String id = UUID.randomUUID().toString();
        String fecha = LocalDate.now().format(fmt);
        String nuevaFecha = LocalDate.now().plusWeeks(1).format(fmt);
        Message m = new Message(id, "Renovacion", codigoLibro, usuarioId, fecha, nuevaFecha);
        colaRenovacion.add(m);
        messagesMap.put(id, m);
        messageStatus.put(id, "PENDING");
        System.out.println("GC publicó renovación: " + m);
        return id;
    }

    @Override
    public String prestarLibroAsync(String codigoLibro, String usuarioId) throws RemoteException {
        String id = UUID.randomUUID().toString();
        String fecha = LocalDate.now().format(fmt);
        Message m = new Message(id, "Prestamo", codigoLibro, usuarioId, fecha, null);
        colaPrestamo.add(m);
        messagesMap.put(id, m);
        messageStatus.put(id, "PENDING");
        System.out.println("GC publicó préstamo: " + m);
        return id;
    }

    @Override
    public Message fetchNextMessage(String topic) throws RemoteException {
        Message m = null;
        if ("Devolucion".equalsIgnoreCase(topic)) {
            m = colaDevolucion.poll();
        } else if ("Renovacion".equalsIgnoreCase(topic)) {
            m = colaRenovacion.poll();
        } else if ("Prestamo".equalsIgnoreCase(topic)) {
            m = colaPrestamo.poll();
        }
        if (m != null) System.out.println("GC envía mensaje al actor (" + topic + "): " + m);
        return m;
    }

    @Override
    public void ackMessage(String messageId, boolean success) throws RemoteException {
        messageStatus.put(messageId, success ? "OK" : "FAILED");
        System.out.println("GC recibió confirmación: " + messageId + " -> " + messageStatus.get(messageId));
    }

    @Override
    public String getMessageStatus(String messageId) throws RemoteException {
        return messageStatus.getOrDefault(messageId, "UNKNOWN");
    }

    @Override
    public void setGestorAlmacenamientoEndpoint(String host) throws RemoteException {
        System.out.println("Gestor de almacenamiento configurado: " + host);
    }
}
