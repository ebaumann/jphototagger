package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.AppSettings;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * Basisklasse für Renderer, die Metadaten darstellen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class TableCellRendererMetadata {

    private Font headerFont = null;
    private Font contentFont = null;
    protected static final String paddingLeft = " "; // NOI18N

    /**
     * Setzt die Standard-Vorder- und Hintergrundfarbe einer Zelle.
     * 
     * @param cellLabel   Label
     * @param isSelected  true, wenn die Zelle selektiert ist
     */
    protected static void setDefaultCellColors(JLabel cellLabel, boolean isSelected) {
        cellLabel.setForeground(isSelected
            ? AppSettings.colorForegroundTableTextSelected
            : AppSettings.colorForegroundTableTextDefault);
        cellLabel.setBackground(isSelected
            ? AppSettings.colorBackgroundTableTextSelected
            : AppSettings.colorBackgroundTableTextDefault);
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
     */
    protected void setIsStoredInDatabaseColors(JLabel cellLabel) {
        cellLabel.setForeground(AppSettings.colorForegroundTableTextStoredInDatabase);
        cellLabel.setBackground(AppSettings.colorBackgroundTableTextStoredInDatabase);
    }

    private void initFonts(JLabel cellLabel) {
        if (headerFont == null) {
            Font cellFont = cellLabel.getFont();
            headerFont = new Font(cellFont.getName(), Font.BOLD, cellFont.getSize());
            contentFont = new Font(cellFont.getName(), Font.PLAIN, cellFont.getSize());
        }
    }
}
