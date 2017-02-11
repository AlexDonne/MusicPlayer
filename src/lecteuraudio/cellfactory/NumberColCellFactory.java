/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.cellfactory;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.Musique;

/**
 *
 * @author nahel
 */
public class NumberColCellFactory implements Callback<TableColumn<IMusique, IMusique>, TableCell<IMusique, IMusique>> {
    /** 
     * fabrique une cellule pour le TreeView
     * @param param le TreeView à mettre à jour
     * @return une cellule avec le bon format
     */
    
     @Override 
     public TableCell<IMusique, IMusique> call(TableColumn<IMusique, IMusique> param) {
         NumberColCell cell=new NumberColCell();
         return cell;
    }
}
