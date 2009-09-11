package de.elmar_baumann.imv.app;

import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import de.elmar_baumann.lib.dialog.SystemOutputDialog;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Look and feel of this application.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-06
 */
public final class AppLookAndFeel {

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
    /**
     * Path where all icons stored
     */
    private static final String PATH_ICONS =
            "/de/elmar_baumann/imv/resource/icons"; // NOI18N
    /**
     * Path to the small application's icon (16 x 16 pixels)
     */
    private static final String PATH_APP_ICON_SMALL =
            PATH_ICONS + "/icon_app_small.png";  // NOI18N
    /**
     * Path to the medium sized application's icon (32 x 32 pixels)
     */
    private static final String PATH_APP_ICON_MEDIUM =
            PATH_ICONS + "/icon_app_medium.png";  // NOI18N
    private static List<String> appIconPaths = new ArrayList<String>();
    private static List<Image> appIcons = new ArrayList<Image>();

    static {
        appIconPaths.add(PATH_APP_ICON_SMALL);
        appIconPaths.add(PATH_APP_ICON_MEDIUM);
    }

    static {
        appIcons.add(IconUtil.getIconImage(PATH_APP_ICON_SMALL));
        appIcons.add(IconUtil.getIconImage(PATH_APP_ICON_MEDIUM));
    }

    /**
     * Returns the application's icons (small and medium sized).
     *
     * @return icons
     */
    public static List<Image> getAppIcons() {
        return appIcons;
    }

    /**
     * Returns the paths to the application's icons (small and medium sized).
     *
     * @return Pfade
     */
    public static List<String> getAppIconPaths() {
        return appIconPaths;
    }

    /**
     * Returns an icon located in the application's icon path.
     *
     * @param  name  name of the icon file
     * @return icon
     */
    public static Icon getIcon(String name) {
        return IconUtil.getImageIcon(PATH_ICONS + "/" + name); // NOI18N
    }
    /**
     * CSS of the table row headers
     */
    public static final String TABLE_CSS_ROW_HEADER =
            "margin-left:3px;margin-right:3px;"; // NOI18N
    /**
     * CSS of the table cells
     */
    public static final String TABLE_CSS_CELL =
            "margin-left:3px;margin-right:3px;"; // NOI18N
    /**
     * Maximum character count in the table row headers before breaking into
     * lines
     */
    public static final int TABLE_MAX_CHARS_ROW_HEADER = 40;
    /**
     * Maximum character count in the table cells before breaking into
     * lines
     */
    public static final int TABLE_MAX_CHARS_CELL = 45;

    public static void set() {
        LookAndFeelUtil.setSystemLookAndFeel();
        setFonts();
        SystemOutputDialog.INSTANCE.setIconImages(getAppIcons());
    }

    private static void setFonts() {
        setFontWeight("Button.font", false); // NOI18N
        setFontWeight("CheckBox.font", false); // NOI18N
        setFontWeight("CheckBoxMenuItem.font", false); // NOI18N
        setFontWeight("ColorChooser.font", false); // NOI18N
        setFontWeight("ComboBox.font", false); // NOI18N
        setFontWeight("EditorPane.font", false); // NOI18N
        setFontWeight("FormattedTextField.font", false); // NOI18N
        setFontWeight("Label.font", false); // NOI18N
        setFontWeight("List.font", false); // NOI18N
        setFontWeight("MenuBar.font", false); // NOI18N
        setFontWeight("Menu.font", false); // NOI18N
        setFontWeight("MenuItem.font", false); // NOI18N
        setFontWeight("OptionPane.font", false); // NOI18N
        setFontWeight("Panel.font", false); // NOI18N
        setFontWeight("PopupMenu.font", false); // NOI18N
        setFontWeight("RadioButton.font", false); // NOI18N
        setFontWeight("RadioButtonMenuItem.font", false); // NOI18N
        setFontWeight("ScrollPane.font", false); // NOI18N
        setFontWeight("Slider.font", false); // NOI18N
        setFontWeight("Spinner.font", false); // NOI18N
        setFontWeight("TabbedPane.font", false); // NOI18N
        setFontWeight("Table.font", false); // NOI18N
        setFontWeight("TextArea.font", false); // NOI18N
        setFontWeight("TextField.font", false); // NOI18N
        setFontWeight("Text.font", false); // NOI18N
        setFontWeight("TextPane.font", false); // NOI18N
        setFontWeight("TextPane.font", false); // NOI18N
        setFontWeight("ToggleButton.font", false); // NOI18N
        setFontWeight("ToolBar.font", false); // NOI18N
        setFontWeight("ToolTip.font", false); // NOI18N
        setFontWeight("Tree.font", false); // NOI18N
        setFontWeight("Viewport.font", false); // NOI18N
        // Bold
        setFontWeight("PasswordField.font", true); // NOI18N
        setFontWeight("ProgressBar.font", true); // NOI18N
        setFontWeight("TableHeader.font", true); // NOI18N
        setFontWeight("TitledBorder.font.font", true); // NOI18N
    }

    private static void setFontWeight(String key, boolean bold) {
        Font defaultFont = UIManager.getFont(key);
        int weight = bold
                ? Font.BOLD
                : Font.PLAIN;
        if (defaultFont != null) {
            Font plainFont = new Font(defaultFont.getName(),
                    defaultFont.isItalic()
                    ? weight | Font.ITALIC
                    : Font.PLAIN,
                    defaultFont.getSize());
            UIManager.put(key, new FontUIResource(plainFont));
        }
    }

    private AppLookAndFeel() {
    }
}
