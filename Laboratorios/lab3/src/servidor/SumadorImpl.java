import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class SumadorImpl extends UnicastRemoteObject implements Sumador {

  public SumadorImpl() throws RemoteException {
    super();
  }

  @Override
  public int sumar (int a, int b) throws RemoteException{
    return a + b; 
  }

  @Override
  public int restar (int a, int b) throws RemoteException{ 
    return a - b; 
  }

}
