/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author alexd
 */
public abstract class IPlayList extends NoeudMusique {
    public abstract List<IPlayList> getListPlayList();
    public abstract boolean ajouter(NoeudMusique nm);
    public abstract ObservableList<NoeudMusique> getPlayList();
    public abstract boolean isEmpty(); 
    public abstract void supprimer(NoeudMusique m);     
    public abstract ListProperty<NoeudMusique> playlistProperty();
    public abstract List<IMusique> getListMusique(); 

    
}
