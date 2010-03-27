/*
 * @(#)Settings.java    Created on 2009-02-23
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.util;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.TreeUtil;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JTree;
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
 * @author  Elmar Baumann
 */
public final class Settings {
    private final Properties    properties;
    private static final String DOT                                  = ".";
    private static final String BOOLEAN_FALSE_STRING                 = "0";
    private static final String BOOLEAN_TRUE_STRING                  = "1";
    private static final String DELIMITER_NUMBER_ARRAY               = ";";
    private static final String DELIMITER_ARRAY_KEYS                 = DOT;
    private static final String TREE_PATH_SEPARATOR                  = "|";
    private static final String KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X =
        ".ViewportViewPositionX";
    private static final String KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y =
        ".ViewportViewPositionY";
    private static final String KEY_APPENDIX_SELECTED  = "-selected";
    private static final String POSTFIX_KEY_WIDTH      = ".Width";
    private static final String POSTFIX_KEY_HEIGHT     = ".Height";
    private static final String POSTFIX_KEY_LOCATION_X = ".LocationX";
    private static final String POSTFIX_KEY_LOCATION_Y = ".LocationY";

    public Settings(Properties properties) {
        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        this.properties = properties;
    }

    public List<String> getKeysMatching(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        return RegexUtil.getMatches(properties.stringPropertyNames(), pattern);
    }

    public void removeKeysWithEmptyValues() {
        Set<String> keys = properties.stringPropertyNames();

        for (String key : keys) {
            String value = properties.getProperty(key);

            if ((value == null) || value.isEmpty()) {
                properties.remove(key);
            }
        }
    }

