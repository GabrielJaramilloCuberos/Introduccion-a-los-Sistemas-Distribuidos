/**************************************************************************************
* Fecha: 10/10/2025
* Autor: Gabriel Jaramillo, Roberth Méndez, Mariana Osorio Vasquez, Juan Esteban Vera
* Tema: 
* - Proyecto préstamo de libros (Sistema Distribuido)
* Descripción:
* - Clase Servidor Principal (ServidorGA):
* - Programa de arranque para el Gestor de Almacenamiento (GA) en una sede.
* - Inicializa la BaseDatos, cargándola desde libros.txt.
* - Expone la implementación del GA (`GestorAlmacenamientoImpl`) como un servicio RMI.
* - Determina su rol (Primary o Replica/Follower) basado en argumentos de línea de comandos 
* y registra el servicio RMI con el nombre correspondiente (GestorAlmacenamientoPrimary o GestorAlmacenamientoReplica).
* - Incluye un Shutdown Hook para asegurar la persistencia de los datos en el archivo 
* al cerrar el servidor.
***************************************************************************************/
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
