package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.event.fileexcludepattern.FileExcludePatternDeletedEvent;
import org.jphototagger.domain.repository.event.fileexcludepattern.FileExcludePatternInsertedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseFileExcludePatterns;

/**
 * Element are {@link String}s retrieved through
 * {@link DatabaseFileExcludePatterns#getAll()}.
 *
 * Filenames matching these patterns (strings) shall not be handled by
 * <strong>JPhotoTagger</strong>.
 *
 * @author Elmar Baumann
 */
public final class ListModelFileExcludePatterns extends DefaultListModel {

    private static final long serialVersionUID = -8337739189362442866L;
    private volatile transient boolean listenToDb = true;
    private List<String> patterns;

    public ListModelFileExcludePatterns() {
        addElements();
        AnnotationProcessor.process(this);
    }

    public List<String> getPatterns() {
        return new ArrayList<String>(patterns);
    }

    public void insert(final String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        listenToDb = false;

        String trimmedPattern = pattern.trim();

        if (DatabaseFileExcludePatterns.INSTANCE.exists(trimmedPattern)) {
            errorMessageExists(trimmedPattern);

            return;
        }

        if (DatabaseFileExcludePatterns.INSTANCE.insert(trimmedPattern)) {
            addElement(trimmedPattern);
            patterns.add(trimmedPattern);
        } else {
            errorMessageInsert(trimmedPattern);
        }

        listenToDb = true;
    }

    public void delete(final String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }

        listenToDb = false;

        String trimmedPattern = pattern.trim();

        if (DatabaseFileExcludePatterns.INSTANCE.delete(trimmedPattern)) {
            removeElement(trimmedPattern);
            patterns.remove(trimmedPattern);
        } else {
            errorMessageDelete(trimmedPattern);
        }

        listenToDb = true;
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        patterns = DatabaseFileExcludePatterns.INSTANCE.getAll();

        for (String pattern : patterns) {
            addElement(pattern);
        }
    }

    private void insertPattern(String pattern) {
        addElement(pattern);
        patterns.add(pattern);
    }

    private void deletePattern(String pattern) {
        removeElement(pattern);
        patterns.remove(pattern);
    }

    private void errorMessageDelete(String trimmedPattern) {
        String message = Bundle.getString(ListModelFileExcludePatterns.class, "ListModelFileExcludePatterns.Error.Delete", trimmedPattern);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageInsert(String trimmedPattern) {
        String message = Bundle.getString(ListModelFileExcludePatterns.class, "ListModelFileExcludePatterns.Error.InsertPattern.Add", trimmedPattern);
        MessageDisplayer.error(null, message);
    }

    private void errorMessageExists(String trimmedPattern) {
        String message = Bundle.getString(ListModelFileExcludePatterns.class, "ListModelFileExcludePatterns.Error.InsertPattern.Exists", trimmedPattern);
        MessageDisplayer.error(null, message);
    }

    @EventSubscriber(eventClass = FileExcludePatternInsertedEvent.class)
    public void patternInserted(final FileExcludePatternInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                if (listenToDb) {
                    insertPattern(evt.getPattern());
                }
            }
        });
    }

    @EventSubscriber(eventClass = FileExcludePatternDeletedEvent.class)
    public void patternDeleted(final FileExcludePatternDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                if (listenToDb) {
                    deletePattern(evt.getPattern());
                }
            }
        });
    }
}
