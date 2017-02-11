/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;

import javafx.beans.property.StringProperty;

/**
 *
 * @author alexd
 */
public abstract class IMusique extends NoeudMusique {
    public abstract String getAuteur();
    public abstract String getPath(); 
    public abstract String getDuree(); 
    public abstract void setPath(String path);
    public abstract void setAuteur(String auteur); 
    public abstract void setDuree(String duree);
    public abstract StringProperty auteurProperty();
    public abstract StringProperty dureeProperty();
}
