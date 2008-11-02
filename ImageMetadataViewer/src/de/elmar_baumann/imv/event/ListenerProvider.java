package de.elmar_baumann.imv.event;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides Listener. Listener to GUIs add themselve here and the GUIs fetch
 * them here. To increase speed:
 * 
 * <ul>
 *     <li><strong>Declaring</strong> a field of a <code>ListenerProvider</code></li>
 *     <li>During <strong>construction</strong>
 *         they assign an instance of this class ({@link #getInstance()}) to
 *         the declared field and retrieving their specific listeners whith the
 *         <code>get...()</code> methods
 *     </li>
 *     <li>GUIs are providing <em>no</em> method to add listeners, interested
 *         classes using this class to listen to them
 *     </li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/18
 */
public class ListenerProvider {

    private List<SearchListener> searchListeners = new LinkedList<SearchListener>();
    private List<RenameFileListener> renameFileListeners = new LinkedList<RenameFileListener>();
    private List<UserSettingsChangeListener> userSettingsChangeListeners = new LinkedList<UserSettingsChangeListener>();
    private List<MetadataEditPanelListener> metadataEditPanelListeners = new LinkedList<MetadataEditPanelListener>();
    private List<FileSystemActionListener> fileSystemActionListeners = new LinkedList<FileSystemActionListener>();
    private static ListenerProvider instance = new ListenerProvider();

    synchronized public void addFileSystemActionListener(FileSystemActionListener listener) {
        fileSystemActionListeners.add(listener);
    }

    synchronized public List<FileSystemActionListener> getFileSystemActionListener() {
        return fileSystemActionListeners;
    }

    synchronized public void addMetadataEditPanelListener(MetadataEditPanelListener listener) {
        metadataEditPanelListeners.add(listener);
    }

    synchronized public List<MetadataEditPanelListener> getMetadataEditPanelListeners() {
        return metadataEditPanelListeners;
    }

    synchronized public void addUserSettingsChangeListener(UserSettingsChangeListener listener) {
        userSettingsChangeListeners.add(listener);
    }

    synchronized public List<UserSettingsChangeListener> getUserSettingsChangeListeners() {
        return userSettingsChangeListeners;
    }

    synchronized public void addRenameFileListener(RenameFileListener listener) {
        renameFileListeners.add(listener);
    }

    synchronized public List<RenameFileListener> getRenameFileListeners() {
        return renameFileListeners;
    }

    synchronized public void addSearchListener(SearchListener listener) {
        searchListeners.add(listener);
    }

    synchronized public List<SearchListener> getSearchListeners() {
        return searchListeners;
    }

    synchronized public void notifyUserSettingsChangeListener(UserSettingsChangeEvent evt) {
        for (UserSettingsChangeListener l : userSettingsChangeListeners) {
            l.applySettings(evt);
        }
    }

    public static ListenerProvider getInstance() {
        return instance;
    }

    private ListenerProvider() {
    }
}
