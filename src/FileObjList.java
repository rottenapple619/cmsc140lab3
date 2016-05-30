
import java.io.Serializable;
import java.util.ArrayList;
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
public class FileObjList extends ArrayList<FileObj> implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    FileObjList(){
        super();
    }
    
    /*
    void addToList(int fileID) {
        
        FileObj file = getFile(fileID);
        addToList(file);
        
    }*/
    
    void addToList(FileObj fileReceived){
        int index=0;

        for(FileObj file : this){
            if(fileReceived.getID()<=file.getID())
                break;
            index++;
        }
        
        this.add(index, fileReceived);
        
        //announce here
    }

    FileObj removeFromList(int fileID) {
        FileObj file;
        Iterator<FileObj> it = this.iterator();
        while(it.hasNext()){
            file = it.next();
            if(file.getID()==fileID){
                it.remove();
                return file;
            }
        }
        return null;
    }
    
    FileObj getFile(int fileID) {//to publish file
        
        FileObj file = null;
        Iterator<FileObj> it = this.iterator();
        
        while(it.hasNext()){
            file = it.next();
            if(file.getID() == fileID){
               it.remove();
               break;
            }
        }
        
        return file;
        
    }        
}
