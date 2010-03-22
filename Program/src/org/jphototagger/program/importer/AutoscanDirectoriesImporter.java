/*
 * @(#)AutoscanDirectoriesImporter.java    Created on 2010-03-02
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
import org.jphototagger.program.database.DatabaseAutoscanDirectories;
import org.jphototagger.program.exporter.AutoscanDirectoriesExporter;
import org.jphototagger.program.exporter.AutoscanDirectoriesExporter
    .CollectionWrapper;
import org.jphototagger.program.exporter.StringWrapper;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class AutoscanDirectoriesImporter implements Importer {
    public static final AutoscanDirectoriesImporter INSTANCE =
        new AutoscanDirectoriesImporter();

    private AutoscanDirectoriesImporter() {}

    @Override
    public void importFile(File file) {
        try {
            AutoscanDirectoriesExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    AutoscanDirectoriesExporter.CollectionWrapper.class);

            for (StringWrapper stringWrapper : wrapper.getCollection()) {
                if (!DatabaseAutoscanDirectories.INSTANCE.exists(
                        new File(stringWrapper.getString()))) {
                    DatabaseAutoscanDirectories.INSTANCE.insert(
                        new File(stringWrapper.getString()));
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(AutoscanDirectoriesImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return AutoscanDirectoriesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return AutoscanDirectoriesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return AutoscanDirectoriesExporter.INSTANCE.getDefaultFilename();
    }
}
