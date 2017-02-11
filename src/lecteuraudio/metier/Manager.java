/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;

import java.io.File;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Window;
import lecteuraudio.persistanceBin.BinaryPlayList;

/**
 *
 * @author alexd
 */
public class Manager {
    
    private GestionnaireImport gesImp; 
    private IDataManager datamanager;   
    
    private ObjectProperty noeudCourant = new SimpleObjectProperty();
    public ObjectProperty noeudCourantProperty() { return noeudCourant;}
    public NoeudMusique getNoeudCourant() { return (NoeudMusique) noeudCourant.get();}
    public void setNoeudCourant(NoeudMusique noeud) { this.noeudCourant.set(noeud);}
    

    private ObjectProperty<IPlayList> racine=new SimpleObjectProperty<>(new PlayList("Racine"));;
    public ObjectProperty<IPlayList> racineProperty (){  return racine;   }
    public IPlayList getRacine (){return racine.get(); }
        
    
    public Manager (IDataManager datamanager){ 
        setDataManager(datamanager); 
        gesImp= new GestionnaireImport(); 
        
    }
    
    private void setDataManager (IDataManager m){ 
        datamanager=m; 
    }
    
    public void charger(){ 
        gesImp.ouverture();
        //gesImp.importerRepertoireMusiques(getRacine());  
        datamanager.charger(getRacine());
    }
    
    public void sauver(){  
        datamanager.sauver(getRacine());
    }
    
    private void suppressionDansAutresPlayLists(List<IMusique> m, IPlayList courante){ 
        for (IPlayList p : courante.getListPlayList()){
            p.getPlayList().removeAll(m); 
            suppressionDansAutresPlayLists(m,p); 
        }
    }
    
    public void suppression(List<IMusique> m){
        //On supprime la/les musique dans toutes les playlists 
        suppressionDansAutresPlayLists(m,getRacine()); 
        //On supprime la/les musiques dans le dossier Musiques de l'appli
        gesImp.suppression(m);
    }
    
    
    public boolean ajouter(NoeudMusique nm){ 
        return getRacine().ajouter(nm); 
    }
    
    /*
    *chercherDisqueDur :
    return true si une musique est ajouté, false si rien n'est ajouté
    */
    public boolean chercherDisqueDur(Window window){
        return gesImp.chercherDisqueDur(getRacine(), window);
    }
    
    public boolean chercherDisqueDurRep(Window window){
        return gesImp.chercherDisqueDurRep(getRacine(), window);
    }
    
    public boolean copierDansRepository(File f){
        try{
            gesImp.copierDansRepository(f);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public boolean ajouterMusique(File f) {
        return gesImp.ajouterMusique(f, getRacine());
    }
    
}
