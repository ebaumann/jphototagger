/*
 * @(#)ProgramsImporter.java    Created on 2010-03-02
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
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilter;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter;
import org.jphototagger.program.exporter.UserDefinedFileFilterExporter
    .CollectionWrapper;

import java.io.File;

import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class UserDefinedFileFilterImporter implements Importer {
    public static final UserDefinedFileFilterImporter INSTANCE =
        new UserDefinedFileFilterImporter();

    @Override
    public void importFile(File file) {
        try {
            CollectionWrapper wrapper =
                (CollectionWrapper) XmlObjectImporter.importObject(file,
                    UserDefinedFileFilterExporter.CollectionWrapper.class);

            for (UserDefinedFileFilter filter : wrapper.getCollection()) {
                if (!DatabaseUserDefinedFileFilter.INSTANCE.exists(
                        filter.getName())) {
                    DatabaseUserDefinedFileFilter.INSTANCE.insert(filter);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(UserDefinedFileFilterImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return UserDefinedFileFilterExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return UserDefinedFileFilterExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return UserDefinedFileFilterExporter.INSTANCE.getDefaultFilename();
    }

    private UserDefinedFileFilterImporter() {}
}
