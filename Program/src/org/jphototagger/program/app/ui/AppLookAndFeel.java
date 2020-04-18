package org.jphototagger.program.app.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.swing.util.LookAndFeelUtil;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.SystemUtil;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class AppLookAndFeel {

    private static final float FONT_SCALE = CommonPreferences.getFontScale();
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
    private static Color listBackground;
    private static Color listForeground;
    private static Color listSelectionBackground;
    private static Color listSelectionForeground;
    private static Color treeSelectionBackground;
    private static Color treeSelectionForeground;
    private static Color treeTextBackground;
    private static Color treeTextForeground;

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
        setDefaultIcons();
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
        treeSelectionForeground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Tree.selectionForeground"), treeSelectionForeground);
        treeSelectionBackground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Tree.selectionBackground"), treeSelectionBackground);
        treeTextBackground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Tree.textBackground"), treeTextBackground);
        treeTextForeground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Tree.textForeground"), treeTextForeground);
        listSelectionForeground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("List.selectionForeground"), listSelectionForeground);
        listSelectionBackground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("List.selectionBackground"), listSelectionBackground);
        listBackground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("List.background"), listBackground);
        listForeground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("List.foreground"), listForeground);
        tableForeground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Table.foreground"), tableForeground);
        tableBackground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Table.background"), tableBackground);
        tableSelectionForeground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Table.selectionForeground"), tableSelectionForeground);
        tableSelectionBackground = ObjectUtil.firstNonNull(LookAndFeelUtil.getUiColor("Table.selectionBackground"), tableSelectionBackground);
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

    private static void setDefaultIcons() {
        String size = FONT_SCALE < 2f ? "-32" : "-64";

        UIManager.put("OptionPane.questionIcon", Icons.getUnscaledIcon("icon_question" + size + ".png"));
        UIManager.put("OptionPane.warningIcon", Icons.getUnscaledIcon("icon_warning" + size + ".png"));
        UIManager.put("OptionPane.informationIcon", Icons.getUnscaledIcon("icon_information" + size + ".png"));
        UIManager.put("OptionPane.errorIcon", Icons.getUnscaledIcon("icon_error_2" + size + ".png"));
    }

    private AppLookAndFeel() {
    }
}
