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
package de.elmar_baumann.jpt.types;

import java.io.File;

/**
 * Edits a file.
 *
 * @author  Elmar Baumann
 * @version 2009-05-22
 */
public class FileEditor {

    private volatile boolean confirmOverwrite;

    public void setConfirmOverwrite(boolean overwrite) {
        this.confirmOverwrite = overwrite;
    }

    public boolean getConfirmOverwrite() {
        return confirmOverwrite;
    }

    /**
     * Does nothing. Specialized classes can do here something.
     *
     * @param file  file to edit
     */
    public void edit(File file) {
    }
}
