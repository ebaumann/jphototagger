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

import de.elmar_baumann.jpt.database.DatabaseAutoscanDirectories;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Elements are directory {@link File}s retrieved through
 * {@link DatabaseAutoscanDirectories#getAll()}.
 *
 * These directorys shall be scanned automatically for updates.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ListModelAutoscanDirectories extends DefaultListModel {

    private static final long serialVersionUID = 5568827666022563702L;

    public ListModelAutoscanDirectories() {
        addElements();
    }

    private void addElements() {
        List<String> directoryNames = DatabaseAutoscanDirectories.INSTANCE.getAll();
        for (String directoryName : directoryNames) {
            File directory = new File(directoryName);
            if (directory.isDirectory() && directory.exists()) {
                addElement(directory);
            }
        }
    }
}
