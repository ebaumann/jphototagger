package org.jphototagger.program.view.renderer;

import java.awt.Font;

import javax.swing.JLabel;

import org.jphototagger.program.app.AppLookAndFeel;

/**
 * Basisklasse für Renderer, die Metadaten darstellen.
 *
 * @author Elmar Baumann
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
    protected static void setDefaultCellColors(JLabel cellLabel, boolean isSelected) {
        if (cellLabel == null) {
            throw new NullPointerException("cellLabel == null");
        }

        cellLabel.setForeground(isSelected
                ? AppLookAndFeel.getTableSelectionForeground()
                : AppLookAndFeel.getTableForeground());
        cellLabel.setBackground(isSelected
                ? AppLookAndFeel.getTableSelectionBackground()
                : AppLookAndFeel.getTableBackground());
        cellLabel.setOpaque(true);
    }

    /**
     * Setzt den Font eines Spaltenheaders.
     *
     * @param cellLabel  Label
     */
    protected void setHeaderFont(JLabel cellLabel) {
        if (cellLabel == null) {
            throw new NullPointerException("cellLabel == null");
        }

        initFonts(cellLabel);
        cellLabel.setFont(headerFont);
    }

    /**
     * Setzt den Standardfont einer Zelle.
     *
     * @param cellLabel  Label
     */
    protected void setContentFont(JLabel cellLabel) {
        if (cellLabel == null) {
            throw new NullPointerException("cellLabel == null");
        }

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
    protected void setIsStoredInRepositoryColors(JLabel cellLabel, boolean isSelected) {
        if (cellLabel == null) {
            throw new NullPointerException("cellLabel == null");
        }

        cellLabel.setForeground(isSelected
                ? AppLookAndFeel.getTableSelectionForeground()
                : AppLookAndFeel.getTableStoredInRepositoryForeground());
        cellLabel.setBackground(isSelected
                ? AppLookAndFeel.getTableSelectionBackground()
                : AppLookAndFeel.getTableStoredInRepositoryBackground());
    }

    protected void setIsExifMakerNoteColors(JLabel cellLabel, boolean isSelected) {
        if (cellLabel == null) {
            throw new NullPointerException("cellLabel == null");
        }

        cellLabel.setForeground(isSelected
                ? AppLookAndFeel.getTableSelectionForeground()
                : AppLookAndFeel.getTableExifMakerNoteForeground());
        cellLabel.setBackground(isSelected
                ? AppLookAndFeel.getTableSelectionBackground()
                : AppLookAndFeel.getTableExifMakerNoteBackground());
    }

    private void initFonts(JLabel cellLabel) {
        if (headerFont == null) {
            Font cellFont = cellLabel.getFont();

            headerFont = new Font(cellFont.getName(), Font.BOLD, cellFont.getSize());
            contentFont = new Font(cellFont.getName(), Font.PLAIN, cellFont.getSize());
        }
    }
}
