package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * Contains all Keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/25
 */
public final class ListModelKeywords extends DefaultListModel
        implements DatabaseListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

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
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.isTextMetadataAffected()) {
            checkForNewKeywords(event.getImageFile());
            removeNotExistingKeywords(event.getOldImageFile());
        }
    }

    private void checkForNewKeywords(final ImageFile imageFile) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                List<String> keywords = getKeywords(imageFile);
                for (String keyword : keywords) {
                    if (!contains(keyword)) {
                        addElement(keyword);
                    }
                }
            }
        });
    }

    private void removeNotExistingKeywords(final ImageFile imageFile) {
        if (imageFile == null) return;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                List<String> keywords = getKeywords(imageFile);
                for (String keyword : keywords) {
                    if (contains(keyword) && !databaseHasKeyword(keyword)) {
                        removeElement(keyword);
                    }
                }
            }
        });
    }

    boolean databaseHasKeyword(String keyword) {
        return DatabaseStatistics.INSTANCE.exists(
                ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
    }

    private List<String> getKeywords(ImageFile imageFile) {
        List<String> keywords = new ArrayList<String>();
        Xmp xmp = imageFile.getXmp();
        if (xmp != null && xmp.getDcSubjects() != null) {
            keywords.addAll(xmp.getDcSubjects());
        }
        return keywords;
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }
}
