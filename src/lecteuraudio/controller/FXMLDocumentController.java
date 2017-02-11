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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.Utils;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lecteuraudio.cellfactory.NumberColCellFactory;
import lecteuraudio.cellfactory.NumberColCellValueFactory;
import lecteuraudio.metier.Lecteur;
import lecteuraudio.metier.Manager;
import lecteuraudio.metier.Musique;
import lecteuraudio.metier.NoeudMusique;
import lecteuraudio.metier.PlayList;
import lecteuraudio.metier.IPlayList;
import lecteuraudio.metier.PlayListMusiques;
import lecteuraudio.persistanceBin.BinaryPlayList;

/**
 * Controleur de la fenetre principale
 *
 * @author nachazot1
 */
public class FXMLDocumentController implements Initializable {

    
    /*********************************************************************************************************************
        *                                             BOUTONS
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="BOUTONS">
    
    @FXML
    private Button play;
    @FXML
    private Button previous;
    @FXML
    private Button next;
    @FXML
    private Button muteButton;
    @FXML
    private Button ajoutPlayList;
    @FXML
    private Button rechButton;
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                               TEXTFIELD
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="TEXTFIELD">
    @FXML
    private TextField nomPlayListAjout;

    @FXML
    private TextField zoneRech;
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                                 LABEL
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="LABEL">
    
    @FXML
    private Label titreMusique;
    @FXML
    private Label tempsEcoule;
    @FXML
    private Label tempsRestant;
    @FXML
    private Label auteur;
    
    //</editor-fold>

    /*********************************************************************************************************************
     *                                               CONTENEURS
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="CONTENEURS">

    @FXML
    private BorderPane borderPane;
    @FXML
    private FlowPane zoneAjout;
    @FXML
    private TreeItem<NoeudMusique> rootItem;
    @FXML
    private TreeView<NoeudMusique> treeView;
    @FXML
    private TableView tableView = new TableView<IMusique>();
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                             LECTEURCONTROLS
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="LECTEURCONTROLS">
    @FXML
    private ProgressBar progressbar;
    @FXML
    private Slider volumeSlider;
    
    private Lecteur lec = new Lecteur();
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                                PROPERTY
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY">
    
    private ObjectProperty<Manager> manager;
    public Manager getManager() {return manager.get();}
    public void setManager(Manager value) {manager.set(value);}
    public ObjectProperty<Manager> managerProperty() {return manager;}
    
    //Noeud sélectionné dans la TreeView
    private final ObjectProperty<NoeudMusique> selectedNoeud = new SimpleObjectProperty<>();
    public NoeudMusique getSelectedNoeud() {return selectedNoeud.get();}
    public void setSelectedNoeud(NoeudMusique value) {selectedNoeud.set(value);}
    public ObjectProperty<NoeudMusique> selectedNoeudProperty() {return selectedNoeud;}
    
    //</editor-fold>

    /*********************************************************************************************************************
     *                                                 STAGE
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="STAGE">

    //Fenetre YouTube
    private Stage youTubeStage;
    private YouTubeFXMLController youTubeController;

    //Fenetre de modification
    private Stage modifStage;
    private ModificationController modifController;

    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                             INITIALISATION
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="INITIALISATION">
    
    private ArrayList<NoeudMusique> pressePapier; //Utilisé pour le copié collé

    private IPlayList listecourante;
    private PlayListMusiques listemusiques; //Liste de musiques seulement

    private boolean onProgressMoving=false; //Permet de savoir si la souris se situe au dessus de la progress bar
    
    public FXMLDocumentController(Manager manager) {
        this.manager = new SimpleObjectProperty<>(manager);
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTreeView();
        
        //resélectionne le premier item pour mettre a jour l'affichage
        treeView.getSelectionModel().select(null);
        treeView.getSelectionModel().selectFirst();
        
        ObjectBinding<NoeudMusique> ob = Bindings.createObjectBinding(
                () -> treeView.getSelectionModel().getSelectedItem().getValue(),
                treeView.getSelectionModel().selectedItemProperty());
        
        selectedNoeudProperty().bind(ob);

        //Initialisation des listes
        listecourante = getManager().getRacine();
        listemusiques = new PlayListMusiques(listecourante);

        updateTreeView(rootItem, getManager().getRacine());

        initializeTableView();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

   

    //Initialise les bindings sur les property du lecteur qui ne sont pas fait dans le FXML, utilisé aux appelles des fonctions onMusiqueChoisie, .
    private void bindings() {

        //Binding du Slider sur le volume du Lecteur
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    lec.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });

        //Binding des labels tempsEcoule, tempsRestant sur les valeur du mediaPlayer, ainsi que le binding de la progressBar
        lec.currentTimeProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                Duration current = lec.getCurrentTime();
                Duration duration = lec.getTotalDuration();
                
                tempsRestant.textProperty().bind(new SimpleStringProperty(Utils.formatTime(duration)));
                if(!onProgressMoving){
                    tempsEcoule.textProperty().bind(new SimpleStringProperty(Utils.formatTime(current)));
                    progressbar.setProgress((current.toSeconds() / duration.toSeconds()));
                }
            }
        });
        //Si la musique est à la fin, jouer la prochaine musique du lecteur
        lec.setOnEndOfMedia(new Runnable() {
            public void run() {
                getManager().setNoeudCourant(lec.next());
                bindings();
            }
        });
    }

    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                              MENU DU HAUT
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="MENU DU HAUT">
    
    public void exit() {
        if (youTubeStage != null) {
            youTubeStage.close();
        }
        Platform.exit();
    }

    @FXML
    private void onExit(ActionEvent event) {
        getManager().sauver();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir quitter ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            exit();
        }

    }
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                        AJOUT PLAYLIST ZONE DU BAS
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="AJOUT PLAYLIST ZONE DU BAS">
    
    //creerIPlayListDepuisView : validation apres la saisie d'une playlist, la rajoute dans la liste des playlist, affiche une fenetre d'erreur si le titre de la playlist existe deja
    private void creerPlaylistDepuisView() {
        IPlayList p = new PlayList(nomPlayListAjout.getText());
        if (!getSelectedNoeud().getTitre().equals("Racine")) {   // Si la playlist choisi n'est pas la playlist racine :
            Alert alert = new Alert(AlertType.NONE);
            alert.setTitle("Choix");
            alert.setHeaderText(null);
            alert.setContentText("Voulez vous ajouter la playlist \"" + nomPlayListAjout.getText() + "\" à la Racine, ou à la playlist \"" + getSelectedNoeud().getTitre() + "\" ?");
            ButtonType racineButton = new ButtonType("Racine");
            ButtonType playlistButton = new ButtonType(getSelectedNoeud().getTitre());
            alert.getButtonTypes().addAll(racineButton, playlistButton, ButtonType.CANCEL);
            Optional result = alert.showAndWait();
            if (result.get() == racineButton) {
                if (!getManager().ajouter(p)) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Une playlist possède déja le nom \"" + p.getTitre() + "\".");
                    alert.showAndWait();
                }
                else treeView.getSelectionModel().selectLast();
            } else if (result.get() == playlistButton) {
                if (!((IPlayList) getSelectedNoeud()).ajouter(p)) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Une playlist possède déja le nom \"" + p.getTitre() + "\".");
                    alert.showAndWait();
                }
                else treeView.getSelectionModel().selectLast();
            }
        }
        else{   // Si la playlist choisi est la playlist racine :
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Choix");
            alert.setHeaderText(null);
            alert.setContentText("Voulez vous ajouter la playlist \"" + nomPlayListAjout.getText() + "\" à la Racine ?");
            Optional result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (!getManager().ajouter(p)) {
                    alert.setAlertType(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Une playlist possède déja le nom \"" + p.getTitre() + "\".");
                    alert.showAndWait();
                }
                else treeView.getSelectionModel().selectLast();
            }
        }
        updateTreeView(rootItem, getManager().getRacine());
        nomPlayListAjout.clear();
        zoneAjout.setVisible(false);
        ajoutPlayList.setVisible(true);
    }

    //onAnnulerPlaylist : annule l'ajout d'une playlist, cache la zone permettant de saisir une nouvelle playlist à ajouter 
    @FXML
    private void onAnnulerPlaylist(ActionEvent event) {
        nomPlayListAjout.clear();
        zoneAjout.setVisible(false);
        ajoutPlayList.setVisible(true);
    }

    @FXML
    private void onValidNom(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            creerPlaylistDepuisView();
        }
    }

    @FXML
    private void onValidNomButton(ActionEvent event) {
        creerPlaylistDepuisView();
    }
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                          TREEVIEW ZONE GAUCHE
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="TREEVIEW ZONE GAUCHE">
    
    @FXML
    private void onNoeudMusiqueChoisie(MouseEvent event) {
        NoeudMusique nm = null;
        if (treeView.getSelectionModel().getSelectedItem() != null) {
            nm = treeView.getSelectionModel().getSelectedItem().getValue();
        }

        if (nm != null) {
            if (nm instanceof IMusique) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {  //double clic gauche
                    //Met la musique séléctionné dans musiqueView puis la joue
                    getManager().setNoeudCourant((IMusique) nm);
                    lec.setPlaylist((IPlayList) treeView.getSelectionModel().getSelectedItem().getParent().getValue());
                    lec.setMusiqueCourante(getManager().getNoeudCourant());
                    lec.pause();
                    if ("play".equals(play.getId())) {
                        play.setId("pause");
                    }
                    lec.play(getManager().getNoeudCourant());

                    lec.setVolume(volumeSlider.getValue());
                    bindings();

                } else {
                    //Changement de node sur la zone centrale à implémenter
                }
            } else {
                listecourante = (IPlayList) nm;
                listemusiques = new PlayListMusiques(listecourante);
            }
        }
    }
    
    /*
    * Intitilisation du TreeView
    * @author nachazot1
     */
    private void initializeTreeView() {
        //sélectionne le premier item
        treeView.getSelectionModel().selectFirst();
        //Ajoute le listner sur le noeud séléctionné
        treeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue)
                -> treeViewSelectedItemListener(observable, oldValue, newValue));

    }

    /*
    * Bindings sur les musiques de la PlayList séléctionné dans le treeView
    * @author nachazot1
     */
    private void treeViewSelectedItemListener(Observable observable,
            Object oldValue, Object newValue) {
        //récupération de l'élément de la structure composite correspondant
        NoeudMusique noeud = getSelectedItemInTreeView((TreeItem<NoeudMusique>) newValue);
        //Affichage des musiques de la IPlayList dans la zone centrale
        if (noeud instanceof IPlayList) {
            tableView.itemsProperty().bind(new PlayListMusiques((IPlayList) noeud).playlistProperty());
        }
    }

    /*
    * Return la valeur du treeItem
    * @author nachazot1
     */
    private NoeudMusique getSelectedItemInTreeView(TreeItem<NoeudMusique> item) {

        TreeItem<NoeudMusique> treeItem = item;

        if (treeItem == null) {
            return null;
        }

        NoeudMusique noeud = treeItem.getValue();

        return noeud;
    }

    //Met a jour le treeView à partir d'un noeud
    private  void updateTreeView(TreeItem<NoeudMusique> item, NoeudMusique noeud) {
        //vide le TreeItem
        item.getChildren().clear();

        //ajoute des TreeItems pour chaque noeud du composite
        if (noeud instanceof IPlayList) {

            for (NoeudMusique nm : ((IPlayList) noeud).getListPlayList()) {
                TreeItem<NoeudMusique> noeudItem = new TreeItem<>(nm);
                item.getChildren().add(noeudItem);
                updateTreeView(noeudItem, nm);
            }
        }
    }

    /*Désélectionne puis reselectionne pour mettre à jour l'affichage de la zone centrale
    *Puis met à jour l'affichage du treeView
    *
     */
    private  void updateLayoutTreeView(TreeItem<NoeudMusique> item, NoeudMusique noeud) {

        //Met à jour l'affichage du treeView
        updateTreeView(item, noeud);
        //Met à jour l'affichage de la zone centrale
        treeView.getSelectionModel().select(null);
        treeView.getSelectionModel().select(item);
    }
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                        MENU CONTEXTUEL ZONE GAUCHE
     *********************************************************************************************************************/

    //<editor-fold defaultstate="collapsed" desc="MENU CONTEXTUEL ZONE GAUCHE">
    
    //onAjoutPlayList : affiche la zone permettant de saisir une nouvelle playlist à ajouter 
    @FXML
    private void onAjoutPlayList(ActionEvent event) {
        zoneAjout.setVisible(true);
        ajoutPlayList.setVisible(false);
        
        zoneAjout.requestFocus();

        
    }
    
    //onSupprimerNoeudMusique : Supprime un noeud, affiche une fenetre de dialogue permettant de confirmer
    @FXML
    private void onSupprimerNoeudMusique(ActionEvent event) {

        if (treeView.getSelectionModel().getSelectedItem().getValue() == getManager().getRacine()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Vous ne pouvez pas supprimer la racine.");
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + treeView.getSelectionModel().getSelectedItem().getValue().getTitre() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            TreeItem<NoeudMusique> item = treeView.getSelectionModel().getSelectedItem();
            ((IPlayList) item.getParent().getValue()).supprimer(item.getValue());

            updateLayoutTreeView(item.getParent(), item.getParent().getValue());
        }
    }
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                              ZONE CENTRAL
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="ZONE CENTRAL">
    
    /*
    * Intitilisation de la zone central, la TableView
    * Donne lesproperty à associer à chaque colonne de la TableView
    * @author nachazot1
     */
    public void initializeTableView() {

        ((TableColumn<Musique, String>) tableView.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory("titre"));
        ((TableColumn<Musique, String>) tableView.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory("auteur"));
        ((TableColumn<Musique, String>) tableView.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory("duree"));
        //NumberCol fabrique des index pour chaque cellule
        TableColumn<IMusique, IMusique> numberCol=(TableColumn<IMusique, IMusique>) tableView.getColumns().get(0);
        numberCol.setCellValueFactory(new NumberColCellValueFactory());
        numberCol.setCellFactory(new NumberColCellFactory());
    }
    
    @FXML
    private void onMusiqueChoisie(MouseEvent event) {

        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {  //double clic gauche
            //Met la musique séléctionné dans musiqueView puis la joue
            getManager().setNoeudCourant((IMusique) tableView.getSelectionModel().getSelectedItem());
            lec.setPlaylist(listecourante);
            lec.setMusiqueCourante(getManager().getNoeudCourant());
            lec.pause();
            if ("play".equals(play.getId())) {
                play.setId("pause");
            }
            lec.play(getManager().getNoeudCourant());

            lec.setVolume(volumeSlider.getValue());
            
            bindings();   
            

        }
    }
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                       MENU CONTEXTUEL ZONE CENTRAL
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="MENU CONTEXTUEL ZONE CENTRAL">
    
    /*
    *onModifier : ouvre une nouvelle fenetre permettant de modifier la musique séléctionné.
     */
    @FXML
    private void onModifier(ActionEvent event) throws Exception {
        ObservableList list = tableView.getSelectionModel().getSelectedItems();
        if (list.size() > 1 || list.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Vous devez séléctionner un seul élément à modifier.");
            alert.showAndWait();
        } else {
            modifStage = new Stage();
            modifStage.setTitle("Modification");
            FXMLLoader fxmloader = new FXMLLoader(getClass().getResource("/lecteuraudio/vue/modificationFXML.fxml"));

            Parent root = fxmloader.load();
            modifController = fxmloader.getController();
            modifController.setMusique((IMusique) list.get(0));
            Scene scene = new Scene(root);
            modifStage.setScene(scene);
            modifStage.showAndWait();
            if (modifController.isNewVal()) {
                ((IMusique) tableView.getSelectionModel().getSelectedItems().get(0)).setAuteur(modifController.getAuteur());
                ((IMusique) tableView.getSelectionModel().getSelectedItems().get(0)).setTitre(modifController.getTitre());
            }

        }

    }

    
    /*
    *modificationInteligente : cherche le titre et l'auteur dans un titre composé d'une musique.
    * Gere les erreurs grace à des alerts.
    */
    private IMusique modificationInteligente(IMusique musique, String separateur){
        IMusique newmusique = new Musique(musique.getAuteur(),musique.getTitre(),musique.getPath());
        int index = musique.getTitre().indexOf(separateur);
        int indexsuiv = index + separateur.length();
        
        if(index==-1){
            return null;
        }
        else{
            String newauteur = musique.getTitre().substring(0, index);
            String newtitre = musique.getTitre().substring(indexsuiv, musique.getTitre().length());

            newmusique.setTitre(newtitre);
            newmusique.setAuteur(newauteur);
            
        }
        return newmusique;
    }
    
    /*
    *onModifierIntelligent : Gere les erreurs de la modification grace à des alerts.
    */
    @FXML
    private void onModifierIntelligent(ActionEvent event) throws Exception {
        ObservableList list = tableView.getSelectionModel().getSelectedItems();
        if(list==null) return;
        
        ButtonType stopAffich = new ButtonType("Ne plus afficher");
        boolean musiquePossedeDejaAuteurAffichage = true; //Permet de savoir si le message d'erreur doit être afficher
        boolean titreEtAuteurIndeterminable = true;
        for (Object m : list) {
            IMusique musique = (IMusique) m;
            
            //Verifie que la musique n'a pas déja un titre
            if (musique.getAuteur() != null && !musique.getAuteur().equals("") && !musique.getAuteur().equals("Artiste inconnu")) {
                if (musiquePossedeDejaAuteurAffichage) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Musique déja modifié.");
                    alert.setContentText("La musique \"" + musique.getTitre() + "\" possède déja un titre et un auteur.\n" + (list.size() - list.indexOf(m)) + " éléments restant à modifier.");
                    alert.getButtonTypes().add(stopAffich);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == stopAffich) {
                        //Si l'utilisateur ne veut plus afficher ce message, la varibale passe à false et l'alert n'est plus affichée
                        musiquePossedeDejaAuteurAffichage = false;
                    }
                }
                continue;
            }
            //Essaie de trouver un titre et un auteur séparés par " - "
            IMusique newmusique=modificationInteligente(musique, " - ");
            if (newmusique == null) {
                //Essaie de trouver un titre et un auteur séparés par "-"
                newmusique=modificationInteligente(musique, "-");
                if (newmusique == null) {

                    if (titreEtAuteurIndeterminable) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setTitle("Modification introuvable.");
                        alert.setContentText("Impossible de différencier le titre et l'auteur de \"" + musique.getTitre() + "\".\n" + (list.size() - list.indexOf(m)) + " éléments restant à modifier.");
                        alert.getButtonTypes().add(stopAffich);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == stopAffich) {
                            //Si l'utilisateur ne veut plus afficher ce message, la varibale passe à false et l'alert n'est plus affichée
                            titreEtAuteurIndeterminable = false;
                        }
                    }
                    continue;
                }
                
            }
            
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Confirmation");
            alert.setAlertType(AlertType.CONFIRMATION);
            alert.setContentText("Titre : \"" + newmusique.getTitre() + "\"\nAuteur : \"" + newmusique.getAuteur() + "\"\nValidez-vous la modification ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                ((IMusique) musique).setTitre(newmusique.getTitre());
                ((IMusique) musique).setAuteur(newmusique.getAuteur());
            }
        }

    }

    /*
    *onCopier : met le contenu des items séléctionné dans le presse papier
    */
    @FXML
    private void onCopier(ActionEvent event) {
        ObservableList list = tableView.getSelectionModel().getSelectedItems();
        pressePapier = new ArrayList<>(list);
    }

    /*
    *onColler : met le contenu du pressePapier dans la PlayList séléctionné
    */
    
    @FXML
    private void onColler(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Attention");
        alert.setHeaderText(null);

        if (pressePapier != null) {
            TreeItem<NoeudMusique> playlist = treeView.getSelectionModel().getSelectedItem();
            if (playlist.getValue() instanceof IMusique) {
                alert.setContentText("Veuillez sélectionner la playlist dans laquelle coller dans le menu à gauche.");
                alert.showAndWait();
                return;
            }
            for (NoeudMusique nm : pressePapier) {
                if (!((IPlayList) playlist.getValue()).ajouter(nm)) {
                    alert.setContentText("Vous ne pouvez pas ajouter deux fois la même musique dans une playlist.\n" + nm.getTitre() + " existe déja.");
                    alert.showAndWait();
                }
            }
            updateLayoutTreeView(playlist, playlist.getValue());
        }
    }
    
    /*
    * onCreerPlayListDepuisSelection : créer une playlist à partir des musiques séléctionnés dans la tableview.
    * Ouvre une boite de dialogue pour demander le nom de la playlist
    * Gere les erreurs sous forme d'alert.
    */
    @FXML
    private void onCreerPlayListDepuisSelection(ActionEvent event) {
        ObservableList list = tableView.getSelectionModel().getSelectedItems();
        
        if(list.size()==0) return;
        GridPane gridPane = new GridPane(); 
        gridPane.setHgap(6); 
        gridPane.setVgap(6); 
        TextField playlistfield = new TextField();
        gridPane.add(playlistfield, 1, 0); 
        playlistfield.setPromptText("Playlist"); 
        Dialog<String> dialog = new Dialog(); 
        dialog.setTitle("Playlist"); 
        dialog.setHeaderText("Veuillez saisir le nom de la playlist :");
        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Conversion du résultat lors de la validation du dialogue, null si c'est annulé.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return playlistfield.getText();
            }
            return null;
        });
        // On place le focus sur le champ de saisie. 
        Platform.runLater(() -> playlistfield.requestFocus());
        
        // Le bouton OK reste désactivé tant que l'identifiant n'est pas correct. 
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        playlistfield.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        Optional<String> result=dialog.showAndWait();
        if(result==null) return;
        IPlayList newplaylist=new BinaryPlayList(new PlayList(result.get()));
        
        for(Object m : list){
            newplaylist.ajouter((IMusique)m);
        }
        if(getManager().ajouter(newplaylist)){
            updateLayoutTreeView(rootItem, rootItem.getValue());
            rootItem.setExpanded(true);
            treeView.getSelectionModel().selectLast();
        }
        else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("PlayList déja existante");
            alert.setContentText("La playlist " + newplaylist.getTitre() + " existe déja.");
            alert.showAndWait();
        }
        
    }
    
    
    @FXML
    private void onSupprimerMusique() {
        
        List<IMusique> listeSuppr = (List<IMusique>) tableView.getSelectionModel().getSelectedItems();
        String message="";
        //Utilisation d'une arrayList pour pouvoir enlever le musique entrain d'etre joué
        ArrayList<IMusique> listeASuppr=new ArrayList(listeSuppr);
        
        if(getManager().getNoeudCourant()!=null)
            if(listeASuppr.contains((IMusique)getManager().getNoeudCourant())){
                listeASuppr.remove((IMusique)getManager().getNoeudCourant());
                message="\nLa musique "+getManager().getNoeudCourant().getTitre()+" ne sera pas supprimé (Musique en cours)";
            }
        
        if(listeASuppr.isEmpty())
            return;
        TreeItem<NoeudMusique> playlistItem = treeView.getSelectionModel().getSelectedItem();
        IPlayList playlist = (IPlayList) playlistItem.getValue();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        if(listeASuppr.size()>1)
            alert.setContentText("Êtes-vous sûr de vouloir supprimer "+listeASuppr.size()+" éléments ?");
        else if(listeASuppr.size()==1)
            alert.setContentText("Êtes-vous sûr de vouloir supprimer "+listeASuppr.get(0).getTitre()+" ?"+message);
        Optional result=alert.showAndWait();
        if(result.get()==ButtonType.OK){
            
            playlist.getPlayList().removeAll(listeASuppr);
        // Si on supprime dans la racine, on doit supprimer la musique dans le dossier Musiques et dans toutes les playlists 
            if (playlist.getTitre().equals("Racine")) {
                getManager().suppression(listeASuppr);
            }
            updateLayoutTreeView(playlistItem, playlistItem.getValue());
        }
    }
    
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                             LECTEUR MUSIQUE
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="LECTEUR MUSIQUE">
    @FXML
    private void playPressed(ActionEvent event) {

        if ("play".equals(play.getId())) {
            if (lec.play() != null) {
                play.setId("pause");
            }
        } else {
            play.setId("play");
            lec.pause();
        }

    }

    @FXML
    private void previousPressed(ActionEvent event) {

        NoeudMusique nm = lec.precedent();
        getManager().setNoeudCourant(nm);
        if (nm == null) {
            return;
        }
        if ("play".equals(play.getId())) {
            play.setId("pause");
        }
        bindings(); 
    }

    @FXML
    private void nextPressed(ActionEvent event) {

        NoeudMusique nm = lec.next();
        if (nm == null) {
            return;
        }
        getManager().setNoeudCourant(nm);
        if ("play".equals(play.getId())) {
            play.setId("pause");
        }
        bindings(); 
    }
    
    //onMuteClic : mute si le lecteur est unmute, unmute si le lecteur est mute
    @FXML
    private void onMuteClic(ActionEvent event) {
        if (!lec.isNull()) {
            if (lec.isMute()) {
                lec.setMute(false);
                muteButton.setId("mute");
            } else {
                lec.setMute(true);
                muteButton.setId("unmute");
            }
        }
    }
    
    // Permet de connaitre la valeur de la progressBar et du temps écoulé avant le hover
    private double progressSauv = -1;
    private Duration tempsSauv;
    
    @FXML
    private void OnProgressBar(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && lec.getMusiqueCourante()!=null) {
            if (progressbar != null && !lec.isNull()) {
                double d = event.getX();
                Duration newDuration = new Duration(event.getX() / progressbar.getWidth() * lec.getTotalDuration().toMillis());
                tempsSauv=newDuration;
                lec.seek(newDuration);
                progressSauv=event.getX() / progressbar.getWidth();
                progressbar.setProgress(progressSauv);
                
            }
            bindings();
        }
    }
    
    
    
    @FXML
    private void onProgressBarMoved(MouseEvent event) {
        if (lec.getMusiqueCourante()!=null) {
            if (progressbar != null && !lec.isNull()) {
                if(!lec.isPlaying() && progressSauv==-1){
                    progressSauv=progressbar.getProgress();
                    tempsSauv=lec.getCurrentTime();
                }
                onProgressMoving=true;
                Duration duration = lec.getTotalDuration();
                //Affiche le temps correspondant a la position du curseur sur la progressBar
                tempsEcoule.textProperty().bind(new SimpleStringProperty(Utils.formatTime(new Duration((event.getX()/progressbar.getWidth())*duration.toMillis())))); 
                progressbar.setId("progressBarHover");
                progressbar.setProgress(event.getX() / progressbar.getWidth());
            }
        }
    }
    
    @FXML
    private void onProgressBarExited(MouseEvent event) {
        if (lec.getMusiqueCourante() != null) {
            if (progressbar != null && !lec.isNull()) {
                onProgressMoving = false;
                progressbar.setId("progressBar");
                if(!lec.isPlaying()){
                    progressbar.setProgress(progressSauv);
                    Duration duration = lec.getTotalDuration();
                    tempsEcoule.textProperty().bind(new SimpleStringProperty(Utils.formatTime(tempsSauv))); 

                }
                progressSauv=-1;
                bindings();
            }
        }
    }
    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                               RECHERCHE
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="RECHERCHE">
    
    @FXML
    private void onRech(ActionEvent event) {
        String recherche = zoneRech.getText();
        if (recherche != null && !"".equals(recherche)) {
            tableView.itemsProperty().bind(listemusiques.rechByString(recherche).playlistProperty());
        } else {
            tableView.itemsProperty().bind(listemusiques.playlistProperty());
        }
    }

    @FXML
    private void onEnter(KeyEvent key) {
        if (key.getCode().equals(KeyCode.ENTER)) {
            String recherche = zoneRech.getText();
            if (recherche != null && !"".equals(recherche)) {
                tableView.itemsProperty().bind(listemusiques.rechByString(recherche).playlistProperty());
            } else {
                tableView.itemsProperty().bind(listemusiques.playlistProperty());
            }
        }
    }

    //</editor-fold>
    
    /*********************************************************************************************************************
     *                                            IMPORT ET YOUTUBE
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="IMPORT ET YOUTUBE">
    
    @FXML
    private void importPressed(ActionEvent event) throws Exception{

        
        if (getManager().chercherDisqueDur(borderPane.getScene().getWindow())) {
            updateLayoutTreeView(rootItem, getManager().getRacine());
        }
        

    }
    
    @FXML 
    private void importDirectoryPressed(ActionEvent event){ 
        if (getManager().chercherDisqueDurRep(borderPane.getScene().getWindow())) {
            updateLayoutTreeView(rootItem, getManager().getRacine());
        }
    }
    
    @FXML
    private void onYoutube() throws Exception {

        youTubeStage = new Stage();
        youTubeStage.setTitle("YouTube");
        FXMLLoader fxmloader = new FXMLLoader(getClass().getResource("/lecteuraudio/vue/youTubeFXML.fxml"));
        Parent root = fxmloader.load();
        youTubeController = fxmloader.getController();
        youTubeController.setManager(getManager());
        Scene scene = new Scene(root);
        youTubeStage.setScene(scene);
        
        youTubeStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                //Ferme la fenetre youTube
                youTubeController.getWebEngine().load(null);
            }
        });
        youTubeStage.show();

        //Récupère le chemin de la musique téléchargé quand il change
        youTubeController.pathDownloadProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue o, Object oldVal,
                    Object newVal) {
                String pathdownload = youTubeController.getpathDownload();
               
                /*Les fichiers téléchargés peuvent etre de deux formes : soit un seul fichier avec un nom normal, soit deux fichier : 
                * - nomdufichier.video.mp4
                * - nomdufichier.audio.mp4
                * On doit donc vérifier le nom du fichier, et modifier son nom ainsi que supprimer la video si nécéssaire
                */
                File f = new File(pathdownload);
                if (pathdownload.substring(pathdownload.length() - 4, pathdownload.length()).equals(".mp4")) {

                    String debutpath = pathdownload.substring(0, pathdownload.length() - 4);
                    if (!f.exists()) {
                        try {
                            Files.deleteIfExists(new File(debutpath + ".video.mp4").toPath());
                            Files.copy(new File(debutpath + ".audio.mp4").toPath(), new File(debutpath + ".mp4").toPath());
                            Files.delete(new File(debutpath + ".audio.mp4").toPath());

                            pathdownload = debutpath + ".mp4";
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                getManager().ajouterMusique(new File(pathdownload));
                updateLayoutTreeView(rootItem, getManager().getRacine());

            }
        });

    }

    //</editor-fold>

    /*********************************************************************************************************************
     *                                              DRAG AND DROP
     *********************************************************************************************************************/
    
    //<editor-fold defaultstate="collapsed" desc="DRAG AND DROP">
    
    @FXML
    private void onDragDetected(MouseEvent event) {
        if(event.getSceneY()>150){ // Ne pas commencer de drag quand on essaye de redimensionner les colonnes
            List<IMusique> dragged = (List<IMusique>) tableView.getSelectionModel().getSelectedItems();
            Dragboard dragBoard = tableView.startDragAndDrop(TransferMode.COPY);
            dragBoard.setDragView(new Text("  " + dragged.size() + " :" + dragged.toString()).snapshot(null, null), event.getX() / 100, event.getY() / 100);
            ClipboardContent content = new ClipboardContent();
            content.putString(dragged.toString());
            dragBoard.setContent(content);

            event.consume();
        }
    }
    
    
    @FXML
    private void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }

    }
    
   

    /*
    *onDragDropped : Permet de déposer des fichiers audio venant de l'ordinateur
     */
    @FXML
    private void onDragDropped(DragEvent event) throws Exception {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            for (File file : db.getFiles()) {
                String filePath = file.getAbsolutePath();
                String fileType = filePath.substring(filePath.length() - 3, filePath.length());
                if (fileType.equals("mp3") || fileType.equals("mp4") || fileType.equals("wav") || fileType.equals("aac")) {
                    if (getManager().copierDansRepository(file)) { //Si le fichier est bien copié sans erreur
                        getManager().ajouterMusique(file);

                        //Mise a jour de la vue pour voir l'élément importé
                        updateLayoutTreeView(rootItem, getManager().getRacine());
                        tableView.getSelectionModel().select(null);
                        tableView.getSelectionModel().selectLast();

                    } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Musique déja existante");
                        alert.setContentText("La musique " + file.getName() + " existe déja.");
                        alert.showAndWait();
                    }

                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Le fichier n'est pas une musique");
                    alert.setContentText("Le fichier " + file.getName() + " n'est pas une musique.");
                    alert.showAndWait();
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }
    //</editor-fold>
}
