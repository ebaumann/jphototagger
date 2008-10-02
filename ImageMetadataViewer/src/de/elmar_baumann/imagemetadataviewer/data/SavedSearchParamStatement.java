package de.elmar_baumann.imagemetadataviewer.data;

import de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement;
import java.util.Vector;

/**
 * Daten eines Objekts der Klasse {@link de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public class SavedSearchParamStatement {

    private String name;
    private String sql;
    private Vector<String> values;
    private boolean query;

    /**
     * Liefert den Namen.
     * 
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen.
     * 
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Liefert, ob das Statement eine Abrage ist.
     * 
     * @return true bei einer Abfrage, false bei einem Update
     */
    public boolean isQuery() {
        return query;
    }

    /**
     * Setzt, ob das Statement eine Abfrage ist.
     * 
     * @param query true bei einer Abfrage, false bei einem Update
     */
    public void setQuery(boolean query) {
        this.query = query;
    }

    /**
     * Liefert das Statement.
     * 
     * @return Statement
     */
    public String getSql() {
        return sql;
    }

    /**
     * Setzt das Statement.
     * 
     * @param sql Statement
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Liefert die Werte.
     * 
     * @return Werte
     */
    public Vector<String> getValues() {
        return values;
    }

    /**
     * Setzt die Werte.
     * 
     * @param values Werte
     */
    public void setValues(Vector<String> values) {
        this.values = values;
    }

    /**
     * Erzeugt ein parametrisiertes Statement aus den Daten.
     * 
     * @return Statement oder null, wenn die Daten ungültig sind
     */
    public ParamStatement createStatement() {
        ParamStatement stmt = null;
        if (!sql.isEmpty()) {
            stmt = new ParamStatement();
            stmt.setSql(sql);
            stmt.setIsQuery(query);
            stmt.setName(name);
            if (values != null) {
                stmt.setValues(values.toArray());
            }
        }
        return stmt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SavedSearchParamStatement other = (SavedSearchParamStatement) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
