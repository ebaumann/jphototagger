/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.util;

import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

/**
 * A settings object writes the state of several objects to a
 * {@link java.util.Properties} instance and resets the state of these objects
 * from an {@link java.util.Properties} instance.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-23
 */
public final class Settings {

    private final        Properties properties;
    private static final String     DELIMITER_NUMBER_ARRAY               = ";";
    private static final String     DELIMITER_ARRAY_KEYS                 = ".";
    private static final String     FILE_PATH_SEPARATOR                  = "|";
    private static final String     KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X = ".ViewportViewPositionX";
    private static final String     KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y = ".ViewportViewPositionY";
    private static final String     KEY_APPENDIX_SELECTED                = "-selected";
    private static final String     POSTFIX_KEY_WIDTH                    = ".Width";
    private static final String     POSTFIX_KEY_HEIGHT                   = ".Height";
    private static final String     POSTFIX_KEY_LOCATION_X               = ".LocationX";
    private static final String     POSTFIX_KEY_LOCATION_Y               = ".LocationY";

    public Settings(Properties properties) {

        if (properties == null) throw new NullPointerException("properties == null");

        this.properties = properties;
    }

    /**
     * Liefert alle Schlüssel, die auf ein Muster passen.
     *
     * @param pattern Muster
     * @return        Passende Schlüssel
     */
    public List<String> getKeysMatching(String pattern) {

        if (pattern == null) throw new NullPointerException("pattern == null");

        return RegexUtil.getMatches(properties.stringPropertyNames(), pattern);
    }

    /**
     * Removes all keys with no value
     */
    public void removeKeysWithEmptyValues() {

        Set<String> keys = properties.stringPropertyNames();

        for (String key : keys) {

            String value = properties.getProperty(key);

            if (value == null || value.isEmpty()) {
                properties.remove(key);
            }
        }
    }

