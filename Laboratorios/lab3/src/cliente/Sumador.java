public interface Sumador extends java.rmi.Remote {

  public int sumar(int a, int b) throws java.rmi.RemoteException;
  
  public int restar(int a, int b) throws java.rmi.RemoteException;

}
