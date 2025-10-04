import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorGC {
    public static void main(String[] args) {
        try {
            BibliotecaGCImpl impl = new BibliotecaGCImpl();

            try {
                LocateRegistry.createRegistry(3000);
                System.out.println("RMI registry creado en 3000");
            } catch (Exception ex) {
                System.out.println("RMI registry posiblemente ya existía: " + ex.getMessage());
            }

            Registry registry = LocateRegistry.getRegistry(3000);
            registry.rebind("BibliotecaGCService", impl);
            System.out.println("BibliotecaGCService listo.");

            System.out.println("ServidorGC corriendo. Pulse Ctrl+C para terminar.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
