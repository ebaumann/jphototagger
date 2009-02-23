package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.comparator.ComparatorStringAscending;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.tasks.ListModelElementRemover;
import de.elmar_baumann.lib.componentutil.ListUtil;
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
        columns.add(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        remover = new ListModelElementRemover(this, columns);
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
            remover.removeNotExistingElements();
        }
    }

    private void checkForNewCategories(ImageFile imageFileData) {
        List<String> categories = getCategories(imageFileData);
        synchronized (this) {
            for (String category : categories) {
                if (!contains(category)) {
                    ListUtil.insertSorted(this, category, ComparatorStringAscending.IGNORE_CASE);
                }
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
