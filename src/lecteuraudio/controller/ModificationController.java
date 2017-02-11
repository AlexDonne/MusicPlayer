/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lecteuraudio.metier.IMusique;

/**
 * FXML Controller class
 *
 * @author nahel
 */
public class ModificationController implements Initializable {

    @FXML
    private Button validButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField titreTextField;
    @FXML
    private TextField auteurTextField;
    
    private boolean newval=false;
    
    private IMusique musique;
    
    private StringProperty titreProperty=new SimpleStringProperty();
    public StringProperty titreProperty(){return titreProperty; }
    public String getTitre() {return titreProperty.get();}
    public void setTitre(String titre) {this.titreProperty.set(titre); }
    
    private StringProperty auteurProperty=new SimpleStringProperty();     
    public StringProperty auteurProperty(){return auteurProperty; }
    public String getAuteur() {return auteurProperty.get();}
    public void setAuteur(String auteur) {this.auteurProperty.set(auteur);}
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        titreProperty().bind(titreTextField.textProperty());
        auteurProperty().bind(auteurTextField.textProperty());
    }    

    @FXML
    private void onValid(ActionEvent event) {
        newval=true;
        ((Stage)cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }
    
    public boolean isNewVal(){
        return newval;
    }
    
    public void setMusique(IMusique musique){
        this.musique=musique;
        titreTextField.setText(musique.getTitre());
        auteurTextField.setText(musique.getAuteur());
    }
}
