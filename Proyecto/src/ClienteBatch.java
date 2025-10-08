import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class ClienteBatch {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ClienteBatch <archivo_peticiones> <host_gc>");
            System.exit(1);
        }

        String archivo = args[0];
        String host = args[1];

        try {
            Registry registry = LocateRegistry.getRegistry(host, 3000);
            BibliotecaGC stub = (BibliotecaGC) registry.lookup("BibliotecaGCService");

            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] parts = linea.split("\\|");
                if (parts.length < 3) continue;

                String tipo = parts[0].trim();
                String codigo = parts[1].trim();
                String usuario = parts[2].trim();

                String messageId = null;
                if ("DEVOLUCION".equalsIgnoreCase(tipo)) {
                    messageId = stub.devolverLibroAsync(codigo, usuario);
                    System.out.println(LocalDate.now() + " -> DEVOLUCION enviada (" + codigo + ")");
                } else if ("RENOVACION".equalsIgnoreCase(tipo)) {
                    messageId = stub.renovarLibroAsync(codigo, usuario);
                    System.out.println(LocalDate.now() + " -> RENOVACION enviada (" + codigo + ")");
                } else if ("PRESTAMO".equalsIgnoreCase(tipo)) {
                    messageId = stub.prestarLibroAsync(codigo, usuario);
                    System.out.println(LocalDate.now() + " -> PRESTAMO enviado (" + codigo + ")");
                }

                if (messageId != null) {
                    String status = "PENDING";
                    int waited = 0;
                    while (waited < 10000 && "PENDING".equals(status)) {
                        TimeUnit.MILLISECONDS.sleep(250);
                        status = stub.getMessageStatus(messageId);
                        waited += 250;
                    }

                    if ("OK".equalsIgnoreCase(status))
                        System.out.println("Operación " + tipo + " sobre " + codigo + " procesada con éxito.");
                    else if ("FAILED".equalsIgnoreCase(status))
                        System.out.println("Error: operación " + tipo + " sobre " + codigo + " falló.");
                    else
                        System.out.println("Sin confirmación final para " + tipo + " (" + codigo + ")");
                }

                TimeUnit.MILLISECONDS.sleep(200);
            }
            br.close();
            System.out.println("ClienteBatch finalizado.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
