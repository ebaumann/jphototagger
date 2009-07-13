package de.elmar_baumann.imv.app;

import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import de.elmar_baumann.lib.dialog.SystemOutputDialog;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Sets the look and feel of this application.
 * 
 * Currently only the font weights are set (bold or not) to look consistent
 * under certain operation systems.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/06
 */
public final class AppLookAndFeel {

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
        SystemOutputDialog.INSTANCE.setIconImages(AppIcons.getAppIcons());
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
