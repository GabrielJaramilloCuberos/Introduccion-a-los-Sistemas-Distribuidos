import java.rmi.*;
import java.rmi.server.*;

public class SumadorServer {

    public static void main (String args[]) {

        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());

        try {
            SumadorImpl misuma = new SumadorImpl("MiSumador");
        } catch(Exception excr) {
            System.out.println("Excepcion: " + excr);
        }

    }

}