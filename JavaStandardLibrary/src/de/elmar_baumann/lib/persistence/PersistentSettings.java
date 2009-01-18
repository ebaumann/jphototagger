package de.elmar_baumann.lib.persistence;

import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

/**
 * Persistent gespeicherte Einstellungen. Benutzung:
 * 
 * <ol>
 *  <li>Einmalig bei Programmstart aufrufen:
 *      <ol>
 *          <li>{@link #setDomainName(java.lang.String)}</li>
 *          <li>{@link #setAppName(java.lang.String)}</li>
 *          <li>bei Bedarf {@link #setFileName(java.lang.String)}</li>
 *      </ol>
 *  <li>Im Verlauf der Anwendung Getter und Setter benutzen</li>
 *  <li>Wenn die Einstellungen in eine Datei geschrieben werden sollen -
 *      spätestens bevor die Anwendung beendet wird -
 *      {@link #writeToFile()} aufrufen</li>
 * </ul>
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class PersistentSettings {

    private static final String delimiterNumberArray = ";"; // NOI18N
    private static final String delimiterArrayKeys = "."; // NOI18N
    private static final String filePathSeparator = "|"; // NOI18N
    private static final String keyPostfixViewportViewPositionX = ".ViewportViewPositionX"; // NOI18N
    private static final String keyPostfixViewportViewPositionY = ".ViewportViewPositionY"; // NOI18N
    private static final String keyAppendixSelected = "-selected"; // NOI18N
    private String propertiesFilename = "Settings.properties"; // NOI18N
    private String domainName = ".de.elmar_baumann"; // NOI18N
    private Properties appProperties;
    private String appName;
    private static final PersistentSettings instance = new PersistentSettings();

    private PersistentSettings() {
    }

    private String getArrayKeyMatchPattern(String key) {
        return "^" + java.util.regex.Pattern.quote(key + delimiterArrayKeys) + "[0-9]+$"; // NOI18N
    }

    /**
     * Liefert alle Schlüssel, die auf ein Muster passen.
     * 
     * @param pattern Muster
     * @return        Passende Schlüssel
     */
    public List<String> getKeysMatches(String pattern) {
        Set<String> allKeys = getProperties().stringPropertyNames();
        List<String> keysMatches = new ArrayList<String>();

        for (String key : allKeys) {
            if (key.matches(pattern)) {
                keysMatches.add(key);
            }
        }
        return keysMatches;
    }

    /**
     * Removes all keys with no value
     */
    public void removeEmptyKeys() {
        Properties properties = getProperties();
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            String value = properties.getProperty(key);
            if (value == null || value.isEmpty()) {
                properties.remove(key);
            }
        }
    }

    /**
     * Setzt den Namen der Domain, beispielsweise <code>.de.elmar_baumann</code>.
     * In einem Verzeichnis dieses Namens unterhalb des Home-Verzeichnisses des
     * Betriebssystems werden die Einstellungen gespeichert.
     * 
     * @param domainName Name, darf keine Zeichen enthalten, die das
     *                   Dateisystem nicht akzeptiert. Tipp: Punkt + eigene Domain benutzen
     *                   gemäß den Regeln zum Benennen von Java-Paketen.
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static PersistentSettings getInstance() {
        return instance;
    }

    /**
     * Liefert die Properties, die persistent gespeichert werden beim Aufruf
     * von {@link #writeToFile()}.
     * 
     * @return Properties
     */
    public Properties getProperties() {
        if (appProperties == null) {
            appProperties = new Properties();
            ensureFileExists();
            readFromFile();
        }
        return appProperties;
    }

    /**
     * Setzt bei den Attributen einer Komponente den gespeicherten Inhalt und
     * zwar bei den Attributen, die setComponent() speichert.
     * 
     * @param component Komponente
     * @param hints     Hinweise
     * @see             #setComponent(java.awt.Component, de.elmar_baumann.lib.persistence.PersistentSettingsHints)
     */
    public void getComponent(Component component, PersistentSettingsHints hints) {
        if (component == null) {
            return;
        }
        Class<? extends Component> c = component.getClass();
        String componentName = c.getName();
        Field[] fields = c.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            field.setAccessible(true);
            String fieldName = field.getName();
            String key = componentName + "." + fieldName + hints.getKeyPostfix(); // NOI18N

            if (hints.isPersistent(key)) {
                try {
                    Class<?> fieldType = field.getType();
                    if (fieldType.equals(JTabbedPane.class)) {
                        getTabbedPane((JTabbedPane) field.get(component), key, hints);
                    } else if (fieldType.equals(JTable.class)) {
                        getTable((JTable) field.get(component), key);
                    } else if (fieldType.equals(JTree.class)) {
                        getTree((JTree) field.get(component), key);
                    } else if (fieldType.equals(JComboBox.class)) {
                        if (hints.isSetComboBoxContent()) {
                            getComboBoxContent((JComboBox) field.get(component), key);
                        } else {
                            getSelectedIndex((JComboBox) field.get(component), key);
                        }
                    } else if (fieldType.equals(JList.class)) {
                        if (hints.isSetListContent()) {
                            getListContent((JList) field.get(component), key);
                        } else {
                            getSelectedIndex((JList) field.get(component), key);
                        }
                    } else if (fieldType.equals(JTextField.class)) {
                        getTextField(((JTextField) field.get(component)), key);
                    } else if (fieldType.equals(JCheckBox.class)) {
                        getCheckBox((JCheckBox) field.get(component), key);
                    } else if (fieldType.equals(JSpinner.class)) {
                        getSpinner((JSpinner) field.get(component), key);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
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
    public void getCheckBox(JCheckBox checkBox, String key) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            checkBox.setSelected(value.equals("1")); // NOI18N
        }
    }

    /**
     * Setzt den Zustand eines Trees, aktuell den selektierten Pfad
     * (nur <strong>eine</strong> Selektion).
     * 
     * @param tree Tree
     * @param key  Schlüssel
     */
    public void getTree(JTree tree, String key) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            TreePath path = TreeUtil.getTreePath(tree.getModel(), value, filePathSeparator);
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
    public void getSplitPane(JSplitPane splitPane, String key) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            try {
                Integer location = new Integer(properties.getProperty(key));
                splitPane.setDividerLocation(location);
            } catch (NumberFormatException ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt  die Eigenschaften einer ScrollPane (aktuell die View-Position des Viewports).
     * 
     * @param scrollPane ScrollPane
     * @param key        Schlüssel
     */
    public void getScrollPane(JScrollPane scrollPane, String key) {
        Properties properties = getProperties();
        String keyX = key + keyPostfixViewportViewPositionX;
        String keyY = key + keyPostfixViewportViewPositionY;
        if (properties.containsKey(keyX) && properties.containsKey(keyY)) {
            try {
                Integer x = new Integer(properties.getProperty(keyX));
                Integer y = new Integer(properties.getProperty(keyY));
                scrollPane.getViewport().setViewPosition(new Point(x, y));
            } catch (NumberFormatException ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt die Eigenschaften einer Tabelle (aktuell: Spaltenbreiten).
     * 
     * @param table Tabelle
     * @param key   Schlüssel
     */
    public void getTable(JTable table, String key) {
        Properties properties = getProperties();
        TableModel model = table.getModel();

        if (properties.containsKey(key)) {
            TableColumnModel colModel = table.getColumnModel();
            List<Integer> storedWidths = getIntegerArray(key);
            int tableColumnCount = model.getColumnCount();
            int storedColumnCount = storedWidths.size();

            for (int index = 0; index < tableColumnCount && index <
                storedColumnCount; index++) {
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
    public void getSpinner(JSpinner spinner, String key) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            try {
                spinner.setValue(new Integer(value));
            } catch (Exception ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
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
    public void getTabbedPane(JTabbedPane pane, String key, PersistentSettingsHints hints) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            try {
                Integer index = new Integer(value);
                if (index < pane.getTabCount()) {
                    pane.setSelectedIndex(index);
                }
            } catch (Exception ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (hints.isTabbedPaneContents()) {
            int componentCount = pane.getComponentCount();
            for (int index = 0; index < componentCount; index++) {
                getComponent(pane.getComponentAt(index), hints);
            }
        }
    }

    /**
     * Setzt den Inhalt einer Combobox. Existierender Inhalt wird entfernt.
     * Geeignet für Comboboxen, deren Items alle Stringobjekte sind.
     * 
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void getComboBoxContent(JComboBox comboBox, String key) {
        Properties properties = getProperties();
        List<String> keys = getKeysMatches(getArrayKeyMatchPattern(key));

        comboBox.removeAllItems();
        for (String vKey : keys) {
            comboBox.addItem(properties.getProperty(vKey));
        }
        getSelectedItem(comboBox, key);
    }

    /**
     * Setzt den Inhalt einer Liste,
     * <em>sofern das Model eine Instanz von DefaultListModel ist</em>.
     * Existierender Inhalt wird entfernt.
     * 
     * @param list Liste
     * @param key  Schlüssel
     */
    public void getListContent(JList list, String key) {
        Properties properties = getProperties();
        ListModel lm = list.getModel();
        if (lm instanceof DefaultListModel) {
            DefaultListModel model = (DefaultListModel) lm;
            List<String> keys = getKeysMatches(getArrayKeyMatchPattern(key));

            model.removeAllElements();
            for (String vKey : keys) {
                model.addElement(properties.getProperty(vKey));
            }
            list.setModel(model);
            getSelectedValue(list, key);
        }
    }

    /**
     * Setzt das selektierte Item einer Combobox.
     * 
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void getSelectedItem(JComboBox comboBox, String key) {
        Properties properties = getProperties();
        String sKey = key + keyAppendixSelected;
        String selected = properties.getProperty(sKey);

        if (selected != null) {
            comboBox.setSelectedItem(selected);
        }
    }

    /**
     * Setzt den Index eines selektierten Combobox-Items.
     * 
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void getSelectedIndex(JComboBox comboBox, String key) {
        Properties properties = getProperties();
        String index = properties.getProperty(key + keyAppendixSelected);
        if (index != null) {
            try {
                Integer ind = new Integer(index);
                if (ind < comboBox.getItemCount()) {
                    comboBox.setSelectedIndex(ind);
                }
            } catch (Exception ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt den Status eines Toggle-Buttons (<code>isSelected()</code>).
     * 
     * @param button Button
     * @param key    Schlüssel
     */
    public void getToggleButton(JToggleButton button,
        String key) {
        Properties properties = getProperties();
        String status = properties.getProperty(key);
        if (status != null) {
            boolean isSelected = status.equals("1"); // NOI18N
            button.setSelected(isSelected);
        }
    }

    /**
     * Setzt selektierten Wert einer Liste.
     * 
     * @param list Liste
     * @param key  Schlüssel
     */
    public void getSelectedValue(JList list, String key) {
        Properties properties = getProperties();
        String sKey = key + keyAppendixSelected;
        String selected = properties.getProperty(sKey);

        if (selected != null) {
            list.setSelectedValue(selected, true);
        }
    }

    /**
     * Setzt selektierten Wert einer Liste.
     * 
     * @param list Liste
     * @param key  Schlüssel
     */
    public void getSelectedIndex(JList list, String key) {
        Properties properties = getProperties();
        String sKey = key + keyAppendixSelected;
        String index = properties.getProperty(sKey);

        if (index != null) {
            try {
                Integer ind = new Integer(index);
                if (ind < list.getModel().getSize()) {
                    list.setSelectedIndex(ind);
                }
            } catch (Exception ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Setzt den Inhalt eines Textfelds.
     * 
     * @param textField Textfelds
     * @param key       Schlüssel
     */
    public void getTextField(JTextField textField, String key) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            String value = properties.getProperty(key);
            textField.setText(value);
        }
    }

    /**
     * Liefert einen String.
     * 
     * @param key Schlüssel
     * @return    String, Leerstring, wenn zum Schlüssel keiner existiert
     */
    public String getString(String key) {
        Properties properties = getProperties();

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }

        return ""; // NOI18N
    }

    /**
     * Speichert ein Integer-Array.
     * 
     * @param array  Array
     * @param key    Schlüssel
     */
    public void setIntegerArray(List<Integer> array, String key) {
        StringBuffer buffer = new StringBuffer();

        for (Integer integer : array) {
            buffer.append(integer.toString());
            buffer.append(delimiterNumberArray);
        }
        getProperties().setProperty(key, buffer.toString());
    }

    /**
     * Speichert ein Stringarray.
     * 
     * @param list Array
     * @param key  Schlüssel
     */
    public void setStringArray(List<String> list, String key) {
        deleteKeysMatches(getArrayKeyMatchPattern(key));
        Properties properties = getProperties();
        int count = list.size();
        for (int i = 0; i < count; i++) {
            properties.setProperty(key + delimiterArrayKeys + Integer.toString(i), list.get(i));
        }
    }

    /**
     * Löscht alle Schlüssel mit bestimmtem Muster.
     * 
     * @param pattern Muster
     */
    public void deleteKeysMatches(String pattern) {
        List<String> keys = getKeysMatches(pattern);
        Properties properties = getProperties();
        for (String pKey : keys) {
            properties.remove(pKey);
        }
    }

    /**
     * Liefert ein Integer-Array.
     * 
     * @param key Schlüssel
     * @return    Array
     */
    public List<Integer> getIntegerArray(String key) {
        Properties properties = getProperties();
        List<Integer> array = new ArrayList<Integer>();

        if (properties.containsKey(key)) {
            StringTokenizer tokenizer = new StringTokenizer(properties.getProperty(key), delimiterNumberArray);

            while (tokenizer.hasMoreTokens()) {
                try {
                    array.add(new Integer(tokenizer.nextToken()));
                } catch (Exception ex) {
                    Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return array;
    }

    /**
     * Liefert ein Stringarray.
     * 
     * @param key Schlüssel
     * @return    Stringarray
     */
    public List<String> getStringArray(String key) {
        List<String> array = new ArrayList<String>();
        List<String> keys = getKeysMatches(getArrayKeyMatchPattern(key));
        Properties properties = getProperties();
        for (String vKey : keys) {
            array.add(properties.getProperty(vKey));
        }
        return array;
    }

    /**
     * Removes a string array.
     * 
     * @param key  key of the string array
     */
    public void removeStringArray(String key) {
        List<String> keys = getKeysMatches(getArrayKeyMatchPattern(key));
        Properties properties = getProperties();
        for (String k : keys) {
            properties.remove(k);
        }
    }

    /**
     * Speichert die Attribute einer Komponente.
     * 
     * Unterstützt:
     * <ul>
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
     * @see             #getComponent(java.awt.Component, de.elmar_baumann.lib.persistence.PersistentSettingsHints)
     */
    public void setComponent(Component component,
        PersistentSettingsHints hints) {
        if (component == null) {
            return;
        }
        Class<? extends Component> c = component.getClass();
        String componentName = c.getName();
        Field[] fields = c.getDeclaredFields();

        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            field.setAccessible(true);
            String fieldName = field.getName();
            String key = componentName + "." + fieldName + hints.getKeyPostfix(); // NOI18N

            if (hints.isPersistent(key)) {
                try {
                    Class<?> fieldType = field.getType();
                    if (fieldType.equals(JTextField.class)) {
                        setTextField((JTextField) field.get(component), key);
                    } else if (fieldType.equals(JComboBox.class)) {
                        if (hints.isSetComboBoxContent()) {
                            setComboBoxContent((JComboBox) field.get(component), key);
                        } else {
                            setSelectedIndex((JComboBox) field.get(component), key);
                        }
                    } else if (fieldType.equals(JList.class)) {
                        if (hints.isSetListContent()) {
                            setListContent((JList) field.get(component), key);
                        } else {
                            setSelectedIndex((JList) field.get(component), key);
                        }
                    } else if (fieldType.equals(JCheckBox.class)) {
                        setCheckBox((JCheckBox) field.get(component), key);
                    } else if (fieldType.equals(JTabbedPane.class)) {
                        setTabbedPane((JTabbedPane) field.get(component), key, hints);
                    } else if (fieldType.equals(JSpinner.class)) {
                        setSpinner((JSpinner) field.get(component), key);
                    } else if (fieldType.equals(JTable.class)) {
                        setTable((JTable) field.get(component), key);
                    } else if (fieldType.equals(JSplitPane.class)) {
                        setSplitPane((JSplitPane) field.get(component), key);
                    } else if (fieldType.equals(JTree.class)) {
                        setTree((JTree) field.get(component), key);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
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
    public void setCheckBox(JCheckBox checkBox, String key) {
        String isSelected = checkBox.isSelected() ? "1" : "0"; // NOI18N

        getProperties().setProperty(key, isSelected);
    }

    /**
     * Speichert den Wert eines Spinners.
     * 
     * @param spinner Spinner
     * @param key     Schlüssel
     */
    public void setSpinner(JSpinner spinner, String key) {
        getProperties().setProperty(key, spinner.getValue().toString());
    }

    /**
     * Speichert den Inhalt einer Tabbed Pane und ihrer Komponenten.
     * 
     * @param pane  Pane
     * @param key   Schlüssel
     * @param hints Hinweise
     */
    public void setTabbedPane(JTabbedPane pane, String key, PersistentSettingsHints hints) {
        try {
            Integer index = new Integer(pane.getSelectedIndex());

            getProperties().setProperty(key, index.toString());

            if (hints.isTabbedPaneContents()) {
                int componentCount = pane.getComponentCount();
                for (int i = 0; i < componentCount; i++) {
                    setComponent(pane.getComponentAt(i), hints);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Speichert den Inhalt einer Combobox. Geeignet sind Comboboxen mit
     * Strings.
     * 
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void setComboBoxContent(JComboBox comboBox, String key) {
        Properties properties = getProperties();
        int itemCount = comboBox.getItemCount();

        deleteKeysMatches(getArrayKeyMatchPattern(key));
        for (int index = 0; index < itemCount; index++) {
            Object item = comboBox.getItemAt(index);
            if (item != null) {
                String cKey = key + delimiterArrayKeys + Integer.toString(index);
                properties.setProperty(cKey, item.toString());
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
    public void setListContent(JList list, String key) {
        Properties properties = getProperties();
        ListModel model = list.getModel();
        int itemCount = model.getSize();

        deleteKeysMatches(getArrayKeyMatchPattern(key));
        for (int i = 0; i < itemCount; i++) {
            properties.setProperty(
                key + delimiterArrayKeys + Integer.toString(i),
                model.getElementAt(i).toString());
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
        getProperties().setProperty(
            key + keyAppendixSelected,
            Integer.toString(comboBox.getSelectedIndex()));
    }

    /**
     * Speichert den Status eines Toggle-Buttons (<code>isSelected()</code>).
     * 
     * @param button Button
     * @param key Schlüssel
     */
    public void setToggleButton(JToggleButton button, String key) {
        String status = button.isSelected() ? "1" : "0"; // NOI18N
        getProperties().setProperty(key, status);
    }

    /**
     * Speichertden selektierten Wert einer Liste.
     * Geeignet für Listen mit Strings.
     * 
     * @param list Liste
     * @param key  Schlüssel
     */
    public void setSelectedValue(JList list, String key) {
        String sKey = key + keyAppendixSelected;
        Object selectedValue = list.getSelectedValue();

        if (selectedValue == null) {
            getProperties().remove(key);
        } else {
            getProperties().setProperty(sKey, selectedValue.toString());
        }
    }

    /**
     * Speichert den Index des selektierten Items einer Combobox.
     * 
     * @param list  Liste
     * @param key   Schlüssel
     */
    public void setSelectedIndex(JList list, String key) {
        getProperties().setProperty(
            key + keyAppendixSelected,
            Integer.toString(list.getSelectedIndex()));
    }

    /**
     * Speichert das selektierte Item einer Combobox.
     * Geeignet für Comboboxen mit Strings.
     * 
     * @param comboBox Combobox
     * @param key      Schlüssel
     */
    public void setSelectedItem(JComboBox comboBox, String key) {
        String sKey = key + keyAppendixSelected;
        Object selected = comboBox.getSelectedItem();

        if (selected == null) {
            getProperties().remove(sKey);
        } else {
            getProperties().setProperty(sKey, selected.toString());
        }
    }

    /**
     * Speichert den Inhalt eines Textfelds.
     * 
     * @param textField Textfeld
     * @param key       Schlüssel
     */
    public void setTextField(JTextField textField, String key) {
        getProperties().setProperty(key, textField.getText());
    }

    /**
     * Speichert einen String.
     * 
     * @param string String
     * @param key    Schlüssel
     */
    public void setString(String string, String key) {
        getProperties().setProperty(key, string);
    }

    /**
     * Entfernt einen Schlüssel (plus Wert).
     * 
     * @param key Schlüssel
     */
    public void removeKey(String key) {
        getProperties().remove(key);
    }

    /**
     * Speichert gesetzte Einstellungen persistent.
     * <em>Ohne Aufruf werden diese nicht gespeichert!</em>
     * 
     * @return true bei Erfolg
     */
    public boolean writeToFile() {
        try {
            FileOutputStream out = new FileOutputStream(getPropertyFilePathName());
            getProperties().store(out, "--- " + appName + " persistent settings ---"); // NOI18N
            out.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void readFromFile() {
        FileInputStream in;
        try {
            in = new FileInputStream(getPropertyFilePathName());
            appProperties.load(in);
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getPropertyFilePathName() {
        return getDirectoryName() + File.separator + getFileName();
    }

    /**
     * Setzt den Namen der Datei, in die die Einstellungen geschrieben werden.
     * 
     * @param fileName Dateiname <em>ohne</em> Pfad.
     *                 Default <code>Settings.properties</code>.
     */
    public void setFileName(String fileName) {
        propertiesFilename = fileName;
    }

    private String getFileName() {
        return propertiesFilename;
    }

    /**
     * Setzt den Namen der Anwendung. Für diesen wird im Home-Verzeichnis ein
     * Unterverzeichnis angelegt, in das die Einstellungsdatei geschrieben wird.
     * 
     * @param appName Anwendungsname
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Returns the application's name.
     * 
     * @return name
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Liefert den Namen des Verzeichnisses, in dem die Einstellungen
     * gespeichert werden.
     * 
     * @return Verzeichnisname
     */
    public String getDirectoryName() {
        String homeDir = System.getProperty("user.home"); // NOI18N
        return homeDir + (appName == null
            ? "" // NOI18N
            : File.separator + domainName + File.separator + appName);
    }

    private void ensureFileExists() {
        if (ensureDirectoryExists()) {
            try {
                File file = new File(getPropertyFilePathName());
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean ensureDirectoryExists() {
        String directoryName = getDirectoryName();
        File directory = new File(directoryName);

        if (!directory.exists() && !directory.mkdirs()) {
            Logger.getLogger(PersistentSettings.class.getName()).log(
                Level.SEVERE, null, Bundle.getString("PersistentSettings.ErrorMessage.CreateDirectoryFailed"));
            return false;
        }
        return true;
    }

    /**
     * Speichert die Eigenschaften einer Tabelle (aktuell die Spaltenbreiten).
     * 
     * @param table Tabelle
     * @param key Schlüssel
     */
    public void setTable(JTable table, String key) {
        TableModel model = table.getModel();
        List<Integer> persistentColumnWidths = new ArrayList<Integer>();

        TableColumnModel colModel = table.getColumnModel();
        int tableColumnCount = model.getColumnCount();

        for (int index = 0; index < tableColumnCount; index++) {
            persistentColumnWidths.add(
                new Integer(colModel.getColumn(index).getWidth()));
        }
        setIntegerArray(persistentColumnWidths, key);
    }

    /**
     * Speichert die Eigenschaften einer Splitpane (aktuell die Position des
     * Dividers).
     * 
     * @param splitPane Splitpane
     * @param key       Schlüssel
     */
    public void setSplitPane(JSplitPane splitPane, String key) {
        int dividerLocation = splitPane.getDividerLocation();
        Properties properties = getProperties();

        properties.setProperty(key, Integer.toString(dividerLocation));
    }

    /**
     * Speichert die Eigenschaften einer ScrollPane (aktuell die
     * View-Position des Viewports).
     * 
     * @param scrollPane ScrolPane
     * @param key        Schlüssel Schlüssel
     */
    public void setScrollPane(JScrollPane scrollPane, String key) {
        Integer x = scrollPane.getViewport().getViewPosition().x;
        Integer y = scrollPane.getViewport().getViewPosition().y;
        Properties properties = getProperties();

        properties.setProperty(key + keyPostfixViewportViewPositionX, x.toString());
        properties.setProperty(key + keyPostfixViewportViewPositionY, y.toString());
    }

    /**
     * Speichert einen Tree, aktuell den zuerst selektierten Pfad.
     * 
     * @param tree Tree
     * @param key  Schlüssel
     */
    public void setTree(JTree tree, String key) {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null) {
            getProperties().remove(key);
        } else {
            Object[] path = selectionPath.getPath();
            StringBuffer pathBuffer = new StringBuffer();
            for (int index = 0; index < path.length; index++) {
                pathBuffer.append(path[index].toString() + (index + 1 <
                    path.length ? filePathSeparator : "")); // NOI18N
            }
            getProperties().setProperty(key, pathBuffer.toString());
        }
    }

    /**
     * Returns an Integer value.
     * 
     * @param  key  key
     * @return value or <code>Integer.MIN_VALUE</code> if not defined
     */
    public Integer getInt(String key) {
        Integer result = Integer.MIN_VALUE;
        if (getProperties().containsKey(key)) {
            try {
                result = Integer.parseInt(getProperties().getProperty(key));
            } catch (NumberFormatException ex) {
                Logger.getLogger(PersistentSettings.class.getName()).log(Level.SEVERE, null, ex);
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
    public void setInt(Integer value, String key) {
        Properties properties = getProperties();
        properties.setProperty(key, value.toString());
    }

    public boolean getBoolean(String key) {
        int result = getInt(key);
        return result == 1 ? true : false;
    }

    public void setBoolean(boolean b, String key) {
        setInt(b ? 1 : 0, key);
    }
}
