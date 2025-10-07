/**************************************************************************************
* Fecha: 10/10/2025
* Autor: Gabriel Jaramillo, Roberth Méndez, Mariana Osorio Vasquez, Juan Esteban Vera
* Tema: 
* - Proyecto préstamo de libros (Sistema Distribuido)
* Descripción:
* - Clase Implementación de Servicio Remoto (BibliotecaGCImpl):
* - Es el Gestor de Carga (GC), punto de entrada para las solicitudes del cliente.
* - Implementa la mensajería asíncrona usando colecciones concurrentes (`ConcurrentLinkedQueue`) 
* para simular un Broker de Mensajes interno (colas por tópico: Devolución/Renovación).
* - Proporciona métodos para recibir solicitudes (`*Async`), devolver una respuesta 
* inmediata (implícita en el retorno del ID) y guardar el estado.
* - Maneja el **seguimiento del estado** de cada mensaje (`mapaMensajes`, `estadoMensajes`) 
* para permitir el polling por parte de los clientes.
***************************************************************************************/
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

// Implementación del Gestor de Carga (GC)
public class BibliotecaGCImpl extends UnicastRemoteObject implements BibliotecaGC {

    private static final long serialVersionUID = 1L;

    // Cola de mensajes de devoluciones
    private final ConcurrentLinkedQueue<Message> colaDevolucion = new ConcurrentLinkedQueue<>();
    // Cola de mensajes de renovaciones
    private final ConcurrentLinkedQueue<Message> colaRenovacion = new ConcurrentLinkedQueue<>();

    // Guardamos todos los mensajes por su id
    private final Map<String, Message> mapaMensajes = new ConcurrentHashMap<>();

    // Guardamos el estado de cada mensaje: PENDIENTE, OK, FALLIDO
    private final Map<String, String> estadoMensajes = new ConcurrentHashMap<>();

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ISO_LOCAL_DATE;
    private String hostGestorAlmacenamiento = null;

    // Constructor
    protected BibliotecaGCImpl() throws RemoteException {
        super();
    }

    // Publicar una devolución en la cola
    @Override
    public String devolverLibroAsync(String codigoLibro, String usuarioId) throws RemoteException {
        String id = UUID.randomUUID().toString();
        String fecha = LocalDate.now().format(formatoFecha);
        Message m = new Message(id, "Devolucion", codigoLibro, usuarioId, fecha, null);

        colaDevolucion.add(m);
        mapaMensajes.put(id, m);
        estadoMensajes.put(id, "PENDIENTE");

        System.out.println("GC: mensaje publicado en cola Devolucion: " + m);

        // Devolvemos el id del mensaje
        return id;
    }

    // Publicar una renovación en la cola
    @Override
    public String renovarLibroAsync(String codigoLibro, String usuarioId) throws RemoteException {
        String id = UUID.randomUUID().toString();
        String fecha = LocalDate.now().format(formatoFecha);
        String nuevaFecha = LocalDate.now().plusWeeks(1).format(formatoFecha);

        Message m = new Message(id, "Renovacion", codigoLibro, usuarioId, fecha, nuevaFecha);

        colaRenovacion.add(m);
        mapaMensajes.put(id, m);
        estadoMensajes.put(id, "PENDIENTE");

        System.out.println("GC: mensaje publicado en cola Renovacion: " + m);

        // Devolvemos el id del mensaje
        return id;
    }

    // Entregar el siguiente mensaje de la cola que corresponda
    @Override
    public Message fetchNextMessage(String tema) throws RemoteException {
        Message m = null;
        if ("Devolucion".equalsIgnoreCase(tema)) {
            m = colaDevolucion.poll();
        } else if ("Renovacion".equalsIgnoreCase(tema)) {
            m = colaRenovacion.poll();
        }

        if (m != null) {
            System.out.println("GC: fetchNextMessage(" + tema + ") -> " + m);
            return m;
        } else {
            return null;
        }
    }

    // Confirmar la recepción de un mensaje y marcar su estado
    @Override
    public void ackMessage(String idMensaje, boolean exito) throws RemoteException {
        if (exito) {
            estadoMensajes.put(idMensaje, "OK");
        } else {
            estadoMensajes.put(idMensaje, "FALLIDO");
        }
        System.out.println("GC: ackMessage: " + idMensaje + " => " + estadoMensajes.get(idMensaje));
    }

    // Consultar el estado de un mensaje por id
    @Override
    public String getMessageStatus(String idMensaje) throws RemoteException {
        return estadoMensajes.getOrDefault(idMensaje, "DESCONOCIDO");
    }

    // Configurar el host del Gestor de Almacenamiento
    @Override
    public void setGestorAlmacenamientoEndpoint(String host) throws RemoteException {
        this.hostGestorAlmacenamiento = host;
        System.out.println("GC: host del Gestor de Almacenamiento establecido en " + host);
    }
}
