/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.persistancetexte;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import lecteuraudio.metier.IPlayList;
import lecteuraudio.metier.IDataManager;
import lecteuraudio.metier.Musique;
import lecteuraudio.metier.NoeudMusique;
import lecteuraudio.metier.PlayList;

/**
 *
 * @author alexd
 */
public class TextDataManager implements IDataManager {
    private String repositoryPlayLists=System.getProperty("user.dir")+"/Playlists"; 
    
    
    @Override
    public void charger(IPlayList racine) {
        if (!new File(repositoryPlayLists).exists()) 
            new File(repositoryPlayLists).mkdir(); 
        for (File f : new File(repositoryPlayLists).listFiles()) {
            String nom = f.getName();
            if (nom.equals("TouteslesPlayLists.txt") ) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))){
                    importerPlayLists(racine,br,racine,racine,racine);
                }
                catch (Exception e){ 
                      e.printStackTrace();
                }
            }

        }
    }

    
    private void importerPlayLists(IPlayList courante, BufferedReader br, IPlayList pere,IPlayList grandPere, IPlayList racine) {//Marche pour 3 imbrications, à revoir
        String nomNoeudMusique;
        String nomPlayList;
        int nbcourant,nbpere=1;
        char caracNum; 
        try {           
            while ((nomNoeudMusique = br.readLine()) != null) {
                if (nomNoeudMusique.charAt(1) == ':'&& nomNoeudMusique.charAt(2) == 'p') { //Si le nom correspond à une playlist
                    nomPlayList= nomNoeudMusique.substring(3, nomNoeudMusique.length()); 
                    IPlayList p=new PlayList (nomPlayList);
                    caracNum=nomNoeudMusique.charAt(0);  
                    nbcourant=Integer.parseInt(caracNum+ ""); 
                    if (nbcourant==nbpere){ //Si la playlist doit être imbriquée au même niveau que la précédente                               
                        pere.ajouter(p); 
                        courante=p; 
                    } 
                    else if (nbcourant>nbpere){ //Si la playlist doit être imbriquée dans la playlist courante
                        courante.ajouter(p); 
                        grandPere=pere; 
                        pere=courante;
                        courante=p; 
                        nbpere++; 
                    }
                    else  { // Sinon il faut remonter dans la hiérarchie
                        for (nbcourant=nbcourant;nbcourant<=nbpere;nbcourant++){ 
                            courante=pere; 
                            pere=grandPere;
                            nbpere --; 
                        }
                        pere.ajouter(p); 
                        courante=p; 
                    }
                    
                }
                else {
                    ajouterMusiqueAPlayList(courante,nomNoeudMusique,racine); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        //Essai de fonction pour gérer tous les niveaux d'imbrication, ne fonctionne pas encore
        /*private void importerPlayLists (List<PlayList> liste, BufferedReader br){
        int niveauImbrication=1, nbcourant, indiceCourant=0,nbPlayListsImbriques=0;   
        String nomNoeudMusique, nomPlayList; 
        char caracNum;         
        try {           
            while ((nomNoeudMusique = br.readLine()) != null) {
                if (nomNoeudMusique.charAt(1) == ':'&& nomNoeudMusique.charAt(2) == 'p') { //Si le nom correspond à une playlist
                    nomPlayList= nomNoeudMusique.substring(3, nomNoeudMusique.length()); 
                    PlayList p=new PlayList (nomPlayList);
                    caracNum=nomNoeudMusique.charAt(0);  
                    nbcourant=Integer.parseInt(caracNum+ ""); 
                    if (nbcourant==1){ 
                        liste.get(0).ajouter(p); 
                        indiceCourant++;
                        niveauImbrication=1; 
                    }
                    else if (nbcourant==niveauImbrication){ //Si la playlist doit être imbriquée au même niveau que la précédente                               
                        nbPlayListsImbriques++; 
                        liste.get(indiceCourant-niveauImbrication-nbPlayListsImbriques).ajouter(p);
                        liste.add(p);
                        indiceCourant++;
                    } 
                    else if (nbcourant>niveauImbrication){ //Si la playlist doit être imbriquée dans la playlist courante
                        liste.get(indiceCourant).ajouter(p); 
                        niveauImbrication++;
                        liste.add(p);
                        indiceCourant++; 
                    }
                    else  { // Sinon il faut remonter dans la hiérarchie 
                        for (nbcourant=nbcourant;nbcourant<=niveauImbrication;nbcourant++){                            
                            niveauImbrication --; 
                        }  
                        liste.get(indiceCourant-niveauImbrication-nbPlayListsImbriques).ajouter(p);
                        nbPlayListsImbriques++; 
                        liste.add(p); 
                        indiceCourant++; 
                                                                    
                         
                    }
                    
                }
                else {
                    ajouterMusiqueAPlayList(liste.get(indiceCourant),nomNoeudMusique,liste.get(0)); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    private void ajouterMusiqueAPlayList(IPlayList p, String nom, IPlayList tout) {
        for (NoeudMusique m : tout.getPlayList()) {
            if (m.getTitre().equals(nom)) {
                p.ajouter(m);
            }
        }
    }
    
    
    @Override
    public void sauver(IPlayList racine) {
        for (File f : new File(repositoryPlayLists).listFiles()){ 
                if (f.getName().equals("ToutesLesPlaylists.txt"))
                    f.delete(); 
        }
            File f = new File(repositoryPlayLists+"/TouteslesPlayLists.txt");
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)))){
                for (NoeudMusique nm : racine.getPlayList()){ 
                    if (nm instanceof IPlayList){
                        ajouterPlayListaFichier((IPlayList)nm,pw,1);
                    }
                    else {
                        pw.println(nm.getTitre());
                    }
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        
    }

    private void ajouterPlayListaFichier(IPlayList p,PrintWriter pw, int nb) {
        pw.println(nb+":p"+p.getTitre()); 
        for (NoeudMusique nm : p.getPlayList()){
            if (nm instanceof Musique){
                pw.println(nm.getTitre());
            }
            else {
                ajouterPlayListaFichier((PlayList) nm, pw, ++nb);
            }
        }
    }
   
    
    
    
}


