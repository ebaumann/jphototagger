package de.elmar_baumann.imv.app;

import java.awt.Color;

/**
 * Definitions of application wide colors.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/19
 */
public final class AppColors {

    /**
     * Foreground color of table cells containing text which is stored in the
     * database
     */
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_STORED_IN_DATABASE =
            Color.BLACK;
    /**
     * Background color of table cells containing text which is stored in the
     * database
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_STORED_IN_DATABASE =
            new Color(251, 249, 241);
    /**
     * Foreground color of selected table cells
     */
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_SELECTED = Color.BLACK;
    /**
     * Background color of selected table cells
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_SELECTED =
            new Color(226, 226, 255);
    /**
     * Default foreground color of table cells
     */
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_DEFAULT = Color.BLACK;
    /**
     * Default background color of table cells
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_DEFAULT = Color.WHITE;

    private AppColors() {
    }
}
