/*
 * @(#)RenameTemplatesExporter.java    Created on 2010-03-02
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.exporter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.RenameTemplate;
import org.jphototagger.program.database.DatabaseRenameTemplates;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.io.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class RenameTemplatesExporter implements Exporter {
    public static final FileFilter FILE_FILTER =
        new FileNameExtensionFilter(
            JptBundle.INSTANCE.getString(
                "RenameTemplatesExporter.DisplayName.FileFilter"), "xml");
    public static final RenameTemplatesExporter INSTANCE =
        new RenameTemplatesExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File xmlFile = FileUtil.getWithSuffixIgnoreCase(file, ".xml");

        try {
            Set<RenameTemplate> templates =
                DatabaseRenameTemplates.INSTANCE.getAll();

            XmlObjectExporter.export(new CollectionWrapper(templates), xmlFile);
        } catch (Exception ex) {
            AppLogger.logSevere(RenameTemplatesExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString(
            "RenameTemplatesExporter.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptRenameTemplates.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "RenameTemplates")
        @XmlElement(type = RenameTemplate.class)
        private final ArrayList<RenameTemplate> collection =
            new ArrayList<RenameTemplate>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<RenameTemplate> collection) {
            this.collection.addAll(collection);
        }

        public List<RenameTemplate> getCollection() {
            return new ArrayList<RenameTemplate>(collection);
        }
    }


    private RenameTemplatesExporter() {}
}
