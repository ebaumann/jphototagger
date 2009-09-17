/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
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
 * Contains all Keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-25
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

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
