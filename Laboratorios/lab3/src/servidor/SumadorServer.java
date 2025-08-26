import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class SumadorServer {

    public static void main (String args[]) {

        try {
            // Crear instancia del objeto remoto
            SumadorImpl obj = new SumadorImpl();

            LocateRegistry.createRegistry(1099);

            Naming.rebind("rmi://localhost/MiSumador", obj);

            System.out.println("Servidor RMI listo con Naming." );

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}