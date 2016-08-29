
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marz
 */
public class PeerConnection extends Node{

    private final DatagramSocket socket;

    private final OutgoingListener outgoing;
    private final IncomingListener incoming;
    private PeerNotifier notifier = null;
    private ObjReceiver objReceiver;
    private ObjSender objSender;
    
    PeerConnection(int id, int port) throws SocketException {
        super(false);
        System.out.println("Your ID: "+getID()+" Your Port: "+getPort());
        this.setInitiatorID(id);
        this.setInitiatorPort(port);
        this.socket = new DatagramSocket(this.getPort());
        
      
        this.incoming = new IncomingListener(this, socket);
        this.outgoing = new OutgoingListener(this, socket);
    }
    
    PeerConnection() throws SocketException{
        super(true);
        this.setInitiatorID(getID());
        this.setInitiatorPort(getPort());
        this.notifier = new PeerNotifier(this);
        this.socket = new DatagramSocket(this.getPort());
        
        this.incoming = new IncomingListener(this, socket);
        this.outgoing = new OutgoingListener(this, socket);
    }
    

    void startConnection(boolean isInitiator) {
        incoming.start();
        outgoing.start();
        if(isInitiator){
            notifier.start();
        }
    }
    
    OutgoingListener getOutgoing(){
        return this.outgoing;
    }
    
    ObjSender getObjSender(){
        return this.objSender;
    }

    void openObjReceiver(String type, int sID, int sPORT, int objPORT) {
        objReceiver = new ObjReceiver(type, this, sID,sPORT,objPORT);
        objReceiver.startReceiving();
    }

    
    /*void openObjSender(String type,int rID, int rPORT, int fID) {
        FileObj file = null;
        if(type.equalsIgnoreCase(Messages.RETRIEVE)){
            file = this.getFilesInNetwork().copyFile(fID);
            objSender = new ObjSender(Messages.RETRIEVE,this, rID, rPORT, file);
        }
        else if(type.equalsIgnoreCase(Messages.PUBLISH)){
            file = this.getFilesToPublish().getFile(fID);
            objSender = new ObjSender(Messages.PUBLISH,this, rID, rPORT, file);
        }
        
        objSender.startSending();
    }*/

//    private FileObj getFile(int fileID) {
//        FileObj file = null;
//        /*for(FileObj f : this.getFilesToPublish()){
//            if(f.getID() == fileID){
//                file = f;
//                break;
//            }
//        }*/
//        Iterator<FileObj> it = this.getFilesToPublish().iterator();
//        while(it.hasNext()){
//            file = it.next();
//            if(file.getID() == fileID){
//               it.remove();
//               break;
//            }
//        }
//        return file;
//    }        
//    
//    
//    void addToNetworkFiles(FileObj fileReceived){
//        int index=0;
//                
//        for(FileObj file: getFilesInNetwork()){
//            if(fileReceived.getID()<=file.getID()){
//                  break;
//            }
//            index++;
//        }
//        getFilesInNetwork().add(index, fileReceived);
//        
//        Connections.getConnection().getMulticastConnection().getOutgoing().send(Messages.PUBLISH
//                +Messages.REGEX+getID()
//                +Messages.REGEX+getPort()
//                +Messages.REGEX+getInitiatorID()
//                +Messages.REGEX+getInitiatorPort()
//                +Messages.REGEX+fileReceived.getName()
//                +Messages.REGEX+fileReceived.getID());
//    }
//
//    void addToNetworkFiles(int fileID) {
//        FileObj file = getFile(fileID);
//        addToNetworkFiles(file);
//    }
//
//    FileObj deleteToNetworkFiles(int fileID) {
//        FileObj file;
//        Iterator<FileObj> it = getFilesInNetwork().iterator();
//        while(it.hasNext()){
//            file = it.next();
//            if(file.getID()==fileID){
//                it.remove();
//                return file;
//            }
//        }
//        return null;
//    }

    
    
        
    
    
    
    
}
