package de.elmar_baumann.imv.app;

import java.awt.Color;

/**
 * Definitions of application wide colors.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-19
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
    /**
     * Background color of a hierarchical keyword if that keyword is a keyword
     * of a selected image
     */
    public static final Color COLOR_BACKGROUND_HIERARCHICAL_KEYWORD_TREE_IMG_HAS_KEYWORD =
            new Color(100, 100, 100);
    /**
     * Foreground color of a hierarchical keyword if that keyword is a keyword
     * of a selected image
     */
    public static final Color COLOR_FOREGROUND_HIERARCHICAL_KEYWORD_TREE_IMG_HAS_KEYWORD =
            new Color(255, 255, 255);
    /**
     * Selection foreground color of highlighted tree nodes when a popup menu
     * was invoked obove a tree node
     */
    public static final Color COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE =
            Color.BLACK;
    /**
     * Selection background color of highlighted tree nodes when a popup menu
     * was invoked obove a tree node
     */
    public static final Color COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE =
            new Color(251, 232, 158);
    /**
     * Selection foreground color of highlighted list items when a popup menu
     * was invoked obove a list item
     */
    public static final Color COLOR_FOREGROUND_POPUP_HIGHLIGHT_LIST =
            COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE;
    /**
     * Selection background color of highlighted list items when a popup menu
     * was invoked obove a list item
     */
    public static final Color COLOR_BACKGROUND_POPUP_HIGHLIGHT_LIST =
            COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE;

    private AppColors() {
    }
}
