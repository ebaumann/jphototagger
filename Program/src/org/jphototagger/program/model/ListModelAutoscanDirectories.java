package org.jphototagger.program.model;

import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseAutoscanDirectories;
import org.jphototagger.program.event.listener.DatabaseAutoscanDirectoriesListener;


import java.io.File;

import java.util.List;

import javax.swing.DefaultListModel;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Elements are directory {@link File}s retrieved through
 * {@link DatabaseAutoscanDirectories#getAll()}.
 *
 * These directorys shall be scanned automatically for updates.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ListModelAutoscanDirectories extends DefaultListModel
        implements DatabaseAutoscanDirectoriesListener {
    private static final long serialVersionUID = 5568827666022563702L;

    public ListModelAutoscanDirectories() {
        addElements();
        DatabaseAutoscanDirectories.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<File> directories = DatabaseAutoscanDirectories.INSTANCE.getAll();

        for (File directory : directories) {
            if (directory.isDirectory() && directory.exists()) {
                addElement(directory);
            }
        }
    }

    private void removeDirectory(File directory) {
        if (contains(directory)) {
            removeElement(directory);
        }
    }

    private void addDirectory(File directory) {
        if (!contains(directory)) {
            addElement(directory);
        }
    }

    @Override
    public void directoryInserted(final File directory) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                addDirectory(directory);
            }
        });
    }

    @Override
    public void directoryDeleted(final File directory) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                removeDirectory(directory);
            }
        });
    }
}
