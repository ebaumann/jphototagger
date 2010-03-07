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

package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseFileExcludePatterns;
import de.elmar_baumann.jpt.event.DatabaseFileExcludePatternsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseFileExcludePatternsListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Element are {@link String}s retrieved through
 * {@link DatabaseFileExcludePatterns#getAll()}.
 *
 * Filenames matching these patterns (strings) shall not be handled by
 * <strong>JPhotoTagger</strong>.
 *
 * @author  Elmar Baumann
 * @version 2008-10-09
 */
public final class ListModelFileExcludePatterns extends DefaultListModel
        implements DatabaseFileExcludePatternsListener {
    private static final long                           serialVersionUID =
        -8337739189362442866L;
    private final transient DatabaseFileExcludePatterns db               =
        DatabaseFileExcludePatterns.INSTANCE;
    private List<String>      patterns;
    private transient boolean listenToDb = true;

    public ListModelFileExcludePatterns() {
        addElements();
        db.addListener(this);
    }

    public List<String> getPatterns() {
        return new ArrayList<String>(patterns);
    }

    public void insert(String pattern) {
        listenToDb = false;

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

        listenToDb = true;
    }

    public void delete(String pattern) {
        listenToDb = false;

        String trimmedPattern = pattern.trim();

        if (db.delete(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessageDelete(trimmedPattern);
        }

        listenToDb = true;
    }

    private void addElements() {
        patterns = db.getAll();

        for (String pattern : patterns) {
            addElement(pattern);
        }
    }

    private void errorMessageDelete(String trimmedPattern) {
        MessageDisplayer.error(null,
                               "ListModelFileExcludePatterns.Error.Delete",
                               trimmedPattern);
    }

    private void errorMessageInsert(String trimmedPattern) {
        MessageDisplayer.error(
            null, "ListModelFileExcludePatterns.Error.InsertPattern.Add",
            trimmedPattern);
    }

    private void errorMessageExists(String trimmedPattern) {
        MessageDisplayer.error(
            null, "ListModelFileExcludePatterns.Error.InsertPattern.Exists",
            trimmedPattern);
    }

    @Override
    public void actionPerformed(DatabaseFileExcludePatternsEvent evt) {
        if (!listenToDb) {
            return;
        }

        String pattern = evt.getPattern();

        if (evt.isPatternInserted()) {
            addElement(pattern);
            patterns.add(pattern);
        } else if (evt.isPatternDeleted()) {
            removeElement(pattern);
            patterns.remove(pattern);
        }
    }
}
