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
        EXPORTERS.add(KeywordExporterJpt.INSTANCE);
        EXPORTERS.add(SynonymsExporter.INSTANCE);
        EXPORTERS.add(RenameTemplatesExporter.INSTANCE);
        EXPORTERS.add(SavedSearchesExporter.INSTANCE);
    }

    public static List<Exporter> get() {
        return new ArrayList<Exporter>(EXPORTERS);
    }

    private JptExporters() {
    }
}
