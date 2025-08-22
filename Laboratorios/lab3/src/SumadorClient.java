import java.rmi.*;

public class SumadorClient {

  public static void main(String args[]) {

    int res = 0;

    try {

      System.out.println("Buscando Objeto ");

      Sumador misuma = (Sumador)Naming.lookup("rmi://" + args[0] + "/" +"MiSumador");
      res = misuma.sumar(5, 2);

      System.out.println("5 + 2 = " + res);

    } catch(Exception e) {
        System.err.println(" System exception");
    }

    System.exit(0);

  }

}