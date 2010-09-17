/*
 * @(#)ControllerThumbnailsDatabaseChanges.java    Created on 2008-10-15
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.cache.ThumbnailCache;
import org.jphototagger.program.cache.XmpCache;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;

import java.awt.EventQueue;

import java.io.File;

import java.util.Collections;
import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailsDatabaseChanges
        implements DatabaseImageFilesListener {
    public ControllerThumbnailsDatabaseChanges() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    private void updateXmpCache(final File imageFile) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                XmpCache.INSTANCE.remove(imageFile);
                XmpCache.INSTANCE.notifyUpdate(imageFile);
            }
        });
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateXmpCache(imageFile);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateXmpCache(imageFile);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        updateXmpCache(imageFile);
    }

    @Override
    public void thumbnailUpdated(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        final File file = imageFile;

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ThumbnailCache.INSTANCE.remove(file);
                ThumbnailCache.INSTANCE.notifyUpdate(file);
            }
        });
    }

    @Override
    public void imageFileDeleted(final File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.getThumbnailsPanel().remove(
                    Collections.singleton(imageFile));
            }
        });
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
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }
}
