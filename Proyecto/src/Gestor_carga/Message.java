import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String topic;
    private final String codigoLibro;
    private final String usuarioId;
    private final String fechaOperacion;
    private final String nuevaFechaEntrega;

    public Message(String id, String topic, String codigoLibro, String usuarioId, String fechaOperacion, String nuevaFechaEntrega) {
        this.id = id;
        this.topic = topic;
        this.codigoLibro = codigoLibro;
        this.usuarioId = usuarioId;
        this.fechaOperacion = fechaOperacion;
        this.nuevaFechaEntrega = nuevaFechaEntrega;
    }

    public String getId() { return id; }
    public String getTopic() { return topic; }
    public String getCodigoLibro() { return codigoLibro; }
    public String getUsuarioId() { return usuarioId; }
    public String getFechaOperacion() { return fechaOperacion; }
    public String getNuevaFechaEntrega() { return nuevaFechaEntrega; }

    public String toString() {
        return String.format("Message[id=%s,topic=%s,codigo=%s,user=%s,fecha=%s,nueva=%s]",
                id, topic, codigoLibro, usuarioId, fechaOperacion, nuevaFechaEntrega);
    }
}
