/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.cellfactory;

import javafx.scene.control.TableCell;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.Musique;

/**
 *
 * @author nahel
 */
public class NumberColCell extends TableCell<IMusique, IMusique> {
    
    @Override 
    public void updateItem(IMusique item, boolean empty)
    {
        super.updateItem(item, empty);
        if (this.getTableRow() != null && item != null) {
          setText(this.getTableRow().getIndex()+"");
        } else {
          setText("");
        }
    }
      
}
