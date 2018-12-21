package org.jphototagger.lib.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.lib.swing.util.TreeUtil;

/**
 * A settings object writes the state of several objects to a
 * {@code java.util.Properties} instance and resets the state of these objects
 * from an {@code java.util.Properties} instance.
 *
 * @author Elmar Baumann
 */
public final class Settings {

    private final Properties properties;
    public static final String BOOLEAN_FALSE_STRING = "0";
    public static final String BOOLEAN_TRUE_STRING = "1";
    private static final String DOT = ".";
    private static final String DELIMITER_NUMBER_ARRAY = ";";
    private static final String DELIMITER_ARRAY_KEYS = DOT;
    public static final String TREE_PATH_SEPARATOR = "|";
    private static final String KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X = ".ViewportViewPositionX";
    private static final String KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y = ".ViewportViewPositionY";
    private static final String KEY_APPENDIX_SELECTED = "-selected";
    public static final String KEY_POSTFIX_WIDTH = ".Width";
    public static final String KEY_POSTFIX_HEIGHT = ".Height";
    public static final String KEY_POSTFIX_LOCATION_X = ".LocationX";
    public static final String KEY_POSTFIX_LOCATION_Y = ".LocationY";

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
    public void applySettings(Component component, PreferencesHints hints) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        final Class<? extends Component> clazz = component.getClass();
        final String componentName = clazz.getName();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String key = componentName + DOT + fieldName;
            boolean isSet = hints == null || !hints.isExclude(key);
            if (isSet) {
                try {
                    final Class<?> fieldType = field.getType();
                    if (fieldType.equals(JTabbedPane.class)) {
                        applySettings(key, (JTabbedPane) field.get(component), hints);
                    } else if (fieldType.equals(JSplitPane.class)) {
                        applySettings(key, (JSplitPane) field.get(component));
                    } else if (fieldType.equals(JTable.class)) {
                        applySettings(key, (JTable) field.get(component));
                    } else if (fieldType.equals(JTree.class)) {
                        applySettings(key, (JTree) field.get(component));
                    } else if (fieldType.equals(JComboBox.class)) {
                        applySelectedIndex(key, (JComboBox) field.get(component));
                    } else if (fieldType.equals(JList.class)) {
                        applySelectedIndices(key, (JList) field.get(component));
                    }
                } catch (Throwable t) {
                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
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
    public void applySettings(String key, ButtonGroup buttonGroup) {
        if (buttonGroup == null) {
            throw new NullPointerException("buttonGroup == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
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
     * Sets one selected path of a tree.
     *
     * @param tree tree
     * @param key  key
     */
    public void applySettings(String key, JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        tree.clearSelection();
        int pathIndex = 0;
        String indexedKey = toIndexedKey(key, pathIndex);
        while (properties.containsKey(indexedKey)) {
            String value = properties.getProperty(indexedKey);
            TreePath path = TreeUtil.getTreePath(tree.getModel(), removeSelToken(value), TREE_PATH_SEPARATOR);
            if (path != null) {
                TreeUtil.expandPath(tree, path);
                tree.scrollPathToVisible(path);
                if (isSelected(value)) {
                    tree.addSelectionPath(path);
                }
            }
            pathIndex++;
            indexedKey = toIndexedKey(key, pathIndex);
        }
    }

    private String toIndexedKey(String key, int index) {
        return key + DOT + Integer.toString(index);
    }

    private String removeSelToken(String path) {
        int sepIndex = path.lastIndexOf(TREE_PATH_SEPARATOR);
        int length = path.length();

        return ((sepIndex >= 0) && (sepIndex < length - 1))
                ? path.substring(0, sepIndex)
                : path;
    }

    private boolean isSelected(String path) {
        int sepIndex = path.lastIndexOf(TREE_PATH_SEPARATOR);
        int length = path.length();
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
    public void applySettings(String key, JSplitPane splitPane) {
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
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    /**
     * Sets the viewport view position of a scroll pane.
     *
     * @param scrollPane scroll pane
     * @param key        key
     */
    public void applySettings(String key, JScrollPane scrollPane) {
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
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    /**
     * Sets the column widths of a table.
     *
     * @param table table
     * @param key   key
     */
    public void applySettings(String key, JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        TableModel model = table.getModel();
        if (properties.containsKey(key)) {
            TableColumnModel colModel = table.getColumnModel();
            List<Integer> storedWidths = getIntegerCollection(key);
            int tableColumnCount = model.getColumnCount();
            int storedColumnCount = storedWidths.size();
            for (int index = 0; (index < tableColumnCount) && (index < storedColumnCount); index++) {
                colModel.getColumn(index).setPreferredWidth(storedWidths.get(index));
            }
        }
    }

    /**
     * Sets the value of a spinner.
     *
     * @param spinner spinner
     * @param key     key
     */
    public void applySettings(String key, JSpinner spinner) {
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
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
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
    public void applySettings(String key, JTabbedPane pane, PreferencesHints hints) {
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
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
        if ((hints != null) && hints.isOption(PreferencesHints.Option.SET_TABBED_PANE_CONTENT)) {
            int componentCount = pane.getComponentCount();
            for (int index = 0; index < componentCount; index++) {
                applySettings(pane.getComponentAt(index), hints);
            }
        }
    }

    public void applySelectedIndex(String key, JComboBox<?> comboBox) {
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
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    /**
     * Sets the toggle state of a toggle button.
     *
     * @param button toggle button
     * @param key    key
     */
    public void applySettings(String key, JToggleButton button) {
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
    public void applySelectedIndices(String key, JList<?> list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        list.clearSelection();
        List<Integer> selIndices = getIntegerCollection(key + KEY_APPENDIX_SELECTED);
        if (selIndices.isEmpty()) {
            return;
        }
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
            } catch (Throwable t) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, t);
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

    public void setIntegerCollection(String key, Collection<? extends Integer> integers) {
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

    public void setStringCollection(String key, Collection<? extends String> strings) {
        if (strings == null) {
            throw new NullPointerException("strings == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        deleteKeysMatching(getArrayKeyMatchPattern(key));
        int index = 0;
        for (String string : strings) {
            properties.setProperty(key + DELIMITER_ARRAY_KEYS + Integer.toString(index), string);
            index++;
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
        List<Integer> integers = new ArrayList<>();
        if (properties.containsKey(key)) {
            StringTokenizer tokenizer = new StringTokenizer(properties.getProperty(key), DELIMITER_NUMBER_ARRAY);
            while (tokenizer.hasMoreTokens()) {
                try {
                    integers.add(new Integer(tokenizer.nextToken()));
                } catch (Throwable t) {
                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
                }
            }
        }
        return integers;
    }

    public List<String> getStringCollection(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        List<String> strings = new ArrayList<>();
        List<String> keys = getKeysMatching(getArrayKeyMatchPattern(key));
        Collections.sort(keys);
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
    public void set(Component component, PreferencesHints hints) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        final Class<? extends Component> c = component.getClass();
        final String componentName = c.getName();
        final Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String key = componentName + DOT + fieldName;
            boolean isSet = hints == null || !hints.isExclude(key);
            if (isSet) {
                try {
                    final Class<?> fieldType = field.getType();
                    if (fieldType.equals(JComboBox.class)) {
                        setSelectedIndex(key, (JComboBox) field.get(component));
                    } else if (fieldType.equals(JList.class)) {
                        setSelectedIndices(key, (JList) field.get(component));
                    } else if (fieldType.equals(JTabbedPane.class)) {
                        set(key, (JTabbedPane) field.get(component), hints);
                    } else if (fieldType.equals(JTable.class)) {
                        set(key, (JTable) field.get(component));
                    } else if (fieldType.equals(JSplitPane.class)) {
                        set(key, (JSplitPane) field.get(component));
                    } else if (fieldType.equals(JTree.class)) {
                        set(key, (JTree) field.get(component));
                    }
                } catch (Throwable t) {
                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
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
    public void set(String key, ButtonGroup buttonGroup) {
        if (buttonGroup == null) {
            throw new NullPointerException("buttonGroup == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        String textOfSelectedButton = null;
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                textOfSelectedButton = button.getText();
            }
        }
        if ((textOfSelectedButton != null) && !textOfSelectedButton.isEmpty()) {
            properties.setProperty(key, textOfSelectedButton);
        }
    }

    public void set(String key, JSpinner spinner) {
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
    public void set(String key, JTabbedPane pane, PreferencesHints hints) {
        if (pane == null) {
            throw new NullPointerException("pane == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        try {
            int index = pane.getSelectedIndex();
            properties.setProperty(key, Integer.toString(index));
            if ((hints != null) && hints.isOption(PreferencesHints.Option.SET_TABBED_PANE_CONTENT)) {
                int componentCount = pane.getComponentCount();

                for (int i = 0; i < componentCount; i++) {
                    set(pane.getComponentAt(i), hints);
                }
            }
        } catch (Throwable t) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    public void setSelectedIndex(String key, JComboBox<?> comboBox) {
        if (comboBox == null) {
            throw new NullPointerException("comboBox == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        properties.setProperty(key + KEY_APPENDIX_SELECTED, Integer.toString(comboBox.getSelectedIndex()));
    }

    public void set(String key, JToggleButton button) {
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

    public void setSelectedIndices(String key, JList<?> list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        final int[] selIndices = list.getSelectedIndices();
        final String keySelIndices = key + KEY_APPENDIX_SELECTED;
        if (selIndices.length == 0) {
            properties.remove(keySelIndices);
        } else {
            setIntegerCollection(keySelIndices, ArrayUtil.toList(selIndices));
        }
    }

    public void set(String key, String string) {
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

    public void set(String key, JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        TableModel model = table.getModel();
        List<Integer> colWidths = new ArrayList<>();
        TableColumnModel colModel = table.getColumnModel();
        int tableColumnCount = model.getColumnCount();
        for (int index = 0; index < tableColumnCount; index++) {
            colWidths.add(colModel.getColumn(index).getWidth());
        }
        setIntegerCollection(key, colWidths);
    }

    public void set(String key, JSplitPane splitPane) {
        if (splitPane == null) {
            throw new NullPointerException("splitPane == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        int dividerLocation = splitPane.getDividerLocation();
        properties.setProperty(key, Integer.toString(dividerLocation));
    }

    public void set(String key, JScrollPane scrollPane) {
        if (scrollPane == null) {
            throw new NullPointerException("scrollPane == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        Integer x = scrollPane.getViewport().getViewPosition().x;
        Integer y = scrollPane.getViewport().getViewPosition().y;
        properties.setProperty(key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_X, x.toString());
        properties.setProperty(key + KEY_POSTFIX_VIEWPORT_VIEW_POSITION_Y, y.toString());
    }

    public void set(String key, JTree tree) {
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        int rowCount = tree.getRowCount();
        int pathIndex = 0;    // has to be increased only on written paths!
        deleteKeysMatching(key + "\\.[0-9]+");
        for (int row = 0; row < rowCount; row++) {
            if (tree.isExpanded(row)) {
                setTreePath(toIndexedKey(key, pathIndex), tree.getPathForRow(row).getPath(), tree.isRowSelected(row));
                pathIndex++;
            } else if (tree.isRowSelected(row)) {    // Selected but not expanded
                setTreePath(toIndexedKey(key, pathIndex), tree.getPathForRow(row).getPath(), true);
                pathIndex++;
            }
        }
    }

    private void setTreePath(String key, Object[] path, boolean selected) {
        StringBuilder sb = new StringBuilder();
        for (Object path1 : path) {
            sb.append(path1.toString());
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
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
        return result;
    }

    public void set(String key, int value) {
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

    public void set(String key, boolean b) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        set(key, b
                ? 1
                : 0);
    }

    /**
     * Sets to a component the size and location. Uses the
     * class name as key. If the key does not exist, nothing will be done.
     *
     * @param component component
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
        applySize(component.getClass().getName(), component);
    }

    /**
     * Returns whether the properties contains a specific key.
     *
     * @param  key key
     * @return     true if the properties contains that key
     */
    public boolean containsKey(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        return properties.containsKey(key);
    }

    /**
     * Sets to a component the size. If the key does not
     * exist, nothing will be done.
     *
     * @param component component
     * @param key       key
     */
    public void applySize(String key, Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        String keyWidth = getKeyWidth(key);
        String keyHeight = getKeyHeight(key);
        if (properties.containsKey(keyWidth) && properties.containsKey(keyHeight)) {
            try {
                int width = Integer.parseInt(properties.getProperty(keyWidth));
                int height = Integer.parseInt(properties.getProperty(keyHeight));
                Dimension preferredSize = component.getPreferredSize();
                if (width < preferredSize.width) {
                    width = preferredSize.width;
                }
                if (height < preferredSize.height) {
                    height = preferredSize.height;
                }
                Dimension dimension = new Dimension(width, height);
                component.setPreferredSize(dimension);
                component.setSize(dimension);
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    public boolean containsSizeKey(String key) {
        String keyWidth = getKeyWidth(key);
        String keyHeight = getKeyHeight(key);
        return properties.containsKey(keyWidth) && properties.containsKey(keyHeight);
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
        applyLocation(component.getClass().getName(), component);
    }

    /**
     * Sets to a component the location. If the key does not
     * exist, nothing will be done.
     *
     * @param component component
     * @param key       key
     * @return          true if location has been applied
     */
    public boolean applyLocation(String key, Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        String keyLocationX = getKeyLocationX(key);
        String keyLocationY = getKeyLocationY(key);
        if (properties.containsKey(keyLocationX) && properties.containsKey(keyLocationY)) {
            try {
                int locationX = Integer.parseInt(properties.getProperty(keyLocationX));
                int locationY = Integer.parseInt(properties.getProperty(keyLocationY));
                component.setLocation(new Point(locationX, locationY));
                return true;
            } catch (Throwable t) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, t);
            }
        }
        return false;
    }

    public boolean containsLocationKey(String key) {
        String keyLocationX = getKeyLocationX(key);
        String keyLocationY = getKeyLocationY(key);
        return properties.containsKey(keyLocationX) && properties.containsKey(keyLocationY);
    }

    /**
     * Sets to the {@code Properties} instance the size and location of a component. Uses the
     * class name as key.
     *
     * @param component component
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
        setSize(component.getClass().getName(), component);
    }

    /**
     * Sets to the {@code Properties} instance the size of a component.
     *
     * @param component component
     * @param key       key
     */
    public void setSize(String key, Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
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
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        setLocation(component.getClass().getName(), component);
    }

    /**
     * Sets to the {@code Properties} instance the size of a component.
     *
     * @param component component
     * @param key       key
     */
    public void setLocation(String key, Component component) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        Point location = component.getLocation();
        properties.setProperty(getKeyLocationX(key), Integer.toString(location.x));
        properties.setProperty(getKeyLocationY(key), Integer.toString(location.y));
    }

    private static String getKeyHeight(String key) {
        return key + KEY_POSTFIX_HEIGHT;
    }

    private static String getKeyWidth(String key) {
        return key + KEY_POSTFIX_WIDTH;
    }

    private static String getKeyLocationX(String key) {
        return key + KEY_POSTFIX_LOCATION_X;
    }

    private static String getKeyLocationY(String key) {
        return key + KEY_POSTFIX_LOCATION_Y;
    }

    private String getArrayKeyMatchPattern(String key) {
        return "^" + java.util.regex.Pattern.quote(key + DELIMITER_ARRAY_KEYS) + "[0-9]+$";
    }

    public Set<String> keys() {
        Set<String> keys = new HashSet<>();
        for (Object key: properties.keySet()) {
            if (key != null) {
                keys.add(key.toString());
            }
        }
        return keys;
    }
}
