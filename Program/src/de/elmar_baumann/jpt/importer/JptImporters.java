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

import java.util.ArrayList;
import java.util.List;

/**
 * All importers exporting JPhotoTagger data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-03
 */
public final class JptImporters {

    public static final  JptImporters   INSTANCE  = new JptImporters();
    private static final List<Importer> IMPORTERS = new ArrayList<Importer>();

    static {
        // Please add new importes at the end
        IMPORTERS.add(KeywordsImporterJpt.INSTANCE);
        IMPORTERS.add(SynonymsImporter.INSTANCE);
        IMPORTERS.add(RenameTemplatesImporter.INSTANCE);
        IMPORTERS.add(SavedSearchesImporter.INSTANCE);
        IMPORTERS.add(ImageCollectionsImporter.INSTANCE);
        IMPORTERS.add(MetadataTemplatesImporter.INSTANCE);
        IMPORTERS.add(ProgramsImporter.INSTANCE);
        IMPORTERS.add(FavoritesImporter.INSTANCE);
        IMPORTERS.add(AutoscanDirectoriesImporter.INSTANCE);
        IMPORTERS.add(FileExcludePatternsImporter.INSTANCE);
    }

    public static List<Importer> get() {
        return new ArrayList<Importer>(IMPORTERS);
    }

    private JptImporters() {
    }
}
