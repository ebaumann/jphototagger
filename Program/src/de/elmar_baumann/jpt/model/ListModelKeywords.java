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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.DatabaseStatistics;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;

/**
 * Elements are keyword {@link String}s retrieved through
 * {@link DatabaseImageFiles#getAllDcSubjects()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-25
 */
public final class ListModelKeywords extends DefaultListModel implements DatabaseImageFilesListener {

    private static final    long               serialVersionUID = -9181622876402951455L;
    private final transient DatabaseImageFiles db               = DatabaseImageFiles.INSTANCE;

    public ListModelKeywords() {
        addElements();
        db.addListener(this);
    }

    private void addElements() {
        Set<String> keywords = db.getAllDcSubjects();
        for (String keyword : keywords) {
            addElement(keyword);
        }
    }

    /**
     * Returns whether a keyword existsValueIn whithin this model, does <em>not</em>
     * check the database.
     *
     * @param  keyword keywords
     * @return         true if this model contains that keyword
     */
    public synchronized boolean exists(String keyword) {
        return contains(keyword);
    }

    /**
     * Renames a keyword whithin this model, does <em>not</em> update the
     * database or sidecar files.
     *
     * @param  oldName old keyword name
     * @param  newName new keyword name
     * @return         true if renamed
     */
    public synchronized boolean rename(String oldName, String newName) {
        assert !oldName.equals(newName);
        int index = indexOf(oldName);
        if (index < 0) return false;
        remove(index);
        add(index, newName);
        return true;
    }

    /**
     * Removes a keyword from this model, does <em>not</em> update the dataase
     * or sidecar files.
     *
     * @param  keyword keyword
     * @return         true if removed
     */
    public synchronized boolean delete(String keyword) {
        int index = indexOf(keyword);
        if (index < 0) return false;
        remove(index);
        return true;
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        if (event.isTextMetadataAffected()) {
            checkForNewKeywords(event.getImageFile());
            removeNotExistingKeywords(event.getOldImageFile());
        }
    }

    private void checkForNewKeywords(final ImageFile imageFile) {
        List<String> keywords = getKeywords(imageFile);
        for (String keyword : keywords) {
            if (!contains(keyword)) {
                addElement(keyword);
            }
        }
    }

    private void removeNotExistingKeywords(final ImageFile imageFile) {
        if (imageFile == null) return;
        List<String> keywords = getKeywords(imageFile);
        for (String keyword : keywords) {
            if (contains(keyword) && !databaseHasKeyword(keyword)) {
                removeElement(keyword);
            }
        }
    }

    boolean databaseHasKeyword(String keyword) {
        return DatabaseStatistics.INSTANCE.existsValueIn(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
    }

    @SuppressWarnings("unchecked")
    private List<String> getKeywords(ImageFile imageFile) {
        List<String> keywords = new ArrayList<String>();
        Xmp          xmp      = imageFile.getXmp();
        if (xmp != null && xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            keywords.addAll((List<String>)xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE));
        }
        return keywords;
    }
}
