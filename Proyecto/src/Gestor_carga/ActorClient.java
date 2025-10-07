/**************************************************************************************
* Fecha: 10/10/2025
* Autor: Gabriel Jaramillo, Roberth Méndez, Mariana Osorio Vasquez, Juan Esteban Vera
* Tema: 
* - Proyecto préstamo de libros (Sistema Distribuido)
* Descripción:
* - Clase Cliente/Actor (ActorClient):
* - Programa que simula un Actor especializado (Renovación o Devolución) en una sede.
* - Consume mensajes del Gestor de Carga (GC) mediante polling RMI (`fetchNextMessage`).
* - Contiene la lógica para conectarse al Gestor de Almacenamiento (GA), intentando 
* primero la réplica Primary y luego la Replica/Follower para asegurar 
* tolerancia a fallos.
* - Aplica la operación en la Base de Datos a través del GA y envía una 
* confirmación (ACK/NACK) al GC.
***************************************************************************************/

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ActorClient {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java ActorClient <hostGC> <hostGA> <topic>");
            System.exit(1);
        }

        String hostGC = args[0];
        String hostGA = args[1];
        String topic = args[2];

        try {
            // Conexión con el Gestor de Carga
            Registry regGc = LocateRegistry.getRegistry(hostGC, 3000);
            BibliotecaGC stubGc = (BibliotecaGC) regGc.lookup("BibliotecaGCService");

            // Conexión con el Gestor de Almacenamiento (intenta primero el primario)
            GestorAlmacenamiento stubGa = null;
            try {
                Registry regGa = LocateRegistry.getRegistry(hostGA, 1099);
                stubGa = (GestorAlmacenamiento) regGa.lookup("GestorAlmacenamientoPrimary");
                System.out.println("Actor conectado al Gestor de Almacenamiento PRIMARIO en " + hostGA);
            } catch (Exception e) {
                System.out.println("No se pudo conectar al GA primario, intentando con el secundario...");
                Registry regGaSec = LocateRegistry.getRegistry(hostGA, 1099);
                stubGa = (GestorAlmacenamiento) regGaSec.lookup("GestorAlmacenamientoReplica");
                System.out.println("Actor conectado al Gestor de Almacenamiento SECUNDARIO en " + hostGA);
            }

            System.out.println("ActorClient escuchando topic=" + topic);

            while (true) {
                // Pedir un nuevo mensaje al GC
                Message m = stubGc.fetchNextMessage(topic);
                if (m != null) {
                    System.out.println(java.time.LocalDateTime.now() + " - Actor recibió: " + m);

                    boolean ok = false;
                    if ("Devolucion".equalsIgnoreCase(m.getTopic())) {
                        ok = stubGa.aplicarDevolucionEnBD(m.getCodigoLibro(), m.getUsuarioId());
                    } else if ("Renovacion".equalsIgnoreCase(m.getTopic())) {
                        ok = stubGa.aplicarRenovacionEnBD(m.getCodigoLibro(), m.getUsuarioId(), m.getNuevaFechaEntrega());
                    }

                    // Enviar confirmación al GC
                    if (ok) {
                        stubGc.ackMessage(m.getId(), true);
                        System.out.println("Operacion procesada correctamente, ACK positivo enviado al GC");
                    } else {
                        stubGc.ackMessage(m.getId(), false);
                        System.out.println("Operacion fallo, ACK negativo enviado al GC");
                    }
                }

                // Espera un segundo antes de volver a preguntar
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
