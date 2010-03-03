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
        IMPORTERS.add(KeywordImporterJpt.INSTANCE);
        IMPORTERS.add(SynonymsImporter.INSTANCE);
        IMPORTERS.add(RenameTemplatesImporter.INSTANCE);
        IMPORTERS.add(SavedSearchesImporter.INSTANCE);
    }

    public static List<Importer> get() {
        return new ArrayList<Importer>(IMPORTERS);
    }

    private JptImporters() {
    }
}
