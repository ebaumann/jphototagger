package org.jphototagger.program.importer;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains instances of all {@link Importer}s importing keywords for external
 * applications (<em>not</em> for JPhotoTagger).
 *
 * @author Elmar Baumann
 */
public final class KeywordImporters {
    private static final List<KeywordsImporter> importers = new ArrayList<KeywordsImporter>();

    static {
        importers.add(KeywordsImporterLightroom.INSTANCE);
    }

    /**
     * Returns all importers of keywords.
     *
     * @return importers
     */
    public static List<KeywordsImporter> getAll() {
        return new ArrayList<KeywordsImporter>(importers);
    }

    private KeywordImporters() {}
}
