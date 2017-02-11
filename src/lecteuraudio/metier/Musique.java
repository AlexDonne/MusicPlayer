/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lecteuraudio.metier;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 *
 * @author nahel
 */
public class Musique extends IMusique{
    private String path;
    
    private StringProperty auteurProperty=new SimpleStringProperty();
    
    @Override
    public StringProperty auteurProperty(){return auteurProperty; }
    
    @Override
    public String getAuteur() {return auteurProperty.get();}
    
    @Override
    public void setAuteur(String auteur) {this.auteurProperty.set(auteur);}
    

    private StringProperty dureeProperty=new SimpleStringProperty(); 
    
    @Override
    public StringProperty dureeProperty(){return dureeProperty; }
    
    @Override
    public String getDuree() {return dureeProperty.get();}
    
    @Override
    public void setDuree(String duree) {this.dureeProperty.set(duree);}
    
    public Musique(){
        
    }
    
    public Musique (String auteur, String titre, String path){
        this.auteurProperty.set(auteur); 
        this.titreProperty.set(titre);
        this.path=path; 
    } 
    
    public Musique (String auteur, String titre, String path, String duree){
        this.auteurProperty.set(auteur); 
        this.titreProperty.set(titre);
        this.path=path; 
        setDuree(duree);
    }  
    
    
    
    @Override
    public void setPath(String path){    
        this.path=path;        
    }
    
    @Override
    public String getPath(){
        return path;
    }
    
    
    @Override 
    public String toString (){ 
        return titreProperty.get(); 
    }

    
}
