package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.event.UserSettingsChangeEvent;
import de.elmar_baumann.imv.event.listener.UserSettingsChangeListener;
import de.elmar_baumann.imv.event.listener.FileSystemActionListener;
import de.elmar_baumann.imv.event.listener.MetadataEditPanelListener;
import de.elmar_baumann.imv.event.listener.SearchListener;
import de.elmar_baumann.imv.event.listener.RenameFileListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides Listener. Listener to GUIs add themselve here and the GUIs fetch
 * them here. To increase speed:
 * 
 * <ul>
 *     <li><strong>Declaring</strong> a field of a <code>ListenerProvider</code></li>
 *     <li>During <strong>construction</strong>
 *         they assign an instance of this class ({@link #INSTANCE}) to
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
public final class ListenerProvider {

    private final List<SearchListener> searchListeners = new LinkedList<SearchListener>();
    private final List<RenameFileListener> renameFileListeners = new LinkedList<RenameFileListener>();
    private final List<UserSettingsChangeListener> userSettingsChangeListeners = new LinkedList<UserSettingsChangeListener>();
    private final List<MetadataEditPanelListener> metadataEditPanelListeners = new LinkedList<MetadataEditPanelListener>();
    private final List<FileSystemActionListener> fileSystemActionListeners = new LinkedList<FileSystemActionListener>();
    public static final ListenerProvider INSTANCE = new ListenerProvider();

    public synchronized void addFileSystemActionListener(FileSystemActionListener listener) {
        fileSystemActionListeners.add(listener);
    }

    public synchronized List<FileSystemActionListener> getFileSystemActionListener() {
        return fileSystemActionListeners;
    }

    public synchronized void addMetadataEditPanelListener(MetadataEditPanelListener listener) {
        metadataEditPanelListeners.add(listener);
    }

    public synchronized List<MetadataEditPanelListener> getMetadataEditPanelListeners() {
        return metadataEditPanelListeners;
    }

    public synchronized void addUserSettingsChangeListener(UserSettingsChangeListener listener) {
        userSettingsChangeListeners.add(listener);
    }

    public synchronized List<UserSettingsChangeListener> getUserSettingsChangeListeners() {
        return userSettingsChangeListeners;
    }

    public synchronized void addRenameFileListener(RenameFileListener listener) {
        renameFileListeners.add(listener);
    }

    public synchronized List<RenameFileListener> getRenameFileListeners() {
        return renameFileListeners;
    }

    public synchronized void addSearchListener(SearchListener listener) {
        searchListeners.add(listener);
    }

    public synchronized List<SearchListener> getSearchListeners() {
        return searchListeners;
    }

    public synchronized void notifyUserSettingsChangeListener(UserSettingsChangeEvent evt) {
        for (UserSettingsChangeListener l : userSettingsChangeListeners) {
            l.applySettings(evt);
        }
    }

    private ListenerProvider() {
    }
}
