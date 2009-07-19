package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseContent;
import de.elmar_baumann.imv.database.metadata.Column;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Model for Autocomplete.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class AutoCompleteData {

    private final DatabaseContent db = DatabaseContent.INSTANCE;
    private final Set<Column> columns;
    private final List<String> content = Collections.synchronizedList(
            new LinkedList<String>());

    /**
     * Standardkonstruktor.
     * 
     * Liest die Schnellsuchspalten ein mit
     * {@link de.elmar_baumann.imv.UserSettings#getFastSearchColumns()}.
     */
    public AutoCompleteData() {
        columns = new LinkedHashSet<Column>(UserSettings.INSTANCE.
                getFastSearchColumns());
        addColumnsContent();
    }

    /**
     * Konstruktor.
     * 
     * @param column  Tabellenspalte, deren Inhalt für Autocomplete benötigt
     *                wird
     */
    public AutoCompleteData(Column column) {
        Set<Column> cols = new LinkedHashSet<Column>();
        cols.add(column);
        this.columns = cols;
        addColumnsContent();
    }

    /**
     * Adds a string to the autocomplete data if not already exists.
     * 
     * @param string  new string
     */
    public void addString(String string) {
        if (!content.contains(string)) {
            content.add(string);
        }
    }

    private void addColumnsContent() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                content.addAll(db.getContent(columns));
            }
        });
        thread.setName("Adding auto complete data of columns" + " @ " + // NOI18N
                getClass().getName());
        thread.start();

    }

    public List<String> getList() {
        return content;
    }
}
