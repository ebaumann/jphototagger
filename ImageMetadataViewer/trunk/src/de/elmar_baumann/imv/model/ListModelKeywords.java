package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.tasks.ListModelElementRemover;
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
public final class ListModelKeywords extends DefaultListModel
        implements DatabaseListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private ListModelElementRemover remover;

    public ListModelKeywords() {
        addElements();
        remover = new ListModelElementRemover(this,
                ColumnXmpDcSubjectsSubject.INSTANCE);
        db.addDatabaseListener(this);
    }

    private void addElements() {
        Set<String> keywords = db.getDcSubjects();
        for (String keyword : keywords) {
            addElement(keyword);
        }
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.isTextMetadataAffected()) {
            checkForNewKeywords(event.getImageFile());
            remover.removeNotExistingElements();
        }
    }

    private void checkForNewKeywords(ImageFile imageFile) {
        List<String> keywords = getKeywords(imageFile);
        for (String keyword : keywords) {
            if (!contains(keyword)) {
                addElement(keyword);
            }
        }
    }

    private List<String> getKeywords(ImageFile imageFile) {
        List<String> keywords = new ArrayList<String>();
        Xmp xmpData = imageFile.getXmp();
        if (xmpData != null && xmpData.getDcSubjects() != null) {
            keywords.addAll(xmpData.getDcSubjects());
        }
        return keywords;
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // nothing to do
    }
}
