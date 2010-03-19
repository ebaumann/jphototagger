/*
 * @(#)FileExcludePatternsImporter.java    Created on 2010-03-02
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

package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.database.DatabaseFileExcludePatterns;
import de.elmar_baumann.jpt.exporter.FileExcludePatternsExporter;
import de.elmar_baumann.jpt.exporter.FileExcludePatternsExporter
    .CollectionWrapper;
import de.elmar_baumann.jpt.exporter.StringWrapper;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class FileExcludePatternsImporter implements Importer {
    public static final FileExcludePatternsImporter INSTANCE =
        new FileExcludePatternsImporter();

    @Override
    public void importFile(File file) {
        try {
            FileExcludePatternsExporter.CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    FileExcludePatternsExporter.CollectionWrapper.class);

            for (StringWrapper stringWrapper : wrapper.getCollection()) {
                if (!DatabaseFileExcludePatterns.INSTANCE.exists(
                        stringWrapper.getString())) {
                    DatabaseFileExcludePatterns.INSTANCE.insert(
                        stringWrapper.getString());
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(FileExcludePatternsImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FileExcludePatternsExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return FileExcludePatternsExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return FileExcludePatternsExporter.INSTANCE.getDefaultFilename();
    }

    private FileExcludePatternsImporter() {}
}
