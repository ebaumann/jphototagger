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
    private List<SavedSearchPanel>    panels;
    private SavedSearchParamStatement paramStatement;
    private Type                      type;

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

        paramStatement.set(other.paramStatement);
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

    public SavedSearchParamStatement getSavedSearchParamStatement() {
        return (paramStatement == null)
               ? null
               : new SavedSearchParamStatement(paramStatement);
    }

    public void setParamStatement(SavedSearchParamStatement paramStatement) {
        if (paramStatement == null) {
            throw new NullPointerException("paramStatement == null");
        }

        this.paramStatement = new SavedSearchParamStatement(paramStatement);
    }

    public boolean hasParamStatement() {
        return paramStatement != null;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final SavedSearch other = (SavedSearch) obj;

        return paramStatement.equals(other.paramStatement);
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 19 * hash + ((this.paramStatement != null)
                            ? this.paramStatement.hashCode()
                            : 0);

        return hash;
    }

    public String getName() {
        String string = null;

        if (paramStatement != null) {
            string = paramStatement.getName();
        }

        return ((string == null)
                ? ""
                : string);
    }

    public void setName(String name) {
        if (paramStatement == null) {
            return;
        }

        paramStatement.setName(name);
    }

    @Override
    public String toString() {

        // Never change that (will be used to find model items)!
        return getName();
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

    public void createAndSetParamStatementFromCustomSql(String sql) {
        SavedSearchParamStatement paramStmt = new SavedSearchParamStatement();

        setType(Type.CUSTOM_SQL);
        paramStmt.setQuery(true);

        if (paramStatement != null) {
            paramStmt.setName(paramStatement.getName());
        }

        paramStatement = paramStmt;
    }

    public void createAndSetParamStatementFromPanels() {
        StringBuilder  sb     = getStartSelectFrom();
        List<String>   values = new ArrayList<String>();
        ParamStatement stmt   = new ParamStatement();

        appendToFrom(sb);
        appendWhere(sb, values);
        stmt.setSql(sb.toString());
        stmt.setValues(values.toArray());
        stmt.setIsQuery(true);

        SavedSearchParamStatement ssParamStmt = new SavedSearchParamStatement();

        ssParamStmt.setQuery(stmt.isQuery());
        ssParamStmt.setSql(stmt.getSql());

        List<String> paramStmtValues = stmt.getValuesAsStringList();

        ssParamStmt.setValues((paramStmtValues.size() > 0)
                            ? paramStmtValues
                            : null);
        setType(Type.KEYWORDS_AND_PANELS);

        if (paramStatement != null) {
            ssParamStmt.setName(paramStatement.getName());
        }

        paramStatement = ssParamStmt;
    }

    private synchronized void appendWhere(StringBuilder statement,
            List<String> values) {
        statement.append(" WHERE");

        int index = 0;

        if (panels != null) {
            for (SavedSearchPanel panel : panels) {
                if (panel.hasSql(index == 0)) {
                    statement.append(panel.getSqlString(index == 0));
                    values.add(panel.getValue());
                    index++;
                }
            }
        }

        appendKeywords(statement, values, index > 0);
    }

    private void appendKeywords(StringBuilder statement, List<String> values,
                                boolean and) {
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
        values.addAll(keywords);
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
