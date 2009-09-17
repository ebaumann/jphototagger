/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Daten einer gespeicherten Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-12
 */
public final class SavedSearch {

    private SavedSearchParamStatement paramStatement;
    private List<SavedSearchPanel> panels;

    /**
     * Liefert die Paneldaten.
     * 
     * @return Paneldaten
     */
    public List<SavedSearchPanel> getPanels() {
        return panels == null
               ? null
               : getDeepCopyPanels();
    }

    /**
     * Setzt die Paneldaten.
     * 
     * @param panels panels
     */
    public void setPanels(List<SavedSearchPanel> panels) {
        if (panels == null) {
            this.panels = null;
        } else {
            setDeepCopyPanels(panels);
        }
    }

    /**
     * Fügt Paneldaten hinzu.
     * 
     * @param panel Panel
     */
    public void addPanel(SavedSearchPanel panel) {
        if (panels == null) {
            panels = new ArrayList<SavedSearchPanel>();
        }
        panels.add(panel);
    }

    /**
     * Setzt die Satementdaten.
     * 
     * @return <strong>reference</strong> to satement data
     */
    public SavedSearchParamStatement getParamStatement() {
        return paramStatement == null
               ? null
               : new SavedSearchParamStatement(paramStatement);
    }

    /**
     * Sets the param statement.
     * 
     * @param paramStatement Satement
     */
    public void setParamStatement(SavedSearchParamStatement paramStatement) {
        this.paramStatement = new SavedSearchParamStatement(paramStatement);
    }

    /**
     * Liefert, ob Statementdaten existieren.
     * 
     * @return true, wenn Statementdaten existieren
     */
    public boolean hasParamStatement() {
        return paramStatement != null;
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
        return paramStatement.equals(other.paramStatement);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.paramStatement != null
                            ? this.paramStatement.hashCode()
                            : 0);
        return hash;
    }

    /**
     * Liefert den Namen des parametrisierten Statements. Diesen liefert auch
     * {@link #toString()}.
     * 
     * @return Name
     * @see    de.elmar_baumann.jpt.database.metadata.ParamStatement#getName()
     */
    public String getName() {
        String string = null;
        if (paramStatement != null) {
            string = paramStatement.getName();
        }
        return (string == null
                ? "" // NOI18N
                : string); // NOI18N
    }

    /**
     * Setzt den Namen des parametrisierten Statements.
     * 
     * @param name Name
     * @see    de.elmar_baumann.jpt.database.metadata.ParamStatement#setName(java.lang.String)
     */
    public void setName(String name) {
        paramStatement.setName(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    private List<SavedSearchPanel> getDeepCopyPanels() {
        assert panels != null : "panels field is null!"; // NOI18N
        List<SavedSearchPanel> copy =
                new ArrayList<SavedSearchPanel>(panels.size());
        for (SavedSearchPanel panel : panels) {
            copy.add(new SavedSearchPanel(panel));
        }
        return copy;
    }

    private void setDeepCopyPanels(List<SavedSearchPanel> p) {
        assert p != null : "panels parameter is null!"; // NOI18N
        panels = new ArrayList<SavedSearchPanel>(p.size());
        for (SavedSearchPanel panel : p) {
            panels.add(new SavedSearchPanel(panel));
        }
    }
}
