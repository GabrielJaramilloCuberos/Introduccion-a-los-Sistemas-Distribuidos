// Permite ejecutar métodos de objetos ubicados en otra máquina con Remote Method Invocation
import java.rmi.*;

//convierte una clase en un objeto remoto accesible por clientes RMI.
import java.rmi.server.UnicastRemoteObject;

public class SumadorImpl extends UnicastRemoteObject implements Sumador {

  // Constructor de la clase.
  // "throws RemoteException" es obligatorio en todos los métodos remotos
  // por cualquier fallo de red o de comunicación entre cliente y servidor
  public SumadorImpl() throws RemoteException {
    super(); // llama al constructor de UnicastRemoteObject, que se encarga de registrar el objeto para que pueda ser invocado remotamente.
  }

  // Recibe dos enteros y devuelve la suma de ambos.
  @Override
  public int sumar (int a, int b) throws RemoteException{
    return a + b; 
  }

  // Recibe dos enteros y devuelve la resta de ambos.
  // También puede lanzar RemoteException porque se invoca remotamente.
  @Override
  public int restar (int a, int b) throws RemoteException{ 
    return a - b; 
  }

}
