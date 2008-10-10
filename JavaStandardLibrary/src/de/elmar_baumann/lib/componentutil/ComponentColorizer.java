package de.elmar_baumann.lib.componentutil;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

/**
 * Sets the colors of the components instead the UI manager. 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/09
 */
public class ComponentColorizer {

    ComponentColors colors;

    public ComponentColorizer(ComponentColors colors, Container container) {
        this.colors = colors;
        init();
        setColors(container);
    }

    private void setColors(Container container) {
        Component[] components = container.getComponents();
        Component component;

        container.setBackground(colors.defaultBackground);
        for (int index = 0; index < components.length; index++) {
            component = container.getComponent(index);
            boolean set = checkMenuBar(component) &&
                checkButton(component) &&
                checkCombobox(component) &&
                checkLabel(component) &&
                checkCheckBox(component) &&
                checkEditorPane(component) &&
                checkTextField(component);
        }
    }

    private void set(JLabel label) {
        label.setForeground(colors.labelForeground);
        label.setBackground(colors.labelBackground);
    }

    private void set(JCheckBox checkBox) {
        checkBox.setForeground(colors.labelForeground);
        checkBox.setBackground(colors.labelBackground);
    }

    private void set(JTextField textField) {
        textField.setForeground(colors.inputForeground);
        textField.setBackground(colors.inputBackground);
        textField.setBorder(new LineBorder(colors.inputBorder));
    }

    private void set(JButton button) {
        button.setForeground(colors.buttonForeground);
        button.setBackground(colors.buttonBackground);
        button.setBorder(new LineBorder(colors.buttonBorder));
    }

    private void set(JComboBox comboBox) {
        comboBox.setForeground(colors.inputForeground);
        comboBox.setBackground(colors.inputBackground);
        try {
            JTextField cbEdit = (JTextField) comboBox.getEditor().getEditorComponent();
            cbEdit.setForeground(colors.inputForeground);
            cbEdit.setBackground(colors.inputBackground);
            cbEdit.setBorder(new LineBorder(colors.inputBorder));
        } catch (ClassCastException ex) {
            Logger.getLogger(ComponentColorizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void set(JEditorPane editorPane) {
        editorPane.setBorder(new LineBorder(colors.editorPaneBorder));
        editorPane.setBackground(colors.editorPaneBackground);
        editorPane.setForeground(colors.editorPaneForeground);
    }

    private void set(JMenuBar menuBar) {
        menuBar.setBackground(colors.menuBarBackground);
        menuBar.setForeground(colors.menuBarForeground);
        for (int menuIndex = 0; menuIndex < menuBar.getMenuCount(); menuIndex++) {
            JMenu menu = menuBar.getMenu(menuIndex);
            set(menu);
            for (int itemIndex = 0; itemIndex < menu.getItemCount(); itemIndex++) {
                set(menu.getItem(itemIndex));
            }
        }
    }

    private void set(JMenu menu) {
        menu.setBackground(colors.menuBackground);
        menu.setForeground(colors.menuForeground);
    }

    private void set(JMenuItem menuItem) {
        menuItem.setBackground(colors.menuItemBackground);
        menuItem.setForeground(colors.menuItemForeground);
    }

    private boolean checkTextField(Component component) {
        if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            set(textField);
            return true;
        }
        return false;
    }

    private boolean checkEditorPane(Component component) {
        if (component instanceof JEditorPane) {
            JEditorPane editorPane = (JEditorPane) component;
            set(editorPane);
            return true;
        }
        return false;
    }

    private boolean checkCheckBox(Component component) {
        if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            set(checkBox);
            return true;
        }
        return false;
    }

    private boolean checkLabel(Component component) {
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            set(label);
            return true;
        }
        return false;
    }

    private boolean checkCombobox(Component component) {
        if (component instanceof JComboBox) {
            JComboBox comboBox = (JComboBox) component;
            set(comboBox);
            return true;
        }
        return false;
    }

    private boolean checkButton(Component component) {
        if (component instanceof JButton) {
            JButton button = (JButton) component;
            set(button);
            return true;
        }
        return false;
    }

    private boolean checkMenuBar(Component component) {
        if (component instanceof JMenuBar) {
            JMenuBar menuBar = (JMenuBar) component;
            set(menuBar);
            return true;
        }
        return false;
    }

    private void init() {
        UIManager.put("Panel.background", colors.labelBackground);
        UIManager.put("Button.foreground", colors.buttonForeground);
        UIManager.put("Button.background", colors.buttonBackground);

        UIManager.put("Menu.selectionBackground", colors.menuSelectedBackground);
        UIManager.put("MenuItem.selectionBackground", colors.menuItemSelectedBackground);

        UIManager.put("TabbedPane.selected", colors.tabSelectedBackground);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        UIManager.put("OptionPane.background", colors.labelBackground);
        UIManager.put("OptionPane.messageForeground", colors.labelForeground);

        UIManager.put("TextField.selectionBackground", colors.inputSelectedBackground);
        UIManager.put("TextField.selectionForeground", colors.inputSelectedForeground);

        UIManager.put("ComboBox.selectionBackground", colors.inputSelectedBackground);
        UIManager.put("ComboBox.selectionForeground", colors.inputSelectedForeground);

        UIManager.put("ToolTip.background", colors.labelBackground);
    }
}
