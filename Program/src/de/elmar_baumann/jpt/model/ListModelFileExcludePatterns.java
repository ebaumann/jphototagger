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
 * Element are {@link String}s retrieved through
 * {@link DatabaseFileExcludePattern#getAll()}.
 *
 * Filenames matching these patterns (strings) shall not be handled by
 * <strong>JPhotoTagger</strong>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-09
 */
public final class ListModelFileExcludePatterns extends DefaultListModel {

    private static final    long                       serialVersionUID = -8337739189362442866L;
    private final transient DatabaseFileExcludePattern db               = DatabaseFileExcludePattern.INSTANCE;
    private                 List<String>               patterns;

    public ListModelFileExcludePatterns() {
        addElements();
    }

    public List<String> getPatterns() {
        return new ArrayList<String>(patterns);
    }

    public void insert(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.exists(trimmedPattern)) {
            errorMessageExists(trimmedPattern);
        }
        if (db.insert(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            errorMessageInsert(trimmedPattern);
        }
    }

    public void delete(String pattern) {
        String trimmedPattern = pattern.trim();
        if (db.delete(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessageDelete(trimmedPattern);
        }
    }

    private void addElements() {
        patterns = db.getAll();
        for (String pattern : patterns) {
            addElement(pattern);
        }
    }

    private void errorMessageDelete(String trimmedPattern) {
        MessageDisplayer.error(null, "ListModelFileExcludePatterns.Error.Delete", trimmedPattern);
    }

    private void errorMessageInsert(String trimmedPattern) {
        MessageDisplayer.error(null, "ListModelFileExcludePatterns.Error.InsertPattern.Add", trimmedPattern);
    }

    private void errorMessageExists(String trimmedPattern) {
        MessageDisplayer.error(null, "ListModelFileExcludePatterns.Error.InsertPattern.Exists", trimmedPattern);
    }
}
