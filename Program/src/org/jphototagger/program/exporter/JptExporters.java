package org.jphototagger.program.exporter;

import java.util.ArrayList;
import java.util.List;

/**
 * All exporters exporting JPhotoTagger data.
 *
 * @author Elmar Baumann
 */
public final class JptExporters {
    public static final JptExporters INSTANCE = new JptExporters();
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
        EXPORTERS.add(UserDefinedFileFilterExporter.INSTANCE);
    }

    public static List<Exporter> get() {
        return new ArrayList<Exporter>(EXPORTERS);
    }

    private JptExporters() {}
}
