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

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseFileExcludePattern;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-09
 */
public final class ListModelFileExcludePatterns extends DefaultListModel {

    private final DatabaseFileExcludePattern db =
            DatabaseFileExcludePattern.INSTANCE;
    private List<String> patterns;

    public ListModelFileExcludePatterns() {
        addElements();
    }

    public List<String> getPatterns() {
        return new ArrayList<String>(patterns);
    }

    public void insertPattern(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.existsFileExcludePattern(trimmedPattern)) {
            MessageDisplayer.error(null,
                    "ListModelFileExcludePatterns.Error.InsertPattern.Exists", // NOI18N
                    trimmedPattern);
        }
        if (db.insertFileExcludePattern(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            MessageDisplayer.error(null,
                    "ListModelFileExcludePatterns.Error.InsertPattern.Add", // NOI18N
                    trimmedPattern);
        }
    }

    public void deletePattern(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.deleteFileExcludePattern(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            MessageDisplayer.error(null,
                    "ListModelFileExcludePatterns.Error.Delete", // NOI18N
                    trimmedPattern);
        }
    }

    private void addElements() {
        patterns = db.getFileExcludePatterns();
        for (String pattern : patterns) {
            addElement(pattern);
        }
    }
}
