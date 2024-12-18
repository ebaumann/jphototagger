package org.jphototagger.program.settings;

/**
 * @author Elmar Baumann
 */
public final class AppPreferencesKeys {

    public static final String KEY_CHECK_FOR_UPDATES = "UserSettings.AutoDownloadNewerVersions";
    public static final String KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES = "WarnOnEqualBasenamesTaskDialog.DisplayInFuture";
    public static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS = "UserSettings.ExecuteActionsAfterImageChangeInDbAlways";
    public static final String KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP = "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp";
    public static final String KEY_FILE_SYSTEM_OPERATIONS_OPTIONS_COPY_MOVE_FILES = "UserSettings.CopyMoveFiles";
    public static final String KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES = "UserSettings.IsAutoscanIncludeSubdirectories";
    public static final String KEY_SCHEDULED_TASKS_MINUTES_TO_START_SCHEDULED_TASKS = "UserSettings.MinutesToStartScheduledTasks";
    public static final String KEY_THUMBNAILS_ZOOM = "org.jphototagger.program.controller.thumbnail.ControllerSliderThumbnailSize." + "SliderValue";
    public static final String KEY_UI_DISPLAY_SEARCH_BUTTON = "UserSettings.DisplaySearchButton";
    public static final String KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP = "UserSettings.DisplayThumbnailTooltip";
    public static final String KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL = "UserSettings.DisplayThumbnailsBottomPanel";
    public static final String KEY_UI_DISPLAY_WORD_SETS_EDIT_PANEL = "UserSettings.DisplayWordSettingsEditPanel";
    public static final String KEY_UI_COLUMNS_MD_TEXT_AREAS = "UserSettings.Columns.MetadataInputTextAreas";
    public static final String KEY_UI_INPUT_HELPER_DIALOG_ALWAYS_ON_TOP = "UserSettings.Ui.InputHelperDialogAlwaysOnTop";

    private AppPreferencesKeys() {
    }
}
