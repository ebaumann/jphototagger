package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import java.util.Vector;
import javax.swing.DefaultListModel;

/**
 * Enthält die Namen aller Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/08
 */
public class ListModelImageCollections extends DefaultListModel {

    public ListModelImageCollections() {
        addItems();
    }

    private void addItems() {
        Database db = Database.getInstance();
        if (db.isConnected()) {
            Vector<String> collections = db.getImageCollectionNames();
            for (String collection : collections) {
                addElement(collection);
            }
        }
    }
}
