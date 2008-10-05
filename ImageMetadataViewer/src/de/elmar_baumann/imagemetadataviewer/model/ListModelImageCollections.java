package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.database.Database;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Enthält die Namen aller Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ListModelImageCollections extends DefaultListModel {

    public ListModelImageCollections() {
        addItems();
    }

    private void addItems() {
        Database db = Database.getInstance();
        List<String> collections = db.getImageCollectionNames();
        for (String collection : collections) {
            addElement(collection);
        }
    }
}
