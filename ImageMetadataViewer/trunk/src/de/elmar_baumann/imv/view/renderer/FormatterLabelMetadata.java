package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppColors;
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
                                ? AppColors.COLOR_FOREGROUND_TABLE_TEXT_SELECTED
                                : AppColors.COLOR_FOREGROUND_TABLE_TEXT_DEFAULT);
        cellLabel.setBackground(isSelected
                                ? AppColors.COLOR_BACKGROUND_TABLE_TEXT_SELECTED
                                : AppColors.COLOR_BACKGROUND_TABLE_TEXT_DEFAULT);
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
                ? AppColors.COLOR_FOREGROUND_TABLE_TEXT_SELECTED
                : AppColors.COLOR_FOREGROUND_TABLE_TEXT_STORED_IN_DATABASE);
        cellLabel.setBackground(
                isSelected
                ? AppColors.COLOR_BACKGROUND_TABLE_TEXT_SELECTED
                : AppColors.COLOR_BACKGROUND_TABLE_TEXT_STORED_IN_DATABASE);
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
