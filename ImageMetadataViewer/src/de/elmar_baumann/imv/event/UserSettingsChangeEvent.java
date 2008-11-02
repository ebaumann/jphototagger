package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.database.metadata.Column;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * Action: The user has changed the settings.
 * 
 * <em>The get...() methos returns only valid content for the appropriate
 * action. E.g. if the action is {@link Type#AutocopyDirectory} the
 * appropriate method ist {@link #getAutoCopyDirectory()}. Other getters
 * will return null or an unitialized value!</em>
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class UserSettingsChangeEvent {

    private Type type;
    private Object source;
    private File defaultImageOpenApp;
    private String externalThumbnailCreationCommand;
    private String iptcCharset;
    private File autoCopyDirectory;
    private boolean acceptHiddenDirectories;
    private boolean autoscanIncludeSubdirectories;
    private boolean createThumbnailsWithExternalApp;
    private boolean taskRemoveRecordsWithNotExistingFiles;
    private boolean autocomplete;
    private boolean useEmbeddedThumbnails;
    private boolean noFastSearchColumns;
    private Integer maxThumbnailWidth;
    private Integer minutesToStartScheduledTasks;
    private Integer threadPriority;
    private Level logLevel;
    private Class logfileFormatterClass;
    private List<File> otherImageOpenApps;
    private List<Column> fastSearchColumns;
    private List<Column> editColumns;

    public enum Type {

        AutoscanDirectories,
        AutocopyDirectory,
        DefaultImageOpenApp,
        EditColumns,
        ExternalThumbnailCreationCommand,
        FastSearchColumns,
        IptcCharset,
        IsAcceptHiddenDirectories,
        IsAutoscanIncludeSubdirectories,
        IsCreateThumbnailsWithExternalApp,
        IsTaskRemoveRecordsWithNotExistingFiles,
        IsUseAutocomplete,
        IsUseEmbeddedThumbnails,
        LogfileFormatterClass,
        LogLevel,
        MaxThumbnailWidth,
        MinutesToStartScheduledTasks,
        NoFastSearchColumns,
        OtherImageOpenApps,
        ThreadPriority,
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

    public boolean isAutocomplete() {
        return autocomplete;
    }

    public void setAutocomplete(boolean autocomplete) {
        this.autocomplete = autocomplete;
    }

    public boolean isAutoscanIncludeSubdirectories() {
        return autoscanIncludeSubdirectories;
    }

    public void setAutoscanIncludeSubdirectories(boolean autoscanIncludeSubdirectories) {
        this.autoscanIncludeSubdirectories = autoscanIncludeSubdirectories;
    }

    public boolean isCreateThumbnailsWithExternalApp() {
        return createThumbnailsWithExternalApp;
    }

    public void setCreateThumbnailsWithExternalApp(boolean createThumbnailsWithExternalApp) {
        this.createThumbnailsWithExternalApp = createThumbnailsWithExternalApp;
    }

    public String getExternalThumbnailCreationCommand() {
        return externalThumbnailCreationCommand;
    }

    public void setExternalThumbnailCreationCommand(String externalThumbnailCreationCommand) {
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

    public void setMinutesToStartScheduledTasks(Integer minutesToStartScheduledTasks) {
        this.minutesToStartScheduledTasks = minutesToStartScheduledTasks;
    }

    public boolean isNoFastSearchColumns() {
        return noFastSearchColumns;
    }

    public void setNoFastSearchColumns(boolean noFastSearchColumns) {
        this.noFastSearchColumns = noFastSearchColumns;
    }

    public List<File> getOtherImageOpenApps() {
        return otherImageOpenApps;
    }

    public void setOtherImageOpenApps(List<File> otherImageOpenApps) {
        this.otherImageOpenApps = otherImageOpenApps;
    }

    public boolean isTaskRemoveRecordsWithNotExistingFiles() {
        return taskRemoveRecordsWithNotExistingFiles;
    }

    public void setTaskRemoveRecordsWithNotExistingFiles(boolean taskRemoveRecordsWithNotExistingFiles) {
        this.taskRemoveRecordsWithNotExistingFiles = taskRemoveRecordsWithNotExistingFiles;
    }

    public Integer getThreadPriority() {
        return threadPriority;
    }

    public void setThreadPriority(Integer threadPriority) {
        this.threadPriority = threadPriority;
    }

    public boolean isUseEmbeddedThumbnails() {
        return useEmbeddedThumbnails;
    }

    public void setUseEmbeddedThumbnails(boolean useEmbeddedThumbnails) {
        this.useEmbeddedThumbnails = useEmbeddedThumbnails;
    }
}
