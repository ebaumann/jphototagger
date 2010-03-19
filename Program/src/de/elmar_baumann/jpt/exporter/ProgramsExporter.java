/*
 * @(#)ProgramsExporter.java    Created on 2010-03-04
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

package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.io.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class ProgramsExporter implements Exporter {
    public static final FileFilter FILE_FILTER =
        new FileNameExtensionFilter(
            JptBundle.INSTANCE.getString(
                "ProgramsExporter.DisplayName.FileFilter"), "xml");
    public static final ProgramsExporter INSTANCE = new ProgramsExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");

        try {
            List<Program> programs =
                DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);

            programs.addAll(
                DatabasePrograms.INSTANCE.getAll(
                    DatabasePrograms.Type.PROGRAM));
            XmlObjectExporter.export(new CollectionWrapper(programs), file);
        } catch (Exception ex) {
            AppLogger.logSevere(ProgramsExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString("ProgramsExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptPrograms.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "Programs")
        @XmlElement(type = Program.class)
        private final ArrayList<Program> collection = new ArrayList<Program>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<Program> collection) {
            this.collection.addAll(collection);
        }

        public List<Program> getCollection() {
            return new ArrayList<Program>(collection);
        }
    }


    private ProgramsExporter() {}
}
