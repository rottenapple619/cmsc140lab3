
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marz
 */
class ObjReceiver extends Thread{

    private final PeerConnection node;
    private final int senderID;
//    private final int fileID;
    private final int senderPORT;
    private final int objPORT;

    private Socket cSocket;
    private ObjectInput in;
    
    ObjReceiver(PeerConnection node, int sID, int sPORT, int objPORT) {
        this.node = node;
        this.senderID = sID;
        this.senderPORT = sPORT;
        this.objPORT = objPORT;
        //this.fileID = fileID;
    }
    
    @Override
    public void run(){

        try {
            cSocket = new Socket(InetAddress.getLocalHost(), objPORT);
            System.out.println();
            System.out.println("Connected to: "+senderID);
            System.out.println("Receiving file(s) from: "+senderID+"...");
            in = new ObjectInputStream(cSocket.getInputStream());
            FileObj f = (FileObj) in.readObject(); 
            System.out.println("Received file: "+f.getName()+" with ID: "+f.getID());
            System.out.println("Receiving file(s) from: "+senderID+" completed.");
            
            node.getFilesInNetwork().addToList(f);
            //node.addToNetworkFiles(f);
            Connections.getConnection().getMulticastConnection().getOutgoing().send(Messages.PUBLISH
                +Messages.REGEX+node.getID()
                +Messages.REGEX+node.getPort()
                +Messages.REGEX+node.getInitiatorID()
                +Messages.REGEX+node.getInitiatorPort()
                +Messages.REGEX+f.getName()
                +Messages.REGEX+f.getID());
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ObjReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {//closing all sockets and streams
            if(this.in!=null){
                try {
                    this.in.close();
                } catch (IOException ex) {
                    Logger.getLogger(ObjReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(this.cSocket!=null){
                try {
                    this.in.close();
                } catch (IOException ex) {
                    Logger.getLogger(ObjReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }

    void startReceiving() {
        this.start();
    }
    
}
