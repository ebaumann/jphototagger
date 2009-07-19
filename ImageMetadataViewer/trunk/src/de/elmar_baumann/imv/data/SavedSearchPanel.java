package de.elmar_baumann.imv.data;

/**
 * Daten gespeicherter Suchen für ein {@link de.elmar_baumann.imv.view.panels.SearchColumnPanel}-Objekt.
 * Die Indexe sind Indexe von Listenitems in Comboboxen oder Listboxen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-12
 */
public final class SavedSearchPanel {

    private int panelIndex = Integer.MIN_VALUE;
    private boolean bracketLeft1Selected;
    private boolean bracketLeft2Selected;
    private boolean bracketRightSelected;
    private int operatorId = -1;
    private int columnId = -1;
    private int comparatorId = -1;
    private String value;

    /**
     * Liefert den Panelindex.
     * 
     * @return Panelindex
     */
    public int getPanelIndex() {
        return panelIndex;
    }

    /**
     * Setzt den Panelindex.
     * 
     * @param index Panelindex
     */
    public void setPanelIndex(int index) {
        this.panelIndex = index;
    }

    /**
     * Liefert, ob die rechte Klammer ausgewählt ist.
     * 
     * @return true, wenn ausgewählt
     */
    public boolean isBracketRightSelected() {
        return bracketRightSelected;
    }

    /**
     * Wählt die rechte Klammer aus.
     * 
     * @param selected true, wenn ausgewählt
     */
    public void setBracketRightSelected(boolean selected) {
        this.bracketRightSelected = selected;
    }

    /**
     * Liefert die ID der Spalte.
     * 
     * @return Spalten-ID
     */
    public int getColumnId() {
        return columnId;
    }

    /**
     * Setzt die ID der Spalte.
     * 
     * @param id Spalten-ID
     */
    public void setColumnId(int id) {
        columnId = id;
    }

    /**
     * Liefert die ID des Vergleichs.
     * 
     * @return Vergleichs-ID
     */
    public int getComparatorId() {
        return comparatorId;
    }

    /**
     * Setzt die ID des Vergleichs.
     * 
     * @param id ID
     */
    public void setComparatorId(int id) {
        comparatorId = id;
    }

    /**
     * Liefert, ob die 1. Klammer links ausgewählt ist.
     * 
     * @return true, wenn ausgewählt
     */
    public boolean isBracketLeft1Selected() {
        return bracketLeft1Selected;
    }

    /**
     * Setzt die 1. Klammer links auf ausgewählt.
     * 
     * @param selected true, wenn ausgewählt
     */
    public void setBracketLeft1Selected(boolean selected) {
        this.bracketLeft1Selected = selected;
    }

    /**
     * Liefert, ob die 2. Klammer links ausgewählt ist.
     * 
     * @return true, wenn ausgewählt
     */
    public boolean isBracketLeft2Selected() {
        return bracketLeft2Selected;
    }

    /**
     * Setzt die 2. Klammer links auf ausgewählt.
     * 
     * @param leftBracket2Selected true, wenn ausgewählt
     */
    public void setBracketLeft2Selected(boolean leftBracket2Selected) {
        this.bracketLeft2Selected = leftBracket2Selected;
    }

    /**
     * Liefert die ID des Operators.
     * 
     * @return Operator-ID
     */
    public int getOperatorId() {
        return operatorId;
    }

    /**
     * Setzt die ID des Operators.
     * 
     * @param id ID
     */
    public void setOperatorId(int id) {
        operatorId = id;
    }

    /**
     * Liefert, ob ein Wert definiert ist.
     * 
     * @return true, wenn ein Wert definiert ist
     */
    public boolean hasValue() {
        return value != null;
    }

    /**
     * Liefert den Wert.
     * 
     * @return Wert oder null wenn nicht gesetzt
     * @see    #hasValue() 
     */
    public String getValue() {
        return value;
    }

    /**
     * Setzt den Wert.
     * 
     * @param value Wert
     */
    public void setValue(String value) {
        this.value = value;
    }
}
