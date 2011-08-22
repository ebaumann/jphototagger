package org.jphototagger.api.core;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Storage {

    public static final String KEY_ACCEPT_HIDDEN_DIRECTORIES = "UserSettings.IsAcceptHiddenDirectories";
    public static final String KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES = "UserSettings.IsAutoscanIncludeSubdirectories";
    public static final String KEY_DATABASE_BACKUP_DIRECTORY = "UserSettings.DatabaseBackupDirectoryName";
    public static final String KEY_DATABASE_BACKUP_INTERVAL = "UserSettings.DbBackupInterval";
    public static final String KEY_DATABASE_DIRECTORY = "UserSettings.DatabaseDirectoryName";
    public static final String KEY_DATABASE_SCHEDULED_BACKUP = "UserSettings.DbScheduledBackup";
    public static final String KEY_DISPLAY_SEARCH_BUTTON = "UserSettings.DisplaySearchButton";
    public static final String KEY_ENABLE_AUTOCOMPLETE = "UserSettings.EnableAutoComplete";
    public static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS = "UserSettings.ExecuteActionsAfterImageChangeInDbAlways";
    public static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP = "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp";
    public static final String KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND = "UserSettings.ExternalThumbnailCreationCommand";
    public static final String KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS = "UserSettings.MaximumSecondsToTerminateExternalPrograms";
    public static final String KEY_MINUTES_TO_START_SCHEDULED_TASKS = "UserSettings.MinutesToStartScheduledTasks";
    public static final String KEY_OPTIONS_COPY_MOVE_FILES = "UserSettings.CopyMoveFiles";
    public static final String KEY_SAVE_INPUT_EARLY = "UserSettings.SaveInputEarly";
    public static final String KEY_SCAN_FOR_EMBEDDED_XMP = "UserSettings.ScanForEmbeddedXmp";
    public static final String KEY_THUMBNAIL_CREATOR = "UserSettings.ThumbnailCreator";
    public static final String KEY_UPDATE_AUTOCOMPLETE = "UserSettings.UpdateAutocomplete";
    public static final String KEY_ADD_FILENAME_TO_GPS_LOCATION_EXPORT = "UserSettings.AddFilenameToGpsLocationExport";
    public static final String KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE = "UserSettings.Autocomplete.IgnoreCase";
    public static final String KEY_HIDE_ROOT_FILES_FROM_DIRECTORIES_TAB = "UserSettings.HideRootFilesFromDirectoriesTab";

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

    void setTabbedPane(String key, JTabbedPane pane, StorageHints hints);

    void setComponent(Component component, StorageHints hints);

    void setSelectedIndex(String key, JComboBox comboBox);

    void setSelectedIndices(String key, JList list);

    boolean containsKey(String key);

    void removeKey(String key);

    void removeStringCollection(String key);

    void setSize(String key, Component component);

    void applySize(String key, Component component);

    void applyTreeSettings(String key, JTree tree);

    void applyScrollPaneSettings(String key, JScrollPane scrollPane);

    void applyToggleButtonSettings(String key, JToggleButton button);

    void applyTabbedPaneSettings(String key, JTabbedPane pane, StorageHints hints);

    void applyComponentSettings(Component component, StorageHints hints);

    void applySelectedIndex(String key, JComboBox comboBox);

    void applySelectedIndices(String key, JList list);

    void setLocation(String key, Component component);

    void applyLocation(String key, Component component);

    List<String> getStringCollection(String key);
}
