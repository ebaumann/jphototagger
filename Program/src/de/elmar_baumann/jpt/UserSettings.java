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
package de.elmar_baumann.jpt;

import de.elmar_baumann.jpt.app.AppInfo;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.update.UpdateUserSettings;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.ColumnUtil;
import de.elmar_baumann.jpt.database.metadata.selections.EditColumns;
import de.elmar_baumann.jpt.event.UserSettingsEvent;
import de.elmar_baumann.jpt.event.UserSettingsEvent.Type;
import de.elmar_baumann.jpt.event.listener.UserSettingsListener;
import de.elmar_baumann.jpt.event.listener.impl.UserSettingsListenerSupport;
import de.elmar_baumann.jpt.helper.CopyFiles;
import de.elmar_baumann.jpt.helper.CopyFiles.Options;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailCreator;
import de.elmar_baumann.jpt.types.Filename;
import de.elmar_baumann.lib.dialog.DirectoryChooser;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.util.ArrayUtil;
import de.elmar_baumann.lib.util.PropertiesFile;
import de.elmar_baumann.lib.util.Settings;
import de.elmar_baumann.lib.util.SettingsHints;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.XMLFormatter;

/**
 * Stores user settings in a single {@link java.util.Properties} instance.
 * <p>
 * To make changes permanent the application has to call {@link #writeToFile()}.
 * <p>
 * While creating an instance, this class loads the written properties if is
 * does exist.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class UserSettings {

    private static final int                         DEFAULT_MAX_THUMBNAIL_WIDTH                                   = 150;
    private static final int                         DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS                      = 5;
    private static final String                      DELIMITER_COLUMNS                                             = "\t";
    private static final String                      DOMAIN_NAME                                                   = "de.elmar_baumann"; // NEVER CHANGE!
    private static final String                      KEY_ACCEPT_HIDDEN_DIRECTORIES                                 = "UserSettings.IsAcceptHiddenDirectories";
    private static final String                      KEY_AUTO_DOWNLOAD_NEWER_VERSIONS                              = "UserSettings.AutoDownloadNewerVersions";
    private static final String                      KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES                          = "UserSettings.IsAutoscanIncludeSubdirectories";
    private static final String                      KEY_DATABASE_DIRECTORY                                        = "UserSettings.DatabaseDirectoryName";
    private static final String                      KEY_DEFAULT_IMAGE_OPEN_APP                                    = "UserSettings.DefaultImageOpenApp";
    private static final String                      KEY_DISPLAY_SEARCH_BUTTON                                     = "UserSettings.DisplaySearchButton";
    private static final String                      KEY_EDIT_COLUMNS                                              = "UserSettings.EditColumns";
    private static final String                      KEY_DISPLAY_IPTC                                              = "UserSettings.DisplayIptc";
    private static final String                      KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS           = "UserSettings.ExecuteActionsAfterImageChangeInDbAlways";
    private static final String                      KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP = "UserSettings.ExecuteActionsAfterImageChangeInDbIfImageHasXmp";
    private static final String                      KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND                       = "UserSettings.ExternalThumbnailCreationCommand";
    private static final String                      KEY_FAST_SEARCH_COLUMNS                                       = "UserSettings.FastSearchColumns";
    private static final String                      KEY_IPTC_CHARSET                                              = "UserSettings.IptcCharset";
    private static final String                      KEY_LOGFILE_FORMATTER_CLASS                                   = "UserSettings.LogfileFormatterClass";
    private static final String                      KEY_LOG_LEVEL                                                 = "UserSettings.LogLevel";
    private static final String                      KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS                = "UserSettings.MaximumSecondsToTerminateExternalPrograms";
    private static final String                      KEY_MAX_THUMBNAIL_WIDTH                                       = "UserSettings.MaxThumbnailWidth";
    private static final String                      KEY_MINUTES_TO_START_SCHEDULED_TASKS                          = "UserSettings.MinutesToStartScheduledTasks";
    private static final String                      KEY_OPTIONS_COPY_MOVE_FILES                                   = "UserSettings.CopyMoveFiles";
    private static final String                      KEY_PDF_VIEWER                                                = "UserSettings.PdfViewer";
    private static final String                      KEY_SAVE_INPUT_EARLY                                          = "UserSettings.SaveInputEarly";
    private static final String                      KEY_SCAN_FOR_EMBEDDED_XMP                                     = "UserSettings.ScanForEmbeddedXmp";
    private static final String                      KEY_THUMBNAIL_CREATOR                                         = "UserSettings.ThumbnailCreator";
    private static final String                      KEY_TREE_DIRECTORIES_SELECT_LAST_DIRECTORY                    = "UserSettings.TreeDirectoriesSelectLastDirectory";
    private static final String                      KEY_WEB_BROWSER                                               = "UserSettings.WebBrowser";
    private static final String                      PROPERTIES_FILENAME                                           = "Settings.properties"; // NEVER CHANGE!
    public static final  SettingsHints               SET_TABBED_PANE_SETTINGS                                      = new SettingsHints(SettingsHints.Option.SET_TABBED_PANE_CONTENT);
    private final        Properties                  properties                                                    = new Properties();
    private final        PropertiesFile              propertiesFile                                                = new PropertiesFile(DOMAIN_NAME, AppInfo.PROJECT_NAME, PROPERTIES_FILENAME, properties);
    private final        Settings                    settings                                                      = new Settings(properties);
    public static final  UserSettings                INSTANCE                                                      = new UserSettings();
    private final        UserSettingsListenerSupport listenerSupport                                               = new UserSettingsListenerSupport();

    private UserSettings() {
        propertiesFile.readFromFile();
        UpdateUserSettings.update(properties);
        settings.removeKeysWithEmptyValues();
        writeToFile();
    }

    /**
     * Returns the properties with the user settings.
     * <p>
     * If You are modifying the properties not through a setter of this class,
     * You have to call {@link #writeToFile()} to make the changes persistent.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Returns the settings object instanciated with the properties file of
     * this class.
     * <p>
     * If You are modifying the properties through the settings (calling a
     * setter of the class <code>Settings</code>) rather than through a setter
     * of this class, You have to call {@link #writeToFile()} to make the
     * changes persistent.
     *
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Writes the properties to a file.
     * <p>
     * The setters of this class always calling this method after updating the
     * properties. If You change the properties outside
     * ({@link #getProperties()}, {@link #getSettings()}, You have to call
     * <code>writeToFile()</code> to make changes persistent.
     *
     */
    public void writeToFile() {
        propertiesFile.writeToFile();
    }

    /**
     * Returns directory name of the propertie's file .
     *
     * @return directory name
     */
    public String getSettingsDirectoryName() {
        return propertiesFile.getDirectoryName();
    }

    public void setDatabaseDirectoryName(String directoryName) {
        settings.set(directoryName, KEY_DATABASE_DIRECTORY);
        writeToFile();
        notifyListeners(Type.DATABASE_DIRECTORY);
    }

    /**
     * Returns the name of the directory where the database file is located.
     *
     * @return directory name
     */
    public String getDatabaseDirectoryName() {
        return properties.containsKey(KEY_DATABASE_DIRECTORY)
               ? settings.getString(KEY_DATABASE_DIRECTORY)
               : getDefaultDatabaseDirectoryName();
    }

    /**
     * Returns the default name of the directory where the database file is
     * located.
     * <p>
     * When the user doesn't change the directory, the database file is
     * in the same directory as the properties file.
     *
     * @return directory name
     */
    public String getDefaultDatabaseDirectoryName() {
        return getSettingsDirectoryName();
    }

    /**
     * Returns the filename of the database.
     *
     * @param  name name token. Has to be {@link Filename#FULL_PATH} or
     *              {@link Filename#FULL_PATH_NO_SUFFIX}.
     * @return      filename
     */
    public String getDatabaseFileName(Filename name) {

        assert    name.equals(Filename.FULL_PATH)
               || name.equals(Filename.FULL_PATH_NO_SUFFIX) : name;

        return getDatabaseDirectoryName() + File.separator + "database" +
                (name.equals(Filename.FULL_PATH)
                 ? ".data"
                 : "");
    }

    /**
     * Returns the directory name where the thumbnails are located.
     * <p>
     * This is a directory below the database directory.
     *
     * @return directory name
     */
    public String getThumbnailsDirectoryName() {
        return getDatabaseDirectoryName() + File.separator + "thumbnails";
    }

    public DirectoryChooser.Option getDirChooserOptionShowHiddenDirs() {
        return isAcceptHiddenDirectories()
                   ? DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES
                   : DirectoryChooser.Option.NO_OPTION;
    }

    public DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    public void setThumbnailCreator(ThumbnailCreator creator) {
        properties.put(KEY_THUMBNAIL_CREATOR, creator.name());
        writeToFile();
        notifyListeners(Type.THUMBNAIL_CREATOR);
    }

    public ThumbnailCreator getThumbnailCreator() {
        return properties.containsKey(KEY_THUMBNAIL_CREATOR)
                ? ThumbnailCreator.valueOf(properties.getProperty(KEY_THUMBNAIL_CREATOR))
                : ThumbnailCreator.JAVA_IMAGE_IO;
    }

    public void setExternalThumbnailCreationCommand(String command) {
        settings.set(command, KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
        writeToFile();
        notifyListeners(Type.EXTERNAL_THUMBNAIL_CREATION_COMMAND);
    }

    /**
     * Returns the command line of the external program which creates the
     * thumbnails.
     *
     * @return command line or empty string when not defined
     */
    public String getExternalThumbnailCreationCommand() {
        return settings.getString(KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
    }

    public void setWebBrowser(String webBrowser) {
        settings.set(webBrowser, KEY_WEB_BROWSER);
        writeToFile();
        notifyListeners(Type.WEB_BROWSER);
    }

    /**
     * Returns the path to the web browser.
     *
     * @return path (filename) or empty string if not defined
     */
    public String getWebBrowser() {
        return settings.getString(KEY_WEB_BROWSER);
    }

    public void setPdfViewer(String pdfViewer) {
        settings.set(pdfViewer, KEY_PDF_VIEWER);
        writeToFile();
        notifyListeners(Type.PDF_VIEWER);
    }

    /**
     * Returns the path to the PDF viewer.
     *
     * @return path (filename) or empty string if not defined
     */
    public String getPdfViewer() {
        return settings.getString(KEY_PDF_VIEWER);
    }

    public void setLogLevel(Level logLevel) {
        settings.set(logLevel.toString(), KEY_LOG_LEVEL);
        writeToFile();
        notifyListeners(Type.LOG_LEVEL);
    }

    /**
     * Returns the log level.
     *
     * @return log level as returned from {@link Level#getLocalizedName()}.
     *         Default: {@link Level#INFO}
     */
    public Level getLogLevel() {
        Level level = null;
        if (properties.containsKey(KEY_LOG_LEVEL)) {
            String levelString = settings.getString(KEY_LOG_LEVEL);
            try {
                level = Level.parse(levelString);
            } catch (Exception ex) {
                AppLogger.logSevere(UserSettings.class, ex);
            }
        }
        if (level == null) {
            settings.set(Level.INFO.getLocalizedName(), KEY_LOG_LEVEL);
        }
        return level == null
               ? Level.INFO
               : level;
    }

    public void setFastSearchColumns(List<Column> columns) {
        boolean hasColumns = columns.size() > 0;

        if (hasColumns) {
            settings.set(getColumnKeys(columns), KEY_FAST_SEARCH_COLUMNS);
        } else {
            properties.remove(KEY_FAST_SEARCH_COLUMNS);
        }

        writeToFile();
        notifyListeners(hasColumns
                            ? UserSettingsEvent.Type.FAST_SEARCH_COLUMNS
                            : UserSettingsEvent.Type.NO_FAST_SEARCH_COLUMNS);
    }

    private String getColumnKeys(List<Column> columns) {
        StringBuffer buffer = new StringBuffer();
        for (Column column : columns) {
            buffer.append(column.getKey() + DELIMITER_COLUMNS);
        }
        return buffer.toString();
    }

    /**
     * Returns the columns to include into the fast search.
     *
     * @return columns. Default: Empty list
     */
    public List<Column> getFastSearchColumns() {
        List<Column> columns = new ArrayList<Column>();
        if (!settings.getString(KEY_FAST_SEARCH_COLUMNS).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(KEY_FAST_SEARCH_COLUMNS),
                    DELIMITER_COLUMNS);
            return ColumnUtil.columnKeysToColumns(columnKeys);
        }
        return columns;
    }

    public void setEditColumns(List<Column> columns) {
        settings.set(getColumnKeys(columns), KEY_EDIT_COLUMNS);
        writeToFile();
        notifyListeners(Type.EDIT_COLUMNS);
    }

    /**
     * Returns the edit columns the user want to see in the edit columns panel
     * array.
     *
     * @return edit columns
     */
    public List<Column> getEditColumns() {
        if (!settings.getString(KEY_EDIT_COLUMNS).isEmpty()) {
            List<String> columnKeys = ArrayUtil.stringTokenToList(
                    settings.getString(KEY_EDIT_COLUMNS), DELIMITER_COLUMNS);
            return ColumnUtil.columnKeysToColumns(columnKeys);
        }
        return new ArrayList<Column>(EditColumns.get());
    }

    public void setDefaultImageOpenApp(File app) {
        settings.set(app.getAbsolutePath(), KEY_DEFAULT_IMAGE_OPEN_APP);
        writeToFile();
        notifyListeners(Type.DEFAULT_IMAGE_OPEN_APP);
    }

    /**
     * Returns the path to the application which opens images when double
     * clicking onto a thumbnail.
     *
     * @return path or empty string if not defined
     */
    public String getDefaultImageOpenApp() {
        return settings.getString(KEY_DEFAULT_IMAGE_OPEN_APP);
    }

    /**
     * Returns whether a default image open application is defined.
     *
     * @return true if defined
     */
    public boolean hasDefaultImageOpenApp() {
        return !getDefaultImageOpenApp().isEmpty();
    }

    /**
     * Returns the maximum length of the thumbnail width.
     *
     * @return maximum length in pixel. Default: Internal constant
     *         <code>DEFAULT_MAX_THUMBNAIL_LENGTH</code>.
     */
    public int getMaxThumbnailWidth() {
        int width = settings.getInt(KEY_MAX_THUMBNAIL_WIDTH);
        return width != Integer.MIN_VALUE
               ? width
               : DEFAULT_MAX_THUMBNAIL_WIDTH;
    }

    public void setDisplaySearchButton(boolean display) {
        settings.set(display, KEY_DISPLAY_SEARCH_BUTTON);
        writeToFile();
        notifyListeners(Type.DISPLAY_SEARCH_BUTTON);
    }

    /**
     * Returns wheter the search button shall be displayed.
     *
     * @return true, if the search button shall be displayed
     *         Default: <code>true</code>
     */
    public boolean isDisplaySearchButton() {
        return properties.containsKey(KEY_DISPLAY_SEARCH_BUTTON)
               ? settings.getBoolean(KEY_DISPLAY_SEARCH_BUTTON)
               : true;
    }

    public void setScanForEmbeddedXmp(boolean scan) {
        settings.set(scan, KEY_SCAN_FOR_EMBEDDED_XMP);
        writeToFile();
        notifyListeners(Type.SCAN_FOR_EMBEDDED_XMP);
    }

    /**
     * Returns whether to scan for embedded XMP metadata if no sidecar file
     * exists.
     *
     * @return true, when to scan image files for embedded XMP metadata.
     *         Default: <code>false</code>.
     */
    public boolean isScanForEmbeddedXmp() {
        return properties.containsKey(KEY_SCAN_FOR_EMBEDDED_XMP)
               ? settings.getBoolean(KEY_SCAN_FOR_EMBEDDED_XMP)
               : false;
    }

    public void setCopyMoveFilesOptions(Options options) {
        settings.set(options.getInt(), KEY_OPTIONS_COPY_MOVE_FILES);
        writeToFile();
        notifyListeners(Type.OPTIONS_COPY_MOVE_FILES);
    }

    /**
     * Returns the options when copying or moving files.
     *
     * @return options. Default: <code>CONFIRM_OVERWRITE</code>
     */
    public CopyFiles.Options getCopyMoveFilesOptions() {
        return properties.containsKey(KEY_OPTIONS_COPY_MOVE_FILES)
               ? CopyFiles.Options.fromInt(settings.getInt(KEY_OPTIONS_COPY_MOVE_FILES))
               : CopyFiles.Options.CONFIRM_OVERWRITE;
    }

    public void setExecuteActionsAfterImageChangeInDbAlways(boolean set) {
        settings.set(set, KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS);
        settings.set(!set, KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP);
        writeToFile();
        notifyListeners(Type.EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS);
    }

    /**
     * Returns wheter actions shall be executed always after changing images in
     * the database, e.g. a user defined action which embeds XMP into the image
     * files.
     *
     * @return true when the actions shall be executed always.
     *         Default: <code>false</code>
     * @see    #isExecuteActionsAfterImageChangeInDbIfImageHasXmp()
     */
    public boolean isExecuteActionsAfterImageChangeInDbAlways() {
        return properties.containsKey(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
               ? settings.getBoolean(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS)
               : false;
    }

    public void setExecuteActionsAfterImageChangeInDbIfImageHasXmp(boolean set) {
        settings.set(!set, KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_ALWAYS);
        settings.set(set, KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP);
        writeToFile();
        notifyListeners(Type.EXECUTE_ACTION_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP);
    }

    /**
     * Returns wheter actions shall be executed after changing images in the
     * database when the image has embbeded XMP metadata.
     *
     * @return true when the actions shall be executed if the modified image
     *         has embedded XMP metadata. Default: <code>false</code>
     * @see    #isExecuteActionsAfterImageChangeInDbAlways()
     */
    public boolean isExecuteActionsAfterImageChangeInDbIfImageHasXmp() {
        return properties.containsKey(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
               ? settings.getBoolean(KEY_EXECUTE_ACTIONS_AFTER_IMAGE_CHANGE_IN_DB_IF_IMAGE_HAS_XMP)
               : false;
    }

    public void setIptcCharset(String charset) {
        settings.set(charset, KEY_IPTC_CHARSET);
        writeToFile();
        notifyListeners(Type.IPTC_CHARSET);
    }

    /**
     * Returns the charset for decoding IPTC metadata strings.
     *
     * @return charset. Default: <code>"ISO-8859-1"</code>
     */
    public String getIptcCharset() {
        String charset = settings.getString(KEY_IPTC_CHARSET);
        return charset.isEmpty()
               ? "ISO-8859-1"
               : charset;
    }

    public void setAutoscanIncludeSubdirectories(boolean include) {
        settings.set(include, KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES);
        writeToFile();
        notifyListeners(Type.AUTO_SCAN_INCLUDE_DIRECTORIES);
    }

    /**
     * Returns whether automatated scans of directories for updating the
     * database shall include subdirectories.
     *
     * @return true if include subdirectories. Default: <code>true</code>
     */
    public boolean isAutoscanIncludeSubdirectories() {
        return properties.containsKey(KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
               ? settings.getBoolean(KEY_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
               : true;
    }

    /**
     * Returns whether to save input early, e.g. when an input control has been
     * leaved.
     *
     * @return true if input shall be saved early
     */
    public boolean isSaveInputEarly() {
        return properties.containsKey(KEY_SAVE_INPUT_EARLY)
                ? settings.getBoolean(KEY_SAVE_INPUT_EARLY)
                : true;
    }

    /**
     * Sets whether to save input early, e.g. when an input control has been
     * leaved.
     *
     * @param early true if input shall be saved early. Default: true.
     */
    public void setSaveInputEarly(boolean early) {
        settings.set(early, KEY_SAVE_INPUT_EARLY);
        writeToFile();
        notifyListeners(Type.SAVE_INPUT_EARLY);
    }

    public void setLogfileFormatterClass(Class<?> logfileFormatterClass) {
        setToPropertiesLogfileFormatterClass(logfileFormatterClass);
        writeToFile();
        notifyListeners(Type.LOGFILE_FORMATTER_CLASS);
    }

    private void setToPropertiesLogfileFormatterClass(Class<?> formatterClass) {
        String classString = formatterClass.toString();
        int    index       = classString.lastIndexOf(" ");
        settings.set(index >= 0 && index + 1 < classString.length()
                           ? classString.substring(index + 1)
                           : XMLFormatter.class.getName(), KEY_LOGFILE_FORMATTER_CLASS);
    }

    /**
     * Returns the class object of the logfile formatter.
     *
     * @return Class object of the logfile formatter.
     *         Default: {@link XMLFormatter}
     */
    public Class<?> getLogfileFormatterClass() {
        String className = settings.getString(KEY_LOGFILE_FORMATTER_CLASS);
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            AppLogger.logSevere(UserSettings.class, ex);
            settings.set(XMLFormatter.class.getName(), KEY_LOGFILE_FORMATTER_CLASS);
        }
        return XMLFormatter.class;
    }

    public void setMinutesToStartScheduledTasks(int minutes) {
        settings.set(Integer.toString(minutes), KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        writeToFile();
        notifyListeners(Type.MINUTES_TO_START_SCHEDULED_TASKS);
    }

    /**
     * Returns the miniutes to wait after starting before the application starts
     * the automated tasks.
     *
     * @return minutes. Default: Internal constant
     *         <code>DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS</code>
     */
    public int getMinutesToStartScheduledTasks() {
        int minutes = settings.getInt(KEY_MINUTES_TO_START_SCHEDULED_TASKS);
        return minutes > 0
               ? minutes
               : DEFAULT_MINUTES_TO_START_SCHEDULED_TASKS;
    }

    public void setMaxThumbnailWidth(int width) {
        settings.set(Integer.toString(width), KEY_MAX_THUMBNAIL_WIDTH);
        writeToFile();
        notifyListeners(Type.MAX_THUMBNAIL_WIDTH);
    }

    public void setMaxSecondsToTerminateExternalPrograms(Integer seconds) {
        settings.set(seconds, KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS);
        writeToFile();
        notifyListeners(Type.MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS);
    }

    /**
     * Returns the maximum time to wait before terminating external programs.
     *
     * @return time in seconds. Default: <code>60</code>.
     */
    public int getMaxSecondsToTerminateExternalPrograms() {
        return properties.containsKey(KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
               ? settings.getInt(KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
               : 60;
    }

    public void setAcceptHiddenDirectories(boolean accept) {
        settings.set(accept, KEY_ACCEPT_HIDDEN_DIRECTORIES);
        writeToFile();
        notifyListeners(Type.ACCEPT_HIDDEN_DIRECTORIES);
    }

    /**
     * Returns whether directory choosers and -trees shall show hidden
     * directories and if directory scans shall include them.
     *
     * @return true, if accepted. Default: <code>false</code>
     */
    public boolean isAcceptHiddenDirectories() {
        return properties.containsKey(KEY_ACCEPT_HIDDEN_DIRECTORIES)
               ? settings.getBoolean(KEY_ACCEPT_HIDDEN_DIRECTORIES)
               : false;
    }

    /**
     * Sets wheter to check and auto download newer program versions.
     *
     * @param auto true if to check and auto download.
     *             Default: true.
     */
    public void setAutoDownloadNewerVersions(boolean auto) {
        settings.set(auto, KEY_AUTO_DOWNLOAD_NEWER_VERSIONS);
        writeToFile();
        notifyListeners(Type.AUTO_DOWNLOAD_NEWER_VERSIONS);
    }

    /**
     * Returns wheter to check and auto download newer program versions.
     * 
     * @return true, if to check and auto download
     */
    public boolean isAutoDownloadNewerVersions() {
        return properties.containsKey(KEY_AUTO_DOWNLOAD_NEWER_VERSIONS)
                ? settings.getBoolean(KEY_AUTO_DOWNLOAD_NEWER_VERSIONS)
                : true;
    }

    public void setDisplayIptc(boolean display) {
        settings.set(display, KEY_DISPLAY_IPTC);
        writeToFile();
        notifyListeners(Type.DISPLAY_IPTC);
    }

    /**
     * Sets whether to display IPTC.
     *
     * @return true if display IPTC. Default: false.
     */
    public boolean isDisplayIptc() {
        return properties.containsKey(KEY_DISPLAY_IPTC)
                ? settings.getBoolean(KEY_DISPLAY_IPTC)
                : false;
    }

    public void addUserSettingsListener(UserSettingsListener listener) {
        listenerSupport.add(listener);
    }

    public void removeUserSettingsListener(UserSettingsListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(UserSettingsEvent.Type type) {
        listenerSupport.notifyUserListeners(new UserSettingsEvent(type, this));
    }
}
