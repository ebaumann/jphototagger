package org.jphototagger.program.app.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.util.LookAndFeelUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class AppLookAndFeel {

    private static final float FONT_SCALE = CommonPreferences.getFontScale();
    private static final String ICONS_PATH = "/org/jphototagger/program/resource/icons";
    public static final String TABLE_CELL_CSS = "margin-left:3px;margin-right:3px;";
    public static final int TABLE_MAX_CHARS_CELL = 45;
    public static final int TABLE_MAX_CHARS_ROW_HEADER = 40;
    public static final String TABLE_ROW_HEADER_CSS = "margin-left:3px;margin-right:3px;";
    private static Color tableSelectionForeground = Color.BLACK;
    private static Color tableSelectionBackground = new Color(226, 226, 255);
    private static Color tableForeground = Color.BLACK;
    private static Color tableBackground = Color.WHITE;
    public static final Color TREE_SEL_IMG_HAS_KEYWORD_FOREGROUND = Color.BLACK;
    public static final Color TREE_SEL_IMG_HAS_KEYWORD_BACKGROUND = new Color(255, 223, 181);
    public static final Color LIST_SEL_IMG_HAS_KEYWORD_FOREGROUND = TREE_SEL_IMG_HAS_KEYWORD_FOREGROUND;
    public static final Color LIST_SEL_IMG_HAS_KEYWORD_BACKGROUND = TREE_SEL_IMG_HAS_KEYWORD_BACKGROUND;
    public static final String SMALL_APP_ICON_PATH = ICONS_PATH + "/icon_app_small.png";
    public static final String MEDIUM_APP_ICON_PATH = ICONS_PATH + "/icon_app_medium.png";
    public static final Icon ICON_RENAME = getIcon("icon_rename.png");
    public static final Icon ICON_REFRESH = getIcon("icon_refresh.png");
    public static final Icon ICON_PASTE = getIcon("icon_paste.png");
    public static final Icon ICON_NEW = getIcon("icon_new.png");
    public static final Icon ICON_EDIT = getIcon("icon_edit.png");
    public static final Icon ICON_DELETE = getIcon("icon_delete.png");
    public static final Icon ICON_CUT = getIcon("icon_cut.png");
    public static final Icon ICON_COPY = getIcon("icon_copy.png");
    public static final Icon ICON_FILTER = getIcon("icon_filter.png");
    public static final Icon ICON_START = getIcon("icon_start.png");
    public static final Icon ICON_CANCEL = getIcon("icon_cancel.png");
    public static final Image ERROR_THUMBNAIL = IconUtil.getIconImage(Bundle.getString(AppLookAndFeel.class, "ErrorThumbnailPath"));
    private static final List<Image> APP_ICONS = new ArrayList<>();
    private static final List<String> APP_ICON_PATHS = new ArrayList<>();
    private static Color listBackground;
    private static Color listForeground;
    private static Color listSelectionBackground;
    private static Color listSelectionForeground;
    private static Color treeSelectionBackground;
    private static Color treeSelectionForeground;
    private static Color treeTextBackground;
    private static Color treeTextForeground;

    static {
        APP_ICON_PATHS.add(SMALL_APP_ICON_PATH);
        APP_ICON_PATHS.add(MEDIUM_APP_ICON_PATH);
    }

    static {
        APP_ICONS.add(IconUtil.getIconImage(SMALL_APP_ICON_PATH));
        APP_ICONS.add(IconUtil.getIconImage(MEDIUM_APP_ICON_PATH));
    }

    public static List<Image> getAppIcons() {
        return Collections.unmodifiableList(APP_ICONS);
    }

    public static List<String> getAppIconPaths() {
        return Collections.unmodifiableList(APP_ICON_PATHS);
    }

    /**
     * Converts a path to a localized path.
     * <p>
     * A localized path is the path where before the last path component the
     * default locale's language code will be added as path component. If the
     * language code is <code>"de"</code> and the path is
     * <code>"/org/jphototagger/program/resoure/images/image.png"</code>, the
     * localized path will be
     * <code>"/org/jphototagger/program/resoure/images/de/image.png"</code>.
     *
     * @param  path path
     * @return      localized path
     * @throws      NullPointerException if <code>path</code> is null
     * @throws      IllegalArgumentException if the trimmed path is empty
     */
    public static String toLocalizedPath(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        if (path.trim().isEmpty()) {
            throw new IllegalArgumentException("path is empty!");
        }

        String lang = Locale.getDefault().getLanguage();
        int lastPathDelim = path.lastIndexOf('/');

        return (lastPathDelim >= 0)
                ? path.substring(0, lastPathDelim + 1) + lang + "/" + path.substring(lastPathDelim + 1)
                : lang + "/" + path;
    }

    /**
     * Returns a localized image if exists.
     * <p>
     * A localized icon has the same path plus the default locale's language
     * code before the last path component.
     *
     * @param  path not localized path, e.g.
     *         <code>"/org/jphototagger/program/resoure/images/image.png"</code>
     * @return      localized icon, e.g. if the path is the same as in the
     *              the parameter doc obove, the icon of the path
     *     <code>"/org/jphototagger/program/resoure/images/de/image.png"</code>.
     *              If a localized icon does not exist, the icon of the path
     *              will be returned or null if the icon of the path does not
     *              exist.
     */
    public static Image getLocalizedImage(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }

        java.net.URL imgURL = IconUtil.class.getResource(toLocalizedPath(path));

        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return IconUtil.getIconImage(path);
        }
    }

    public static ImageIcon getIcon(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        ImageIcon icon = IconUtil.getImageIcon(ICONS_PATH + "/" + getScaledName(name));

        return icon == null
                ? IconUtil.getImageIcon(ICONS_PATH + "/" + name) // Trying to get unscaled icon
                : icon;
    }

    // All image names are assumed to be in lowercase due performance (not
    // creating an uppercase or lowercase name and compare with ".PNG" or ".png"
    private static String getScaledName(String name) {
        if (FONT_SCALE < 1.5) {
            return name;
        }

        if (!name.endsWith(".png")) {
            return name;
        }

        int index = name.lastIndexOf(".png");
        if (index < 1) {
            return name;
        }

        String sizePostfix = FONT_SCALE > 3
                ? "-64.png"
                : FONT_SCALE > 2.0
                ? "-48.png"
                : FONT_SCALE > 1.5
                ? "-32.png"
                : FONT_SCALE > 1.0
                ? "-24.png"
                : "";

        return name.substring(0, index) + sizePostfix;
    }

    public static void set() {
        try {
            lookupLookAndFeel();
        } catch (Throwable t) {
            Logger.getLogger(AppLookAndFeel.class.getName()).log(Level.SEVERE, null, t);
            LookAndFeelUtil.setSystemLookAndFeel();
        }
        setJPhotoTaggerDefaults();
    }

    private static void setJPhotoTaggerDefaults() {
        if (SystemUtil.isWindows()) {
            UIManager.put("FileChooser.useSystemExtensionHiding", true); // else FileSystemView#getSystemDisplayName() does display File#getName(), under Windows e.g. *not* localized "Users"
        }
        scaleFonts(FONT_SCALE);
        changeFontWeights();
        takeFromUiColors();
    }

    static final String PREF_KEY_LOOK_AND_FEEL = "AppLookAndFeel";

    private static void lookupLookAndFeel() {
        boolean set = false;
        try {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            if (!prefs.containsKey(PREF_KEY_LOOK_AND_FEEL)) {
                return;
            }
            for (LookAndFeelProvider provider : Lookup.getDefault().lookupAll(LookAndFeelProvider.class)) {
                if (prefs.getString(PREF_KEY_LOOK_AND_FEEL).equals(provider.getPreferencesKey())) {
                    provider.setLookAndFeel();
                    set = true;
                    return;
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(AppLookAndFeel.class.getName()).log(Level.SEVERE, null, t);
        } finally {
            if (!set) {
                LookAndFeelUtil.setSystemLookAndFeel();
            }
        }
    }

    private static void takeFromUiColors() {
        treeSelectionForeground = LookAndFeelUtil.getUiColor("Tree.selectionForeground");
        treeSelectionBackground = LookAndFeelUtil.getUiColor("Tree.selectionBackground");
        treeTextBackground = LookAndFeelUtil.getUiColor("Tree.textBackground");
        treeTextForeground = LookAndFeelUtil.getUiColor("Tree.textForeground");
        listSelectionForeground = LookAndFeelUtil.getUiColor("List.selectionForeground");
        listSelectionBackground = LookAndFeelUtil.getUiColor("List.selectionBackground");
        listBackground = LookAndFeelUtil.getUiColor("List.background");
        listForeground = LookAndFeelUtil.getUiColor("List.foreground");
        tableForeground = LookAndFeelUtil.getUiColor("Table.foreground");
        tableBackground = LookAndFeelUtil.getUiColor("Table.background ");
        tableSelectionForeground = LookAndFeelUtil.getUiColor("Table.selectionForeground");
        tableSelectionBackground = LookAndFeelUtil.getUiColor("Table.selectionBackground ");
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

    public static Color getTreeBackground() {
        return treeTextBackground;
    }

    public static Color getTreeForeground() {
        return treeTextForeground;
    }

    public static Color getTableSelectionForeground() {
        return tableSelectionForeground;
    }

    public static Color getTableSelectionBackground() {
        return tableSelectionBackground;
    }

    public static Color getTableForeground() {
        return tableForeground;
    }

    public static Color getTableBackground() {
        return tableBackground;
    }

    private static void changeFontWeights() {
        setBoldFont("Button.font", false);
        setBoldFont("CheckBox.font", false);
        setBoldFont("CheckBoxMenuItem.font", false);
        setBoldFont("ColorChooser.font", false);
        setBoldFont("ComboBox.font", false);
        setBoldFont("EditorPane.font", false);
        setBoldFont("FormattedTextField.font", false);
        setBoldFont("Label.font", false);
        setBoldFont("List.font", false);
        setBoldFont("MenuBar.font", false);
        setBoldFont("Menu.font", false);
        setBoldFont("MenuItem.font", false);
        setBoldFont("OptionPane.font", false);
        setBoldFont("Panel.font", false);
        setBoldFont("PopupMenu.font", false);
        setBoldFont("RadioButton.font", false);
        setBoldFont("RadioButtonMenuItem.font", false);
        setBoldFont("ScrollPane.font", false);
        setBoldFont("Slider.font", false);
        setBoldFont("Spinner.font", false);
        setBoldFont("TabbedPane.font", false);
        setBoldFont("Table.font", false);
        setBoldFont("TextArea.font", false);
        setBoldFont("TextField.font", false);
        setBoldFont("Text.font", false);
        setBoldFont("TextPane.font", false);
        setBoldFont("TextPane.font", false);
        setBoldFont("ToggleButton.font", false);
        setBoldFont("ToolBar.font", false);
        setBoldFont("ToolTip.font", false);
        setBoldFont("Tree.font", false);
        setBoldFont("Viewport.font", false);

        // Bold
        setBoldFont("PasswordField.font", true);
        setBoldFont("ProgressBar.font", true);
        setBoldFont("TableHeader.font", true);
        setBoldFont("TitledBorder.font.font", true);
    }

    private static void setBoldFont(String key, boolean bold) {
        Font defaultFont = UIManager.getFont(key);
        if (defaultFont != null) {
            int weight = bold
                    ? Font.BOLD
                    : Font.PLAIN;
            int style = defaultFont.isItalic()
                    ? weight | Font.ITALIC
                    : weight;
            Font derivedFont = defaultFont.deriveFont(style);
            UIManager.put(key, new FontUIResource(derivedFont));
        }
    }

    private static void scaleFonts(float scale) {
        if (scale == 1.0 || !CommonPreferences.isValidFontScale(scale)) {
            return;
        }
        for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
            if (key != null && key.toString().toLowerCase().contains("font")) {
                Font font = UIManager.getDefaults().getFont(key);
                if (font != null) {
                    int oldSize = font.getSize();
                    int newSize = Math.round(oldSize * scale);
                    font = font.deriveFont((float)newSize);
                    Logger.getLogger(AppLookAndFeel.class.getName()).log(Level.INFO, "Scaling font with scale {0}: {1}={2}", new Object[]{scale, key, font});
                    UIManager.put(key, font);
                }
            }
        }
    }

    private AppLookAndFeel() {
    }
}
