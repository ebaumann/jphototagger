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
package de.elmar_baumann.imv.event;

/**
 * Files will be checked whether their metadata shall be updated.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-06
 */
public final class CheckForUpdateMetadataEvent {

    /**
     * Check type
     */
    public enum Type {

        /**
         * Check will be started
         */
        CHECK_STARTED,
        /**
         * A file will be checked for update
         */
        CHECKING_FILE,
        /**
         * Check has been finished
         */
        CHECK_FINISHED,
    }
    private final Type type;
    private final String imageFilename;

    public CheckForUpdateMetadataEvent(Type type, String imageFilename) {
        this.type = type;
        this.imageFilename = imageFilename;
    }

    /**
     * Returns the file that will be checked for update.
     *
     * @return file or null when the event is not {@link Type#CHECKING_FILE}
     */
    public String getImageFilename() {
        return imageFilename;
    }

    /**
     * Returns the check type.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }
}
