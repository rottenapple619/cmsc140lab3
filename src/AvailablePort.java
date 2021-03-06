
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marz
 */
public class AvailablePort {
    
    private static final int MIN_PORT = 1025;
    private static final int MAX_PORT = 49151;
    
    /**
     * Gets an available port
     * @return Integer
     */
    public static int getAvailablePort() {
        int port;
        while(true){
            port = ((int) (Math.random()*(MAX_PORT-MIN_PORT))) + MIN_PORT;
            if(isAvailable(port))
                break;
           
   
        }
        return port;
    }
    
    //from http://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
    private static boolean isAvailable(int port){

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }       

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                /* should not be thrown */
                }
            }
        }
        return false;
    }
}
