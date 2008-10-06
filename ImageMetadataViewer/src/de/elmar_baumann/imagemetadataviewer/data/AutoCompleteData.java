package de.elmar_baumann.imagemetadataviewer.data;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Model for Autocomplete.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class AutoCompleteData {

    private Database db = Database.getInstance();
    private LinkedHashSet<Column> columns;
    private List<String> content = Collections.synchronizedList(new ArrayList<String>());

    /**
     * Standardkonstruktor.
     * 
     * Liest die Schnellsuchspalten ein mit
     * {@link de.elmar_baumann.imagemetadataviewer.UserSettings#getFastSearchColumns()}.
     */
    public AutoCompleteData() {
        columns = new LinkedHashSet<Column>(UserSettings.getInstance().getFastSearchColumns());
        addColumnsContent();
    }

    /**
     * Konstruktor.
     * 
     * @param column  Tabellenspalte, deren Inhalt für Autocomplete benötigt
     *                wird
     */
    public AutoCompleteData(Column column) {
        LinkedHashSet<Column> cols = new LinkedHashSet<Column>();
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
        content.addAll(db.getContent(columns));
    }

    public List<String> getList() {
        return content;
    }
}
