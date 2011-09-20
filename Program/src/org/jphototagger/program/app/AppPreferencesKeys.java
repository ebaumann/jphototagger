package org.jphototagger.program.app;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface AppPreferencesKeys {

    static final String KEY_CHECK_FOR_UPDATES = "UserSettings.AutoDownloadNewerVersions";
    static final String KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES = "WarnOnEqualBasenamesTaskDialog.DisplayInFuture";
    static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS = "UserSettings.ExecuteActionsAfterImageChangeInDbAlways";
    static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP = "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp";
    static final String KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES = "UserSettings.CopyMoveFiles";
    static final String KEY_GPS_ADD_FILENAME_TO_GPS_LOCATION_EXPORT = "UserSettings.AddFilenameToGpsLocationExport";
    static final String KEY_SAVE_INPUT_EARLY = "UserSettings.SaveInputEarly";
    static final String KEY_SCAN_FOR_EMBEDDED_XMP = "UserSettings.ScanForEmbeddedXmp";
    static final String KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES = "UserSettings.IsAutoscanIncludeSubdirectories";
    static final String KEY_SCHEDULED_TASKS_MINUTES_TO_START_SCHEDULED_TASKS = "UserSettings.MinutesToStartScheduledTasks";
    static final String KEY_UI_DIRECTORIES_TAB_HIDE_ROOT_FILES = "UserSettings.HideRootFilesFromDirectoriesTab";
    static final String KEY_UI_DISPLAY_SEARCH_BUTTON = "UserSettings.DisplaySearchButton";
    static final String KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP = "UserSettings.DisplayThumbnailTooltip";
}
