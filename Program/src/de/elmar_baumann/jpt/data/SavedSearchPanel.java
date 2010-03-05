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

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Daten gespeicherter Suchen für ein {@link de.elmar_baumann.jpt.view.panels.SearchColumnPanel}-Objekt.
 * Die Indexe sind Indexe von Listenitems in Comboboxen oder Listboxen.
 *
 * @author  Elmar Baumann
 * @version 2008-09-12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class SavedSearchPanel {
    private int     panelIndex = Integer.MIN_VALUE;
    private boolean bracketLeft1Selected;
    private boolean bracketLeft2Selected;
    private boolean bracketRightSelected;
    private int     operatorId   = -1;
    private int     columnId     = -1;
    private int     comparatorId = -1;
    private String  value;

    public SavedSearchPanel() {}

    public SavedSearchPanel(SavedSearchPanel other) {
        panelIndex           = other.panelIndex;
        bracketLeft1Selected = other.bracketLeft1Selected;
        bracketLeft2Selected = other.bracketLeft2Selected;
        bracketRightSelected = other.bracketRightSelected;
        operatorId           = other.operatorId;
        columnId             = other.columnId;
        comparatorId         = other.comparatorId;
        value                = other.value;
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

    public void setColumnId(int id) {
        columnId = id;
    }

    public int getComparatorId() {
        return comparatorId;
    }

    public void setComparatorId(int id) {
        comparatorId = id;
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

    public void setOperatorId(int id) {
        operatorId = id;
    }

    public boolean hasValue() {
        return value != null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
