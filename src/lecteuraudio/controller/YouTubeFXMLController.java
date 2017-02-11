    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.controller;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lecteuraudio.metier.ManagedDownload;
import lecteuraudio.metier.Manager;
import lecteuraudio.metier.NoeudMusique;

/**
 * FXML Controller class
 *
 * @author nahel
 */
public class YouTubeFXMLController implements Initializable {

    @FXML
    private Button download;
    @FXML
    private Button cancel;
    @FXML
    private Button precedentWeb;
    @FXML
    private Button nextWeb;
    @FXML
    private Button downloadFromNav;
    
    @FXML
    private WebView webView;
    @FXML
    private TextField urlTextField;
    @FXML
    private Label downloadStatus;
    @FXML
    private ProgressBar downloadProgress;
    
    
    
    @FXML
    private ChoiceBox<String> listChoice;
    @FXML
    private Button recupButton;
    
    private final ObjectProperty<WebEngine> webengineProperty = new SimpleObjectProperty<>();
    public WebEngine getWebEngine() { return webengineProperty.get(); }
    public void setWebEngine(WebEngine value) { webengineProperty.set(value); }
    public ObjectProperty<WebEngine> webengineProperty() { return webengineProperty; }
    
    private final StringProperty pathDownloadProperty=new SimpleStringProperty();
    public StringProperty pathDownloadProperty(){return pathDownloadProperty; }
    public String getpathDownload() {return pathDownloadProperty.get();}
    public void setpathDownload(String titre) {this.pathDownloadProperty.set(titre);}
    
    private  ListProperty<String> listDownloadNav= new SimpleListProperty<>(FXCollections.observableArrayList());
    public ObservableList<String> getlistDownloadNav() {return listDownloadNav.get(); }   
    public void setlistDownloadNav(ListProperty<String> list) {this.listDownloadNav.set(list);}
    public ListProperty<String> listDownloadNavProperty() { return listDownloadNav ; }
    
    private Manager manager;
    
    public void setManager(Manager manager){
        this.manager=manager;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setWebEngine(webView.getEngine());
        getWebEngine().load("https://www.youtube.com/");

        
        getWebEngine().getHistory().currentIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                precedentWeb.setDisable(false);
            }
        });
       
        // set du premier url
        urlTextField.setText(getWebEngine().getLocation());
        // Binding sur l'url, utilisation du WebHistory car la property location du WebEngine ne marche pas bien
        getWebEngine().getHistory().currentIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                final WebHistory history=getWebEngine().getHistory();
                int currentIndex=history.getCurrentIndex();
                urlTextField.setText(history.getEntries().get(history.getEntries().size()-1).getUrl());
            }
        });
                
    }


    public void DirectDownload(String url) throws Exception {

        String dest = System.getProperty("user.dir") + "/Musiques";

        ManagedDownload managerDownload = new ManagedDownload(url, dest);
        downloadStatus.textProperty().bind(managerDownload.downloadStatusProperty());
        downloadProgress.progressProperty().bind(managerDownload.progressProperty());
        managerDownload.start();
        //Appelle la méthode changed quand le path du téléchargement est appellé, verifie qu'il n'est pas vide
        managerDownload.pathDownloadProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                cancel.setDisable(false);
                download.setDisable(false);
                downloadProgress.setVisible(false);
                if(managerDownload.getpathDownload()!=null && !managerDownload.getpathDownload().equals(""))
                    setpathDownload(managerDownload.getpathDownload());
            }
        });
    }

    @FXML
    private void onDownload(ActionEvent event) throws Exception{

        cancel.setDisable(true);
        download.setDisable(true);
        downloadProgress.setVisible(true);
        DirectDownload(urlTextField.getText());

    }

    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
        
    }
  
    @FXML
    private void onPrecedentWeb(ActionEvent event) {
        
        nextWeb.setDisable(false);
        final WebHistory history=getWebEngine().getHistory();
        ObservableList<WebHistory.Entry> entryList=history.getEntries();
        int currentIndex=history.getCurrentIndex();
        
        Platform.runLater(new Runnable() { 
            public void run() { 
                history.go(-1); 
                urlTextField.setText(entryList.get(currentIndex-1).getUrl());
            } 
        });
        
        if(currentIndex<2) Platform.runLater(new Runnable() { public void run() { precedentWeb.setDisable(true); } });
    }

    @FXML
    private void onNextWeb(ActionEvent event) {
        
        
        precedentWeb.setDisable(false);
        final WebHistory history=getWebEngine().getHistory();
        ObservableList<WebHistory.Entry> entryList=history.getEntries();
        int currentIndex=history.getCurrentIndex();
        
        Platform.runLater(new Runnable() { 
            public void run() { 
                history.go(1); 
                urlTextField.setText(entryList.get(currentIndex+1).getUrl());
            } 
        });
        
        Platform.runLater(new Runnable() { public void run() { nextWeb.setDisable(true); } });
        
        
    }

    private String getTitrePageCourante(){
        final WebHistory history=getWebEngine().getHistory();
        ObservableList<WebHistory.Entry> entryList=history.getEntries();
        int currentIndex=history.getCurrentIndex();
        String titre=entryList.get(currentIndex).getTitle();
        if(titre.contains(" - YouTube")){
            titre=titre.substring(0, titre.indexOf(" - YouTube"))+".mp3";
            
            //ICI UTILISER UNE REGEX
            titre=titre.replace("(Official video)", "").replace("&", "et").replaceAll("[|\"+'/]", "");
            
            return titre;
        }
        return null;
    }
    
    @FXML
    private void onDownloadFromNav(ActionEvent event) throws Exception {
        
        if(urlTextField.getText().contains("https://www.youtube.com/watch")){
            String path="www.youtubeinmp3.com/fetch/?video="+urlTextField.getText()+"&title="+getTitrePageCourante();
            Desktop.getDesktop().browse(new URI(path.replaceAll(" ", "%20")));
            if(!listDownloadNav.contains(getTitrePageCourante()))
                listDownloadNav.add(getTitrePageCourante());
            listChoice.setDisable(false);
            recupButton.setDisable(false);
            listChoice.itemsProperty().bind(listDownloadNavProperty());
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Musique introuvable");
            alert.setContentText("La musique à télécharger est introuvable.");
            alert.showAndWait();
        }
        
    }

    @FXML
    private void onRecupButton(ActionEvent event) {
        String path=listChoice.getSelectionModel().getSelectedItem();
        String home = System.getProperty("user.home");
        File file = new File(home+"/Downloads/" + path); 
        if(file.exists()){
            manager.copierDansRepository(file);
            setpathDownload(file.getPath());
            listDownloadNav.remove(listChoice.getSelectionModel().getSelectedItem());
            if(listDownloadNav.isEmpty()){
            listChoice.setDisable(true);
            recupButton.setDisable(true);
            }
        }
    }
    
}
