package de.elmar_baumann.imagemetadataviewer.database.metadata;

import java.util.Vector;

/**
 * Parametrisiertes SQL-Statement.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/28
 */
public class ParamStatement {

    private String sql;
    private Object[] values;
    private boolean isQuery;
    private String name;

    public ParamStatement() {
    }

    /**
     * Konstruktor.
     * 
     * @param sql      SQL-Statement-String
     * @param values   Parameterwerte
     * @param isQuery  true, wenn das Statement eine Abfrage ist
     * @param name     Name
     */
    public ParamStatement(String sql, Object[] values, boolean isQuery,
        String name) {
        this.sql = sql;
        this.values = values;
        this.isQuery = isQuery;
        this.name = name;
    }

    /**
     * Liefert den SQL-String.
     * 
     * @return SQL-String
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setzt den SQL-String.
     * 
     * @param sql SQL-String. Default: null.
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Liefert die Parameterwerte.
     * 
     * @return Parameterwerte oder null, wenn es keine gibt
     */
    public Object[] getValues() {
        return values;
    }

    /**
     * Setzt die Parameterwerte.
     * 
     * @param values Parameterwerte. Default: null.
     */
    public void setValues(Object[] values) {
        this.values = values;
    }

    /**
     * Liefert die Werte als Stringarray.
     * 
     * @return Werte als Stringarray
     */
    public Vector<String> getValuesAsStringArray() {
        Vector<String> array = new Vector<String>();
        for (int index = 0; index < values.length; index++) {
            array.add(values[index].toString());
        }
        return array;
    }

    /**
     * Liefert, ob das Statement eine Abfrage ist.
     * 
     * @return true, wenn das Statement eine Abfrage ist
     */
    public boolean isQuery() {
        return isQuery;
    }

    /**
     * Setzt, ob das Statement eine Abfrage ist.
     * 
     * @param isQuery true, wenn das Statement eine Abfrage ist.
     *     Default: true.
     */
    public void setIsQuery(boolean isQuery) {
        this.isQuery = isQuery;
    }

    /**
     * Liefert den Namen des Statements.
     * 
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Statements.
     * 
     * @param name Name. Default: null.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getSql() + "\nValues:" + getValuesString("\n\t"); // NOI18N
    }

    private String getValuesString(String delimiter) {
        StringBuffer buffer = new StringBuffer();
        if (getValues() == null) {
            return ""; // NOI18N
        }
        for (int i = 0; i < getValues().length; i++) {
            buffer.append(delimiter + getValues()[i].toString());
        }
        return buffer.toString();
    }
}
