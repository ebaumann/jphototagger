package org.jphototagger.program.exporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains instances of all {@link Exporter}s exporting keywords for external
 * applications (<em>not</em> for JPhotoTagger).
 *
 * @author Elmar Baumann
 */
public final class KeywordsExporters {
    private static final List<Exporter> exporters = new ArrayList<Exporter>();

    static {
        exporters.add(KeywordsExporterLightroom.INSTANCE);
    }

    /**
     * Returns all exporters of keywords.
     *
     * @return exporters
     */
    public static List<Exporter> getAll() {
        return new ArrayList<Exporter>(exporters);
    }

    private KeywordsExporters() {}
}
