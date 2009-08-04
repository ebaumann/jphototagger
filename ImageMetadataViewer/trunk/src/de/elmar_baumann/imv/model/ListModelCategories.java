package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * Enthält Kategorien.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ListModelCategories extends DefaultListModel
        implements DatabaseListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

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
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.isTextMetadataAffected()) {
            checkForNewCategories(event.getImageFile());
            removeNotExistingCategories(event.getOldImageFile());
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

    private void removeNotExistingCategories(final ImageFile imageFile) {
        if (imageFile == null) return;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                List<String> categories = getCategories(imageFile);
                for (String category : categories) {
                    if (contains(category) && !databaseHasCategory(category)) {
                        removeElement(category);
                    }
                }
            }
        });
    }

    private boolean databaseHasCategory(String category) {
        return DatabaseStatistics.INSTANCE.exists(
                ColumnXmpPhotoshopCategory.INSTANCE, category) ||
                DatabaseStatistics.INSTANCE.exists(
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE,
                category);
    }

    private List<String> getCategories(ImageFile imageFile) {
        List<String> categories = new ArrayList<String>();
        Xmp xmp = imageFile.getXmp();
        if (xmp != null && xmp.getPhotoshopSupplementalCategories() !=
                null) {
            categories.addAll(xmp.getPhotoshopSupplementalCategories());
        }
        if (xmp != null && xmp.getPhotoshopCategory() != null) {
            categories.add(xmp.getPhotoshopCategory());
        }
        return categories;
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
