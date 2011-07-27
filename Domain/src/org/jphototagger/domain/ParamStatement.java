package org.jphototagger.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ParamStatement {
    private String sql;
    private List<String> values = new ArrayList<String>();
    private boolean query;

    public ParamStatement() {}

    public ParamStatement(ParamStatement stmt) {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        set(stmt);
    }

    public void set(ParamStatement stmt) {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        sql = stmt.sql;
        values = new ArrayList<String>(stmt.values);
        query = stmt.query;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     *
     * @return parameter values or empty list
     */
    public List<String> getValues() {
        return new ArrayList<String>(values);
    }

    /**
     *
     * @param values parameter values
     */
    public void setValues(List<String> values) {
        this.values = (values == null)
                      ? new ArrayList<String>()
                      : new ArrayList<String>(values);
    }

    public boolean hasValues() {
        return !values.isEmpty();
    }

    public boolean isQuery() {
        return query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return sql == null
                ? ""
                : sql;
    }
}
