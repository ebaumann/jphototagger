package org.jphototagger.lib.io;

import java.awt.Component;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Properties for using a {@link FileChooserExt}.
 *
 * @author Elmar Baumann
 */
/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileChooserProperties {
    private String propertyKeyPrefix;
    private Properties properties;
    private  Component parent;
    private String currentDirectoryPath;
    private String dialogTitle;
    private javax.swing.filechooser.FileFilter fileFilter;
    private boolean multiSelectionEnabled;
    private boolean open;
    private boolean confirmOverwrite;
    private String saveFilenameExtension;
    private int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;

    /**
     * Sets the prefix of the key for storing size and current directory
     * path in {@link #getProperties()}.
     *
     * @param  propertyKeyPrefix prefix or null
     * @return                   this instance (for cascade settings)
     */
    public FileChooserProperties propertyKeyPrefix(String propertyKeyPrefix) {
        this.propertyKeyPrefix = propertyKeyPrefix;
        return this;
    }

    /**
     * Sets the properties for applying and storing size and current
     * directory path.
     *
     * @param  properties properties or null
     * @return            this instance (for cascade settings)
     */
    public FileChooserProperties properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Sets the parent component of the file chooser dialog.
     *
     * @param  parent parent component or null
     * @return        this instance (for cascade settings)
     */
    public FileChooserProperties parent(Component parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Sets the path of the directory to display in the file chooser.
     *
     * @param  currentDirectoryPath directory path or null
     * @return                      this instance (for cascade settings)
     */
    public FileChooserProperties currentDirectoryPath(String currentDirectoryPath) {
        this.currentDirectoryPath = currentDirectoryPath;
        return this;
    }

    /**
     * Sets the dialog title of the file chooser.
     *
     * @param  dialogTitle dialog title or null
     * @return             this instance (for cascade settings)
     */
    public FileChooserProperties dialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    /**
     * Sets the file filterFiles for the file chooser.
     *
     * @param  fileFilter file filterFiles or null
     * @return            this instance (for cascade settings)
     */
    public FileChooserProperties fileFilter(javax.swing.filechooser.FileFilter fileFilter) {
        this.fileFilter = fileFilter;
        return this;
    }

    /**
     * Sets whether the user can select multiple files.
     *
     * @param  multiSelectionEnabled true if the user can select multiple
     *                               files.
     *                               Default: false.
     * @return                       this instance (for cascade settings)
     */
    public FileChooserProperties multiSelectionEnabled(boolean multiSelectionEnabled) {
        this.multiSelectionEnabled = multiSelectionEnabled;
        return this;
    }

    /**
     * Sets whether an open or save dialog shall be displayed.
     *
     * @param  open true if an open dialog shall be displayed and false, if
     *              a save dialog shall be displayed.
     *              Default: false (save mode).
     * @return      this instance (for cascade settings)
     */
    public FileChooserProperties open(boolean open) {
        this.open = open;
        return this;
    }

    /**
     * Sets whether a user has to confirm overwriting existing files in save
     * mode.
     *
     * @param  confirmOverwrite true if the user has to confirm overwriting
     *                          existing files in save mode
     * @return                  this instance (for cascade settings)
     * @see    FileChooserExt#saveFilenameExtension(java.lang.String)
     */
    public FileChooserProperties confirmOverwrite(boolean confirmOverwrite) {
        this.confirmOverwrite = confirmOverwrite;
        return this;
    }

    /**
     * Sets in save mode a file extension which sall be added to files if
     * the user didn't input that extension.
     *
     * @param   saveFilenameExtension extension, e.g. <code>".txt"</code>
     * @return                        this instance (for cascade settings)
     * @see     FileChooserExt#saveFilenameExtension(java.lang.String)
     */
    public FileChooserProperties saveFilenameExtension(String saveFilenameExtension) {
        this.saveFilenameExtension = saveFilenameExtension;
        return this;
    }

    /**
     *
     * @param fileSelectionMode Default: {@link JFileChooser#FILES_AND_DIRECTORIES}
     */
    public void fileSelectionMode(int fileSelectionMode) {
        this.fileSelectionMode = fileSelectionMode;
    }

    public boolean isConfirmOverwrite() {
        return confirmOverwrite;
    }

    public String getCurrentDirectoryPath() {
        return currentDirectoryPath;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public int getFileSelectionMode() {
        return fileSelectionMode;
    }

    public boolean isMultiSelectionEnabled() {
        return multiSelectionEnabled;
    }

    public boolean isOpen() {
        return open;
    }

    public String getSaveFilenameExtension() {
        return saveFilenameExtension;
    }

    public Component getParent() {
        return parent;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getPropertyKeyPrefix() {
        return propertyKeyPrefix;
    }
}
