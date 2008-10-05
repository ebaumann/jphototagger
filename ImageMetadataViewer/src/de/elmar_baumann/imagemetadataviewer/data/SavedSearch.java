package de.elmar_baumann.imagemetadataviewer.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Daten einer gespeicherten Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/12
 */
public class SavedSearch {

    private SavedSearchParamStatement paramStatements;
    private List<SavedSearchPanel> panels;

    /**
     * Liefert die Paneldaten.
     * 
     * @return Paneldaten
     */
    public List<SavedSearchPanel> getPanels() {
        return panels;
    }

    /**
     * Setzt die Paneldaten.
     * 
     * @param panelData Paneldaten
     */
    public void setPanels(List<SavedSearchPanel> panelData) {
        this.panels = panelData;
    }

    /**
     * Fügt Paneldaten hinzu.
     * 
     * @param data Paneldaten
     */
    public void addToPanelData(SavedSearchPanel data) {
        if (panels == null) {
            panels = new ArrayList<SavedSearchPanel>();
        }
        panels.add(data);
    }

    /**
     * Setzt die Satementdaten.
     * 
     * @return Satementdaten
     */
    public SavedSearchParamStatement getParamStatements() {
        return paramStatements;
    }

    /**
     * Liefert die Satementdaten.
     * 
     * @param paramStatementData Satementdaten
     */
    public void setParamStatements(SavedSearchParamStatement paramStatementData) {
        this.paramStatements = paramStatementData;
    }

    /**
     * Liefert, ob Statementdaten existieren.
     * 
     * @return true, wenn Statementdaten existieren
     */
    public boolean hasParamStatement() {
        return paramStatements != null;
    }

    /**
     * Liefert, ob Paneldaten existieren.
     * 
     * @return true, wenn Paneldaten existieren
     */
    public boolean hasPanels() {
        return panels != null;
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
        return paramStatements.equals(other.paramStatements);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.paramStatements != null ? this.paramStatements.hashCode() : 0);
        return hash;
    }

    /**
     * Liefert den Namen des parametrisierten Statements. Diesen liefert auch
     * {@link #toString()}.
     * 
     * @return Name
     * @see    de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement#getName()
     */
    public String getName() {
        String string = null;
        if (paramStatements != null) {
            string = paramStatements.getName();
        }
        return (string == null ? "" : string); // NOI18N
    }

    /**
     * Setzt den Namen des parametrisierten Statements.
     * 
     * @param name Name
     * @see    de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement#setName(java.lang.String)
     */
    public void setName(String name) {
        paramStatements.setName(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
