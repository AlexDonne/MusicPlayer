package lecteuraudio.cellfactory;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.NoeudMusique;
import lecteuraudio.metier.IPlayList;

/**
 *
 * @author nahel
 */

public class TreeItemCell extends TreeCell<NoeudMusique> {
    
    
    @Override
    public void updateItem(NoeudMusique item, boolean empty)
    {
        super.updateItem(item, empty);
        if(empty)
        {
            setText(null);
            setGraphic(null);
            textProperty().unbind();
        }
        
        if(item instanceof IMusique)
        {
            setText(item.getTitre());
            ImageView src = new ImageView();
            src.setImage(new Image("lecteuraudio/vue/musique.png"));
            src.setFitHeight(16);
            src.setFitWidth(16);
            setGraphic(src);
        }
        
        
        if(item instanceof IPlayList)
        {
            setText(item.getTitre());
            ImageView src = new ImageView();
            src.setImage(new Image("lecteuraudio/vue/playlist.png"));
            src.setFitHeight(16);
            src.setFitWidth(16);
            setGraphic(src);
        }  
        
    }
}
