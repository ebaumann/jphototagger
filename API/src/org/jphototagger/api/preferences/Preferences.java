package org.jphototagger.api.preferences;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;

/**
 * @author Elmar Baumann
 */
public interface Preferences {

    static final String KEY_ACCEPT_HIDDEN_DIRECTORIES = "UserSettings.IsAcceptHiddenDirectories";
    static final String KEY_LOG_LEVEL = "UserSettings.Logging.Level";
    static final String KEY_MAX_THUMBNAIL_WIDTH = "UserSettings.MaxThumbnailWidth";

    String getString(String key);

    void setString(String key, String value);

    void setBoolean(String key, boolean value);

    boolean getBoolean(String key);

    void setStringCollection(String key, Collection<? extends String> stringCollection);

    int getInt(String key);

    void setInt(String key, int value);

    void setTree(String key, JTree tree);

    void setScrollPane(String key, JScrollPane scrollPane);

    void setToggleButton(String key, JToggleButton button);

    void setTabbedPane(String key, JTabbedPane pane, PreferencesHints hints);

    void setComponent(Component component, PreferencesHints hints);

    void setSelectedIndex(String key, JComboBox<?> comboBox);

    void setSelectedIndices(String key, JList<?> list);

    boolean containsKey(String key);

    boolean containsLocationKey(String key);

    boolean containsSizeKey(String key);

    void removeKey(String key);

    void removeStringCollection(String key);

    void setSize(String key, Component component);

    void applySize(String key, Component component);

    void applyTreeSettings(String key, JTree tree);

    void applyScrollPaneSettings(String key, JScrollPane scrollPane);

    void applyToggleButtonSettings(String key, JToggleButton button);

    void applyTabbedPaneSettings(String key, JTabbedPane pane, PreferencesHints hints);

    void applyComponentSettings(Component component, PreferencesHints hints);

    void applySelectedIndex(String key, JComboBox<?> comboBox);

    void applySelectedIndices(String key, JList<?> list);

    void setLocation(String key, Component component);

    void applyLocation(String key, Component component);

    List<String> getStringCollection(String key);

    Set<String> keys();
}
