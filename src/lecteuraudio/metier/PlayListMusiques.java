/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;

/**
 *
 * @author nahel
 */
public class PlayListMusiques extends PlayList{
    
    public PlayListMusiques(){
        
    }
    
    
    @Override
    public boolean ajouter(NoeudMusique m) {

        for (NoeudMusique nm : super.getPlayList()) {
            if (nm.getTitre().equals(m.getTitre())) {
                return false;
            }
        }
        if(m instanceof IMusique){
            super.getPlayList().add(m);
        }
        return true;
    }
    
    public PlayListMusiques(IPlayList playlist){
        for(NoeudMusique nm : playlist.getPlayList()){
            this.ajouter(nm);
        }
    }
}
