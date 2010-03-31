/*
 * @(#)SavedSearch.java    Created on 2008-09-12
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.data;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.program.database.metadata.Join;
import org.jphototagger.program.database.metadata.Util;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author  Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class SavedSearch {

    /**
     * Keywords
     */
    @XmlElementWrapper(name = "SavedSearchKeywords")
    @XmlElement(type = String.class)
    private List<String> keywords = new ArrayList<String>();

    /**
     * Column panels
     */
    @XmlElementWrapper(name = "SavedSearchPanels")
    @XmlElement(type = SavedSearchPanel.class)
    private List<SavedSearchPanel> panels;
    private String                 customSql;
    private Type                   type;
    private String                 name = "";

    public SavedSearch() {}

    public SavedSearch(SavedSearch other) {
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

        if (other == this) {
            return;
        }

        panels = other.getDeepCopyPanels();
        type   = other.type;
    }

    public List<SavedSearchPanel> getPanels() {
        return (panels == null)
               ? null
               : getDeepCopyPanels();
    }

    public void setPanels(List<SavedSearchPanel> panels) {
        setDeepCopyPanels(panels);
    }

    public void addPanel(SavedSearchPanel panel) {
        if (panels == null) {
            panels = new ArrayList<SavedSearchPanel>();
        }

        panels.add(panel);
    }

    public ParamStatement getParamStatement() {
        return isCustomSql()
               ? createParamStmtFromCustomSql()
               : createParamStmtFromPanels();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        this.type = type;
    }

    public boolean hasPanels() {
        return (panels != null) &&!panels.isEmpty();
    }

    public boolean isCustomSql() {
        return (type != null) && type.equals(Type.CUSTOM_SQL);
    }

    public boolean hasKeywords() {
        return !keywords.isEmpty();
    }

    public List<String> getKeywords() {
        return new ArrayList<String>(keywords);
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = (keywords == null)
                        ? new ArrayList<String>()
                        : new ArrayList<String>(keywords);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null)
                    ? ""
                    : name;
    }

    @Override
    public String toString() {

        // Never change that (will be used to find model items)!
        return name;
    }

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

    public String getCustomSql() {
        return customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    private List<SavedSearchPanel> getDeepCopyPanels() {
        if (panels == null) {
            return null;
        }

        List<SavedSearchPanel> copy =
            new ArrayList<SavedSearchPanel>(panels.size());

        for (SavedSearchPanel panel : panels) {
            copy.add(new SavedSearchPanel(panel));
        }

        return copy;
    }

    private void setDeepCopyPanels(List<SavedSearchPanel> p) {
        if (p == null) {
            panels = null;

            return;
        }

        panels = new ArrayList<SavedSearchPanel>(p.size());

        for (SavedSearchPanel panel : p) {
            panels.add(new SavedSearchPanel(panel));
        }
    }

    private ParamStatement createParamStmtFromCustomSql() {
        ParamStatement stmt = new ParamStatement();

        setType(Type.CUSTOM_SQL);
        stmt.setQuery(true);
        stmt.setSql(customSql);

        return stmt;
    }

    private ParamStatement createParamStmtFromPanels() {
        StringBuilder  sb   = getStartSelectFrom();
        ParamStatement stmt = new ParamStatement();

        setType(Type.KEYWORDS_AND_PANELS);
        appendToFrom(sb);
        appendWhere(sb);
        stmt.setSql(sb.toString());
        setValues(stmt);
        stmt.setQuery(true);

        return stmt;
    }

    private void setValues(ParamStatement stmt) {
        List<String> values = new ArrayList<String>(10);

        for (SavedSearchPanel panel : panels) {
            if (panel.hasValue()) {
                values.add(panel.getValue());
            }
        }

        values.addAll(keywords);
        stmt.setValues(values);
    }

    private synchronized void appendWhere(StringBuilder statement) {
        statement.append(" WHERE");

        int index = 0;

        if (panels != null) {
            for (SavedSearchPanel panel : panels) {
                if (panel.hasSql(index == 0)) {
                    statement.append(panel.getSqlString(index == 0));
                    index++;
                }
            }
        }

        appendKeywordStmt(statement, index > 0);
    }

    private void appendKeywordStmt(StringBuilder statement, boolean and) {
        int count = keywords.size();

        if (count == 0) {
            return;
        }

        statement.append((and
                          ? " AND"
                          : "") + " dc_subjects.subject IN "
                                + org.jphototagger.program.database.Util
                                    .getParamsInParentheses(
                                        count) + " GROUP BY files.filename"
                                            + " HAVING COUNT(*) = "
                                                + Integer.toString(count));
    }

    private void appendToFrom(StringBuilder statement) {
        statement.append(" files");

        int index = 0;

        for (String tablename :
                Util.getDistinctTablenamesOfColumns(getColumns())) {
            statement.append((index++ == 0)
                             ? ""
                             : " ");
            statement.append(Join.getJoinToFiles(tablename, Join.Type.INNER));
        }
    }

    private StringBuilder getStartSelectFrom() {
        Column columnFilename     = ColumnFilesFilename.INSTANCE;
        String columnNameFilename = columnFilename.getName();
        String tableNameFiles     = columnFilename.getTablename();

        return new StringBuilder("SELECT DISTINCT " + tableNameFiles + "."
                                 + columnNameFilename + " FROM");
    }

    private synchronized List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();
        int          index   = 0;

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
