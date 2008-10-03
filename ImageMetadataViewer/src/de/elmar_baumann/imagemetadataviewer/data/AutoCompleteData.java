package de.elmar_baumann.imagemetadataviewer.data;

import de.elmar_baumann.imagemetadataviewer.UserSettings;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import java.util.LinkedHashSet;

/**
 * Model für Autocomplete.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public class AutoCompleteData {

    private Database db = Database.getInstance();
    private LinkedHashSet<Column> columns;
    private LinkedHashSet<String> content = new LinkedHashSet<String>();

    /**
     * Standardkonstruktor.
     * 
     * Liest die Schnellsuchspalten ein mit
     * {@link de.elmar_baumann.imagemetadataviewer.UserSettings#getFastSearchColumns()}.
     */
    public AutoCompleteData() {
        columns = new LinkedHashSet<Column>(UserSettings.getInstance().getFastSearchColumns());
        addColumns();
    }

    /**
     * Konstruktor.
     * 
     * @param columns  Tabellenspalten, deren Inhalt für Autocomplete benötigt
     *                 wird
     */
    public AutoCompleteData(LinkedHashSet<Column> columns) {
        this.columns = columns;
        addColumns();
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
        addColumns();
    }

    private void addColumns() {
        content = db.getContent(columns);
    }

    /**
     * Liefert den Spalteninhalt.
     * 
     * @return Spalteninhalt
     */
    public Object[] toArray() {
        return content.toArray();
    }
}
