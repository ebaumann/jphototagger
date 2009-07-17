package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.database.metadata.Column;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * Action: The user has changed the settings.
 * 
 * <em>The get...() methos returns only valid content for the appropriate
 * action. E.g. if the action is {@link Type#AUTOCOPY_DIRECTORY} the
 * appropriate method ist {@link #getAutoCopyDirectory()}. Other getters
 * will return null or an unitialized value!</em>
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class UserSettingsChangeEvent {

    private Type type;
    private Object source;
    private File defaultImageOpenApp;
    private String externalThumbnailCreationCommand;
    private String webBrowser;
    private String iptcCharset;
    private String databaseDirectoryName;
    private File autoCopyDirectory;
    private boolean acceptHiddenDirectories;
    private boolean autoscanIncludeSubdirectories;
    private boolean createThumbnailsWithExternalApp;
    private boolean useEmbeddedThumbnails;
    private boolean noFastSearchColumns;
    private boolean treeDirectoriesSelectLastDirectory;
    private boolean scanForEmbeddedXmp;
    private boolean executeActionsAfterImageChangeInDbAlways;
    private boolean executeActionsAfterImageChangeInDbIfImageHasXmp;
    private Integer maxThumbnailWidth;
    private Integer maxSecondsToTerminateExternalPrograms;
    private Integer minutesToStartScheduledTasks;
    private Level logLevel;
    private Class logfileFormatterClass;
    private List<Column> fastSearchColumns;
    private List<Column> editColumns;

    public enum Type {

        AUTOSCAN_DIRECTORIES,
        AUTOCOPY_DIRECTORY,
        DATABASE_DIRECTORY,
        DEFAULT_IMAGE_OPEN_APP,
        EDIT_COLUMNS,
        EXTERNAL_THUMBNAIL_CREATION_COMMAND,
        EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS,
        EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP,
        FAST_SEARCH_COLUMNS,
        IPTC_CHARSET,
        IS_ACCEPT_HIDDEN_DIRECTORIES,
        IS_AUTSCAN_INCLUDE_DIRECTORIES,
        IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP,
        IS_USE_EMBEDDED_THUMBNAILS,
        LOGFILE_FORMATTER_CLASS,
        LOG_LEVEL,
        MAX_THUMBNAIL_WIDTH,
        MINUTES_TO_START_SCHEDULED_TASKS,
        NO_FAST_SEARCH_COLUMNS,
        OTHER_IMAGE_OPEN_APPS,
        WEB_BROWSER,
        TREE_DIRECTORIES_SELECT_LAST_DIRECTORY,
        MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS,
        SCAN_FOR_EMBEDDED_XMP,
    }

    public UserSettingsChangeEvent(Type type, Object source) {
        this.type = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public boolean isAcceptHiddenDirectories() {
        return acceptHiddenDirectories;
    }

    public void setAcceptHiddenDirectories(boolean acceptHiddenDirectories) {
        this.acceptHiddenDirectories = acceptHiddenDirectories;
    }

    public File getAutoCopyDirectory() {
        return autoCopyDirectory;
    }

    public void setAutoCopyDirectory(File autoCopyDirectory) {
        this.autoCopyDirectory = autoCopyDirectory;
    }

    public boolean isTreeDirectoriesSelectLastDirectory() {
        return treeDirectoriesSelectLastDirectory;
    }

    public void setTreeDirectoriesSelectLastDirectory(
            boolean treeDirectoriesSelectLastDirectory) {
        this.treeDirectoriesSelectLastDirectory =
                treeDirectoriesSelectLastDirectory;
    }

    public boolean isAutoscanIncludeSubdirectories() {
        return autoscanIncludeSubdirectories;
    }

    public void setAutoscanIncludeSubdirectories(
            boolean autoscanIncludeSubdirectories) {
        this.autoscanIncludeSubdirectories = autoscanIncludeSubdirectories;
    }

    public boolean isCreateThumbnailsWithExternalApp() {
        return createThumbnailsWithExternalApp;
    }

    public void setCreateThumbnailsWithExternalApp(
            boolean createThumbnailsWithExternalApp) {
        this.createThumbnailsWithExternalApp = createThumbnailsWithExternalApp;
    }

    public String getExternalThumbnailCreationCommand() {
        return externalThumbnailCreationCommand;
    }

    public void setExternalThumbnailCreationCommand(
            String externalThumbnailCreationCommand) {
        this.externalThumbnailCreationCommand = externalThumbnailCreationCommand;
    }

    public List<Column> getFastSearchColumns() {
        return fastSearchColumns;
    }

    public void setFastSearchColumns(List<Column> fastSearchColumns) {
        this.fastSearchColumns = fastSearchColumns;
    }

    public List<Column> getEditColumns() {
        return editColumns;
    }

    public void setEditColumns(List<Column> editColumns) {
        this.editColumns = editColumns;
    }

    public File getDefaultImageOpenApp() {
        return defaultImageOpenApp;
    }

    public void setDefaultImageOpenApp(File app) {
        defaultImageOpenApp = app;
    }

    public String getIptcCharset() {
        return iptcCharset;
    }

    public void setIptcCharset(String iptcCharset) {
        this.iptcCharset = iptcCharset;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public Class getLogfileFormatterClass() {
        return logfileFormatterClass;
    }

    public void setLogfileFormatterClass(Class logfileFormatterClass) {
        this.logfileFormatterClass = logfileFormatterClass;
    }

    public Integer getMaxThumbnailWidth() {
        return maxThumbnailWidth;
    }

    public void setMaxThumbnailWidth(Integer maxThumbnailWidth) {
        this.maxThumbnailWidth = maxThumbnailWidth;
    }

    public Integer getMinutesToStartScheduledTasks() {
        return minutesToStartScheduledTasks;
    }

    public void setMinutesToStartScheduledTasks(
            Integer minutesToStartScheduledTasks) {
        this.minutesToStartScheduledTasks = minutesToStartScheduledTasks;
    }

    public boolean isNoFastSearchColumns() {
        return noFastSearchColumns;
    }

    public void setNoFastSearchColumns(boolean noFastSearchColumns) {
        this.noFastSearchColumns = noFastSearchColumns;
    }

    public boolean isUseEmbeddedThumbnails() {
        return useEmbeddedThumbnails;
    }

    public void setUseEmbeddedThumbnails(boolean useEmbeddedThumbnails) {
        this.useEmbeddedThumbnails = useEmbeddedThumbnails;
    }

    public String getWebBrowser() {
        return webBrowser;
    }

    public void setWebBrowser(String webBrowser) {
        this.webBrowser = webBrowser;
    }

    public String getDatabaseDirectoryName() {
        return databaseDirectoryName;
    }

    public void setDatabaseDirectoryName(String databaseDirectoryName) {
        this.databaseDirectoryName = databaseDirectoryName;
    }

    public void setMaxSecondsToTerminateExternalPrograms(Integer seconds) {
        maxSecondsToTerminateExternalPrograms = seconds;
    }

    public int getMaxSecondsToTerminateExternalPrograms() {
        return maxSecondsToTerminateExternalPrograms;
    }

    public boolean isScanForEmbeddedXmp() {
        return scanForEmbeddedXmp;
    }

    public void setScanForEmbeddedXmp(boolean scanForEmbeddedXmp) {
        this.scanForEmbeddedXmp = scanForEmbeddedXmp;
    }

    public boolean isExecuteActionsAfterImageChangeInDbAlways() {
        return executeActionsAfterImageChangeInDbAlways;
    }

    public void setExecuteActionsAfterImageChangeInDbAlways(
            boolean executeActionAfterImageChangeInDbAlways) {
        this.executeActionsAfterImageChangeInDbAlways =
                executeActionAfterImageChangeInDbAlways;
    }

    public boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        return executeActionsAfterImageChangeInDbIfImageHasXmp;
    }

    public void setExecuteActionsAfterImageChangeInDbIfImageHasXmp(
            boolean executeActionAfterImageChangeInDbIfImageHasXmp) {
        this.executeActionsAfterImageChangeInDbIfImageHasXmp =
                executeActionAfterImageChangeInDbIfImageHasXmp;
    }
}
