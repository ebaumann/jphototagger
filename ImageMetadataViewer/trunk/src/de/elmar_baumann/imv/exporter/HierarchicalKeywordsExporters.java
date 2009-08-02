package de.elmar_baumann.imv.exporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains instances of all {@link HierarchicalKeywordsExporter}
 * implementations.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public final class HierarchicalKeywordsExporters {

    private static final Set<HierarchicalKeywordsExporter> exporters =
            new HashSet<HierarchicalKeywordsExporter>();

    static {
        exporters.add(HierarchicalKeywordsExporterLightroom.INSTANCE);
    }

    /**
     * Returns all exporters of hierarchical keywords.
     *
     * @return exporters
     */
    public static List<HierarchicalKeywordsExporter> getAll() {
        return new ArrayList<HierarchicalKeywordsExporter>(exporters);
    }

    private HierarchicalKeywordsExporters() {
    }
}
