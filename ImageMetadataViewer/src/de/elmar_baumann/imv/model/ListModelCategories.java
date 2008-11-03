package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;

/**
 * Enthält Kategorien.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ListModelCategories extends DefaultListModel
    implements DatabaseListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();

    public ListModelCategories() {
        addElements();
        db.addDatabaseListener(this);
    }

    private void addElements() {
        Set<String> categories = db.getCategories();
        for (String category : categories) {
            addElement(category);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (action.isImageModified() && action.getImageFileData() != null) {
            checkForNewCategories(action.getImageFileData());
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

    private List<String> getCategories(ImageFile imageFileData) {
        List<String> categories = new ArrayList<String>();
        Xmp xmpData = imageFileData.getXmp();
        if (xmpData != null && xmpData.getPhotoshopSupplementalCategories() != null) {
            categories.addAll(xmpData.getPhotoshopSupplementalCategories());
        }
        if (xmpData != null && xmpData.getPhotoshopCategory() != null) {
            categories.add(xmpData.getPhotoshopCategory());
        }
        return categories;
    }
}
