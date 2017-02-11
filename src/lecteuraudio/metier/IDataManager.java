/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;


/**
 *
 * @author alexd
 */
public interface IDataManager {
    public void charger(IPlayList racine); 
    public void sauver (IPlayList racine); 
}
