
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marz
 */
public class PeerProtocol implements Messages{

    static void check(String incomingMessage) throws UnknownHostException {
        String msg[];
        msg = incomingMessage.split(REGEX);
        
        
        
        /*
            Received a TELLSUCCESSOR MESSAGE
            RECEIVER is the SUCCESSOR of the SENDER of this MESSAGE
            RECEIVER then sends a TELLPREDECESSOR MESSAGE to the SENDER
        */
        if(msg[0].equalsIgnoreCase(TELLSUCCESSOR)){
            
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int predecessorID = Integer.parseInt(msg[3]);
            int predecessorPORT = Integer.parseInt(msg[4]);
            
            int successorID = Integer.parseInt(msg[5]);
            int successorPORT = Integer.parseInt(msg[6]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            peer.setPID(predecessorID);
            peer.setPredecessorPort(predecessorPORT);
            peer.setSID(successorID);
            peer.setSuccessorPort(successorPORT);
            
            peer.getOutgoing().send(TELLPREDECESSOR
                    +REGEX+peer.getInitiatorID()        //network ID
                    +REGEX+peer.getInitiatorPort()
                    +REGEX+peer.getID()                 //receiver's predecessor
                    +REGEX+peer.getPort(),
                    InetAddress.getLocalHost(), successorPORT);
            printStatus(peer);
        }
        
        /*
            Received a TELLPREDECESSOR MESSAGE
            RECEIVER is the PREDECESSOR of the SENDER of this MESSAGE
        */
        else if(msg[0].equalsIgnoreCase(TELLPREDECESSOR)){
            
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int predecessorID = Integer.parseInt(msg[3]);
            int predecessorPORT = Integer.parseInt(msg[4]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            peer.setPID(predecessorID);
            peer.setPredecessorPort(predecessorPORT);
            
            printStatus(peer);
        }
        /*
            Received a FINDSUCCESSOR MESSAGE
        */
        else if(msg[0].equalsIgnoreCase(FINDSUCCESSOR)){    //received find successor message
            
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int joinID  = Integer.parseInt(msg[3]);
            int joinPORT = Integer.parseInt(msg[4]);
            
            
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);

            if(peer.getPredecessorID() == 0){ //initiator in initial state
                peer.setPID(joinID);
                peer.setPredecessorPort(joinPORT);
                peer.setSID(joinID);
                peer.setSuccessorPort(joinPORT);
                peer.getOutgoing().send(TELLSUCCESSOR 
                    +REGEX+netID                //network id
                    +REGEX+netPORT
                    +REGEX+peer.getID()         //receiver's predecessor
                    +REGEX+peer.getPort()
                    +REGEX+peer.getID()         //receiver's successor
                    +REGEX+peer.getPort(),
                    InetAddress.getLocalHost(), joinPORT);
            }
            else{//initiator in populated state
                if((joinID > peer.getID() && joinID <= peer.getSuccessorID()) //amazing Lyle
                    || (peer.getID() > peer.getSuccessorID() && (joinID > peer.getID() || joinID < peer.getSuccessorID()))){
                    peer.getOutgoing().send(TELLSUCCESSOR 
                        +REGEX+netID                         //network id
                        +REGEX+netPORT
                        +REGEX+peer.getID()                 //receiver's predecessor
                        +REGEX+peer.getPort()
                        +REGEX+peer.getSuccessorID()        //receiver's successor
                        +REGEX+peer.getSuccessorPort(),
                        InetAddress.getLocalHost(), joinPORT);
                    peer.setSID(joinID);
                    peer.setSuccessorPort(joinPORT);
                    printStatus(peer);
                }
                else{
                    peer.getOutgoing().send(FINDSUCCESSOR   //find successor message sent to successor
                        +REGEX+netID
                        +REGEX+netPORT
                        +REGEX+joinID                   
                        +REGEX+joinPORT,
                        InetAddress.getLocalHost(), peer.getSuccessorPort());

                } 
            }

            
        }
        /*
            Received PUBLISH Message
        */
        else if(msg[0].equalsIgnoreCase(PUBLISH)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int publisherID  = Integer.parseInt(msg[3]);
            int publisherPORT = Integer.parseInt(msg[4]);
            
            int fileID = Integer.parseInt(msg[5]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            if(peer.getPredecessorID() == 0){ //P2P Network in initial state
                peer.getOutgoing().send(TELLPUBLISHER 
                    +REGEX+netID                //network id
                    +REGEX+netPORT
                    +REGEX+peer.getID()         //successor ID (the one to keep the file)
                    +REGEX+peer.getPort()
                    +REGEX+fileID,              //file ID
                    InetAddress.getLocalHost(), publisherPORT);
            }
            else{//P2P Network in populated state
                if((fileID > peer.getID() && fileID <= peer.getSuccessorID()) //amazing Lyle
                    || (peer.getID() > peer.getSuccessorID() && (fileID > peer.getID() || fileID < peer.getSuccessorID()))){
                    peer.getOutgoing().send(TELLPUBLISHER 
                        +REGEX+netID                //network id
                        +REGEX+netPORT
                        +REGEX+peer.getID()         //successor ID (the one to keep the file)
                        +REGEX+peer.getPort()
                        +REGEX+fileID,              //file ID
                        InetAddress.getLocalHost(), publisherPORT); //send to the publisher of the file

                }
                else{
                    peer.getOutgoing().send(PUBLISH
                        +REGEX+netID                //network ID
                        +REGEX+netPORT
                        +REGEX+publisherID          //publisher ID
                        +REGEX+publisherPORT
                        +REGEX+fileID,              //file ID
                        InetAddress.getLocalHost(), peer.getSuccessorPort()); //send to successor

                } 
            }
        }
        else if(msg[0].equalsIgnoreCase(TELLPUBLISHER)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int receiverID  = Integer.parseInt(msg[3]);
            int receiverPORT = Integer.parseInt(msg[4]);
            
            int fileID = Integer.parseInt(msg[5]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            if(receiverID!=peer.getID()){
                System.out.println();
                System.out.println("FILE KEEPER FOUND FOR THE P2P NETWORK: "+netID+"@"+netPORT
                        +"\nFileID: "+fileID
                        +"\nFile Keeper: "+receiverID+"@"+receiverPORT);
                System.out.println();
                peer.openObjSender("PUBLISH",receiverID,receiverPORT,fileID);
                
            }
            else{
                System.out.println();
                System.out.println("FILE KEEPER FOUND FOR THE P2P NETWORK: "+netID+"@"+netPORT
                        +"\nFileID: "+fileID
                        +"\nFile Keeper: "+peer.getID()+"@"+peer.getPort()+"(You)");
                System.out.println();
                FileObj f = peer.getFilesToPublish().getFile(fileID);
                peer.getFilesInNetwork().addToList(f);
                
                Connections.getConnection().getMulticastConnection().getOutgoing().send(Messages.PUBLISH//broadcast to multicast that a file has been
                    +Messages.REGEX+peer.getID()                                                        //published
                    +Messages.REGEX+peer.getPort()
                    +Messages.REGEX+peer.getInitiatorID()
                    +Messages.REGEX+peer.getInitiatorPort()
                    +Messages.REGEX+f.getName()
                    +Messages.REGEX+f.getID());
            }
            
        }
        else if(msg[0].equalsIgnoreCase(READYTOPUBLISH)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int senderID  = Integer.parseInt(msg[3]);
            int senderPORT = Integer.parseInt(msg[4]);
            
            int objPORT = Integer.parseInt(msg[5]);
            //int fileID = Integer.parseInt(msg[6]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            peer.openObjReceiver(senderID,senderPORT,objPORT); 
        }
        else if(msg[0].equalsIgnoreCase(DELETE)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int senderID  = Integer.parseInt(msg[3]);//the one who requested to delete the file
            int senderPORT = Integer.parseInt(msg[4]);
            
            int fileID = Integer.parseInt(msg[5]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            if((fileID > peer.getID() && fileID <= peer.getSuccessorID()) //amazing Lyle
                    || (peer.getID() > peer.getSuccessorID() && (fileID > peer.getID() || fileID < peer.getSuccessorID()))){
                FileObj file;
                if((file = peer.getFilesInNetwork().removeFromList(fileID)) != null){//successful delete
                    System.out.println();
                    System.out.println("DELETING A FILE IN THE P2P NETWORK: "+netID+"@"+netPORT
                        + "\nFileID: "+fileID
                        + "\nFilename: '"+file.getName()+"'"
                        + "\nDeleted by: "+senderID+"@"+senderPORT);
                    System.out.println();
                    if(senderID!=peer.getID()){
                        peer.getOutgoing().send(SUCCESSDELETE
                            +REGEX+netID
                            +REGEX+netPORT
                            +REGEX+peer.getID()
                            +REGEX+peer.getPort()
                            +REGEX+file.getName()
                            +REGEX+file.getID(),
                            InetAddress.getLocalHost(), senderPORT);
                    }
                }
                else{//file not found
                    peer.getOutgoing().send(FAILDELETE
                        +REGEX+netID
                        +REGEX+netPORT
                        +REGEX+peer.getID()
                        +REGEX+peer.getPort()
                        +REGEX+fileID,
                        InetAddress.getLocalHost(), senderPORT);
                }
            }
            else{
                peer.getOutgoing().send(Messages.DELETE
                    +REGEX+netID
                    +REGEX+netPORT
                    +REGEX+senderID
                    +REGEX+senderPORT
                    +REGEX+fileID,
                    InetAddress.getLocalHost(), peer.getSuccessorPort());
            }
        }
        else if(msg[0].equalsIgnoreCase(SUCCESSDELETE)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int senderID  = Integer.parseInt(msg[3]);
            int senderPORT = Integer.parseInt(msg[4]);
            
            String fileName = msg[5];
            int fileID = Integer.parseInt(msg[6]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            System.out.println();
            System.out.println("FILE SUCCESSFULLY DELETED IN THE P2P NETWORK: "+netID+"@"+netPORT
                    +"\nFileID: "+fileID
                    +"\nFilename: '"+fileName+"'"
                    +"\nMessage from: "+senderID+"@"+senderPORT);
            System.out.println();
        }
        else if(msg[0].equalsIgnoreCase(FAILDELETE)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int senderID  = Integer.parseInt(msg[3]);
            int senderPORT = Integer.parseInt(msg[4]);
            
            int fileID = Integer.parseInt(msg[5]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            System.out.println();
            System.out.println("FAILED TO DELETE A FILE IN THE P2P NETWORK: "+netID+"@"+netPORT
                    +"\nFileID: "+fileID
                    +"\nMessage from: "+senderID+"@"+senderPORT);
            System.out.println();
        }
        else if(msg[0].equalsIgnoreCase(FILESNETWORK)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int senderID  = Integer.parseInt(msg[3]);
            int senderPORT = Integer.parseInt(msg[4]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            
            if(senderID==peer.getID()){
                if(!peer.requestForFiles){
                    peer.requestForFiles = true;
                    for(FileObj file : peer.getFilesInNetwork()){
                        System.out.println("FileID: "+file.getID()
                            +" Filename: "+file.getName()
                            +" Kept by: "+peer.getID()+"@"+peer.getPort()+"(You)");
                        
                    }
                    peer.getOutgoing().send(FILESNETWORK
                        +REGEX+netID
                        +REGEX+netPORT
                        +REGEX+senderID
                        +REGEX+senderPORT, 
                        InetAddress.getLocalHost(), peer.getSuccessorPort());
                    
                }
                else{
                    peer.requestForFiles = false;
                }
            }
            else{
                if(netID==peer.getID() && peer.requestForFiles){
                    peer.requestForFiles = false;
                    peer.getOutgoing().send(FILESNETWORK
                        +REGEX+netID
                        +REGEX+netPORT
                        +REGEX+senderID
                        +REGEX+senderPORT, 
                        InetAddress.getLocalHost(), peer.getSuccessorPort());
                    return;
                }
                    
                for(FileObj file : peer.getFilesInNetwork()){
                    
                    peer.getOutgoing().send(FILE
                        +REGEX+netID
                        +REGEX+netPORT
                        +REGEX+peer.getID()
                        +REGEX+peer.getPort()
                        +REGEX+file.getID()
                        +REGEX+file.getName(), 
                        InetAddress.getLocalHost(), senderPORT);
                    
                }
                    
                peer.getOutgoing().send(FILESNETWORK
                    +REGEX+netID
                    +REGEX+netPORT
                    +REGEX+senderID
                    +REGEX+senderPORT, 
                    InetAddress.getLocalHost(), peer.getSuccessorPort());
                
                if(netID==peer.getID()){
                    peer.requestForFiles = true;
                }
                
            }
            
//            if(!peer.requestForFiles){
//                
//                for(FileObj file : peer.getFilesInNetwork()){
//                    if(senderID==peer.getID()){
//                        System.out.println("FileID: "+file.getID()
//                            +" Filename: "+file.getName()
//                            +" Kept by: "+peer.getID()+"@"+peer.getPort()+"(You)");
//                    }
//                    else{
//                        peer.getOutgoing().send(FILE
//                            +REGEX+netID
//                            +REGEX+netPORT
//                            +REGEX+peer.getID()
//                            +REGEX+peer.getPort()
//                            +REGEX+file.getID()
//                            +REGEX+file.getName(), 
//                            InetAddress.getLocalHost(), senderPORT);
//                    }
//                }
//                    
//                peer.getOutgoing().send(FILESNETWORK
//                    +REGEX+netID
//                    +REGEX+netPORT
//                    +REGEX+senderID
//                    +REGEX+senderPORT, 
//                    InetAddress.getLocalHost(), peer.getSuccessorPort());
//                
//                
//                
//                    
//            }
//            else{
//                peer.requestForFiles = false;
//            }
            
        }
        else if(msg[0].equalsIgnoreCase(FILE)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int senderID  = Integer.parseInt(msg[3]);
            int senderPORT = Integer.parseInt(msg[4]);
            
            int fileID = Integer.parseInt(msg[5]);
            String fileName = msg[6];
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            System.out.println("FileID: "+fileID
                            +" Filename: "+fileName
                            +" Kept by: "+senderID+"@"+senderPORT);
        }
        
        else if(msg[0].equalsIgnoreCase(Messages.RETRIEVE)){
            int netID = Integer.parseInt(msg[1]);
            int netPORT = Integer.parseInt(msg[2]);
            
            int requestID  = Integer.parseInt(msg[3]); //address or ID of the one who retrieves the file
            int requestPORT = Integer.parseInt(msg[4]);
            
            int fileID = Integer.parseInt(msg[5]);
            
            PeerConnection peer = Connections.getConnection().getPeerConnection(netID);
            
            if(peer.getPredecessorID() == 0){ //P2P Network in initial state
                FileObj f;
                if((f = peer.getFilesInNetwork().copyFile(fileID))==null){
                        System.err.println("File not found!");
                } 
                else {
                    System.out.println();
                    System.out.println("FILE KEEPER FOUND FOR THE P2P NETWORK: "+netID+"@"+netPORT
                            +"\nFileID: "+fileID
                            +"\nFilename : '"+f.getName()
                            +"\nFile Keeper: "+peer.getID()+"@"+peer.getPort()+"(You)");
                    System.out.println();
                    Connections.getConnection().getLocalFiles().addToList(f);
                    System.out.println("'"+f.getName()+"' has been retrieved and saved to local files.");
                }
            }
            else{//P2P Network in populated state
                if((fileID > peer.getID() && fileID <= peer.getSuccessorID()) //amazing Lyle
                    || (peer.getID() > peer.getSuccessorID() && (fileID > peer.getID() || fileID < peer.getSuccessorID()))){
                    peer.openObjSender("RETRIEVE",requestID,requestPORT,fileID);
                    /*peer.getOutgoing().send(INIT_TRANSFER 
                        +REGEX+netID                //network id
                        +REGEX+netPORT
                        +REGEX+peer.getID()         //successor ID (the one who keeps the file)
                        +REGEX+peer.getPort()
                        +REGEX+fileID,              //file ID
                        InetAddress.getLocalHost(), requestPORT); //send to the one who requests/retrieves the file
*/
                }
                else{
                    peer.getOutgoing().send(RETRIEVE
                        +REGEX+netID                //network ID
                        +REGEX+netPORT
                        +REGEX+requestID            //request ID
                        +REGEX+requestPORT
                        +REGEX+fileID,              //file ID
                        InetAddress.getLocalHost(), peer.getSuccessorPort()); //send to successor

                } 
            }
        }
       
    }

    private static void printStatus(PeerConnection peer) {
        
        System.out.println();
        System.out.println("Your successor: "+      peer.getSuccessorID()+" "
                        + "Your successor port: "+  peer.getSuccessorPort()+"\n"
                        + "Your predecessor: "+     peer.getPredecessorID()+" "
                        + "Your predecessor port: "+peer.getPredecessorPort());
        
    }

    
    
}
