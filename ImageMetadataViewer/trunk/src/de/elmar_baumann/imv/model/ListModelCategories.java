package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.tasks.ListModelElementRemover;
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
public final class ListModelCategories extends DefaultListModel
        implements DatabaseListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private ListModelElementRemover remover;

    public ListModelCategories() {
        addElements();
        createRemover();
        db.addDatabaseListener(this);
    }

    private void createRemover() {
        List<Column> columns = new ArrayList<Column>();
        columns.add(ColumnXmpPhotoshopCategory.INSTANCE);
        columns.add(
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        remover = new ListModelElementRemover(this, columns);
    }

    private void addElements() {
        Set<String> categories = db.getCategories();
        for (String category : categories) {
            addElement(category);
        }
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.isTextMetadataAffected()) {
            checkForNewCategories(event.getImageFile());
            remover.removeNotExistingElements();
        }
    }

    private void checkForNewCategories(ImageFile imageFile) {
        List<String> categories = getCategories(imageFile);
        synchronized (this) {
            for (String category : categories) {
                if (!contains(category)) {
                    addElement(category);
                }
            }
        }
    }

    private List<String> getCategories(ImageFile imageFile) {
        List<String> categories = new ArrayList<String>();
        Xmp xmpData = imageFile.getXmp();
        if (xmpData != null && xmpData.getPhotoshopSupplementalCategories() !=
                null) {
            categories.addAll(xmpData.getPhotoshopSupplementalCategories());
        }
        if (xmpData != null && xmpData.getPhotoshopCategory() != null) {
            categories.add(xmpData.getPhotoshopCategory());
        }
        return categories;
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // nothing to do
    }
}
