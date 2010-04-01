/*
 * @(#)ListModelKeywords.java    Created on 2008-10-25
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseStatistics;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;

/**
 * Elements are keyword {@link String}s retrieved through
 * {@link DatabaseImageFiles#getAllDcSubjects()}.
 *
 * @author  Elmar Baumann
 */
public final class ListModelKeywords extends DefaultListModel
        implements DatabaseImageFilesListener {
    private static final long                  serialVersionUID =
        -9181622876402951455L;
    private final transient DatabaseImageFiles db               =
        DatabaseImageFiles.INSTANCE;

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
     * Returns whether a keyword existsValueIn whithin this model, does
     * <em>not</em> check the database.
     *
     * @param  keyword keyword
     * @return         true if this model contains that keyword
     */
    public synchronized boolean exists(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        return contains(keyword);
    }

    /**
     * Renames a keyword whithin this model, does <em>not</em> update the
     * database or sidecar files.
     *
     * @param  fromName old keyword name
     * @param  toName new keyword name
     * @return         true if renamed
     */
    public synchronized boolean rename(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        assert !fromName.equals(toName);

        int index = indexOf(fromName);

        if (index < 0) {
            return false;
        }

        remove(index);
        add(index, toName);

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
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        int index = indexOf(keyword);

        if (index < 0) {
            return false;
        }

        remove(index);

        return true;
    }

    private void addNewKeywords(Collection<? extends String> keywords) {
        for (String keyword : keywords) {
            if (!contains(keyword)) {
                addElement(keyword);
            }
        }
    }

    private void removeKeywordsNotInDb(Collection<? extends String> keywords) {
        for (String keyword : keywords) {
            if (contains(keyword) &&!databaseHasKeyword(keyword)) {
                removeElement(keyword);
            }
        }
    }

    boolean databaseHasKeyword(String keyword) {
        return DatabaseStatistics.INSTANCE.existsValueIn(
            ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
    }

    @SuppressWarnings("unchecked")
    private List<String> getKeywords(Xmp xmp) {
        List<String> keywords = new ArrayList<String>();

        if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            keywords.addAll(
                (List<String>) xmp.getValue(
                    ColumnXmpDcSubjectsSubject.INSTANCE));
        }

        return keywords;
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        addNewKeywords(getKeywords(xmp));
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        removeKeywordsNotInDb(getKeywords(xmp));
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (oldXmp == null) {
            throw new NullPointerException("oldXmp == null");
        }

        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        addNewKeywords(getKeywords(updatedXmp));
        removeKeywordsNotInDb(getKeywords(oldXmp));
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }

        removeKeywordsNotInDb(Collections.singleton(dcSubject));
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }

        addNewKeywords(Collections.singleton(dcSubject));
    }

    @Override
    public void imageFileDeleted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }
}
