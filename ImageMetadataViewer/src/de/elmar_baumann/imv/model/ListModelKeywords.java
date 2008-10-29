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
 * Contains all Keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/25
 */
public class ListModelKeywords extends DefaultListModel
    implements DatabaseListener {

    private DatabaseImageFiles db = DatabaseImageFiles.getInstance();

    public ListModelKeywords() {
        addElements();
        db.addDatabaseListener(this);
    }

    private void addElements() {
        Set<String> keywords = db.getDcSubjects();
        for (String keyword : keywords) {
            addElement(keyword);
        }
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (action.isImageModified()) {
            checkForNewKeywords(action.getImageFileData());
        }
    }

    private void checkForNewKeywords(ImageFile imageFileData) {
        List<String> keywords = getKeywords(imageFileData);
        if (keywords != null) {
            for (String keyword : keywords) {
                if (!contains(keyword)) {
                    addElement(keyword);
                }
            }
        }
    }

    private List<String> getKeywords(ImageFile imageFileData) {
        List<String> keywords = new ArrayList<String>();
        Xmp xmpData = imageFileData.getXmp();
        if (xmpData != null && xmpData.getDcSubjects() != null) {
            keywords.addAll(xmpData.getDcSubjects());
        }
        return keywords;
    }
}
