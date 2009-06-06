package de.elmar_baumann.imv.app;

import de.elmar_baumann.lib.componentutil.LookAndFeelUtil;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Sets the look and feel of this application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/06
 */
public final class AppLookAndFeel {

    public static void set() {
        LookAndFeelUtil.setSystemLookAndFeel();
        setFonts();
    }

    private static void setFonts() {
        setFontWeight("Button.font", true);
        setFontWeight("Checkbox.font", true);
        setFontWeight("ColorChooser.font", true);
        setFontWeight("ComboBox.font", true);
        setFontWeight("EditorPane.font", true);
        setFontWeight("Label.font", true);
        setFontWeight("List.font", true);
        setFontWeight("MenuBar.font", true);
        setFontWeight("Menu.font", true);
        setFontWeight("MenuItem.font", true);
        setFontWeight("OptionPane.font", true);
        setFontWeight("Panel.font", true);
        setFontWeight("PopupMenu.font", true);
        setFontWeight("RadioButton.font", true);
        setFontWeight("ScrollPane.font", true);
        setFontWeight("Table.font", true);
        setFontWeight("TextArea.font", true);
        setFontWeight("TextField.font", true);
        setFontWeight("Text.font", true);
        setFontWeight("TextPane.font", true);
        setFontWeight("ToggleButton.font", true);
        setFontWeight("ToolBar.font", true);
        setFontWeight("ToolTip.font", true);
        setFontWeight("Tree.font", true);
        // Bold
        setFontWeight("PasswordField.font", true);
        setFontWeight("ProgressBar.font", true);
        setFontWeight("TableHeader.font", true);
        setFontWeight("TitledBorder.font.font", true);
    }

    private static void setFontWeight(String key, boolean bold) {
        Font defaultFont = UIManager.getFont(key);
        int weight = bold ? Font.BOLD : Font.PLAIN;
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
