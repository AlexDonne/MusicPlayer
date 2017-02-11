/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecteuraudio.metier;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author alexd
 */
public class MusiqueFileFilter implements FileFilter {

    private final String[] okFileExtensions = new String[]{"mp3", "mp4", "wav"};

    public MusiqueFileFilter() {
    }

    @Override
    public boolean accept(File file) {
        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

}
