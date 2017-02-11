/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.persistanceBin;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import lecteuraudio.metier.IPlayList;
import lecteuraudio.metier.NoeudMusique;
import lecteuraudio.metier.IDataManager;

/**
 *
 * @author alexd
 */
public class BinaryDataManager implements IDataManager{
    private String repositoryPlayLists=System.getProperty("user.dir")+"/Playlists"; 
    private File f= new File (repositoryPlayLists); 
    // TOUTES LES PLAYLISTS VONT DEVENIR DES IPLAYLISTS
    
    @Override
    public void charger(IPlayList racine) {
        if (!new File(repositoryPlayLists).exists()) {
            new File(repositoryPlayLists).mkdir();
        } 
        for (File f : new File(repositoryPlayLists).listFiles()) {
            String nom = f.getName();
            if (nom.equals("ToutesLesPlaylistsSer.bin")) {
               
                try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(f))) {
                    for (NoeudMusique p : ((IPlayList)reader.readObject()).getPlayList()) {
                        racine.ajouter(p); 
                    } 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
 
    }
    
  

    @Override
    public void sauver(IPlayList racine) {
        for (File f : new File(repositoryPlayLists).listFiles()){ 
                if (f.getName().equals("ToutesLesPlaylistsSer.bin"))
                    f.delete(); 
        }
        File fbin=new File (repositoryPlayLists+"/ToutesLesPlaylistsSer.bin"); 
        try (ObjectOutputStream writer=new ObjectOutputStream( new FileOutputStream(fbin))){
            writer.writeObject(new BinaryPlayList(racine));
        }
        catch (Exception e){ 
            e.printStackTrace();
        }
    }
    
    
    
}
