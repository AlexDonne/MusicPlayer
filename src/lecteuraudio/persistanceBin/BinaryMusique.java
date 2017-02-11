/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.persistanceBin;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import javafx.beans.property.StringProperty;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.Musique;

/**
 *
 * @author alexd
 */
public class BinaryMusique extends IMusique implements  Externalizable{
    private IMusique musique= new Musique(); 
    
    public BinaryMusique(){}
    
    public BinaryMusique (IMusique model){ 
        musique=model; 
    }  
    
    @Override
    public String getAuteur() {
        return musique.getAuteur(); 
    }

    @Override
    public String getPath() {
        return musique.getPath(); 
    }

    @Override
    public void setPath(String path) {
        musique.setPath(path);
    }

    @Override
    public void setAuteur(String auteur) {
        musique.setAuteur(auteur); 
    }
    
    @Override 
    public void setTitre(String titre){ 
        musique.setTitre(titre);
    }

    @Override
    public String getTitre() {
        return musique.getTitre(); 
    }
    
    @Override
    public String getDuree() {
        return musique.getDuree(); 
    }

    @Override
    public void setDuree(String duree) {
        musique.setDuree(duree);
    }
    
    @Override
    public StringProperty titreProperty(){
        return musique.titreProperty();
    }
    
    @Override
    public StringProperty auteurProperty(){
        return musique.auteurProperty(); 
    }

    @Override
    public StringProperty dureeProperty() {
        return musique.dureeProperty();
    }
    
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getTitre());
        out.writeObject(getPath());
        out.writeObject(getAuteur());
        out.writeObject(getDuree());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setTitre((String)in.readObject());
        setPath((String)in.readObject());
        setAuteur((String)in.readObject());
        setDuree((String)in.readObject());
    }

    
    
}
