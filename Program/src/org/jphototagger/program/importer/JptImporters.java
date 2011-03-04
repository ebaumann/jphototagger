package org.jphototagger.program.importer;

import java.util.ArrayList;
import java.util.List;

/**
 * All importers exporting JPhotoTagger data.
 *
 * @author Elmar Baumann
 */
public final class JptImporters {
    public static final JptImporters INSTANCE = new JptImporters();
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
        IMPORTERS.add(UserDefinedFileFilterImporter.INSTANCE);
    }

    public static List<Importer> get() {
        return new ArrayList<Importer>(IMPORTERS);
    }

    private JptImporters() {}
}
