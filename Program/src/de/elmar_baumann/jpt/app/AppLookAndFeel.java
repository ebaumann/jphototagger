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
package de.elmar_baumann.jpt.app;

import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import de.elmar_baumann.lib.dialog.SystemOutputDialog;
import de.elmar_baumann.lib.image.util.IconUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.ImageIcon;
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
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_STORED_IN_DATABASE = Color.BLACK;

    /**
     * Background color of table cells containing text which is stored in the
     * database
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_STORED_IN_DATABASE = new Color(251, 249, 241);

    /**
     * Foreground color of table cells containing text which is from an EXIF maker note
     */
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_EXIF_MAKER_NOTE = Color.BLACK;

    /**
     * Foreground color of table cells containing text which is from an EXIF maker note
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_EXIF_MAKER_NOTE = new Color(226, 226, 255);

    /**
     * Foreground color of selected table cells
     */
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_SELECTED = Color.BLACK;

    /**
     * Background color of selected table cells
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_SELECTED = new Color(226, 226, 255);

    /**
     * Default foreground color of table cells
     */
    public static final Color COLOR_FOREGROUND_TABLE_TEXT_DEFAULT = Color.BLACK;

    /**
     * Default background color of table cells
     */
    public static final Color COLOR_BACKGROUND_TABLE_TEXT_DEFAULT = Color.WHITE;

    /**
     * Background color of a keyword in a tree if a selected image has that keyword
     * of a selected image
     */
    public static final Color COLOR_BACKGROUND_KEYWORD_TREE_IMG_HAS_KEYWORD = new Color(100, 100, 100);

    /**
     * Foreground color of a keyword in a tree if a selected image has that keyword
     * of a selected image
     */
    public static final Color COLOR_FOREGROUND_KEYWORD_TREE_IMG_HAS_KEYWORD = new Color(255, 255, 255);

    /**
     * Selection foreground color of highlighted tree nodes when a popup menu
     * was invoked obove a tree node
     */
    public static final Color COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE = Color.BLACK;

    /**
     * Selection background color of highlighted tree nodes when a popup menu
     * was invoked obove a tree node
     */
    public static final Color COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE = new Color(251, 232, 158);

    /**
     * Selection foreground color of highlighted list items when a popup menu
     * was invoked obove a list item
     */
    public static final Color COLOR_FOREGROUND_POPUP_HIGHLIGHT_LIST = COLOR_FOREGROUND_POPUP_HIGHLIGHT_TREE;

    /**
     * Selection background color of highlighted list items when a popup menu
     * was invoked obove a list item
     */
    public static final Color COLOR_BACKGROUND_POPUP_HIGHLIGHT_LIST = COLOR_BACKGROUND_POPUP_HIGHLIGHT_TREE;

    /**
     * Path where all icons stored
     */
    private static final String PATH_ICONS = "/de/elmar_baumann/jpt/resource/icons";

    /**
     * Path to the small application's icon (16 x 16 pixels)
     */
    private static final String PATH_APP_ICON_SMALL = PATH_ICONS + "/icon_app_small.png";

    /**
     * Path to the medium sized application's icon (32 x 32 pixels)
     */
    private static final String       PATH_APP_ICON_MEDIUM = PATH_ICONS + "/icon_app_medium.png";
    private static final List<String> APP_ICON_PATHS       = new ArrayList<String>();
    private static final List<Image>  APP_ICONS            = new ArrayList<Image>();

    static {
        APP_ICON_PATHS.add(PATH_APP_ICON_SMALL);
        APP_ICON_PATHS.add(PATH_APP_ICON_MEDIUM);
    }

    static {
        APP_ICONS.add(IconUtil.getIconImage(PATH_APP_ICON_SMALL));
        APP_ICONS.add(IconUtil.getIconImage(PATH_APP_ICON_MEDIUM));
    }

    /**
     * Returns the application's icons (small and medium sized).
     *
     * @return icons
     */
    public static List<Image> getAppIcons() {
        return APP_ICONS;
    }

    /**
     * Returns the paths to the application's icons (small and medium sized).
     *
     * @return Pfade
     */
    public static List<String> getAppIconPaths() {
        return APP_ICON_PATHS;
    }

    /**
     * Converts a path to a localized path.
     * <p>
     * A localized path is the path where before the last path component the
     * default locale's language code will be added as path component. If the
     * language code is <code>"de"</code> and the path is
     * <code>"/de/elmar_baumann/jpt/resoure/images/image.png"</code>, the
     * localized path will be
     * <code>"/de/elmar_baumann/jpt/resoure/images/de/image.png"</code>.
     *
     * @param  path path
     * @return      localized path
     * @throws      NullPointerException if <code>path</code> is null
     * @throws      IllegalArgumentException if the trimmed path is empty
     */
    public static String toLocalizedPath(String path) {

        if (path == null         ) throw new NullPointerException("path == null");
        if (path.trim().isEmpty()) throw new IllegalArgumentException("path is empty!");

        String lang          = Locale.getDefault().getLanguage();
        int    lastPathDelim = path.lastIndexOf("/");

        return lastPathDelim >= 0
                ? path.substring(0, lastPathDelim + 1) + lang + "/" + path.substring(lastPathDelim + 1)
                : lang + "/" + path
                ;
    }

    /**
     * Returns a localized image if exists.
     * <p>
     * A localized icon has the same path plus the default locale's language
     * code before the last path component.
     *
     * @param  path not localized path, e.g. <code>"/de/elmar_baumann/jpt/resoure/images/image.png"</code>
     * @return      localized icon, e.g. if the path is the same as in the
     *              the parameter doc obove, the icon of the path
     *              <code>"/de/elmar_baumann/jpt/resoure/images/de/image.png"</code>.
     *              If a localized icon does not exist, the icon of the path
     *              will be returned or null if the icon of the path does not
     *              exist.
     */
    public static Image localizedImage(String path) {
        java.net.URL imgURL = IconUtil.class.getResource(toLocalizedPath(path));
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return IconUtil.getIconImage(path);
        }
    }

    /**
     * Returns an icon located in the application's icon path.
     *
     * @param  name  name of the icon file
     * @return icon
     */
    public static ImageIcon getIcon(String name) {
        return IconUtil.getImageIcon(PATH_ICONS + "/" + name);
    }
    /**
     * CSS of the table row headers
     */
    public static final String TABLE_CSS_ROW_HEADER = "margin-left:3px;margin-right:3px;";

    /**
     * CSS of the table cells
     */
    public static final String TABLE_CSS_CELL = "margin-left:3px;margin-right:3px;";

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
        setBoldFont("Button.font"             , false);
        setBoldFont("CheckBox.font"           , false);
        setBoldFont("CheckBoxMenuItem.font"   , false);
        setBoldFont("ColorChooser.font"       , false);
        setBoldFont("ComboBox.font"           , false);
        setBoldFont("EditorPane.font"         , false);
        setBoldFont("FormattedTextField.font" , false);
        setBoldFont("Label.font"              , false);
        setBoldFont("List.font"               , false);
        setBoldFont("MenuBar.font"            , false);
        setBoldFont("Menu.font"               , false);
        setBoldFont("MenuItem.font"           , false);
        setBoldFont("OptionPane.font"         , false);
        setBoldFont("Panel.font"              , false);
        setBoldFont("PopupMenu.font"          , false);
        setBoldFont("RadioButton.font"        , false);
        setBoldFont("RadioButtonMenuItem.font", false);
        setBoldFont("ScrollPane.font"         , false);
        setBoldFont("Slider.font"             , false);
        setBoldFont("Spinner.font"            , false);
        setBoldFont("TabbedPane.font"         , false);
        setBoldFont("Table.font"              , false);
        setBoldFont("TextArea.font"           , false);
        setBoldFont("TextField.font"          , false);
        setBoldFont("Text.font"               , false);
        setBoldFont("TextPane.font"           , false);
        setBoldFont("TextPane.font"           , false);
        setBoldFont("ToggleButton.font"       , false);
        setBoldFont("ToolBar.font"            , false);
        setBoldFont("ToolTip.font"            , false);
        setBoldFont("Tree.font"               , false);
        setBoldFont("Viewport.font"           , false);
        // Bold
        setBoldFont("PasswordField.font"      , true);
        setBoldFont("ProgressBar.font"        , true);
        setBoldFont("TableHeader.font"        , true);
        setBoldFont("TitledBorder.font.font"  , true);
    }

    private static void setBoldFont(String key, boolean bold) {

        Font defaultFont = UIManager.getFont(key);
        int  weight      = bold ? Font.BOLD : Font.PLAIN;

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