    /**
     * Setzt bei den Attributen einer Komponente den gespeicherten Inhalt und
     * zwar bei den Attributen, die set() speichert.
     *
     * @param component Komponente
     * @param hints     Hinweise
     */
    public void applySettings(Component component, SettingsHints hints) {

        if (component == null) throw new NullPointerException("component == null");
        if (hints == null    ) throw new NullPointerException("hints == null");

        final Class<? extends Component> clazz         = component.getClass();
        final String                     componentName = clazz.getName();
        final Field[]                    fields        = clazz.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {

            final Field field = fields[index];

            field.setAccessible(true);

            final String fieldName = field.getName();
            final String key       = componentName + "." + fieldName;

            if (hints.isSet(key)) {
                try {
                    final Class<?> fieldType = field.getType();

                    if (fieldType.equals(JTabbedPane.class)) {

                        applySettings((JTabbedPane) field.get(component), key, hints);

                    } else if (fieldType.equals(JTable.class)) {

                        applySettings((JTable) field.get(component), key);

                    } else if (fieldType.equals(JTree.class)) {

                        applySettings((JTree) field.get(component), key);

                    } else if (fieldType.equals(JComboBox.class)) {

                        if (hints.isOption(SettingsHints.Option.SET_COMBOBOX_CONTENT)) {

                            insertStringItems((JComboBox) field.get(component), key);
                        } else {
                            applySelectedIndex((JComboBox) field.get(component), key);
                        }
                    } else if (fieldType.equals(JList.class)) {

                        if (hints.isOption(SettingsHints.Option.SET_LIST_CONTENT)) {

                            insertStringElements((JList) field.get(component), key);
                        } else {
                            applySelectedIndices((JList) field.get(component), key);
                        }
                    } else if (fieldType.equals(JTextField.class)) {

                        applySettings(((JTextField) field.get(component)), key);

                    } else if (fieldType.equals(JCheckBox.class)) {

                        applySettings((JCheckBox) field.get(component), key);

                    } else if (fieldType.equals(JSpinner.class)) {

                        applySettings((JSpinner) field.get(component), key);

                    } else if (fieldType.equals(ButtonGroup.class)) {

                        applySettings((ButtonGroup) field.get(component), key);
                    }
                } catch (Exception ex) {

                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Setzt den Zustand einer Checkbox (angekreuzt oder nicht).
     *
     * @param checkBox Checkbox
     * @param key      Schlüssel
     */
    public void applySettings(JCheckBox checkBox, String key) {

        if (checkBox == null) throw new NullPointerException("checkBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        if (properties.containsKey(key)) {

            String value = properties.getProperty(key);

            checkBox.setSelected(value.equals("1"));
        }
    }

    /**
     * Selects a button of a button group.
     *
     * @param buttonGroup button group
     * @param key         key
     */
    public void applySettings(ButtonGroup buttonGroup, String key) {

        if (buttonGroup == null) throw new NullPointerException("buttonGroup == null");
        if (key         == null) throw new NullPointerException("key == null");

        if (properties.containsKey(key)) {

            String textOfSelectedButton = properties.getProperty(key);

            for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {

                AbstractButton button = buttons.nextElement();

                if (button.getText().equals(textOfSelectedButton)) {

                    button.setSelected(true);
                    return;
                }
            }
        }
    }

    /**
     * Setzt den Zustand eines Trees, aktuell den selektierten Pfad
     * (nur <strong>eine</strong> Selektion).
     *
     * @param tree Tree
     * @param key  Schlüssel
     */
    public void applySettings(JTree tree, String key) {

        if (tree == null) throw new NullPointerException("tree == null");
        if (key  == null) throw new NullPointerException("key == null");

        tree.clearSelection();

        if (properties.containsKey(key)) {

            String   value = properties.getProperty(key);
            TreePath path  = TreeUtil.getTreePath(tree.getModel(), value, FILE_PATH_SEPARATOR);

            if (path != null) {

                TreeUtil.expandPath(tree, path);
                tree.scrollPathToVisible(path);
                tree.setSelectionPath(path);
            }
        }
    }

    /**
     * Setzt einer Splitpane (aktuell: Position des Dividers).
     *
     * @param splitPane Splitpane
     * @param key       Schlüssel
     */
    public void applySettings(JSplitPane splitPane, String key) {

        if (splitPane == null) throw new NullPointerException("splitPane == null");
        if (key       == null) throw new NullPointerException("key == null");

        if (properties.containsKey(key)) {
            try {
                int location = Integer.parseInt(properties.getProperty(key));

                splitPane.setDividerLocation(location);

            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt  die Eigenschaften einer ScrollPane (aktuell die View-Position des Viewports).
     *
     * @param scrollPane ScrollPane
     * @param key        Schlüssel
     */
    public void applySettings(JScrollPane scrollPane, String key) {

        if (scrollPane == null) throw new NullPointerException("scrollPane == null");
        if (key        == null) throw new NullPointerException("key == null");

        String keyX = key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X;
        String keyY = key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y;

        if (properties.containsKey(keyX) && properties.containsKey(keyY)) {
            try {
                int x = Integer.parseInt(properties.getProperty(keyX));
                int y = Integer.parseInt(properties.getProperty(keyY));

                scrollPane.getViewport().setViewPosition(new Point(x, y));

            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt die Eigenschaften einer Tabelle (aktuell: Spaltenbreiten).
     *
     * @param table Tabelle
     * @param key   Schlüssel
     */
    public void applySettings(JTable table, String key) {

        if (table == null) throw new NullPointerException("table == null");
        if (key   == null) throw new NullPointerException("key == null");

        TableModel model = table.getModel();

        if (properties.containsKey(key)) {

            TableColumnModel colModel          = table.getColumnModel();
            List<Integer>    storedWidths      = getIntegerCollection(key);
            int              tableColumnCount  = model.getColumnCount();
            int              storedColumnCount = storedWidths.size();

            for (int index = 0; index < tableColumnCount && index < storedColumnCount; index++) {

                colModel.getColumn(index).setPreferredWidth(storedWidths.get(index));
            }
        }
    }

    /**
     * Setzt den Wert eines Spinners.
     *
     * @param spinner Spinner
     * @param key     Schlüssel
     */
    public void applySettings(JSpinner spinner, String key) {

        if (spinner == null) throw new NullPointerException("spinner == null");
        if (key     == null) throw new NullPointerException("key == null");

        if (properties.containsKey(key)) {

            String value = properties.getProperty(key);
            try {
                spinner.setValue(Integer.parseInt(value));

            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt den Zustand einer Tabbedpane und ihrer Komponenten.
     *
     * @param pane  Pane
     * @param key   Schlüssel
     * @param hints Hinweise
     */
    public void applySettings(JTabbedPane pane, String key, SettingsHints hints) {

        if (pane  == null) throw new NullPointerException("pane == null");
        if (key   == null) throw new NullPointerException("key == null");
        if (hints == null) throw new NullPointerException("hints == null");

        if (properties.containsKey(key)) {

            String value = properties.getProperty(key);

            try {
                int index = Integer.parseInt(value);

                if (index < pane.getTabCount()) {

                    pane.setSelectedIndex(index);
                }
            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (hints.isOption(SettingsHints.Option.SET_TABBED_PANE_CONTENT)) {

            int componentCount = pane.getComponentCount();

            for (int index = 0; index < componentCount; index++) {

                applySettings(pane.getComponentAt(index), hints);
            }
        }
    }

    /**
     * Adds strings as items to a combo box. Existing items will be removed.
     *
     * @param comboBox combo box
     * @param key      key for the string items
     */
    public void insertStringItems(JComboBox comboBox, String key) {

        if (comboBox == null) throw new NullPointerException("comboBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        List<String> keys = getKeysMatching(getArrayKeyMatchPattern(key));

        comboBox.removeAllItems();

        for (String vKey : keys) {

            comboBox.addItem(properties.getProperty(vKey));
        }
        applySelectedStringItem(comboBox, key);
    }

    /**
     * Adds strings to the model of a {@code JList} <em>if the model is an
     * instance of DefaultListModel</em>.
     *
     * Existing elements will be removed.
     *
     * @param list list
     * @param key  key for string elements
     */
    public void insertStringElements(JList list, String key) {

        if (list == null) throw new NullPointerException("list == null");
        if (key  == null) throw new NullPointerException("key == null");

        ListModel lm = list.getModel();

        if (lm instanceof DefaultListModel) {

            DefaultListModel model = (DefaultListModel) lm;
            List<String>     keys  = getKeysMatching(getArrayKeyMatchPattern(key));

            model.removeAllElements();

            for (String vKey : keys) {

                model.addElement(properties.getProperty(vKey));
            }
            list.setModel(model);
            applySelectedStringValue(list, key);
        }
    }

    /**
     * Setzt das selektierte Item einer Combobox.
     *
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void applySelectedStringItem(JComboBox comboBox, String key) {

        if (comboBox == null) throw new NullPointerException("comboBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        String sKey         = key + KEY_APPENDIX_SELECTED;
        String selectedItem = properties.getProperty(sKey);

        comboBox.setSelectedIndex(-1);

        if (selectedItem != null) {

            comboBox.setSelectedItem(selectedItem);
        }
    }

    /**
     * Setzt den Index eines selektierten Combobox-Items.
     *
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void applySelectedIndex(JComboBox comboBox, String key) {

        if (comboBox == null) throw new NullPointerException("comboBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        String index = properties.getProperty(key + KEY_APPENDIX_SELECTED);

        comboBox.setSelectedIndex(-1);

        if (index != null) {
            try {
                int ind = Integer.parseInt(index);

                if (ind < comboBox.getItemCount()) {

                    comboBox.setSelectedIndex(ind);
                }
            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt den Status eines Toggle-Buttons (<code>isSelected()</code>).
     *
     * @param button Button
     * @param key    Schlüssel
     */
    public void applySettings(JToggleButton button, String key) {

        if (button == null) throw new NullPointerException("button == null");
        if (key    == null) throw new NullPointerException("key == null");

        String status = properties.getProperty(key);

        if (status != null) {

            boolean isSelected = status.equals("1");
            button.setSelected(isSelected);
        }
    }

    /**
     * Setzt selektierten String-Wert einer Liste.
     *
     * @param list Liste
     * @param key  Schlüssel
     */
    public void applySelectedStringValue(JList list, String key) {

        if (list == null) throw new NullPointerException("list == null");
        if (key  == null) throw new NullPointerException("key == null");

        String sKey          = key + KEY_APPENDIX_SELECTED;
        String selectedValue = properties.getProperty(sKey);

        list.clearSelection();

        if (selectedValue != null) {

            list.setSelectedValue(selectedValue, true);
        }
    }

    /**
     * Sets the selected indices to a {@code JList}.
     *
     * @param list list
     * @param key  key for the indices
     */
    public void applySelectedIndices(JList list, String key) {

        if (list == null) throw new NullPointerException("list == null");
        if (key  == null) throw new NullPointerException("key == null");

        list.clearSelection();

        List<Integer> selIndices = getIntegerCollection(key + KEY_APPENDIX_SELECTED);

        if (selIndices.isEmpty()) return;

        List<Integer> existingIndices = ListUtil.getExistingIndicesOf(ArrayUtil.toIntArray(selIndices), list);
        
        if (!existingIndices.isEmpty()) {
            try {
                if (list.getSelectionMode() == ListSelectionModel.SINGLE_SELECTION) {
                    list.setSelectedIndex(existingIndices.get(0));
                } else {
                    Collections.sort(existingIndices);
                    for (int index : existingIndices) {
                        list.addSelectionInterval(index, index);
                    }
                }
                list.ensureIndexIsVisible(existingIndices.get(0));
            } catch (Exception ex) {

                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    /**
     * Setzt den Inhalt eines Textfelds.
     *
     * @param textField Textfelds
     * @param key       Schlüssel
     */
    public void applySettings(JTextField textField, String key) {

        if (textField == null) throw new NullPointerException("textField == null");
        if (key       == null) throw new NullPointerException("key == null");

        if (properties.containsKey(key)) {

            textField.setText(properties.getProperty(key));
        }
    }

    /**
     * Liefert einen String.
     *
     * @param key Schlüssel
     * @return    String, Leerstring, wenn zum Schlüssel keiner existiert
     */
    public String getString(String key) {
        if (key == null)
            throw new NullPointerException("key == null");

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }

        return "";
    }

    /**
     * Speichert ein Integerarray.
     *
     * @param integers integers
     * @param key      Schlüssel
     */
    public void setIntegerCollection(Collection<? extends Integer> integers, String key) {

        if (integers == null) throw new NullPointerException("array == null");
        if (key      == null) throw new NullPointerException("key == null");

        StringBuilder sb = new StringBuilder();

        for (Integer integer : integers) {
            sb.append(integer.toString());
            sb.append(DELIMITER_NUMBER_ARRAY);
        }

        properties.setProperty(key, sb.toString());
    }

    /**
     * Speichert ein Stringarray.
     *
     * @param strings Array
     * @param key     Schlüssel
     */
    public void setStringCollection(Collection<? extends String> strings, String key) {

        if (strings == null) throw new NullPointerException("strings == null");
        if (key     == null) throw new NullPointerException("key == null");

        deleteKeysMatching(getArrayKeyMatchPattern(key));

        int index = 0;

        for (String string : strings) {

            properties.setProperty(key + DELIMITER_ARRAY_KEYS + Integer.toString(index++), string);
        }
    }

    /**
     * Löscht alle Schlüssel mit bestimmtem Muster.
     *
     * @param pattern Muster
     */
    public void deleteKeysMatching(String pattern) {

        if (pattern == null) throw new NullPointerException("pattern == null");

        for (String key : getKeysMatching(pattern)) {
            properties.remove(key);
        }
    }

    /**
     * Liefert ein Integer-Array.
     *
     * @param key Schlüssel
     * @return    Array
     */
    public List<Integer> getIntegerCollection(String key) {

        if (key == null) throw new NullPointerException("key == null");

        List<Integer> integers = new ArrayList<Integer>();

        if (properties.containsKey(key)) {

            StringTokenizer tokenizer = new StringTokenizer(properties. getProperty(key), DELIMITER_NUMBER_ARRAY);

            while (tokenizer.hasMoreTokens()) {
                try {
                    integers.add(new Integer(tokenizer.nextToken()));

                } catch (Exception ex) {

                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return integers;
    }

    /**
     * Liefert ein Stringarray.
     *
     * @param key Schlüssel
     * @return    Stringarray
     */
    public List<String> getStringCollection(String key) {

        if (key == null) throw new NullPointerException("key == null");

        List<String> strings = new ArrayList<String>();
        List<String> keys    = getKeysMatching(getArrayKeyMatchPattern(key));

        for (String stringKey : keys) {
            strings.add(properties.getProperty(stringKey));
        }
        return strings;
    }

    /**
     * Removes a string array.
     *
     * @param key  key of the string array
     */
    public void removeStringCollection(String key) {

        if (key == null) throw new NullPointerException("key == null");

        for (String k : getKeysMatching(getArrayKeyMatchPattern(key))) {

            properties.remove(k);
        }
    }

    /**
     * Speichert die Attribute einer Komponente.
     *
     * Unterstützt:
     * <ul>
     *  <li>ButtonGroup</li>
     *  <li>JCheckBox</li>
     *  <li>JComboBox</li>
     *  <li>JList</li>
     *  <li>JSpinner</li>
     *  <li>JSplitPane</li>
     *  <li>JTabbedPane</li>
     *  <li>JTable</li>
     *  <li>JTextField</li>
     *  <li>JTree</li>
     * </ul>
     *
     * @param component Komponente
     * @param hints     Hinweise
     */
    public void set(Component component, SettingsHints hints) {

        if (component == null) throw new NullPointerException("component == null");
        if (hints     == null) throw new NullPointerException("hints == null");

        final Class<? extends Component> c             = component.getClass();
        final String                     componentName = c.getName();
        final Field[]                    fields        = c.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {

            final Field field = fields[index];

            field.setAccessible(true);

            final String fieldName = field.getName();
            final String key       = componentName + "." + fieldName;

            if (hints.isSet(key)) {
                try {
                    final Class<?> fieldType = field.getType();

                    if (fieldType.equals(JTextField.class)) {

                        set((JTextField) field.get(component), key);

                    } else if (fieldType.equals(JComboBox.class)) {

                        if (hints.isOption(SettingsHints.Option.SET_COMBOBOX_CONTENT)) {

                            setStringContent((JComboBox) field.get(component), key);
                        } else {
                            setSelectedIndex((JComboBox) field.get(component), key);
                        }
                    } else if (fieldType.equals(JList.class)) {

                        if (hints.isOption(SettingsHints.Option.SET_LIST_CONTENT)) {

                            setStringContent((JList) field.get(component), key);
                        } else {
                            setSelectedIndices((JList) field.get(component), key);
                        }
                    } else if (fieldType.equals(JCheckBox.class)) {

                        set((JCheckBox) field.get(component), key);

                    } else if (fieldType.equals(JTabbedPane.class)) {

                        set((JTabbedPane) field.get(component), key, hints);

                    } else if (fieldType.equals(JSpinner.class)) {

                        set((JSpinner) field.get(component), key);

                    } else if (fieldType.equals(JTable.class)) {

                        set((JTable) field.get(component), key);

                    } else if (fieldType.equals(JSplitPane.class)) {

                        set((JSplitPane) field.get(component), key);

                    } else if (fieldType.equals(JTree.class)) {

                        set((JTree) field.get(component), key);

                    } else if (fieldType.equals(ButtonGroup.class)) {

                        set((ButtonGroup) field.get(component), key);
                    }
                } catch (Exception ex) {

                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Speichert den Inhalt einer Checkbox (angekreuzt oder nicht).
     *
     * @param checkBox Checkbox
     * @param key      Schlüssel
     */
    public void set(JCheckBox checkBox, String key) {

        if (checkBox == null) throw new NullPointerException("checkBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        String isSelected = checkBox.isSelected() ? "1" : "0";

        properties.setProperty(key, isSelected);
    }

    /**
     * Writes to the properties the selected button of a button group.
     *
     * Uses the button text as identifier.
     *
     * @param buttonGroup button group
     * @param key         key
     */
    public void set(ButtonGroup buttonGroup, String key) {

        if (buttonGroup == null) throw new NullPointerException("buttonGroup == null");
        if (key         == null) throw new NullPointerException("key == null");

        String textOfSelectedButton = null;

        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {

            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {

                textOfSelectedButton = button.getText();
            }
        }

        if (textOfSelectedButton != null && !textOfSelectedButton.isEmpty()) {

            properties.setProperty(key, textOfSelectedButton);
        }
    }

    /**
     * Speichert den Wert eines Spinners.
     *
     * @param spinner Spinner
     * @param key     Schlüssel
     */
    public void set(JSpinner spinner, String key) {

        if (spinner == null) throw new NullPointerException("spinner == null");
        if (key     == null) throw new NullPointerException("key == null");

        properties.setProperty(key, spinner.getValue().toString());
    }

    /**
     * Speichert den Inhalt einer Tabbed Pane und ihrer Komponenten.
     *
     * @param pane  Pane
     * @param key   Schlüssel
     * @param hints Hinweise
     */
    public void set(JTabbedPane pane, String key, SettingsHints hints) {

        if (pane  == null) throw new NullPointerException("pane == null");
        if (hints == null) throw new NullPointerException("hints == null");

        try {
            int index = pane.getSelectedIndex();

            properties.setProperty(key, Integer.toString(index));

            if (hints.isOption(SettingsHints.Option.SET_TABBED_PANE_CONTENT)) {

                int componentCount = pane.getComponentCount();

                for (int i = 0; i < componentCount; i++) {

                    set(pane.getComponentAt(i), hints);
                }
            }
        } catch (Exception ex) {

            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Speichert den Inhalt einer Combobox. Geeignet sind Comboboxen mit
     * Strings.
     *
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void setStringContent(JComboBox comboBox, String key) {

        if (comboBox == null) throw new NullPointerException("comboBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        int itemCount = comboBox.getItemCount();

        deleteKeysMatching(getArrayKeyMatchPattern(key));

        for (int index = 0; index < itemCount; index++) {

            final Object item    = comboBox.getItemAt(index);
            final String itemKey = key + DELIMITER_ARRAY_KEYS + Integer.toString(index);

            if (item != null) {

                assert item instanceof String : item; // Not a combo box of strings!

                properties.setProperty(itemKey, item.toString());
            }
        }
        setSelectedItem(comboBox, key);
    }

    /**
     * Speichert den Inhalt einer Liste. Geeignet für Listen mit Strings.
     *
     * @param list Liste
     * @param key  Schlüssel
     */
    public void setStringContent(JList list, String key) {

        if (list == null) throw new NullPointerException("list == null");
        if (key  == null) throw new NullPointerException("key == null");

        ListModel model        = list.getModel();
        int       elementCount = model.getSize();

        deleteKeysMatching(getArrayKeyMatchPattern(key));

        for (int i = 0; i < elementCount; i++) {

            final Object element    = model.getElementAt(i);
            final String elementKey = key + DELIMITER_ARRAY_KEYS + Integer.toString(i);

            if (element != null) {

                assert element instanceof String : element; // Not a list of strings!

                properties.setProperty(elementKey, element.toString());
            }
        }
        setSelectedValue(list, key);
    }

    /**
     * Speichert den Index des selektierten Items einer Combobox.
     *
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void setSelectedIndex(JComboBox comboBox, String key) {

        if (comboBox == null) throw new NullPointerException("comboBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        properties.setProperty(
                key + KEY_APPENDIX_SELECTED,
                Integer.toString(comboBox.getSelectedIndex()));
    }

    /**
     * Speichert den Status eines Toggle-Buttons (<code>isSelected()</code>).
     *
     * @param button Button
     * @param key Schlüssel
     */
    public void set(JToggleButton button, String key) {

        if (button == null) throw new NullPointerException("button == null");
        if (key    == null) throw new NullPointerException("key == null");

        String status = button.isSelected() ? "1" : "0";

        properties.setProperty(key, status);
    }

    /**
     * Speichertden selektierten Wert einer Liste.
     * Geeignet für Listen mit Strings.
     *
     * @param list Liste
     * @param key  Schlüssel
     */
    public void setSelectedValue(JList list, String key) {

        if (list == null) throw new NullPointerException("list == null");
        if (key  == null) throw new NullPointerException("key == null");

        String valueKey      = key + KEY_APPENDIX_SELECTED;
        Object selectedValue = list.getSelectedValue();

        if (selectedValue == null) {
            properties.remove(key);
        } else {
            properties.setProperty(valueKey, selectedValue.toString());
        }
    }

    /**
     * Speichert den Index des selektierten Items einer Combobox.
     *
     * @param list Liste
     * @param key  Schlüssel
     */
    public void setSelectedIndices(JList list, String key) {

        if (list == null) throw new NullPointerException("list == null");
        if (key  == null) throw new NullPointerException("key == null");

        final int[]  selIndices    = list.getSelectedIndices();
        final String keySelIndices = key + KEY_APPENDIX_SELECTED;

        if (selIndices.length == 0) {
            properties.remove(keySelIndices);
        } else {
            setIntegerCollection(ArrayUtil.toList(selIndices), keySelIndices);
        }
    }

    /**
     * Speichert das selektierte Item einer Combobox.
     * Geeignet für Comboboxen mit Strings.
     *
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void setSelectedItem(JComboBox comboBox, String key) {

        if (comboBox == null) throw new NullPointerException("comboBox == null");
        if (key      == null) throw new NullPointerException("key == null");

        String sKey         = key + KEY_APPENDIX_SELECTED;
        Object selectedItem = comboBox.getSelectedItem();

        if (selectedItem == null) {
            properties.remove(sKey);
        } else {
            properties.setProperty(sKey, selectedItem.toString());
        }
    }

    /**
     * Speichert den Inhalt eines Textfelds.
     *
     * @param textField Textfeld
     * @param key       Schlüssel
     */
    public void set(JTextField textField, String key) {

        if (textField == null) throw new NullPointerException("textField == null");
        if (key       == null) throw new NullPointerException("key == null");

        properties.setProperty(key, textField.getText());
    }

    /**
     * Speichert einen String.
     *
     * @param string String
     * @param key    Schlüssel
     */
    public void set(String string, String key) {
        if (string == null) throw new NullPointerException("string == null");
        if (key    == null) throw new NullPointerException("key == null");

        properties.setProperty(key, string);
    }

    /**
     * Entfernt einen Schlüssel (plus Wert).
     *
     * @param key Schlüssel
     */
    public void removeKey(String key) {

        if (key == null) throw new NullPointerException("key == null");

        properties.remove(key);
    }

    /**
     * Speichert die Eigenschaften einer Tabelle (aktuell die Spaltenbreiten).
     *
     * @param table Tabelle
     * @param key Schlüssel
     */
    public void set(JTable table, String key) {

        if (table == null) throw new NullPointerException("table == null");
        if (key   == null) throw new NullPointerException("key == null");

        TableModel    model     = table.getModel();
        List<Integer> colWidths = new ArrayList<Integer>();

        TableColumnModel colModel         = table.getColumnModel();
        int              tableColumnCount = model.getColumnCount();

        for (int index = 0; index < tableColumnCount; index++) {

            colWidths.add(new Integer(colModel.getColumn(index).getWidth()));
        }
        setIntegerCollection(colWidths, key);
    }

    /**
     * Speichert die Eigenschaften einer Splitpane (aktuell die Position des
     * Dividers).
     *
     * @param splitPane Splitpane
     * @param key       Schlüssel
     */
    public void set(JSplitPane splitPane, String key) {

        if (splitPane == null) throw new NullPointerException("splitPane == null");
        if (key       == null) throw new NullPointerException("key == null");

        int dividerLocation = splitPane.getDividerLocation();

        properties.setProperty(key, Integer.toString(dividerLocation));
    }

    /**
     * Speichert die Eigenschaften einer ScrollPane (aktuell die
     * View-Position des Viewports).
     *
     * @param scrollPane ScrolPane
     * @param key        Schlüssel Schlüssel
     */
    public void set(JScrollPane scrollPane, String key) {

        if (scrollPane == null) throw new NullPointerException("scrollPane == null");
        if (key == null       ) throw new NullPointerException("key == null");

        Integer x = scrollPane.getViewport().getViewPosition().x;
        Integer y = scrollPane.getViewport().getViewPosition().y;

        properties.setProperty(key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X, x.toString());
        properties.setProperty(key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y, y.toString());
    }

    /**
     * Speichert einen Tree, aktuell den zuerst selektierten Pfad.
     *
     * @param tree Tree
     * @param key  Schlüssel
     */
    public void set(JTree tree, String key) {

        if (tree == null) throw new NullPointerException("tree == null");
        if (key == null ) throw new NullPointerException("key == null");

        TreePath selectionPath = tree.getSelectionPath();

        if (selectionPath == null) {

            properties.remove(key);

        } else {
            Object[]      path = selectionPath.getPath();
            StringBuilder sb   = new StringBuilder();

            for (int index = 0; index < path.length; index++) {

                final String delimiter = index + 1 < path.length ? FILE_PATH_SEPARATOR : "";

                sb.append(path[index].toString() + delimiter);
            }
            properties.setProperty(key, sb.toString());
        }
    }

    /**
     * Returns an Integer value.
     *
     * @param  key  key
     * @return value or <code>Integer.MIN_VALUE</code> if not defined
     */
    public int getInt(String key) {

        if (key == null) throw new NullPointerException("key == null");

        int result = Integer.MIN_VALUE;

        if (properties.containsKey(key)) {
            try {
                result = Integer.parseInt(properties.getProperty(key));

            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    /**
     * Sets an Integer value.
     *
     * @param value  value
     * @param key    key
     */
    public void set(int value, String key) {

        if (key == null) throw new NullPointerException("key == null");

        properties.setProperty(key, Integer.toString(value));
    }

    public boolean getBoolean(String key) {

        if (key == null) throw new NullPointerException("key == null");

        int result = getInt(key);

        return result == 1 ? true : false;
    }

    public void set(boolean b, String key) {

        if (key == null) throw new NullPointerException("key == null");

        set(b ? 1 : 0, key);
    }

    /**
     * Sets to a component the size and location. Uses the
     * class name as key. If the key does not exist, nothing will be done.
     *
     * @param component component
     * @see             #applySize(java.awt.Component)
     * @see             #applyLocation(java.awt.Component)
     */
    public void applySizeAndLocation(Component component) {

        if (component == null) throw new NullPointerException("component == null");

        applySize(component);
        applyLocation(component);
    }

    /**
     * Sets to a component the size. Uses the class name as
     * key. If the key does not exist, nothing will be done.
     *
     * @param component component
     */
    public void applySize(Component component) {

        if (component == null) throw new NullPointerException("component == null");

        applySize(component, component.getClass().getName());
    }

    /**
     * Returns whether the properties contains a specific key.
     *
     * @param  key key
     * @return     true if the properties contains that key
     */
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    /**
     * Sets to a component the size. If the key does not
     * exist, nothing will be done.
     *
     * @param component component
     * @param key       key
     */
    public void applySize(Component component, String key) {

        if (component == null) throw new NullPointerException("component == null");
        if (key       == null) throw new NullPointerException("key == null");

        String keyWidth  = getKeyWidth(key);
        String keyHeight = getKeyHeight(key);

        if (properties.containsKey(keyWidth) && properties.containsKey(keyHeight)) {

            try {
                int width  = Integer.parseInt(properties.getProperty(keyWidth));
                int height = Integer.parseInt(properties.getProperty(keyHeight));

                component.setPreferredSize(new Dimension(width, height));
                component.setSize(new Dimension(width, height));

            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Sets to a component the location. Uses the class name
     * as key. If the key does not exist, nothing will be done.
     *
     * @param component component
     */
    public void applyLocation(Component component) {

        if (component == null) throw new NullPointerException("component == null");

        applyLocation(component, component.getClass().getName());
    }

    /**
     * Sets to a component the location. If the key does not
     * exist, nothing will be done.
     *
     * @param component component
     * @param key       key
     */
    public void applyLocation(Component component, String key) {

        if (component == null) throw new NullPointerException("component == null");
        if (key       == null) throw new NullPointerException("key == null");

        String keyLocationX = getKeyLocationX(key);
        String keyLocationY = getKeyLocationY(key);

        if (properties.containsKey(keyLocationX) && properties.containsKey(keyLocationY)) {
            try {
                int locationX = Integer.parseInt(properties.getProperty(keyLocationX));
                int locationY = Integer.parseInt(properties.getProperty(keyLocationY));

                component.setLocation(new Point(locationX, locationY));

            } catch (Exception ex) {

                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Sets to the {@code Properties} instance the size and location of a component. Uses the
     * class name as key.
     *
     * @param component component
     * @see             #setSize(java.awt.Component)
     * @see             #setLocation(java.awt.Component)
     */
    public void setSizeAndLocation(Component component) {

        if (component == null) throw new NullPointerException("component == null");

        setSize(component);
        setLocation(component);
    }

    /**
     * Sets to the {@code Properties} instance the size of a component. Uses the class name as key.
     *
     * @param component component
     */
    public void setSize(Component component) {

        if (component == null) throw new NullPointerException("component == null");

        setSize(component, component.getClass().getName());
    }

    /**
     * Sets to the {@code Properties} instance the size of a component.
     *
     * @param component component
     * @param key       key
     */
    public void setSize(Component component, String key) {

        if (component == null) throw new NullPointerException("component == null");
        if (key       == null) throw new NullPointerException("key == null");

        Dimension size = component.getSize();

        properties.setProperty(getKeyWidth(key), Integer.toString(size.width));
        properties.setProperty(getKeyHeight(key), Integer.toString(size.height));
    }

    /**
     * Sets to the {@code Properties} instance the size of a component. Uses the class name as key.
     *
     * @param component component
     */
    public void setLocation(Component component) {

        if (component == null) throw new NullPointerException("component == null");

        setLocation(component, component.getClass().getName());
    }

    /**
     * Sets to the {@code Properties} instance the size of a component.
     *
     * @param component component
     * @param key       key
     */
    public void setLocation(Component component, String key) {

        if (component == null) throw new NullPointerException("component == null");
        if (key == null      ) throw new NullPointerException("key == null");

        Point location = component.getLocation();

        properties.setProperty(getKeyLocationX(key), Integer.toString(location.x));
        properties.setProperty(getKeyLocationY(key), Integer.toString(location.y));
    }

    private static String getKeyHeight(String key) {
        assert key != null : key;
        return key + POSTFIX_KEY_HEIGHT;
    }

    private static String getKeyWidth(String key) {
        assert key != null : key;
        return key + POSTFIX_KEY_WIDTH;
    }

    private static String getKeyLocationX(String key) {
        assert key != null : key;
        return key + POSTFIX_KEY_LOCATION_X;
    }

    private static String getKeyLocationY(String key) {
        assert key != null : key;
        return key + POSTFIX_KEY_LOCATION_Y;
    }

    private String getArrayKeyMatchPattern(String key) {

        assert key != null : key;

        return "^" + java.util.regex.Pattern.quote(key + DELIMITER_ARRAY_KEYS) + "[0-9]+$";
    }
}