    /**
     *
     * @param component
     * @param hints     hints or null
     */
    public void applySettings(Component component, SettingsHints hints) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        final Class<? extends Component> clazz         = component.getClass();
        final String                     componentName = clazz.getName();
        final Field[]                    fields        =
            clazz.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {
            final Field field = fields[index];

            field.setAccessible(true);

            final String fieldName = field.getName();
            final String key       = componentName + DOT + fieldName;

            if ((hints == null) || hints.isSet(key)) {
                try {
                    final Class<?> fieldType = field.getType();

                    if (fieldType.equals(JTabbedPane.class)) {
                        applySettings((JTabbedPane) field.get(component), key,
                                      hints);
                    } else if (fieldType.equals(JSplitPane.class)) {
                        applySettings((JSplitPane) field.get(component), key);
                    } else if (fieldType.equals(JTable.class)) {
                        applySettings((JTable) field.get(component), key);
                    } else if (fieldType.equals(JTree.class)) {
                        applySettings((JTree) field.get(component), key);
                    } else if (fieldType.equals(JComboBox.class)) {
                        applySelectedIndex((JComboBox) field.get(component),
                                           key);
                    } else if (fieldType.equals(JList.class)) {
                        applySelectedIndices((JList) field.get(component), key);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Settings.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Selects a button of a button group.
     *
     * @param buttonGroup button group
     * @param key         key
     */
    public void applySettings(ButtonGroup buttonGroup, String key) {
        if (buttonGroup == null) {
            throw new NullPointerException("buttonGroup == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (properties.containsKey(key)) {
            String textOfSelectedButton = properties.getProperty(key);

            for (Enumeration<AbstractButton> buttons =
                    buttonGroup.getElements();
                    buttons.hasMoreElements(); ) {
                AbstractButton button = buttons.nextElement();

                if (button.getText().equals(textOfSelectedButton)) {
                    button.setSelected(true);

                    return;
                }
            }
        }
    }

    /**
     * Sets one selected path of a tree.
     *
     * @param tree tree
     * @param key  key
     */
    public void applySettings(JTree tree, String key) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        tree.clearSelection();

        int    pathIndex  = 0;
        String indexedKey = toIndexedKey(key, pathIndex);

        while (properties.containsKey(indexedKey)) {
            String   value = properties.getProperty(indexedKey);
            TreePath path  = TreeUtil.getTreePath(tree.getModel(),
                                 removeSelToken(value), TREE_PATH_SEPARATOR);

            if (path != null) {
                TreeUtil.expandPath(tree, path);
                tree.scrollPathToVisible(path);

                if (isSelected(value)) {
                    tree.addSelectionPath(path);
                }
            }

            indexedKey = toIndexedKey(key, ++pathIndex);
        }
    }

    private String toIndexedKey(String key, int index) {
        return key + DOT + Integer.toString(index);
    }

    private String removeSelToken(String path) {
        int sepIndex = path.lastIndexOf(TREE_PATH_SEPARATOR);
        int length   = path.length();

        return ((sepIndex >= 0) && (sepIndex < length - 1))
               ? path.substring(0, sepIndex)
               : path;
    }

    private boolean isSelected(String path) {
        int sepIndex = path.lastIndexOf(TREE_PATH_SEPARATOR);
        int length   = path.length();

        if ((sepIndex >= 0) && (sepIndex < length - 1)) {
            return path.substring(sepIndex + 1).equals(BOOLEAN_TRUE_STRING);
        }

        return false;
    }

    /**
     * Sets the divider position of a split pane.
     *
     * @param splitPane split pane
     * @param key       key
     */
    public void applySettings(JSplitPane splitPane, String key) {
        if (splitPane == null) {
            throw new NullPointerException("splitPane == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (properties.containsKey(key)) {
            try {
                int location = Integer.parseInt(properties.getProperty(key));

                splitPane.setDividerLocation(location);
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }
    }

    /**
     * Sets the viewport view position of a scroll pane.
     *
     * @param scrollPane scroll pane
     * @param key        key
     */
    public void applySettings(JScrollPane scrollPane, String key) {
        if (scrollPane == null) {
            throw new NullPointerException("scrollPane == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String keyX = key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X;
        String keyY = key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y;

        if (properties.containsKey(keyX) && properties.containsKey(keyY)) {
            try {
                int x = Integer.parseInt(properties.getProperty(keyX));
                int y = Integer.parseInt(properties.getProperty(keyY));

                scrollPane.getViewport().setViewPosition(new Point(x, y));
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }
    }

    /**
     * Sets the column widths of a table.
     *
     * @param table table
     * @param key   key
     */
    public void applySettings(JTable table, String key) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        TableModel model = table.getModel();

        if (properties.containsKey(key)) {
            TableColumnModel colModel          = table.getColumnModel();
            List<Integer>    storedWidths      = getIntegerCollection(key);
            int              tableColumnCount  = model.getColumnCount();
            int              storedColumnCount = storedWidths.size();

            for (int index = 0;
                    (index < tableColumnCount) && (index < storedColumnCount);
                    index++) {
                colModel.getColumn(index).setPreferredWidth(
                    storedWidths.get(index));
            }
        }
    }

    /**
     * Sets the value of a spinner.
     *
     * @param spinner spinner
     * @param key     key
     */
    public void applySettings(JSpinner spinner, String key) {
        if (spinner == null) {
            throw new NullPointerException("spinner == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);

            try {
                spinner.setValue(Integer.parseInt(value));
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }
    }

    /**
     * Sets the selected index of a tabbed pane.
     *
     * @param pane  tabbed pane
     * @param key   key
     * @param hints hints or null
     */
    public void applySettings(JTabbedPane pane, String key,
                              SettingsHints hints) {
        if (pane == null) {
            throw new NullPointerException("pane == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);

            try {
                int index = Integer.parseInt(value);

                if (index < pane.getTabCount()) {
                    pane.setSelectedIndex(index);
                }
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }

        if ((hints != null)
                && hints.isOption(
                    SettingsHints.Option.SET_TABBED_PANE_CONTENT)) {
            int componentCount = pane.getComponentCount();

            for (int index = 0; index < componentCount; index++) {
                applySettings(pane.getComponentAt(index), hints);
            }
        }
    }

    public void applySelectedIndex(JComboBox comboBox, String key) {
        if (comboBox == null) {
            throw new NullPointerException("comboBox == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String index = properties.getProperty(key + KEY_APPENDIX_SELECTED);

        if (index != null) {
            try {
                int ind = Integer.parseInt(index);

                if (ind < comboBox.getItemCount()) {
                    comboBox.setSelectedIndex(ind);
                }
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }
    }

    /**
     * Sets the toggle state of a toggle button.
     *
     * @param button toggle button
     * @param key    key
     */
    public void applySettings(JToggleButton button, String key) {
        if (button == null) {
            throw new NullPointerException("button == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String status = properties.getProperty(key);

        if (status != null) {
            button.setSelected(status.equals(BOOLEAN_TRUE_STRING));
        }
    }

    /**
     * Sets the selected indices to a {@code JList}.
     *
     * @param list list
     * @param key  key for the indices
     */
    public void applySelectedIndices(JList list, String key) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        list.clearSelection();

        List<Integer> selIndices = getIntegerCollection(key
                                       + KEY_APPENDIX_SELECTED);

        if (selIndices.isEmpty()) {
            return;
        }

        List<Integer> existingIndices =
            ListUtil.getExistingIndicesOf(ArrayUtil.toIntArray(selIndices),
                                          list);

        if (!existingIndices.isEmpty()) {
            try {
                if (list.getSelectionMode()
                        == ListSelectionModel.SINGLE_SELECTION) {
                    list.setSelectedIndex(existingIndices.get(0));
                } else {
                    Collections.sort(existingIndices);

                    for (int index : existingIndices) {
                        list.addSelectionInterval(index, index);
                    }
                }

                list.ensureIndexIsVisible(existingIndices.get(0));
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null,
                                 ex);
            }
        }
    }

    /**
     *
     * @param key
     * @return    string (is empty if key does not exist)
     */
    public String getString(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }

        return "";
    }

    public void setIntegerCollection(Collection<? extends Integer> integers,
                                     String key) {
        if (integers == null) {
            throw new NullPointerException("array == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        StringBuilder sb = new StringBuilder();

        for (Integer integer : integers) {
            sb.append(integer.toString());
            sb.append(DELIMITER_NUMBER_ARRAY);
        }

        properties.setProperty(key, sb.toString());
    }

    public void setStringCollection(Collection<? extends String> strings,
                                    String key) {
        if (strings == null) {
            throw new NullPointerException("strings == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        deleteKeysMatching(getArrayKeyMatchPattern(key));

        int index = 0;

        for (String string : strings) {
            properties.setProperty(key + DELIMITER_ARRAY_KEYS
                                   + Integer.toString(index++), string);
        }
    }

    public void deleteKeysMatching(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        for (String key : getKeysMatching(pattern)) {
            properties.remove(key);
        }
    }

    public List<Integer> getIntegerCollection(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        List<Integer> integers = new ArrayList<Integer>();

        if (properties.containsKey(key)) {
            StringTokenizer tokenizer =
                new StringTokenizer(properties.getProperty(key),
                                    DELIMITER_NUMBER_ARRAY);

            while (tokenizer.hasMoreTokens()) {
                try {
                    integers.add(new Integer(tokenizer.nextToken()));
                } catch (Exception ex) {
                    Logger.getLogger(Settings.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
            }
        }

        return integers;
    }

    public List<String> getStringCollection(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        List<String> strings = new ArrayList<String>();
        List<String> keys    = getKeysMatching(getArrayKeyMatchPattern(key));

        for (String stringKey : keys) {
            strings.add(properties.getProperty(stringKey));
        }

        return strings;
    }

    public void removeStringCollection(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        for (String k : getKeysMatching(getArrayKeyMatchPattern(key))) {
            properties.remove(k);
        }
    }

    /**
     *
     * @param component
     * @param hints     hints or null
     */
    public void set(Component component, SettingsHints hints) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        final Class<? extends Component> c             = component.getClass();
        final String                     componentName = c.getName();
        final Field[]                    fields        = c.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {
            final Field field = fields[index];

            field.setAccessible(true);

            final String fieldName = field.getName();
            final String key       = componentName + DOT + fieldName;

            if ((hints == null) || hints.isSet(key)) {
                try {
                    final Class<?> fieldType = field.getType();

                    if (fieldType.equals(JComboBox.class)) {
                        setSelectedIndex((JComboBox) field.get(component), key);
                    } else if (fieldType.equals(JList.class)) {
                        setSelectedIndices((JList) field.get(component), key);
                    } else if (fieldType.equals(JTabbedPane.class)) {
                        set((JTabbedPane) field.get(component), key, hints);
                    } else if (fieldType.equals(JTable.class)) {
                        set((JTable) field.get(component), key);
                    } else if (fieldType.equals(JSplitPane.class)) {
                        set((JSplitPane) field.get(component), key);
                    } else if (fieldType.equals(JTree.class)) {
                        set((JTree) field.get(component), key);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Settings.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
            }
        }
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
        if (buttonGroup == null) {
            throw new NullPointerException("buttonGroup == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String textOfSelectedButton = null;

        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements();
                buttons.hasMoreElements(); ) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                textOfSelectedButton = button.getText();
            }
        }

        if ((textOfSelectedButton != null) &&!textOfSelectedButton.isEmpty()) {
            properties.setProperty(key, textOfSelectedButton);
        }
    }

    public void set(JSpinner spinner, String key) {
        if (spinner == null) {
            throw new NullPointerException("spinner == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        properties.setProperty(key, spinner.getValue().toString());
    }

    /**
     *
     * @param pane
     * @param key
     * @param hints hints or null
     */
    public void set(JTabbedPane pane, String key, SettingsHints hints) {
        if (pane == null) {
            throw new NullPointerException("pane == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        try {
            int index = pane.getSelectedIndex();

            properties.setProperty(key, Integer.toString(index));

            if ((hints != null)
                    && hints.isOption(
                        SettingsHints.Option.SET_TABBED_PANE_CONTENT)) {
                int componentCount = pane.getComponentCount();

                for (int i = 0; i < componentCount; i++) {
                    set(pane.getComponentAt(i), hints);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null,
                             ex);
        }
    }

    public void setSelectedIndex(JComboBox comboBox, String key) {
        if (comboBox == null) {
            throw new NullPointerException("comboBox == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        properties.setProperty(key + KEY_APPENDIX_SELECTED,
                               Integer.toString(comboBox.getSelectedIndex()));
    }

    public void set(JToggleButton button, String key) {
        if (button == null) {
            throw new NullPointerException("button == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String status = button.isSelected()
                        ? BOOLEAN_TRUE_STRING
                        : BOOLEAN_FALSE_STRING;

        properties.setProperty(key, status);
    }

    public void setSelectedIndices(JList list, String key) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        final int[]  selIndices    = list.getSelectedIndices();
        final String keySelIndices = key + KEY_APPENDIX_SELECTED;

        if (selIndices.length == 0) {
            properties.remove(keySelIndices);
        } else {
            setIntegerCollection(ArrayUtil.toList(selIndices), keySelIndices);
        }
    }

    public void set(String string, String key) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        properties.setProperty(key, string);
    }

    public void removeKey(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        properties.remove(key);
    }

    public void set(JTable table, String key) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        TableModel       model            = table.getModel();
        List<Integer>    colWidths        = new ArrayList<Integer>();
        TableColumnModel colModel         = table.getColumnModel();
        int              tableColumnCount = model.getColumnCount();

        for (int index = 0; index < tableColumnCount; index++) {
            colWidths.add(colModel.getColumn(index).getWidth());
        }

        setIntegerCollection(colWidths, key);
    }

    public void set(JSplitPane splitPane, String key) {
        if (splitPane == null) {
            throw new NullPointerException("splitPane == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        int dividerLocation = splitPane.getDividerLocation();

        properties.setProperty(key, Integer.toString(dividerLocation));
    }

    public void set(JScrollPane scrollPane, String key) {
        if (scrollPane == null) {
            throw new NullPointerException("scrollPane == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        Integer x = scrollPane.getViewport().getViewPosition().x;
        Integer y = scrollPane.getViewport().getViewPosition().y;

        properties.setProperty(key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X,
                               x.toString());
        properties.setProperty(key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y,
                               y.toString());
    }

    public void set(JTree tree, String key) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        int rowCount  = tree.getRowCount();
        int pathIndex = 0;    // has to be increased only on written paths!

        deleteKeysMatching(key + "\\.[0-9]+");

        for (int row = 0; row < rowCount; row++) {
            if (tree.isExpanded(row)) {
                setTreePath(tree.getPathForRow(row).getPath(),
                            tree.isRowSelected(row),
                            toIndexedKey(key, pathIndex++));
            } else if (tree.isRowSelected(row)) {    // Selected but not expanded
                setTreePath(tree.getPathForRow(row).getPath(), true,
                            toIndexedKey(key, pathIndex++));
            }
        }
    }

    private void setTreePath(Object[] path, boolean selected, String key) {
        StringBuilder sb = new StringBuilder();

        for (int index = 0; index < path.length; index++) {
            sb.append(path[index].toString());
            sb.append(TREE_PATH_SEPARATOR);
        }

        sb.append(selected
                  ? BOOLEAN_TRUE_STRING
                  : BOOLEAN_FALSE_STRING);
        properties.setProperty(key, sb.toString());
    }

    public int getInt(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        int result = Integer.MIN_VALUE;

        if (properties.containsKey(key)) {
            try {
                result = Integer.parseInt(properties.getProperty(key));
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }

        return result;
    }

    public void set(int value, String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        properties.setProperty(key, Integer.toString(value));
    }

    public boolean getBoolean(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        return (getInt(key) == 1)
               ? true
               : false;
    }

    public void set(boolean b, String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        set(b
            ? 1
            : 0, key);
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
        if (component == null) {
            throw new NullPointerException("component == null");
        }

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
        if (component == null) {
            throw new NullPointerException("component == null");
        }

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
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String keyWidth  = getKeyWidth(key);
        String keyHeight = getKeyHeight(key);

        if (properties.containsKey(keyWidth)
                && properties.containsKey(keyHeight)) {
            try {
                int width  = Integer.parseInt(properties.getProperty(keyWidth));
                int height =
                    Integer.parseInt(properties.getProperty(keyHeight));

                component.setPreferredSize(new Dimension(width, height));
                component.setSize(new Dimension(width, height));
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
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
        if (component == null) {
            throw new NullPointerException("component == null");
        }

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
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        String keyLocationX = getKeyLocationX(key);
        String keyLocationY = getKeyLocationY(key);

        if (properties.containsKey(keyLocationX)
                && properties.containsKey(keyLocationY)) {
            try {
                int locationX =
                    Integer.parseInt(properties.getProperty(keyLocationX));
                int locationY =
                    Integer.parseInt(properties.getProperty(keyLocationY));

                component.setLocation(new Point(locationX, locationY));
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE,
                                 null, ex);
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
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        setSize(component);
        setLocation(component);
    }

    /**
     * Sets to the {@code Properties} instance the size of a component. Uses the class name as key.
     *
     * @param component component
     */
    public void setSize(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        setSize(component, component.getClass().getName());
    }

    /**
     * Sets to the {@code Properties} instance the size of a component.
     *
     * @param component component
     * @param key       key
     */
    public void setSize(Component component, String key) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        Dimension size = component.getSize();

        properties.setProperty(getKeyWidth(key), Integer.toString(size.width));
        properties.setProperty(getKeyHeight(key),
                               Integer.toString(size.height));
    }

    /**
     * Sets to the {@code Properties} instance the size of a component. Uses the class name as key.
     *
     * @param component component
     */
    public void setLocation(Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        setLocation(component, component.getClass().getName());
    }

    /**
     * Sets to the {@code Properties} instance the size of a component.
     *
     * @param component component
     * @param key       key
     */
    public void setLocation(Component component, String key) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        Point location = component.getLocation();

        properties.setProperty(getKeyLocationX(key),
                               Integer.toString(location.x));
        properties.setProperty(getKeyLocationY(key),
                               Integer.toString(location.y));
    }

    private static String getKeyHeight(String key) {
        return key + POSTFIX_KEY_HEIGHT;
    }

    private static String getKeyWidth(String key) {
        return key + POSTFIX_KEY_WIDTH;
    }

    private static String getKeyLocationX(String key) {
        return key + POSTFIX_KEY_LOCATION_X;
    }

    private static String getKeyLocationY(String key) {
        return key + POSTFIX_KEY_LOCATION_Y;
    }

    private String getArrayKeyMatchPattern(String key) {
        return "^" + java.util.regex.Pattern.quote(key + DELIMITER_ARRAY_KEYS)
               + "[0-9]+$";
    }
}
