/*
 * @(#)SavedSearchesImporter.java    Created on 2010-03-02
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

package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.event.SearchEvent;
import org.jphototagger.program.exporter.SavedSearchesExporter;
import org.jphototagger.program.exporter.SavedSearchesExporter.CollectionWrapper;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class SavedSearchesImporter implements Importer {
    public static final SavedSearchesImporter INSTANCE =
        new SavedSearchesImporter();

    @Override
    public void importFile(File file) {
        try {
            SavedSearchesExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    SavedSearchesExporter.CollectionWrapper.class);

            for (SavedSearch savedSearch : wrapper.getCollection()) {
                if (!DatabaseSavedSearches.INSTANCE.exists(
                        savedSearch.getName())) {
                    if (DatabaseSavedSearches.INSTANCE.insertOrUpdate(
                            savedSearch)) {
                        notifySearchListeners(savedSearch);
                    }
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(SavedSearchesImporter.class, ex);
        }
    }

    private void notifySearchListeners(SavedSearch savedSearch) {
        SearchEvent evt = new SearchEvent(SearchEvent.Type.SAVE);

        evt.setData(savedSearch);
        evt.setSearchName(savedSearch.getName());
        evt.setForceOverwrite(true);
        AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel().notify(evt);
    }

    @Override
    public FileFilter getFileFilter() {
        return SavedSearchesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return SavedSearchesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return SavedSearchesExporter.INSTANCE.getDefaultFilename();
    }

    private SavedSearchesImporter() {}
}
