/*
 * @(#)ImageCollectionsExporter.java    Created on 2010-03-02
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

package org.jphototagger.program.exporter;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.ImageCollection;
import org.jphototagger.program.database.DatabaseImageCollections;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.io.FileUtil;

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
public final class ImageCollectionsExporter implements Exporter {
    public static final FileFilter FILE_FILTER =
        new FileNameExtensionFilter(
            JptBundle.INSTANCE.getString(
                "ImageCollectionsExporter.DisplayName.FileFilter"), "xml");
    public static final ImageCollectionsExporter INSTANCE =
        new ImageCollectionsExporter();

    @Override
    public void exportFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        file = FileUtil.getWithSuffixIgnoreCase(file, ".xml");

        try {
            List<ImageCollection> templates =
                DatabaseImageCollections.INSTANCE.getAll2();

            XmlObjectExporter.export(new CollectionWrapper(templates), file);
        } catch (Exception ex) {
            AppLogger.logSevere(ImageCollectionsExporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return JptBundle.INSTANCE.getString(
            "ExportImageCollections.DisplayName");
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_export.png");
    }

    @Override
    public String getDefaultFilename() {
        return "JptImageCollections.xml";
    }

    @XmlRootElement
    public static class CollectionWrapper {
        @XmlElementWrapper(name = "ImageCollections")
        @XmlElement(type = ImageCollection.class)
        private final ArrayList<ImageCollection> collection =
            new ArrayList<ImageCollection>();

        public CollectionWrapper() {}

        public CollectionWrapper(Collection<ImageCollection> collection) {
            this.collection.addAll(collection);
        }

        public List<ImageCollection> getCollection() {
            return new ArrayList<ImageCollection>(collection);
        }
    }


    private ImageCollectionsExporter() {}
}
