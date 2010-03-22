/*
 * @(#)ControllerKeywordsDbUpdates.java    Created on 2009-12-17
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

package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to database updates and adds not existing keywords.
 *
 * @author  Elmar Baumann
 */
public final class ControllerKeywordsDbUpdates
        implements DatabaseImageFilesListener {
    public ControllerKeywordsDbUpdates() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    @SuppressWarnings("unchecked")
    private void addNotExistingKeywords(Xmp xmp) {
        Object o = xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE);

        if (o instanceof List<?>) {
            addNotExistingKeywords((List<String>) o);
        }
    }

    private void addNotExistingKeywords(Collection<? extends String> keywords) {
        for (String keyword : keywords) {
            if (!DatabaseKeywords.INSTANCE.exists(keyword)) {
                addKeyword(keyword);
            }
        }
    }

    private void addKeyword(String keyword) {
        TreeModelKeywords model =
            ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

        model.insert((DefaultMutableTreeNode) model.getRoot(), keyword, true);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        addNotExistingKeywords(updatedXmp);
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        addNotExistingKeywords(Collections.singleton(dcSubject));
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        addNotExistingKeywords(xmp);
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
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {

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
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }
}
