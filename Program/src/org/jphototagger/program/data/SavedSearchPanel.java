package org.jphototagger.program.data;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Comparator;
import org.jphototagger.program.database.metadata.Operator;
import org.jphototagger.program.database.metadata.selections.ColumnIds;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class SavedSearchPanel {
    private int columnId = -1;
    private int comparatorId = -1;
    private int operatorId = -1;
    private int panelIndex = Integer.MIN_VALUE;
    private boolean bracketLeft1Selected;
    private boolean bracketLeft2Selected;
    private boolean bracketRightSelected;
    private String value;

    public SavedSearchPanel() {}

    public SavedSearchPanel(SavedSearchPanel other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        set(other);
    }

    public void set(SavedSearchPanel other) {
        if (other == null) {
            throw new NullPointerException("other == null");
        }

        panelIndex = other.panelIndex;
        bracketLeft1Selected = other.bracketLeft1Selected;
        bracketLeft2Selected = other.bracketLeft2Selected;
        bracketRightSelected = other.bracketRightSelected;
        operatorId = other.operatorId;
        columnId = other.columnId;
        comparatorId = other.comparatorId;
        value = other.value;
    }

    public int getPanelIndex() {
        return panelIndex;
    }

    public void setPanelIndex(int index) {
        this.panelIndex = index;
    }

    public boolean isBracketRightSelected() {
        return bracketRightSelected;
    }

    public void setBracketRightSelected(boolean selected) {
        this.bracketRightSelected = selected;
    }

    public int getColumnId() {
        return columnId;
    }

    public Column getColumn() {
        return ColumnIds.getColumn(columnId);
    }

    public void setColumnId(int id) {
        columnId = id;
    }

    public int getComparatorId() {
        return comparatorId;
    }

    public Comparator getComparator() {
        return Comparator.get(comparatorId);
    }

    public void setComparatorId(int id) {
        comparatorId = id;
    }

    public void setComparator(Comparator comparator) {
        comparatorId = comparator.getId();
    }

    public boolean isBracketLeft1Selected() {
        return bracketLeft1Selected;
    }

    public void setBracketLeft1Selected(boolean selected) {
        this.bracketLeft1Selected = selected;
    }

    public boolean isBracketLeft2Selected() {
        return bracketLeft2Selected;
    }

    public void setBracketLeft2Selected(boolean leftBracket2Selected) {
        this.bracketLeft2Selected = leftBracket2Selected;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public Operator getOperator() {
        return Operator.get(operatorId);
    }

    public void setOperatorId(int id) {
        operatorId = id;
    }

    public boolean hasValue() {
        return (value != null) &&!value.isEmpty();
    }

    public boolean isTrimmedValueEmpty() {
        return (value == null) || value.trim().isEmpty();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = (value == null)
                     ? null
                     : value.trim();
    }

    public String getSqlString(boolean isFirst) {
        if (hasSql(isFirst)) {
            Operator operator = getOperator();
            Column column = getColumn();
            Comparator comparator = getComparator();
            StringBuilder sb = new StringBuilder();

            if (!isFirst) {
                sb.append(bracketLeft1Selected
                          ? " ("
                          : "");
                sb.append(" ").append(operator.toSqlString());
            }

            sb.append(bracketLeft2Selected
                      ? " ("
                      : "");
            sb.append(" ").append(column.getTablename()).append(".").append(column.getName());
            sb.append(" ").append(comparator.toSqlString());
            sb.append(" ?");
            sb.append(bracketRightSelected
                      ? ")"
                      : "");

            return sb.toString();
        }

        return null;
    }

    public boolean hasOperator() {
        return getOperator() != null;
    }

    public boolean hasComperator() {
        return getComparator() != null;
    }

    public boolean hasColumn() {
        return getColumn() != null;
    }

    public boolean hasSql(boolean isFirst) {
        return (isFirst || hasOperator()) && hasColumn() && hasComperator() && (value != null) &&!value.isEmpty();
    }

    @Override
    public String toString() {
        String sql = getSqlString(false);

        return sql == null
                ? ""
                : sql;
    }
}
