import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorGA {
    public static void main(String[] args) {
        String role = (args.length > 0) ? args[0] : "primary";
        String archivoBD = "libros.txt";

        try {
            BaseDatos bd = new BaseDatos();

            // cargar datos desde archivo
            try {
                bd.cargarDesdeArchivo(archivoBD);
            } catch (Exception e) {
                System.out.println("No se pudo cargar " + archivoBD + ", iniciando BD vacía.");
            }

            GestorAlmacenamientoImpl impl = new GestorAlmacenamientoImpl(bd, role);

            try {
                LocateRegistry.createRegistry(1099);
                System.out.println("RMI registry creado en 1099");
            } catch (Exception ex) {
                System.out.println("RMI registry posiblemente ya existía: " + ex.getMessage());
            }

            Registry registry = LocateRegistry.getRegistry(1099);
            if ("primary".equalsIgnoreCase(role)) {
                registry.rebind("GestorAlmacenamientoPrimary", impl);
                System.out.println("GestorAlmacenamientoPrimary listo.");
            } else {
                registry.rebind("GestorAlmacenamientoReplica", impl);
                System.out.println("GestorAlmacenamientoReplica listo.");
            }

            // hook para guardar al cerrar
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    bd.guardarEnArchivo(archivoBD);
                    System.out.println("BD guardada en " + archivoBD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            System.out.println("ServidorGA corriendo. role=" + role);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
