package org.jphototagger.program.app.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

/**
 * @author Elmar Baumann
 */
public class MetadataLabelFormatter {

    public static final Color SELECTION_FOREGROUND = AppLookAndFeel.getTableSelectionForeground();
    public static final Color TABLE_BACKGROUND = AppLookAndFeel.getTableBackground();
    public static final Color TABLE_FOREGROUND = AppLookAndFeel.getTableForeground();
    public static final Color TABLE_SELECTION_BACKGROUND = AppLookAndFeel.getTableSelectionBackground();
    private Font headerFont = null;
    private Font contentFont = null;

    protected static void setDefaultCellColors(JLabel cellLabel, boolean isSelected) {
        cellLabel.setForeground(isSelected ? SELECTION_FOREGROUND : TABLE_FOREGROUND);
        cellLabel.setBackground(isSelected ? TABLE_SELECTION_BACKGROUND : TABLE_BACKGROUND);
        cellLabel.setOpaque(true);
    }

    protected void setHeaderFont(JLabel cellLabel) {
        initFonts(cellLabel);
        cellLabel.setFont(headerFont);
    }

    protected void setContentFont(JLabel cellLabel) {
        initFonts(cellLabel);
        cellLabel.setFont(contentFont);
    }

    private void initFonts(JLabel cellLabel) {
        if (headerFont == null) {
            Font cellFont = cellLabel.getFont();
            String cellFontName = cellFont.getName();
            int cellFontSize = cellFont.getSize();

            headerFont = new Font(cellFontName, Font.BOLD, cellFontSize);
            contentFont = new Font(cellFontName, Font.PLAIN, cellFontSize);
        }
    }
}
