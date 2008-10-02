package de.elmar_baumann.imagemetadataviewer.database.metadata;

import java.util.Vector;

/**
 * Utils für Datenbankmetadaten.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2007/07/29
 */
public class DatabaseMetadataUtil {

    /**
     * Liefert aus verschiedenen Spalten die gemeinsamen Tabellennamen.
     * 
     * @param columns Spalten
     * @return        Tabellennamen
     */
    public static Vector<String> getUniqueTableNamesOfColumnArray(Vector<Column> columns) {
        Vector<String> tablenames = new Vector<String>();

        for (Column column : columns) {
            String tableName = column.getTable().getName();
            if (!tablenames.contains(tableName)) {
                tablenames.add(tableName);
            }
        }
        return tablenames;
    }

    /**
     * Liefert aus verschiedenen Tabellen die gemeinsamen.
     * 
     * @param tables Tabellen
     * @return       Gemeinsame Tabellen
     */
    public static Vector<Table> getUniqueTablesOfTableArray(Vector<Table> tables) {
        Vector<Table> uniqueTables = new Vector<Table>();
        for (Table table : tables) {
            if (!uniqueTables.contains(table)) {
                uniqueTables.add(table);
            }
        }
        return uniqueTables;
    }

    /**
     * Liefert aus verschiedenen Spalten die gemeinsamen Tabellen.
     * 
     * @param columns Spalten
     * @return        Tabellen
     */
    public static Vector<Table> getUniqueTablesOfColumnArray(Vector<Column> columns) {
        Vector<Table> tables = new Vector<Table>();
        for (Column column : columns) {
            Table table = column.getTable();
            if (!tables.contains(table)) {
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Liefert aus verschiedenen Spalten die gemeinsamen.
     * 
     * @param columns Spalten
     * @return        Spalten
     */
    public static Vector<Column> getUniqueColumnsOfColumnArray(Vector<Column> columns) {
        Vector<Column> uniqueColumns = new Vector<Column>();
        for (Column column : columns) {
            if (!uniqueColumns.contains(column)) {
                uniqueColumns.add(column);
            }
        }
        return uniqueColumns;
    }

    /**
     * Liefert von mehreren Tabellen alle, die eine bestimmte (andere) Tabelle
     * referenzieren: In einer der Tabellen ist (mindestens) eine Spalte
     * Fremdschlüssel der den Primärschlüssel dieser Tabelle 
     * (<code>referenceTable</code>) referenziert.
     * 
     * @param tables         Tabellen
     * @param referenceTable Tabelle, die referenziert werden soll
     * @param direction      Richtung der Referenz
     * @return               Tabellen, die <code>referenceTable</code>
     *                       referenzieren (unique)
     */
    public static Vector<Table> getTablesWithReferenceTo(Vector<Table> tables,
        Table referenceTable, Column.ReferenceDirection direction) {
        Vector<Table> referenced = new Vector<Table>();
        for (Table table : tables) {
            Vector<Column> refCols = table.getReferenceColumns();
            for (Column column : refCols) {
                Column refdCol = column.getReferences();
                if (refdCol.getTable().equals(referenceTable) && column.getReferenceDirection().equals(direction)) {
                    referenced.add(table);
                }
            }
        }
        return getUniqueTablesOfTableArray(referenced);
    }

    /**
     * Liefert die Tabellenspalten einer bestimmten Tabellenkategorie.
     * 
     * @param tableColumns Tabellenspalten (beliebiger Tabellen)
     * @param tablename    Name der Tabelle: Alle Tabellen, deren Namen damit
     *                     anfangen, werden hinzugefügt
     * @return             Spalten der Tabelle aus <code>tableColumns</code>
     */
    public static Vector<Column> getTableColumnsOfTableCategory(
        Vector<Column> tableColumns, String tablename) {
        Vector<Column> columns = new Vector<Column>();
        for (Column column : tableColumns) {
            if (column.getTable().getName().startsWith(tablename)) {
                columns.add(column);
            }
        }
        return columns;
    }

    /**
     * Liefert einen String mit "SELECT spalte, spalte, ... FROM tabelle, ..."
     * 
     * @param tableColumns Spalten
     * @return             SQL-String
     */
    public static String getSqlSelectFrom(Vector<Column> tableColumns) {
        StringBuffer sql = new StringBuffer("SELECT "); // NOI18N
        int columnCount = tableColumns.size();

        for (int index = 0; index < columnCount; index++) {
            Column tableColumn = tableColumns.get(index);
            sql.append(tableColumn.getTable().getName() + "." + tableColumn. // NOI18N
                getName() + (index < columnCount - 1
                ? ", " : "")); // NOI18N
        }

        sql.append(" FROM "); // NOI18N

        Vector<String> tablenames = getUniqueTableNamesOfColumnArray(tableColumns);
        int tableCount = tablenames.size();
        for (int index = 0; index < tableCount; index++) {
            String tablename = tablenames.get(index);
            sql.append(tablename + (index < columnCount - 1 ? ", " : "")); // NOI18N
        }

        return sql.toString();
    }
}
