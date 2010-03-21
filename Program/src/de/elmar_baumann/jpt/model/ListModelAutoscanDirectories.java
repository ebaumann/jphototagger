/*
 * @(#)ListModelAutoscanDirectories.java    Created on 2008-10-05
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

package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.jpt.event.listener.DatabaseAutoscanDirectoriesListener;

import java.io.File;

import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Elements are directory {@link File}s retrieved through
 * {@link DatabaseAutoscanDirectories#getAll()}.
 *
 * These directorys shall be scanned automatically for updates.
 *
 * @author  Elmar Baumann, Tobias Stening
 */
public final class ListModelAutoscanDirectories extends DefaultListModel
        implements DatabaseAutoscanDirectoriesListener {
    private static final long serialVersionUID = 5568827666022563702L;

    public ListModelAutoscanDirectories() {
        addElements();
        DatabaseAutoscanDirectories.INSTANCE.addListener(this);
    }

    private void addElements() {
        List<File> directories = DatabaseAutoscanDirectories.INSTANCE.getAll();

        for (File directory : directories) {
            if (directory.isDirectory() && directory.exists()) {
                addElement(directory);
            }
        }
    }

    @Override
    public void directoryInserted(File directory) {
        if (!contains(directory)) {
            addElement(directory);
        }
    }

    @Override
    public void directoryDeleted(File directory) {
        if (contains(directory)) {
            removeElement(directory);
        }
    }
}
