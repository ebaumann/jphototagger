/*
 * JPhotoTagger tags and finds images fast
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

import java.util.ArrayList;
import java.util.List;

/**
 * All exporters exporting JPhotoTagger data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-03
 */
public final class JptExporters {

    public static final  JptExporters   INSTANCE  = new JptExporters();
    private static final List<Exporter> EXPORTERS = new ArrayList<Exporter>();

    static {
        // Please add new exportes at the end
        EXPORTERS.add(KeywordsExporterJpt.INSTANCE);
        EXPORTERS.add(SynonymsExporter.INSTANCE);
        EXPORTERS.add(RenameTemplatesExporter.INSTANCE);
        EXPORTERS.add(SavedSearchesExporter.INSTANCE);
        EXPORTERS.add(ImageCollectionsExporter.INSTANCE);
        EXPORTERS.add(MetadataTemplatesExporter.INSTANCE);
        EXPORTERS.add(ProgramsExporter.INSTANCE);
        EXPORTERS.add(FavoritesExporter.INSTANCE);
        EXPORTERS.add(AutoscanDirectoriesExporter.INSTANCE);
        EXPORTERS.add(FileExcludePatternsExporter.INSTANCE);
    }

    public static List<Exporter> get() {
        return new ArrayList<Exporter>(EXPORTERS);
    }

    private JptExporters() {
    }
}
