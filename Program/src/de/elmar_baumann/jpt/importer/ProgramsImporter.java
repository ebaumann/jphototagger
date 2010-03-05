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
package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.exporter.ProgramsExporter;
import de.elmar_baumann.jpt.exporter.ProgramsExporter.CollectionWrapper;
import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-02
 */
public final class ProgramsImporter implements Importer {

    public static final ProgramsImporter INSTANCE = new ProgramsImporter();

    @Override
    public void importFile(File file) {
        try {
            ProgramsExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, ProgramsExporter.CollectionWrapper.class);

            for (Program program : wrapper.getCollection()) {
                if (!DatabasePrograms.INSTANCE.exists(program)) {
                    DatabasePrograms.INSTANCE.insert(program);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(ProgramsImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return ProgramsExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return ProgramsExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return ProgramsExporter.INSTANCE.getDefaultFilename();
    }

    private ProgramsImporter() {
    }
}
