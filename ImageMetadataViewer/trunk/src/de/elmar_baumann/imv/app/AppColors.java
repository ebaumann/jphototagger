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
    public static final Color colorForegroundTableTextStoredInDatabase =
            Color.BLACK;
    /**
     * Background color of table cells containing text which is stored in the
     * database
     */
    public static final Color colorBackgroundTableTextStoredInDatabase =
            new Color(251, 249, 241);
    /**
     * Foreground color of selected table cells
     */
    public static final Color colorForegroundTableTextSelected = Color.BLACK;
    /**
     * Background color of selected table cells
     */
    public static final Color colorBackgroundTableTextSelected =
            new Color(226, 226, 255);
    /**
     * Default foreground color of table cells
     */
    public static final Color colorForegroundTableTextDefault = Color.BLACK;
    /**
     * Default background color of table cells
     */
    public static final Color colorBackgroundTableTextDefault = Color.WHITE;

    private AppColors() {
    }
}
