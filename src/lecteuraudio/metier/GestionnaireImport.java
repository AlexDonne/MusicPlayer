 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;


import java.io.File;
import java.io.FileFilter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.Musique;
import lecteuraudio.metier.IPlayList;
import lecteuraudio.persistanceBin.BinaryMusique;

/**
 *
 * @author aldonne
 */
public class GestionnaireImport {
    
    private String repositoryPath=System.getProperty("user.dir")+"/Musiques";
    
    //Permet de savoir si les erreurs sous forme d'alert doivent etre affichés
    boolean affErreur=true;
    
    public String getRepositoryPath (){
        return repositoryPath;
    }
    
     
   
    public void ouverture() {
        if (!new File(repositoryPath+ "/").exists()) {
            new File(repositoryPath + "/").mkdir();
        }
    }
    
 
    public  void importerRepertoireMusiques (IPlayList racine){ 
        for( File f : new File(repositoryPath).listFiles()){ 
           ajouterMusique(f,racine); 
        }
        
    }    
    
    public  boolean chercherDisqueDur (IPlayList racine, Window window) {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Music files", "*.mp3", "*.wav", "*.mp4"), 
            new FileChooser.ExtensionFilter("All files", "*.*"),       
            new FileChooser.ExtensionFilter("MP3", "*.mp3"),
            new FileChooser.ExtensionFilter("WAV", "*.wav"),
            new FileChooser.ExtensionFilter("MP4", "*.mp4")
        );

        File fichier = fileChooser.showOpenDialog(window);
        if (fichier != null) {
            try {
                copierDansRepository(fichier);
                ajouterMusique(fichier, racine);
                return true;
            } catch (FileAlreadyExistsException e) {
                if(!ajouterMusique(fichier, racine)){
                    dialogueErreurFichierExistant(fichier.getName().substring(0, fichier.getName().length()-4));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }   
    
    private void dialogueErreurFichierExistant(String nom) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(nom+" déjà importé");
        ButtonType stopAffich=new ButtonType("Ne plus afficher");
        alert.getButtonTypes().add(stopAffich);
        Optional result=alert.showAndWait();
        if(result.get()==stopAffich){
            affErreur=false;
        }

    }

    public boolean ajouterMusique(File f, IPlayList racine) {
        int taille = (int) f.getName().length() - 4;
        IMusique m = new Musique("Artiste inconnu", f.getName().substring(0, taille), ("file:///" + System.getProperty("user.dir").replace("\\", "/") + "/Musiques/" + f.getName().replaceAll(" ", "%20")));
        Media media = new Media(m.getPath());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        //Attendre que le mediaplayer soit pret pour récuperer les metadatas
        
        mediaPlayer.setOnReady(() -> {
            m.setDuree(Utils.formatTime(media.getDuration()));
            m.setAuteur((String)media.getMetadata().get("artist"));
        });

        return racine.ajouter(m);
    }

    public void copierDansRepository(File source) throws Exception {
        String destination = System.getProperty("user.dir") + "/Musiques" + "/" + source.getName();

        Files.copy(source.toPath(), new File(destination).toPath());

    }
    
    /*
    *suppression : supprime toutes les musiques de la List. Les supprime du dossier "Musiques" à la fermeture de l'application.
    */
    public void suppression (List<IMusique> musique){
        for (IMusique m : musique){ 
            for (File f : new File(repositoryPath).listFiles()){
                String nom=f.getPath();
                String con=m.getPath().substring(m.getPath().indexOf("Musiques")+9,m.getPath().length());
                if (nom.replaceAll(" ", "%20").contains(m.getPath().substring(m.getPath().indexOf("Musiques")+9,m.getPath().length()))){
                    f.delete();
                }
            }
        }
    }
    
    public boolean chercherDisqueDurRep(IPlayList racine, Window window) {

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Open Resource File");

        File rep = dirChooser.showDialog(window);
        if (rep != null) {

            for (File f : rep.listFiles(new MusiqueFileFilter())) {
                try {
                    copierDansRepository(f);
                    ajouterMusique(f, racine);
                } catch (FileAlreadyExistsException e) {
                    if (!ajouterMusique(f, racine) && affErreur) {
                        dialogueErreurFichierExistant(f.getName().substring(0, f.getName().length()-4));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            affErreur=true;
            return true;
        }
        affErreur=true;
        return false;
    }
    
   

    
}
