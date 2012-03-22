package org.jphototagger.domain.metadata.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.file.FilesFilenameMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.StringUtil;

/**
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
     * MetaDataValue panels if type equals KEYWORDS_AND_PANELS
     */
    @XmlElementWrapper(name = "Panels")
    @XmlElement(type = SavedSearchPanel.class)
    private List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>();
    /**
     * Custom SQL if type equals CUSTOM_SQL
     */
    private String customSql;
    private Type type;
    private String name;

    public SavedSearch() {
    }

    public SavedSearch(SavedSearch other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }
        set(other);
    }

    public enum Type {

        KEYWORDS_AND_PANELS((short) 0), CUSTOM_SQL((short) 1),;
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
        return (panels != null) && !panels.isEmpty();
    }

    /**
     * @return panels or empty list
     */
    public List<SavedSearchPanel> getPanels() {
        return getDeepCopyPanels();
    }

    public void setPanels(List<SavedSearchPanel> panels) {
        setDeepCopyPanels(panels);
    }

    /**
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
     * {@code Type#CUSTOM_SQL} and {@code #getCustomSql()} is a not empty string
     * or if its type is {@code Type#KEYWORDS_AND_PANELS} and it has panels
     * and/or keywords.
     *
     * @return true if this search is valid
     */
    public boolean isValid() {
        if (name == null || type == null || !StringUtil.hasContent(name)) {
            return false;
        }
        if (type == Type.CUSTOM_SQL) {
            return StringUtil.hasContent(customSql);
        } else if (type == Type.KEYWORDS_AND_PANELS) {
            return hasKeywords() || hasPanels();
        } else {
            return false;
        }
    }

    public ParamStatement createParamStatement() {
        return isCustomSql()
                ? createParamStmtFromCustomSql()
                : createParamStmtFromPanels();
    }

    public boolean isCustomSql() {
        return type == Type.CUSTOM_SQL;
    }

    public boolean hasKeywords() {
        return !keywords.isEmpty();
    }

    /**
     * @return keywords or empty list
     */
    public List<String> getKeywords() {
        return new ArrayList<String>(keywords);
    }

    public void setKeywords(List<String> keywords) {
        setNotEmptyKeywords(keywords);
    }

    /**
     * @return name (identifier) or null
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
        return name == null
                ? ""
                : name;
    }

    /**
     * Two saved searches are equals if their names are equals.
     *
     * @param  obj object
     * @return     true if equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SavedSearch)) {
            return false;
        }
        SavedSearch other = (SavedSearch) obj;
        return ObjectUtil.equals(name, other.name);
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
        ParamStatement stmt = new ParamStatement();
        setType(Type.KEYWORDS_AND_PANELS);
        StringBuilder sb = getStartSelectFrom();
        appendToFrom(sb);
        appendWhere(sb);
        setStmt(stmt, sb);
        return stmt;
    }

    private StringBuilder getStartSelectFrom() {
        MetaDataValue filesFilenameMetaDataValue = FilesFilenameMetaDataValue.INSTANCE;
        String filesColumnName = filesFilenameMetaDataValue.getValueName();
        String filesTableName = filesFilenameMetaDataValue.getCategory();
        return new StringBuilder(
                "SELECT DISTINCT " + filesTableName + "." + filesColumnName + " FROM " + filesTableName);
    }

    private void appendToFrom(StringBuilder statement) {
        for (String tablename : getDistinctTablenamesOfColumns(getColumns())) {
            statement.append(Join.getJoinToFiles(tablename, Join.Type.INNER));
        }
        String sql = Join.removeMultipleJoinsToFiles(statement.toString(), Join.Type.INNER);
        statement.replace(0, statement.length(), sql);
    }

    private static Set<String> getDistinctTablenamesOfColumns(Collection<? extends MetaDataValue> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
    }
        Set<String> tablenames = new HashSet<String>();
        for (MetaDataValue column : columns) {
            tablenames.add(column.getCategory());
        }
        return tablenames;
    }

    private void appendWhere(StringBuilder statement) {
        statement.append(" WHERE");
        int index = 0;
        for (SavedSearchPanel panel : panels) {
            boolean isFirstPanel = index == 0;
            if (panel.hasSql(isFirstPanel)) {
                statement.append(panel.getSqlString(isFirstPanel));
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
        String paramsInParentheses = getParamsInParentheses(count);
        statement.append(and ? " AND" : "")
                 .append(" dc_subjects.subject IN ")
                 .append(paramsInParentheses)
                 .append(" GROUP BY files.filename")
                 .append(" HAVING COUNT(*) = ")
                 .append(Integer.toString(count));
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

    private List<MetaDataValue> getColumns() {
        List<MetaDataValue> columns = new ArrayList<MetaDataValue>();
        int index = 0;
        if (panels != null) {
            for (SavedSearchPanel panel : panels) {
                if (panel.hasSql(index == 0)) {
                    columns.add(panel.getColumn());
                }
                index++;
            }
        }
        if (!keywords.isEmpty()) {
            columns.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        }
        return columns;
    }

    /**
     * Returns parameters (?) whitin paranteses.
     *
     * @param  count count of parameters
     * @return       parameters in parantheses, e.g. <code>"(?, ?, ?)"</code>
     *               if count equals 3
     */
    private String getParamsInParentheses(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count < 1: " + count);
        }
        StringBuilder sb = new StringBuilder(count * 2);
        sb.append("(");
        for (int i = 0; i < count; i++) {
            sb.append((i == 0)
                    ? ""
                    : ",").append("?");
        }
        sb.append(")");
        return sb.toString();
    }
}
