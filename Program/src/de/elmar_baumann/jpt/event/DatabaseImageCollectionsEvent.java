/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Image collection database event.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-04
 */
public final class DatabaseImageCollectionsEvent {

    /**
     * Event type.
     */
    public enum Type {

        /**
         * An image collection was deleted
         */
        COLLECTION_DELETED,
        /**
         * An image collection was inserted
         */
        COLLECTION_INSERTED,
        /**
         * Images were inserted into an image collection
         */
        IMAGES_INSERTED,
        /**
         * Images were deleted from an image collection
         */
        IMAGES_DELETED,
    };
    private final String collectionName;
    private final Set<String> filenames;
    private final Type type;

    /**
     * Creates a new event.
     *
     * @param type           event type
     * @param collectionName name of the image collection
     * @param filenames      names of the affected files
     */
    public DatabaseImageCollectionsEvent(Type type,
            String collectionName,
            Collection<? extends String> filenames) {
        this.type = type;
        this.collectionName = collectionName;
        this.filenames = new HashSet<String>(filenames);
    }

    /**
     * Returns the event type.
     *
     * @return event type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the name of the affected image collection.
     *
     * @return name of the image collection
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * Returns the affected image filenames.
     *
     * @return affected image filenames
     */
    public Set<String> getFilenames() {
        return filenames;
    }
}
