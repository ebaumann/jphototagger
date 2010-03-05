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
import de.elmar_baumann.jpt.data.RenameTemplate;
import de.elmar_baumann.jpt.database.DatabaseRenameTemplates;
import de.elmar_baumann.jpt.exporter.RenameTemplatesExporter;
import de.elmar_baumann.jpt.exporter.RenameTemplatesExporter.CollectionWrapper;
import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class RenameTemplatesImporter implements Importer {

    public static final RenameTemplatesImporter INSTANCE = new RenameTemplatesImporter();

    @Override
    public void importFile(File file) {
        try {
            RenameTemplatesExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, RenameTemplatesExporter.CollectionWrapper.class);

            for (RenameTemplate template : wrapper.getCollection()) {
                if (!DatabaseRenameTemplates.INSTANCE.exists(template.getName())) {
                    DatabaseRenameTemplates.INSTANCE.insert(template);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(RenameTemplatesImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return RenameTemplatesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return RenameTemplatesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return RenameTemplatesExporter.INSTANCE.getDefaultFilename();
    }

    private RenameTemplatesImporter() {
    }
}
