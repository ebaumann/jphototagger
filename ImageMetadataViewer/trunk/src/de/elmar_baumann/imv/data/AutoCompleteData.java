package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.database.DatabaseContent;
import de.elmar_baumann.imv.database.metadata.Column;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains autocomplete data (words, terms).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class AutoCompleteData {

    private final DatabaseContent db = DatabaseContent.INSTANCE;
    private final Set<Column> columns;
    private final List<String> content =
            Collections.synchronizedList(new LinkedList<String>());

    /**
     * Creates a new instance of this class.
     * 
     * @param columns columns. All words of
     *               {@link DatabaseContent#getContent(de.elmar_baumann.imv.database.metadata.Column)}
     *               will be added to the autocomplete data.
     */
    AutoCompleteData(Collection<? extends Column> columns) {
        this.columns = new LinkedHashSet<Column>(columns);
        content.addAll(db.getContent(this.columns));
    }

    /**
     * Adds a string to the autocomplete data if it does not already exist.
     * 
     * @param string new string
     */
    void addString(String string) {
        synchronized (content) {
            if (!content.contains(string)) {
                content.add(string);
            }
        }
    }

    /**
     * Returns a <strong>reference</strong> to the list with the autocomplete
     * data.
     *
     * @return autocomplete data
     */
    public List<String> getData() {
        return content;
    }
}
