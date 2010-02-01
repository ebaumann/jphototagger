/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database.metadata;

import java.util.ArrayList;
import java.util.List;


/**
 * Utils für Datenbankmetadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class Util {

    /**
     * Liefert aus verschiedenen Spalten die gemeinsamen Tabellennamen.
     *
     * @param columns Spalten
     * @return        Tabellennamen
     */
    public static List<String> getUniqueTableNamesOfColumnArray(List<Column> columns) {
        List<String> tablenames = new ArrayList<String>();

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
    public static List<Table> getUniqueTablesOfTableArray(List<Table> tables) {
        List<Table> uniqueTables = new ArrayList<Table>();
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
    public static List<Table> getUniqueTablesOfColumnArray(List<Column> columns) {
        List<Table> tables = new ArrayList<Table>();
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
    public static List<Column> getUniqueColumnsOfColumnArray(List<Column> columns) {
        List<Column> uniqueColumns = new ArrayList<Column>();
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
    public static List<Table> getTablesWithReferenceTo(List<Table> tables,
        Table referenceTable, Column.ReferenceDirection direction) {
        List<Table> referenced = new ArrayList<Table>();
        for (Table table : tables) {
            List<Column> refCols = table.getReferenceColumns();
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
     *
     * @param tableColumns Tabellenspalten (beliebiger Tabellen)
     * @param tablename    Name der Tabelle: Alle Tabellen, deren Namen damit
     *                     anfangen, werden hinzugefügt
     * @return             Spalten der Tabelle aus <code>tableColumns</code>
     */
    public static List<Column> getTableColumnsOfTableStartsWith(
        List<Column> tableColumns, String tablename) {
        List<Column> columns = new ArrayList<Column>();
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
    public static String getSqlSelectFrom(List<Column> tableColumns) {
        StringBuffer sql = new StringBuffer("SELECT ");
        int columnCount = tableColumns.size();

        for (int index = 0; index < columnCount; index++) {
            Column tableColumn = tableColumns.get(index);
            sql.append(tableColumn.getTable().getName() + "." + tableColumn.
                getName() + (index < columnCount - 1
                ? ", " : ""));
        }

        sql.append(" FROM ");

        List<String> tablenames = getUniqueTableNamesOfColumnArray(tableColumns);
        int tableCount = tablenames.size();
        for (int index = 0; index < tableCount; index++) {
            String tablename = tablenames.get(index);
            sql.append(tablename + (index < columnCount - 1 ? ", " : ""));
        }

        return sql.toString();
    }

    private Util() {}
}
