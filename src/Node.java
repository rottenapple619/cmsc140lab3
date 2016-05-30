
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marz
 */
class Node{
    public boolean requestForFiles = false;
    
    private final FileObjList filesNetwork;
    private final FileObjList filesToPublish;
    
    private ObjSender objSender;
    private ObjReceiver ojbReceiver;
        
    private int successorID;
    private int successorPort;
    
    private int predecessorID;
    private int predecessorPort;

    private int initiatorID;
    private int initiatorPort;
    
    private final int port;
    
    private final boolean isServer;
    
    Node(boolean isServer){

        this.filesToPublish = new FileObjList();
        this.filesNetwork = new FileObjList();
        
        this.isServer = isServer;
        this.port = AvailablePort.getAvailablePort();
        
        this.predecessorID = 0;
        this.predecessorPort = 0;
        if(isServer){
            this.successorID = getID();
            this.successorPort = port;
            
        }
    }
    
    FileObjList getFilesToPublish(){
        return this.filesToPublish;
    }
    
    FileObjList getFilesInNetwork(){
        return this.filesNetwork;
    }
    
    int getPort(){
        return port;
    }
    
    final int getID(){
        /*if(isServer) //remove this
            return 9999;
        else*/
            return Connections.getConnection().getID();
    }
    
    int getPredecessorID(){
        return predecessorID;
    }
    
    int getPredecessorPort(){
        return predecessorPort;
    }
    
    int getSuccessorID(){
        return successorID;
    }
    
    int getSuccessorPort(){
        return successorPort;
    }

    int getInitiatorID(){
        return initiatorID;
    }
    
    int getInitiatorPort(){
        return initiatorPort;
    }
    
    boolean isServer(){
        return isServer;
    }
    void setPID(int id){
        predecessorID = id;
    }
    
    void setSID(int id){
        successorID = id;
    }
    
    void setPredecessorPort(int port){
        predecessorPort = port;
    }
    
    void setSuccessorPort(int port){
        successorPort = port;
    }
    
    void setInitiatorID(int id){
        initiatorID = id;
    }
    
    void setInitiatorPort(int port){
        initiatorPort = port;
    }
    
}
