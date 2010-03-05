/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class SavedSearch {

    public enum Type {
        /**
         * Saved search was generated through dialog, search column panels
         */
        PANELS      ((short)0),

        /**
         * Saved search is a custom SQL Query
         */
        CUSTOM_SQL  ((short)1),
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
                if (t.getValue() == value) return t;
            }
            return null;
        }
    }

    private SavedSearchParamStatement paramStatement;

    @XmlElementWrapper(name = "SavedSearchPanels")
    @XmlElement(type = SavedSearchPanel.class)
    private List<SavedSearchPanel> panels;

    private Type type;

    public SavedSearch() {
    }

    public SavedSearch(SavedSearch other) {
        set(other);
    }

    public void set(SavedSearch other) {
        if (other == this) return;

        paramStatement.set(other.paramStatement);
        panels = other.getDeepCopyPanels();
        type   = other.type;
    }

    public List<SavedSearchPanel> getPanels() {
        return panels == null
               ? null
               : getDeepCopyPanels();
    }

    public void setPanels(List<SavedSearchPanel> panels) {
        if (panels == null) {
            this.panels = null;
        } else {
            setDeepCopyPanels(panels);
        }
    }

    public void addPanel(SavedSearchPanel panel) {
        if (panels == null) {
            panels = new ArrayList<SavedSearchPanel>();
        }
        panels.add(panel);
    }

    public SavedSearchParamStatement getParamStatement() {
        return paramStatement == null
               ? null
               : new SavedSearchParamStatement(paramStatement);
    }

    public void setParamStatement(SavedSearchParamStatement paramStatement) {
        this.paramStatement = new SavedSearchParamStatement(paramStatement);
    }

    public boolean hasParamStatement() {
        return paramStatement != null;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean hasPanels() {
        return panels != null && !panels.isEmpty();
    }

    public boolean isCustomSql() {
        return type != null && type.equals(Type.CUSTOM_SQL);
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

    public String getName() {
        String string = null;
        if (paramStatement != null) {
            string = paramStatement.getName();
        }
        return (string == null
                ? ""
                : string);
    }

    public void setName(String name) {
        if (paramStatement == null) return;
        paramStatement.setName(name);
    }

    @Override
    public String toString() {
        return getName(); // Never change that (will be used to find model items)!
    }

    private List<SavedSearchPanel> getDeepCopyPanels() {
        if (panels == null) return null;

        List<SavedSearchPanel> copy = new ArrayList<SavedSearchPanel>(panels.size());

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
}
