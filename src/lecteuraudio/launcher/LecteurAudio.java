/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.launcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lecteuraudio.controller.FXMLDocumentController;
import lecteuraudio.metier.Manager;
import lecteuraudio.persistanceBin.BinaryDataManager;
import lecteuraudio.persistancetexte.TextDataManager;

/**
 *
 * @author nachazot1
 */
public class LecteurAudio extends Application {
    
    private Manager manager;
    
    private void charger (){ 
        manager=new Manager(new BinaryDataManager());   
        manager.charger();
    }
    
    
   
    
    @Override
    public void start(Stage stage) throws Exception {
        charger();
        FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("/lecteuraudio/vue/FXMLDocument.fxml"));
        fxmlloader.setController(new FXMLDocumentController(manager));
        Parent root = fxmlloader.load();
        Scene scene = new Scene(root);
        stage.setTitle("MusicPlayer");
        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stop();
            }
        });
        stage.show();
        
    }
    
    
    
    @Override
    public void stop()
    {
        manager.sauver();
        Platform.exit();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    
}
