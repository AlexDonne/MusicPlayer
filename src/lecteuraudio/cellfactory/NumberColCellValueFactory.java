/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.cellfactory;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import lecteuraudio.metier.IMusique;
import lecteuraudio.metier.Musique;

/**
 *
 * @author nahel
 */
public class NumberColCellValueFactory implements Callback<CellDataFeatures<IMusique, IMusique>, ObservableValue<IMusique>> {

    @Override 
    public ObservableValue<IMusique> call(CellDataFeatures<IMusique, IMusique> p) {
        return new ReadOnlyObjectWrapper(p.getValue());
    }
    
  }
  

