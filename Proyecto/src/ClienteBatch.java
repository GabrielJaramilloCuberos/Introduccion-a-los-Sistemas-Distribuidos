/**************************************************************************************
* Fecha: 10/10/2025
* Autor: Gabriel Jaramillo, Roberth Méndez, Mariana Osorio Vasquez, Juan Esteban Vera
* Tema: 
* - Proyecto préstamo de libros (Sistema Distribuido)
* Descripción:
* - Clase Cliente (ClienteBatch):
* - Simula el Proceso Solicitante (PS), cargando y enviando múltiples peticiones 
* de Devolución o Renovación desde un archivo de texto.
* - Utiliza RMI para la comunicación con el Gestor de Carga (GC).
* - Implementa el patrón asíncrono: envía la solicitud (`*Async`), obtiene un 
* ID de mensaje, y luego realiza polling (`getMessageStatus`) durante 10 segundos
* para verificar el estado final de la operación.
***************************************************************************************/
import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/*
Formato del archivo de entrada (cada línea):
TIPO|codigoLibro|usuarioId
TIPO puede ser DEVOLUCION o RENOVACION
*/

public class ClienteBatch {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ClienteBatch <archivo_peticiones> <host_gc>");
            System.exit(1);
        }

        String archivo = args[0];
        String host = args[1];

        try {
            // Nos conectamos al Gestor de Carga en el puerto 3000
            Registry registry = LocateRegistry.getRegistry(host, 3000);
            BibliotecaGC stub = (BibliotecaGC) registry.lookup("BibliotecaGCService");

            // Leemos el archivo de peticiones
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                String[] partes = linea.split("\\|");
                if (partes.length < 3) continue;

                String tipo = partes[0].trim();
                String codigo = partes[1].trim();
                String usuario = partes[2].trim();

                String idMensaje = null;

                // Dependiendo del tipo, enviamos la petición al GC
                if ("DEVOLUCION".equalsIgnoreCase(tipo)) {
                    idMensaje = stub.devolverLibroAsync(codigo, usuario);
                    System.out.println(LocalDate.now() + " -> Devolución enviada para libro " + codigo + " => idMensaje=" + idMensaje);
                } else if ("RENOVACION".equalsIgnoreCase(tipo)) {
                    idMensaje = stub.renovarLibroAsync(codigo, usuario);
                    System.out.println(LocalDate.now() + " -> Renovación enviada para libro " + codigo + " => idMensaje=" + idMensaje);
                } else {
                    System.out.println("Tipo desconocido en archivo: " + tipo);
                    continue;
                }

                // Revisamos el resultado hasta 10 segundos
                if (idMensaje != null) {
                    String estado = "PENDIENTE";
                    int tiempoEsperado = 0;

                    while (tiempoEsperado < 10000 && "PENDIENTE".equals(estado)) {
                        TimeUnit.MILLISECONDS.sleep(250);
                        estado = stub.getMessageStatus(idMensaje);
                        tiempoEsperado += 250;
                    }

                    if ("OK".equalsIgnoreCase(estado)) {
                        System.out.println("Operacion " + tipo + " sobre libro " + codigo + " procesada correctamente.");
                    } else if ("FAILED".equalsIgnoreCase(estado)) {
                        System.out.println("Error: Operacion " + tipo + " sobre libro " + codigo + " no se pudo procesar.");
                    } else {
                        System.out.println("Advertencia: Operacion " + tipo + " sobre libro " + codigo + " sin confirmacion (estado=" + estado + ")");
                    }
                }

                // Pausa corta entre operaciones
                TimeUnit.MILLISECONDS.sleep(200);
            }
            br.close();

            System.out.println("ClienteBatch terminado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
