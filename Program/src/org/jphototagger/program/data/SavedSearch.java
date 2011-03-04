package org.jphototagger.program.data;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.program.database.metadata.Join;
import org.jphototagger.program.database.metadata.Util;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class SavedSearch {

    /**
     * Keywords if type equals KEYWORDS_AND_PANELS
     */
    @XmlElementWrapper(name = "Keywords")
    @XmlElement(type = String.class)
    private List<String> keywords = new ArrayList<String>();

    /**
     * Column panels if type equals KEYWORDS_AND_PANELS
     */
    @XmlElementWrapper(name = "Panels")
    @XmlElement(type = SavedSearchPanel.class)
    private List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>();

    /**
     * Custom SQL if type equals CUSTOM_SQL
     */
    private String customSql;

    /**
     * Type
     */
    private Type type;

    /**
     * Name and identifier
     */
    private String name;

    public SavedSearch() {}

    public SavedSearch(SavedSearch other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        set(other);
    }

    public enum Type {
        KEYWORDS_AND_PANELS((short) 0), CUSTOM_SQL((short) 1),
        ;

        private final short value;

        private Type(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }

        public static Type fromValue(short value) {
            for (Type t : values()) {
                if (t.getValue() == value) {
                    return t;
                }
            }

            return null;
        }
    }

    public void set(SavedSearch other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        if (other != this) {
            panels = other.getDeepCopyPanels();
            type = other.type;
        }
    }

    public boolean hasPanels() {
        return (panels != null) &&!panels.isEmpty();
    }

    /**
     * Returns the saved search panels.
     *
     * @return panels or empty list
     */
    public List<SavedSearchPanel> getPanels() {
        return getDeepCopyPanels();
    }

    public void setPanels(List<SavedSearchPanel> panels) {
        setDeepCopyPanels(panels);
    }

    /**
     * Returns the type.
     *
     * @return type or null
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * A search is valid if it has a name, a type, its type is
     * {@link Type#CUSTOM_SQL} and {@link #getCustomSql()} is a not empty string
     * or if its type is {@link Type#KEYWORDS_AND_PANELS} and it has panels
     * and/or keywords.
     *
     * @return true if this search is valid
     */
    public boolean isValid() {
        if ((name == null) || name.isEmpty() || (type == null)) {
            return false;
        }

        if (type.equals(Type.CUSTOM_SQL)) {
            return (customSql != null) &&!customSql.isEmpty();
        } else if (type.equals(Type.KEYWORDS_AND_PANELS)) {
            return hasKeywords() || hasPanels();
        } else {
            assert false : type;

            return false;
        }
    }

    public ParamStatement createParamStatement() {
        return isCustomSql()
               ? createParamStmtFromCustomSql()
               : createParamStmtFromPanels();
    }

    public boolean isCustomSql() {
        return (type != null) && type.equals(Type.CUSTOM_SQL);
    }

    public boolean hasKeywords() {
        return !keywords.isEmpty();
    }

    /**
     * Returns the keywords.
     *
     * @return keywords or empty list
     */
    public List<String> getKeywords() {
        return new ArrayList<String>(keywords);
    }

    public void setKeywords(List<String> keywords) {
        setNotEmptyKeywords(keywords);
    }

    /**
     * Returns the name (identifier).
     *
     * @return name or null
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null)
                    ? null
                    : name.trim();
    }

    /**
     * Returns the custom SQL.
     *
     * @return SQL string or null
     */
    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = (customSql == null)
                         ? null
                         : customSql.trim();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Two saved searches are equals if their names are equals.
     *
     * @param  obj object
     * @return     true if equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final SavedSearch other = (SavedSearch) obj;

        if ((this.name == null)
            ? (other.name != null)
            : !this.name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 73 * hash + ((this.name != null)
                            ? this.name.hashCode()
                            : 0);

        return hash;
    }

    private List<SavedSearchPanel> getDeepCopyPanels() {
        List<SavedSearchPanel> copy = new ArrayList<SavedSearchPanel>(panels.size());

        for (SavedSearchPanel panel : panels) {
            copy.add(new SavedSearchPanel(panel));
        }

        return copy;
    }

    private void setNotEmptyKeywords(List<String> keywords) {
        if (keywords == null) {
            this.keywords = new ArrayList<String>();

            return;
        }

        this.keywords = new ArrayList<String>(keywords.size());

        for (String keyword : keywords) {
            String trimmedKeyword = keyword.trim();

            if (!trimmedKeyword.isEmpty()) {
                this.keywords.add(trimmedKeyword);
            }
        }
    }

    private void setDeepCopyPanels(List<SavedSearchPanel> panels) {
        if (panels == null) {
            this.panels = new ArrayList<SavedSearchPanel>();

            return;
        }

        this.panels = new ArrayList<SavedSearchPanel>(panels.size());

        for (SavedSearchPanel panel : panels) {
            if (panel.hasValue()) {
                this.panels.add(new SavedSearchPanel(panel));
            }
        }
    }

    private ParamStatement createParamStmtFromCustomSql() {
        ParamStatement stmt = new ParamStatement();

        setType(Type.CUSTOM_SQL);
        stmt.setSql(customSql);
        stmt.setQuery(true);

        return stmt;
    }

    private ParamStatement createParamStmtFromPanels() {
        StringBuilder sb = getStartSelectFrom();
        ParamStatement stmt = new ParamStatement();

        setType(Type.KEYWORDS_AND_PANELS);
        appendToFrom(sb);
        appendWhere(sb);
        setStmt(stmt, sb);

        return stmt;
    }

    private void setStmt(ParamStatement stmt, StringBuilder sb) {
        stmt.setSql(sb.toString());
        setValues(stmt);
        stmt.setQuery(true);
    }

    private void setValues(ParamStatement stmt) {
        List<String> values = new ArrayList<String>(panels.size() + keywords.size());

        for (SavedSearchPanel panel : panels) {
            values.add(panel.getValue());
        }

        values.addAll(keywords);
        stmt.setValues(values);
    }

    private synchronized void appendWhere(StringBuilder statement) {
        statement.append(" WHERE");

        int index = 0;

        for (SavedSearchPanel panel : panels) {
            if (panel.hasSql(index == 0)) {
                statement.append(panel.getSqlString(index == 0));
                index++;
            }
        }

        appendKeywordStmt(statement, index > 0);
    }

    private void appendKeywordStmt(StringBuilder statement, boolean and) {
        int count = keywords.size();

        if (count == 0) {
            return;
        }

        String paramsInParentheses = org.jphototagger.program.database.Util.getParamsInParentheses(count);

        statement.append(and
                         ? " AND"
                         : "").append(" dc_subjects.subject IN ").append(paramsInParentheses).append(
                             " GROUP BY files.filename" + " HAVING COUNT(*) = ").append(Integer.toString(count));
    }

    private void appendToFrom(StringBuilder statement) {
        statement.append(" files");

        int index = 0;

        for (String tablename : Util.getDistinctTablenamesOfColumns(getColumns())) {
            statement.append((index++ == 0)
                             ? ""
                             : " ");
            statement.append(Join.getJoinToFiles(tablename, Join.Type.INNER));
        }
    }

    private StringBuilder getStartSelectFrom() {
        Column columnFilename = ColumnFilesFilename.INSTANCE;
        String columnNameFilename = columnFilename.getName();
        String tableNameFiles = columnFilename.getTablename();

        return new StringBuilder("SELECT DISTINCT " + tableNameFiles + "." + columnNameFilename + " FROM");
    }

    private synchronized List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();
        int index = 0;

        if (panels != null) {
            for (SavedSearchPanel panel : panels) {
                if (panel.hasSql(index++ == 0)) {
                    columns.add(panel.getColumn());
                }
            }
        }

        if (!keywords.isEmpty()) {
            columns.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        }

        return columns;
    }
}
