package lecteuraudio.cellfactory;

import java.util.List;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.IPlayList;
import lecteuraudio.metier.NoeudMusique;

/**
 *
 * @author nahel
 */

public class TreeItemCellFactory implements Callback<TreeView<NoeudMusique>, TreeCell<NoeudMusique>> {
    /** 
     * fabrique une cellule pour le TreeView
     * @param param le TreeView à mettre à jour
     * @return une cellule avec le bon format
     */
    
    @Override
    public TreeCell<NoeudMusique> call(TreeView<NoeudMusique> param) {
        TreeItemCell treeItem=new TreeItemCell();
        treeItem.setOnDragDropped(e->{onDragDropped(e,treeItem);});
        treeItem.setOnDragOver(e->{onDragOver(treeItem,e);});
        treeItem.setOnDragExited(e->{onDragExited(treeItem);});
        return treeItem;
    }
    
    private void onDragDropped(DragEvent event,TreeItemCell treeItem) {
        if (((TreeItemCell)event.getGestureTarget()).getItem() instanceof IPlayList ){
            ((IPlayList)((TreeItemCell)event.getGestureTarget()).getItem()).getPlayList().addAll((List<IMusique>)((TableView)event.getGestureSource()).getSelectionModel().getSelectedItems());
            treeItem.getTreeView().getSelectionModel().select(treeItem.getTreeItem());
            event.setDropCompleted(true);
            
        }       
        event.consume();
    }
    
    private void onDragOver (TreeItemCell treeItem, DragEvent event){ 
        if (!treeItem.isEmpty()){
            event.acceptTransferModes(TransferMode.COPY);
            treeItem.setBorder(new Border(new BorderStroke(Color.BLACK, 
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        event.consume();
    }
    
    private void onDragExited (TreeItemCell treeItem){ 
        treeItem.setBorder(Border.EMPTY);
       
    }
    

}
