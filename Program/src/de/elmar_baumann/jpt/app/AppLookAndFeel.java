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

import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
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
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-06
 */
public final class AppLookAndFeel {

    public static final Color TABLE_STORED_IN_DATABASE_FOREGROUND = Color.BLACK;
    public static final Color TABLE_STORED_IN_DATABASE_BACKGROUND = new Color(251, 249, 241);
    public static final Color TABLE_EXIF_MAKER_NOTE_FOREGROUND  = Color.BLACK;
    public static final Color TABLE_EXIF_MAKER_NOTE_BACKGROUND = new Color(226, 226, 255);
    public static final Color TABLE_SELECTION_FOREGROUND = Color.BLACK;
    public static final Color TABLE_SELECTION_BACKGROUND = new Color(226, 226, 255);
    public static final Color TABLE_TEXT_FOREGROUND = Color.BLACK;
    public static final Color TABLE_TEXT_BACKGROUND = Color.WHITE;
    public static final Color TREE_SEL_IMG_HAS_KEYWORD_BACKGROUND = new Color(100, 100, 100);
    public static final Color TREE_SEL_IMG_HAS_KEYWORD_FOREGROUND = new Color(255, 255, 255);
    private static Color treeSelectionForeground;
    private static Color treeSelectionBackground;
    private static Color treeTextBackground;
    private static Color treeTextForeground;
    private static Color listSelectionForeground;
    private static Color listSelectionBackground;
    private static Color listBackground;
    private static Color listForeground;

    public static final String TABLE_ROW_HEADER_CSS = "margin-left:3px;margin-right:3px;";
    public static final String TABLE_CELL_CSS = "margin-left:3px;margin-right:3px;";
    public static final int TABLE_MAX_CHARS_ROW_HEADER = 40;
    public static final int TABLE_MAX_CHARS_CELL = 45;

    private static final String ICONS_PATH = "/de/elmar_baumann/jpt/resource/icons";
    private static final String SMALL_APP_ICON_PATH = ICONS_PATH + "/icon_app_small.png";
    private static final String MEDIUM_APP_ICON_PATH = ICONS_PATH + "/icon_app_medium.png";
    private static final List<String> APP_ICON_PATHS = new ArrayList<String>();
    private static final List<Image>  APP_ICONS = new ArrayList<Image>();

    static {
        APP_ICON_PATHS.add(SMALL_APP_ICON_PATH);
        APP_ICON_PATHS.add(MEDIUM_APP_ICON_PATH);
    }

    static {
        APP_ICONS.add(IconUtil.getIconImage(SMALL_APP_ICON_PATH));
        APP_ICONS.add(IconUtil.getIconImage(MEDIUM_APP_ICON_PATH));
    }

    public static List<Image> getAppIcons() {
        return APP_ICONS;
    }

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
    public static Image getLocalizedImage(String path) {
        java.net.URL imgURL = IconUtil.class.getResource(toLocalizedPath(path));
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return IconUtil.getIconImage(path);
        }
    }

    public static ImageIcon getIcon(String name) {
        return IconUtil.getImageIcon(ICONS_PATH + "/" + name);
    }

    public static void set() {
        LookAndFeelUtil.setSystemLookAndFeel();
        setFonts();
        setUiColors();
    }

    private static void setUiColors() {
        treeSelectionForeground = ComponentUtil.getUiColor("Tree.selectionForeground");
        treeSelectionBackground = ComponentUtil.getUiColor("Tree.selectionBackground");
        treeTextBackground      = ComponentUtil.getUiColor("Tree.textBackground");
        treeTextForeground      = ComponentUtil.getUiColor("Tree.textForeground");
        listSelectionForeground = ComponentUtil.getUiColor("List.selectionForeground");
        listSelectionBackground = ComponentUtil.getUiColor("List.selectionBackground");
        listBackground          = ComponentUtil.getUiColor("List.background");
        listForeground          = ComponentUtil.getUiColor("List.foreground");
    }

    public static Color getListBackground() {
        return listBackground;
    }

    public static Color getListForeground() {
        return listForeground;
    }

    public static Color getListSelectionBackground() {
        return listSelectionBackground;
    }

    public static Color getListSelectionForeground() {
        return listSelectionForeground;
    }

    public static Color getTreeSelectionBackground() {
        return treeSelectionBackground;
    }

    public static Color getTreeSelectionForeground() {
        return treeSelectionForeground;
    }

    public static Color getTreeTextBackground() {
        return treeTextBackground;
    }

    public static Color getTreeTextForeground() {
        return treeTextForeground;
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
