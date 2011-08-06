package org.jphototagger.program.model;

import java.io.File;
import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.event.AutoscanDirectoryDeletedEvent;
import org.jphototagger.domain.repository.event.AutoscanDirectoryInsertedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseAutoscanDirectories;

/**
 * Elements are directory {@link File}s retrieved through
 * {@link DatabaseAutoscanDirectories#getAll()}.
 *
 * These directorys shall be scanned automatically for updates.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ListModelAutoscanDirectories extends DefaultListModel {

    private static final long serialVersionUID = 5568827666022563702L;

    public ListModelAutoscanDirectories() {
        addElements();
        AnnotationProcessor.process(this);
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

    @EventSubscriber(eventClass = AutoscanDirectoryInsertedEvent.class)
    public void directoryInserted(final AutoscanDirectoryInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addDirectory(evt.getDirectory());
            }
        });
    }

    @EventSubscriber(eventClass = AutoscanDirectoryDeletedEvent.class)
    public void directoryDeleted(final AutoscanDirectoryDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                removeDirectory(evt.getDirectory());
            }
        });
    }
}
