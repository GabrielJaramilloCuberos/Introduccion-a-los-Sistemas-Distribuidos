// Permite ejecutar métodos de objetos ubicados en otra máquina con Remote Method Invocation
import java.rmi.*;

public class SumadorClient {

  // Punto de entrada del programa.
  //Los argumentos que se pasen por consola estarán en el arreglo 'args'. Se espera que el primer argumento sea la dirección del servidor RMI.
  public static void main(String args[]) {

    int res = 0; // Variable local donde se guardará el resultado de la operación remota.

    try {

      System.out.println("Buscando Objeto "); // Mensaje para saber que el cliente está buscando el objeto remoto.

      // Se hace un casting a la interfaz "Sumador" para poder usar sus métodos.
      // Se utiliza la clase Naming para localizar el objeto remoto.
      // "rmi://" indica que se usará RMI.
      Sumador misuma = (Sumador)Naming.lookup("rmi://" + args[0] + "/" +"MiSumador");

      res = misuma.sumar(5, 2); // Aquí se suman los números 5 y 2 en el servidor, y el resultado se devuelve al cliente.

      System.out.println("5 + 2 = " + res); //Aqui se imprime el resultado de la operacion obtenido desde el servidor

    } catch(Exception e) {
      // Si ocurre cualquier excepción se captura aquí.
        System.err.println(" System exception");
    }
    //finaliza la ejecucion del programa
    System.exit(0);

  }

}