package de.elmar_baumann.imv.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains instances of all {@link HierarchicalKeywordsImporter}
 * implementations.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public final class HierarchicalKeywordsImporters {

    private static final Set<HierarchicalKeywordsImporter> importers =
            new HashSet<HierarchicalKeywordsImporter>();

    static {
        importers.add(HierarchicalKeywordsImporterLightroom.INSTANCE);
    }

    /**
     * Returns all importers of hierarchical keywords.
     *
     * @return importers
     */
    public static List<HierarchicalKeywordsImporter> getAll() {
        return new ArrayList<HierarchicalKeywordsImporter>(importers);
    }

    private HierarchicalKeywordsImporters() {
    }
}
