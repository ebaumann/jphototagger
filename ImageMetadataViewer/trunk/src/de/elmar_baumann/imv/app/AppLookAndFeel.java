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

    public static void set() {
        LookAndFeelUtil.setSystemLookAndFeel();
        setFonts();
        SystemOutputDialog.INSTANCE.setIconImages(AppIcons.getAppIcons());
    }

    private static void setFonts() {
        setFontWeight("Button.font", false);
        setFontWeight("CheckBox.font", false);
        setFontWeight("CheckBoxMenuItem.font", false);
        setFontWeight("ColorChooser.font", false);
        setFontWeight("ComboBox.font", false);
        setFontWeight("EditorPane.font", false);
        setFontWeight("FormattedTextField.font", false);
        setFontWeight("Label.font", false);
        setFontWeight("List.font", false);
        setFontWeight("MenuBar.font", false);
        setFontWeight("Menu.font", false);
        setFontWeight("MenuItem.font", false);
        setFontWeight("OptionPane.font", false);
        setFontWeight("Panel.font", false);
        setFontWeight("PopupMenu.font", false);
        setFontWeight("RadioButton.font", false);
        setFontWeight("RadioButtonMenuItem.font", false);
        setFontWeight("ScrollPane.font", false);
        setFontWeight("Slider.font", false);
        setFontWeight("Spinner.font", false);
        setFontWeight("TabbedPane.font", false);
        setFontWeight("Table.font", false);
        setFontWeight("TextArea.font", false);
        setFontWeight("TextField.font", false);
        setFontWeight("Text.font", false);
        setFontWeight("TextPane.font", false);
        setFontWeight("TextPane.font", false);
        setFontWeight("ToggleButton.font", false);
        setFontWeight("ToolBar.font", false);
        setFontWeight("ToolTip.font", false);
        setFontWeight("Tree.font", false);
        setFontWeight("Viewport.font", false);
        // Bold
        setFontWeight("PasswordField.font", true);
        setFontWeight("ProgressBar.font", true);
        setFontWeight("TableHeader.font", true);
        setFontWeight("TitledBorder.font.font", true);
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
