/*
 * @(#)DatabaseImageCollectionsListener.java    Created on 2010-01-11
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

package de.elmar_baumann.jpt.event.listener;

import java.io.File;
import java.util.List;

/**
 * Listens to events in
 * {@link de.elmar_baumann.jpt.database.DatabaseImageCollections}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseImageCollectionsListener {

    /**
     * Will be called if an image collection was inserted into
     * {@link de.elmar_baumann.jpt.database.DatabaseImageCollections}.
     *
     * @param collectionName     name of the inserted collection
     * @param insertedImageFiles inserted image files
     */
    public void collectionInserted(String collectionName,
                                   List<File> insertedImageFiles);

    /**
     * Will be called if an image collection was deleted from
     * {@link de.elmar_baumann.jpt.database.DatabaseImageCollections}.
     *
     * @param collectionName    name of the deleted collection
     * @param deletedImageFiles deleted image files
     */
    public void collectionDeleted(String collectionName,
                                  List<File> deletedImageFiles);

    /**
     * Will be called if an image collection was renamed from
     * {@link de.elmar_baumann.jpt.database.DatabaseImageCollections}.
     *
     * @param oldName old name of the image collection
     * @param newName new name of the image collection
     */
    public void collectionRenamed(String oldName, String newName);

    /**
     * Will be called if images were inserted into
     * {@link de.elmar_baumann.jpt.database.DatabaseImageCollections}.
     *
     * @param collectionName     name of the image collection
     * @param insertedImageFiles inserted image files
     */
    public void imagesInserted(String collectionName,
                               List<File> insertedImageFiles);

    /**
     * Will be called if images were deleted from
     * {@link de.elmar_baumann.jpt.database.DatabaseImageCollections}.
     *
     * @param collectionName    name of the image collection
     * @param deletedImageFiles deleted image files
     */
    public void imagesDeleted(String collectionName,
                              List<File> deletedImageFiles);
}
