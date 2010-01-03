/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.event;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.helper.CopyFiles;
import de.elmar_baumann.jpt.helper.CopyFiles.Options;
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
 * @version 2008-09-14
 */
public final class UserSettingsChangeEvent {

    private boolean           acceptHiddenDirectories;
    private boolean           autoscanIncludeSubdirectories;
    private boolean           createThumbnailsWithExternalApp;
    private boolean           displaySearchButton;
    private boolean           executeActionsAfterImageChangeInDbAlways;
    private boolean           executeActionsAfterImageChangeInDbIfImageHasXmp;
    private boolean           noFastSearchColumns;
    private boolean           scanForEmbeddedXmp;
    private boolean           treeDirectoriesSelectLastDirectory;
    private boolean           useEmbeddedThumbnails;
    private Class             logfileFormatterClass;
    private CopyFiles.Options optionsCopyMoveFiles;
    private File              autoCopyDirectory;
    private File              defaultImageOpenApp;
    private Integer           maxSecondsToTerminateExternalPrograms;
    private Integer           maxThumbnailWidth;
    private Integer           minutesToStartScheduledTasks;
    private Level             logLevel;
    private List<Column>      editColumns;
    private List<Column>      fastSearchColumns;
    private Object            source;
    private String            databaseDirectoryName;
    private String            externalThumbnailCreationCommand;
    private String            iptcCharset;
    private String            pdfViewer;
    private String            webBrowser;
    private Type              type;

    public enum Type {
        AUTOCOPY_DIRECTORY,
        AUTOSCAN_DIRECTORIES,
        DATABASE_DIRECTORY,
        DEFAULT_IMAGE_OPEN_APP,
        DISPLAY_SEARCH_BUTTON,
        EDIT_COLUMNS,
        EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS,
        EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP,
        EXTERNAL_THUMBNAIL_CREATION_COMMAND,
        FAST_SEARCH_COLUMNS,
        IPTC_CHARSET,
        IS_ACCEPT_HIDDEN_DIRECTORIES,
        IS_AUTSCAN_INCLUDE_DIRECTORIES,
        IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP,
        IS_USE_EMBEDDED_THUMBNAILS,
        LOGFILE_FORMATTER_CLASS,
        LOG_LEVEL,
        MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS,
        MAX_THUMBNAIL_WIDTH,
        MINUTES_TO_START_SCHEDULED_TASKS,
        NO_FAST_SEARCH_COLUMNS,
        OPTIONS_COPY_MOVE_FILES,
        OTHER_IMAGE_OPEN_APPS,
        PDF_VIEWER,
        SCAN_FOR_EMBEDDED_XMP,
        TREE_DIRECTORIES_SELECT_LAST_DIRECTORY,
        WEB_BROWSER,
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

    public void setMinutesToStartScheduledTasks(Integer minutesToStartScheduledTasks) {
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

    public String getPdfViewer() {
        return pdfViewer;
    }

    public void setPdfViewer(String pdfViewer) {
        this.pdfViewer = pdfViewer;
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
        this.executeActionsAfterImageChangeInDbAlways = executeActionAfterImageChangeInDbAlways;
    }

    public boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        return executeActionsAfterImageChangeInDbIfImageHasXmp;
    }

    public void setExecuteActionsAfterImageChangeInDbIfImageHasXmp(boolean executeActionAfterImageChangeInDbIfImageHasXmp) {
        this.executeActionsAfterImageChangeInDbIfImageHasXmp = executeActionAfterImageChangeInDbIfImageHasXmp;
    }

    public boolean isDisplaySearchButton() {
        return displaySearchButton;
    }

    public void setDisplaySearchButton(boolean displaySearchButton) {
        this.displaySearchButton = displaySearchButton;
    }

    public Options getOptionsCopyMoveFiles() {
        return optionsCopyMoveFiles;
    }

    public void setOptionsCopyMoveFiles(Options optionsCopyMoveFiles) {
        this.optionsCopyMoveFiles = optionsCopyMoveFiles;
    }
}
