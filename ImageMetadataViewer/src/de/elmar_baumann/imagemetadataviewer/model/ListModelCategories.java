package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.data.ImageFile;
import de.elmar_baumann.imagemetadataviewer.data.Iptc;
import de.elmar_baumann.imagemetadataviewer.data.Xmp;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseAction;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Enthält Kategorien.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ListModelCategories extends DefaultListModel
    implements DatabaseListener {

    private Database db = Database.getInstance();

    public ListModelCategories() {
        addElements();
        db.addDatabaseListener(this);
    }

    private void addElements() {
        LinkedHashSet<String> categories = db.getCategories();
        for (String category : categories) {
            addElement(category);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type type = action.getType();
        if (type.equals(DatabaseAction.Type.ImageFileInserted)) {
            checkForNewCategories(action.getImageFileData());
        } else if (type.equals(DatabaseAction.Type.ImageFilesDeleted)) {
            //checkForNotExistingCategories(); // Performance ?
        } else if (type.equals(DatabaseAction.Type.ImageFileUpdated)) {
            checkForNewCategories(action.getImageFileData());
        //checkForNotExistingCategories(); // Performance ?
        }
    }

    private void checkForNewCategories(ImageFile imageFileData) {
        List<String> categories = getCategories(imageFileData);
        for (String category : categories) {
            if (!contains(category)) {
                addElement(category);
            }
        }
    }

    private void checkForNotExistingCategories() {
        for (int i = 0; i < getSize(); i++) {
            String category = (String) get(i);
            if (!db.existsCategory(category)) {
                remove(i);
            }
        }
    }

    private List<String> getCategories(ImageFile imageFileData) {
        List<String> categories = new ArrayList<String>();
        Iptc iptcData = imageFileData.getIptc();
        Xmp xmpData = imageFileData.getXmp();
        if (iptcData != null && iptcData.getSupplementalCategories() != null) {
            categories.addAll(iptcData.getSupplementalCategories());
        }
        if (xmpData != null && xmpData.getPhotoshopSupplementalCategories() != null) {
            categories.addAll(xmpData.getPhotoshopSupplementalCategories());
        }
        if (xmpData != null && xmpData.getPhotoshopCategory() != null) {
            categories.add(xmpData.getPhotoshopCategory());
        }
        return categories;
    }
}
