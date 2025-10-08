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
        String topic = args[2].toUpperCase();

        // Validar el tipo de mensaje permitido
        if (!topic.equals("DEVOLUCION") && !topic.equals("RENOVACION") && !topic.equals("PRESTAMO")) {
            System.out.println("Error: tipo de operación no reconocido. Solo se permiten DEVOLUCION, RENOVACION o PRESTAMO.");
            System.exit(1);
        }

        try {
            Registry regGc = LocateRegistry.getRegistry(hostGC, 3000);
            BibliotecaGC stubGc = (BibliotecaGC) regGc.lookup("BibliotecaGCService");

            // Intentar conectar con GA primaria y si falla, usar la réplica
            GestorAlmacenamiento stubGa = null;
            try {
                Registry regGa = LocateRegistry.getRegistry(hostGA, 1099);
                stubGa = (GestorAlmacenamiento) regGa.lookup("GestorAlmacenamientoPrimary");
                System.out.println("Actor conectado con GestorAlmacenamientoPrimary");
            } catch (Exception ex) {
                Registry regGaReplica = LocateRegistry.getRegistry(hostGA, 1099);
                stubGa = (GestorAlmacenamiento) regGaReplica.lookup("GestorAlmacenamientoReplica");
                System.out.println("Actor conectado con GestorAlmacenamientoReplica");
            }

            System.out.println("ActorClient escuchando el tema " + topic);

            while (true) {
                Message m = stubGc.fetchNextMessage(topic);
                if (m != null) {
                    System.out.println(java.time.LocalDateTime.now() + " - Actor recibió: " + m);
                    boolean ok = false;

                    if (topic.equals("DEVOLUCION")) {
                        ok = stubGa.aplicarDevolucionEnBD(m.getCodigoLibro(), m.getUsuarioId());
                    } else if (topic.equals("RENOVACION")) {
                        ok = stubGa.aplicarRenovacionEnBD(m.getCodigoLibro(), m.getUsuarioId(), m.getNuevaFechaEntrega());
                    } else if (topic.equals("PRESTAMO")) {
                        ok = stubGa.aplicarPrestamoEnBD(m.getCodigoLibro(), m.getUsuarioId());
                    }

                    if (ok) {
                        stubGc.ackMessage(m.getId(), true);
                        System.out.println("Actor: operación procesada correctamente, ACK enviado al GC");
                    } else {
                        stubGc.ackMessage(m.getId(), false);
                        System.out.println("Actor: operación falló, ACK de error enviado al GC");
                    }
                }

                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
