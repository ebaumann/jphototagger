package de.elmar_baumann.imv.database.metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine Tabelle.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public abstract class Table {

    private final List<Column> columns = new ArrayList<Column>();
    private final List<Column> referenceColumns = new ArrayList<Column>();
    private final String name;

    /**
     * Erzeugt eine Instanz.
     * 
     * @param name Tabellenname
     */
    protected Table(String name) {
        this.name = name;
    }

    /**
     * Liefert den Tabellennamen.
     * 
     * @return Tabellenname
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Table) {
            Table otherTable = (Table) o;
            return name.equals(otherTable.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Fügt der Tabelle eine Spalte hinzu. Setzt diese Tabelle bei der Spalte.
     * 
     * @param column Spalte
     * @see          de.elmar_baumann.imv.database.metadata.Column#setTable(de.elmar_baumann.imv.database.metadata.Table)
     */
    protected void addColumn(Column column) {
        column.setTable(this);
        columns.add(column);
        if (column.getReferences() != null) {
            referenceColumns.add(column);
        }
    }

    /**
     * Liefert die Spalten der Tabelle.
     * 
     * @return Spalten
     */
    public List<Column> getColumns() {
        if (columns.isEmpty()) {
            addColumns();
        }
        return columns;
    }

    /**
     * Liefert alle Spalten, die Spalten einer anderen Tabelle referenzieren.
     * 
     * @return Referenzspalten
     */
    public List<Column> getReferenceColumns() {
        if (columns.isEmpty()) {
            addColumns();
        }
        return referenceColumns;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Liefert, ob die Tabelle Spalten hat, die Spalten anderer Tabellen
     * referenzieren (Fremdschlüsselspalten):
     * 
     * @return true, wenn die Tabelle Fremdschlüsselspalten hat
     */
    public boolean hasReferenceColumns() {
        return !referenceColumns.isEmpty();
    }

    /**
     * Liefert die Spalten, die eine bestimmte Tabelle referenzieren.
     * 
     * @param table Tabelle
     * @return Spalten
     */
    public List<Column> getJoinColumnsFor(Table table) {
        List<Column> joinColumns = new ArrayList<Column>();
        for (Column column : referenceColumns) {
            Column referencedColumn = column.getReferences();
            if (referencedColumn.getTable().equals(table)) {
                joinColumns.add(column);
            }
        }
        return joinColumns;
    }

    /**
     * Liefert alle Spalten, die den Primärschlüssel bilden.
     * 
     * @return Primärschlüsselspalten
     */
    public List<Column> getPrimaryKeyColumns() {
        List<Column> pColumns = new ArrayList<Column>();
        for (Column column : columns) {
            if (column.isPrimaryKey()) {
                pColumns.add(column);
            }
        }
        return pColumns;
    }

    /**
     * Abgeleitete Tabellen sollen ihre Spalten hinzufügen. Dies können sie nicht
     * im Konstruktor, da auch die Spalten Singletons sind, welche die Instanzen
     * der Tabellen aufrufen und es so zu einem Stackoverflow käme.
     */
    protected abstract void addColumns();
}
