import java.io.Serializable;

public class Ejemplar implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private char estado; // 'D' = Disponible, 'P' = Prestado
    private String fecha; // fecha de devolución si está prestado

    public Ejemplar(int id, char estado, String fecha) {
        this.id = id;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public char getEstado() { return estado; }
    public String getFecha() { return fecha; }

    public void setEstado(char estado) { this.estado = estado; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
