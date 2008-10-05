package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Model mit Verzeichnissen, die automatisch nach Metadataten gescannt werden
 * sollen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ListModelAutoscanDirectories extends DefaultListModel {

    public ListModelAutoscanDirectories() {
        addItems();
    }

    private void addItems() {
        Database db = Database.getInstance();
        List<String> directoryNames = db.getAutoscanDirectories();
        for (String directoryName : directoryNames) {
            File directory = new File(directoryName);
            if (directory.isDirectory() && directory.exists()) {
                addElement(directory);
            }
        }
    }
}
