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
package de.elmar_baumann.jpt.view.renderer;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * Basisklasse für Renderer, die Metadaten darstellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public class FormatterLabelMetadata {

    private Font headerFont = null;
    private Font contentFont = null;

    /**
     * Setzt die Standard-Vorder- und Hintergrundfarbe einer Zelle.
     * 
     * @param cellLabel   Label
     * @param isSelected  true, wenn die Zelle selektiert ist
     */
    protected static void setDefaultCellColors(JLabel cellLabel,
            boolean isSelected) {
        cellLabel.setForeground(isSelected
                                ? AppLookAndFeel.COLOR_FOREGROUND_TABLE_TEXT_SELECTED
                                : AppLookAndFeel.COLOR_FOREGROUND_TABLE_TEXT_DEFAULT);
        cellLabel.setBackground(isSelected
                                ? AppLookAndFeel.COLOR_BACKGROUND_TABLE_TEXT_SELECTED
                                : AppLookAndFeel.COLOR_BACKGROUND_TABLE_TEXT_DEFAULT);
        cellLabel.setOpaque(true);
    }

    /**
     * Setzt den Font eines Spaltenheaders.
     * 
     * @param cellLabel  Label
     */
    protected void setHeaderFont(JLabel cellLabel) {
        initFonts(cellLabel);
        cellLabel.setFont(headerFont);
    }

    /**
     * Setzt den Standardfont einer Zelle.
     * 
     * @param cellLabel  Label
     */
    protected void setContentFont(JLabel cellLabel) {
        initFonts(cellLabel);
        cellLabel.setFont(contentFont);
    }

    /**
     * Setzt die Farben von Zellen, deren Inhalt in der Datenbank gespeichert
     * ist.
     * 
     * @param cellLabel Label  Label
     * @param isSelected
     */
    protected void setIsStoredInDatabaseColors(JLabel cellLabel,
            boolean isSelected) {
        cellLabel.setForeground(
                isSelected
                ? AppLookAndFeel.COLOR_FOREGROUND_TABLE_TEXT_SELECTED
                : AppLookAndFeel.COLOR_FOREGROUND_TABLE_TEXT_STORED_IN_DATABASE);
        cellLabel.setBackground(
                isSelected
                ? AppLookAndFeel.COLOR_BACKGROUND_TABLE_TEXT_SELECTED
                : AppLookAndFeel.COLOR_BACKGROUND_TABLE_TEXT_STORED_IN_DATABASE);
    }

    private void initFonts(JLabel cellLabel) {
        if (headerFont == null) {
            Font cellFont = cellLabel.getFont();
            headerFont = new Font(
                    cellFont.getName(), Font.BOLD, cellFont.getSize());
            contentFont = new Font(
                    cellFont.getName(), Font.PLAIN, cellFont.getSize());
        }
    }
}
