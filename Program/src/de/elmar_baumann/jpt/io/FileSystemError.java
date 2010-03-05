/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.io;

import de.elmar_baumann.jpt.resource.JptBundle;

/**
 *
 * @author Elmar Baumann
 */
public enum FileSystemError {
    LOCKED(JptBundle.INSTANCE.getString("FileSystemError.LOCKED")),
    MISSING_PRIVILEGES(
        JptBundle.INSTANCE.getString("FileSystemError.MISSING_PRIVILEGES")),
    MOVE_RENAME_EXISTS(
        JptBundle.INSTANCE.getString("FileSystemError.MOVE_RENAME_EXISTS")),
    READ_ONLY(JptBundle.INSTANCE.getString("FileSystemError.READ_ONLY")),
    UNKNOWN(JptBundle.INSTANCE.getString("FileSystemError.UNKNOWN"));

    private final String message;

    private FileSystemError(String message) {
        this.message = message;
    }

    public String getLocalizedMessage() {
        return message;
    }
}
